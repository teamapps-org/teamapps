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
package org.teamapps.ux.component.media;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.splitpane.SplitDirection;
import org.teamapps.ux.component.splitpane.SplitPane;
import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.toolbar.ToolbarButton;

import java.util.Arrays;
import java.util.List;


public class MediaPlayer extends AbstractComponent {

	private final VideoPlayer videoPlayer;
	private MediaTrackGraph trackGraph;
	private final SplitPane splitPane;


	public MediaPlayer() {
		videoPlayer = new VideoPlayer(null);
		videoPlayer.onProgress.addListener(position -> {
			if (trackGraph != null) {
				trackGraph.setCursorPosition(position * 1000);
			}
		});
		splitPane = new SplitPane(SplitDirection.HORIZONTAL, SplitSizePolicy.LAST_FIXED, 150);
		splitPane.setFirstChildMinSize(0);
		splitPane.setLastChildMinSize(70);
		splitPane.setCollapseEmptyChildren(true);
		splitPane.setFirstChild(videoPlayer);
		splitPane.setLastChild(trackGraph);
	}

	public List<ToolbarButton> createToolbarControls() {
		return createToolbarControls(BaseTemplate.TOOLBAR_BUTTON, MaterialIcon.PLAY_ARROW, MaterialIcon.PAUSE, MaterialIcon.STOP);
	}

	public List<ToolbarButton> createToolbarControls(Template template, Icon playIcon, Icon pauseIcon, Icon stopIcon) {
		ToolbarButton play = new ToolbarButton(template, new BaseTemplateRecord(playIcon, "Play", null));
		ToolbarButton pause = new ToolbarButton(template, new BaseTemplateRecord(pauseIcon, "Pause", null));
		ToolbarButton stop = new ToolbarButton(template, new BaseTemplateRecord(stopIcon, "Stop", null));
		pause.setVisible(false);

		play.onClick.addListener(toolbarButtonClickEvent -> {
			videoPlayer.play();
			play.setVisible(false);
			pause.setVisible(true);
		});

		pause.onClick.addListener(toolbarButtonClickEvent -> {
			videoPlayer.pause();
			play.setVisible(true);
			pause.setVisible(false);
		});

		stop.onClick.addListener(toolbarButtonClickEvent -> {
			videoPlayer.pause();
			videoPlayer.setPosition(0);
			play.setVisible(true);
			pause.setVisible(false);
		});

		return Arrays.asList(play, pause, stop);
	}

	public void setPlayerData(String url, MediaTrackData trackData) {
		videoPlayer.setUrl(url);
		trackGraph = new MediaTrackGraph(trackData);
		splitPane.setLastChild(trackGraph);
		trackGraph.onTimeSelection.addListener(timeSelection -> {
			videoPlayer.setPosition((int) timeSelection.start / 1000);
		});
	}

	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}

	public MediaTrackGraph getTrackGraph() {
		return trackGraph;
	}

	public SplitPane getSplitPane() {
		return splitPane;
	}

	@Override
	public UiComponent createUiComponent() {
		return splitPane.createUiComponent();
	}

	@Override
	public UiClientObjectReference createUiReference() {
		return splitPane.createUiReference();
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

}
