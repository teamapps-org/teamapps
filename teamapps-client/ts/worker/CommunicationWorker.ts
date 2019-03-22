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
import {UiCommand} from "../generated/UiCommand";
import {TeamAppsConnectionImpl} from "../shared/TeamAppsConnectionImpl";

(function (self: ServiceWorkerGlobalScope) {

	let sessionConnection: TeamAppsConnectionImpl;
	let isInitializationMessage = true;

	self.onmessage = function (e) {
		if (isInitializationMessage) {
			sessionConnection = new TeamAppsConnectionImpl(
				e.data.webSocketUrl,
				e.data.sessionId,
				e.data.clientInfo,
				{
					onConnectionInitialized: () => postMessage({_type: "onConnectionInitialized"}),
					onConnectionErrorOrBroken: (reason, message) => postMessage({_type: "onConnectionBroken", reason, message}),
					executeCommand: (uiCommand: UiCommand) => postMessage({_type: "onCommand", uiCommand}),
					executeCommands: (uiCommands: UiCommand[]) => postMessage({_type: "onCommands", uiCommands})
				}
			);
			isInitializationMessage = false;
		} else {
			sessionConnection.sendEvent(e.data.event);
		}
	};

})(self as ServiceWorkerGlobalScope);
