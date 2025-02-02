/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import {TeamAppsEvent} from "projector-client-object-api";

export class AudioTrackMixPlayer {

	public readonly onResumeSuccessful: TeamAppsEvent<void> = new TeamAppsEvent();
	private audioContext: AudioContext;

	constructor() {
		this.audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext || (window as any).mozAudioContext)();
	}

	public async addAudioTrack(track: MediaStreamTrack) {
		let audioTrackSource = this.audioContext.createMediaStreamSource(new MediaStream([track]));
		audioTrackSource.connect(this.audioContext.destination);
		
		track.addEventListener("ended", () => {
			// This is probably not necessary, since the audio nodes should die with their mediaStream. See: https://www.w3.org/TR/webaudio/#lifetime-AudioNode
			// Note that this event is somehow not triggered by Firefox although the track switches to state "ended" as stated in the spec.
			audioTrackSource.disconnect();
		});
	}

	public async tryResume() {
		if (this.audioContext.state !== "running") {
			await this.audioContext.resume();
			if ((this.audioContext.state as string) === "running") {
				this.onResumeSuccessful.fireIfChanged(null);
			}
		} else {
			this.onResumeSuccessful.fireIfChanged(null);
		}
	}

	public getAudioContextState() {
		return this.audioContext.state;
	}
}
