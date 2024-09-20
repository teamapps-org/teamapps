/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.component.audiolevel;

import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.component.common.DtoAudioLevelIndicator;
import org.teamapps.projector.component.common.DtoAudioLevelIndicatorClientObjectChannel;
import org.teamapps.projector.component.common.DtoAudioLevelIndicatorEventHandler;

public class AudioLevelIndicator extends AbstractComponent implements DtoAudioLevelIndicatorEventHandler {

	private final DtoAudioLevelIndicatorClientObjectChannel clientObjectChannel = new DtoAudioLevelIndicatorClientObjectChannel(getClientObjectChannel());

	private String deviceId;
	private int barWidth = 1;

	@Override
	public ComponentConfig createConfig() {
		DtoAudioLevelIndicator ui = new DtoAudioLevelIndicator();
		mapAbstractConfigProperties(ui);
		ui.setDeviceId(this.deviceId);
		ui.setBarWidth(this.barWidth);
		return ui;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		clientObjectChannel.setDeviceId(deviceId);
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
		clientObjectChannel.setBarWidth(barWidth);
	}
}
