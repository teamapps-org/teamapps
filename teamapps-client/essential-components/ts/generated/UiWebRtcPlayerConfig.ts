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
import {UiWebRtcPlayingSettingsConfig} from "./UiWebRtcPlayingSettingsConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiWebRtcPlayerConfig extends UiComponentConfig {
	_type?: string;
	playingSettings?: UiWebRtcPlayingSettingsConfig;
	backgroundImageUrl?: string
}

export interface UiWebRtcPlayerCommandHandler extends UiComponentCommandHandler {
	play(settings: UiWebRtcPlayingSettingsConfig): any;
	stopPlaying(): any;
	setBackgroundImageUrl(backgroundImageUrl: string): any;
}


