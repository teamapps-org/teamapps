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
declare module "voice-activity-detection" {
	export default function vad(audioContext: AudioContext, stream: MediaStream, options: {
		fftSize?: number,
		bufferLen?: number,
		smoothingTimeConstant?: number,
		minCaptureFreq?: number,         // in Hz
		maxCaptureFreq?: number,        // in Hz
		noiseCaptureDuration?: number, // in ms
		minNoiseLevel?: number,         // from 0 to 1
		maxNoiseLevel?: number,         // from 0 to 1
		avgNoiseMultiplier?: number,
		onVoiceStart?: () => void,
		onVoiceStop?: () => void,
		onUpdate?: (val: number) => void
	}): VoiceActivityDetectionHandle;

	export type VoiceActivityDetectionHandle = {
		connect: () => void,
		disconnect: () => void,
		destroy: () => void
	}
}
