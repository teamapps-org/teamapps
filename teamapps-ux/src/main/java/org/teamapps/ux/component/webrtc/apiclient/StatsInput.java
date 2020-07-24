package org.teamapps.ux.component.webrtc.apiclient;

import java.util.List;

public class StatsInput {
	private final List<String> ids;

	public StatsInput(List<String> ids) {
		this.ids = ids;
	}

	public List<String> getIds() {
		return ids;
	}
}