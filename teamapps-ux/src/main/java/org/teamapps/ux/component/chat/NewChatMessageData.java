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
package org.teamapps.ux.component.chat;

import java.util.List;

public class NewChatMessageData {

	private final String text;
	private final List<UploadedFile> files;

	public NewChatMessageData(String text, List<UploadedFile> files) {
		this.text = text;
		this.files = files;
	}

	public String getText() {
		return text;
	}

	public List<UploadedFile> getFiles() {
		return files;
	}

	public static class UploadedFile {
		private final String uploadedFileUuid;
		private final String fileName;

		public UploadedFile(String uploadedFileUuid, String fileName) {
			this.uploadedFileUuid = uploadedFileUuid;
			this.fileName = fileName;
		}

		public String getUploadedFileUuid() {
			return uploadedFileUuid;
		}

		public String getFileName() {
			return fileName;
		}
	}
}
