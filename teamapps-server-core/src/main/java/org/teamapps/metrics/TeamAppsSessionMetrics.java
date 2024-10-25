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
package org.teamapps.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.teamapps.uisession.TeamAppsSessionManager;
import org.teamapps.projector.session.uisession.UiSessionState;

public class TeamAppsSessionMetrics implements MeterBinder {

	private static final String ACTIVITY_STATE_TAG = "state";

	private final TeamAppsSessionManager sessionManager;

	public TeamAppsSessionMetrics(TeamAppsSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void bindTo(MeterRegistry registry) {

		Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.ACTIVE))
				.description("Current number of active UI sessions.")
				.tag(ACTIVITY_STATE_TAG, UiSessionState.ACTIVE.toString().toLowerCase())
				.register(registry);

		Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.NEARLY_INACTIVE))
				.description("Current number of nearly inactive UI sessions.")
				.tag(ACTIVITY_STATE_TAG, UiSessionState.NEARLY_INACTIVE.toString().toLowerCase())
				.register(registry);

		Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.INACTIVE))
				.description("Current number of inactive UI sessions.")
				.tag(ACTIVITY_STATE_TAG, UiSessionState.INACTIVE.toString().toLowerCase())
				.register(registry);

		Gauge.builder("teamapps.uisession.commandbuffers.size", sessionManager, TeamAppsSessionManager::getBufferedCommandsCount)
				.description("Current number of all commands in all command buffers.")
				.register(registry);

		Gauge.builder("teamapps.uisession.commandbuffers.unconsumed", sessionManager, TeamAppsSessionManager::getUnconsumedCommandsCount)
				.description("Current number of commands in command buffers that are queued waiting to be consumed.")
				.register(registry);
	}
}
