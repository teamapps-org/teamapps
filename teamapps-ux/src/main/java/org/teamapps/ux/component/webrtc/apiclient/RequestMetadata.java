package org.teamapps.ux.component.webrtc.apiclient;

public class RequestMetadata {

	private final int workerId;
	private final String token;

	public RequestMetadata(int workerId, String token) {
		this.workerId = workerId;
		this.token = token;
	}

	public int getWorkerId() {
		return workerId;
	}

	public String getToken() {
		return token;
	}
}
