/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export enum UiTreeGraphNodeImage_CornerShape {
	ORIGINAL, ROUNDED, CIRCLE
}

export interface UiTreeGraphNodeImageConfig {
	_type?: string;
	url: string;
	width: number;
	height: number;
	centerTopDistance?: number;
	centerLeftDistance?: number;
	cornerShape?: UiTreeGraphNodeImage_CornerShape;
	shadow?: boolean;
	borderWidth?: number;
	borderColor?: string
}


