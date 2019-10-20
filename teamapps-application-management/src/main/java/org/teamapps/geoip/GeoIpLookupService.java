/*
 * Copyright (c) 2016 teamapps.org (see code comments for author's name)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teamapps.geoip;

import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.ux.session.ClientGeoIpInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeoIpLookupService {

	private static Logger logger = LoggerFactory.getLogger(GeoIpLookupService.class);

    private final String geoIpDatabasePath;
    private DatabaseReader databaseReader;
    private Map<String, CityResponse> resultCache;

    public GeoIpLookupService(String geoIpDatabasePath) throws IOException {
		this.geoIpDatabasePath = geoIpDatabasePath;
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(geoIpDatabasePath)));
		databaseReader = new DatabaseReader.Builder(inputStream)
				.fileMode(Reader.FileMode.MEMORY)
				.build();
        resultCache = new LinkedHashMap<>() {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, CityResponse> eldest) {
				return size() > 7500;
			}
		};
    }

    public ClientGeoIpInfo getClientGeoIpInfo(String ip) {
		GeoIpInfo ipInfo = getGeoIpInfo(ip);
		return new ClientGeoIpInfo(ipInfo.getCountryIso(), ipInfo.getCountry(), ipInfo.getCity(), ipInfo.getLatitude(), ipInfo.getLongitude());
	}

	public GeoIpInfo getGeoIpInfo(String ip) {
		GeoIpInfo info = new GeoIpInfo(ip);
		CityResponse response = readIpEntry(ip);
		if (response == null) {
			return info;
		}
		if (response.getCountry() != null) {
			String iso = response.getCountry().getIsoCode();
			String country = response.getCountry().getName();
			info.setCountry(country);
			info.setCountryIso(iso);
		}
		if (response.getCity() != null) {
			info.setCity(response.getCity().getName());
		}
		Location location = response.getLocation();
		if (location != null) {
			info.setLatitude(location.getLatitude().floatValue());
			info.setLongitude(location.getLongitude().floatValue());
		}
		return info;
	}

	private CityResponse readIpEntry(String ip) {
		if (ip == null) return null;
		CityResponse result = resultCache.get(ip);
		if (result == null) {
			try {
				CityResponse response = databaseReader.city(InetAddress.getByName(ip));
				resultCache.put(ip, response);
				return response;
			} catch (Exception e) {
				logger.warn("GEO-IP-ERROR:" + ip);
			}
		}
		return result;
	}




}
