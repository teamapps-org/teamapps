package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiMediaServerUrlAndToken;

public class MediaSoupServerUrlAndToken {

	private final String url;
	private final String token;

	public MediaSoupServerUrlAndToken(String url, String token) {
		this.url = url;
		this.token = token;
	}

	public UiMediaServerUrlAndToken createUiMediaSoupServerChain() {
		return new UiMediaServerUrlAndToken(url, token);
	}

	public String getUrl() {
		return url;
	}

	public String getToken() {
		return token;
	}

}
