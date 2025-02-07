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

package org.teamapps.projector.component.filefield.simple;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.commons.formatter.FileSizeFormatter;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.filefield.*;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.i18n.ProjectorTranslationKeys;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(FileFieldLibrary.class)
public class SimpleFileField extends AbstractField<List<FileItem>> implements DtoSimpleFileFieldEventHandler {

	private final DtoSimpleFileFieldClientObjectChannel clientObjectChannel = new DtoSimpleFileFieldClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<FileItem> onUploadInitiatedByUser = new ProjectorEvent<>(clientObjectChannel::toggleUploadInitiatedByUserEvent);
	public final ProjectorEvent<FileItem> onUploadTooLarge = new ProjectorEvent<>(clientObjectChannel::toggleUploadTooLargeEvent);
	public final ProjectorEvent<FileItem> onUploadStarted = new ProjectorEvent<>(clientObjectChannel::toggleUploadStartedEvent);
	public final ProjectorEvent<FileItem> onUploadCanceledByUser = new ProjectorEvent<>(clientObjectChannel::toggleUploadCanceledEvent);
	public final ProjectorEvent<FileItem> onUploadFailed = new ProjectorEvent<>(clientObjectChannel::toggleUploadFailedEvent);
	public final ProjectorEvent<FileItem> onUploadSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleUploadSuccessfulEvent);
	public final ProjectorEvent<FileItem> onFileItemClicked = new ProjectorEvent<>(clientObjectChannel::toggleFileItemClickedEvent);
	public final ProjectorEvent<FileItem> onFileItemRemoved = new ProjectorEvent<>(clientObjectChannel::toggleFileItemRemovedEvent);

	private final List<FileItem> fileItems = new ArrayList<>();

	private FileFieldDisplayType displayType = FileFieldDisplayType.FLOATING;
	private int maxFiles = Integer.MAX_VALUE;
	private long maxBytesPerFile = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private Icon browseButtonIcon = MaterialIcon.FILE_UPLOAD;
	private final Icon defaultItemIcon = MaterialIcon.CARD_TRAVEL;

	private String browseButtonCaption = getSessionContext().getLocalized(ProjectorTranslationKeys.UPLOAD.getKey());

	public void addFileItem(FileItem fileItem) {
		fileItem.setState(FileItemState.DONE);
		this.fileItems.add(fileItem);
		fileItem.setFileField(this);
		clientObjectChannel.addFileItem(fileItem.createDtoFileItem());
	}

	public void removeFileItem(FileItem fileItem) {
		removeFileItemInternal(fileItem);
		clientObjectChannel.removeFileItem(fileItem.getUuid());
	}

	private void removeFileItemInternal(FileItem fileItem) {
		this.fileItems.remove(fileItem);
		fileItem.setFileField(null);
	}

	/*package-private*/  void handleFileItemChanged(FileItem fileItem) {
		clientObjectChannel.updateFileItem(fileItem.createDtoFileItem());
	}

	private FileItem getFileItemByUuid(String uuid) {
		return fileItems.stream()
				.filter(item -> item.getUuid().equals(uuid))
				.findFirst().orElse(null);
	}

	@Override
	public List<FileItem> doConvertClientValueToServerValue(@Nonnull JsonNode value) {
		return fileItems; // the list is up to date anyways!
	}

	@Override
	public List<FileItem> getValue() {
		return fileItems;
	}

	@Override
	public boolean isEmptyValue(List<FileItem> value) {
		return value.isEmpty();
	}

	@Override
	public DtoComponentConfig createDto() {
		DtoSimpleFileField field = new DtoSimpleFileField();
		mapAbstractFieldAttributesToUiField(field);
		field.setBrowseButtonIcon(getSessionContext().resolveIcon(browseButtonIcon));
		field.setBrowseButtonCaption(browseButtonCaption);
		field.setUploadUrl(uploadUrl);
		field.setMaxBytesPerFile(maxBytesPerFile);
		field.setFileTooLargeMessage(getSessionContext().getLocalized(ProjectorTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		field.setUploadErrorMessage(getSessionContext().getLocalized(ProjectorTranslationKeys.UPLOAD_ERROR_MESSAGE.getKey()));
		field.setMaxFiles(maxFiles);
		field.setDisplayMode(displayType);
		field.setFileItems(fileItems.stream()
				.map(fi -> fi.createDtoFileItem())
				.collect(Collectors.toList()));
		return field;
	}

	@Override
	public void handleUploadInitiatedByUser(DtoSimpleFileField.UploadInitiatedByUserEventWrapper initEvent) {
		FileItem fileItem = new FileItem(initEvent.getUuid(), initEvent.getFileName(), FileItemState.INITIATING, initEvent.getMimeType(), initEvent.getSizeInBytes());
		fileItem.setFileField(this);
		this.fileItems.add(fileItem);
		onUploadInitiatedByUser.fire(fileItem);
	}

	@Override
	public void handleUploadTooLarge(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			fileItem.setState(FileItemState.TOO_LARGE);
			onUploadTooLarge.fire(fileItem);
		}
	}

	@Override
	public void handleUploadStarted(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			fileItem.setState(FileItemState.UPLOADING);
			onUploadStarted.fire(fileItem);
		}
	}

	@Override
	public void handleUploadCanceled(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			fileItem.setState(FileItemState.CANCELED);
			onUploadCanceledByUser.fire(fileItem);
		}
	}

	@Override
	public void handleUploadFailed(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			fileItem.setState(FileItemState.FAILED);
			onUploadFailed.fire(fileItem);
		}
	}

	@Override
	public void handleUploadSuccessful(DtoSimpleFileField.UploadSuccessfulEventWrapper event) {
		FileItem fileItem = getFileItemByUuid(event.getFileItemUuid());
		if (fileItem != null) {
			fileItem.setState(FileItemState.DONE);
			fileItem.setIcon(this.defaultItemIcon);
			File uploadedFile = getSessionContext().getUploadedFileByUuid(event.getUploadedFileUuid());
			if (uploadedFile != null) {
				fileItem.setLinkUrl(getSessionContext().createFileLink(uploadedFile));
				fileItem.setFile(uploadedFile);
			}
			onUploadSuccessful.fire(fileItem);
			onValueChanged.fire(getValue());
		}
	}

	@Override
	public void handleFileItemClicked(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			onFileItemClicked.fire(fileItem);
		}
	}

	@Override
	public void handleFileItemRemoved(String fileItemUuid) {
		FileItem fileItem = getFileItemByUuid(fileItemUuid);
		if (fileItem != null) {
			removeFileItemInternal(fileItem);
			onFileItemRemoved.fire(fileItem);
		}
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		clientObjectChannel.setDisplayMode(displayType);
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		clientObjectChannel.setMaxFiles(maxFiles);
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		clientObjectChannel.setMaxBytesPerFile(maxBytesPerFile);
		clientObjectChannel.setFileTooLargeMessage(getSessionContext().getLocalized(ProjectorTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(uploadUrl);
	}

	public Icon getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
		clientObjectChannel.setBrowseButtonIcon(getSessionContext().resolveIcon(browseButtonIcon));
	}

	public String getBrowseButtonCaption() {
		return browseButtonCaption;
	}

	public void setBrowseButtonCaption(String browseButtonCaption) {
		this.browseButtonCaption = browseButtonCaption;
		clientObjectChannel.setBrowseButtonCaption(browseButtonCaption);
	}

}
