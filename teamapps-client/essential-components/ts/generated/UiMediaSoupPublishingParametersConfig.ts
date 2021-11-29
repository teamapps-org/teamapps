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
import {UiAudioTrackConstraintsConfig} from "./UiAudioTrackConstraintsConfig";
import {UiVideoTrackConstraintsConfig} from "./UiVideoTrackConstraintsConfig";
import {UiScreenSharingConstraintsConfig} from "./UiScreenSharingConstraintsConfig";


export interface UiMediaSoupPublishingParametersConfig {
	_type?: string;
	streamUuid?: string;
	server?: UiMediaServerUrlAndTokenConfig;
	audioConstraints?: UiAudioTrackConstraintsConfig;
	videoConstraints?: UiVideoTrackConstraintsConfig;
	screenSharingConstraints?: UiScreenSharingConstraintsConfig;
	simulcast?: boolean;
	minBitrate?: number;
	maxBitrate?: number;
	keyFrameRequestDelay?: number
}


