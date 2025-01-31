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
package org.teamapps.projector.session;

import java.net.URL;
import java.util.*;

public class ClientInfo {

	private final String ip;
	private final int screenWidth;
	private final int screenHeight;
	private final int viewPortWidth;
	private final int viewPortHeight;
	private final List<Locale> acceptedLanguages;
	private final boolean highDensityScreen;
	private final String timeZone;
	private final int timeZoneOffsetMinutes;
	private final Set<String> clientTokens;
	private final String userAgent;
	private final URL location;
	private final Map<String, String> clientParameters;
	private final String teamAppsVersion;

	public ClientInfo(String ip, int screenWidth, int screenHeight, int viewPortWidth, int viewPortHeight,
					  List<Locale> acceptedLanguages, boolean highDensityScreen, String timeZone, int timeZoneOffsetMinutes, List<String> clientTokens, String userAgent,
					  URL location, Map<String, String> clientParameters, String teamAppsVersion) {
		this.ip = ip;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.viewPortWidth = viewPortWidth;
		this.viewPortHeight = viewPortHeight;
		this.acceptedLanguages = acceptedLanguages;
		this.highDensityScreen = highDensityScreen;
		this.timeZone = timeZone;
		this.timeZoneOffsetMinutes = timeZoneOffsetMinutes;
		this.clientTokens = new HashSet<>(clientTokens);
		this.userAgent = userAgent;
		this.location = location;
		this.clientParameters = clientParameters;
		this.teamAppsVersion = teamAppsVersion;
	}

	public boolean isMobileDevice() {
		int screenResolution = Math.max(getScreenWidth(), getScreenHeight());
		if (screenResolution < 700) {
			return true;
		}
		return userAgent != null && userAgent.toLowerCase().contains(" mobile");
	}

	public String getIp() {
		return ip;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getViewPortWidth() {
		return viewPortWidth;
	}

	public int getViewPortHeight() {
		return viewPortHeight;
	}

	public List<Locale> getAcceptedLanguages() {
		return acceptedLanguages;
	}

	public boolean isHighDensityScreen() {
		return highDensityScreen;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public int getTimeZoneOffsetMinutes() {
		return timeZoneOffsetMinutes;
	}

	public Set<String> getClientTokens() {
		return clientTokens;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public URL getLocation() {
		return location;
	}

	public Map<String, String> getClientParameters() {
		return clientParameters;
	}

	public String getTeamAppsVersion() {
		return teamAppsVersion;
	}
}
