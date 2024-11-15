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
package org.teamapps.uisession.statistics.app;

import org.teamapps.projector.session.uisession.ClientBackPressureInfo;
import org.teamapps.projector.session.uisession.UiSession;
import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;

public class UiSessionStatsStatsTableRecord implements SessionStatsTableRecord {

	private final UiSessionStatistics uiSessionStatistics;

	public UiSessionStatsStatsTableRecord(UiSessionStatistics uiSessionStatistics) {
		this.uiSessionStatistics = uiSessionStatistics;
	}

	@Override
	public UiSession getUiSession() {
		return null;
	}

	@Override
	public UiSessionStatistics getStatistics() {
		return uiSessionStatistics;
	}

	@Override
	public ClientBackPressureInfo getClientBackPressureInfo() {
		return null;
	}
}