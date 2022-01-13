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
package org.teamapps.ux.component.webrtc;

public class MediaSoupServerUrlAndToken {

	private final String serverUrl;
	private final int worker;
	private final String token;

	public MediaSoupServerUrlAndToken(String serverUrl, int worker, String token) {
		this.serverUrl = serverUrl;
		this.worker = worker;
		this.token = token;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public int getWorker() {
		return worker;
	}

	public String getToken() {
		return token;
	}

}
