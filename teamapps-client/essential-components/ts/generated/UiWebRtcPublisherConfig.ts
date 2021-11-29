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
import {UiWebRtcPublishingSettingsConfig} from "./UiWebRtcPublishingSettingsConfig";
import {UiWebRtcPublishingErrorReason} from "./UiWebRtcPublishingErrorReason";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiWebRtcPublisherConfig extends UiComponentConfig {
	_type?: string;
	publishingSettings?: UiWebRtcPublishingSettingsConfig;
	microphoneMuted?: boolean;
	backgroundImageUrl?: string
}

export interface UiWebRtcPublisherCommandHandler extends UiComponentCommandHandler {
	publish(settings: UiWebRtcPublishingSettingsConfig): any;
	unPublish(): any;
	setMicrophoneMuted(microphoneMuted: boolean): any;
	setBackgroundImageUrl(backgroundImageUrl: string): any;
}

export interface UiWebRtcPublisherEventSource {
	onPublishingFailed: TeamAppsEvent<UiWebRtcPublisher_PublishingFailedEvent>;
}

export interface UiWebRtcPublisher_PublishingFailedEvent extends UiEvent {
	reason: UiWebRtcPublishingErrorReason
}

