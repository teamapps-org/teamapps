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
type MediaStreamAudioDestinationNode = AudioNode & { stream: MediaStream };
export type MediaStreamWithMixiSizingInfo = MediaStream & { fullcanvas?: boolean, width?: number, height?: number, left?: number, top?: number };
type EnhancedVideoElement = HTMLVideoElement & { mixSizingInfo?: any };

export class MultiStreamsMixer {

	private inputMediaStreams: MediaStreamWithMixiSizingInfo[];
	private outputMediaStream: MediaStream;

	private videos: EnhancedVideoElement[] = [];
	private canvas: HTMLCanvasElement & { stream?: MediaStream };
	private context: CanvasRenderingContext2D;

	public frameInterval = 10;
	private isStopDrawingFrames = false;

	// use gain node to prevent echo
	public useGainNode = true;
	private audioContext: AudioContext & { createMediaStreamDestination(): MediaStreamAudioDestinationNode };
	private audioDestination: MediaStreamAudioDestinationNode;

	constructor(inputMediaStreams: MediaStreamWithMixiSizingInfo[]) {
		this.inputMediaStreams = inputMediaStreams;
		this.canvas = document.createElement('canvas');
		this.context = this.canvas.getContext('2d');
		this.canvas.classList.add('pseudo-hidden');
		(document.body || document.documentElement).appendChild(this.canvas);
	}

	private static setSrcObject(stream: MediaStream, element: HTMLMediaElement) {
		if ('srcObject' in element) {
			element.srcObject = stream;
		} else if ('mozSrcObject' in element) {
			(element as any).mozSrcObject = stream;
		} else if ('createObjectURL' in URL) {
			(element as any).src = URL.createObjectURL(stream);
		} else {
			alert('createObjectURL/srcObject both are not supported.');
		}
	}

	private drawVideosToCanvas() {
		if (this.isStopDrawingFrames) {
			return;
		}

		let fullcanvasVideo: EnhancedVideoElement = null;
		const remaining: EnhancedVideoElement[] = [];
		this.videos.forEach((video: EnhancedVideoElement) => {
			if (!video.mixSizingInfo) {
				video.mixSizingInfo = {};
			}
			if (video.mixSizingInfo.fullcanvas) {
				fullcanvasVideo = video;
			} else {
				remaining.push(video);
			}
		});

		if (fullcanvasVideo) {
			this.canvas.width = fullcanvasVideo.mixSizingInfo.width;
			this.canvas.height = fullcanvasVideo.mixSizingInfo.height;
		} else if (remaining.length) {
			const videosLength = this.videos.length;
			this.canvas.width = videosLength > 1 ? remaining[0].width * 2 : remaining[0].width;

			let height = 1;
			if (videosLength === 3 || videosLength === 4) {
				height = 2;
			}
			if (videosLength === 5 || videosLength === 6) {
				height = 3;
			}
			if (videosLength === 7 || videosLength === 8) {
				height = 4;
			}
			if (videosLength === 9 || videosLength === 10) {
				height = 5;
			}
			this.canvas.height = remaining[0].height * height;
		} else {
			this.canvas.width = 360;
			this.canvas.height = 240;
		}

		if (fullcanvasVideo && fullcanvasVideo instanceof HTMLVideoElement) {
			this.drawImage(fullcanvasVideo);
		}

		remaining.forEach((video, idx) => {
			this.drawImage(video, idx);
		});

		setTimeout(() => this.drawVideosToCanvas(), this.frameInterval);
	}

	private drawImage(video: EnhancedVideoElement, idx = 0) {
		if (this.isStopDrawingFrames) {
			return;
		}

		let x = 0;
		let y = 0;
		let width = video.width;
		let height = video.height;

		if (idx === 1) {
			x = video.width;
		}

		if (idx === 2) {
			y = video.height;
		}

		if (idx === 3) {
			x = video.width;
			y = video.height;
		}

		if (idx === 4) {
			y = video.height * 2;
		}

		if (idx === 5) {
			x = video.width;
			y = video.height * 2;
		}

		if (idx === 6) {
			y = video.height * 3;
		}

		if (idx === 7) {
			x = video.width;
			y = video.height * 3;
		}

		if (typeof video.mixSizingInfo.left !== 'undefined') {
			x = video.mixSizingInfo.left;
		}

		if (typeof video.mixSizingInfo.top !== 'undefined') {
			y = video.mixSizingInfo.top;
		}

		if (typeof video.mixSizingInfo.width !== 'undefined') {
			width = video.mixSizingInfo.width;
		}

		if (typeof video.mixSizingInfo.height !== 'undefined') {
			height = video.mixSizingInfo.height;
		}

		this.context.drawImage(video, x, y, width, height);

		if (typeof video.mixSizingInfo.onRender === 'function') {
			video.mixSizingInfo.onRender(this.context, x, y, width, height, idx);
		}
	}

	public getMixedStream() {
		if (this.outputMediaStream == null) {
			if (this.inputMediaStreams.length > 1) {
				const mixedVideoStream = this.getMixedVideoStream();
				const mixedAudioStream = this.getMixedAudioStream();
				if (mixedAudioStream) {
					mixedAudioStream.getTracks().filter((t) => {
						return t.kind === 'audio';
					}).forEach((track) => {
						mixedVideoStream.addTrack(track);
					});
				}

				let fullcanvas;
				this.inputMediaStreams.forEach((stream) => {
					if (stream.fullcanvas) {
						fullcanvas = true;
					}
				});

				this.drawVideosToCanvas();

				this.outputMediaStream = mixedVideoStream;
			} else {
				this.outputMediaStream = this.inputMediaStreams[0];
			}
		}
		return this.outputMediaStream;
	}

	public getInputMediaStreams() {
		return this.inputMediaStreams;
	}

	private getMixedVideoStream() {
		this.resetVideoStreams();

		const videoStream = new MediaStream();

		const capturedStream = ((this.canvas as any).captureStream && (this.canvas as any).captureStream())
			|| ((this.canvas as any).mozCaptureStream && (this.canvas as any).mozCaptureStream());
		capturedStream.getTracks().filter((t: MediaStreamTrack) => {
			return t.kind === 'video';
		}).forEach((track: MediaStreamTrack) => {
			videoStream.addTrack(track);
		});

		this.canvas.stream = videoStream;
		return videoStream;
	}

	private getMixedAudioStream(): MediaStream {
		this.audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext || (window as any).mozAudioContext)();

		let gainNode: GainNode;
		if (this.useGainNode === true) {
			gainNode = this.audioContext.createGain();
			gainNode.connect(this.audioContext.destination);
			gainNode.gain.value = 0; // don't hear self
		}

		const audioSources: MediaStreamAudioSourceNode[] = [];
		let audioTracksLength = 0;
		this.inputMediaStreams.forEach((stream) => {
			if (!stream.getTracks().filter((t) => {
				return t.kind === 'audio';
			}).length) {
				return;
			}

			audioTracksLength++;

			const audioSource: MediaStreamAudioSourceNode = this.audioContext.createMediaStreamSource(stream);

			if (this.useGainNode === true) {
				audioSource.connect(gainNode);
			}

			audioSources.push(audioSource);
		});

		if (!audioTracksLength) {
			return;
		}

		this.audioDestination = this.audioContext.createMediaStreamDestination();
		audioSources.forEach((audioSource) => {
			audioSource.connect(this.audioDestination);
		});
		return this.audioDestination.stream;
	}

	private createVideo(stream: MediaStreamWithMixiSizingInfo): EnhancedVideoElement {
		const video = document.createElement('video');

		MultiStreamsMixer.setSrcObject(stream, video);

		video.muted = true;
		video.volume = 0;

		video.width = stream.width;
		video.height = stream.height;

		video.play();

		return video;
	}

	public close() {
		this.inputMediaStreams.forEach(ms => ms.getTracks().forEach(t => t.stop()));
		this.canvas.remove();
	};


	public resetVideoStreams(streams: MediaStream[] = this.inputMediaStreams) {
		this.videos = [];
		if (streams && !Array.isArray(streams)) {
			streams = [streams];
		}
		streams = streams || this.inputMediaStreams;

		// via: @adrian-ber
		streams.forEach((stream) => {
			if (!stream.getTracks().filter((t) => {
				return t.kind === 'video';
			}).length) {
				return;
			}

			const video = this.createVideo(stream);
			video.mixSizingInfo = stream;
			this.videos.push(video);
		});
	}

}
