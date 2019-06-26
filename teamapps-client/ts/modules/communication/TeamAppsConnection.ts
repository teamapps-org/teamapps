/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {UiCommand} from "../../generated/UiCommand";
import {INIT_NOK_Reason} from "../../generated/INIT_NOKConfig";
import {REINIT_NOK_Reason} from "../../generated/REINIT_NOKConfig";
import {SERVER_ERROR_Reason} from "../../generated/SERVER_ERRORConfig";
import {UiEvent} from "../../generated/UiEvent";

export const typescriptDeclarationFixConstant = 1;

export interface TeamAppsConnection {
	sendEvent(event: UiEvent): void;
}

export interface TeamAppsConnectionListener {
	onConnectionInitialized(): void;
	onConnectionErrorOrBroken(reason: INIT_NOK_Reason | REINIT_NOK_Reason | SERVER_ERROR_Reason, message?: string): void;
	executeCommand(uiCommand: UiCommand): Promise<any>;
	executeCommands(uiCommands: UiCommand[]): Promise<any>[];
}
