/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiQuery;

import java.util.concurrent.CompletableFuture;

/**
 * A client object has a representation on the client.
 * This representation is said to be rendered when the client holds a corresponding instance of it.
 * The server and the client refer to the {@link ClientObject} using the id (a UUID).
 */
public interface ClientObject {

	String getId();

	void render();

	void unrender();

	boolean isRendered();

	/**
	 * Creates a ui reference to a client object.
	 * Ui references are just a simple way to reference objects on the client side.
	 */
	UiClientObjectReference createUiReference();

	default void handleUiEvent(UiEvent event) {
	}

	default CompletableFuture<?> handleUiQuery(UiQuery query) {
		return CompletableFuture.completedFuture(null);
	}
}
