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

export enum CLIENT_ERROR_Reason {
	EXCEPTION
}

export interface CLIENT_ERRORConfig extends AbstractServerMessageConfig {
	_type?: string;
	reason: CLIENT_ERROR_Reason;
	message?: string
}


