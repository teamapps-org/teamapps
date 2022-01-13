/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.ux.component.field.upload;

import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

public class UploadedFile {

	private final String uuid;
	private final String name;
	private final long sizeInBytes;
	private final String mimeType;
	private final Supplier<InputStream> inputStreamSupplier;
	private final Supplier<File> fileSupplier;

	public UploadedFile(String uuid, String name, long sizeInBytes, String mimeType, Supplier<InputStream> inputStreamSupplier, Supplier<File> fileSupplier) {
		this.uuid = uuid;
		this.name = name;
		this.sizeInBytes = sizeInBytes;
		this.mimeType = mimeType;
		this.inputStreamSupplier = inputStreamSupplier;
		this.fileSupplier = fileSupplier;
	}

	public InputStream getAsInputStream() throws UploadedFileAccessException {
		return inputStreamSupplier.get();
	}

	public File getAsFile() {
		return fileSupplier.get();
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Supplier<InputStream> getInputStreamSupplier() {
		return inputStreamSupplier;
	}

	public Supplier<File> getFileSupplier() {
		return fileSupplier;
	}

	public InputStream createInputStream() {
		return inputStreamSupplier.get();
	}
}
