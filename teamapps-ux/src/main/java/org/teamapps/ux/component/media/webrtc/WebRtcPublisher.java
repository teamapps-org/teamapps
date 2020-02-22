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
package org.teamapps.ux.component.media.webrtc;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiWebRtcPlayer;
import org.teamapps.dto.UiWebRtcPublisher;
import org.teamapps.ux.component.AbstractComponent;

public class WebRtcPublisher extends AbstractComponent {

	private WebRtcPublishingSettings publishingSettings;
	private boolean microphoneMuted = false;
	private String backgroundImageUrl;

	public WebRtcPublisher() {
	}

	@Override
	public UiComponent createUiComponent() {
		UiWebRtcPublisher ui = new UiWebRtcPublisher();
		mapAbstractUiComponentProperties(ui);
		ui.setPublishingSettings(publishingSettings != null ? publishingSettings.createUWebRtcPublishingSettings() : null);
		ui.setMicrophoneMuted(microphoneMuted);
		ui.setBackgroundImageUrl(backgroundImageUrl);
		return ui;
	}

	public void publish(String signalingUrl, String wowzaApplicationName, String streamName, WebRtcPublishingMediaSettings inputSettings) {
		publishingSettings = new WebRtcPublishingSettings(signalingUrl, wowzaApplicationName, streamName, inputSettings);
		queueCommandIfRendered(() -> new UiWebRtcPublisher.PublishCommand(getId(), publishingSettings.createUWebRtcPublishingSettings()));
	}

	public void unPublish() {
		this.publishingSettings = null;
		queueCommandIfRendered(() -> new UiWebRtcPublisher.UnPublishCommand(getId()));
	}

	public void setMicrophoneMuted(boolean microphoneMuted) {
		this.microphoneMuted = microphoneMuted;
		queueCommandIfRendered(() -> new UiWebRtcPublisher.SetMicrophoneMutedCommand(getId(), microphoneMuted));
	}

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	public void setBackgroundImageUrl(String backgroundImageUrl) {
		this.backgroundImageUrl = backgroundImageUrl;
		queueCommandIfRendered(() -> new UiWebRtcPlayer.SetBackgroundImageUrlCommand(getId(), backgroundImageUrl));
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

}
