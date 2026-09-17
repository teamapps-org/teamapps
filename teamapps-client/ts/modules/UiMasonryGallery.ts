/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
	UiMasonryGallery_ImageClickedEvent,
	UiMasonryGalleryCommandHandler,
	UiMasonryGalleryConfig,
	UiMasonryGalleryEventSource
} from "../generated/UiMasonryGalleryConfig";
import {UiMasonryGalleryImageConfig} from "../generated/UiMasonryGalleryImageConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

type ImageTileClass = "portrait" | "square" | "landscape";

export class UiMasonryGallery extends AbstractUiComponent<UiMasonryGalleryConfig> implements UiMasonryGalleryCommandHandler, UiMasonryGalleryEventSource {

	private static readonly TILE_RATIOS: Record<ImageTileClass, number> = {
		portrait: 0.5,
		square: 1,
		landscape: 2,
	};

	public readonly onImageClicked: TeamAppsEvent<UiMasonryGallery_ImageClickedEvent> = new TeamAppsEvent();

	private readonly $main: HTMLElement;
	private readonly $grid: HTMLElement;

	constructor(config: UiMasonryGalleryConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = document.createElement("div");
		this.$main.classList.add("UiMasonryGallery");
		this.$grid = document.createElement("div");
		this.$grid.classList.add("image-grid");
		this.$main.appendChild(this.$grid);
		this.renderImages(config.images);
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public setImages(images: UiMasonryGalleryImageConfig[]): void {
		this._config.images = images;
		this.renderImages(images);
	}

	private renderImages(images: UiMasonryGalleryImageConfig[]): void {
		this.$grid.replaceChildren();
		this.$main.classList.toggle("image-count-1", images.length === 1);

		images.forEach((image, imageIndex) => {
			const $image = document.createElement("img");
			$image.classList.add("image");
			$image.loading = "lazy";
			$image.decoding = "async";
			$image.title = image.fileName ?? "";
			$image.alt = image.fileName ?? "";

			const $tile = document.createElement("button");
			$tile.type = "button";
			$tile.classList.add("image-tile");
			$tile.addEventListener("click", event => {
				event.preventDefault();
				this.onImageClicked.fire({imageIndex});
			});
			$image.addEventListener("load", () => this.applyTileLayout($tile, $image));

			$tile.appendChild($image);
			this.$grid.appendChild($tile);
			const imageUrl = image.thumbnailUrl ?? image.imageUrl;
			$image.src = imageUrl;
			$tile.style.setProperty("--tile-image", "url('" + imageUrl + "')");
			this.applyTileLayoutWhenDimensionsAreAvailable($tile, $image);
		});
	}

	private applyTileLayout($tile: HTMLElement, $image: HTMLImageElement): void {
		if ($image.naturalWidth <= 0 || $image.naturalHeight <= 0) {
			return;
		}
		$tile.classList.remove("portrait", "square", "landscape");
		$tile.classList.add(this.getTileClass($image.naturalWidth, $image.naturalHeight));
	}

	private applyTileLayoutWhenDimensionsAreAvailable($tile: HTMLElement, $image: HTMLImageElement): void {
		let attempt = 0;
		const checkDimensions = () => {
			if ($image.naturalWidth > 0 && $image.naturalHeight > 0) {
				this.applyTileLayout($tile, $image);
			} else if (!$image.complete && attempt++ < 20) {
				requestAnimationFrame(checkDimensions);
			}
		};
		requestAnimationFrame(checkDimensions);
	}

	private getTileClass(width: number, height: number): ImageTileClass {
		const imageRatio = width / height;
		return (Object.entries(UiMasonryGallery.TILE_RATIOS) as Array<[ImageTileClass, number]>)
			.map(([className, tileRatio]) => ({
				className,
				distance: Math.abs(Math.log(imageRatio / tileRatio)),
			}))
			.sort((a, b) => a.distance - b.distance)[0].className;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMasonryGallery", UiMasonryGallery);
