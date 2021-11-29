/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiBaseTreeGraphNodeConfig} from "./UiBaseTreeGraphNodeConfig";
import {UiTreeGraphNodeImageConfig} from "./UiTreeGraphNodeImageConfig";
import {UiTreeGraphNodeIconConfig} from "./UiTreeGraphNodeIconConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";


export interface UiTreeGraphNodeConfig extends UiBaseTreeGraphNodeConfig {
	_type?: string;
	parentId?: string;
	parentExpandable?: boolean;
	parentExpanded?: boolean;
	expanded?: boolean;
	hasLazyChildren?: boolean;
	sideListNodes?: UiBaseTreeGraphNodeConfig[];
	sideListExpanded?: boolean
}


