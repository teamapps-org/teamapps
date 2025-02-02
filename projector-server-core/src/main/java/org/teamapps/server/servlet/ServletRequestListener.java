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
package org.teamapps.server.servlet;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.http.HttpServletRequest;

public class ServletRequestListener implements jakarta.servlet.ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		HttpServletRequest servletRequest = (HttpServletRequest) sre.getServletRequest();
		servletRequest.getSession(true).setAttribute(WebSocketServerEndpointConfigurator.CLIENT_IP_PROPERTY_NAME, servletRequest.getRemoteAddr());
	}

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {

	}
}
