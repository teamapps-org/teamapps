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
type MediaStreamAudioDestinationNode = AudioNode & { stream: MediaStream };

export class FakeMediaStreamFactory {

	public static createFakeMediaStream() {
		const canvasMediaStream = this.createVideoStream();
		const audioStream = this.createAudioStream();

		const fakeStream = new MediaStream();
		[canvasMediaStream, audioStream].forEach(stream =>
			stream.getTracks().forEach((track: MediaStreamTrack) => {
				fakeStream.addTrack(track);
			})
		);

		return fakeStream;
	}

	private static createVideoStream() {
		const canvas = document.createElement("canvas");
		(document.body || document.documentElement).appendChild(canvas);
		const canvasMediaStream = ((canvas as any).captureStream && (canvas as any).captureStream())
			|| ((canvas as any).mozCaptureStream && (canvas as any).mozCaptureStream());
		return canvasMediaStream;
	}

	private static createAudioStream() {
		const audioContext: AudioContext & { createMediaStreamDestination(): MediaStreamAudioDestinationNode } = new AudioContext() as any;
		const o = audioContext.createOscillator();
		o.type = "sine";
		o.connect(audioContext.destination);
		o.start();

		const audioDestination = audioContext.createMediaStreamDestination();
		o.connect(audioDestination);
		const audioStream = audioDestination.stream;
		return audioStream;
	}

}
