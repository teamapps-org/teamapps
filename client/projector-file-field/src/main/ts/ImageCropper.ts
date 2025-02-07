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
	AbstractLegacyComponent, applyCss,
	executeWhenFirstDisplayed,
	parseHtml,
	ServerObjectChannel,
	ProjectorEvent
} from "projector-client-object-api";
import {
	createImageCropperSelection,
	DtoImageCropper,
	DtoImageCropper_SelectionChangedEvent,
	DtoImageCropperCommandHandler,
	DtoImageCropperEventSource, ImageCropperSelection, ImageCropperSelectionMode
} from "./generated";
import {applyDisplayMode, Direction, draggable} from "projector-client-core-components";

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

		this.$element = parseHtml(`<div data-id="' + config.id + '" class="UiImageCropper">
    <img></img>
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
			this.selection = createImageCropperSelection(0, 0, 0, 0);
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
				selection: createImageCropperSelection(this.selection.left, this.selection.top, this.selection.width, this.selection.height)
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
			selection: createImageCropperSelection(this.selection.left, this.selection.top, this.selection.width, this.selection.height)
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


	private frameRectToSelection(frameSelectionPos: Rect) {
		let correctionFactor = this.calculateCoordinateCorrectionFactor();
		// let correctionOffsetX =
		return createImageCropperSelection(
			(frameSelectionPos.x - this.htmlImageElement.offsetLeft) * correctionFactor,
			(frameSelectionPos.y - this.htmlImageElement.offsetTop) * correctionFactor,
			frameSelectionPos.width * correctionFactor,
			frameSelectionPos.height * correctionFactor
		);
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

export function boundSelection(
	selection: { left: number, top: number, width: number, height: number },
	bounds: { width: number, height: number },
	aspectRatio?: number,
	fixedAt?: Direction
) {
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



