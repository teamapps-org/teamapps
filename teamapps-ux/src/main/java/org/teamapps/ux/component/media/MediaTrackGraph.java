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
package org.teamapps.ux.component.media;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMediaTrackGraph;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

public class MediaTrackGraph extends AbstractComponent {

	public Event<TimeSelection> onTimeSelection = new Event<>();

	private MediaTrackData data;

	public MediaTrackGraph(MediaTrackData data) {
		this.data = data;
	}

	@Override
	public UiComponent createUiComponent() {
		UiMediaTrackGraph uiMediaTrackGraph = new UiMediaTrackGraph();
		mapAbstractUiComponentProperties(uiMediaTrackGraph);
		uiMediaTrackGraph.setTrackCount(data.getTrackCount());
		uiMediaTrackGraph.setTrackData(data.getTrackData());
		uiMediaTrackGraph.setMarkers(data.getMarkers());
		return uiMediaTrackGraph;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MEDIA_TRACK_GRAPH_HANDLE_TIME_SELECTION:
				UiMediaTrackGraph.HandleTimeSelectionEvent timeSelectionEvent = (UiMediaTrackGraph.HandleTimeSelectionEvent) event;
				long start = timeSelectionEvent.getStart();
				long end = timeSelectionEvent.getEnd();
				onTimeSelection.fire(new TimeSelection(start, end));
				break;
		}
	}

	public void setCursorPosition(long time) {
		queueCommandIfRendered(() -> new UiMediaTrackGraph.SetCursorPositionCommand(getId(), time));
	}

	public static class TimeSelection {
		public long start;
		public long end;

		public TimeSelection(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}

}

