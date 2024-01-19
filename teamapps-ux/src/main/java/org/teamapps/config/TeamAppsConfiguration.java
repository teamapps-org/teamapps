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
package org.teamapps.config;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionListener;
import org.teamapps.event.Event;
import org.teamapps.util.threading.SequentialExecutorFactory;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.navigation.RouteHandler;

import java.io.File;

/**
 * TODO Explain difference between UI session and HTTP session.
 * TODO Describe UI protocol and back pressure.
 */
public class TeamAppsConfiguration {

	/**
	 * The timeout after which the UX server regards a UI session as obsolete.
	 * This happens when the client stops sending messages to the server (especially KEEPALIVE messages, but also normal messages).
	 * This may be caused by the browser tab being closed or the internet connection being interrupted.
	 * <p>
	 * When a UI session times out, the corresponding {@link SessionContext} gets closed
	 * and removed from the map of known {@link String}s.
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
	 * The timeout after which clients not sending any messages to the server anymore (including KEEPALIVE) are regarded as "inactive".
	 * <p>
	 * While this does not have any direct effects inside the TeamApps framework, the "activity state" can be monitored
	 * on the application level (see {@link SessionContext#onActivityStateChanged}). For instance, this can be used to mark an application user
	 * as "away".
	 * <p>
	 * The default value of 75 seconds has been chosen because browsers tend to optimize inactive tabs.
	 * In Chrome for example, timers (e.g. for periodically sending KEEPALIVE messages) will be triggered only every 60 seconds
	 * regardless of the actual scheduled interval.
	 * <p>
	 * Also note that, when a client did not send a KEEPALIVE message for uiSessionInactivityTimeoutMillis - uiSessionPreInactivityPingMillis,
	 * the server will send a PING message to the client, which the client should respond to with a KEEPALIVE message.
	 * This circumvents client-side resource consumption optimizations, since the client is actively triggered by the server
	 * instead of waiting for a timeout.
	 */
	private long uiSessionInactivityTimeoutMillis = 75_000;

	/**
	 * Before setting a session inactive (after uiSessionInactivityTimeoutMillis without any sign from the client), the server will attempt
	 * to send a PING message to the client, in order to actively request a KEEPALIVE from it.
	 * <p>
	 * This specifies the amount of milliseconds before setting the client inactive that the server will send the PING.
	 * <p>
	 * By default, this is 10 seconds, so the server will wait (uiSessionInactivityTimeoutMillis - 10s) before sending the PING.
	 */
	private long uiSessionPreInactivityPingMillis = 10_000;

	/**
	 * The interval at which the client sends keep alive messages.
	 * <p>
	 * Not that this interval should be less than uiSessionInactivityTimeoutMillis / 2.
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
	 *
	 * @deprecated TeamApps does not care about HTTP sessions anymore. Please set the http session timeout in a different way.
	 * This will be removed in some future version.
	 * The cleanest way to set {@link HttpSession#setMaxInactiveInterval(int)} would be by registering a
	 * {@link HttpSessionListener}.
	 * Also note that the {@link HttpSession} is still available via {@link SessionContext#getHttpSession()}.
	 */
	@Deprecated
	private int httpSessionTimeoutSeconds = 24 * 3600;

	/**
	 * The number of UI commands for a client that will be buffered on the server side before the UI session will be closed.
	 */
	private int commandBufferLength = 5_000;

	/**
	 * The total number of characters that all commands in the command buffer of a session may hold. This is NOT the same as
	 * {@link #commandBufferLength}, which specifies the maximum <i>number</i> of commands that the buffer may hold.
	 * <p>
	 * Keep in mind that every character in Java takes 1 or 2 bytes of heap space (prior Java 9 always 2 bytes).
	 */
	private int commandBufferTotalSize = 5_000_000;

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

	/**
	 * The maximum size that client websocket messages may have (basically corresponding to individual client events).
	 * If you plan having MultiLineTextFields with huge amounts of text, increase this.
	 */
	private int maxUiClientMessageSize = 1024 * 1024;

	/**
	 * The directory that uploaded files should get stored to (see UploadServlet).
	 */
	private File uploadDirectory = new File(System.getProperty("java.io.tmpdir"));

	/**
	 * Max number of threads that should be used for changing session state.
	 * Note that you can also choose to write an own {@link SequentialExecutorFactory},
	 * in which case this
	 *
	 * @see org.teamapps.uisession.TeamAppsSessionManager#sessionExecutorFactory
	 */
	private int maxNumberOfSessionExecutorThreads = Runtime.getRuntime().availableProcessors() * 2;

	/**
	 * Path prefix to be ignored when routing and added when creating URLs.
	 *
	 * @see RouteHandler
	 */
	private String navigationPathPrefix = "";

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
	 * @see #uiSessionPreInactivityPingMillis
	 */
	public long getUiSessionPreInactivityPingMillis() {
		return uiSessionPreInactivityPingMillis;
	}

	/**
	 * @see #uiSessionPreInactivityPingMillis
	 */
	public void setUiSessionPreInactivityPingMillis(long uiSessionPreInactivityPingMillis) {
		this.uiSessionPreInactivityPingMillis = uiSessionPreInactivityPingMillis;
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
	 * @deprecated TeamApps does not care about HTTP sessions anymore. Please set the http session timeout in a different way.
	 * This will be removed in some future version.
	 */
	@Deprecated
	public int getHttpSessionTimeoutSeconds() {
		return httpSessionTimeoutSeconds;
	}

	/**
	 * @see #httpSessionTimeoutSeconds
	 * @deprecated TeamApps does not care about HTTP sessions anymore. Please set the http session timeout in a different way.
	 * This will be removed in some future version.
	 * The cleanest way to set {@link HttpSession#setMaxInactiveInterval(int)} would be by registering a
	 * {@link HttpSessionListener}.
	 * Also note that the {@link HttpSession} is still available via {@link SessionContext#getHttpSession()}.
	 */
	@Deprecated
	public void setHttpSessionTimeoutSeconds(int httpSessionTimeoutSeconds) {
		this.httpSessionTimeoutSeconds = httpSessionTimeoutSeconds;
	}

	/**
	 * @see #commandBufferLength
	 */
	public int getCommandBufferLength() {
		return commandBufferLength;
	}

	/**
	 * @see #commandBufferLength
	 */
	public void setCommandBufferLength(int commandBufferLength) {
		this.commandBufferLength = commandBufferLength;
	}

	/**
	 * @see #commandBufferTotalSize
	 */
	public int getCommandBufferTotalSize() {
		return commandBufferTotalSize;
	}

	/**
	 * @see #commandBufferTotalSize
	 */
	public void setCommandBufferTotalSize(int commandBufferTotalSize) {
		this.commandBufferTotalSize = commandBufferTotalSize;
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

	/**
	 * @see #maxUiClientMessageSize
	 */
	public int getMaxUiClientMessageSize() {
		return maxUiClientMessageSize;
	}

	/**
	 * @see #maxUiClientMessageSize
	 */
	public void setMaxUiClientMessageSize(int maxUiClientMessageSize) {
		this.maxUiClientMessageSize = maxUiClientMessageSize;
	}

	/**
	 * @see #uploadDirectory
	 */
	public File getUploadDirectory() {
		return uploadDirectory;
	}

	/**
	 * @see #uploadDirectory
	 */
	public void setUploadDirectory(File uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	/**
	 * @see #maxNumberOfSessionExecutorThreads
	 */
	public int getMaxNumberOfSessionExecutorThreads() {
		return maxNumberOfSessionExecutorThreads;
	}

	/**
	 * @see #maxNumberOfSessionExecutorThreads
	 */
	public void setMaxNumberOfSessionExecutorThreads(int maxNumberOfSessionExecutorThreads) {
		this.maxNumberOfSessionExecutorThreads = maxNumberOfSessionExecutorThreads;
	}

	public String getNavigationPathPrefix() {
		return navigationPathPrefix;
	}

	public void setNavigationPathPrefix(String navigationPathPrefix) {
		this.navigationPathPrefix = navigationPathPrefix;
	}
}
