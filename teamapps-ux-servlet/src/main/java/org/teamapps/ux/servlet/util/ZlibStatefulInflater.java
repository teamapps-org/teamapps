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
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ZlibStatefulInflater {

	private Inflater inflater = new Inflater();

	public byte[] inflate(byte[] data) {
		inflater.setInput(data);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] outputBuffer = new byte[data.length * 2];
		do {
			int decompressedLength;
			try {
				decompressedLength = inflater.inflate(outputBuffer, 0, outputBuffer.length);
			} catch (DataFormatException e) {
				throw new RuntimeException(e);
			}
			byteArrayOutputStream.write(outputBuffer, 0, decompressedLength);
		} while (!inflater.needsInput());
		return byteArrayOutputStream.toByteArray();
	}

}
