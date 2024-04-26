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
package org.teamapps.ux.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.JsonWrapper;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * A client object has a representation on the client.
 * This representation is said to be rendered when the client holds a corresponding instance of it.
 * The server and the client refer to the {@link ClientObject} using the id (a UUID).
 */
public interface ClientObject {

	Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Sets the channel that this object must use to communicate with its client-side representation.
	 */
	void setClientObjectChannel(ClientObjectChannel clientObjectChannel);

	Object createConfig();

	List<String> getListeningEventNames();

	default void handleEvent(String name, List<JsonWrapper> params) {
	}
	default Object handleQuery(String name, List<JsonWrapper> params) {
		return null;
	}
}
