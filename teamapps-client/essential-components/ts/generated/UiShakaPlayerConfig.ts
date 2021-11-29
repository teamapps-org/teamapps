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
import {UiShakaManifestConfig} from "./UiShakaManifestConfig";
import {UiPosterImageSize} from "./UiPosterImageSize";
import {UiTrackLabelFormat} from "./UiTrackLabelFormat";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiShakaPlayerConfig extends UiComponentConfig {
	_type?: string;
	hlsUrl?: string;
	dashUrl?: string;
	posterImageUrl?: string;
	posterImageSize?: UiPosterImageSize;
	timeUpdateEventThrottleMillis?: number;
	backgroundColor?: string;
	autoplay?: boolean;
	trackLabelFormat?: UiTrackLabelFormat;
	videoDisabled?: boolean;
	timeMillis?: number;
	preferredAudioLanguage?: string
}

export interface UiShakaPlayerCommandHandler extends UiComponentCommandHandler {
	setUrls(hlsUrl: string, dashUrl: string): any;
	setTime(timeMillis: number): any;
	selectAudioLanguage(language: string, role: string): any;
}

export interface UiShakaPlayerEventSource {
	onErrorLoading: TeamAppsEvent<UiShakaPlayer_ErrorLoadingEvent>;
	onManifestLoaded: TeamAppsEvent<UiShakaPlayer_ManifestLoadedEvent>;
	onTimeUpdate: TeamAppsEvent<UiShakaPlayer_TimeUpdateEvent>;
	onEnded: TeamAppsEvent<UiShakaPlayer_EndedEvent>;
}

export interface UiShakaPlayer_ErrorLoadingEvent extends UiEvent {
}

export interface UiShakaPlayer_ManifestLoadedEvent extends UiEvent {
	manifest: UiShakaManifestConfig
}

export interface UiShakaPlayer_TimeUpdateEvent extends UiEvent {
	timeMillis: number
}

export interface UiShakaPlayer_EndedEvent extends UiEvent {
}

