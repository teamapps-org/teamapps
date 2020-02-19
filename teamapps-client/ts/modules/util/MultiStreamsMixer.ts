/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {parseHtml} from "../Common";

type MediaStreamAudioDestinationNode = AudioNode & { stream: MediaStream };
export type MixSizingInfo = {
	fullcanvas?: boolean,
	width?: number,
	height?: number,
	left?: number,
	top?: number,
	onRender?: (context: CanvasRenderingContext2D, video: HTMLVideoElement, mixSizingInfo: MixSizingInfo, x: number, y: number, width: number, height: number) => void
};
export type MediaStreamWithMixiSizingInfo = {
	mediaStream: MediaStream,
	mixSizingInfo: MixSizingInfo
};
export type MediaStreamWithMixiSizingInfoAndVideo = MediaStreamWithMixiSizingInfo & { video?: HTMLVideoElement };

export class MultiStreamsMixer {

	private inputMediaStreams: MediaStreamWithMixiSizingInfoAndVideo[];
	private outputMediaStream: MediaStream;

	private canvas: HTMLCanvasElement & { stream?: MediaStream };
	private context: CanvasRenderingContext2D;

	private frameInterval: number;
	private isStopDrawingFrames = false;

	private audioContext: AudioContext & { createMediaStreamDestination(): MediaStreamAudioDestinationNode };
	private audioDestination: MediaStreamAudioDestinationNode;

	constructor(inputMediaStreams: MediaStreamWithMixiSizingInfo[], frameRate: number = 10) {
		this.frameInterval = 1000 / frameRate;
		this.inputMediaStreams = inputMediaStreams;
		this.canvas = document.createElement('canvas');
		this.context = this.canvas.getContext('2d');
		this.canvas.classList.add('pseudo-hidden');
		(document.body || document.documentElement).appendChild(this.canvas);
	}

	public async getMixedStream() {
		if (this.outputMediaStream == null) {
			if (this.inputMediaStreams.length > 1) {
				const mixedVideoStream = await this.getMixedVideoStream();
				const mixedAudioStream = this.getMixedAudioStream();
				if (mixedAudioStream) {
					mixedAudioStream.getTracks().filter((t) => {
						return t.kind === 'audio';
					}).forEach((track) => {
						mixedVideoStream.addTrack(track);
					});
				}
				this.drawVideosToCanvas();

				this.outputMediaStream = mixedVideoStream;
			} else {
				this.outputMediaStream = this.inputMediaStreams[0].mediaStream;
			}
		}
		return this.outputMediaStream;
	}

	public getInputMediaStreams() {
		return this.inputMediaStreams;
	}

	public close() {
		this.inputMediaStreams.forEach(ms => ms.mediaStream.getTracks().forEach(t => t.stop()));
		this.canvas.remove();
	};

	private drawVideosToCanvas() {
		if (this.isStopDrawingFrames) {
			return;
		}

		let fullcanvasVideo = this.inputMediaStreams.filter(ims => ims.mixSizingInfo.fullcanvas && ims.mediaStream.getVideoTracks().length > 0)[0];
		let remainingVideos = this.inputMediaStreams.filter(ims => ims !== fullcanvasVideo && ims.mediaStream.getVideoTracks().length > 0);

		if (fullcanvasVideo) {
			this.canvas.width = fullcanvasVideo.mixSizingInfo.width;
			this.canvas.height = fullcanvasVideo.mixSizingInfo.height;
		} else if (remainingVideos.length) {
			const videosLength = this.inputMediaStreams.filter(ims => ims.video != null).length;
			this.canvas.width = videosLength > 1 ? remainingVideos[0].mixSizingInfo.width * 2 : remainingVideos[0].mixSizingInfo.width;

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
			this.canvas.height = remainingVideos[0].mixSizingInfo.height * height;
		} else {
			this.canvas.width = 360;
			this.canvas.height = 240;
		}

		if (fullcanvasVideo && fullcanvasVideo.video instanceof HTMLVideoElement) {
			this.drawImage(fullcanvasVideo.video, fullcanvasVideo.mixSizingInfo);
		}

		remainingVideos.forEach((video) => {
			this.drawImage(video.video, video.mixSizingInfo);
		});

		setTimeout(() => this.drawVideosToCanvas(), this.frameInterval);
	}

	private drawImage(video: HTMLVideoElement, mixSizingInfo: MixSizingInfo) {
		if (this.isStopDrawingFrames) {
			return;
		}

		let x = 0;
		let y = 0;
		let width = video.width;
		let height = video.height;


		if (mixSizingInfo.left != null) {
			x = mixSizingInfo.left;
		}

		if (mixSizingInfo.top != null) {
			y = mixSizingInfo.top;
		}

		if (mixSizingInfo.width != null) {
			width = mixSizingInfo.width;
		}

		if (mixSizingInfo.height != null) {
			height = mixSizingInfo.height;
		}

		this.context.drawImage(video, x, y, width, height);

		if (typeof mixSizingInfo.onRender === 'function') {
			mixSizingInfo.onRender(this.context, video, mixSizingInfo, x, y, width, height);
		}
	}

	private async getMixedVideoStream() {
		await this.resetVideoStreams();

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

		const audioSources: MediaStreamAudioSourceNode[] = [];
		let audioTracksLength = 0;
		this.inputMediaStreams.forEach((stream) => {
			if (!stream.mediaStream.getTracks().filter((t) => {
				return t.kind === 'audio';
			}).length) {
				return;
			}

			audioTracksLength++;

			const audioSource: MediaStreamAudioSourceNode = this.audioContext.createMediaStreamSource(stream.mediaStream);

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

	private static async createVideo(stream: MediaStreamWithMixiSizingInfoAndVideo): Promise<HTMLVideoElement> {
		const video = document.createElement('video');
		video.srcObject = stream.mediaStream;
		video.muted = true;
		video.volume = 0;
		video.play();

		if (stream.mixSizingInfo.width != null && stream.mixSizingInfo.height != null) {
			video.width = stream.mixSizingInfo.width;
			video.height = stream.mixSizingInfo.height;
		} else {
			let size = await determineVideoSize(stream.mediaStream);
			video.width = size.width;
			video.height = size.height;
		}

		return video;
	}

	private async resetVideoStreams() {
		for (let stream of this.inputMediaStreams) {
			stream.video = null;
			if (stream.mediaStream.getVideoTracks().length == 0) {
				continue;
			}

			stream.video = await MultiStreamsMixer.createVideo(stream);
		}
	}

}

export async function determineVideoSize(mediaStream: MediaStream, timeout = 1000): Promise<{ width: number, height: number }> {
	if (mediaStream.getVideoTracks().length == 0) {
		return {width: 0, height: 0};
	}
	return new Promise((resolve, reject) => {
		var video: HTMLVideoElement = parseHtml('<video class="pseudo-hidden"></video>');
		document.body.appendChild(video);
		video.srcObject = mediaStream;
		video.muted = true;
		video.onloadedmetadata = (e) => {
			resolve({width: video.videoWidth, height: video.videoHeight});
			video.remove();
		};
		setTimeout(() => {
			reject();
			video.remove();
		}, timeout);
	});
}
