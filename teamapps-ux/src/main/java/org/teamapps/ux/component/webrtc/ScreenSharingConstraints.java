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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenSharingConstraints that = (ScreenSharingConstraints) o;

		if (maxWidth != that.maxWidth) {
			return false;
		}
		return maxHeight == that.maxHeight;
	}

	@Override
	public int hashCode() {
		int result = maxWidth;
		result = 31 * result + maxHeight;
		return result;
	}
}
