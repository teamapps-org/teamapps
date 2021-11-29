/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiRepeatableAnimation} from "./UiRepeatableAnimation";
import {UiEntranceAnimation} from "./UiEntranceAnimation";
import {UiExitAnimation} from "./UiExitAnimation";


export interface UiNotificationBarItemConfig {
	_type?: string;
	id?: string;
	icon?: string;
	iconAnimation?: UiRepeatableAnimation;
	text?: string;
	backgroundColor?: string;
	borderColor?: string;
	textColor?: string;
	padding?: UiSpacingConfig;
	entranceAnimation?: UiEntranceAnimation;
	exitAnimation?: UiExitAnimation;
	dismissible?: boolean;
	displayTimeInMillis?: number;
	progressBarVisible?: boolean
}


