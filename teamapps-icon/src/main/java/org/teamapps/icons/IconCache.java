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
package org.teamapps.icons;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IconCache {

	private final File cacheDirectory;
	private final Map<EncodedIconStringAndSize, File> cachedFilesByEncodedIconString = new ConcurrentHashMap<>();

	public IconCache() {
		try {
			cacheDirectory = File.createTempFile("icon-cache", "temp").getParentFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IconCache(File cacheDirectory) {
		this.cacheDirectory = cacheDirectory;
	}

	public IconResource getCachedIcon(String encodedIconString, int size) {
		File file = cachedFilesByEncodedIconString.get(new EncodedIconStringAndSize(encodedIconString, size));
		return file != null ? readFromFile(file) : null;
	}

	public void putIcon(String encodedIconString, int size, IconResource iconResource) {
		if (iconResource == null || iconResource.getBytes().length == 0) {
			return;
		}
		File file = new File(cacheDirectory, UUID.randomUUID().toString());
		writeToFile(iconResource, file);
		cachedFilesByEncodedIconString.put(new EncodedIconStringAndSize(encodedIconString, size), file);
	}

	private static IconResource readFromFile(File file) {
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				int typeLength = inputStream.read();
				String typeString = new String(inputStream.readNBytes(typeLength), StandardCharsets.US_ASCII);
				IconType iconType = IconType.valueOf(typeString);
				byte[] iconBytes = IOUtils.toByteArray(inputStream);
				return new IconResource(iconBytes, iconType);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private boolean writeToFile(IconResource iconResource, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(iconResource.getIconType().name().length());
			fos.write(iconResource.getIconType().name().getBytes(StandardCharsets.US_ASCII));
			fos.write(iconResource.getBytes());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static class EncodedIconStringAndSize {
		String encodedIconString;
		int size;

		public EncodedIconStringAndSize(String encodedIconString, int size) {
			this.encodedIconString = encodedIconString;
			this.size = size;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			EncodedIconStringAndSize that = (EncodedIconStringAndSize) o;
			return size == that.size &&
					Objects.equals(encodedIconString, that.encodedIconString);
		}

		@Override
		public int hashCode() {
			return Objects.hash(encodedIconString, size);
		}
	}
}
