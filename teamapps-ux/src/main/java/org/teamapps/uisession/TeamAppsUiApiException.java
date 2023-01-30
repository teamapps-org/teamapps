/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.uisession;

public class TeamAppsUiApiException extends RuntimeException {

	private final String sessionId;
	private final String api;

	public TeamAppsUiApiException(String sessionId, String api) {
		super("Incompatible API call: " + api + "; session" + sessionId.toString());
		this.sessionId = sessionId;
		this.api = api;
	}

	public String getApi() {
		return api;
	}

	public String getSessionId() {
		return sessionId;
	}
}
