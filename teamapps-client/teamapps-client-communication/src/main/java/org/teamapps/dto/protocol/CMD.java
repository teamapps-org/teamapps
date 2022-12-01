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
package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class CMD {

	private final int id;

	@JsonProperty("lid")
	private final String libraryUuid;

	@JsonProperty("cid")
	private final String clientObjectId;

	@JsonRawValue
	@JsonProperty("c")
	private final String uiCommand;

	@JsonProperty("r")
	private final Boolean awaitsResponse; // nullable! (for message size reasons)

	public CMD(int id, String libraryUuid, String clientObjectId, String uiCommand, boolean awaitsResponse) {
		this.id = id;
		this.libraryUuid = libraryUuid;
		this.clientObjectId = clientObjectId;
		this.uiCommand = uiCommand;
		this.awaitsResponse = awaitsResponse ? true : null;
	}

	@Override
	public String toString() {
		return "CMD{" +
				"id=" + id +
				", libraryUuid='" + libraryUuid + '\'' +
				", clientObjectId='" + clientObjectId + '\'' +
				", uiCommand='" + uiCommand + '\'' +
				", awaitsResponse=" + awaitsResponse +
				'}';
	}

	public int getId() {
		return id;
	}

	public String getUiCommand() {
		return uiCommand;
	}

}
