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
package org.teamapps.ux.servlet;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class TeamAppsWebSocketMetrics implements MeterBinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WebSocketCommunicationEndpoint webSocketCommunicationEndpoint;

	public TeamAppsWebSocketMetrics(WebSocketCommunicationEndpoint webSocketCommunicationEndpoint) {
		this.webSocketCommunicationEndpoint = webSocketCommunicationEndpoint;
	}

	@Override
	public void bindTo(MeterRegistry registry) {
		Gauge.builder("teamapps.websocket.chars.sent", webSocketCommunicationEndpoint, WebSocketCommunicationEndpoint::getTotalSendCount)
				.description("Total number of characters (uncompressed) sent through websocket to all clients.")
				.register(registry);
		Gauge.builder("teamapps.websocket.chars.received", webSocketCommunicationEndpoint, WebSocketCommunicationEndpoint::getTotalReceiveCount)
				.description("Total number of characters (uncompressed) received through websocket to all clients.")
				.register(registry);
	}

}
