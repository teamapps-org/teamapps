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
import {UiWaitingVideoInfoConfig} from "./UiWaitingVideoInfoConfig";
import {UiAudioInputDeviceInfoConfig} from "./UiAudioInputDeviceInfoConfig";
import {UiVideoInputDeviceInfoConfig} from "./UiVideoInputDeviceInfoConfig";
import {UiPageDisplayMode} from "./UiPageDisplayMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiLiveStreamComponentConfig extends UiComponentConfig {
	_type?: string;
	backgroundImage?: string;
	backgroundImageDisplayMode?: UiPageDisplayMode;
	volume?: number
}

export interface UiLiveStreamComponentCommandHandler extends UiComponentCommandHandler {
	showWaitingVideos(videoInfos: UiWaitingVideoInfoConfig[], offsetSeconds: number, stopLiveStream: boolean): any;
	stopWaitingVideos(): any;
	startHttpLiveStream(url: string): any;
	startLiveStreamComLiveStream(url: string): any;
	startYouTubeLiveStream(url: string): any;
	startCustomEmbeddedLiveStreamPlayer(playerEmbedHtml: string, embedContainerId: string): any;
	stopLiveStream(): any;
	displayImageOverlay(imageUrl: string, displayMode: UiPageDisplayMode, useVideoAreaAsFrame: boolean): any;
	removeImageOverlay(): any;
	displayInfoTextOverlay(text: string): any;
	removeInfoTextOverlay(): any;
	setVolume(volume: number): any;
}

export interface UiLiveStreamComponentEventSource {
	onResultOfRequestInputDeviceAccess: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent>;
	onResultOfRequestInputDeviceInfo: TeamAppsEvent<UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent>;
}

export interface UiLiveStreamComponent_ResultOfRequestInputDeviceAccessEvent extends UiEvent {
	microphoneAccessGranted: boolean;
	cameraAccessGranted: boolean
}

export interface UiLiveStreamComponent_ResultOfRequestInputDeviceInfoEvent extends UiEvent {
	audioInputDeviceInfo: UiAudioInputDeviceInfoConfig;
	videoInputDeviceInfo: UiVideoInputDeviceInfoConfig
}

