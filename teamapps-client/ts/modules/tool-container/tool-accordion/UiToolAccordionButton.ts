import {UiComponent} from "../../UiComponent";
import {UiToolbarButtonConfig} from "../../../generated/UiToolbarButtonConfig";
import {enterFullScreen, exitFullScreen, generateUUID, insertAfter, isFullScreen, parseHtml} from "../../Common";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {AbstractUiToolContainer} from "../AbstractUiToolContainer";
import {UiGridTemplateConfig} from "../../../generated/UiGridTemplateConfig";
import {UiToolAccordion} from "./UiToolAccordion";
import {createUiDropDownButtonClickInfoConfig, UiDropDownButtonClickInfoConfig} from "../../../generated/UiDropDownButtonClickInfoConfig";
import {DEFAULT_TEMPLATES} from "trivial-components";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiColorConfig} from "../../../generated/UiColorConfig";
import {createUiColorCssString} from "../../util/CssFormatUtil";

export class UiToolAccordionButton {

	public readonly onClick: TeamAppsEvent<void> = new TeamAppsEvent(this);

	private config: UiToolbarButtonConfig;
	private $buttonWrapper: HTMLElement;
	private $button: HTMLElement;
	private $dropDownCaret: HTMLElement;

	public optimizedWidth: number;
	public dropDownComponent: UiComponent;
	public $dropDown: HTMLElement;
	public $dropDownSourceButtonIndicator: HTMLElement;

	private uuidClass: string = `UiToolbarButton-${generateUUID()}`;
	private $styleTag: HTMLStyleElement;

	constructor(config: UiToolbarButtonConfig, context: TeamAppsUiContext) {
		let renderer = context.templateRegistry.createTemplateRenderer(config.template);
		this.config = config;
		this.$buttonWrapper = parseHtml(`<div class="toolbar-button-wrapper ${this.uuidClass}" data-buttonId="${config.buttonId}" tabindex="0">
	${renderer.render(config.recordData)}
	<div class="toolbar-button-caret ${config.hasDropDown ? '' : 'hidden'}">
	  <div class="caret"></div>
	</div>
	<style></style>
</div>`);

		this.$button = this.$buttonWrapper.firstElementChild as HTMLElement;
		this.optimizedWidth = AbstractUiToolContainer.optimizeButtonWidth(this.$buttonWrapper, this.$button, (config.template as UiGridTemplateConfig).maxHeight || UiToolAccordion.DEFAULT_TOOLBAR_MAX_HEIGHT);
		this.$dropDownCaret = this.$buttonWrapper.querySelector<HTMLElement>(":scope .toolbar-button-caret");
		this.$buttonWrapper.classList.toggle("hidden", !config.visible);
		this.$styleTag = this.$buttonWrapper.querySelector(":scope style");
		this.updateStyles();

		this.$buttonWrapper.addEventListener('click', () => {
			if (this.config.togglesFullScreenOnComponent) {
				if (isFullScreen()) {
					exitFullScreen();
				} else {
					enterFullScreen(context.getClientObjectById(this.config.togglesFullScreenOnComponent) as UiComponent);
				}
			}
			if (this.config.openNewTabWithUrl) {
				window.open(this.config.openNewTabWithUrl, '_blank');
			}

			this.onClick.fire();
		});
	}


	public get id() {
		return this.config.buttonId;
	}

	public get visible() {
		return this.config.visible;
	}

	public set visible(visible: boolean) {
		this.$buttonWrapper.classList.toggle("hidden", !visible)
		this.config.visible = visible;
	}

	public getMainDomElement() {
		return this.$buttonWrapper;
	}

	public set hasDropDown(hasDropDown: boolean) {
		this.config.hasDropDown = hasDropDown;
		this.$dropDownCaret.classList.toggle("hidden", !hasDropDown);
	}

	public get hasDropDown() {
		return this.config.hasDropDown;
	}

	setColors(backgroundColor: UiColorConfig, hoverBackgroundColor: UiColorConfig) {
		this.config.backgroundColor = backgroundColor;
		this.config.hoverBackgroundColor = hoverBackgroundColor;
		this.updateStyles();
	}

	private updateStyles() {
		this.$styleTag.innerHTML = '';
		this.$styleTag.innerText = `
		.${this.uuidClass} {
			background-color: ${createUiColorCssString(this.config.backgroundColor)} !important;           
        }
		.${this.uuidClass}:hover {
			background-color: ${createUiColorCssString(this.config.hoverBackgroundColor)} !important;            
        }`;
	}
}