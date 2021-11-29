/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";


export interface UiHierarchicalClientRecordConfig extends UiIdentifiableClientRecordConfig {
	_type?: string;
	parentId?: number;
	expanded?: boolean;
	lazyChildren?: boolean;
	selectable?: boolean
}


