/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiToolbarButtonConfig} from "./UiToolbarButtonConfig";
import {UiToolbarButtonGroupPosition} from "./UiToolbarButtonGroupPosition";


export interface UiToolbarButtonGroupConfig {
	_type?: string;
	groupId: string;
	buttons: UiToolbarButtonConfig[];
	position?: UiToolbarButtonGroupPosition;
	visible?: boolean;
	showGroupSeparator?: boolean;
	collapsedButton?: UiToolbarButtonConfig
}


