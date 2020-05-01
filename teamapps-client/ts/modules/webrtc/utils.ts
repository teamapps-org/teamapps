import {MediaStreamWithMixiSizingInfo, MultiStreamsMixer} from './MultiStreamsMixer';

interface MediaDevicesExtended extends MediaDevices {
	getDisplayMedia: (constraints: MediaStreamConstraints) => Promise<MediaStream>
}

export async function getUserMedia(constraints: MediaStreamConstraints, isDisplay: boolean = false): Promise<MediaStream> {
	if (isDisplay) {
		return await (navigator.mediaDevices as MediaDevicesExtended).getDisplayMedia(constraints);
	} else {
		return await navigator.mediaDevices.getUserMedia(constraints);
	}
}

export async function mixStreams(inputMediaStreams: MediaStreamWithMixiSizingInfo[], constraints?: MediaStreamConstraints, frameRate: number = 10): Promise<MediaStream | undefined> {
	let active = true;
	const mixer = new MultiStreamsMixer(inputMediaStreams, frameRate);
	const mixerStream: MediaStream = await mixer.getMixedStream();
	const tracks: MediaStreamTrack[] = mixerStream.getTracks();
	for (let track of tracks) {
		if (constraints && (track.kind === 'audio' || track.kind === 'video')) {
			const constraint: boolean | MediaTrackConstraints | undefined = constraints[track.kind];
			if (constraint && constraint !== true) {
				try {
					await track.applyConstraints(constraint)
				} catch (e) {
				}
			}
		}
	}
	listenStreamEnded(mixerStream, () => {
		if (active) {
			console.log('closing mixer');
			active = false;
			mixer.close();
		}
	});
	for (const inputData of inputMediaStreams) {
		listenStreamEnded(inputData.mediaStream, () => {
			if (active) {
				console.log('closing mixer');
				active = false;
				mixer.close();
			}
		});
	}
	return mixerStream;
}

export function testStreamActive(stream: MediaStream): boolean {
	return stream.active && stream.getTracks().filter(t => t.readyState !== 'ended').length > 0;
}

export function listenStreamEnded(stream: MediaStream, listener: () => void): void {
	let active = true;
	const tracks: MediaStreamTrack[] = stream.getTracks();

	for (let track of tracks) {
		track.addEventListener("ended", () => {
			if (active && !testStreamActive(stream)) {
				active = false;
				listener();
			}
		});
	}
}
