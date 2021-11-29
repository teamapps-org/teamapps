/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiTreeGraphNodeImageConfig} from "./UiTreeGraphNodeImageConfig";
import {UiTreeGraphNodeIconConfig} from "./UiTreeGraphNodeIconConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";


export interface UiBaseTreeGraphNodeConfig {
	_type?: string;
	id: string;
	width: number;
	height: number;
	backgroundColor?: string;
	borderColor?: string;
	borderWidth?: number;
	borderRadius?: number;
	image?: UiTreeGraphNodeImageConfig;
	icon?: UiTreeGraphNodeIconConfig;
	template?: UiTemplateConfig;
	record?: UiClientRecordConfig;
	connectorLineColor?: string;
	connectorLineWidth?: number;
	dashArray?: string
}


