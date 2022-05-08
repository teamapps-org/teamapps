package org.teamapps.ux.component.audiolevelindicator;

import org.teamapps.dto.UiAudioLevelIndicator;
import org.teamapps.dto.UiComponent;
import org.teamapps.ux.component.AbstractComponent;

public class AudioLevelIndicator extends AbstractComponent {

	private String deviceId;

	@Override
	public UiComponent createUiComponent() {
		UiAudioLevelIndicator ui = new UiAudioLevelIndicator();
		mapAbstractUiComponentProperties(ui);
		ui.setDeviceId(this.deviceId);
		return ui;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		queueCommandIfRendered(() -> new UiAudioLevelIndicator.SetDeviceIdCommand(getId(), deviceId));
	}
	
}
