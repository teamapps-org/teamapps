package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiAudioTrackConstraints;

public class AudioTrackConstraints {

	private int channelCount = 1;
	private boolean autoGainControl = true;
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
		ui.setEchoCancellation(echoCancellation);
		ui.setNoiseSuppression(noiseSuppression);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AudioTrackConstraints that = (AudioTrackConstraints) o;

		if (channelCount != that.channelCount) {
			return false;
		}
		if (autoGainControl != that.autoGainControl) {
			return false;
		}
		if (echoCancellation != that.echoCancellation) {
			return false;
		}
		return noiseSuppression == that.noiseSuppression;
	}

	@Override
	public int hashCode() {
		int result = channelCount;
		result = 31 * result + (autoGainControl ? 1 : 0);
		result = 31 * result + (echoCancellation ? 1 : 0);
		result = 31 * result + (noiseSuppression ? 1 : 0);
		return result;
	}
}
