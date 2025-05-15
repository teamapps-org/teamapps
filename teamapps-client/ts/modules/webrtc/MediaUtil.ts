/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import vad, {VoiceActivityDetectionHandle} from "voice-activity-detection";
import {UiAudioTrackConstraintsConfig} from "../../generated/UiAudioTrackConstraintsConfig";
import {UiVideoTrackConstraintsConfig} from "../../generated/UiVideoTrackConstraintsConfig";
import {UiScreenSharingConstraintsConfig} from "../../generated/UiScreenSharingConstraintsConfig";
import {WebRtcPublishingFailureReason} from "../../generated/WebRtcPublishingFailureReason";
import {UiMediaDeviceKind} from "../../generated/UiMediaDeviceKind";
import {createUiMediaDeviceInfoConfig, UiMediaDeviceInfoConfig} from "../../generated/UiMediaDeviceInfoConfig";
import {listenStreamEnded, mixStreams} from "./utils";
import {determineVideoSize, MediaStreamWithMixiSizingInfo, MixSizingInfo} from "./MultiStreamsMixer";

export function addVoiceActivityDetectionToMediaStream(mediaStream: MediaStream, onVoiceStart: () => void, onVoiceStop: () => void) {
	if (((window as any).AudioContext || (window as any).webkitAudioContext) && mediaStream.getAudioTracks().length > 0) {
		console.log("AudioContext detected");
		let audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext)();
		console.log("AudioContext created");
		let vadHandle = vad(audioContext, mediaStream, {onVoiceStart, onVoiceStop});
		console.log("vad attached");
		listenStreamEnded(mediaStream, () => {
			vadHandle.destroy();
		});
	}
}

export function addVoiceActivityDetection(audioTrack: MediaStreamTrack, onVoiceStart: () => void, onVoiceStop: () => void): VoiceActivityDetectionHandle {
	if ((window as any).AudioContext || (window as any).webkitAudioContext) {
		let audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext)();
		let mediaStream = new MediaStream([audioTrack]);
		let vadHandle = vad(audioContext, mediaStream, {onVoiceStart, onVoiceStop});
		console.log("VAD attached");
		let destroy = () => {
			console.log("destroying VAD");
			vadHandle.destroy()
			audioContext.close();
		};
		audioTrack.addEventListener("ended", () => destroy());
		return {
			connect: () => vadHandle.connect(),
			disconnect: () => vadHandle.disconnect(),
			destroy: () => destroy()
		};
	} else {
		return {
			connect: () => {},
			disconnect: () => {},
			destroy: () => {}
		}
	}
}


export async function retrieveUserMedia(audioConstraints: UiAudioTrackConstraintsConfig, videoConstraints: UiVideoTrackConstraintsConfig, screenSharingConstraints: UiScreenSharingConstraintsConfig,
                                        streamEndedHandler: (stream: MediaStream, isDisplay: boolean) => void) {
	let micCamStream: MediaStream = null;
	try {
		if (audioConstraints != null || videoConstraints != null) {
			micCamStream = await window.navigator.mediaDevices.getUserMedia({audio: audioConstraints, video: createVideoConstraints(videoConstraints)}); // rejected if user denies!
			listenStreamEnded(micCamStream, () => streamEndedHandler(micCamStream, false));
		}
	} catch (e) {
		throw {
			exception: e,
			reason: WebRtcPublishingFailureReason.CAM_MIC_MEDIA_RETRIEVAL_FAILED
		};
	}

	let displayStream: MediaStream = null;
	try {
		if (screenSharingConstraints != null) {
			displayStream = await getDisplayStream(screenSharingConstraints); // rejected if user denies!
			listenStreamEnded(displayStream, () => streamEndedHandler(displayStream, true));
		}
	} catch (e) {
		throw {
			exception: e,
			reason: WebRtcPublishingFailureReason.DISPLAY_MEDIA_RETRIEVAL_FAILED
		};
	}

	let targetStream: MediaStream;
	if (displayStream != null && micCamStream != null) {
		try {
			let streamsWithMixSizingInfo: MediaStreamWithMixiSizingInfo[] = [];
			streamsWithMixSizingInfo.push({
				mediaStream: displayStream,
				mixSizingInfo: {fullcanvas: true}
			});

			let cameraMixSizingInfo: MixSizingInfo;
			if (micCamStream.getVideoTracks().length > 0) {
				const displayStreamDimensions = await determineVideoSize(displayStream);
				const mainStreamShortDimension = Math.min(displayStreamDimensions.width, displayStreamDimensions.height);
				const cameraAspectRatio = micCamStream.getTracks().filter(t => t.kind === "video")[0].getSettings().aspectRatio || 4 / 3;
				const pictureInPictureHeight = Math.round((25 / 100) * mainStreamShortDimension);
				const pictureInPictureWidth = Math.round(pictureInPictureHeight * cameraAspectRatio);
				cameraMixSizingInfo = {
					width: pictureInPictureWidth,
					height: pictureInPictureHeight,
					right: 0,
					top: 0
				};
			} else {
				cameraMixSizingInfo = {}; // no camera track. audio only. no mix sizing info needed
			}
			streamsWithMixSizingInfo.push({mediaStream: micCamStream, mixSizingInfo: cameraMixSizingInfo});
			targetStream = await mixStreams(streamsWithMixSizingInfo, createDisplayMediaStreamConstraints(screenSharingConstraints), 10);
		} catch (e) {
			throw {
				exception: e,
				reason: WebRtcPublishingFailureReason.VIDEO_MIXING_FAILED
			};
		}
	} else {
		targetStream = micCamStream || displayStream;
	}

	let sourceStreams: MediaStream[] = [];
	if (micCamStream != null) {
		sourceStreams.push(micCamStream);
	}
	if (displayStream != null) {
		sourceStreams.push(displayStream);
	}
	return {sourceStreams, targetStream};
}

export function createVideoConstraints(videoConstraints: UiVideoTrackConstraintsConfig): MediaTrackConstraints {
	return videoConstraints && {
		...videoConstraints,
		facingMode: null // TODO UiVideoFacingMode[videoConstraints.facingMode].toLocaleLowerCase() ==> make nullable!!!!
	};
}

export function createDisplayMediaStreamConstraints(screenSharingConstraints: UiScreenSharingConstraintsConfig) {
	return {
		video: screenSharingConstraints && {
			frameRate: {max: 5, ideal: 5},
			width: {max: screenSharingConstraints.maxWidth, ideal: screenSharingConstraints.maxWidth},
			height: {max: screenSharingConstraints.maxHeight, ideal: screenSharingConstraints.maxHeight}
		},
		audio: false, // TODO this might be interesting for sharing the actual computer audio...
	};
}

export async function getDisplayStream(screenSharingConstraints: UiScreenSharingConstraintsConfig) {
	if (canPublishScreen()) {
		return await (window.navigator.mediaDevices as any).getDisplayMedia(createDisplayMediaStreamConstraints(screenSharingConstraints)) as MediaStream;
	} else {
		throw new Error("Cannot share screen! Browser does not provide the corresponding API!");
	}
}

export function canPublishScreen() {
	return (window.navigator.mediaDevices as any).getDisplayMedia != null;
}

export async function enumerateDevices(): Promise<UiMediaDeviceInfoConfig[]> {
	const uiMediaDeviceKindByKindString = {
		'audioinput': UiMediaDeviceKind.AUDIO_INPUT,
		'videoinput': UiMediaDeviceKind.VIDEO_INPUT,
		'audiooutput': UiMediaDeviceKind.AUDIO_OUTPUT
	};
	try {
		let stream = await window.navigator.mediaDevices.getUserMedia({audio: true, video: true});
		stream.getTracks().forEach(t => t.stop()); // close the stream directly!
	} catch (e) {
		console.error(e);
	} finally {
		try {
			let devices = await navigator.mediaDevices.enumerateDevices();
			return devices.map((deviceInfo, i) => createUiMediaDeviceInfoConfig({
				deviceId: deviceInfo.deviceId,
				groupId: deviceInfo.groupId,
				kind: uiMediaDeviceKindByKindString[deviceInfo.kind],
				label: deviceInfo.label
			} as UiMediaDeviceInfoConfig))
				.filter(uiDeviceInfo => uiDeviceInfo.kind != null);
		} catch (e) {
			return [];
		}
	}
}
