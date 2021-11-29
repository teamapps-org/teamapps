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
import {UiMediaSoupPublishingParametersConfig} from "./UiMediaSoupPublishingParametersConfig";
import {UiMediaSoupPlaybackParametersConfig} from "./UiMediaSoupPlaybackParametersConfig";
import {WebRtcClientSpinnerPolicy} from "./WebRtcClientSpinnerPolicy";
import {UiMediaRetrievalFailureReason} from "./UiMediaRetrievalFailureReason";
import {UiSourceMediaTrackType} from "./UiSourceMediaTrackType";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMediaSoupV3WebRtcClientConfig extends UiComponentConfig {
	_type?: string;
	displayAreaAspectRatio?: number;
	activityLineVisible?: boolean;
	activityInactiveColor?: string;
	activityActiveColor?: string;
	icons?: string[];
	caption?: string;
	noVideoImageUrl?: string;
	spinnerPolicy?: WebRtcClientSpinnerPolicy;
	playbackVolume?: number;
	contextMenuEnabled?: boolean;
	bitrateDisplayEnabled?: boolean;
	forceRefreshCount?: number;
	publishingParameters?: UiMediaSoupPublishingParametersConfig;
	playbackParameters?: UiMediaSoupPlaybackParametersConfig
}

export interface UiMediaSoupV3WebRtcClientCommandHandler extends UiComponentCommandHandler {
	update(config: UiMediaSoupV3WebRtcClientConfig): any;
	setActive(active: boolean): any;
	setContextMenuContent(requestId: number, component: unknown): any;
	closeContextMenu(requestId: number): any;
}

export interface UiMediaSoupV3WebRtcClientEventSource {
	onSourceMediaTrackRetrievalFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SourceMediaTrackRetrievalFailedEvent>;
	onSourceMediaTrackEnded: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SourceMediaTrackEndedEvent>;
	onTrackPublishingSuccessful: TeamAppsEvent<UiMediaSoupV3WebRtcClient_TrackPublishingSuccessfulEvent>;
	onTrackPublishingFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_TrackPublishingFailedEvent>;
	onConnectionStateChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ConnectionStateChangedEvent>;
	onSubscribingSuccessful: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscribingSuccessfulEvent>;
	onSubscribingFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscribingFailedEvent>;
	onSubscriptionPlaybackFailed: TeamAppsEvent<UiMediaSoupV3WebRtcClient_SubscriptionPlaybackFailedEvent>;
	onVoiceActivityChanged: TeamAppsEvent<UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent>;
	onClicked: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ClickedEvent>;
	onContextMenuRequested: TeamAppsEvent<UiMediaSoupV3WebRtcClient_ContextMenuRequestedEvent>;
}

export interface UiMediaSoupV3WebRtcClient_SourceMediaTrackRetrievalFailedEvent extends UiEvent {
	reason: UiMediaRetrievalFailureReason
}

export interface UiMediaSoupV3WebRtcClient_SourceMediaTrackEndedEvent extends UiEvent {
	trackType: UiSourceMediaTrackType
}

export interface UiMediaSoupV3WebRtcClient_TrackPublishingSuccessfulEvent extends UiEvent {
	audio: boolean;
	video: boolean
}

export interface UiMediaSoupV3WebRtcClient_TrackPublishingFailedEvent extends UiEvent {
	audio: boolean;
	video: boolean;
	errorMessage: string
}

export interface UiMediaSoupV3WebRtcClient_ConnectionStateChangedEvent extends UiEvent {
	connected: boolean
}

export interface UiMediaSoupV3WebRtcClient_SubscribingSuccessfulEvent extends UiEvent {
}

export interface UiMediaSoupV3WebRtcClient_SubscribingFailedEvent extends UiEvent {
	errorMessage: string
}

export interface UiMediaSoupV3WebRtcClient_SubscriptionPlaybackFailedEvent extends UiEvent {
	errorMessage: string
}

export interface UiMediaSoupV3WebRtcClient_VoiceActivityChangedEvent extends UiEvent {
	active: boolean
}

export interface UiMediaSoupV3WebRtcClient_ClickedEvent extends UiEvent {
}

export interface UiMediaSoupV3WebRtcClient_ContextMenuRequestedEvent extends UiEvent {
	requestId: number
}

