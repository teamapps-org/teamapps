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
package org.teamapps.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ClassPathResourceUtil {

	private ClassPathResourceUtil() {}

	public static String readResourceToString(String resourceName) {
		InputStream is = getResourceAsStream(resourceName);
		if (is != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		} else {
			throw new IllegalArgumentException("Could not find resource: " + resourceName);
		}
	}

	public static InputStream getResourceAsStream(String resourceName) {
		InputStream is = ClassPathResourceUtil.class.getClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			is = ClassLoader.getSystemResourceAsStream(resourceName);
		}
		return is;
	}

	public static byte[] readResourceToByteArray(String resourceName) {
		return readInputStreamToByteArray(getResourceAsStream(resourceName));
	}

	public static byte[] readInputStreamToByteArray(InputStream is) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			is.transferTo(buffer);
			return buffer.toByteArray();
		} catch (Exception e) {
			throw ExceptionUtil.softenedException(e);
		}
	}
}
