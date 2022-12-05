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
import org.teamapps.dto.DtoClientObject;
import org.teamapps.dto.DtoClientObjectReference;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.dto.protocol.DtoQueryWrapper;
import org.teamapps.ux.session.SessionContext;

import java.lang.invoke.MethodHandles;

/**
 * A client object has a representation on the client.
 * This representation is said to be rendered when the client holds a corresponding instance of it.
 * The server and the client refer to the {@link ClientObject} using the id (a UUID).
 */
public interface ClientObject {

	Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	enum RenderingState {
		NOT_RENDERED,
		RENDERING,
		RENDERED
	}

	/**
	 * The id of a client object MUST be an immutable globally unique String.
	 * @return the id of this client object
	 */
	String getId();

	default boolean isRendered() {
		return SessionContext.current().isRendered(this);
	}

	DtoClientObject createDto();

	/**
	 * Creates a ui reference to a client object.
	 * Ui references are just a simple way to reference objects on the client side.
	 */
	default DtoClientObjectReference createDtoReference() {
		LOGGER.debug("createDtoClientObjectReference: " + getId());
		SessionContext.current().renderClientObject(this);
		return new DtoClientObjectReference(getId());
	}

	default void handleUiEvent(DtoEventWrapper event) {
	}

	default Object handleUiQuery(DtoQueryWrapper query) {
		return null;
	}
}
