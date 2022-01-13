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
