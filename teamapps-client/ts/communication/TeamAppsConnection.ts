/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {UiCommand} from "../generated/UiCommand";
import {UiEvent} from "../generated/UiEvent";
import {UiQuery} from "../generated/UiQuery";
import {UiSessionClosingReason} from "../generated/UiSessionClosingReason";

export const typescriptDeclarationFixConstant = 1;

export interface TeamAppsConnection {
	sendEvent(event: UiEvent): void;
	sendQuery(query: UiQuery): Promise<any>;
}

export interface TeamAppsConnectionListener {
	onConnectionInitialized(): void;
	onConnectionErrorOrBroken(reason: UiSessionClosingReason, message?: string): void;
	executeCommand(uiCommand: UiCommand): Promise<any>;
}
