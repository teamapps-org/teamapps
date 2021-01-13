/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

public class QrCodeScanner extends AbstractComponent {

	public final Event<String> onQrCodeDetected = new Event<>();

	private boolean scanning;
	private boolean stopsScanningAtFirstResult;

	@Override
	public UiComponent createUiComponent() {
		UiQrCodeScanner ui = new UiQrCodeScanner();
		mapAbstractUiComponentProperties(ui);
		ui.setScanning(scanning);
		ui.setStopsScanningAtFirstResult(stopsScanningAtFirstResult);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_QR_CODE_SCANNER_QR_CODE_DETECTED:
				onQrCodeDetected.fire(((UiQrCodeScanner.QrCodeDetectedEvent) event).getCode());
				break;
		}
	}

	public void startScanning(boolean stopScanningAtFirstResult) {
		scanning = true;
		this.stopsScanningAtFirstResult = stopScanningAtFirstResult;
		queueCommandIfRendered(() -> new UiQrCodeScanner.StartScanningCommand(getId(), stopScanningAtFirstResult));
	}

	public void stopScanning() {
		scanning = false;
		queueCommandIfRendered(() -> new UiQrCodeScanner.StopScanningCommand(getId()));
	}

	public void switchCamera() {
		queueCommandIfRendered(() -> new UiQrCodeScanner.SwitchCameraCommand(getId()));
	}
}
