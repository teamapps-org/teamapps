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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiQrCodeScanner_QrCodeDetectedEvent, UiQrCodeScannerCommandHandler, UiQrCodeScannerConfig, UiQrCodeScannerEventSource} from "../generated/UiQrCodeScannerConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {calculateDisplayModeInnerSize, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {QrScanner} from './qr-code-scanner/qr-scanner';
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";

export class UiQrCodeScanner extends AbstractUiComponent<UiQrCodeScannerConfig> implements UiQrCodeScannerCommandHandler, UiQrCodeScannerEventSource {

	public readonly onQrCodeDetected: TeamAppsEvent<UiQrCodeScanner_QrCodeDetectedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $video: HTMLVideoElement;
	private $crosshair: HTMLElement;
	private qrScanner: QrScanner = null;

	private selectedCameraIndex: number = 0;

	constructor(config: UiQrCodeScannerConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiQrCodeScanner">
	<video></video>
	<div class="crosshair-wrapper">
		<div class="crosshair"></div>
	</div>
</div>`);
		this.$video = this.$main.querySelector(':scope video');
		this.$crosshair = this.$main.querySelector(':scope .crosshair');

		this.$video.addEventListener("playing", () => this.onResize());

		if (config.scanning) {
			this.startScanning(config.stopsScanningAtFirstResult);
		}
	}

	doGetMainElement(): HTMLElement {
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

	onResize(): void {
		if (this.$video.videoWidth > 0 && this.$video.videoHeight > 0) {
			let crosshairSize = calculateDisplayModeInnerSize(this.$main.getBoundingClientRect(), {width: this.$video.videoWidth, height: this.$video.videoHeight}, UiPageDisplayMode.FIT_SIZE);
			this.$crosshair.style.width = crosshairSize.width + "px";
			this.$crosshair.style.height = crosshairSize.height + "px";
		}
	}


	destroy() {
		super.destroy();
		this.qrScanner.destroy();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiQrCodeScanner", UiQrCodeScanner);
