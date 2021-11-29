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
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";


export interface UiTableColumnConfig {
	_type?: string;
	propertyName: string;
	icon: string;
	title: string;
	field: unknown;
	minWidth?: number;
	defaultWidth?: number;
	maxWidth?: number;
	sortable?: boolean;
	resizeable?: boolean;
	visible?: boolean;
	hiddenIfOnlyEmptyCellsVisible?: boolean;
	messages?: UiFieldMessageConfig[]
}


