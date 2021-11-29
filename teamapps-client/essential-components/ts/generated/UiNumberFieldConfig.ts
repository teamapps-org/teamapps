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
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiNumberFieldSliderMode} from "./UiNumberFieldSliderMode";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface UiNumberFieldConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	locale?: string;
	precision?: number;
	placeholderText?: string;
	showClearButton?: boolean;
	minValue?: number;
	maxValue?: number;
	sliderMode?: UiNumberFieldSliderMode;
	sliderStep?: number;
	commitOnSliderChange?: boolean
}

export interface UiNumberFieldCommandHandler extends UiFieldCommandHandler {
	setMinValue(min: number): any;
	setMaxValue(max: number): any;
	setSliderMode(sliderMode: UiNumberFieldSliderMode): any;
	setSliderStep(step: number): any;
	setCommitOnSliderChange(commitOnSliderChange: boolean): any;
	setPrecision(displayPrecision: number): any;
	setPlaceholderText(placeholderText: string): any;
	setShowClearButton(showClearButton: boolean): any;
	setLocale(locale: string): any;
}

export interface UiNumberFieldEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
}


