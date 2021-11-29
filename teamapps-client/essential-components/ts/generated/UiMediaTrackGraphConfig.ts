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
import {UiMediaTrackDataConfig} from "./UiMediaTrackDataConfig";
import {UiMediaTrackMarkerConfig} from "./UiMediaTrackMarkerConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMediaTrackGraphConfig extends UiComponentConfig {
	_type?: string;
	trackCount?: number;
	trackData?: UiMediaTrackDataConfig[];
	markers?: UiMediaTrackMarkerConfig[]
}

export interface UiMediaTrackGraphCommandHandler extends UiComponentCommandHandler {
	setCursorPosition(time: number): any;
}

export interface UiMediaTrackGraphEventSource {
	onHandleTimeSelection: TeamAppsEvent<UiMediaTrackGraph_HandleTimeSelectionEvent>;
}

export interface UiMediaTrackGraph_HandleTimeSelectionEvent extends UiEvent {
	start: number;
	end: number
}

