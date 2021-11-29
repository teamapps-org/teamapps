/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";
import {UiMapLocationConfig} from "./UiMapLocationConfig";
import {UiMapMarkerAnchor} from "./UiMapMarkerAnchor";


export interface UiMapMarkerClientRecordConfig extends UiIdentifiableClientRecordConfig {
	_type?: string;
	location?: UiMapLocationConfig;
	templateId?: string;
	anchor?: UiMapMarkerAnchor;
	offsetPixelsX?: number;
	offsetPixelsY?: number
}


