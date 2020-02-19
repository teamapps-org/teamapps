/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.config;

import org.teamapps.event.Event;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.ux.session.SessionContext;

/**
 * TODO Explain difference between UI session and HTTP session.
 * TODO Describe UI protocol and back pressure.
 */
public class TeamAppsConfiguration {

	/**
	 * The timeout after which the UX server regards a UI session as obsolete.
	 * This happens when the client stops sending keepalive messages or events.
	 * This may be caused by the browser tab being closed or the internet connection being interrupted.
	 * <p>
	 * When a UI session times out, the corresponding {@link SessionContext} gets closed
	 * and removed from the map of known {@link QualifiedUiSessionId}s.
	 * <p>
	 * The {@link SessionContext} will also fire its
	 * {@link SessionContext#onDestroyed onDestroyed} event, which will detach any {@link Event SessionContext-bound event} listeners.
	 * <p>
	 * From a user's perspective, the session is expired and the user most likely needs to reload the page (unless the developer
	 * has implemented an alternative handling on the client side).
	 * <p>
	 * NOTE that UI sessions might not be destroyed "punctually". The interval at which the session states are updated is
	 * the {@link #keepaliveMessageIntervalMillis}.
	 *
	 * @see #keepaliveMessageIntervalMillis
	 */
	private long uiSessionTimeoutMillis = 30 * 60_000;

	/**
	 * The timeout after which sessions are regarded as "inactive".
	 * <p>
	 * While this does not have any direct effects inside the TeamApps framework, the "activity state" can be monitored
	 * on the application level (see {@link SessionContext#onActivityStateChanged}). For instance, this can be used to mark an application user
	 * as "away".
	 */
	private long uiSessionInactivityTimeoutMillis = 60_000;

	/**
	 * The interval at which the client sends keep alive messages.
	 *
	 * @see #uiSessionTimeoutMillis
	 */
	private long keepaliveMessageIntervalMillis = 25_000;

	/**
	 * Will overwrite the servlet container's HTTP session timeout.
	 * Note that this is important, since the TeamApps client protocol does not rely on HTTP requests but on WebSocket communication
	 * and the interchange of WebSocket messages will NOT keep the HTTP session alive.
	 * <p>
	 * TeamApps does currently not provide a HTTP keep-alive request mechanism, so you might want to set this to a high value.
	 * <p>
	 * NOTE that TeamApps will get independent of HTTP sessions. So this configuration will be obsolete very soon.
	 */
	private int httpSessionTimeoutSeconds = 24 * 3600;

	/**
	 * The number of UI commands for a client that will be buffered on the server side before the UI session will be closed.
	 */
	private int commandBufferSize = 5_000;

	/**
	 * This is a client back pressure protocol parameter.
	 * The number of remaining requested commands at which the client will request new commands from the server.
	 * Intuitively, this should be 0, so when the client has no more commands requested from the server, it will do the request.
	 * However, for performance reasons, this value should be set to something greater than 0, so the client can anticipate
	 * commands being sent from the server and prevent the server from buffering commands.
	 */
	private int clientMinRequestedCommands = 3;

	/**
	 * This is a client back pressure protocol parameter.
	 * The maximum number of commands a client will request from a server.
	 */
	private int clientMaxRequestedCommands = 20;

	/**
	 * The number of client/UI events the client will buffer before invalidating its connection (buffer overflow on the client side).
	 */
	private int clientEventsBufferSize = 500;

	public TeamAppsConfiguration() {
	}

	/**
	 * @see #uiSessionTimeoutMillis
	 */
	public long getUiSessionTimeoutMillis() {
		return uiSessionTimeoutMillis;
	}

	/**
	 * @see #uiSessionTimeoutMillis
	 */
	public void setUiSessionTimeoutMillis(long uiSessionTimeoutMillis) {
		this.uiSessionTimeoutMillis = uiSessionTimeoutMillis;
	}

	/**
	 * @see #uiSessionInactivityTimeoutMillis
	 */
	public long getUiSessionInactivityTimeoutMillis() {
		return uiSessionInactivityTimeoutMillis;
	}

	/**
	 * @see #uiSessionInactivityTimeoutMillis
	 */
	public void setUiSessionInactivityTimeoutMillis(long uiSessionInactivityTimeoutMillis) {
		this.uiSessionInactivityTimeoutMillis = uiSessionInactivityTimeoutMillis;
	}

	/**
	 * @see #keepaliveMessageIntervalMillis
	 */
	public long getKeepaliveMessageIntervalMillis() {
		return keepaliveMessageIntervalMillis;
	}

	/**
	 * @see #keepaliveMessageIntervalMillis
	 */
	public void setKeepaliveMessageIntervalMillis(long keepaliveMessageIntervalMillis) {
		this.keepaliveMessageIntervalMillis = keepaliveMessageIntervalMillis;
	}

	/**
	 * @see #httpSessionTimeoutSeconds
	 */
	public int getHttpSessionTimeoutSeconds() {
		return httpSessionTimeoutSeconds;
	}

	/**
	 * @see #httpSessionTimeoutSeconds
	 */
	public void setHttpSessionTimeoutSeconds(int httpSessionTimeoutSeconds) {
		this.httpSessionTimeoutSeconds = httpSessionTimeoutSeconds;
	}

	/**
	 * @see #commandBufferSize
	 */
	public int getCommandBufferSize() {
		return commandBufferSize;
	}

	/**
	 * @see #commandBufferSize
	 */
	public void setCommandBufferSize(int commandBufferSize) {
		this.commandBufferSize = commandBufferSize;
	}

	/**
	 * @see #clientMinRequestedCommands
	 */
	public int getClientMinRequestedCommands() {
		return clientMinRequestedCommands;
	}

	/**
	 * @see #clientMinRequestedCommands
	 */
	public void setClientMinRequestedCommands(int clientMinRequestedCommands) {
		this.clientMinRequestedCommands = clientMinRequestedCommands;
	}

	/**
	 * @see #clientMaxRequestedCommands
	 */
	public int getClientMaxRequestedCommands() {
		return clientMaxRequestedCommands;
	}

	/**
	 * @see #clientMaxRequestedCommands
	 */
	public void setClientMaxRequestedCommands(int clientMaxRequestedCommands) {
		this.clientMaxRequestedCommands = clientMaxRequestedCommands;
	}

	/**
	 * @see #clientEventsBufferSize
	 */
	public int getClientEventsBufferSize() {
		return clientEventsBufferSize;
	}

	/**
	 * @see #clientEventsBufferSize
	 */
	public void setClientEventsBufferSize(int clientEventsBufferSize) {
		this.clientEventsBufferSize = clientEventsBufferSize;
	}
}
