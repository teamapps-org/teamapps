package org.teamapps.ux.component.media.shaka;

import org.teamapps.dto.UiShakaPlayerControlPanelElementType;

public enum ControlPanelElementType {

	REWIND                              ,
			FAST_FORWARD                ,
	SKIP_BACK                           ,
			SKIP_FORWARD                ,
	PLAY_PAUSE                          ,
			TIME_AND_DURATION           ,
	SPACER                              ,
			MUTE                        ,
	VOLUME                              ,
			FULLSCREEN                  ,
	OVERFLOW_MENU;

	public static ControlPanelElementType fromUiControlPanelElementType(UiShakaPlayerControlPanelElementType type) {
		return ControlPanelElementType.valueOf(type.name());
	}

	public UiShakaPlayerControlPanelElementType toUiShakaPlayerControlPanelElementType() {
		return UiShakaPlayerControlPanelElementType.valueOf(this.name());
	}
}
