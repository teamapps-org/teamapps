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


import {AbstractComponent, parseHtml, ServerObjectChannel} from "projector-client-object-api";
import {DtoAudioLevelIndicator, DtoAudioLevelIndicatorCommandHandler} from "./generated";

export class AudioLevelIndicator extends AbstractComponent<DtoAudioLevelIndicator> implements DtoAudioLevelIndicatorCommandHandler {
	private $main: HTMLElement;
	private $activityDisplay: HTMLElement;
	private $canvas: HTMLCanvasElement;
	private mediaStreamSource: MediaStreamAudioSourceNode;
	private audioContext: AudioContext;
	private mediaStreamToBeClosedWhenUnbinding: MediaStream;
	private canvasContext: CanvasRenderingContext2D;

	private maxLevel = 0;
	private maxLevel2 = 0;
	private lastDrawingTimestamp = 0;

	constructor(config: DtoAudioLevelIndicator, serverObjectChannel: ServerObjectChannel) {
		super(config)

		this.$main = parseHtml(`
<div class="AudioLevelIndicator">
	<canvas></canvas>
</div>`);
		this.$activityDisplay = this.$main;
		this.$canvas = this.$main.querySelector<HTMLElement>(':scope canvas') as HTMLCanvasElement;
		this.canvasContext = this.$canvas.getContext("2d");

		this.setDeviceId(config.deviceId);
	}

	private analyserNode: AnalyserNode;

	public bindToStream(mediaStream: MediaStream, mediaStreamIsExclusiveToThisComponent: boolean) {
		this.unbind();

		if (mediaStreamIsExclusiveToThisComponent) {
			this.mediaStreamToBeClosedWhenUnbinding = mediaStream;
		}

		(window as any).AudioContext = (window as any).AudioContext || (window as any).webkitAudioContext;
		this.audioContext = new AudioContext();

		let scriptProcessor = this.audioContext.createScriptProcessor(2048, 1, 1);
		scriptProcessor.connect(this.audioContext.destination);

		this.analyserNode = this.audioContext.createAnalyser();
		this.analyserNode.smoothingTimeConstant = 0.3;
		this.analyserNode.fftSize = 32; // 16 spectral lines - that's the minimum. we don't want to get a spectral analysis anyway...
		this.analyserNode.connect(scriptProcessor);

		this.mediaStreamSource = this.audioContext.createMediaStreamSource(mediaStream);
		this.mediaStreamSource.connect(this.analyserNode, 0, 0);

		scriptProcessor.onaudioprocess = event => {
			var buf = event.inputBuffer.getChannelData(0);
			var bufLength = buf.length;
			for (var i = 0; i < bufLength; i++) {
				let x = buf[i];
				if (x > this.maxLevel) {
					this.maxLevel2 = this.maxLevel;
					this.maxLevel = x;
				}
			}

			if (Date.now() - this.lastDrawingTimestamp > 100) {
				requestAnimationFrame(() => {
					let displayedLevel = this.maxLevel2 == 0 ? 0 : Math.max(-10, Math.log2(this.maxLevel)) / 10 + 1;
					this.draw(displayedLevel);
					this.maxLevel = 0;
					this.maxLevel2 = 0;
				});
			}
		};
	}

	private draw(level: number) {
		let canvasWidth = this.canvasContext.canvas.width;
		let canvasHeight = this.canvasContext.canvas.height;

		var imageData = this.canvasContext.getImageData(this.config.barWidth, 0, canvasWidth - this.config.barWidth, canvasHeight);
		this.canvasContext.putImageData(imageData, 0, 0);
		this.canvasContext.clearRect(canvasWidth - this.config.barWidth, 0, this.config.barWidth, canvasHeight);

		var grd = this.canvasContext.createLinearGradient(0, canvasHeight, 0, 0);
		grd.addColorStop(0, "#192e83")
		grd.addColorStop(.4, "#0fb83f")
		grd.addColorStop(.6, "#0fb83f")
		grd.addColorStop(.8, "orange")
		grd.addColorStop(.95, "#e00")
		this.canvasContext.fillStyle = grd;

		// draw a bar based on the current volume
		let volumeBarSize = canvasHeight * level;
		this.canvasContext.fillRect(canvasWidth - this.config.barWidth, canvasHeight - volumeBarSize, this.config.barWidth, volumeBarSize);

		this.lastDrawingTimestamp = Date.now();
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
		this.audioContext && this.audioContext?.close()
			.catch(reason => console.log(reason));
		this.mediaStreamSource?.disconnect();
		if (this.mediaStreamToBeClosedWhenUnbinding != null) {
			this.mediaStreamToBeClosedWhenUnbinding.getTracks().forEach(t => t.stop());
		}
		this.canvasContext.clearRect(0, 0, this.canvasContext.canvas.width, this.canvasContext.canvas.height);
	}

	onResize() {
		this.$canvas.width = this.getWidth();
		this.$canvas.height = this.getHeight();
	}

	public doGetMainElement() {
		return this.$main;
	}

	public async setDeviceId(deviceId: string) {
		if (deviceId == null) {
			this.unbind();
			return;
		} else {
			let mediaStream = await navigator.mediaDevices.getUserMedia({
				audio: {
					deviceId
				}
			});
			this.bindToStream(mediaStream, true);
		}
	}


	setBarWidth(barWidth: number) {
		this.config.barWidth = barWidth;
	}
}


