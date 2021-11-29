/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiQrCodeScannerConfig extends UiComponentConfig {
	_type?: string;
	scanning?: boolean;
	stopsScanningAtFirstResult?: boolean
}

export interface UiQrCodeScannerCommandHandler extends UiComponentCommandHandler {
	startScanning(stopScanningAtFirstResult: boolean): any;
	stopScanning(): any;
	switchCamera(): any;
}

export interface UiQrCodeScannerEventSource {
	onQrCodeDetected: TeamAppsEvent<UiQrCodeScanner_QrCodeDetectedEvent>;
}

export interface UiQrCodeScanner_QrCodeDetectedEvent extends UiEvent {
	code: string
}

