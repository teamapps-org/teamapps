import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {TemplateRegistry} from "./TemplateRegistry";
import {UiComponent} from "./UiComponent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {IconPathProvider, TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiRadioGroup} from "./UiRadioGroup";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";
import {UiSwitch} from "./UiSwitch";
import {createUiColorConfig} from "../generated/UiColorConfig";
import {AbstractUiReactComponent} from "./AbstractUiReactComponent";


export class UiTestHarness {

	constructor() {
		// let component = this.createRadioGroup();
		let component = this.createRadioGroup();

		(window as any).c = component;
		document.body.appendChild(component.getMainDomElement());
		component.attachedToDom = true;
		window.addEventListener("resize", () => {
			component.reLayout();
		});
	}

	private createRadioGroup() {
		let component = new UiRadioGroup({
			id: "asdf",
			radioButtons: [{
				label: "AAA",
				value: "aaa"
			}, {
				label: "BBB",
				value: "bbb"
			}, {
				label: "CCC",
				value: "ccc"
			}],
			editingMode: UiFieldEditingMode.EDITABLE_IF_FOCUSED
		}, new TestTeamAppsUiContext());
		component.onValueChanged.addListener(eventObject => console.log("Value changed: " + eventObject.value));
		return component;
	}

	private createSwitch() {
		let component = new UiSwitch({
			id: "asdf",
			value: false,
			editingMode: UiFieldEditingMode.EDITABLE_IF_FOCUSED,
			uncheckedTrackColor: createUiColorConfig(0, 255, 0),
			checkedTrackColor: createUiColorConfig(0, 0, 255),
			uncheckedButtonColor: createUiColorConfig(0, 0, 0),
			checkedButtonColor: createUiColorConfig(255, 255, 255)
		}, new TestTeamAppsUiContext());
		component.onValueChanged.addListener(eventObject => console.log("Value changed: " + eventObject.value));
		return component;
	}

}

class TestTeamAppsUiContext implements TeamAppsUiContext, IconPathProvider {
	readonly sessionId: string = "1234567890";
	readonly isHighDensityScreen: boolean = false;
	readonly executingCommand: boolean = false;
	readonly config: UiConfigurationConfig = {};
	readonly templateRegistry: TemplateRegistry = new TemplateRegistry(this);

	getComponentById(id: string): UiComponent<UiComponentConfig> {
		return null;
	}

	getIconPath(iconName: string, iconSize: number, ignoreRetina?: boolean): string {
		return null;
	};
}