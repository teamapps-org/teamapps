import {UiField} from "./UiField";
import {UiClientRecordConfig} from "../../generated/UiClientRecordConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTemplateField_ClickedEvent, UiTemplateFieldCommandHandler, UiTemplateFieldConfig, UiTemplateFieldEventSource} from "../../generated/UiTemplateFieldConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {parseHtml, Renderer} from "../Common";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export class UiTemplateField extends UiField<UiTemplateFieldConfig, UiClientRecordConfig> implements UiTemplateFieldCommandHandler, UiTemplateFieldEventSource {

    public readonly onClicked: TeamAppsEvent<UiTemplateField_ClickedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private templateRenderer: Renderer;

	constructor(config: UiTemplateFieldConfig, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: UiTemplateFieldConfig, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="UiTemplateField"></div>`);
		this.$main.addEventListener("click", ev => this.onClicked.fire({}));
		this.update(config);
	}

	update(config: UiTemplateFieldConfig): void {
		this.templateRenderer = this._context.templateRegistry.createTemplateRenderer(config.template);
		this.displayCommittedValue();
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
		return `<div class="static-readonly-UiTemplateField">${value && this.templateRenderer.render(value.values)}</div>`;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiTemplateField", UiTemplateField);