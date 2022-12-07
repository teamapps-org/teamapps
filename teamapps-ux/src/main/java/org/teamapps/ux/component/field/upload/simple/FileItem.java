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

package org.teamapps.ux.component.field.upload.simple;

import org.teamapps.dto.DtoFileItem;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.session.CurrentSessionContext;

import java.io.File;
import java.util.UUID;

public class FileItem {

	public final ProjectorEvent<Void> onClicked = new ProjectorEvent<>();
	public final ProjectorEvent<Void> onRemoved = new ProjectorEvent<>();

	private final String uuid;
	private FileItemState state;
	private SimpleFileField fileField;

	private Icon<?, ?> icon;
	private String thumbnailUrl;
	private String fileName;
	private String description;
	private long size;
	private String linkUrl;
	private File file;

	public FileItem(String fileName, String description, long size) {
		this.uuid = UUID.randomUUID().toString();
		this.fileName = fileName;
		this.description = description;
		this.size = size;
	}

	/*package-private*/ FileItem(String uuid, String fileName, FileItemState state, String description, long size) {
		this.uuid = uuid;
		this.fileName = fileName;
		this.state = state;
		this.description = description;
		this.size = size;
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		updateClientSideDisplay();
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
		updateClientSideDisplay();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		updateClientSideDisplay();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		updateClientSideDisplay();
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
		updateClientSideDisplay();
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
		updateClientSideDisplay();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	private void updateClientSideDisplay() {
		this.fileField.handleFileItemChanged(this);
	}

	public void setDisplayData(String caption, String description, long size) {
		this.fileName = caption;
		this.description = description;
		this.size = size;
		updateClientSideDisplay();
	}

	/*package-private*/ String getUuid() {
		return uuid;
	}

	public FileItemState getState() {
		return state;
	}

	/*package-private*/  void setState(FileItemState state) {
		this.state = state;
	}

	/*package-private*/ void setFileField(SimpleFileField fileField) {
		this.fileField = fileField;
	}

	public DtoFileItem createUiFileItem() {
		DtoFileItem uiFileItem = new DtoFileItem();
		uiFileItem.setUuid(uuid);
		uiFileItem.setFileName(fileName);
		uiFileItem.setDescription(description);
		uiFileItem.setSize(size);
		uiFileItem.setLinkUrl(linkUrl);
		uiFileItem.setIcon(CurrentSessionContext.get().resolveIcon(icon));
		uiFileItem.setThumbnail(thumbnailUrl);
		return uiFileItem;
	}
}
