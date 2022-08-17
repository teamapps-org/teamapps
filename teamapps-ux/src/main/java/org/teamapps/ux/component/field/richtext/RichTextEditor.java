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
package org.teamapps.ux.component.field.richtext;

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiRichTextEditor;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.component.field.upload.UploadedFile;
import org.teamapps.ux.component.field.upload.UploadedFileAccessException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

public class RichTextEditor extends AbstractField<String> implements TextInputHandlingField {

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();
	public final Event<ImageUploadTooLargeEventData> onImageUploadTooLarge = new Event<>();
	public final Event<ImageUploadStartedEventData> onImageUploadStarted = new Event<>();
	public final Event<ImageUploadSuccessfulEventData> onImageUploadSuccessful = new Event<>();
	public final Event<ImageUploadFailedEventData> onImageUploadFailed = new Event<>();

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
	public UiField createUiComponent() {
		UiRichTextEditor field = new UiRichTextEditor();
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
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiRichTextEditor.ImageUploadTooLargeEvent) {
			UiRichTextEditor.ImageUploadTooLargeEvent tooLargeEvent = (UiRichTextEditor.ImageUploadTooLargeEvent) event;
			onImageUploadTooLarge.fire(new ImageUploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
		} else if (event instanceof UiRichTextEditor.ImageUploadStartedEvent) {
			UiRichTextEditor.ImageUploadStartedEvent uploadStartedEvent = (UiRichTextEditor.ImageUploadStartedEvent) event;
			onImageUploadStarted.fire(new ImageUploadStartedEventData(uploadStartedEvent.getFileName(), uploadStartedEvent.getMimeType(), uploadStartedEvent.getSizeInBytes(),
					uploadStartedEvent.getIncompleteUploadsCount()));
		} else if (event instanceof UiRichTextEditor.ImageUploadSuccessfulEvent) {
			UiRichTextEditor.ImageUploadSuccessfulEvent imageUploadedEvent = (UiRichTextEditor.ImageUploadSuccessfulEvent) event;
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
			queueCommandIfRendered(() -> new UiRichTextEditor.SetUploadedImageUrlCommand(fileUuid, this.uploadedFileToUrlConverter.convert(uploadedFile)));
		} else if (event instanceof UiRichTextEditor.ImageUploadFailedEvent) {
			UiRichTextEditor.ImageUploadFailedEvent uploadFailedEvent = (UiRichTextEditor.ImageUploadFailedEvent) event;
			onImageUploadFailed.fire(new ImageUploadFailedEventData(uploadFailedEvent.getName(), uploadFailedEvent.getMimeType(), uploadFailedEvent.getSizeInBytes(), uploadFailedEvent
					.getIncompleteUploadsCount()));
		}
	}

	public ToolbarVisibilityMode getToolbarVisibilityMode() {
		return toolbarVisibilityMode;
	}

	public void setToolbarVisibilityMode(ToolbarVisibilityMode toolbarVisibilityMode) {
		this.toolbarVisibilityMode = toolbarVisibilityMode;
		queueCommandIfRendered(() -> new UiRichTextEditor.SetToolbarVisibilityModeCommand(toolbarVisibilityMode.toToolbarVisibilityMode()));
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
		queueCommandIfRendered(() -> new UiRichTextEditor.SetUploadUrlCommand(uploadUrl));
	}

	public int getMaxImageFileSizeInBytes() {
		return maxImageFileSizeInBytes;
	}

	public void setMaxImageFileSizeInBytes(int maxImageFileSizeInBytes) {
		this.maxImageFileSizeInBytes = maxImageFileSizeInBytes;
		queueCommandIfRendered(() -> new UiRichTextEditor.SetMaxImageFileSizeInBytesCommand(maxImageFileSizeInBytes));
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		queueCommandIfRendered(() -> new UiRichTextEditor.SetMinHeightCommand(minHeight));
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		queueCommandIfRendered(() -> new UiRichTextEditor.SetMaxHeightCommand(maxHeight));
	}

	public void setFixedHeight(int height) {
		this.minHeight = height;
		this.maxHeight = height;
		queueCommandIfRendered(() -> new UiRichTextEditor.SetMinHeightCommand(height));
		queueCommandIfRendered(() -> new UiRichTextEditor.SetMaxHeightCommand(height));
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
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
