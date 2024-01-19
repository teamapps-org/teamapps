/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.uisession.statistics.app;

import org.teamapps.uisession.ClientBackPressureInfo;
import org.teamapps.uisession.UiSession;
import org.teamapps.uisession.statistics.UiSessionStats;

public class UiSessionSessionStatsTableRecord implements SessionStatsTableRecord {

	private final UiSession uiSession;
	private final ClientBackPressureInfo clientBackPressureInfo;

	public UiSessionSessionStatsTableRecord(UiSession uiSession) {
		this.uiSession = uiSession;
		this.clientBackPressureInfo = uiSession.getClientBackPressureInfo(); // proactively extract, so not every column has to do it!
	}

	@Override
	public UiSession getUiSession() {
		return uiSession;
	}

	@Override
	public UiSessionStats getStatistics() {
		return uiSession.getStatistics();
	}

	@Override
	public ClientBackPressureInfo getClientBackPressureInfo() {
		return clientBackPressureInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UiSessionSessionStatsTableRecord that = (UiSessionSessionStatsTableRecord) o;
		return uiSession.getSessionId().equals(that.uiSession.getSessionId());
	}

	@Override
	public int hashCode() {
		return uiSession.getSessionId().hashCode();
	}
}
