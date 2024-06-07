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
package org.teamapps.projector.components.common.field.upload;

import org.teamapps.projector.components.common.dto.DtoAbstractField;
import org.teamapps.projector.components.common.dto.DtoFileField;
import org.teamapps.projector.components.common.dto.DtoIdentifiableClientRecord;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.cache.record.legacy.ClientRecordCache;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.ProjectorComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class FileField<RECORD> extends AbstractField<List<RECORD>> {


	public final ProjectorEvent<UploadTooLargeEventData> onUploadTooLarge = new ProjectorEvent<>(clientObjectChannel::toggleUploadTooLargeEvent);
	public final ProjectorEvent<UploadStartedEventData> onUploadStarted = new ProjectorEvent<>(clientObjectChannel::toggleUploadStartedEvent);
	public final ProjectorEvent<UploadCanceledEventData> onUploadCanceled = new ProjectorEvent<>(clientObjectChannel::toggleUploadCanceledEvent);
	public final ProjectorEvent<UploadFailedEventData> onUploadFailed = new ProjectorEvent<>(clientObjectChannel::toggleUploadFailedEvent);
	public final ProjectorEvent<UploadSuccessfulEventData<RECORD>> onUploadSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleUploadSuccessfulEvent);
	public final ProjectorEvent<RECORD> onFileItemClicked = new ProjectorEvent<>(clientObjectChannel::toggleFileItemClickedEvent);
	public final ProjectorEvent<RECORD> onFileItemRemoved = new ProjectorEvent<>();

	private FileFieldDisplayType displayType = FileFieldDisplayType.FLOATING;
	private boolean showEntriesAsButtonsOnHover = false;
	private int maxFiles = Integer.MAX_VALUE;

	private long maxBytesPerFile = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private Template uploadButtonTemplate = BaseTemplates.BUTTON;
	private Object uploadButtonData = new BaseTemplateRecord<>(getSessionContext().getIcon(MaterialIcon.BACKUP), getSessionContext().getLocalized(TeamAppsTranslationKeys.UPLOAD.getKey()));
	private PropertyProvider uploadButtonPropertyProvider = new BeanPropertyExtractor<>();

	private final UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter;
	private Template fileItemTemplate = BaseTemplates.FILE_ITEM_FLOATING;
	private PropertyProvider<RECORD> fileItemPropertyProvider = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, DtoIdentifiableClientRecord> recordCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);

	public FileField(UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter) {
		super();
		this.uploadedFileToRecordConverter = uploadedFileToRecordConverter;
	}

	public static FileField<BaseTemplateRecord<UploadedFile>> create() {
		return new FileField<>(file -> new BaseTemplateRecord<>(MaterialIcon.ATTACH_FILE, file.getName(), FileSizeFormatter.humanReadableByteCount(file.getSizeInBytes(), true, 1), file));
	}

	@Override
	public DtoAbstractField createDto() {
		Map uploadButtonData = uploadButtonPropertyProvider.getValues(this.uploadButtonData, uploadButtonTemplate.getPropertyNames());
		DtoFileField uiField = new DtoFileField(fileItemTemplate != null ? fileItemTemplate.createDtoReference() : null, uploadButtonTemplate != null ? uploadButtonTemplate.createDtoReference() : null, uploadButtonData);
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxBytesPerFile(maxBytesPerFile);
		uiField.setUploadUrl(uploadUrl);

		uiField.setFileTooLargeMessage(getSessionContext().getLocalized(TeamAppsTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		uiField.setUploadErrorMessage(getSessionContext().getLocalized(TeamAppsTranslationKeys.UPLOAD_ERROR_MESSAGE.getKey()));

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
		this.clientObjectChannel.cancelAllUploads();
	}

	private DtoIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
		clientRecord.setValues(fileItemPropertyProvider.getValues(record, fileItemTemplate.getPropertyNames()));
		return clientRecord;
	}

	@Override
	public Object convertUxValueToUiValue(List<RECORD> uxValue) {
		if (uxValue == null) {
			return null;
		}
		CacheManipulationHandle<List<DtoIdentifiableClientRecord>> cacheResponse = recordCache.replaceRecords(uxValue);
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
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoFileField.UploadTooLargeEvent.TYPE_ID -> {
				var tooLargeEvent = event.as(DtoFileField.UploadTooLargeEventWrapper.class);
				this.onUploadTooLarge.fire(new UploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
			}
			case DtoFileField.UploadStartedEvent.TYPE_ID -> {
				var uploadStartedEvent = event.as(DtoFileField.UploadStartedEventWrapper.class);
				this.onUploadStarted.fire(new UploadStartedEventData(
						uploadStartedEvent.getFileName(),
						uploadStartedEvent.getMimeType(),
						uploadStartedEvent.getSizeInBytes(),
						() -> this.sendCommandIfRendered(() -> new DtoFileField.CancelUploadCommand(uploadStartedEvent.getFileItemUuid()))
				));
			}
			case DtoFileField.UploadCanceledEvent.TYPE_ID -> {
				var canceledEvent = event.as(DtoFileField.UploadCanceledEventWrapper.class);
				this.onUploadCanceled.fire(new UploadCanceledEventData(canceledEvent.getFileName(), canceledEvent.getMimeType(), canceledEvent.getSizeInBytes()
				));
			}
			case DtoFileField.UploadFailedEvent.TYPE_ID -> {
				var failedEvent = event.as(DtoFileField.UploadFailedEventWrapper.class);
				this.onUploadFailed.fire(new UploadFailedEventData(failedEvent.getFileName(), failedEvent.getMimeType(), failedEvent.getSizeInBytes()
				));
			}
			case DtoFileField.UploadSuccessfulEvent.TYPE_ID -> {
				var uploadedEvent = event.as(DtoFileField.UploadSuccessfulEventWrapper.class);
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
				CacheManipulationHandle<DtoIdentifiableClientRecord> cacheResponse = recordCache.addRecord(record);
				if (isRendered()) {
					final DtoFileField.ReplaceFileItemCommand replaceFileItemCommand = new DtoFileField.ReplaceFileItemCommand(uploadedEvent.getFileItemUuid(), cacheResponse.getAndClearResult());
					getSessionContext().sendCommandIfRendered(this, aVoid -> cacheResponse.commit(), () -> replaceFileItemCommand);
				} else {
					cacheResponse.commit();
				}
				onUploadSuccessful.fire(new UploadSuccessfulEventData<>(uploadedFile, record));
			}
			case DtoFileField.FileItemClickedEvent.TYPE_ID -> {
				var fileClickedEvent = event.as(DtoFileField.FileItemClickedEventWrapper.class);
				RECORD record = recordCache.getRecordByClientId(fileClickedEvent.getClientId());
				onFileItemClicked.fire(record);
			}

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
		clientObjectChannel.setItemTemplate(FileItemTemplate != Null ? fileItemTemplate.createDtoReference() : null);
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		clientObjectChannel.setMaxBytesPerFile(MaxBytesPerFile);
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(UploadUrl);
	}

	public Template getUploadButtonTemplate() {
		return uploadButtonTemplate;
	}

	public void setUploadButtonTemplate(Template uploadButtonTemplate) {
		this.uploadButtonTemplate = uploadButtonTemplate;
		clientObjectChannel.setUploadButtonTemplate(UploadButtonTemplate != Null ? uploadButtonTemplate.createDtoReference() : null);
	}

	public Object getUploadButtonData() {
		return uploadButtonData;
	}

	public void setUploadButtonData(Object uploadButtonData) {
		this.uploadButtonData = uploadButtonData;
		clientObjectChannel.setUploadButtonData(UploadButtonData);
	}

	public boolean isShowEntriesAsButtonsOnHover() {
		return showEntriesAsButtonsOnHover;
	}

	public void setShowEntriesAsButtonsOnHover(boolean showEntriesAsButtonsOnHover) {
		this.showEntriesAsButtonsOnHover = showEntriesAsButtonsOnHover;
		clientObjectChannel.setShowEntriesAsButtonsOnHover(ShowEntriesAsButtonsOnHover);
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		clientObjectChannel.setDisplayType(DisplayType.ToUiFileFieldDisplayType());
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		clientObjectChannel.setMaxFiles(MaxFiles);
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
