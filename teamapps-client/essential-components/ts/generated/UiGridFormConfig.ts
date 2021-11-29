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
import {UiFormLayoutPolicyConfig} from "./UiFormLayoutPolicyConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiGridFormConfig extends UiComponentConfig {
	_type?: string;
	fields: unknown[];
	layoutPolicies: UiFormLayoutPolicyConfig[]
}

export interface UiGridFormCommandHandler extends UiComponentCommandHandler {
	updateLayoutPolicies(layoutPolicies: UiFormLayoutPolicyConfig[]): any;
	setSectionCollapsed(sectionId: string, collapsed: boolean): any;
	addOrReplaceField(field: unknown): any;
}

export interface UiGridFormEventSource {
	onSectionCollapsedStateChanged: TeamAppsEvent<UiGridForm_SectionCollapsedStateChangedEvent>;
}

export interface UiGridForm_SectionCollapsedStateChangedEvent extends UiEvent {
	sectionId: string;
	collapsed: boolean
}

