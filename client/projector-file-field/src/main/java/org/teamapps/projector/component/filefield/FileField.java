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
package org.teamapps.projector.component.filefield;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.commons.formatter.FileSizeFormatter;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientrecordcache.CacheManipulationHandle;
import org.teamapps.projector.clientrecordcache.ClientRecordCache;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.i18n.ProjectorTranslationKeys;
import org.teamapps.projector.record.DtoIdentifiableClientRecord;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(FileFieldLibrary.class)
public class FileField<RECORD> extends AbstractField<List<RECORD>> implements DtoFileFieldEventHandler {

	private final DtoFileFieldClientObjectChannel clientObjectChannel = new DtoFileFieldClientObjectChannel(getClientObjectChannel());

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
	private Object uploadButtonData = new BaseTemplateRecord<>(MaterialIcon.BACKUP, getSessionContext().getLocalized(ProjectorTranslationKeys.UPLOAD.getKey()));
	private PropertyProvider<Object> uploadButtonPropertyProvider = new BeanPropertyExtractor<>();

	private final UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter;
	private Template fileItemTemplate = BaseTemplates.FILE_ITEM_FLOATING;
	private PropertyProvider<RECORD> fileItemPropertyProvider = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, DtoIdentifiableClientRecord> recordCache = new ClientRecordCache<>(this::createDtoIdentifiableClientRecord);

	public FileField(UploadedFileToRecordConverter<RECORD> uploadedFileToRecordConverter) {
		super();
		this.uploadedFileToRecordConverter = uploadedFileToRecordConverter;
	}

	public static FileField<BaseTemplateRecord<UploadedFile>> create() {
		return new FileField<>(file -> new BaseTemplateRecord<>(MaterialIcon.ATTACH_FILE, file.getName(), FileSizeFormatter.humanReadableByteCount(file.getSizeInBytes(), true, 1), file));
	}

	@Override
	public DtoComponentConfig createDto() {
		DtoFileField uiField = new DtoFileField(fileItemTemplate, uploadButtonTemplate, uploadButtonPropertyProvider.getValues(this.uploadButtonData, uploadButtonTemplate.getPropertyNames()));
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxBytesPerFile(maxBytesPerFile);
		uiField.setUploadUrl(uploadUrl);

		uiField.setFileTooLargeMessage(getSessionContext().getLocalized(ProjectorTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey(), FileSizeFormatter.humanReadableByteCount(maxBytesPerFile, true, 1)));
		uiField.setUploadErrorMessage(getSessionContext().getLocalized(ProjectorTranslationKeys.UPLOAD_ERROR_MESSAGE.getKey()));

		uiField.setDisplayType(displayType);
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

	private DtoIdentifiableClientRecord createDtoIdentifiableClientRecord(RECORD record) {
		DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
		clientRecord.setValues(fileItemPropertyProvider.getValues(record, fileItemTemplate.getPropertyNames()));
		return clientRecord;
	}


	@Override
	public Object convertServerValueToClientValue(List<RECORD> records) {
		if (records == null) {
			return null;
		}
		CacheManipulationHandle<List<DtoIdentifiableClientRecord>> cacheResponse = recordCache.replaceRecords(records);
		cacheResponse.commit(); // this is only valid here, because updates from the ui are blocked during transmission of ux values
		return cacheResponse.getAndClearResult();
	}

	@Override
	public List<RECORD> doConvertClientValueToServerValue(@Nonnull JsonNode value) {
		try {
			JavaType integerListType = SessionContext.current().getObjectMapper().getTypeFactory().constructParametricType(List.class, Integer.class);
			List<Integer> clientIds = SessionContext.current().getObjectMapper().treeToValue(value, integerListType);
			return clientIds.stream()
					.map(recordCache::getRecordByClientId)
					.collect(Collectors.toList());
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@Override
	public void handleUploadTooLarge(DtoFileField.UploadTooLargeEventWrapper event) {
		this.onUploadTooLarge.fire(new UploadTooLargeEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadStarted(DtoFileField.UploadStartedEventWrapper event) {
		this.onUploadStarted.fire(new UploadStartedEventData(
				event.getFileName(),
				event.getMimeType(),
				event.getSizeInBytes(),
				() -> clientObjectChannel.cancelUpload(event.getFileItemUuid())
		));
	}

	@Override
	public void handleUploadCanceled(DtoFileField.UploadCanceledEventWrapper event) {
		this.onUploadCanceled.fire(new UploadCanceledEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadFailed(DtoFileField.UploadFailedEventWrapper event) {
		this.onUploadFailed.fire(new UploadFailedEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadSuccessful(DtoFileField.UploadSuccessfulEventWrapper event) {
		UploadedFile uploadedFile = new UploadedFile(event.getUploadedFileUuid(), event.getFileName(), event.getSizeInBytes(), event.getMimeType(),
				() -> {
					try {
						return new FileInputStream(getSessionContext().getUploadedFileByUuid(event.getUploadedFileUuid()));
					} catch (FileNotFoundException e) {
						throw new UploadedFileAccessException(e);
					}
				},
				() -> getSessionContext().getUploadedFileByUuid(event.getUploadedFileUuid())
		);
		RECORD record = uploadedFileToRecordConverter.convert(uploadedFile);
		CacheManipulationHandle<DtoIdentifiableClientRecord> cacheResponse = recordCache.addRecord(record);
		boolean sent = clientObjectChannel.replaceFileItem(event.getFileItemUuid(), cacheResponse.getAndClearResult(), aVoid -> cacheResponse.commit());
		if (!sent) {
			cacheResponse.commit();
		}
		onUploadSuccessful.fire(new UploadSuccessfulEventData<>(uploadedFile, record));
	}

	@Override
	public void handleFileItemClicked(int clientId) {
		RECORD record = recordCache.getRecordByClientId(clientId);
		onFileItemClicked.fire(record);
	}

	@Override
	public void handleFileItemRemoveButtonClicked(int clientId) {
		// TODO ???
	}


	@Override
	public boolean isEmptyValue(List<RECORD> value) {
		return value == null || value.isEmpty();
	}

	@Override
	protected void applyValueFromUi(JsonNode value) {
		List<RECORD> oldValue = new ArrayList<>(getValue() != null ? getValue() : new ArrayList<>());
		super.applyValueFromUi(value);
		oldValue.stream()
				.filter(record -> !getValue().contains(record))
				.forEach(onFileItemRemoved::fire);
	}

	public Template getFileItemTemplate() {
		return fileItemTemplate;
	}

	public void setFileItemTemplate(Template fileItemTemplate) {
		this.fileItemTemplate = fileItemTemplate;
		clientObjectChannel.setItemTemplate(fileItemTemplate);
	}

	public long getMaxBytesPerFile() {
		return maxBytesPerFile;
	}

	public void setMaxBytesPerFile(long maxBytesPerFile) {
		this.maxBytesPerFile = maxBytesPerFile;
		clientObjectChannel.setMaxBytesPerFile(maxBytesPerFile);
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(uploadUrl);
	}

	public Template getUploadButtonTemplate() {
		return uploadButtonTemplate;
	}

	public void setUploadButtonTemplate(Template uploadButtonTemplate) {
		this.uploadButtonTemplate = uploadButtonTemplate;
		clientObjectChannel.setUploadButtonTemplate(uploadButtonTemplate);
	}

	public Object getUploadButtonData() {
		return uploadButtonData;
	}

	public void setUploadButtonData(Object uploadButtonData) {
		this.uploadButtonData = uploadButtonData;
		clientObjectChannel.setUploadButtonData(uploadButtonPropertyProvider.getValues(this.uploadButtonData, uploadButtonTemplate.getPropertyNames()));
	}

	public boolean isShowEntriesAsButtonsOnHover() {
		return showEntriesAsButtonsOnHover;
	}

	public void setShowEntriesAsButtonsOnHover(boolean showEntriesAsButtonsOnHover) {
		this.showEntriesAsButtonsOnHover = showEntriesAsButtonsOnHover;
		clientObjectChannel.setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover);
	}

	public FileFieldDisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(FileFieldDisplayType displayType) {
		this.displayType = displayType;
		clientObjectChannel.setDisplayType(displayType);
	}

	public int getMaxFiles() {
		return maxFiles;
	}

	public void setMaxFiles(int maxFiles) {
		this.maxFiles = maxFiles;
		clientObjectChannel.setMaxFiles(maxFiles);
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
