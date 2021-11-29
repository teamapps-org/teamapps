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
import {UiPosterImageSize} from "./UiPosterImageSize";
import {UiMediaPreloadMode} from "./UiMediaPreloadMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiVideoPlayerConfig extends UiComponentConfig {
	_type?: string;
	url: string;
	autoplay?: boolean;
	showControls?: boolean;
	posterImageUrl?: string;
	posterImageSize?: UiPosterImageSize;
	sendPlayerProgressEventsEachXSeconds?: number;
	backgroundColor?: string;
	preloadMode?: UiMediaPreloadMode
}

export interface UiVideoPlayerCommandHandler extends UiComponentCommandHandler {
	setUrl(url: string): any;
	setPreloadMode(preloadMode: UiMediaPreloadMode): any;
	setAutoplay(autoplay: boolean): any;
	play(): any;
	pause(): any;
	jumpTo(timeInSeconds: number): any;
}

export interface UiVideoPlayerEventSource {
	onErrorLoading: TeamAppsEvent<UiVideoPlayer_ErrorLoadingEvent>;
	onPlayerProgress: TeamAppsEvent<UiVideoPlayer_PlayerProgressEvent>;
	onEnded: TeamAppsEvent<UiVideoPlayer_EndedEvent>;
}

export interface UiVideoPlayer_ErrorLoadingEvent extends UiEvent {
}

export interface UiVideoPlayer_PlayerProgressEvent extends UiEvent {
	positionInSeconds: number
}

export interface UiVideoPlayer_EndedEvent extends UiEvent {
}

