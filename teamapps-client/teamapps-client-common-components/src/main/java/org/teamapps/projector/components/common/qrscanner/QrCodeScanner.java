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
package org.teamapps.projector.components.common.qrscanner;

import org.teamapps.projector.components.common.dto.DtoComponent;
import org.teamapps.projector.components.common.dto.DtoQrCodeScanner;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;

public class QrCodeScanner extends AbstractComponent {

	public final ProjectorEvent<String> onQrCodeDetected = new ProjectorEvent<>(clientObjectChannel::toggleQrCodeDetectedEvent);

	private boolean scanning;
	private boolean stopsScanningAtFirstResult;

	@Override
	public DtoComponent createDto() {
		DtoQrCodeScanner ui = new DtoQrCodeScanner();
		mapAbstractUiComponentProperties(ui);
		ui.setScanning(scanning);
		ui.setStopsScanningAtFirstResult(stopsScanningAtFirstResult);
		return ui;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoQrCodeScanner.QrCodeDetectedEvent.TYPE_ID -> {
				var e = event.as(DtoQrCodeScanner.QrCodeDetectedEventWrapper.class);
				onQrCodeDetected.fire(e.getCode());
			}
		}
	}

	public void startScanning(boolean stopScanningAtFirstResult) {
		scanning = true;
		this.stopsScanningAtFirstResult = stopScanningAtFirstResult;
		sendCommandIfRendered(() -> new DtoQrCodeScanner.StartScanningCommand(stopScanningAtFirstResult));
	}

	public void stopScanning() {
		scanning = false;
		sendCommandIfRendered(() -> new DtoQrCodeScanner.StopScanningCommand());
	}

	public void switchCamera() {
		sendCommandIfRendered(() -> new DtoQrCodeScanner.SwitchCameraCommand());
	}
}
