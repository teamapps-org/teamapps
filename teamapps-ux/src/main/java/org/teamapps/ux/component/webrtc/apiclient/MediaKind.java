package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaKind {
	AUDIO, VIDEO;

	@JsonValue
	public String jsonValue() {
		return name().toLowerCase();
	}
}




