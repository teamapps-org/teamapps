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
package org.teamapps.ux.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.DtoSessionClosingReason;
import org.teamapps.uisession.TeamAppsSessionManager;
import org.teamapps.uisession.UiSession;

import java.io.IOException;

public class LeaveBeaconServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRequestListener.class);
	private final TeamAppsSessionManager uiSessionManager;

	public LeaveBeaconServlet(TeamAppsSessionManager uiSessionManager) {
		this.uiSessionManager = uiSessionManager;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uiSessionId = IOUtils.toString(request.getReader());
		LOGGER.info("Got leaving beacon for teamapps session id: " + uiSessionId);
		UiSession uiSession = uiSessionManager.getUiSessionById(uiSessionId);
		if (uiSession != null) {
			uiSession.close(DtoSessionClosingReason.TERMINATED_BY_CLIENT);
		}
	}
}
