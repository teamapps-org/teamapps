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

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiSimpleFileField;
import org.teamapps.event.Event;
import org.teamapps.formatter.FileSizeFormatter;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.upload.FileFieldDisplayType;
import org.teamapps.ux.i18n.TeamAppsDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yann Massard (yamass@gmail.com)
 */
@TeamAppsComponent(library = CoreComponentLibrary.class)
public class SimpleFileField extends AbstractField<List<FileItem>> {

	public final Event<FileItem> onUploadInitiatedByUser = new Event<>();
	public final Event<FileItem> onUploadTooLarge = new Event<>();
	public final Event<FileItem> onUploadStarted = new Event<>();
	public final Event<FileItem> onUploadCanceledByUser = new Event<>();
	public final Event<FileItem> onUploadFailed = new Event<>();
	public final Event<FileItem> onUploadSuccessful = new Event<>();
	public final Event<FileItem> onFileItemClicked = new Event<>();
	public final Event<FileItem> onFileItemRemoved = new Event<>();

	private final List<FileItem> fileItems = new ArrayList<>();

	private FileFieldDisplayType displayType = FileFieldDisplayType.FLOATING;
	private int maxFiles = Integer.MAX_VALUE;
	private long maxBytesPerFile = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private Icon browseButtonIcon = MaterialIcon.FILE_UPLOAD;
	private final Icon defaultItemIcon = MaterialIcon.CARD_TRAVEL;

	private String browseButtonCaption = getSessionContext().getLocalized(TeamAppsDictionary.UPLOAD.getKey());

	public void addFileItem(FileItem fileItem) {
		fileItem.setState(FileItemState.DONE);
		this.fileItems.add(fileItem);
		fileItem.setFileField(this);
		queueCommandIfRendered(() -> new UiSimpleFileField.AddFileItemCommand(fileItem.createUiFileItem()));
	}

	public void removeFileItem(FileItem fileItem) {
		removeFileItemInternal(fileItem);
		queueCommandIfRendered(() -> new UiSimpleFileField.RemoveFileItemCommand(fileItem.getUuid()));
	}

	private void removeFileItemInternal(FileItem fileItem) {
		this.fileItems.remove(fileItem);
		fileItem.setFileField(null);
	}

	/*package-private*/  void handleFileItemChanged(FileItem fileItem) {
		queueCommandIfRendered(() -> new UiSimpleFileField.UpdateFileItemCommand(fileItem.createUiFileItem()));
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
	public UiComponent createUiClientObject() {
		UiSimpleFileField field = new UiSimpleFileField();
		mapAbstractFieldAttributesToUiField(field);
		field.setBrowseButtonIcon(getSessionContext().resolveIcon(browseButtonIcon));
		field.setBrowseButtonCaption(browseButtonCaption);
		field.setUploadUrl(uploadUrl);
		field.setMaxBytesPerFile(maxBytesPerFile);
		field.setFileTooLargeMessage(getSessionContext().getLocalized(TeamAppsDictionary.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		field.setUploadErrorMessage(getSessionContext().getLocalized(TeamAppsDictionary.UPLOAD_ERROR_MESSAGE.getKey()));
		field.setMaxFiles(maxFiles);
		field.setDisplayMode(displayType.toUiFileFieldDisplayType());
		field.setFileItems(fileItems.stream()
				.map(fi -> fi.createUiFileItem())
				.collect(Collectors.toList()));
		return field;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiSimpleFileField.UploadInitiatedByUserEvent) {
			UiSimpleFileField.UploadInitiatedByUserEvent initEvent = (UiSimpleFileField.UploadInitiatedByUserEvent) event;
			FileItem fileItem = new FileItem(initEvent.getUuid(), initEvent.getFileName(), FileItemState.INITIATING, initEvent.getMimeType(), initEvent.getSizeInBytes());
			fileItem.setFileField(this);
			this.fileItems.add(fileItem);
			onUploadInitiatedByUser.fire(fileItem);
		} else if (event instanceof UiSimpleFileField.UploadTooLargeEvent) {
			UiSimpleFileField.UploadTooLargeEvent tooLargeEvent = (UiSimpleFileField.UploadTooLargeEvent) event;
			FileItem fileItem = getFileItemByUuid(tooLargeEvent.getFileItemUuid());
			if (fileItem != null) {
				fileItem.setState(FileItemState.TOO_LARGE);
				onUploadTooLarge.fire(fileItem);
			}
		} else if (event instanceof UiSimpleFileField.UploadStartedEvent) {
			UiSimpleFileField.UploadStartedEvent startedEvent = (UiSimpleFileField.UploadStartedEvent) event;
			FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
			if (fileItem != null) {
				fileItem.setState(FileItemState.UPLOADING);
				onUploadStarted.fire(fileItem);
			}
		} else if (event instanceof UiSimpleFileField.UploadCanceledEvent) {
			UiSimpleFileField.UploadCanceledEvent canceledEvent = (UiSimpleFileField.UploadCanceledEvent) event;
			FileItem fileItem = getFileItemByUuid(canceledEvent.getFileItemUuid());
			if (fileItem != null) {
				fileItem.setState(FileItemState.CANCELED);
				onUploadCanceledByUser.fire(fileItem);
			}
		} else if (event instanceof UiSimpleFileField.UploadFailedEvent) {
			UiSimpleFileField.UploadFailedEvent startedEvent = (UiSimpleFileField.UploadFailedEvent) event;
			FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
			if (fileItem != null) {
				fileItem.setState(FileItemState.FAILED);
				onUploadFailed.fire(fileItem);
			}
		} else if (event instanceof UiSimpleFileField.UploadSuccessfulEvent) {
			UiSimpleFileField.UploadSuccessfulEvent successEvent = (UiSimpleFileField.UploadSuccessfulEvent) event;
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
		} else if (event instanceof UiSimpleFileField.FileItemClickedEvent) {
			UiSimpleFileField.FileItemClickedEvent clickEvent = (UiSimpleFileField.FileItemClickedEvent) event;
			FileItem fileItem = getFileItemByUuid(clickEvent.getFileItemUuid());
			if (fileItem != null) {
				onFileItemClicked.fire(fileItem);
			}
		} else if (event instanceof UiSimpleFileField.FileItemRemovedEvent) {
			UiSimpleFileField.FileItemRemovedEvent removedEvent = (UiSimpleFileField.FileItemRemovedEvent) event;
			FileItem fileItem = getFileItemByUuid(removedEvent.getFileItemUuid());
			if (fileItem != null) {
				removeFileItemInternal(fileItem);
				onFileItemRemoved.fire(fileItem);
			}
		}
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetDisplayModeCommand(displayType.toUiFileFieldDisplayType()));
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetMaxFilesCommand(maxFiles));
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetMaxBytesPerFileCommand(maxBytesPerFile));
		queueCommandIfRendered(() -> new UiSimpleFileField.SetFileTooLargeMessageCommand(getSessionContext().getLocalized(TeamAppsDictionary.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1))));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetUploadUrlCommand(uploadUrl));
	}

	public Icon getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetBrowseButtonIconCommand(getSessionContext().resolveIcon(browseButtonIcon)));
	}

	public String getBrowseButtonCaption() {
		return browseButtonCaption;
	}

	public void setBrowseButtonCaption(String browseButtonCaption) {
		this.browseButtonCaption = browseButtonCaption;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetBrowseButtonCaptionCommand(browseButtonCaption));
	}
}
