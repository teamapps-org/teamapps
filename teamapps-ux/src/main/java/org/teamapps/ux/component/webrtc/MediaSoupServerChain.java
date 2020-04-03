package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiMediaSoupServerChain;

public class MediaSoupServerChain {

	private final String url;
	private final String token;
	private final MediaSoupServerChain origin;

	public MediaSoupServerChain(String url, String token) {
		this(url, token, null);
	}

	public MediaSoupServerChain(String url, String token, MediaSoupServerChain origin) {
		this.url = url;
		this.token = token;
		this.origin = origin;
	}

	public UiMediaSoupServerChain createUiMediaSoupServerChain() {
		UiMediaSoupServerChain ui = new UiMediaSoupServerChain(url, token);
		if (ui.getOrigin() != null) {
			ui.setOrigin(origin.createUiMediaSoupServerChain());
		}
		return ui;
	}

	public String getUrl() {
		return url;
	}

	public String getToken() {
		return token;
	}

	public MediaSoupServerChain getOrigin() {
		return origin;
	}

}
