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

import org.teamapps.dto.UiMediaTrackMarker;
import org.teamapps.common.format.Color;

public class TrackMarkerData {

	private int track;
	private long start;
	private long end;
	private Color color;
	private Color backgroundColor;

	public TrackMarkerData(int track, long start, long end, Color color, Color backgroundColor) {
		this.track = track;
		this.start = start;
		this.end = end;
		this.color = color;
		this.backgroundColor = backgroundColor;
	}

	public UiMediaTrackMarker createMarker() {
		UiMediaTrackMarker marker = new UiMediaTrackMarker();
		marker.setTrack(track);
		marker.setStart(start);
		marker.setEnd(end);
		marker.setColor(color.toHtmlColorString());
		marker.setBackgroundColor(backgroundColor.toHtmlColorString());
		return marker;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
