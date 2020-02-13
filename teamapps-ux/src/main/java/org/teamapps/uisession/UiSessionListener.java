/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.dto.UiClientInfo;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiSessionClosingReason;

public interface UiSessionListener {

    void onUiSessionStarted(QualifiedUiSessionId sessionId, UiClientInfo uiClientInfo);
	void onUiSessionClientRefresh(QualifiedUiSessionId sessionId, UiClientInfo clientInfo);
	void onUiEvent(QualifiedUiSessionId sessionId, UiEvent event);
	void onActivityStateChanged(QualifiedUiSessionId sessionId, boolean active);
	void onUiSessionClosed(QualifiedUiSessionId sessionId, UiSessionClosingReason reason);
}
