/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiPageViewBlockConfig} from "./UiPageViewBlockConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiPageViewConfig extends UiComponentConfig {
	_type?: string;
	blocks?: UiPageViewBlockConfig[]
}

export interface UiPageViewCommandHandler extends UiComponentCommandHandler {
	addBlock(block: UiPageViewBlockConfig, before: boolean, otherBlockId: string): any;
	removeBlock(blockId: string): any;
}

