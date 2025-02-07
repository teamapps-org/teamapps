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
package org.teamapps.projector.session;

public class ClientObjectNotFoundException extends RuntimeException {

	private final String sessionId;
	private final String clientObjectId;

	public ClientObjectNotFoundException(String sessionId, String clientObjectId) {
		super("Could not find client object " + clientObjectId + " in teamapps session: " + sessionId.toString());
		this.clientObjectId = clientObjectId;
		this.sessionId = sessionId;
	}

	public String getClientObjectId() {
		return clientObjectId;
	}

	public String getSessionId() {
		return sessionId;
	}
}
