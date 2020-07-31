package org.teamapps.ux.component.webrtc.apiclient;

public class KindsByFileInput {

	private String filePath;
	private boolean relativePath;

	public KindsByFileInput() {
	}

	public KindsByFileInput(String filePath, boolean relativePath) {
		this.filePath = filePath;
		this.relativePath = relativePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isRelativePath() {
		return relativePath;
	}

	public void setRelativePath(boolean relativePath) {
		this.relativePath = relativePath;
	}
}
