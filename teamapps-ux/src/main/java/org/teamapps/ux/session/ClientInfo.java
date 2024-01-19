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
package org.teamapps.ux.session;

import org.teamapps.dto.UiClientInfo;
import org.teamapps.ux.session.navigation.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientInfo {

	private final String ip;
	private final int screenWidth;
	private final int screenHeight;
	private final int viewPortWidth;
	private final int viewPortHeight;
	private final String preferredLanguageIso;
	private final boolean highDensityScreen;
	private final String timeZone;
	private final int timeZoneOffsetMinutes;
	private final Set<String> clientTokens;
	private final String userAgent;
	private final Location location;
	private final Map<String, Object> clientParameters;
	private final String teamAppsVersion;

	private ClientUserAgent userAgentData;
	private ClientGeoIpInfo geoIpInfo;


	public ClientInfo(String ip, int screenWidth, int screenHeight, int viewPortWidth, int viewPortHeight,
					  String preferredLanguageIso, boolean highDensityScreen, String timeZone, int timeZoneOffsetMinutes, List<String> clientTokens, String userAgent,
					  Location location, Map<String, Object> clientParameters, String teamAppsVersion) {
		this.ip = ip;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.viewPortWidth = viewPortWidth;
		this.viewPortHeight = viewPortHeight;
		this.preferredLanguageIso = preferredLanguageIso;
		this.highDensityScreen = highDensityScreen;
		this.timeZone = timeZone;
		this.timeZoneOffsetMinutes = timeZoneOffsetMinutes;
		this.clientTokens = new HashSet<>(clientTokens);
		this.userAgent = userAgent;
		this.location = location;
		this.clientParameters = clientParameters;
		this.teamAppsVersion = teamAppsVersion;
	}

	public static ClientInfo fromUiClientInfo(UiClientInfo uiClientInfo) {
		return new ClientInfo(
				uiClientInfo.getIp(),
				uiClientInfo.getScreenWidth(),
				uiClientInfo.getScreenHeight(),
				uiClientInfo.getViewPortWidth(),
				uiClientInfo.getViewPortHeight(),
				uiClientInfo.getPreferredLanguageIso(),
				uiClientInfo.getHighDensityScreen(),
				uiClientInfo.getTimezoneIana(),
				uiClientInfo.getTimezoneOffsetMinutes(),
				uiClientInfo.getClientTokens(),
				uiClientInfo.getUserAgentString(),
				Location.fromUiLocation(uiClientInfo.getLocation()),
				uiClientInfo.getClientParameters(),
				uiClientInfo.getTeamAppsVersion());
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

	public ClientGeoIpInfo getGeoIpInfo() {
		return geoIpInfo;
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

	public String getPreferredLanguageIso() {
		return preferredLanguageIso;
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

	public Location getLocation() {
		return location;
	}

	public String getClientUrl() {
		return location.getHref();
	}

	public Map<String, Object> getClientParameters() {
		return clientParameters;
	}

	public ClientUserAgent getUserAgentData() {
		return userAgentData;
	}

	public void setUserAgentData(ClientUserAgent userAgentData) {
		this.userAgentData = userAgentData;
	}

	public void setGeoIpInfo(ClientGeoIpInfo geoIpInfo) {
		this.geoIpInfo = geoIpInfo;
	}
}
