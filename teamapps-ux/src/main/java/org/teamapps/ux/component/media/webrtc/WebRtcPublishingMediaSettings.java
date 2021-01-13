/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.component.media.webrtc;

import org.teamapps.dto.UiWebRtcPublishingMediaSettings;

public class WebRtcPublishingMediaSettings {

	private boolean audio = true;
	private AudioCodec audioCodec = AudioCodec.OPUS;
	private int audioKiloBitsPerSecond = 64;

	private boolean video = true;
	private VideoCodec videoCodec = VideoCodec.H264;
	private int videoWidth = 720;
	private int videoHeight = 480;
	private int videoFps = 25;
	private int videoKiloBitsPerSecond = 400;

	private boolean screen = false;

	public UiWebRtcPublishingMediaSettings createUiWebRtcPublishingMediaSettings() {
		UiWebRtcPublishingMediaSettings uiMediaInputSettings = new UiWebRtcPublishingMediaSettings();
		uiMediaInputSettings.setAudio(audio);
		uiMediaInputSettings.setAudioCodec(audioCodec.toUiAudioCodec());
		uiMediaInputSettings.setAudioKiloBitsPerSecond(audioKiloBitsPerSecond);
		uiMediaInputSettings.setVideo(video);
		uiMediaInputSettings.setVideoCodec(videoCodec.toUiVideoCodec());
		uiMediaInputSettings.setVideoWidth(videoWidth);
		uiMediaInputSettings.setVideoHeight(videoHeight);
		uiMediaInputSettings.setVideoFps(videoFps);
		uiMediaInputSettings.setVideoKiloBitsPerSecond(videoKiloBitsPerSecond);
		uiMediaInputSettings.setScreen(screen);
		return uiMediaInputSettings;
	}

	public boolean isAudio() {
		return audio;
	}

	public void setAudio(boolean audio) {
		this.audio = audio;
	}

	public AudioCodec getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(AudioCodec audioCodec) {
		this.audioCodec = audioCodec;
	}

	public int getAudioKiloBitsPerSecond() {
		return audioKiloBitsPerSecond;
	}

	public void setAudioKiloBitsPerSecond(int audioKiloBitsPerSecond) {
		this.audioKiloBitsPerSecond = audioKiloBitsPerSecond;
	}

	public boolean isVideo() {
		return video;
	}

	public void setVideo(boolean video) {
		this.video = video;
	}

	public VideoCodec getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(VideoCodec videoCodec) {
		this.videoCodec = videoCodec;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

	public int getVideoFps() {
		return videoFps;
	}

	public void setVideoFps(int videoFps) {
		this.videoFps = videoFps;
	}

	public int getVideoKiloBitsPerSecond() {
		return videoKiloBitsPerSecond;
	}

	public void setVideoKiloBitsPerSecond(int videoKiloBitsPerSecond) {
		this.videoKiloBitsPerSecond = videoKiloBitsPerSecond;
	}

	public boolean isScreen() {
		return screen;
	}

	public void setScreen(boolean screen) {
		this.screen = screen;
	}
}
