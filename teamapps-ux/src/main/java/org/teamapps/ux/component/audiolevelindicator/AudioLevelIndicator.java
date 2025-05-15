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
package org.teamapps.ux.component.audiolevelindicator;

import org.teamapps.dto.UiAudioLevelIndicator;
import org.teamapps.dto.UiComponent;
import org.teamapps.ux.component.AbstractComponent;

public class AudioLevelIndicator extends AbstractComponent {

	private String deviceId;
	private int barWidth = 1;

	@Override
	public UiComponent createUiComponent() {
		UiAudioLevelIndicator ui = new UiAudioLevelIndicator();
		mapAbstractUiComponentProperties(ui);
		ui.setDeviceId(this.deviceId);
		ui.setBarWidth(this.barWidth);
		return ui;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		queueCommandIfRendered(() -> new UiAudioLevelIndicator.SetDeviceIdCommand(getId(), deviceId));
	}

	public void setBarWidth(int barWidth) {
		boolean changed = barWidth != this.barWidth;
		this.barWidth = barWidth;
		if (changed) {
			reRenderIfRendered();
		}
	}
}
