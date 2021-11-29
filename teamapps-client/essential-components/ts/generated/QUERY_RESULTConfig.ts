/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractServerMessageConfig} from "./AbstractServerMessageConfig";


export interface QUERY_RESULTConfig extends AbstractServerMessageConfig {
	_type?: string;
	queryId: number;
	result: any
}


