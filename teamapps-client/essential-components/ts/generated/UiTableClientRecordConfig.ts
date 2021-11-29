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
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";


export interface UiTableClientRecordConfig extends UiIdentifiableClientRecordConfig {
	_type?: string;
	messages?: {[name: string]: UiFieldMessageConfig[]};
	markings?: string[];
	selected?: boolean;
	bold?: boolean
}


