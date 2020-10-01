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
package org.teamapps.ux.servlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.TeamAppsUiSessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LeaveBeaconServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRequestListener.class);
	private final TeamAppsUiSessionManager uiSessionManager;

	public LeaveBeaconServlet(TeamAppsUiSessionManager uiSessionManager) {
		this.uiSessionManager = uiSessionManager;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionId = IOUtils.toString(request.getReader());
		LOGGER.info("Got leaving beacon for SessionId: " + sessionId);
		uiSessionManager.closeSession(new QualifiedUiSessionId(request.getSession().getId(), sessionId), UiSessionClosingReason.TERMINATED_BY_CLIENT);
	}
}
