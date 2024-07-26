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
package org.teamapps.projector.component.common.field.richtext;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.common.DtoAbstractField;
import org.teamapps.projector.component.common.DtoRichTextEditor;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.component.common.field.upload.UploadedFile;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.projector.component.common.field.upload.UploadedFileAccessException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

public class RichTextEditor extends AbstractField<String> {

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = new ProjectorEvent<>(clientObjectChannel::toggleSpecialKeyPressedEvent);
	public final ProjectorEvent<ImageUploadTooLargeEventData> onImageUploadTooLarge = new ProjectorEvent<>(clientObjectChannel::toggleImageUploadTooLargeEvent);
	public final ProjectorEvent<ImageUploadStartedEventData> onImageUploadStarted = new ProjectorEvent<>(clientObjectChannel::toggleImageUploadStartedEvent);
	public final ProjectorEvent<ImageUploadSuccessfulEventData> onImageUploadSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleImageUploadSuccessfulEvent);
	public final ProjectorEvent<ImageUploadFailedEventData> onImageUploadFailed = new ProjectorEvent<>(clientObjectChannel::toggleImageUploadFailedEvent);

	private ToolbarVisibilityMode toolbarVisibilityMode = ToolbarVisibilityMode.VISIBLE;
	private int minHeight = 150;
	private int maxHeight;
	private String uploadUrl = "/upload";
	private int maxImageFileSizeInBytes = 5000000;
	private boolean imageUploadEnabled = false;
	private ULocale locale = getSessionContext().getULocale();

	private UploadedFileToUrlConverter uploadedFileToUrlConverter = (file) -> getSessionContext().createFileLink(getSessionContext().getUploadedFileByUuid(file.getUuid()));

	public RichTextEditor() {
		super();
	}

	@Override
	public DtoAbstractField createDto() {
		DtoRichTextEditor field = new DtoRichTextEditor();
		mapAbstractFieldAttributesToUiField(field);
		field.setToolbarVisibilityMode(this.toolbarVisibilityMode.toToolbarVisibilityMode());
		field.setImageUploadEnabled(imageUploadEnabled);
		field.setUploadUrl(uploadUrl);
		field.setMaxImageFileSizeInBytes(maxImageFileSizeInBytes);
		field.setMinHeight(minHeight);
		field.setMaxHeight(maxHeight);
		field.setLocale(locale.toLanguageTag());
		return field;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoRichTextEditor.ImageUploadTooLargeEvent.TYPE_ID -> {
				var tooLargeEvent = event.as(DtoRichTextEditor.ImageUploadTooLargeEventWrapper.class);
				onImageUploadTooLarge.fire(new ImageUploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
			}
			case DtoRichTextEditor.ImageUploadStartedEvent.TYPE_ID -> {
				var uploadStartedEvent = event.as(DtoRichTextEditor.ImageUploadStartedEventWrapper.class);
				onImageUploadStarted.fire(new ImageUploadStartedEventData(uploadStartedEvent.getFileName(), uploadStartedEvent.getMimeType(), uploadStartedEvent.getSizeInBytes(),
						uploadStartedEvent.getIncompleteUploadsCount()));
			}
			case DtoRichTextEditor.ImageUploadSuccessfulEvent.TYPE_ID -> {
				var imageUploadedEvent = event.as(DtoRichTextEditor.ImageUploadSuccessfulEventWrapper.class);
				onImageUploadSuccessful.fire(new ImageUploadSuccessfulEventData(imageUploadedEvent.getFileUuid(), imageUploadedEvent.getName(), imageUploadedEvent.getMimeType(),
						imageUploadedEvent.getSizeInBytes(), imageUploadedEvent.getIncompleteUploadsCount()));
				String fileUuid = imageUploadedEvent.getFileUuid();
				UploadedFile uploadedFile = new UploadedFile(imageUploadedEvent.getFileUuid(), imageUploadedEvent.getName(), imageUploadedEvent.getSizeInBytes(), imageUploadedEvent.getMimeType(),
						() -> {
							try {
								return new FileInputStream(getSessionContext().getUploadedFileByUuid(imageUploadedEvent.getFileUuid()));
							} catch (FileNotFoundException e) {
								throw new UploadedFileAccessException(e);
							}
						},
						() -> getSessionContext().getUploadedFileByUuid(imageUploadedEvent.getFileUuid())
				);
				clientObjectChannel.setUploadedImageUrl(FileUuid, This.uploadedFileToUrlConverter.convert(uploadedFile));
			}
			case DtoRichTextEditor.ImageUploadFailedEvent.TYPE_ID -> {
				var uploadFailedEvent = event.as(DtoRichTextEditor.ImageUploadFailedEventWrapper.class);
				onImageUploadFailed.fire(new ImageUploadFailedEventData(uploadFailedEvent.getName(), uploadFailedEvent.getMimeType(), uploadFailedEvent.getSizeInBytes(), uploadFailedEvent
						.getIncompleteUploadsCount()));
			}
		}
	}

	public ToolbarVisibilityMode getToolbarVisibilityMode() {
		return toolbarVisibilityMode;
	}

	public void setToolbarVisibilityMode(ToolbarVisibilityMode toolbarVisibilityMode) {
		this.toolbarVisibilityMode = toolbarVisibilityMode;
		clientObjectChannel.setToolbarVisibilityMode(ToolbarVisibilityMode.ToToolbarVisibilityMode());
	}

	public UploadedFileToUrlConverter getUploadedFileToUrlConverter() {
		return uploadedFileToUrlConverter;
	}

	public void setUploadedFileToUrlConverter(UploadedFileToUrlConverter uploadedFileToUrlConverter) {
		this.uploadedFileToUrlConverter = uploadedFileToUrlConverter;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(UploadUrl);
	}

	public int getMaxImageFileSizeInBytes() {
		return maxImageFileSizeInBytes;
	}

	public void setMaxImageFileSizeInBytes(int maxImageFileSizeInBytes) {
		this.maxImageFileSizeInBytes = maxImageFileSizeInBytes;
		clientObjectChannel.setMaxImageFileSizeInBytes(MaxImageFileSizeInBytes);
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		clientObjectChannel.setMinHeight(MinHeight);
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		clientObjectChannel.setMaxHeight(MaxHeight);
	}

	public void setFixedHeight(int height) {
		this.minHeight = height;
		this.maxHeight = height;
		clientObjectChannel.setMinHeight(Height);
		clientObjectChannel.setMaxHeight(Height);
	}

	@Override
	public ProjectorEvent<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public ProjectorEvent<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
		reRenderIfRendered();
	}

	public boolean isImageUploadEnabled() {
		return imageUploadEnabled;
	}

	public void setImageUploadEnabled(boolean imageUploadEnabled) {
		this.imageUploadEnabled = imageUploadEnabled;
		reRenderIfRendered();
	}
}
