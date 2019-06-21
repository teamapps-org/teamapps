/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import {UiComponent} from "./UiComponent";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {applyDisplayMode, boundSelection, css, parseHtml} from "./Common";
import {executeWhenAttached} from "./util/ExecuteWhenAttached";
import {UiImageCropper_SelectionChangedEvent, UiImageCropperCommandHandler, UiImageCropperConfig, UiImageCropperEventSource} from "../generated/UiImageCropperConfig";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiImageCropperSelectionConfig, UiImageCropperSelectionConfig} from "../generated/UiImageCropperSelectionConfig";
import {UiImageCropperSelectionMode} from "../generated/UiImageCropperSelectionMode";


export class UiImageCropper extends UiComponent<UiImageCropperConfig> implements UiImageCropperCommandHandler, UiImageCropperEventSource {

	public readonly onSelectionChanged: TeamAppsEvent<UiImageCropper_SelectionChangedEvent> = new TeamAppsEvent<UiImageCropper_SelectionChangedEvent>(this);

	private $element: HTMLElement;
	private $selectionFrame: HTMLElement;
	private htmlImageElement: HTMLImageElement;

	private selection: UiImageCropperSelectionConfig;
	private imageNaturalWidth: number = null;
	private imageNaturalHeight: number = null;

	constructor(config: UiImageCropperConfig,
	            context: TeamAppsUiContext) {
		super(config, context);

		this.$element = parseHtml(`<div data-id="' + config.id + '" class="UiImageCropper">
    <img></img>
    <div class="cropping-frame" tabindex="-1"></div>
</div>`);
		this.htmlImageElement = this.$element.querySelector<HTMLElement>(":scope img") as HTMLImageElement;
		this.htmlImageElement.onload = () => {
			this.imageNaturalWidth = this.htmlImageElement.naturalWidth;
			this.imageNaturalHeight = this.htmlImageElement.naturalHeight;
			applyDisplayMode(this.getMainDomElement(), this.htmlImageElement, UiPageDisplayMode.FIT_SIZE);
			this.resetSelectionFrame(config.aspectRatio);
			this.updateCroppingFramePosition();
		};
		// this.$element.style.backgroundImage = `url(${config.imageUrl}`);
		this.$selectionFrame = this.$element.querySelector<HTMLElement>(":scope .cropping-frame");
		$(this.$selectionFrame)
			.resizable({
				handles: 'n, e, s, w, ne, se, nw, sw',
				aspectRatio: config.aspectRatio,
				containment: $(this.htmlImageElement),
				stop: this.handleDragEnd.bind(this)
			})
			.draggable({
				containment: $(this.htmlImageElement),
				stop: this.handleDragEnd.bind(this)
			});

		this.setImageUrl(config.imageUrl);
		this.setSelectionMode(config.selectionMode);
		this.setAspectRatio(config.aspectRatio);
	}

	private resetSelectionFrame(aspectRatio: number) {
		if (this.imageNaturalWidth != null) {
			this.selection = createUiImageCropperSelectionConfig(0, 0, 0, 0);
			let naturalImageAspectRatio = this.imageNaturalWidth / this.imageNaturalHeight;
			if (aspectRatio / naturalImageAspectRatio > 1) {
				this.selection.width = 0.8 * this.imageNaturalWidth;
				this.selection.height = this.selection.width / aspectRatio;
			} else {
				this.selection.height = 0.8 * this.imageNaturalHeight;
				this.selection.width = this.selection.height * aspectRatio;
			}

			this.selection.left = 0.5 * (this.imageNaturalWidth - this.selection.width);
			this.selection.top = 0.5 * (this.imageNaturalHeight - this.selection.height);
			
			this.selection = this.boundSelection();
			this.updateCroppingFramePosition();
			this.onSelectionChanged.fire(EventFactory.createUiImageCropper_SelectionChangedEvent(this.getId(), this.selection));
		}
	}

	private handleDragEnd() {
		let correctionFactor = this.calculateCoordinateCorrectionFactor();
		this.selection = createUiImageCropperSelectionConfig(
			(this.$selectionFrame.offsetLeft - this.htmlImageElement.offsetLeft) * correctionFactor,
			(this.$selectionFrame.offsetTop - this.htmlImageElement.offsetTop) * correctionFactor,
			this.$selectionFrame.offsetWidth * correctionFactor,
			this.$selectionFrame.offsetHeight * correctionFactor
		);
		this.logger.debug("selection: ", this.selection);
		this.selection = this.boundSelection();
		this.updateCroppingFramePosition();
		this.onSelectionChanged.fire(EventFactory.createUiImageCropper_SelectionChangedEvent(this.getId(), this.selection));
	}

	private boundSelection() {
		return {
			...boundSelection(this.selection, {width: this.imageNaturalWidth, height: this.imageNaturalHeight}, this._config.aspectRatio),
			_type: "UiImageCropperSelection"
		};
	}

	private calculateCoordinateCorrectionFactor() {
		console.log(`${this.imageNaturalWidth} / ${this.getWidth()}, ${this.imageNaturalHeight} / ${this.getHeight()}`);
		return Math.max(this.imageNaturalWidth / this.getWidth(), this.imageNaturalHeight / this.getHeight());
	}

	public setImageUrl(url: string) {
		this.htmlImageElement.src = url;
	}

	setAspectRatio(aspectRatio: number): void {
		this._config.aspectRatio = aspectRatio;
		$(this.$selectionFrame).resizable("option", "aspectRatio", aspectRatio);
		this.resetSelectionFrame(aspectRatio);
	}

	setSelection(selection: UiImageCropperSelectionConfig): void {
		this.selection = selection;
		this.updateCroppingFramePosition();
	}

	setSelectionMode(selectionMode: UiImageCropperSelectionMode): void {
		this.$selectionFrame.className = this.$selectionFrame.className.replace(/mode-\w+/, '');
		this.$selectionFrame.classList.add(`mode-${UiImageCropperSelectionMode[selectionMode].toLowerCase()}`)
	}

	@executeWhenAttached(true)
	public onResize(): void {
		applyDisplayMode(this.getMainDomElement(), this.htmlImageElement, UiPageDisplayMode.FIT_SIZE, {
			innerPreferedDimensions: {
				width: this.imageNaturalWidth,
				height: this.imageNaturalHeight
			}
		});
		this.updateCroppingFramePosition();
	}

	@executeWhenAttached(true)
	private updateCroppingFramePosition() {
		if (this.selection != null) {
			let correctionFactor = this.calculateCoordinateCorrectionFactor();

			let left = this.selection.left / correctionFactor;
			let top = this.selection.top / correctionFactor;
			let width = this.selection.width / correctionFactor;
			let height = this.selection.height / correctionFactor;

			css(this.$selectionFrame, {
				left: left + (this.htmlImageElement.offsetLeft - this.getMainDomElement().offsetLeft) + "px",
				top: top + (this.htmlImageElement.offsetTop - this.getMainDomElement().offsetTop) + "px",
				width: width + "px",
				height: height + "px"
			});
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$element;
	}

	protected onAttachedToDom() {
		this.reLayout();
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiImageCropper", UiImageCropper);
