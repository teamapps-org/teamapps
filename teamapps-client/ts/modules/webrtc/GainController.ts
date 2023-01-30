/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
export class GainController {

	private readonly context: AudioContext;
	private _inputTrack: MediaStreamTrack;
	private inputStream: MediaStream;
	private micStreamSource: MediaStreamAudioSourceNode;
	private readonly gainFilter: GainNode;
	private readonly destination: MediaStreamAudioDestinationNode;
	private outputStream: MediaStream;
	public readonly outputTrack: MediaStreamTrack;

	constructor(initialGain: number) {
		this.context = new ((window as any).AudioContext || (window as any).webkitAudioContext)()
		this.inputStream = new MediaStream();
		this.gainFilter = this.context.createGain();
		this.gainFilter.gain.value = initialGain;
		this.destination = this.context.createMediaStreamDestination();
		this.outputStream = this.destination.stream;
		this.outputTrack = this.outputStream.getAudioTracks()[0];

		this.gainFilter.connect(this.destination);
	}

	set inputTrack(inputTrack: MediaStreamTrack) {
		this.micStreamSource?.disconnect();
		if (this._inputTrack != null) {
			this.inputStream?.removeTrack(this._inputTrack);
		}

		this._inputTrack = inputTrack;

		if (inputTrack != null) {
			this.inputStream = new MediaStream([inputTrack]);
			this.micStreamSource = this.context.createMediaStreamSource(this.inputStream);
			this.micStreamSource.connect(this.gainFilter);
		}
	}

	get inputTrack() {
		return this._inputTrack;
	}

	get gain(): number {
		return this.gainFilter.gain.value;
	}

	set gain(value: number) {
		this.gainFilter.gain.value = value;
	}

	// close() {
	// 	// just make 100% sure everything is closed and released!
	// 	this.micStreamSource.disconnect();
	// 	this.inputStream.removeTrack(this.inputTrack);
	// 	this.context.close();
	// 	this.gainFilter.disconnect();
	// 	this.destination.disconnect();
	// 	this.outputStream.removeTrack(this.outputTrack);
	// 	this.outputTrack.dispatchEvent(new Event("ended"));
	// }
}

