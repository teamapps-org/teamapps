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
	AbstractComponent, bind, Component, createBorderCssString, createShadowCssString, createSpacingCssString, DtoComponent,
	executeAfterAttached, generateUUID,
	parseHtml,
	ServerObjectChannel, slideDown, slideUp,
	ProjectorEvent, Template
} from "projector-client-object-api";
import {
	DtoFormLayoutPolicy,
	DtoFormSection,
	DtoFormSectionFieldPlacement,
	DtoFormSectionFloatingFieldsPlacement,
	DtoFormSectionPlacement,
	DtoGridForm,
	DtoGridForm_SectionCollapsedStateChangedEvent,
	DtoGridFormCommandHandler,
	DtoGridFormEventSource,
	DtoSizingPolicy,
	SizeType,
	SizeTypes
} from "./generated";

export class GridForm extends AbstractComponent<DtoGridForm> implements DtoGridFormCommandHandler, DtoGridFormEventSource {

	public readonly onSectionCollapsedStateChanged: ProjectorEvent<DtoGridForm_SectionCollapsedStateChangedEvent> = new ProjectorEvent<DtoGridForm_SectionCollapsedStateChangedEvent>();

	private $mainDiv: HTMLElement;

	private sections: FormSection[];

	private layoutPoliciesFromLargeToSmall: DtoFormLayoutPolicy[];
	private activeLayoutPolicyIndex: number;
	private uiFields: Component[] = [];
	private fillRemainingHeightCheckerInterval: number;
	private sectionCollapseOverrides: { [sectionId: string]: boolean };

	private fieldWrappers = new Map<Component, HTMLDivElement>();

	constructor(config: DtoGridForm, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$mainDiv = parseHtml(`<div class="GridForm">
</div>`);

		config.fields.forEach(f => this.addField(f as Component));
		this.updateLayoutPolicies(config.layoutPolicies);

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			if (this.fillRemainingHeightCheckerInterval == null) {
				this.fillRemainingHeightCheckerInterval = window.setInterval(() => {
					this.ensureFillRemainingHeight();
				}, 30000); // chose high delay here, since it makes the scrollbar appear for a few seconds on macos
			}
		})
	}

	private addField(field: Component) {
		this.uiFields.push(field);
	}

	public doGetMainElement(): HTMLElement {
		return this.$mainDiv;
	}

	@executeAfterAttached(true)
	public onResize(): void {
		const newLayoutPolicyIndex = this.determineLayoutPolicyIndexToApply();
		if (newLayoutPolicyIndex !== this.activeLayoutPolicyIndex) {
			this.activeLayoutPolicyIndex = newLayoutPolicyIndex;
			this.applyLayoutPolicy(this.layoutPoliciesFromLargeToSmall[newLayoutPolicyIndex]);
		}
		this.ensureFillRemainingHeight();
	}

	private determineLayoutPolicyIndexToApply(): number {
		const availableWidth = this.getWidth();
		const policyIndex = this.layoutPoliciesFromLargeToSmall.findIndex(p => p.minWidth <= availableWidth);
		if (policyIndex === -1) {
			console.warn(`No applicable layout policy found for width ${availableWidth}. Applying layout with the largest minWidth.`);
			return this.layoutPoliciesFromLargeToSmall.length - 1;
		}
		return policyIndex;
	}

	@executeAfterAttached(true)
	private applyLayoutPolicy(layoutPolicy: DtoFormLayoutPolicy) {
		this.uiFields.forEach(uiField => {
			let fieldWrapper = this.fieldWrappers.get(uiField);
			if (fieldWrapper != null) {
				fieldWrapper.remove();
			}
		});
		this.sections && this.sections.forEach(section => {
			section.destroy();
			section.getMainDomElement().remove();
		});
		this.sections = layoutPolicy.sections.map(sectionConfig => {
			const section = new FormSection(sectionConfig, this.sectionCollapseOverrides[sectionConfig.id], field => this.getFieldWrapper(field));
			this.$mainDiv.appendChild(section.getMainDomElement());
			section.placeFields();
			section.onCollapsedStateChanged.addListener((collapsed) => {
				this.onSectionCollapsedStateChanged.fire({
					sectionId: sectionConfig.id,
					collapsed: collapsed
				});
				this.sectionCollapseOverrides[sectionConfig.id] = collapsed;
			});
			return section;
		});
	}

	public setSectionCollapsed(sectionId: string, collapsed: boolean) {
		this.sectionCollapseOverrides[sectionId] = collapsed;
		this.sections.filter(s => s.config.id === sectionId).forEach(s => s.setCollapsed(collapsed));
	}

	@executeAfterAttached(true)
	public updateLayoutPolicies(layoutPolicies: DtoFormLayoutPolicy[]): void {
		this.sectionCollapseOverrides = {};
		this.layoutPoliciesFromLargeToSmall = layoutPolicies.sort((a, b) => b.minWidth - a.minWidth);
		this.activeLayoutPolicyIndex = this.determineLayoutPolicyIndexToApply();
		let layoutPolicyToApply = this.layoutPoliciesFromLargeToSmall[this.activeLayoutPolicyIndex];
		this.applyLayoutPolicy(layoutPolicyToApply);
	}

	// This hack is needed because css grid does not fill the whole height of a section when as it grows (flex). This is because the height of the (flex) section is dynamic, and not a static value.
	private ensureFillRemainingHeight() {
		let scrollContainer = this.$mainDiv;
		while (scrollContainer.scrollTop === 0 && scrollContainer.parentElement != null) {
			scrollContainer = scrollContainer.parentElement;
		}
		let scrollBefore = scrollContainer.scrollTop;
		this.sections.forEach(section => section.config.fillRemainingHeight && section.updateBodyHeightToFillRemainingHeight());
		scrollContainer.scrollTop = scrollBefore;
	}

	public destroy(): void {
		super.destroy();
		window.clearInterval(this.fillRemainingHeightCheckerInterval);
	}

	addOrReplaceField(field: Component): void {
		this.addField(field);
		this.applyLayoutPolicy(this.layoutPoliciesFromLargeToSmall[this.determineLayoutPolicyIndexToApply()]);
	}


	private getFieldWrapper(field: Component): HTMLDivElement {
		let wrapper = this.fieldWrappers.get(field);
		if (wrapper == null) {
			wrapper = document.createElement("div");
			wrapper.classList.add("field-wrapper");
			this.fieldWrappers.set(field, wrapper);
		}
		return wrapper;
	}

}

class FormSection {

	public readonly onCollapsedStateChanged: ProjectorEvent<boolean> = new ProjectorEvent<boolean>();

	private uiFields: Component[] = [];

	private uuid: string;
	private $div: HTMLElement;
	private $placementStyles: HTMLElement;
	private $header: HTMLElement;
	private $headerTemplateContainer: HTMLElement;
	private $body: HTMLElement;
	private $expander: HTMLElement;
	private collapsed: boolean;

	constructor(public config: DtoFormSection, collapsedOverride: boolean, private getFieldWrapper : (field: Component) => HTMLDivElement) {
		this.uuid = generateUUID();

		const headerLineClass = config.drawHeaderLine ? 'draw-header-line' : '';
		const hasHeaderTemplateClass = config.headerTemplate ? 'has-header-template' : '';
		const hasHeaderDataClass = config.headerData ? 'has-header-data' : '';
		const collapsibleClass = config.collapsible ? 'collapsible' : '';
		const collapsedClass = this.collapsed ? 'collapsed' : '';
		const hiddenClass = config.visible ? '' : 'hidden'; // TODO discuss how the visible attribute will be handled when layout policies are updated. Does this attribute make sense at all?
		const fillRemainingHeightClass = config.fillRemainingHeight ? 'fill-remaining-height' : '';

		const marginCss = createSpacingCssString("margin", config.margin);
		const paddingCss = createSpacingCssString("padding", config.padding);
		const borderCss = createBorderCssString(config.border);
		const shadowCss = createShadowCssString(config.shadow);
		const backgroundColorCss = config.backgroundColor ? `background-color:${(config.backgroundColor ?? '')};` : '';

		const gridTemplateColumnsCss = 'grid-template-columns:' + config.columns.map(column => createCssGridRowOrColumnString(column.widthPolicy)).join(" ") + ';';
		const gridTemplateRowsCss = 'grid-template-rows:' + config.rows.map(row => createCssGridRowOrColumnString(row.heightPolicy)).join(" ") + ';';
		const gridGapCss = 'grid-gap:' + config.gridGap + 'px;';


		this.$div = parseHtml(`<div data-id="${config.id}" data-section-uuid="${this.uuid}" class="FormSection ${headerLineClass} ${hasHeaderTemplateClass} ${hasHeaderDataClass} ${collapsibleClass} ${hiddenClass} ${fillRemainingHeightClass}" style="${marginCss}${borderCss}${shadowCss}${backgroundColorCss}">
	<style></style>
    <div class="header">
        <div class="expand-button">
            <div class="teamapps-expander ${this.collapsed ? '' : 'expanded'} ${config.collapsible ? '' : 'hidden'}"></div>
            <div class="header-template-container"></div>
        </div>
        <div class="header-line"></div>
    </div>
    <div class="body" style="${paddingCss} ${gridTemplateColumnsCss} ${gridTemplateRowsCss} ${gridGapCss}">

	</div>
</div>`);
		this.$placementStyles = this.$div.querySelector<HTMLElement>(":scope style");
		this.$header = this.$div.querySelector<HTMLElement>(":scope > .header");
		this.$headerTemplateContainer = this.$header.querySelector<HTMLElement>(":scope .header-template-container");
		if (config.headerTemplate && config.headerData) {
			this.$headerTemplateContainer.appendChild(parseHtml((config.headerTemplate as Template).render(config.headerData)));
		}

		this.$expander = this.$div.querySelector<HTMLElement>(":scope .teamapps-expander");
		this.$div.querySelector<HTMLElement>(':scope .expand-button').addEventListener('click', () => {
			if (config.collapsible) {
				this.setCollapsed(!this.collapsed);
			}
		});

		this.$body = this.$div.querySelector<HTMLElement>(":scope .body");

		this.setCollapsed(collapsedOverride != null ? collapsedOverride : (config.collapsible && config.collapsed), false);
	}

	public placeFields() {
		let createSectionPlacementStyles: (placement: DtoFormSectionPlacement) => CssDeclarations = (placement: DtoFormSectionPlacement) => {
			return {
				"grid-column": `${placement.column + 1} / ${placement.column + placement.colSpan + 1}`,
				"grid-row": `${placement.row + 1} / ${placement.row + placement.rowSpan + 1}`,
				"justify-self": `${placement.horizontalAlignment}`,
				"align-self": `${placement.verticalAlignment}`,
				"min-width": placement.minWidth ? `${placement.minWidth}px` : '',
				"max-width": placement.maxWidth ? `${placement.maxWidth}px` : '',
				"margin": `${this.config.rows[placement.row].topPadding}px ${this.config.columns[placement.column].rightPadding}px ${this.config.rows[placement.row].bottomPadding}px ${this.config.columns[placement.column].leftPadding}px`
			};
		};
		const allCssRules: { [fieldNameOrWrapperId: string]: CssDeclarations } = {};

		this.config.fieldPlacements.forEach(placement => {
			const placementId = generateUUID(true);
			if (this.isFormSectionFieldPlacement(placement)) {
				const uiField = placement.field as Component;
				uiField.onVisibilityChanged.addListener(this.updateGroupVisibility);
				this.uiFields.push(uiField);
				allCssRules[placementId] = {
					...createSectionPlacementStyles(placement),
					"min-height": placement.minHeight ? `${placement.minHeight}px` : '',
					"max-height": placement.maxHeight ? `${placement.maxHeight}px` : ''
				};
				let fieldWrapper = this.getFieldWrapper(uiField);
				fieldWrapper.appendChild(uiField.getMainElement());
				fieldWrapper.setAttribute("data-placement-id", placementId);
				this.$body.appendChild(fieldWrapper);
			} else if (this.isFormSectionFloatingFieldsPlacement(placement)) {
				let $container = parseHtml(`<div class="FormSectionFloatingFieldsPlacement" data-placement-id="${placementId}"></div>`);
				allCssRules[placementId] = {
					...createSectionPlacementStyles(placement),
					"flex-wrap": placement.wrap ? "wrap" : "nowrap"
				};
				placement.floatingFields.forEach(floatingField => {
					const uiField = floatingField.field as Component;
					uiField.onVisibilityChanged.addListener(this.updateGroupVisibility);
					this.uiFields.push(uiField);
					const floatingFieldPlacementId = generateUUID(true);
					allCssRules[floatingFieldPlacementId] = {
						"min-width": floatingField.minWidth ? `${floatingField.minWidth}px` : '',
						"max-width": floatingField.maxWidth ? `${floatingField.maxWidth}px` : '',
						"min-height": floatingField.minHeight ? `${floatingField.minHeight}px` : '',
						"max-height": floatingField.maxHeight ? `${floatingField.maxHeight}px` : '',
						"margin": `${placement.verticalSpacing / 2}px ${placement.horizontalSpacing / 2}px`
					};

					let fieldWrapper = this.getFieldWrapper(uiField);
					fieldWrapper.appendChild(uiField.getMainElement());
					fieldWrapper.setAttribute("data-placement-id", floatingFieldPlacementId);
					$container.appendChild(fieldWrapper);
				});
				this.$body.appendChild($container);
			}
		});

		this.$placementStyles.textContent = this.createPlacementStylesCssString(allCssRules);
		this.updateGroupVisibility();
	}

	@bind
	private updateGroupVisibility() {
		let hasVisibleFields = this.uiFields.filter(uiField => uiField.isVisible()).length > 0;
		this.$div.classList.toggle("hidden", !hasVisibleFields && this.config.hideWhenNoVisibleFields);
	}

	public destroy() {
		this.uiFields.forEach(f => f.onVisibilityChanged.removeListener(this.updateGroupVisibility));
	}

	createPlacementStylesCssString(cssRules: { [fieldNameOrWrapperId: string]: CssDeclarations }): string {
		return Object.keys(cssRules).map(placementId => {
			let cssRule = cssRules[placementId];
			let cssDeclarationsString = Object.keys(cssRule)
				.filter(cssProperty => !!cssRule[cssProperty])
				.map(cssProperty => `${cssProperty}: ${cssRule[cssProperty]}`).join(";\n");
			return `[data-section-uuid="${this.uuid}"] [data-placement-id="${placementId}"] {${cssDeclarationsString}}`;
		}).join('\n');
	}

	private isFormSectionFieldPlacement(placement: DtoFormSectionPlacement): placement is DtoFormSectionFieldPlacement {
		return placement._type === "DtoFormSectionFieldPlacement";
	}

	private isFormSectionFloatingFieldsPlacement(placement: DtoFormSectionPlacement): placement is DtoFormSectionFloatingFieldsPlacement {
		return placement._type === "DtoFormSectionFloatingFieldsPlacement";
	}

	public getMainDomElement(): HTMLElement {
		return this.$div;
	}

	updateBodyHeightToFillRemainingHeight() {
		this.$body.style.position = "absolute";
		this.$body.style.minHeight = (this.$div.offsetHeight - this.$header.offsetHeight) + "px";
		this.$body.style.position = "";
	}

	setCollapsed(collapsed: boolean, animate = true): void {
		this.collapsed = collapsed;
		this.onCollapsedStateChanged.fire(this.collapsed);
		this.$expander.classList.toggle("expanded", !this.collapsed);
		this.$div.classList.toggle("collapsed", this.collapsed);
		if (animate) {
			if (!this.collapsed) {
				this.$body.classList.remove('hidden');
				slideDown(this.$body);
			} else {
				slideUp(this.$body);
			}
		} else {
			this.$body.classList.toggle('hidden', collapsed);
		}
	}
}

export function createCssGridRowOrColumnString(sizePolicy: DtoSizingPolicy) {
	let maxSizeString: string;
	if (sizePolicy.type === SizeTypes.AUTO) {
		maxSizeString = 'auto';
	} else if (sizePolicy.type === SizeTypes.RELATIVE) {
		maxSizeString = (sizePolicy.value * 100) + sizePolicy.type;
	} else {
		maxSizeString = sizePolicy.value + sizePolicy.type;
	}
	return sizePolicy.minAbsoluteSize ? `minmax(${sizePolicy.minAbsoluteSize}px, ${maxSizeString})` : maxSizeString;
}

class CssDeclarations {
	[name: string]: string;
}


