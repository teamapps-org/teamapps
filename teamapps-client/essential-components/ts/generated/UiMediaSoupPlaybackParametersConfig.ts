/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiMediaServerUrlAndTokenConfig} from "./UiMediaServerUrlAndTokenConfig";


export interface UiMediaSoupPlaybackParametersConfig {
	_type?: string;
	streamUuid?: string;
	server?: UiMediaServerUrlAndTokenConfig;
	origin?: UiMediaServerUrlAndTokenConfig;
	audio?: boolean;
	video?: boolean;
	minBitrate?: number;
	maxBitrate?: number
}


