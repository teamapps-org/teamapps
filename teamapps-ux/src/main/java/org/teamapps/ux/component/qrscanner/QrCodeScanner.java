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
