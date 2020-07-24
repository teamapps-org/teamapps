package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreamData {
	@JsonProperty("stream")
	private final String streamUuid;

	public StreamData(String streamUuid) {
		this.streamUuid = streamUuid;
	}

	public String getStreamUuid() {
		return streamUuid;
	}
}
