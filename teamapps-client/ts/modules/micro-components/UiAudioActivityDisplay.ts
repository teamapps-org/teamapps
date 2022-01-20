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

import {Util} from "leaflet";
import {css, parseHtml} from "../Common";

export class UiAudioActivityDisplay {
	private $main: HTMLElement;
	private $activityDisplay: HTMLElement;
	private $levelDiv: HTMLElement;
	private mediaStreamSource: MediaStreamAudioSourceNode;
	private audioContext: AudioContext;


	constructor() {
		this.$main = parseHtml(`<div class="UiAudioActivityDisplay">
	<div class="level-div"></div>
</div>`);
		this.$activityDisplay = this.$main;
		this.$levelDiv = this.$main.querySelector<HTMLElement>(':scope .level-div');

	}

	private analyserNode: AnalyserNode;

	private intervalId: number;

	public bindToStream(mediaStream: MediaStream) {
		let audioContext = new AudioContext();

		let audioSource = audioContext.createMediaStreamSource(mediaStream);
		let audioGain1 = audioContext.createGain();
		let audioChannelSplitter = audioContext.createChannelSplitter(audioSource.channelCount);

		audioSource.connect(audioGain1);
		audioGain1.connect(audioChannelSplitter);

		let audioAnalyser: AnalyserNode[] = [];
		let freqs: Uint8Array[] = [];
		for (let i = 0; i < audioSource.channelCount; i++) {
			audioAnalyser[i] = audioContext.createAnalyser();
			audioAnalyser[i].smoothingTimeConstant = 0.1;
			audioAnalyser[i].fftSize = 32;
			freqs[i] = new Uint8Array(audioAnalyser[i].frequencyBinCount);

			audioChannelSplitter.connect(audioAnalyser[i], i, 0);
		}

		this.intervalId = window.setInterval(() => {
			let audioLevels = [0];
			for (let channelI = 0; channelI < audioAnalyser.length; channelI++) {
				audioAnalyser[channelI].getByteFrequencyData(freqs[channelI]);
				let value = 0;
				for (let freqBinI = 0; freqBinI < audioAnalyser[channelI].frequencyBinCount; freqBinI++) {
					value = Math.max(value, freqs[channelI][freqBinI]);
				}
				audioLevels[channelI] = value / 256;
			}
			let averageLevel = audioLevels.reduce((sum, val) => sum + val, 0) / audioLevels.length;

			let availableHeight = this.$activityDisplay.offsetHeight;
			css(this.$levelDiv, {
				height: (availableHeight * averageLevel) + "px",
				backgroundPosition: "0 " + (-availableHeight * (1 - averageLevel) + "px"),
				backgroundSize: 100 + "px " + availableHeight + "px"
			});
		}, 100);

		mediaStream.getAudioTracks()[0].addEventListener("ended", ev => this.unbind());
	}

	/**
	 *  Returns a function, that will limit the invocation of the specified function to once in delay. Invocations that come before the end of delay are ignored!
	 */
	private throttle(func: Function, delay: number): (() => void) {
		let previousCall = 0;
		return function () {
			const time = new Date().getTime();
			if ((time - previousCall) >= delay) {
				previousCall = time;
				func.apply(this, arguments);
			}
		};
	}

	public unbind() {
		clearInterval(this.intervalId);
		this.audioContext && this.audioContext.close()
			.catch(reason => console.log(reason));
		this.mediaStreamSource && this.mediaStreamSource.disconnect();
	}

	public getMainDomElement() {
		return this.$main;
	}
}
