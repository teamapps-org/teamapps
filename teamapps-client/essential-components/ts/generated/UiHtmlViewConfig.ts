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
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiHtmlViewConfig extends UiComponentConfig {
	_type?: string;
	html?: string;
	componentsByContainerElementSelector?: {[name: string]: unknown[]};
	contentHtmlByContainerElementSelector?: {[name: string]: string}
}

export interface UiHtmlViewCommandHandler extends UiComponentCommandHandler {
	addComponent(containerElementSelector: string, component: unknown, clearContainer: boolean): any;
	removeComponent(component: unknown): any;
	setContentHtml(containerElementSelector: string, html: string): any;
}


