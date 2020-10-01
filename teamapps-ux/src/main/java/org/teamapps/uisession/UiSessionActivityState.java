/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.uisession;

public class UiSessionActivityState {

	/**
	 * If true, the client has sent any ui protocol message (at least a KEEPALIVE) within
	 * the timeout configured as {@link org.teamapps.config.TeamAppsConfiguration#uiSessionInactivityTimeoutMillis}.
	 * Otherwise (false), the client can be regarded as temporarily disconnected.
	 */
	private final boolean active;

	public UiSessionActivityState(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

}
