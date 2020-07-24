package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamAndKinds {

	@JsonProperty("stream")
	private final String streamUuid;
	private final Set<MediaKind> kinds;

	public StreamAndKinds(String streamUuid, Set<MediaKind> kinds) {
		this.streamUuid = streamUuid;
		this.kinds = kinds;
	}

	public String getStreamUuid() {
		return streamUuid;
	}

	public Set<MediaKind> getKinds() {
		return kinds;
	}
}