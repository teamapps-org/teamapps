/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.field.richtext;

public class ImageUploadSuccessfulEventData {

	private final String fileUuid;
	private final String fileName;
	private final String mimeType;
	private final long sizeInBytes;
	private final int incompleteUploadsCount;

	public ImageUploadSuccessfulEventData(String fileUuid, String fileName, String mimeType, long sizeInBytes, int incompleteUploadsCount) {
		this.fileUuid = fileUuid;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.sizeInBytes = sizeInBytes;
		this.incompleteUploadsCount = incompleteUploadsCount;
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public int getIncompleteUploadsCount() {
		return incompleteUploadsCount;
	}
}
