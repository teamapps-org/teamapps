/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiQuery} from "./UiQuery";
import {UiChatDisplayConfig} from "./UiChatDisplayConfig";
import {UiComboBoxConfig} from "./UiComboBoxConfig";
import {UiTagComboBoxConfig} from "./UiTagComboBoxConfig";

export class QueryFunctionAdder {
	public static addQueryFunctionsToConfig(config: UiClientObjectConfig, queryCallback: (componentId: string, queryTypeId: string, queryObject: UiQuery) => Promise<any>) {
		switch (config._type) {
		    case "UiChatDisplay":
		    	    (config as UiChatDisplayConfig).requestContextMenu = (queryObject: UiQuery) => queryCallback(config.id, "UiChatDisplay.requestContextMenu", queryObject);
		    	    (config as UiChatDisplayConfig).requestPreviousMessages = (queryObject: UiQuery) => queryCallback(config.id, "UiChatDisplay.requestPreviousMessages", queryObject);
		    	break;
		    case "UiComboBox":
		    	    (config as UiComboBoxConfig).retrieveDropdownEntries = (queryObject: UiQuery) => queryCallback(config.id, "UiComboBox.retrieveDropdownEntries", queryObject);
		    	    (config as UiComboBoxConfig).lazyChildren = (queryObject: UiQuery) => queryCallback(config.id, "UiComboBox.lazyChildren", queryObject);
		    	break;
		    case "UiTagComboBox":
		    	    (config as UiTagComboBoxConfig).retrieveDropdownEntries = (queryObject: UiQuery) => queryCallback(config.id, "UiComboBox.retrieveDropdownEntries", queryObject);
		    	    (config as UiTagComboBoxConfig).lazyChildren = (queryObject: UiQuery) => queryCallback(config.id, "UiComboBox.lazyChildren", queryObject);
		    	break;
		}
	}
}