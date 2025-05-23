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
package org.teamapps.projector.clientobject;

/**
 * An object that has a client-side representation.
 * <p>
 * ClientObject extends ClientMessageHandler to handle messages from the client,
 * and provides a method to create a data transfer object (DTO) that contains
 * all the necessary information for the client to create or update the
 * corresponding client-side representation of this object.
 */
public interface ClientObject extends ClientMessageHandler {

	/**
	 * Creates a data transfer object (DTO) that represents this client object.
	 * The DTO contains all the necessary information for the client to create
	 * or update the corresponding client-side representation of this object.
	 *
	 * @return A DTO containing the object's configuration data
	 */
	Object createDto();

}
