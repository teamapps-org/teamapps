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
package org.teamapps.ux.servlet.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

public class ZlibStatefulDeflater {

	private final Deflater deflater = new Deflater();

	public byte[] deflate(byte[] data) {
		deflater.setInput(data);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] outputBuffer = new byte[Math.max(50, data.length)]; // must at least fit the fixed size zlib header + dictionary
		do {
			int compressedDataLength = deflater.deflate(outputBuffer, 0, outputBuffer.length, Deflater.SYNC_FLUSH);
			byteArrayOutputStream.write(outputBuffer, 0, compressedDataLength);
		} while (!deflater.needsInput());

		return byteArrayOutputStream.toByteArray();
	}

}
