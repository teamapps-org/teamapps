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