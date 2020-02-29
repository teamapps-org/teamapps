import vad from "voice-activity-detection";
import {Conference} from "./mediasoup-v2/lib/conference";
import {UiAudioTrackConstraintsConfig} from "../../generated/UiAudioTrackConstraintsConfig";
import {UiVideoTrackConstraintsConfig} from "../../generated/UiVideoTrackConstraintsConfig";
import {UiScreenSharingConstraintsConfig} from "../../generated/UiScreenSharingConstraintsConfig";
import {WebRtcPublishingFailureReason} from "../../generated/WebRtcPublishingFailureReason";
import {MediaStreamWithMixiSizingInfo, MixSizingInfo} from "./mediasoup-v2/lib/MultiStreamsMixer";
import {UiMediaDeviceKind} from "../../generated/UiMediaDeviceKind";
import {createUiMediaDeviceInfoConfig, UiMediaDeviceInfoConfig} from "../../generated/UiMediaDeviceInfoConfig";
import {MediaDevicesExtended} from "./mediasoup-v2/lib/interfaces";
import {UiMediaSoupV2WebRtcClient} from "./mediasoup-v2/UiMediaSoupV2WebRtcClient";

export function addVoiceActivityDetection(mediaStream: MediaStream, onVoiceStart: () => void, onVoiceStop: () => void) {
	if (((window as any).AudioContext || (window as any).webkitAudioContext) && mediaStream.getAudioTracks().length > 0) {
		console.log("AudioContext detected");
		let audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext)();
		console.log("AudioContext created");
		let vadHandle = vad(audioContext, mediaStream, {onVoiceStart, onVoiceStop});
		console.log("vad attached");
		Conference.listenStreamEnded(mediaStream, () => {
			vadHandle.destroy();
		});
	}
}


export async function retrieveUserMedia(audioConstraints: UiAudioTrackConstraintsConfig, videoConstraints: UiVideoTrackConstraintsConfig, screenSharingConstraints: UiScreenSharingConstraintsConfig,
                                        streamEndedHandler: (stream: MediaStream, isDisplay: boolean) => void) {
	let micCamStream: MediaStream = null;
	try {
		if (audioConstraints != null || videoConstraints != null) {
			micCamStream = await window.navigator.mediaDevices.getUserMedia({audio: audioConstraints, video: createVideoConstraints(videoConstraints)}); // rejected if user denies!
			Conference.listenStreamEnded(micCamStream, () => streamEndedHandler(micCamStream, false));
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
			Conference.listenStreamEnded(displayStream, () => streamEndedHandler(displayStream, true));
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
				const displayStreamDimensions = await Conference.determineVideoSize(displayStream);
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
			targetStream = await Conference.mixStreams(streamsWithMixSizingInfo, createDisplayMediaStreamConstraints(screenSharingConstraints), 10);
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
		return await (window.navigator.mediaDevices as MediaDevicesExtended).getDisplayMedia(createDisplayMediaStreamConstraints(screenSharingConstraints));
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