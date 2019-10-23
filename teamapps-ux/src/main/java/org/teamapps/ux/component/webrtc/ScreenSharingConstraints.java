package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiScreenSharingConstraints;

public class ScreenSharingConstraints {

	private int maxWidth = Integer.MAX_VALUE;
	private int maxHeight = Integer.MAX_VALUE;

	public ScreenSharingConstraints() {
	}

	public ScreenSharingConstraints(int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public UiScreenSharingConstraints createUiScreenSharingConstraints() {
		UiScreenSharingConstraints ui = new UiScreenSharingConstraints();
		ui.setMaxWidth(maxWidth);
		ui.setMaxHeight(maxHeight);
		return ui;
	}

}
