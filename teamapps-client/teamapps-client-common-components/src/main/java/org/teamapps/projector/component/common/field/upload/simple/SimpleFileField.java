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

package org.teamapps.projector.component.common.field.upload.simple;

import org.teamapps.projector.component.common.dto.DtoComponent;
import org.teamapps.projector.component.common.dto.DtoSimpleFileField;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.projector.component.common.field.upload.FileSizeFormatter;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.ProjectorComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.projector.component.common.field.upload.FileFieldDisplayType;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yann Massard (yamass@gmail.com)
 */
@ProjectorComponent(library = CoreComponentLibrary.class)
public class SimpleFileField extends AbstractField<List<FileItem>> {

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
	private Icon<?, ?> browseButtonIcon = MaterialIcon.FILE_UPLOAD;
	private final Icon<?, ?> defaultItemIcon = MaterialIcon.CARD_TRAVEL;

	private String browseButtonCaption = getSessionContext().getLocalized(TeamAppsTranslationKeys.UPLOAD.getKey());

	public void addFileItem(FileItem fileItem) {
		fileItem.setState(FileItemState.DONE);
		this.fileItems.add(fileItem);
		fileItem.setFileField(this);
		clientObjectChannel.addFileItem(FileItem.CreateUiFileItem());
	}

	public void removeFileItem(FileItem fileItem) {
		removeFileItemInternal(fileItem);
		clientObjectChannel.removeFileItem(FileItem.GetUuid());
	}

	private void removeFileItemInternal(FileItem fileItem) {
		this.fileItems.remove(fileItem);
		fileItem.setFileField(null);
	}

	/*package-private*/  void handleFileItemChanged(FileItem fileItem) {
		clientObjectChannel.updateFileItem(FileItem.CreateUiFileItem());
	}

	private FileItem getFileItemByUuid(String uuid) {
		return fileItems.stream()
				.filter(item -> item.getUuid().equals(uuid))
				.findFirst().orElse(null);
	}

	@Override
	public List<FileItem> convertUiValueToUxValue(Object value) {
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
	public DtoComponent createDto() {
		DtoSimpleFileField field = new DtoSimpleFileField();
		mapAbstractFieldAttributesToUiField(field);
		field.setBrowseButtonIcon(getSessionContext().resolveIcon(browseButtonIcon));
		field.setBrowseButtonCaption(browseButtonCaption);
		field.setUploadUrl(uploadUrl);
		field.setMaxBytesPerFile(maxBytesPerFile);
		field.setFileTooLargeMessage(getSessionContext().getLocalized(TeamAppsTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		field.setUploadErrorMessage(getSessionContext().getLocalized(TeamAppsTranslationKeys.UPLOAD_ERROR_MESSAGE.getKey()));
		field.setMaxFiles(maxFiles);
		field.setDisplayMode(displayType.toUiFileFieldDisplayType());
		field.setFileItems(fileItems.stream()
				.map(fi -> fi.createUiFileItem())
				.collect(Collectors.toList()));
		return field;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoSimpleFileField.UploadInitiatedByUserEvent.TYPE_ID -> {
				var initEvent = event.as(DtoSimpleFileField.UploadInitiatedByUserEventWrapper.class);
				FileItem fileItem = new FileItem(initEvent.getUuid(), initEvent.getFileName(), FileItemState.INITIATING, initEvent.getMimeType(), initEvent.getSizeInBytes());
				fileItem.setFileField(this);
				this.fileItems.add(fileItem);
				onUploadInitiatedByUser.fire(fileItem);
			}
			case DtoSimpleFileField.UploadTooLargeEvent.TYPE_ID -> {
				var tooLargeEvent = event.as(DtoSimpleFileField.UploadTooLargeEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(tooLargeEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.TOO_LARGE);
					onUploadTooLarge.fire(fileItem);
				}
			}
			case DtoSimpleFileField.UploadStartedEvent.TYPE_ID -> {
				var startedEvent = event.as(DtoSimpleFileField.UploadStartedEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.UPLOADING);
					onUploadStarted.fire(fileItem);
				}
			}
			case DtoSimpleFileField.UploadCanceledEvent.TYPE_ID -> {
				var canceledEvent = event.as(DtoSimpleFileField.UploadCanceledEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(canceledEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.CANCELED);
					onUploadCanceledByUser.fire(fileItem);
				}
			}
			case DtoSimpleFileField.UploadFailedEvent.TYPE_ID -> {
				var startedEvent = event.as(DtoSimpleFileField.UploadFailedEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.FAILED);
					onUploadFailed.fire(fileItem);
				}
			}
			case DtoSimpleFileField.UploadSuccessfulEvent.TYPE_ID -> {
				var successEvent = event.as(DtoSimpleFileField.UploadSuccessfulEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(successEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.DONE);
					fileItem.setIcon(this.defaultItemIcon);
					File uploadedFile = getSessionContext().getUploadedFileByUuid(successEvent.getUploadedFileUuid());
					if (uploadedFile != null) {
						fileItem.setLinkUrl(getSessionContext().createFileLink(uploadedFile));
						fileItem.setFile(uploadedFile);
					}
					onUploadSuccessful.fire(fileItem);
					onValueChanged.fire(getValue());
				}
			}
			case DtoSimpleFileField.FileItemClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoSimpleFileField.FileItemClickedEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(clickEvent.getFileItemUuid());
				if (fileItem != null) {
					onFileItemClicked.fire(fileItem);
				}
			}
			case DtoSimpleFileField.FileItemRemovedEvent.TYPE_ID -> {
				var removedEvent = event.as(DtoSimpleFileField.FileItemRemovedEventWrapper.class);
				FileItem fileItem = getFileItemByUuid(removedEvent.getFileItemUuid());
				if (fileItem != null) {
					removeFileItemInternal(fileItem);
					onFileItemRemoved.fire(fileItem);
				}
			}
		}
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		clientObjectChannel.setDisplayMode(DisplayType.ToUiFileFieldDisplayType());
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		clientObjectChannel.setMaxFiles(MaxFiles);
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		clientObjectChannel.setMaxBytesPerFile(MaxBytesPerFile);
		clientObjectChannel.setFileTooLargeMessage(GetSessionContext().GetLocalized(teamAppsTranslationKeys.file_Too_LARGE_SHORT_MESSAGE.GETKEY(), FILESIZEFORMATTER.humanReadableByteCount(MaxBytesPerFile, true, 1)));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(UploadUrl);
	}

	public Icon<?, ?> getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon<?, ?> browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
		clientObjectChannel.setBrowseButtonIcon(GetSessionContext().ResolveIcon(browseButtonIcon));
	}

	public String getBrowseButtonCaption() {
		return browseButtonCaption;
	}

	public void setBrowseButtonCaption(String browseButtonCaption) {
		this.browseButtonCaption = browseButtonCaption;
		clientObjectChannel.setBrowseButtonCaption(BrowseButtonCaption);
	}
}
