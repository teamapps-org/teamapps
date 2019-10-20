package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiMulticastPlaybackProfile;

public enum  MulticastPlaybackProfile {

	HIGH, MEDIUM, LOW;

	public UiMulticastPlaybackProfile toUiMulticastPlaybackProfile() {
		return UiMulticastPlaybackProfile.valueOf(name());
	}

}