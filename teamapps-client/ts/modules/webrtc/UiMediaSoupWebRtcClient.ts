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
import {UiMediaSoupWebRtcClientCommandHandler, UiMediaSoupWebRtcClientConfig} from "../../generated/UiMediaSoupWebRtcClientConfig";
import {Conference} from "./conference";
import {UiMediaSoupPlaybackParamatersConfig} from "../../generated/UiMediaSoupPlaybackParamatersConfig";
import {UiMediaSoupPublishingParametersConfig} from "../../generated/UiMediaSoupPublishingParametersConfig";
import {UiVideoTrackConstraintsConfig} from "../../generated/UiVideoTrackConstraintsConfig";
import {UiVideoFacingMode} from "../../generated/UiVideoFacingMode";

export class UiMediaSoupWebRtcClient extends AbstractUiComponent<UiMediaSoupWebRtcClientConfig> implements UiMediaSoupWebRtcClientCommandHandler /*UiMediaSoupWebRtcClientEventSource*/ {
	private $main: HTMLDivElement;
	private conference: Conference;
	private $video: HTMLMediaElement;

	constructor(config: UiChatInputConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiMediaSoupWebRtcClient">
	<video></video>
</div>`);
		this.$video = this.$main.querySelector<HTMLMediaElement>(":scope video");
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	publish(parameters: UiMediaSoupPublishingParametersConfig): void {
		if (this.conference != null) {
			// this.conference.destroy();
			// TODO cleanup the conference instance
			alert("conference is already instantiated!");
			return;
		}

		this.conference = new Conference({
			uid: parameters.uid,
			token: parameters.token,
			params: {
				serverUrl: parameters.serverUrl,
				minBitrate: parameters.minBitrate,
				maxBitrate: parameters.maxBitrate,
				constraints: {
					audio: parameters.audioConstraints,
					video: UiMediaSoupWebRtcClient.createVideoConstraints(parameters.videoConstraints)
				},

				localVideo: this.$video,
				errorAutoPlayCallback: () => {
					window.alert("no autoplay")
				},
			}
		});
		this.conference.publish();
	}

	playback(parameters: UiMediaSoupPlaybackParamatersConfig): void {
		if (this.conference != null) {
			// this.conference.destroy();
			// TODO cleanup the conference instance
			alert("conference is already instantiated!");
			return;
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
					window.alert("no autoplay")
				},
			},
		});
		this.conference.play();
	}


	private static createVideoConstraints(videoConstraints: UiVideoTrackConstraintsConfig): MediaTrackConstraints {
		return {
			...videoConstraints,
			facingMode: UiVideoFacingMode[videoConstraints.facingMode].toLocaleLowerCase(),
		};
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMediaSoupWebRtcClient", UiMediaSoupWebRtcClient);

