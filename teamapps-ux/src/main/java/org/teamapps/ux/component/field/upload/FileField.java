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

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiFileField;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.event.Event;
import org.teamapps.formatter.FileSizeFormatter;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.cache.record.legacy.ClientRecordCache;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.i18n.TeamAppsDictionary;
import org.teamapps.ux.icon.TeamAppsIconBundle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileField<RECORD> extends AbstractField<List<RECORD>> {


	public final Event<UploadTooLargeEventData> onUploadTooLarge = new Event<>();
	public final Event<UploadStartedEventData> onUploadStarted = new Event<>();
	public final Event<UploadCanceledEventData> onUploadCanceled = new Event<>();
	public final Event<UploadFailedEventData> onUploadFailed = new Event<>();
	public final Event<UploadSuccessfulEventData<RECORD>> onUploadSuccessful = new Event<>();
	public final Event<RECORD> onFileItemClicked = new Event<>();
	public final Event<RECORD> onFileItemRemoved = new Event<>();

	private FileFieldDisplayType displayType = FileFieldDisplayType.FLOATING;
	private boolean showEntriesAsButtonsOnHover = false;
	private int maxFiles = Integer.MAX_VALUE;

	private long maxBytesPerFile = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private Template uploadButtonTemplate = BaseTemplate.BUTTON;
	private Object uploadButtonData = new BaseTemplateRecord(getSessionContext().getIcon(TeamAppsIconBundle.UPLOAD.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.UPLOAD.getKey()));
	private PropertyProvider uploadButtonPropertyProvider = new BeanPropertyExtractor<>();

	private final UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter;
	private Template fileItemTemplate = BaseTemplate.FILE_ITEM_FLOATING;
	private PropertyProvider<RECORD> fileItemPropertyProvider = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, UiIdentifiableClientRecord> recordCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);

	public FileField(UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter) {
		super();
		this.uploadedFileToRecordConverter = uploadedFileToRecordConverter;
	}

	public static FileField<BaseTemplateRecord<UploadedFile>> create() {
		return new FileField<>(file -> new BaseTemplateRecord<>(MaterialIcon.ATTACH_FILE, file.getName(), FileSizeFormatter.humanReadableByteCount(file.getSizeInBytes(), true, 1), file));
	}

	@Override
	public UiField createUiComponent() {
		Map uploadButtonData = uploadButtonPropertyProvider.getValues(this.uploadButtonData, uploadButtonTemplate.getPropertyNames());
		UiFileField uiField = new UiFileField(fileItemTemplate.createUiTemplate(), uploadButtonTemplate.createUiTemplate(), uploadButtonData);
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxBytesPerFile(maxBytesPerFile);
		uiField.setUploadUrl(uploadUrl);

		uiField.setFileTooLargeMessage(getSessionContext().getLocalized(TeamAppsDictionary.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		uiField.setUploadErrorMessage(getSessionContext().getLocalized(TeamAppsDictionary.UPLOAD_ERROR_MESSAGE.getKey()));

		uiField.setDisplayType(displayType.toUiFileFieldDisplayType());
		uiField.setMaxFiles(this.maxFiles);
		uiField.setShowEntriesAsButtonsOnHover(this.showEntriesAsButtonsOnHover);

		return uiField;
	}

	@Override
	public void setValue(List<RECORD> records) {
		this.setValue(records, true);
	}

	public void setValue(List<RECORD> records, boolean cancelUploads) {
		if (cancelUploads) {
			cancelUploads();
		}
		super.setValue(records);
	}

	public void cancelUploads() {
		this.queueCommandIfRendered(() -> new UiFileField.CancelAllUploadsCommand());
	}

	private UiIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		UiIdentifiableClientRecord clientRecord = new UiIdentifiableClientRecord();
		clientRecord.setValues(fileItemPropertyProvider.getValues(record, fileItemTemplate.getPropertyNames()));
		return clientRecord;
	}

	@Override
	public Object convertUxValueToUiValue(List<RECORD> uxValue) {
		if (uxValue == null) {
			return null;
		}
		CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse = recordCache.replaceRecords(uxValue);
		cacheResponse.commit(); // this is only valid here, because updates from the ui are blocked during transmission of ux values
		return cacheResponse.getAndClearResult();
	}

	@Override
	public List<RECORD> convertUiValueToUxValue(Object uiValues) {
		if (uiValues == null) {
			return new ArrayList<>();
		}
		List<Integer> clientIds = (List<Integer>) uiValues;
		return clientIds.stream()
				.map(clientId -> recordCache.getRecordByClientId(clientId))
				.collect(Collectors.toList());
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiFileField.UploadTooLargeEvent) {
			UiFileField.UploadTooLargeEvent tooLargeEvent = (UiFileField.UploadTooLargeEvent) event;
			this.onUploadTooLarge.fire(new UploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
		} else if (event instanceof UiFileField.UploadStartedEvent) {
			UiFileField.UploadStartedEvent uploadStartedEvent = (UiFileField.UploadStartedEvent) event;
			this.onUploadStarted.fire(new UploadStartedEventData(
					uploadStartedEvent.getFileName(),
					uploadStartedEvent.getMimeType(),
					uploadStartedEvent.getSizeInBytes(),
					() -> this.queueCommandIfRendered(() -> new UiFileField.CancelUploadCommand(uploadStartedEvent.getFileItemUuid()))
			));
		} else if (event instanceof UiFileField.UploadCanceledEvent) {
			UiFileField.UploadCanceledEvent canceledEvent = (UiFileField.UploadCanceledEvent) event;
			this.onUploadCanceled.fire(new UploadCanceledEventData(canceledEvent.getFileName(), canceledEvent.getMimeType(), canceledEvent.getSizeInBytes()
			));
		} else if (event instanceof UiFileField.UploadFailedEvent) {
			UiFileField.UploadFailedEvent failedEvent = (UiFileField.UploadFailedEvent) event;
			this.onUploadFailed.fire(new UploadFailedEventData(failedEvent.getFileName(), failedEvent.getMimeType(), failedEvent.getSizeInBytes()
			));
		} else if (event instanceof UiFileField.UploadSuccessfulEvent) {
			UiFileField.UploadSuccessfulEvent uploadedEvent = (UiFileField.UploadSuccessfulEvent) event;
			UploadedFile uploadedFile = new UploadedFile(uploadedEvent.getUploadedFileUuid(), uploadedEvent.getFileName(), uploadedEvent.getSizeInBytes(), uploadedEvent.getMimeType(),
					() -> {
						try {
							return new FileInputStream(getSessionContext().getUploadedFileByUuid(uploadedEvent.getUploadedFileUuid()));
						} catch (FileNotFoundException e) {
							throw new UploadedFileAccessException(e);
						}
					},
					() -> getSessionContext().getUploadedFileByUuid(uploadedEvent.getUploadedFileUuid())
			);
			RECORD record = uploadedFileToRecordConverter.convert(uploadedFile);
			CacheManipulationHandle<UiIdentifiableClientRecord> cacheResponse = recordCache.addRecord(record);
			if (isRendered()) {
				getSessionContext().sendCommand(getId(), new UiFileField.ReplaceFileItemCommand(uploadedEvent.getFileItemUuid(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
			} else {
				cacheResponse.commit();
			}
			onUploadSuccessful.fire(new UploadSuccessfulEventData<>(uploadedFile, record));
		} else if (event instanceof UiFileField.FileItemClickedEvent) {
			UiFileField.FileItemClickedEvent fileClickedEvent = (UiFileField.FileItemClickedEvent) event;
			RECORD record = recordCache.getRecordByClientId(fileClickedEvent.getClientId());
			onFileItemClicked.fire(record);
		}
	}

	@Override
	public boolean isEmptyValue(List<RECORD> value) {
		return value == null || value.isEmpty();
	}

	@Override
	protected void applyValueFromUi(Object value) {
		List<RECORD> oldValue = new ArrayList<>(getValue() != null ? getValue() : Collections.emptyList());
		super.applyValueFromUi(value);
		if (oldValue != null) {
			oldValue.removeAll(getValue() != null ? getValue() : Collections.emptyList());
			oldValue.forEach(record -> onFileItemRemoved.fire(record));
		}
	}

	public Template getFileItemTemplate() {
		return fileItemTemplate;
	}

	public void setFileItemTemplate(Template fileItemTemplate) {
		this.fileItemTemplate = fileItemTemplate;
		queueCommandIfRendered(() -> new UiFileField.SetItemTemplateCommand(fileItemTemplate.createUiTemplate()));
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		queueCommandIfRendered(() -> new UiFileField.SetMaxBytesPerFileCommand(maxBytesPerFile));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		queueCommandIfRendered(() -> new UiFileField.SetUploadUrlCommand(uploadUrl));
	}

	public Template getUploadButtonTemplate() {
		return uploadButtonTemplate;
	}

	public void setUploadButtonTemplate(Template uploadButtonTemplate) {
		this.uploadButtonTemplate = uploadButtonTemplate;
		queueCommandIfRendered(() -> new UiFileField.SetUploadButtonTemplateCommand(uploadButtonTemplate.createUiTemplate()));
	}

	public Object getUploadButtonData() {
		return uploadButtonData;
	}

	public void setUploadButtonData(Object uploadButtonData) {
		this.uploadButtonData = uploadButtonData;
		queueCommandIfRendered(() -> new UiFileField.SetUploadButtonDataCommand(uploadButtonData));
	}

	public boolean isShowEntriesAsButtonsOnHover() {
		return showEntriesAsButtonsOnHover;
	}

	public void setShowEntriesAsButtonsOnHover(boolean showEntriesAsButtonsOnHover) {
		this.showEntriesAsButtonsOnHover = showEntriesAsButtonsOnHover;
		queueCommandIfRendered(() -> new UiFileField.SetShowEntriesAsButtonsOnHoverCommand(showEntriesAsButtonsOnHover));
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		queueCommandIfRendered(() -> new UiFileField.SetDisplayTypeCommand(displayType.toUiFileFieldDisplayType()));
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		queueCommandIfRendered(() -> new UiFileField.SetMaxFilesCommand(maxFiles));
	}

	public PropertyProvider getUploadButtonPropertyProvider() {
		return uploadButtonPropertyProvider;
	}

	public void setUploadButtonPropertyProvider(PropertyProvider propertyProvider) {
		this.uploadButtonPropertyProvider = propertyProvider;
	}

	public void setUploadButtonPropertyExtractor(PropertyExtractor propertyExtractor) {
		this.setUploadButtonPropertyProvider(propertyExtractor);
	}

	public UploadedFileToRecordConverter<RECORD> getUploadedFileToRecordConverter() {
		return uploadedFileToRecordConverter;
	}

	public PropertyProvider<RECORD> getFileItemPropertyProvider() {
		return fileItemPropertyProvider;
	}

	public void setFileItemPropertyProvider(PropertyProvider<RECORD> fileItemPropertyProvider) {
		this.fileItemPropertyProvider = fileItemPropertyProvider;
	}

	public void setFileItemPropertyExtractor(PropertyExtractor<RECORD> fileItemPropertyExtractor) {
		this.setFileItemPropertyProvider(fileItemPropertyExtractor);
	}
}
