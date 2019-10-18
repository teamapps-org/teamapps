/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import {AbstractUiComponent} from "../AbstractUiComponent";
import {UiChatInputConfig} from "../../generated/UiChatInputConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {parseHtml} from "../Common";
import {
	UiMediaSoupWebRtcClient_OnPlaybackProfileChangedEvent,
	UiMediaSoupWebRtcClientCommandHandler,
	UiMediaSoupWebRtcClientConfig,
	UiMediaSoupWebRtcClientEventSource
} from "../../generated/UiMediaSoupWebRtcClientConfig";
import {Conference} from "./conference";
import {UiMediaSoupPlaybackParamatersConfig} from "../../generated/UiMediaSoupPlaybackParamatersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../generated/UiMediaSoupPublishingParametersConfig";
import {UiVideoTrackConstraintsConfig} from "../../generated/UiVideoTrackConstraintsConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiMulticastPlaybackProfile} from "../../generated/UiMulticastPlaybackProfile";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";

export class UiMediaSoupWebRtcClient extends AbstractUiComponent<UiMediaSoupWebRtcClientConfig> implements UiMediaSoupWebRtcClientCommandHandler, UiMediaSoupWebRtcClientEventSource {

	public readonly onOnPlaybackProfileChanged: TeamAppsEvent<UiMediaSoupWebRtcClient_OnPlaybackProfileChangedEvent>;

	private $main: HTMLDivElement;
	private conference: Conference;
	private $video: HTMLMediaElement;
	private $profileDisplay: HTMLMediaElement;

	constructor(config: UiMediaSoupWebRtcClientConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupWebRtcClient">
	<video></video>
	<div class="profile">.</div>
</div>`);
		this.$video = this.$main.querySelector<HTMLMediaElement>(":scope video");
		this.$profileDisplay = this.$main.querySelector<HTMLMediaElement>(":scope .profile");

		if (config.initialPlaybackOrPublishParams != null) {
			if (config.initialPlaybackOrPublishParams._type === 'UiMediaSoupPlaybackParamaters') {
				this.playback(config.initialPlaybackOrPublishParams);
			} else if (config.initialPlaybackOrPublishParams._type === 'UiMediaSoupPublishingParameters') {
				this.publish(config.initialPlaybackOrPublishParams);
			}
		}
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	@executeWhenFirstDisplayed(true)
	publish(parameters: UiMediaSoupPublishingParametersConfig): void {
		if (this.conference != null) {
			this.stop();
		}

		let constraints = {
			audio: parameters.audioConstraints,
			video: UiMediaSoupWebRtcClient.createVideoConstraints(parameters.videoConstraints)
		};
		console.log(constraints);
		this.conference = new Conference({
			uid: parameters.uid,
			token: parameters.token,
			params: {
				serverUrl: parameters.serverUrl,
				minBitrate: parameters.minBitrate,
				maxBitrate: parameters.maxBitrate,
				constraints: constraints,

				localVideo: this.$video,
				errorAutoPlayCallback: () => {
					console.error("no autoplay on publisher??");
				},
				onProfileChange: (profile: string) => {
					console.error("profile changed on publisher?? " + profile);
				}
			}
		});
		this.conference.publish();
	}

	@executeWhenFirstDisplayed(true)
	playback(parameters: UiMediaSoupPlaybackParamatersConfig): void {
		console.log(parameters);
		if (this.conference != null) {
			this.stop();
		}

		this.conference = new Conference({
			uid: parameters.uid,
			token: null,
			params: {
				serverUrl: parameters.serverUrl,
				audio: parameters.audio,
				video: parameters.video,
				minBitrate: 100,
				maxBitrate: 10000000,
				constraints: null, // not needed for publishing

				localVideo: this.$video,
				errorAutoPlayCallback: () => {
					console.error("no autoplay");
				},
				onProfileChange: (profile: string) => {
					console.log("profile" + profile);
					this.$profileDisplay.innerText = profile;
					this.onOnPlaybackProfileChanged.fire({profile: UiMulticastPlaybackProfile[profile.toUpperCase() as any] as any});
				}
			},
		});
		this.conference.play();
	}

	@executeWhenFirstDisplayed()
	stop() {
		if (this.conference != null) {
			this.conference.stop()
		}
	}


	private static createVideoConstraints(videoConstraints: UiVideoTrackConstraintsConfig): MediaTrackConstraints {
		return {
			...videoConstraints,
			facingMode: null // TODO UiVideoFacingMode[videoConstraints.facingMode].toLocaleLowerCase() ==> make nullable!!!!
		};
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupWebRtcClient", UiMediaSoupWebRtcClient);

