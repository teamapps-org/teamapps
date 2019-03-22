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
package org.teamapps.ux.component.media;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.icons.api.Icons;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.splitpane.SplitDirection;
import org.teamapps.ux.component.splitpane.SplitPane;
import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.toolbar.ToolbarButton;

import java.util.Arrays;
import java.util.List;


public class MediaPlayer extends AbstractComponent {

	private VideoPlayer videoPlayer;
	private MediaTrackGraph trackGraph;
	private SplitPane splitPane;


	public MediaPlayer() {
		videoPlayer = new VideoPlayer(null);
		videoPlayer.onVideoPlayerProgress.addListener(position -> {
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
		ToolbarButton play = ToolbarButton.createLarge(Icons.MEDIA_PLAY, "Play", null);
		ToolbarButton pause = ToolbarButton.createLarge(Icons.MEDIA_PAUSE, "Pause", null);
		ToolbarButton stop = ToolbarButton.createLarge(Icons.MEDIA_STOP, "Stop", null);
		pause.setVisible(false);

		play.onClick.addListener(toolbarButtonClickEvent -> {
			play.setVisible(false);
			pause.setVisible(true);
			videoPlayer.play();
		});

		pause.onClick.addListener(toolbarButtonClickEvent -> {
			play.setVisible(true);
			pause.setVisible(false);
			videoPlayer.pause();
		});

		stop.onClick.addListener(toolbarButtonClickEvent -> {
			play.setVisible(true);
			pause.setVisible(false);
			videoPlayer.pause();
			videoPlayer.setPosition(0);
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
	public UiComponentReference createUiComponentReference() {
		return splitPane.createUiComponentReference();
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
