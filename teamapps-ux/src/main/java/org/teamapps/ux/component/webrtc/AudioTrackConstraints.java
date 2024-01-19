/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.dto.UiAudioTrackConstraints;

import java.util.Objects;

public class AudioTrackConstraints {

	private String deviceId = null;
	private int channelCount = 1;
	private boolean autoGainControl = true;
	/**
	 * Factor multiplied to audio samples.
	 * 1 means unchanged.
	 */
	private double gainFactor = 1;
	private boolean echoCancellation = true;
	private boolean noiseSuppression = true;

	public AudioTrackConstraints() {
	}

	public AudioTrackConstraints(boolean autoGainControl, boolean echoCancellation, boolean noiseSuppression) {
		this.autoGainControl = autoGainControl;
		this.echoCancellation = echoCancellation;
		this.noiseSuppression = noiseSuppression;
	}

	public AudioTrackConstraints(int channelCount, boolean autoGainControl, boolean echoCancellation, boolean noiseSuppression) {
		this.channelCount = channelCount;
		this.autoGainControl = autoGainControl;
		this.echoCancellation = echoCancellation;
		this.noiseSuppression = noiseSuppression;
	}

	public UiAudioTrackConstraints createUiAudioTrackConstraints() {
		UiAudioTrackConstraints ui = new UiAudioTrackConstraints();
		ui.setChannelCount(channelCount);
		ui.setAutoGainControl(autoGainControl);
		ui.setGainFactor(gainFactor);
		ui.setEchoCancellation(echoCancellation);
		ui.setNoiseSuppression(noiseSuppression);
		ui.setDeviceId(deviceId);
		return ui;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public boolean isAutoGainControl() {
		return autoGainControl;
	}

	public void setAutoGainControl(boolean autoGainControl) {
		this.autoGainControl = autoGainControl;
	}

	public boolean isEchoCancellation() {
		return echoCancellation;
	}

	public void setEchoCancellation(boolean echoCancellation) {
		this.echoCancellation = echoCancellation;
	}

	public boolean isNoiseSuppression() {
		return noiseSuppression;
	}

	public void setNoiseSuppression(boolean noiseSuppression) {
		this.noiseSuppression = noiseSuppression;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public double getGainFactor() {
		return gainFactor;
	}

	public void setGainFactor(double gainFactor) {
		this.gainFactor = gainFactor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AudioTrackConstraints)) {
			return false;
		}
		AudioTrackConstraints that = (AudioTrackConstraints) o;
		return channelCount == that.channelCount && autoGainControl == that.autoGainControl && Double.compare(that.gainFactor, gainFactor) == 0 && echoCancellation == that.echoCancellation && noiseSuppression == that.noiseSuppression && Objects.equals(deviceId, that.deviceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceId, channelCount, autoGainControl, gainFactor, echoCancellation, noiseSuppression);
	}
}
