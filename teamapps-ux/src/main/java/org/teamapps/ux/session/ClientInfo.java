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
package org.teamapps.ux.session;

import java.util.List;
import java.util.Map;

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
	private final List<String> clientTokens;
	private final String userAgent;
	private final String clientUrl;
	private final Map<String, Object> clientParameters;
	private ClientUserAgent userAgentData;
	private ClientGeoIpInfo geoIpInfo;


	public ClientInfo(String ip, int screenWidth, int screenHeight, int viewPortWidth, int viewPortHeight,
	                  String preferredLanguageIso, boolean highDensityScreen, String timeZone, int timeZoneOffsetMinutes, List<String> clientTokens, String userAgent,
	                  String clientUrl, Map<String, Object> clientParameters) {
		this.ip = ip;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.viewPortWidth = viewPortWidth;
		this.viewPortHeight = viewPortHeight;
		this.preferredLanguageIso = preferredLanguageIso;
		this.highDensityScreen = highDensityScreen;
		this.timeZone = timeZone;
		this.timeZoneOffsetMinutes = timeZoneOffsetMinutes;
		this.clientTokens = clientTokens;
		this.userAgent = userAgent;
		this.clientUrl = clientUrl;
		this.clientParameters = clientParameters;
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

	public List<String> getClientTokens() {
		return clientTokens;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getClientUrl() {
		return clientUrl;
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
