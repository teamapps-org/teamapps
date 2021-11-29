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
import {UiConfigurationConfig} from "./UiConfigurationConfig";
import {UiWindowConfig} from "./UiWindowConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiNotificationConfig} from "./UiNotificationConfig";
import {UiPopupConfig} from "./UiPopupConfig";
import {UiPageTransition} from "./UiPageTransition";
import {UiNotificationPosition} from "./UiNotificationPosition";
import {UiEntranceAnimation} from "./UiEntranceAnimation";
import {UiExitAnimation} from "./UiExitAnimation";
import {UiGenericErrorMessageOption} from "./UiGenericErrorMessageOption";
import {KeyEventType} from "./KeyEventType";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiRootPanelConfig extends UiComponentConfig {
	_type?: string;
	content?: unknown
}

export interface UiRootPanelCommandHandler extends UiComponentCommandHandler {
	setContent(content: unknown, transition: UiPageTransition, animationDuration: number): any;
}

export interface UiRootPanelEventSource {
}

export interface UiRootPanel_GlobalKeyEventOccurredEvent extends UiEvent {
	eventType: KeyEventType;
	sourceComponentId: string;
	code: string;
	isComposing: boolean;
	key: string;
	charCode: number;
	keyCode: number;
	locale: string;
	location: number;
	repeat: boolean;
	altKey: boolean;
	ctrlKey: boolean;
	shiftKey: boolean;
	metaKey: boolean
}

