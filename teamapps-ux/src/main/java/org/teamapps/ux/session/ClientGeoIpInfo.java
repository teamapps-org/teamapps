/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

public class ClientGeoIpInfo {

	private final String countryIso;
	private final String country;
	private final String city;
	private final float latitude;
	private final float longitude;

	public ClientGeoIpInfo(String countryIso, String country, String city, float latitude, float longitude) {
		this.countryIso = countryIso;
		this.country = country;
		this.city = city;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}
}
