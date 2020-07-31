package org.teamapps.ux.component.webrtc.apiclient;

import java.util.Set;

public class KindsOptionsData {
	private Set<MediaKind>  kinds;
	private int width;
	private int height;

	public KindsOptionsData(Set<MediaKind> kinds, int width, int height) {
		this.kinds = kinds;
		this.width = width;
		this.height = height;
	}

	public KindsOptionsData() {
	}

	public Set<MediaKind> getKinds() {
		return kinds;
	}

	public void setKinds(Set<MediaKind> kinds) {
		this.kinds = kinds;
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
}

