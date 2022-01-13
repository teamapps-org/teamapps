/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import org.teamapps.dto.UiMediaTrackData;
import org.teamapps.dto.UiMediaTrackMarker;

import java.util.ArrayList;
import java.util.List;

public class MediaTrackData {

	private int trackCount;

	private List<TrackMarkerData> markerData = new ArrayList<>();
	private List<TrackData> data = new ArrayList<>();

	public MediaTrackData() {
	}

	public List<UiMediaTrackMarker> getMarkers() {
		List<UiMediaTrackMarker> markers = new ArrayList<>();
		for (TrackMarkerData marker : markerData) {
			markers.add(marker.createMarker());
		}
		return markers;
	}

	public List<UiMediaTrackData> getTrackData() {
		List<UiMediaTrackData> result = new ArrayList<>();
		for (TrackData trackData : data) {
			UiMediaTrackData td = new UiMediaTrackData();
			td.setTime(trackData.getTime());
			td.setValues(trackData.getValues());
			result.add(td);
		}
		return result;
	}

	public void addMarker(TrackMarkerData marker) {
		markerData.add(marker);
	}

	public void addData(long time, int[] values) {
		List<Integer> list = new ArrayList<>();
		for (int value : values) {
			list.add(value);
		}
		data.add(new TrackData(time, list));
	}


	private void addData(long time, List<Integer> values) {
		data.add(new TrackData(time, values));
	}

	public int getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(int trackCount) {
		this.trackCount = trackCount;
	}

	public List<TrackMarkerData> getMarkerData() {
		return markerData;
	}

	public void setMarkerData(List<TrackMarkerData> markerData) {
		this.markerData = markerData;
	}

	public List<TrackData> getData() {
		return data;
	}

	public void setData(List<TrackData> data) {
		this.data = data;
	}

	class TrackData {
		private long time;
		private List<Integer> values;

		public TrackData(long time, List<Integer> values) {
			this.time = time;
			this.values = values;
		}

		public long getTime() {
			return time;
		}

		public List<Integer> getValues() {
			return values;
		}
	}

}
