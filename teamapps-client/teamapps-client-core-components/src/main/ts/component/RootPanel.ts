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
import {DtoRootPanel, DtoRootPanelCommandHandler} from "../generated";
import {AbstractLegacyComponent, Component, PageTransition, parseHtml, ServerObjectChannel} from "projector-client-object-api";
import {pageTransition} from "../Common";

// noinspection JSUnusedGlobalSymbols
export class RootPanel extends AbstractLegacyComponent<DtoRootPanel> implements DtoRootPanelCommandHandler {

	private $root: HTMLElement;
	private content: Component;
	private $contentWrapper: HTMLElement;
	private $imagePreloadDiv: HTMLElement;

	constructor(config: DtoRootPanel, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);

		this.$root = parseHtml(`<div class="RootPanel">
              <div class="image-preload-div"></div>
		</div>`);
		this.$imagePreloadDiv = this.$root.querySelector<HTMLElement>(":scope .image-preload-div");
		this.setContent(config.content as Component);
		this.setBackground(config.backgroundImageUrl, config.blurredBackgroundImageUrl, config.backgroundColor, 0);
	}

	public doGetMainElement(): HTMLElement {
		return this.$root;
	}

	public setContent(content: Component, transition: PageTransition | null = null, animationDuration: number = 0): void {
		if (content == this.content) {
			return;
		}

		let $oldContentWrapper = this.$contentWrapper;

		this.content = content;

		this.$contentWrapper = parseHtml(`<div class="child-component-wrapper">`);
		if (content != null) {
			this.$contentWrapper.appendChild(content.getMainElement());
		}
		this.$root.appendChild(this.$contentWrapper);

		if (transition != null && animationDuration > 0) {
			pageTransition($oldContentWrapper, this.$contentWrapper, transition, animationDuration, () => {
				$oldContentWrapper && $oldContentWrapper.remove();
			});
		} else {
			$oldContentWrapper && $oldContentWrapper.remove();
		}
	}

	private static async loadImage(url: string) {
		return new Promise<void>((resolve, reject) => {
			let img = new Image();
			img.loading = "eager";
			img.addEventListener("load", () => resolve());
			img.addEventListener("error", () => reject());
			img.src = url; // preload
		})
	}

	public async setBackground(backgroundImageUrl: string, blurredBackgroundImageUrl: string, backgroundColor: string, animationDuration: number) {
		this.config.backgroundImageUrl = backgroundImageUrl;
		this.config.blurredBackgroundImageUrl = blurredBackgroundImageUrl;
		this.config.backgroundColor = backgroundColor;
		await Promise.all([backgroundImageUrl, blurredBackgroundImageUrl].filter(url => url != null).map(url => RootPanel.loadImage(url)));
		this.updateBackground(animationDuration);
	}

	public setBackgroundColor(backgroundColor: string, animationDuration: number) {
		this.config.backgroundColor = backgroundColor;
		this.updateBackground(animationDuration);
	}

	private updateBackground(animationDuration: number) {
		[null, ".teamapps-backgroundImage", ".teamapps-blurredBackgroundImage"].forEach(selector => this.setStyle(selector, {"transition": "background-image ${animationDuration}ms ease-in-out, background-color ${animationDuration}ms ease-in-out"}))

		this.$root.clientWidth; // ensure the css is applied!

		[null, ".teamapps-backgroundImage", ".teamapps-blurredBackgroundImage"].forEach(selector => this.setStyle(selector, {"background-color": this.config.backgroundColor || ''}));
		[null, ".teamapps-backgroundImage"].forEach(selector => this.setStyle(selector, {"background-image": this.config.backgroundImageUrl ? `url('${this.config.backgroundImageUrl}')` : 'none'}));
		[".teamapps-blurredBackgroundImage"].forEach(selector => this.setStyle(selector, {"background-image": this.config.blurredBackgroundImageUrl ? `url('${this.config.blurredBackgroundImageUrl}')` : 'none'}));
	}

	public destroy(): void {
		super.destroy();
	}

}


