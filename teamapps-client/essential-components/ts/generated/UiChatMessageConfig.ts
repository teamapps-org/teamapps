/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiChatPhotoConfig} from "./UiChatPhotoConfig";
import {UiChatFileConfig} from "./UiChatFileConfig";


export interface UiChatMessageConfig {
	_type?: string;
	id?: string;
	userImageUrl?: string;
	userNickname?: string;
	text?: string;
	photos?: UiChatPhotoConfig[];
	files?: UiChatFileConfig[]
}


