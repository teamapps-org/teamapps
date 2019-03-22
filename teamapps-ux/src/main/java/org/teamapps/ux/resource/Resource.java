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
package org.teamapps.ux.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface Resource {

	InputStream getInputStream();

	default long getLength() {
		try {
			InputStream inputStream = getInputStream();
			byte[] buf = new byte[4096];
			int totalLength = 0;
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				totalLength += len;
			}
			return totalLength;
		} catch (IOException e) {
			return 0;
		}
	}

	default String getName() {
		return null;
	}

	default Date getLastModified() {
		return new Date(); // no caching
	}

	default Date getExpires() {
		return new Date(System.currentTimeMillis() + 604800000L);
	}

	default String getMimeType() {
		return null;
	}

	/**
	 * @return true if this resource should be handled by the browser by showing a "Save As" dialogue (HTTP header Content-Disposition: attachment),
	 * false if the browser should attempt to embed or display the resource directly (HTTP header Content-Disposition: inline).
	 */
	default boolean isAttachment() {
		return false;
	}

}
