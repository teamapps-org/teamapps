/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {UiGridLayoutConfig} from "../../generated/UiGridLayoutConfig";
import {
	createCssGridRowOrColumnString,
	createUiBorderCssObject,
	createUiColorCssObject,
	createUiShadowCssObject,
	createUiSpacingCssObject,
	cssHorizontalAlignmentByUiVerticalAlignment, cssObjectToString, CssPropertyObject,
	cssVerticalAlignmentByUiVerticalAlignment
} from "../util/CssFormatUtil";
import {UiFormSectionPlacementConfig} from "../../generated/UiFormSectionPlacementConfig";
import {UiGridPlacementConfig} from "../../generated/UiGridPlacementConfig";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {generateUUID, parseHtml} from "../Common";
import {UiComponentGridPlacementConfig} from "../../generated/UiComponentGridPlacementConfig";
import {UiFloatingComponentGridPlacementConfig} from "../../generated/UiFloatingComponentGridPlacementConfig";
import {UiComponent} from "../UiComponent";

export class UiGridLayout {

	private uuid = generateUUID();

	constructor(private config: UiGridLayoutConfig) {
	}

	public applyTo($container: HTMLElement) {
		$container.setAttribute("data-grid-layout-uuid", this.uuid);

		const containerCssObject = this.createContainerStyles();
		const cssPropertyObjectByPlacementId = this.placeComponents($container);
		
		let $stylesContainer: HTMLElement = $container.querySelector<HTMLElement>(":scope > style[data-grid-layout-placement-styles]");
		if ($stylesContainer == null) {
			$stylesContainer = parseHtml("<style data-grid-layout-placement-styles></style>");
			$container.appendChild($stylesContainer);
		}
		$stylesContainer.innerText = this.createStylesCssString(containerCssObject, cssPropertyObjectByPlacementId);
	}

	private createContainerStyles() {
		return {
			... createUiSpacingCssObject("margin", this.config.margin),
			... createUiSpacingCssObject("padding", this.config.padding),
			... createUiBorderCssObject(this.config.border),
			... createUiShadowCssObject(this.config.shadow),
			... createUiColorCssObject("background-color", this.config.backgroundColor),
			'grid-template-columns': this.config.columns.map(column => createCssGridRowOrColumnString(column.widthPolicy)).join(" ") + ';',
			'grid-template-rows': this.config.rows.map(row => createCssGridRowOrColumnString(row.heightPolicy)).join(" ") + ';',
			'grid-gap': this.config.gridGap + 'px;',
			'justify-items': cssHorizontalAlignmentByUiVerticalAlignment[this.config.horizontalAlignment],
			'align-items': cssVerticalAlignmentByUiVerticalAlignment[this.config.verticalAlignment]
		};
	}

	private placeComponents($container: HTMLElement) {
		const cssRules: { [componentNameOrWrapperId: string]: CssPropertyObject } = {};

		this.config.componentPlacements.forEach(placement => {
			const placementId = generateUUID();
			if (this.isSimplePlacement(placement)) {
				const component = placement.component as UiComponent;
				cssRules[placementId] = {
					...this.createPlacementStyles(placement),
					"min-height": placement.minHeight ? `${placement.minHeight}px` : '',
					"max-height": placement.maxHeight ? `${placement.maxHeight}px` : ''
				};
				component.getMainElement().setAttribute("data-placement-uuid", placementId);
				$container.appendChild(component.getMainElement());
			} else if (this.isFloatingPlacement(placement)) {
				let $floatingContainer = parseHtml(`<div data-placement-uuid="${placementId}"></div>`);
				cssRules[placementId] = {
					...this.createPlacementStyles(placement),
					"flex-wrap": placement.wrap ? "wrap" : "nowrap"
				};
				placement.components.forEach(floatingComponent => {
					const uiComponent = floatingComponent.component as AbstractUiComponent;
					cssRules[uiComponent.getId()] = {
						"min-width": floatingComponent.minWidth ? `${floatingComponent.minWidth}px` : '',
						"max-width": floatingComponent.maxWidth ? `${floatingComponent.maxWidth}px` : '',
						"min-height": floatingComponent.minHeight ? `${floatingComponent.minHeight}px` : '',
						"max-height": floatingComponent.maxHeight ? `${floatingComponent.maxHeight}px` : '',
						"margin": `${placement.verticalSpacing / 2}px ${placement.horizontalSpacing / 2}px`
					};
					uiComponent.getMainElement().setAttribute("data-placement-uuid", placementId);
					$floatingContainer.appendChild(uiComponent.getMainElement());
				});
				$container.appendChild($floatingContainer);
			}
		});

		return cssRules;
	}

	private createPlacementStyles(placement: UiGridPlacementConfig): CssPropertyObject {
		return {
			"grid-column": `${placement.column + 1} / ${placement.column + placement.colSpan + 1}`,
			"grid-row": `${placement.row + 1} / ${placement.row + placement.rowSpan + 1}`,
			"justify-self": cssHorizontalAlignmentByUiVerticalAlignment[placement.horizontalAlignment],
			"align-self": cssVerticalAlignmentByUiVerticalAlignment[placement.verticalAlignment],
			"min-width": placement.minWidth ? `${placement.minWidth}px` : '',
			"max-width": placement.maxWidth ? `${placement.maxWidth}px` : '',
			"margin": `${this.config.rows[placement.row].topPadding}px ${this.config.columns[placement.column].rightPadding}px ${this.config.rows[placement.row].bottomPadding}px ${this.config.columns[placement.column].leftPadding}px`
		};
	}

	private createStylesCssString(containerCssObject: CssPropertyObject, placementCssObjects: { [placementUuid: string]: CssPropertyObject }): string {
		const containerCss = `[data-grid-layout-uuid="${this.uuid}"] {
			${cssObjectToString(containerCssObject)}
		}`;
		const placementsCss = Object.keys(placementCssObjects).map(placementId => {
			let cssRule = placementCssObjects[placementId];
			let cssPropertyObjectString = cssObjectToString(cssRule);
			return `[data-grid-layout-uuid="${this.uuid}"] [data-placement-uuid="${placementId}"] {
				${cssPropertyObjectString}
			}`;
		}).join('\n');
		return containerCss + placementsCss;
	}

	private isSimplePlacement(placement: UiFormSectionPlacementConfig): placement is UiComponentGridPlacementConfig {
		return placement._type === "UiComponentGridPlacement";
	}

	private isFloatingPlacement(placement: UiFormSectionPlacementConfig): placement is UiFloatingComponentGridPlacementConfig {
		return placement._type === "UiFloatingComponentGridPlacement";
	}

	public getAllComponents(): UiComponent[] {
		return this.config.componentPlacements.map(p => {
			if (this.isSimplePlacement(p)) {
				return p.component as UiComponent;
			} else if (this.isFloatingPlacement(p)) {
				return p.components.map(c => c.component as UiComponent);
			}
		}).flat(Number.MAX_SAFE_INTEGER) as UiComponent[];
	}
}
