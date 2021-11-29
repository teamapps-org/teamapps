/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiSliderConfig extends UiFieldConfig {
	_type?: string;
	min?: number;
	max?: number;
	step?: number;
	displayedDecimals?: number;
	selectionColor?: string;
	tooltipPrefix?: string;
	tooltipPostfix?: string;
	humanReadableFileSize?: boolean
}

export interface UiSliderCommandHandler extends UiFieldCommandHandler {
	setMin(min: number): any;
	setMax(max: number): any;
	setStep(step: number): any;
	setDisplayedDecimals(displayedDecimals: number): any;
	setSelectionColor(selectionColor: string): any;
	setTooltipPrefix(tooltipPrefi: string): any;
	setTooltipPostfix(tooltipPostfi: string): any;
	setHumanReadableFileSize(humanReadableFileSize: boolean): any;
}

export interface UiSliderEventSource extends UiFieldEventSource {
}


