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
package org.teamapps.ux.component.webrtc;

public class TrackPublishingSuccessfulEventData {

	private final boolean audio;
	private final boolean video;

	public TrackPublishingSuccessfulEventData(boolean audio, boolean video) {
		this.audio = audio;
		this.video = video;
	}

	public boolean isAudio() {
		return audio;
	}

	public boolean isVideo() {
		return video;
	}

	@Override
	public String toString() {
		return "TrackPublishingSuccessfulEventData{" +
				"audio=" + audio +
				", video=" + video +
				'}';
	}
}
