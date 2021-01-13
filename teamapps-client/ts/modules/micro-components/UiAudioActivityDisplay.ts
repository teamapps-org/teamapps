/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
		this.$main = parseHtml(`
<div class="UiAudioActivityDisplay">
	<div class="level-div hidden"></div>
</div>`);
		this.$activityDisplay = this.$main;
		this.$levelDiv = this.$main.querySelector<HTMLElement>(':scope .level-div');

	}

	private analyserNode: AnalyserNode;

	public bindToStream(userMediaStream: MediaStream) {
		(window as any).AudioContext = (window as any).AudioContext || (window as any).webkitAudioContext;
		this.audioContext = new AudioContext();

		let scriptProcessor = this.audioContext.createScriptProcessor(2048, 1, 1);
		scriptProcessor.connect(this.audioContext.destination);

		this.analyserNode = this.audioContext.createAnalyser();
		this.analyserNode.smoothingTimeConstant = 0.3;
		this.analyserNode.fftSize = 32; // 16 spectral lines - that's the minimum. we don't want to get a spectral analysis anyway...
		this.analyserNode.connect(scriptProcessor);

		this.mediaStreamSource = this.audioContext.createMediaStreamSource(userMediaStream);
		this.mediaStreamSource.connect(this.analyserNode, 0, 0);

		scriptProcessor.onaudioprocess = this.throttle(() => {
			const array = new Uint8Array(this.analyserNode.frequencyBinCount);
			this.analyserNode.getByteFrequencyData(array);
			const average = UiAudioActivityDisplay.getAverageVolume(array);
			let averageRatio = average / 255;
			let videoHeight = this.$activityDisplay.offsetHeight;
			css(this.$levelDiv, {
				height: (videoHeight * averageRatio) + "px",
				backgroundPosition: "0 " + (-videoHeight * (1 - averageRatio) + "px"),
				backgroundSize: 100 + "px " + videoHeight + "px"
			});
		}, 200);

		this.$levelDiv.classList.remove("hidden");
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
		this.audioContext && this.audioContext.close()
			.catch(reason => console.log(reason));
		this.mediaStreamSource && this.mediaStreamSource.disconnect();
		this.$levelDiv.classList.add("hidden");
	}

	private static getAverageVolume(sampleVolumes: Uint8Array) {
		let max = 0;
		let sum = 0;
		for (let i = 0; i < sampleVolumes.length; i++) {
			max = Math.max(max, sampleVolumes[i]);
			sum += sampleVolumes[i];
		}
		return (max * 3 + (sum / sampleVolumes.length)) / 4;
	}

	public getMainDomElement() {
		return this.$main;
	}
}
