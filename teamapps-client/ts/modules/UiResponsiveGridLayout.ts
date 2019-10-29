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
import {UiGridLayout} from "./micro-components/UiGridLayout";
import {UiResponsiveGridLayoutCommandHandler, UiResponsiveGridLayoutConfig} from "../generated/UiResponsiveGridLayoutConfig";
import {UiResponsiveGridLayoutPolicyConfig} from "../generated/UiResponsiveGridLayoutPolicyConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";

export class UiResponsiveGridLayout extends AbstractUiComponent<UiResponsiveGridLayoutConfig> implements UiResponsiveGridLayoutCommandHandler {

	private $main: HTMLElement;
	private $gridLayout: HTMLElement;

	private layoutsFromSmallToLargeMinApplicableWidth: { minApplicableWidth: number, layout: UiGridLayout }[];
	private currentLayout: UiGridLayout;

	constructor(config: UiResponsiveGridLayoutConfig,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiResponsiveGridLayout">
	<div class="UiGridLayout"></div>
</div>`);
		this.$gridLayout = this.$main.querySelector<HTMLElement>(":scope >.UiGridLayout");
		this.setFillHeight(config.fillHeight);
		this.updateLayoutPolicies(config.layoutPolicies);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	updateLayoutPolicies(layoutPolicies: UiResponsiveGridLayoutPolicyConfig[]): void {
		layoutPolicies.sort((a, b) => a.minApplicableWidth - b.minApplicableWidth);
		this.layoutsFromSmallToLargeMinApplicableWidth = layoutPolicies.map(lp => {
			return {
				minApplicableWidth: lp.minApplicableWidth,
				layout: new UiGridLayout(lp.descriptor)
			}
		});
		this.updateLayout();
	}

	onResize(): void {
		this.updateLayout();
	}

	private updateLayout() {
		const layout = this.getApplicableLayout();
		if (this.currentLayout !== layout) {
			this.currentLayout = layout;
			layout.applyTo(this.$gridLayout);
		}
	}

	private getApplicableLayout(): UiGridLayout {
		const availableWidth = this.getWidth();
		let firstTooLargePolicyIndex = this.layoutsFromSmallToLargeMinApplicableWidth.findIndex(p => p.minApplicableWidth > availableWidth);
		if (firstTooLargePolicyIndex === -1) {
			firstTooLargePolicyIndex = this.layoutsFromSmallToLargeMinApplicableWidth.length;
		}
		return this.layoutsFromSmallToLargeMinApplicableWidth[firstTooLargePolicyIndex - 1].layout;
	}

	public setFillHeight(fillHeight: boolean) {
		this.$main.classList.toggle("fill-height", !!fillHeight);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiResponsiveGridLayout", UiResponsiveGridLayout);
