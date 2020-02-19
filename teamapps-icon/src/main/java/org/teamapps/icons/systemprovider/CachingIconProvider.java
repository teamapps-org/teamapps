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
package org.teamapps.icons.systemprovider;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class CachingIconProvider {

	private File cacheDirectory;

	public CachingIconProvider()  {
		try {
			cacheDirectory = File.createTempFile("icon-cache", "temp").getParentFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CachingIconProvider(File cacheDirectory) {
		this.cacheDirectory = cacheDirectory;
	}

	public byte[] getCachedIcon(int size, String iconId) {
		File icon = createFilePath(size, iconId);
		return getCachedFile(icon);
	}

	public byte[] getCachedIcon(String libraryId, String styleId, int size, String iconName) {
		File icon = createFilePath(libraryId, styleId, size, iconName);
		return getCachedFile(icon);
	}

	private byte[] getCachedFile(File file) {
		if (file.exists()) {
			try {
				return IOUtils.toByteArray(new FileInputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean puIconInCache(int size, String iconId, byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return false;
		}
		File icon = createFilePath(size, iconId);
		return writeCacheFile(icon, bytes);
	}

	public boolean putIconInCache(String libraryId, String styleId, int size, String iconName, byte[] bytes) {
		File icon = createFilePath(libraryId, styleId, size, iconName);
		return writeCacheFile(icon, bytes);
	}

	private boolean writeCacheFile(File file, byte[] bytes) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private File createFilePath(String libraryId, String styleId, int size, String iconName) {
		return new File(cacheDirectory, libraryId + "." + styleId + "." + size + "." + iconName + ".png");
	}

	private File createFilePath(int size, String iconId) {
		return new File(cacheDirectory, size + "." + iconId + ".png");
	}
}
