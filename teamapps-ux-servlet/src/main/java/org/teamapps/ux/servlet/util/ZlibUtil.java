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
package org.teamapps.ux.servlet.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class ZlibUtil {

	public static byte[] deflateString(String string) {
		return deflate(string.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] deflate(byte[] dataToCompress) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(dataToCompress.length / 3);
			try (DeflaterOutputStream deflateStream = new DeflaterOutputStream(byteOutStream)) {
				deflateStream.write(dataToCompress);
			}
			return byteOutStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String inflateToString(byte[] input) {
		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(input.length * 5);
			try (InflaterOutputStream deflateStream = new InflaterOutputStream(byteOutStream)) {
				deflateStream.write(input);
			}
			byte[] bytes = byteOutStream.toByteArray();
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
