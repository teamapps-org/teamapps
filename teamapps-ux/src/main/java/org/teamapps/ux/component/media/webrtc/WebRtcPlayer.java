/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiWebRtcPlayer;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.session.CurrentSessionContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WebRtcPlayer extends AbstractComponent {

	private WebRtcPlayingSettings playingSettings;
	private String backgroundImageUrl;

	public WebRtcPlayer() {
	}

	@Override
	public UiComponent createUiComponent() {
		UiWebRtcPlayer ui = new UiWebRtcPlayer();
		mapAbstractUiComponentProperties(ui);
		ui.setPlayingSettings(playingSettings != null ? playingSettings.createUiWebRtcPlayingSettings() : null);
		ui.setBackgroundImageUrl(backgroundImageUrl);
		return ui;
	}

	public static void getPlayableVideoCodecs(Consumer<List<VideoCodec>> resultCallback) {
		CurrentSessionContext.get().queueCommand((UiCommand) new UiWebRtcPlayer.GetPlayableVideoCodecsCommand(), uiVideoCodecs -> {  // TODO #command-results fix this when the generators are fixed (wrap results with type description)!
			List<Integer> uiVideoCodecsAsIntegerList = (List<Integer>) uiVideoCodecs; // TODO #command-results fix this when the generators are fixed (wrap results with type description)!
			List<VideoCodec> codecs = uiVideoCodecsAsIntegerList.stream()
					.map(uiCodec -> VideoCodec.values()[uiCodec])
					.collect(Collectors.toList());
			resultCallback.accept(codecs);
		});
	}

	public void play(String signalingUrl, String wowzaApplicationName, String streamName) {
		this.playingSettings = new WebRtcPlayingSettings(signalingUrl, wowzaApplicationName, streamName);
		queueCommandIfRendered(() -> new UiWebRtcPlayer.PlayCommand(getId(), playingSettings.createUiWebRtcPlayingSettings()));
	}

	public void stop() {
		playingSettings = null;
		queueCommandIfRendered(() -> new UiWebRtcPlayer.StopPlayingCommand(getId()));
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

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
