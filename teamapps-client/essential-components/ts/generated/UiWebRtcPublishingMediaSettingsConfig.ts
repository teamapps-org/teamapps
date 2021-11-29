/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiAudioCodec} from "./UiAudioCodec";
import {UiVideoCodec} from "./UiVideoCodec";


export interface UiWebRtcPublishingMediaSettingsConfig {
	_type?: string;
	audio?: boolean;
	audioCodec?: UiAudioCodec;
	audioKiloBitsPerSecond?: number;
	video?: boolean;
	videoCodec?: UiVideoCodec;
	videoWidth?: number;
	videoHeight?: number;
	videoFps?: number;
	videoKiloBitsPerSecond?: number;
	screen?: boolean
}


