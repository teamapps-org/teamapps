import {UiDummyComponent} from "./UiDummyComponent";
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {TemplateRegistry} from "./TemplateRegistry";
import {UiComponent} from "./UiComponent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {IconPathProvider} from "./TeamAppsUiContext";


export class UiTestHarness {

	constructor() {

		let dummyComponent = new UiDummyComponent({
			id: "asdf"
		}, new TestTeamAppsUiContext());

		document.body.appendChild(dummyComponent.getMainDomElement());

		dummyComponent.attachedToDom = true;

		window.addEventListener("resize", () => {
			dummyComponent.reLayout();
		});
	}

}

class TestTeamAppsUiContext implements IconPathProvider {
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