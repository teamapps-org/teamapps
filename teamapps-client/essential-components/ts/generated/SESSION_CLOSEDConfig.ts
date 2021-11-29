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
import {UiSessionClosingReason} from "./UiSessionClosingReason";


export interface SESSION_CLOSEDConfig extends AbstractServerMessageConfig {
	_type?: string;
	reason: UiSessionClosingReason;
	message?: string
}


