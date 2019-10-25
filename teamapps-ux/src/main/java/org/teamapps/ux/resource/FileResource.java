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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileResource implements Resource {

	private static final Map<String, String> MIMETYPES_BY_FILE_EXTENSION;

	private final File file;
	private final String name;

	static {
		try {
			MIMETYPES_BY_FILE_EXTENSION = new ObjectMapper().readValue(FileResource.class.getResource("mimetypes-by-file-extension.json"), HashMap.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public FileResource(File file) {
		this(file, file.getName());
	}

	public FileResource(File file, String name) {
		this.file = file;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getLength() {
		return file.length();
	}

	@Override
	public Date getLastModified() {
		return new Date(file.lastModified());
	}

	@Override
	public Date getExpires() {
		return new Date(System.currentTimeMillis() + 604800000L);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMimeType() {
		String fileType = getFileType();
		if (fileType == null) {
			return "application/octet-stream";
		}
		return MIMETYPES_BY_FILE_EXTENSION.getOrDefault(fileType, "application/octet-stream");
	}

	private String getFileType() {
		int pos = name.lastIndexOf('.');
		if (pos <= 0 || pos >= name.length() - 1) {
			return null;
		}
		return name.substring(pos + 1).toLowerCase();
	}

	public File getFile() {
		return file;
	}

	@Override
	public File getAsFile() {
		return file;
	}
}
