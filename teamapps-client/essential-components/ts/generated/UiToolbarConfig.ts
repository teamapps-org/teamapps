/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractUiToolContainerConfig} from "./AbstractUiToolContainerConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiToolbarButtonGroupConfig} from "./UiToolbarButtonGroupConfig";
import {UiToolbarButtonConfig} from "./UiToolbarButtonConfig";
import {UiDropDownButtonClickInfoConfig} from "./UiDropDownButtonClickInfoConfig";
import {AbstractUiToolContainerCommandHandler} from "./AbstractUiToolContainerConfig";
import {AbstractUiToolContainerEventSource} from "./AbstractUiToolContainerConfig";


export interface UiToolbarConfig extends AbstractUiToolContainerConfig {
	_type?: string;
	logoImage?: string
}

export interface UiToolbarCommandHandler extends AbstractUiToolContainerCommandHandler {
	setLogoImage(logoImage: string): any;
}

export interface UiToolbarEventSource extends AbstractUiToolContainerEventSource {
}

