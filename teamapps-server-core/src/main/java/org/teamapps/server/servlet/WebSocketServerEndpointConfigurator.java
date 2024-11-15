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

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

public class WebSocketServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String SERVLET_CONTEXT_PROPERTY_NAME = "SERVLET_CONTEXT";
	public static final String HTTP_SESSION_PROPERTY_NAME = "HTTP_SESSION";
	public static final String USER_AGENT_PROPERTY_NAME = "USER_AGENT";
	public static final String LANGUAGE_PROPERTY_NAME = "LANGUAGE";
	public static final String CLIENT_IP_PROPERTY_NAME = "CLIENT_IP";
	private final WebSocketCommunicationEndpoint webSocketCommunicationEndpoint;

	public WebSocketServerEndpointConfigurator(WebSocketCommunicationEndpoint webSocketCommunicationEndpoint) {
		this.webSocketCommunicationEndpoint = webSocketCommunicationEndpoint;
	}

	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		String userAgentString = getFirstHeaderOrNull(request, "User-Agent");
		String acceptLanguageHeader = getFirstHeaderOrNull(request, "Accept-Language");
		if (acceptLanguageHeader == null) {
			acceptLanguageHeader = "en";
		}
		String languageString = Locale.LanguageRange.parse(acceptLanguageHeader).stream()
				.map(range -> new Locale(range.getRange()))
				.findFirst()
				.map(locale -> locale.getLanguage())
				.orElse(null);

		String proxiedIp = getFirstHeaderOrNull(request, "X-Forwarded-For");

		sec.getUserProperties().put(SERVLET_CONTEXT_PROPERTY_NAME, httpSession.getServletContext());
		sec.getUserProperties().put(HTTP_SESSION_PROPERTY_NAME, httpSession);
		sec.getUserProperties().put(USER_AGENT_PROPERTY_NAME, userAgentString);
		sec.getUserProperties().put(LANGUAGE_PROPERTY_NAME, languageString);
		sec.getUserProperties().put(CLIENT_IP_PROPERTY_NAME, proxiedIp != null ? proxiedIp : httpSession.getAttribute(CLIENT_IP_PROPERTY_NAME));
	}

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		return (T) webSocketCommunicationEndpoint;
	}

	private String getFirstHeaderOrNull(HandshakeRequest request, String headerName) {
		List<String> headers = request.getHeaders().get(headerName);
		if (headers == null || headers.isEmpty()) {
			return null;
		} else {
			return headers.get(0);
		}
	}
}
