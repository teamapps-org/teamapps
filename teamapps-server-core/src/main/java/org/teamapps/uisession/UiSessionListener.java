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

import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.projector.session.uisession.UiSessionState;

import java.util.List;
import java.util.function.Consumer;

public interface UiSessionListener {

	default void handleEvent(String sessionId, String libraryId, String clientObjectId, String name, JsonWrapper eventObject) {
	}

	default void handleQuery(String sessionId, String libraryId, String clientObjectId, String name, List<JsonWrapper> params, Consumer<Object> resultCallback) {
	}

	default void onStateChanged(String sessionId, UiSessionState state) {
	}

	/**
	 * Additional state change callback method for sessions that got closed, but with a reason code.
	 * Note that this will be executed AFTER {@link #onStateChanged(String, UiSessionState)}.
	 */
	default void onClosed(String sessionId, SessionClosingReason reason) {
	}
}
