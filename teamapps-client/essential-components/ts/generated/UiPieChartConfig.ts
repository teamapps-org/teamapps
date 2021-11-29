/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractUiChartConfig} from "./AbstractUiChartConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiChartNamedDataPointConfig} from "./UiChartNamedDataPointConfig";
import {UiDataPointWeighting} from "./UiDataPointWeighting";
import {AbstractUiChartCommandHandler} from "./AbstractUiChartConfig";


export interface UiPieChartConfig extends AbstractUiChartConfig {
	_type?: string;
	dataPointWeighting?: UiDataPointWeighting;
	rotation3D?: number;
	height3D?: number;
	rotationClockwise?: number;
	innerRadiusProportion?: number;
	dataPoints?: UiChartNamedDataPointConfig[]
}

export interface UiPieChartCommandHandler extends AbstractUiChartCommandHandler {
	setDataPointWeighting(dataPointWeighting: UiDataPointWeighting): any;
	setRotation3D(rotation3D: number): any;
	setHeight3D(height3D: number): any;
	setRotationClockwise(rotationClockwise: number): any;
	setInnerRadiusProportion(innerRadiusProportion: number): any;
	setDataPoints(dataPoints: UiChartNamedDataPointConfig[], animationDuration: number): any;
}

export interface UiPieChartEventSource {
	onDataPointClicked: TeamAppsEvent<UiPieChart_DataPointClickedEvent>;
}

export interface UiPieChart_DataPointClickedEvent extends UiEvent {
	dataPointName: string
}

