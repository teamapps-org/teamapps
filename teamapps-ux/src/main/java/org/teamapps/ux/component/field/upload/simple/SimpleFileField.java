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

package org.teamapps.ux.component.field.upload.simple;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiSimpleFileField;
import org.teamapps.event.Event;
import org.teamapps.formatter.FileSizeFormatter;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
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
		queueCommandIfRendered(() -> new UiSimpleFileField.AddFileItemCommand(getId(), fileItem.createUiFileItem()));
	}

	public void removeFileItem(FileItem fileItem) {
		removeFileItemInternal(fileItem);
		queueCommandIfRendered(() -> new UiSimpleFileField.RemoveFileItemCommand(getId(), fileItem.getUuid()));
	}

	private void removeFileItemInternal(FileItem fileItem) {
		this.fileItems.remove(fileItem);
		fileItem.setFileField(null);
	}

	/*package-private*/  void handleFileItemChanged(FileItem fileItem) {
		queueCommandIfRendered(() -> new UiSimpleFileField.UpdateFileItemCommand(getId(), fileItem.createUiFileItem()));
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
	public UiComponent createUiComponent() {
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
		switch (event.getUiEventType()) {
			case UI_SIMPLE_FILE_FIELD_UPLOAD_INITIATED_BY_USER: {
				UiSimpleFileField.UploadInitiatedByUserEvent initEvent = (UiSimpleFileField.UploadInitiatedByUserEvent) event;
				FileItem fileItem = new FileItem(initEvent.getUuid(), initEvent.getFileName(), FileItemState.INITIATING, initEvent.getMimeType(), initEvent.getSizeInBytes());
				fileItem.setFileField(this);
				this.fileItems.add(fileItem);
				onUploadInitiatedByUser.fire(fileItem);
				break;
			}
			case UI_SIMPLE_FILE_FIELD_UPLOAD_TOO_LARGE: {
				UiSimpleFileField.UploadTooLargeEvent tooLargeEvent = (UiSimpleFileField.UploadTooLargeEvent) event;
				FileItem fileItem = getFileItemByUuid(tooLargeEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.TOO_LARGE);
					onUploadTooLarge.fire(fileItem);
				}
				break;
			}
			case UI_SIMPLE_FILE_FIELD_UPLOAD_STARTED: {
				UiSimpleFileField.UploadStartedEvent startedEvent = (UiSimpleFileField.UploadStartedEvent) event;
				FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.UPLOADING);
					onUploadStarted.fire(fileItem);
				}
				break;
			}
			case UI_SIMPLE_FILE_FIELD_UPLOAD_CANCELED: {
				UiSimpleFileField.UploadCanceledEvent startedEvent = (UiSimpleFileField.UploadCanceledEvent) event;
				FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.CANCELED);
					onUploadCanceledByUser.fire(fileItem);
				}
				break;
			}
			case UI_SIMPLE_FILE_FIELD_UPLOAD_FAILED: {
				UiSimpleFileField.UploadFailedEvent startedEvent = (UiSimpleFileField.UploadFailedEvent) event;
				FileItem fileItem = getFileItemByUuid(startedEvent.getFileItemUuid());
				if (fileItem != null) {
					fileItem.setState(FileItemState.FAILED);
					onUploadFailed.fire(fileItem);
				}
				break;
			}
			case UI_SIMPLE_FILE_FIELD_UPLOAD_SUCCESSFUL: {
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
				break;
			}
			case UI_SIMPLE_FILE_FIELD_FILE_ITEM_CLICKED: {
				UiSimpleFileField.FileItemClickedEvent clickEvent = (UiSimpleFileField.FileItemClickedEvent) event;
				FileItem fileItem = getFileItemByUuid(clickEvent.getFileItemUuid());
				if (fileItem != null) {
					onFileItemClicked.fire(fileItem);
				}
				break;
			}
			case UI_SIMPLE_FILE_FIELD_FILE_ITEM_REMOVED: {
				UiSimpleFileField.FileItemRemovedEvent removedEvent = (UiSimpleFileField.FileItemRemovedEvent) event;
				FileItem fileItem = getFileItemByUuid(removedEvent.getFileItemUuid());
				if (fileItem != null) {
					removeFileItemInternal(fileItem);
					onFileItemRemoved.fire(fileItem);
				}
				break;
			}
		}
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetDisplayModeCommand(getId(), displayType.toUiFileFieldDisplayType()));
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetMaxFilesCommand(getId(), maxFiles));
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetMaxBytesPerFileCommand(getId(), maxBytesPerFile));
		queueCommandIfRendered(() -> new UiSimpleFileField.SetFileTooLargeMessageCommand(getId(), getSessionContext().getLocalized(TeamAppsDictionary.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1))));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetUploadUrlCommand(getId(), uploadUrl));
	}

	public Icon getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetBrowseButtonIconCommand(getId(), getSessionContext().resolveIcon(browseButtonIcon)));
	}

	public String getBrowseButtonCaption() {
		return browseButtonCaption;
	}

	public void setBrowseButtonCaption(String browseButtonCaption) {
		this.browseButtonCaption = browseButtonCaption;
		queueCommandIfRendered(() -> new UiSimpleFileField.SetBrowseButtonCaptionCommand(getId(), browseButtonCaption));
	}
}
