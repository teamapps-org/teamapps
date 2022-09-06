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
package org.teamapps.ux.component.qrscanner;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiQrCodeScanner;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;

public class QrCodeScanner extends AbstractComponent {

	public final ProjectorEvent<String> onQrCodeDetected = createProjectorEventBoundToUiEvent(UiQrCodeScanner.QrCodeDetectedEvent.NAME);

	private boolean scanning;
	private boolean stopsScanningAtFirstResult;

	@Override
	public UiComponent createUiClientObject() {
		UiQrCodeScanner ui = new UiQrCodeScanner();
		mapAbstractUiComponentProperties(ui);
		ui.setScanning(scanning);
		ui.setStopsScanningAtFirstResult(stopsScanningAtFirstResult);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiQrCodeScanner.QrCodeDetectedEvent) {
			onQrCodeDetected.fire(((UiQrCodeScanner.QrCodeDetectedEvent) event).getCode());
		}
	}

	public void startScanning(boolean stopScanningAtFirstResult) {
		scanning = true;
		this.stopsScanningAtFirstResult = stopScanningAtFirstResult;
		sendCommandIfRendered(() -> new UiQrCodeScanner.StartScanningCommand(stopScanningAtFirstResult));
	}

	public void stopScanning() {
		scanning = false;
		sendCommandIfRendered(() -> new UiQrCodeScanner.StopScanningCommand());
	}

	public void switchCamera() {
		sendCommandIfRendered(() -> new UiQrCodeScanner.SwitchCameraCommand());
	}
}
