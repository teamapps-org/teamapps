import {UiField} from "./UiField";
import {UiClientRecordConfig} from "../../generated/UiClientRecordConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTemplateFieldConfig} from "../../generated/UiTemplateFieldConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {parseHtml, Renderer} from "../Common";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";

export class UiTemplateField extends UiField<UiTemplateFieldConfig, UiClientRecordConfig> {
	private $main: HTMLElement;
	private templateRenderer: Renderer;

	constructor(config: UiTemplateFieldConfig, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: UiTemplateFieldConfig, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="UiTemplateField"></div>`)
		this.templateRenderer = context.templateRegistry.createTemplateRenderer(config.template);
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	protected displayCommittedValue(): void {
		this.$main.innerHTML = this.templateRenderer.render(this.getCommittedValue() && this.getCommittedValue().values);
	}

	getFocusableElement(): HTMLElement {
		return null;
	}

	getTransientValue(): UiClientRecordConfig {
		return this.getCommittedValue();
	}

	isValidData(v: UiClientRecordConfig): boolean {
		return true;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		// nothing to do!
	}

	valuesChanged(v1: UiClientRecordConfig, v2: UiClientRecordConfig): boolean {
		return false;
	}


	getReadOnlyHtml(value: UiClientRecordConfig, availableWidth: number): string {
		return `<div class="static-readonly-UiTemplateField">${value && this.templateRenderer.render(value)}</div>`;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiTemplateField", UiTemplateField);