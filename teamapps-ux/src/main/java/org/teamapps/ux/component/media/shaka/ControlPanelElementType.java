/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
