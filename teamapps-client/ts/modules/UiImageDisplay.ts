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
import {keyCodes} from "trivial-components";
import {UiCachedImageConfig} from "../generated/UiCachedImageConfig";
import {applyDisplayMode, enableScrollViaDragAndDrop, parseHtml} from "./Common";
import {UiImageDisplay_ImageDisplayedEvent, UiImageDisplay_ImagesRequestEvent, UiImageDisplayCommandHandler, UiImageDisplayConfig, UiImageDisplayEventSource} from "../generated/UiImageDisplayConfig";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

interface UiCachedImage {
	id: string;
	imageUrl: string;
	$img: HTMLElement;
	naturalWidth?: number;
	naturalHeight?: number;
}

export class UiImageDisplay extends UiComponent<UiImageDisplayConfig> implements UiImageDisplayCommandHandler, UiImageDisplayEventSource {

	public readonly onImagesRequest: TeamAppsEvent<UiImageDisplay_ImagesRequestEvent> = new TeamAppsEvent<UiImageDisplay_ImagesRequestEvent>(this);
	public readonly onImageDisplayed: TeamAppsEvent<UiImageDisplay_ImageDisplayedEvent> = new TeamAppsEvent<UiImageDisplay_ImageDisplayedEvent>(this);

	private forwardImageSvg = `<svg width="64" height="64" viewBox="0 0 10240 10240" version="1.1">
    <path d="M5892 2747l2147 2147c124,124 124,328 0,452l-2147 2147c-124,124 -276,187 -452,187l-1159 0c-66,0 -122,-38 -148,-99 -25,-61 -12,-127 35,-174l1647 -1647 -3255 0c-176,0 -320,-144 -320,-320l0 -640c0,-176 144,-320 320,-320l3255 0 -1647 -1647c-47,-47 -60,-113 -35,-174 26,-61 82,-99 148,-99l1159 0c176,0 328,63 452,187z"
          style="fill:#ffffff"/>
    <path d="M5120 9600c-2474,0 -4480,-2006 -4480,-4480 0,-2474 2006,-4480 4480,-4480 2474,0 4480,2006 4480,4480 0,2474 -2006,4480 -4480,4480zm-3840 -4480c0,2121 1719,3840 3840,3840 2121,0 3840,-1719 3840,-3840 0,-2121 -1719,-3840 -3840,-3840 -2121,0 -3840,1719 -3840,3840z"
          style="fill:#ffffff"/>`;

	private $componentWrapper: HTMLElement;
	private $imageContainerWrapper: HTMLElement;
	private $imageContainer: HTMLElement;
	private $imageCacheContainer: HTMLElement;
	private $backwardButton: HTMLElement;
	private $forwardButton: HTMLElement;

	private cachedImages: UiCachedImage[] = [];
	private totalNumberOfRecords: number;
	private zoomFactor: number;
	private displayMode: UiPageDisplayMode;
	private currentImageIndex: number;

	constructor(config: UiImageDisplayConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$componentWrapper = parseHtml(
			`<div id=${config.id}" class="UiImageDisplay" tabindex="-1">
                  <div class="toolbar-container"></div>
                  <div class="image-container-wrapper">
                    <div class="image-container"></div>
                  </div>
                  <div class="handle backward-handle">${this.forwardImageSvg}</div>
                  <div class="handle forward-handle">${this.forwardImageSvg}</div>
                  <div class="image-cache-container"></div>
                </div>`);
		this.$componentWrapper.addEventListener("keydown", (e) => {
			if (e.keyCode == keyCodes.left_arrow
				|| e.keyCode == keyCodes.up_arrow) {
				this.jumpToNextImage(-1);
			} else if (e.keyCode == keyCodes.right_arrow
				|| e.keyCode == keyCodes.down_arrow
				|| e.keyCode == keyCodes.space) {
				this.jumpToNextImage(1);
			}
		});
		this.$componentWrapper.addEventListener("mousedown", (e: any) => {
			this.$componentWrapper.focus();
		});

		this.$imageContainerWrapper = this.$componentWrapper.querySelector<HTMLElement>(':scope .image-container-wrapper');
		this.$imageContainerWrapper.style.backgroundColor = config.backgroundColor;
		enableScrollViaDragAndDrop(this.$imageContainerWrapper);
		this.$imageContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .image-container');
		this.$imageContainer.style.padding = config.padding + "px";
		this.$imageCacheContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .image-cache-container');
		this.$backwardButton = this.$componentWrapper.querySelector<HTMLElement>(':scope .backward-handle');
		this.$backwardButton.addEventListener("click", () => this.jumpToNextImage(-1));
		this.$forwardButton = this.$componentWrapper.querySelector<HTMLElement>(':scope .forward-handle');
		this.$forwardButton.addEventListener("click", () => this.jumpToNextImage(1));

		this.zoomFactor = config.zoomFactor;
		this.displayMode = config.displayMode;

		if (config.cachedImages) {
			this.setCachedImages(0, config.cachedImages, config.totalNumberOfRecords);
			this.showImageByIndex(0);
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$componentWrapper;
	}

	private jumpToNextImage(direction: number) {
		this.showImageByIndex(this.currentImageIndex + direction);
		this.onImageDisplayed.fire(EventFactory.createUiImageDisplay_ImageDisplayedEvent(this._config.id, this.cachedImages[this.currentImageIndex].id));
		if (this.cachedImages.length - this.currentImageIndex - 1 < this._config.cacheSize) {
			this.onImagesRequest.fire(EventFactory.createUiImageDisplay_ImagesRequestEvent(this._config.id, this.cachedImages.length, Math.min(this.totalNumberOfRecords - this.cachedImages.length, this._config.cacheSize)));
		}
	};

	public setCachedImages(startIndex: number, images: UiCachedImageConfig[], totalNumberOfRecords: number) {
		this.totalNumberOfRecords = totalNumberOfRecords;

		images.forEach((imageConfig, index) => {
			var img = new Image();
			let uiCachedImage: UiCachedImage = {
				id: imageConfig.id,
				imageUrl: imageConfig.imageUrl,
				$img: img,
				naturalWidth: 0,
				naturalHeight: 0
			};

			img.onload = () => {
				uiCachedImage.naturalWidth = img.naturalWidth;
				uiCachedImage.naturalHeight = img.naturalHeight;
				this.updateImageSizes();
			};

			img.src = imageConfig.imageUrl;
			this.cachedImages[startIndex + index] = uiCachedImage;
			this.$imageCacheContainer.append(img);
		});

		this.updateImageSizes();
	}

	public showImage(id: string) {
		let imageIndex = this.findImageIndexById(id);
		if (imageIndex != null) {
			this.showImageByIndex(imageIndex);
		}
	}

	private showImageByIndex(imageIndex: number) {
		if (this.cachedImages[imageIndex]) {
			this.currentImageIndex = imageIndex;
			this.$imageContainer.innerHTML = '';
			this.$imageContainer.append(this.cachedImages[imageIndex].$img);
			this.updateImageSizes();
			this.$backwardButton.classList.toggle("hidden", imageIndex <= 0);
			this.$forwardButton.classList.toggle("hidden", imageIndex >= this.totalNumberOfRecords - 1);
		}
	};

	private findImageIndexById(id: string): number {
		for (let i = 0; i < this.cachedImages.length; i++) {
			var cachedImage = this.cachedImages[i];
			if (cachedImage.id === id) {
				return i;
			}
		}
		return null;
	}

	public setDisplayMode(displayMode: UiPageDisplayMode, zoomFactor: number) {
		this.displayMode = displayMode;
		this.zoomFactor = zoomFactor;
		this.updateImageSizes();
	}

	public setZoomFactor(zoomFactor: number) {
		this.zoomFactor = zoomFactor;
		this.updateImageSizes();
	}

	private updateImageSizes() {
		let currentImage = this.cachedImages[this.currentImageIndex];
		if (currentImage == null) {
			return;
		}

		let $container = this.$imageContainerWrapper;
		applyDisplayMode($container, currentImage.$img, this.displayMode, {
			padding: this._config.padding,
			zoomFactor: this.zoomFactor,
			considerScrollbars: true
		});
	}

	onResize(): void {
		this.updateImageSizes();
	}

	public destroy(): void {
		// nothing to do
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiImageDisplay", UiImageDisplay);
