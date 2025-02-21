/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import {
	AbstractLegacyComponent,
	applyCss,
	executeWhenFirstDisplayed,
	parseHtml,
	ProjectorEvent,
	ServerObjectChannel
} from "projector-client-object-api";
import {
	DtoImageCropper,
	DtoImageCropper_SelectionChangedEvent,
	DtoImageCropperCommandHandler,
	DtoImageCropperEventSource,
	ImageCropperSelection,
	ImageCropperSelectionMode
} from "./generated";
import {Constants, draggable} from "projector-client-core-components";

type Selection = Omit<ImageCropperSelection, '_type'>;

type Rect = { x: number, y: number, width: number, height: number }

export class ImageCropper extends AbstractLegacyComponent<DtoImageCropper> implements DtoImageCropperCommandHandler, DtoImageCropperEventSource {

	public readonly onSelectionChanged: ProjectorEvent<DtoImageCropper_SelectionChangedEvent> = new ProjectorEvent<DtoImageCropper_SelectionChangedEvent>();

	private $element: HTMLElement;
	private $selectionFrame: HTMLElement;
	private htmlImageElement: HTMLImageElement;

	private selection: Selection;
	private imageNaturalWidth: number = null;
	private imageNaturalHeight: number = null;

	constructor(config: DtoImageCropper, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$element = parseHtml(`<div data-id="' + config.id + '" class="ImageCropper">
    <img draggable="false"></img>
    <div class="cropping-frame" tabindex="-1">
    	<div class="ui-resizable-handle ui-resizable-ne" data-direction="ne" data-fixed-at="sw"></div>
    	<div class="ui-resizable-handle ui-resizable-se" data-direction="se" data-fixed-at="nw"></div>
    	<div class="ui-resizable-handle ui-resizable-nw" data-direction="nw" data-fixed-at="se"></div>
    	<div class="ui-resizable-handle ui-resizable-sw" data-direction="sw" data-fixed-at="ne"></div>    
	</div>
</div>`);
		this.htmlImageElement = this.$element.querySelector<HTMLElement>(":scope img") as HTMLImageElement;
		this.htmlImageElement.onload = () => {
			this.imageNaturalWidth = this.htmlImageElement.naturalWidth;
			this.imageNaturalHeight = this.htmlImageElement.naturalHeight;
			applyDisplayMode(this.getMainElement(), this.htmlImageElement, "fit-size");
			this.resetSelectionFrame(config.aspectRatio);
			this.updateCroppingFramePosition(this.selection);
		};
		// this.$element.style.backgroundImage = `url(${config.imageUrl}`);
		this.$selectionFrame = this.$element.querySelector<HTMLElement>(":scope .cropping-frame");

		let startBox: any;
		draggable(this.$selectionFrame, {
			validDragStartDecider: e => {
				const specialButton = e.button != null && e.button !== 0;
				return e.target == this.$selectionFrame && !specialButton;
			},
			dragStart: e => {
				startBox = {
					x: parseFloat(this.$selectionFrame.style.left),
					y: parseFloat(this.$selectionFrame.style.top),
					width: parseFloat(this.$selectionFrame.style.width),
					height: parseFloat(this.$selectionFrame.style.height),
					x2() {
						return this.x + this.width;
					},
					y2() {
						return this.y + this.height;
					}
				};
			},
			drag: (e, eventData) => {
				this.$selectionFrame.style.left = (startBox.x + eventData.deltaX) + "px";
				this.$selectionFrame.style.top = (startBox.y + eventData.deltaY) + "px";
			},
			dragEnd: (e, eventData) => {
				this.handleDragEnd();
			}
		});

		let direction: Direction;
		let fixedAt: Direction;
		draggable(this.$selectionFrame.querySelectorAll(":scope .ui-resizable-handle"), {
			dragStart: e => {
				direction = (e.target as HTMLElement).getAttribute("data-direction") as Direction;
				fixedAt = (e.target as HTMLElement).getAttribute("data-fixed-at") as Direction;
				startBox = {
					x: parseFloat(this.$selectionFrame.style.left),
					y: parseFloat(this.$selectionFrame.style.top),
					width: parseFloat(this.$selectionFrame.style.width),
					height: parseFloat(this.$selectionFrame.style.height),
					x2() {
						return this.x + this.width;
					},
					y2() {
						return this.y + this.height;
					}
				}
			},
			drag: (e, eventData) => {
				if (direction.indexOf('n') != -1) {
					this.$selectionFrame.style.top = (startBox.y + eventData.deltaY) + "px";
					this.$selectionFrame.style.height = (startBox.height - eventData.deltaY) + "px";
				}
				if (direction.indexOf('w') != -1) {
					this.$selectionFrame.style.left = (startBox.x + eventData.deltaX) + "px";
					this.$selectionFrame.style.width = (startBox.width - eventData.deltaX) + "px";
				}
				if (direction.indexOf('s') != -1) {
					this.$selectionFrame.style.height = (startBox.height + eventData.deltaY) + "px";
				}
				if (direction.indexOf('e') != -1) {
					this.$selectionFrame.style.width = (startBox.width + eventData.deltaX) + "px";
				}
				let selection = this.frameRectToSelection(this.getSelectionFrameOffsetRect());
				selection = this.boundSelection(selection, fixedAt);
				this.updateCroppingFramePosition(selection);
			},
			dragEnd: (e, eventData) => {
				let selection = this.frameRectToSelection(this.getSelectionFrameOffsetRect());
				selection = this.boundSelection(selection, fixedAt);
				this.updateCroppingFramePosition(selection);
				this.handleDragEnd();
			}
		});

		this.setImageUrl(config.imageUrl);
		this.setSelectionMode(config.selectionMode);
		this.setAspectRatio(config.aspectRatio);
	}

	private resetSelectionFrame(aspectRatio: number) {
		if (this.imageNaturalWidth != null) {
			this.selection = {
				left: 0, top: 0, width: 0, height: 0
			};
			let naturalImageAspectRatio = this.imageNaturalWidth / this.imageNaturalHeight;
			if (aspectRatio === 0) {
				this.selection.width = 0.8 * this.imageNaturalWidth;
				this.selection.height = 0.8 * this.imageNaturalHeight;
			} else if (aspectRatio / naturalImageAspectRatio > 1) {
				this.selection.width = 0.8 * this.imageNaturalWidth;
				this.selection.height = this.selection.width / aspectRatio;
			} else {
				this.selection.height = 0.8 * this.imageNaturalHeight;
				this.selection.width = this.selection.height * aspectRatio;
			}

			this.selection.left = 0.5 * (this.imageNaturalWidth - this.selection.width);
			this.selection.top = 0.5 * (this.imageNaturalHeight - this.selection.height);

			this.selection = this.boundSelection(this.selection);
			this.updateCroppingFramePosition(this.selection);
			this.onSelectionChanged.fire({
				selection: {
					left: this.selection.left, top: this.selection.top, width: this.selection.width, height: this.selection.height
				}
			});
		}
	}

	private handleDragEnd() {
		let selectionFrameOffsetRect = this.getSelectionFrameOffsetRect();
		let selection = this.frameRectToSelection(selectionFrameOffsetRect);
		selection = this.boundSelection(selection);
		this.updateCroppingFramePosition(selection);
		this.selection = selection;
		console.debug("selection: ", this.selection);
		this.onSelectionChanged.fire({
			selection: {
				left: this.selection.left, top: this.selection.top, width: this.selection.width, height: this.selection.height
			}
		});
	}

	private getSelectionFrameOffsetRect(): Rect {
		return {
			x: this.$selectionFrame.offsetLeft,
			y: this.$selectionFrame.offsetTop,
			width: this.$selectionFrame.offsetWidth,
			height: this.$selectionFrame.offsetHeight,
		};
	}

	private boundSelection(selection: Selection, fixedAt?: Direction): Selection {
		return boundSelection(selection, {
			width: this.imageNaturalWidth,
			height: this.imageNaturalHeight
		}, this.config.aspectRatio, fixedAt);
	}

	private calculateCoordinateCorrectionFactor() {
		let fx = this.imageNaturalWidth / this.getWidth();
		let fy = this.imageNaturalHeight / this.getHeight();
		let factor = Math.max(fx, fy);
		return factor;
	}

	public setImageUrl(url: string) {
		this.htmlImageElement.src = url;
	}

	setAspectRatio(aspectRatio: number): void {
		this.config.aspectRatio = aspectRatio;
		// $(this.$selectionFrame).resizable("option", "aspectRatio", aspectRatio);      TODO
		this.resetSelectionFrame(aspectRatio);
	}

	setSelection(selection: ImageCropperSelection): void {
		this.selection = selection;
		this.updateCroppingFramePosition(this.selection);
	}

	setSelectionMode(selectionMode: ImageCropperSelectionMode): void {
		this.$selectionFrame.className = this.$selectionFrame.className.replace(/mode-\w+/, '');
		this.$selectionFrame.classList.add(`mode-${ImageCropperSelectionMode[selectionMode].toLowerCase()}`)
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		applyDisplayMode(this.getMainElement(), this.htmlImageElement, "fit-size", {
			innerPreferredDimensions: {
				width: this.imageNaturalWidth,
				height: this.imageNaturalHeight
			}
		});
		this.updateCroppingFramePosition(this.selection);
	}

	@executeWhenFirstDisplayed(true)
	private updateCroppingFramePosition(selection: Selection) {
		if (selection != null) {
			let frameRect = this.selectionToFrameRect(selection);
			applyCss(this.$selectionFrame, {
				left: frameRect.x + "px",
				top: frameRect.y + "px",
				width: frameRect.width + "px",
				height: frameRect.height + "px",
			});
		}
	}


	private frameRectToSelection(frameSelectionPos: Rect): Selection {
		let correctionFactor = this.calculateCoordinateCorrectionFactor();
		// let correctionOffsetX =
		return {
			left: (frameSelectionPos.x - this.htmlImageElement.offsetLeft) * correctionFactor,
			top: (frameSelectionPos.y - this.htmlImageElement.offsetTop) * correctionFactor,
			width: frameSelectionPos.width * correctionFactor,
			height: frameSelectionPos.height * correctionFactor
		};
	}

	private selectionToFrameRect(selection: Selection): Rect {
		let correctionFactor = this.calculateCoordinateCorrectionFactor();
		return {
			x: (selection.left / correctionFactor) + this.htmlImageElement.offsetLeft,
			y: (selection.top / correctionFactor) + this.htmlImageElement.offsetTop,
			width: selection.width / correctionFactor,
			height: selection.height / correctionFactor
		};
	}

	public doGetMainElement(): HTMLElement {
		return this.$element;
	}

}

function boundSelection(
	selection: Selection,
	bounds: { width: number, height: number },
	aspectRatio?: number,
	fixedAt?: Direction
): Selection {
	let newSelection = {...selection};

	if (fixedAt == null) {
		if (newSelection.width > bounds.width) {
			newSelection.width = bounds.width;
		}
		if (newSelection.height > bounds.height) {
			newSelection.height = bounds.height;
		}
		if (aspectRatio != null && aspectRatio > 0) {
			if (newSelection.width / newSelection.height > aspectRatio) {
				newSelection.width = newSelection.height * aspectRatio;
			} else {
				newSelection.height = newSelection.width / aspectRatio;
			}
		}
		if (newSelection.left < 0) {
			newSelection.left = 0;
		}
		if (newSelection.left + newSelection.width > bounds.width) {
			newSelection.left = bounds.width - newSelection.width;
		}
		if (newSelection.top < 0) {
			newSelection.top = 0;
		}
		if (newSelection.top + newSelection.height > bounds.height) {
			newSelection.top = bounds.height - newSelection.height;
		}
	} else {
		let selectionXCenter = selection.left + selection.width / 2;
		let selectionYCenter = selection.top + selection.height / 2;
		let maxWidth =
			fixedAt == "n" || fixedAt == "s" ? Math.min(Math.min(selectionXCenter, bounds.width - selectionXCenter) * 2, bounds.width) :
				fixedAt == "e" || fixedAt == "ne" || fixedAt == "se" ? Math.min(selection.left + selection.width, bounds.width) :
					fixedAt == "w" || fixedAt == "nw" || fixedAt == "sw" ? Math.min(bounds.width - selection.left, bounds.width)
						: bounds.width;
		let maxHeight =
			fixedAt == "e" || fixedAt == "w" ? Math.min(Math.min(selectionYCenter, bounds.height - selectionYCenter) * 2, bounds.height) :
				fixedAt == "s" || fixedAt == "se" || fixedAt == "sw" ? Math.min(selection.top + selection.height, bounds.height) :
					fixedAt == "n" || fixedAt == "ne" || fixedAt == "nw" ? Math.min(bounds.height - selection.top, bounds.height)
						: bounds.height;

		newSelection.width = Math.min(newSelection.width, maxWidth);
		newSelection.height = Math.min(newSelection.height, maxHeight);

		if (aspectRatio != null && aspectRatio > 0) {
			if (aspectRatio > newSelection.width / newSelection.height) {
				newSelection.height = newSelection.width / aspectRatio;
			} else if (aspectRatio < newSelection.width / newSelection.height) {
				newSelection.width = newSelection.height * aspectRatio;
			}
		}

		newSelection.left =
			fixedAt == "n" || fixedAt == "s" ? selectionXCenter - newSelection.width / 2 :
				fixedAt == "e" || fixedAt == "ne" || fixedAt == "se" ? selection.left + selection.width - newSelection.width :
					fixedAt == "w" || fixedAt == "nw" || fixedAt == "sw" ? selection.left
						: 0;
		newSelection.top =
			fixedAt == "e" || fixedAt == "w" ? selectionYCenter - newSelection.height / 2 :
				fixedAt == "s" || fixedAt == "se" || fixedAt == "sw" ? selection.top + selection.height - newSelection.height :
					fixedAt == "n" || fixedAt == "ne" || fixedAt == "nw" ? selection.top
						: 0;
	}
	return newSelection;
}

function applyDisplayMode($outer: HTMLElement, $inner: HTMLElement, displayMode: "fit-width" | "fit-height" | "fit-size" | "cover" | "original-size", options?: {
	innerPreferredDimensions?: { // only needed for ORIGINAL_SIZE!
		width: number,
		height: number,
	},
	zoomFactor?: number,
	padding?: number,
	considerScrollbars?: boolean
}) {
	options = {...options}; // copy the options as we are potentially making changes to the object...
	if (options.innerPreferredDimensions == null || !options.innerPreferredDimensions.width || !options.innerPreferredDimensions.height) {
		if ($inner instanceof HTMLImageElement && $inner.naturalHeight > 0) {
			let imgElement = $inner;
			options.innerPreferredDimensions = {
				width: imgElement.naturalWidth,
				height: imgElement.naturalHeight
			}
		} else {
			$inner.style.width = "100%";
			$inner.style.height = "100%";
			return;
		}
	}
	if (options.padding == null) {
		options.padding = parseInt($outer.style.paddingLeft) || 0;
	}
	let availableWidth = $outer.offsetWidth - 2 * options.padding;
	let availableHeight = $outer.offsetHeight - 2 * options.padding;

	let size = calculateDisplayModeInnerSize({
		width: availableWidth,
		height: availableHeight
	}, options.innerPreferredDimensions, displayMode, options.zoomFactor, options.considerScrollbars);
	$inner.style.width = size.width + "px";
	$inner.style.height = size.height + "px";
}

type Direction = "n" | "e" | "s" | "w" | "ne" | "se" | "nw" | "sw";

function calculateDisplayModeInnerSize(containerDimensions: { width: number, height: number },
											  innerPreferredDimensions: { width: number, height: number },
											  displayMode: "fit-width" | "fit-height" | "fit-size" | "cover" | "original-size",
											  zoomFactor: number = 1,
											  considerScrollbars = false
): { width: number, height: number } {
	let viewPortAspectRatio = containerDimensions.width / containerDimensions.height;
	let imageAspectRatio = innerPreferredDimensions.width / innerPreferredDimensions.height;

	console.debug(`outer dimensions: ${containerDimensions.width}x${containerDimensions.height}`);
	console.debug(`inner dimensions: ${innerPreferredDimensions.width}x${innerPreferredDimensions.height}`);
	console.debug(`displayMode: ${displayMode}`);

	if (displayMode === "fit-width") {
		let width = Math.floor(containerDimensions.width * zoomFactor);
		if (considerScrollbars && zoomFactor <= 1 && Math.ceil(width / imageAspectRatio) > containerDimensions.height) {
			// There will be a vertical scroll bar, so make sure the width will not result in a horizontal scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			width = Math.min(width, containerDimensions.width - Constants.SCROLLBAR_WIDTH);
		}
		return {width: width, height: width / imageAspectRatio};
	} else if (displayMode === "fit-height") {
		let height = Math.floor(containerDimensions.height * zoomFactor);
		if (considerScrollbars && zoomFactor <= 1 && height * imageAspectRatio > containerDimensions.width) {
			// There will be a horizontal scroll bar, so make sure the width will not result in a vertical scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			height = Math.min(height, containerDimensions.height - Constants.SCROLLBAR_WIDTH);
		}
		return {width: height * imageAspectRatio, height: height};
	} else if (displayMode === "fit-size") {
		if (imageAspectRatio > viewPortAspectRatio) {
			let width = Math.floor(containerDimensions.width * zoomFactor);
			return {width: width, height: width / imageAspectRatio};
		} else {
			let height = Math.floor(containerDimensions.height * zoomFactor);
			return {width: height * imageAspectRatio, height: height};
		}
	} else if (displayMode === "cover") {
		if (imageAspectRatio < viewPortAspectRatio) {
			let width = Math.floor(containerDimensions.width * zoomFactor);
			return {width: width, height: width / imageAspectRatio};
		} else {
			let height = Math.floor(containerDimensions.height * zoomFactor);
			return {width: height * imageAspectRatio, height: height};
		}
	} else { // ORIGINAL_SIZE
		let width = innerPreferredDimensions.width * zoomFactor;
		return {width: width, height: width / imageAspectRatio};
	}
}



