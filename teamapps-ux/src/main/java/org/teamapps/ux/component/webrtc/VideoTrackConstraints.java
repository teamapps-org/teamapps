package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiVideoFacingMode;
import org.teamapps.dto.UiVideoTrackConstraints;

public class VideoTrackConstraints {

	private int width = 800;
	private int height = 600;
	private UiVideoFacingMode facingMode = UiVideoFacingMode.USER;
	private double frameRate = 20;

	public VideoTrackConstraints() {
	}

	public VideoTrackConstraints(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public VideoTrackConstraints(int width, int height, UiVideoFacingMode facingMode, double frameRate) {
		this.width = width;
		this.height = height;
		this.facingMode = facingMode;
		this.frameRate = frameRate;
	}

	public UiVideoTrackConstraints createUiVideoTrackConstraints() {
		UiVideoTrackConstraints ui = new UiVideoTrackConstraints();
		ui.setHeight(height);
		ui.setWidth(width);
		ui.setFacingMode(facingMode);
		ui.setFrameRate(frameRate);
		return ui;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public UiVideoFacingMode getFacingMode() {
		return facingMode;
	}

	public void setFacingMode(UiVideoFacingMode facingMode) {
		this.facingMode = facingMode;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}
}
