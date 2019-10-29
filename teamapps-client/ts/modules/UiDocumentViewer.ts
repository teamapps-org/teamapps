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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiDocumentViewerCommandHandler, UiDocumentViewerConfig} from "../generated/UiDocumentViewerConfig";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {css, enableScrollViaDragAndDrop, generateUUID, parseHtml} from "./Common";
import {UiBorderConfig} from "../generated/UiBorderConfig";
import {createUiBorderCssString, createUiShadowCssString} from "./util/CssFormatUtil";
import {UiShadowConfig} from "../generated/UiShadowConfig";

interface Page {
	$img: HTMLElement;
	naturalWidth?: number;
	naturalHeight?: number;
}

export class UiDocumentViewer extends AbstractUiComponent<UiDocumentViewerConfig> implements UiDocumentViewerCommandHandler {

	private $componentWrapper: HTMLElement;
	private $pagesContainerWrapper: HTMLElement;
	private $pagesContainer: HTMLElement;
	private zoomFactor: number;
	private displayMode: UiPageDisplayMode;
	private pages: Page[] = [];

	private uuidClass: string;
	private $styleTag: HTMLElement;
	private pageBorder: UiBorderConfig;
	private pageSpacing: number;
	private pageShadow: UiShadowConfig;

	constructor(config: UiDocumentViewerConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.uuidClass = `UiDocumentViewer-${generateUUID()}`;

		this.$componentWrapper = parseHtml(`<div id="${config.id}" class="UiDocumentViewer ${this.uuidClass}">  
		    <style></style>
			<div class="toolbar-container"></div>  
			<div class="pages-container-wrapper">
			    <div class="pages-container"></div>
		    </div>
	    </div>`);

		this.$pagesContainerWrapper = this.$componentWrapper.querySelector<HTMLElement>(":scope .pages-container-wrapper");
		this.$styleTag = this.$componentWrapper.querySelector<HTMLElement>(":scope style");
		this.$pagesContainer = this.$componentWrapper.querySelector<HTMLElement>(':scope .pages-container');
		enableScrollViaDragAndDrop(this.$pagesContainerWrapper);

		this.zoomFactor = config.zoomFactor;
		this.displayMode = config.displayMode;

		this.setPageBorder(config.pageBorder);
		this.setPaddding(config.padding);
		this.setPageSpacing(config.pageSpacing);
		this.setPageShadow(config.pageShadow);

		if (config.pageUrls) {
			this.setPageUrls(config.pageUrls);
		}
	}

	public setPageUrls(pageUrls: string[]) {
		this.$pagesContainer.innerHTML = '';
		pageUrls.forEach((pageUrl) => {
			const img = new Image();
			let page: Page = {
				$img: img,
				naturalWidth: 0,
				naturalHeight: 0
			};

			img.onload = () => {
				page.naturalWidth = img.naturalWidth;
				page.naturalHeight = img.naturalHeight;
				this.updateImageSizes();
			};

			img.src = pageUrl;
			img.classList.add("page");

			this.pages.push(page);
			this.$pagesContainer.append(img);
		});
		this.updateImageSizes();
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
		let viewPortWidth = $(this.$pagesContainerWrapper).width() - 2 * this._config.padding;
		let viewPortHeight = $(this.$pagesContainerWrapper).height() - 2 * this._config.padding;
		let viewPortAspectRatio = viewPortWidth / viewPortHeight;

		this.pages.forEach((p) => {
			let imageAspectRatio = p.naturalWidth / p.naturalHeight;
			this.logger.trace("image: " + p.naturalWidth + "/" + p.naturalHeight + " = " + imageAspectRatio);
			this.logger.trace("viewport: " + viewPortWidth + "/" + viewPortHeight + " = " + viewPortAspectRatio);
			if (this.displayMode === UiPageDisplayMode.FIT_WIDTH) {
				css(p.$img, {
					width: Math.floor(viewPortWidth * this.zoomFactor) + "px",
					height: "auto"
				});
			} else if (this.displayMode === UiPageDisplayMode.FIT_HEIGHT) {
				css(p.$img, {
					width: "auto",
					height: Math.floor(viewPortHeight * this.zoomFactor) + "px"
				});
			} else if (this.displayMode === UiPageDisplayMode.FIT_SIZE) {
				if (imageAspectRatio > viewPortAspectRatio) {
					css(p.$img, {
						width: Math.floor(viewPortWidth * this.zoomFactor) + "px",
						height: "auto"
					});
				} else {
					css(p.$img, {
						width: "auto",
						height: Math.floor(viewPortHeight * this.zoomFactor) + "px"
					});
				}
			} else if (this.displayMode === UiPageDisplayMode.COVER) {
				if (imageAspectRatio < viewPortAspectRatio) {
					css(p.$img, {
						width: Math.floor(viewPortWidth * this.zoomFactor) + "px",
						height: "auto"
					});
				} else {
					css(p.$img, {
						width: "auto",
						height: Math.floor(viewPortHeight * this.zoomFactor) + "px"
					});
				}
			} else {
				css(p.$img, {
					width: (p.naturalWidth * this.zoomFactor) + "px",
					height: "auto"
				});
			}
		});
	}

	onResize(): void {
		this.updateImageSizes();
	}

	public doGetMainElement(): HTMLElement {
		return this.$componentWrapper;
	}

	public destroy(): void {
		// nothing to do
	}

	setPageBorder(pageBorder: UiBorderConfig): void {
		this.pageBorder = pageBorder;
		this.updateStyles();
	}

	setPaddding(padding: number): void {
		this.$pagesContainer.style.padding = padding + "px";
	}

	setPageSpacing(pageSpacing: number): void {
		this.pageSpacing = pageSpacing;
		this.updateStyles();
	}

	setPageShadow(pageShadow: UiShadowConfig): void {
		this.pageShadow = pageShadow;
		this.updateStyles();
	}

	private updateStyles() {
		this.$styleTag.innerHTML = '';
		this.$styleTag.innerText = `
		.${this.uuidClass} .page {
            ${createUiBorderCssString(this.pageBorder)}
            ${createUiShadowCssString(this.pageShadow)}
        }
        .${this.uuidClass} .page:not(:last-child) {
            margin-bottom: ${this.pageSpacing}px !important;
        }`;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiDocumentViewer", UiDocumentViewer);
