import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiQrCodeScanner_QrCodeDetectedEvent, UiQrCodeScannerCommandHandler, UiQrCodeScannerConfig, UiQrCodeScannerEventSource} from "../generated/UiQrCodeScannerConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {QrScanner} from './qr-code-scanner/qr-scanner';
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";

export class UiQrCodeScanner extends AbstractUiComponent<UiQrCodeScannerConfig> implements UiQrCodeScannerCommandHandler, UiQrCodeScannerEventSource {

	public readonly onQrCodeDetected: TeamAppsEvent<UiQrCodeScanner_QrCodeDetectedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $video: HTMLVideoElement;
	private qrScanner: QrScanner = null;

	private selectedCameraIndex: number = 0;

	constructor(config: UiQrCodeScannerConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiQrCodeScanner">
<video></video>
</div>`);
		this.$video = this.$main.querySelector(':scope video');

		if (config.scanning) {
			this.startScanning(config.stopsScanningAtFirstResult);
		}
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	@executeWhenFirstDisplayed(true)
	async startScanning(stopScanningAtFirstResult: boolean = true): Promise<void> {
		if (this.qrScanner != null) {
			this.stopScanning();
		}

		this._config.stopsScanningAtFirstResult = stopScanningAtFirstResult;
		this.qrScanner = new QrScanner(this.$video, (result: string) => {
			this.onQrCodeDetected.fire({code: result});
			if (this._config.stopsScanningAtFirstResult) {
				this.stopScanning();
			}
		});
		let availableCameras = await this.getAvailableCameras();
		if (availableCameras.length > 0) {
			let deviceId = availableCameras[this.selectedCameraIndex % availableCameras.length].deviceId;
			this.qrScanner.start(deviceId);
		}
	}

	stopScanning(): void {
		if (this.qrScanner != null) {
			this.qrScanner.stop();
			this.qrScanner.destroy();
		}
		this.qrScanner = null;
	}

	switchCamera(): void {
		this.selectedCameraIndex ++;
		this.startScanning();
	}

	private async getAvailableCameras() {
		return await navigator.mediaDevices.enumerateDevices()
			.then(devices => devices.filter(device => device.kind === 'videoinput'));
	}


	destroy() {
		this.qrScanner.destroy();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiQrCodeScanner", UiQrCodeScanner);