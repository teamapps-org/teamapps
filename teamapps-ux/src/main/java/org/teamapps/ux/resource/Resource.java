/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.function.Supplier;

public interface Resource {

	Logger LOGGER = LoggerFactory.getLogger(Resource.class);

	InputStream getInputStream();

	default long getLength() {
		try {
			InputStream inputStream = getInputStream();
			if (inputStream == null) {
				throw new IOException("Cannot get length of null resource: " + toString());
			}
			byte[] buf = new byte[4096];
			int totalLength = 0;
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				totalLength += len;
			}
			return totalLength;
		} catch (IOException e) { // TODO change this to actually throw the IOException (see below)
			LOGGER.error("Exception while calculating resource length: " + toString(), e);
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
		return new Date(System.currentTimeMillis() + 604800000L); // one week
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

	default File getAsFile() {
		try {
			String name = getName() != null ? getName() : ".bin";
			File tempFile = File.createTempFile("temp", name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
			IOUtils.copyLarge(getInputStream(), bos);
			bos.close();
			return tempFile;
		} catch (IOException e) { // TODO change this to actually throw the IOException. Returning null is much more dangerous (NPE) than throwing a caught exception!
			LOGGER.error("Exception while writing resource to file: " + toString(), e);
			e.printStackTrace();
		}
		return null;
	}

	// util

	default Resource lastModified(Date lastModifiedDate) {
		return new ResourceWrapper(this) {
			@Override
			public Date getLastModified() {
				return lastModifiedDate;
			}
		};
	}

	default Resource expiring(Date expiryDate) {
		return new ResourceWrapper(this) {
			@Override
			public Date getExpires() {
				return expiryDate;
			}
		};
	}

	default Resource withMimeType(String mimeType) {
		return new ResourceWrapper(this) {
			@Override
			public String getMimeType() {
				return mimeType;
			}
		};
	}

	default Resource asAttachment(boolean attachment) {
		return new ResourceWrapper(this) {
			@Override
			public boolean isAttachment() {
				return attachment;
			}
		};
	}

}
