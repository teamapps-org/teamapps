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
package org.teamapps.uisession;

import java.util.function.Consumer;

public class UiCommandWithResultCallback {

	private final String libraryUuid;
	private final String clientObjectId;
	private final String commandName;
	private final Object[] params;
	private final Consumer<Object> resultCallback;

	public UiCommandWithResultCallback(String libraryUuid, String clientObjectId, String commandName, Object[] params, Consumer<Object> resultCallback) {
		this.libraryUuid = libraryUuid;
		this.clientObjectId = clientObjectId;
		this.commandName = commandName;
		this.params = params;
		this.resultCallback = resultCallback;
	}

	public UiCommandWithResultCallback(String libraryUuid, String clientObjectId, String commandName, Object... params) {
		this(libraryUuid, clientObjectId, commandName, params, null);
	}

	public String getLibraryUuid() {
		return libraryUuid;
	}

	public String getClientObjectId() {
		return clientObjectId;
	}

	public String getCommandName() {
		return commandName;
	}

	public Object[] getParams() {
		return params;
	}

	public Consumer<Object> getResultCallback() {
		return resultCallback;
	}
}
