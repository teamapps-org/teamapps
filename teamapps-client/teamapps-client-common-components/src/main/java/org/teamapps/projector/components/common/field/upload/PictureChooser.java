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

import net.coobird.thumbnailator.Thumbnails;
import org.teamapps.projector.components.common.dto.DtoField;
import org.teamapps.projector.components.common.dto.DtoPictureChooser;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.flexcontainer.HorizontalLayout;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.projector.components.common.imagecropper.ImageCropper;
import org.teamapps.projector.components.common.imagecropper.ImageCropperSelection;
import org.teamapps.projector.components.common.imagecropper.ImageCropperSelectionMode;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.css.CssJustifyContent;
import org.teamapps.ux.i18n.TeamAppsDictionary;
import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.Resource;

import java.io.*;
import java.nio.file.Files;

public class PictureChooser extends AbstractField<Resource> {

	public final ProjectorEvent<UploadTooLargeEventData> onUploadTooLarge = createProjectorEventBoundToUiEvent(DtoPictureChooser.UploadTooLargeEvent.TYPE_ID);
	public final ProjectorEvent<UploadStartedEventData> onUploadStarted = createProjectorEventBoundToUiEvent(DtoPictureChooser.UploadStartedEvent.TYPE_ID);
	public final ProjectorEvent<UploadCanceledEventData> onUploadCanceled = createProjectorEventBoundToUiEvent(DtoPictureChooser.UploadCanceledEvent.TYPE_ID);
	public final ProjectorEvent<UploadFailedEventData> onUploadFailed = createProjectorEventBoundToUiEvent(DtoPictureChooser.UploadFailedEvent.TYPE_ID);
	public final ProjectorEvent<UploadedFile> onUploadSuccessful = createProjectorEventBoundToUiEvent(DtoPictureChooser.UploadSuccessfulEvent.TYPE_ID);

	private long maxFileSize = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private String fileTooLargeMessage;
	private String uploadErrorMessage;

	private Icon<?, ?> browseButtonIcon = MaterialIcon.EDIT;
	private Icon<?, ?> deleteButtonIcon = MaterialIcon.DELETE;

	private int targetImageWidth = 240;
	private int targetImageHeight = 240;
	private int imageDisplayWidth = -1;
	private int imageDisplayHeight = -1;

	private final ImageCropper imageCropper;
	private final Window imageCropperWindow;
	private UploadedFile uploadedFile;

	private final ImageCropperConverter imageCropperConverter = this::cropAndConvertImage;

	public PictureChooser() {
		imageCropper = new ImageCropper();
		imageCropper.setAspectRatio(targetImageWidth / (float) targetImageHeight);
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponentFillRemaining(imageCropper);
		Button<BaseTemplateRecord<?>> cancelButton = Button.create(getSessionContext().getLocalized(TeamAppsDictionary.CANCEL.getKey()));
		cancelButton.setCssStyle("margin-right", "5px");
		Button<BaseTemplateRecord<?>> rotateButton = Button.create(getSessionContext().getLocalized(TeamAppsDictionary.ROTATE.getKey()));
		rotateButton.setCssStyle("margin-right", "5px");
		Button<BaseTemplateRecord<?>> okButton = Button.create(getSessionContext().getLocalized(TeamAppsDictionary.OK.getKey()));
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponentAutoSize(cancelButton);
		horizontalLayout.addComponentAutoSize(rotateButton);
		horizontalLayout.addComponentAutoSize(okButton);
		horizontalLayout.setJustifyContent(CssJustifyContent.END);
		horizontalLayout.setCssStyle("padding", "5px");
		verticalLayout.addComponentAutoSize(horizontalLayout);
		imageCropperWindow = new Window(MaterialIcon.IMAGE, getSessionContext().getLocalized(TeamAppsDictionary.CROP_IMAGE.getKey()), 700, 500, verticalLayout);
		imageCropperWindow.setCloseable(true);
		imageCropperWindow.setMaximizable(true);
		imageCropperWindow.setModal(true);
		imageCropperWindow.setCloseOnEscape(true);

		okButton.onClicked.addListener(() -> {
			try {
				ImageCropperSelection selection = imageCropper.getSelection();
				Resource converted = imageCropperConverter.convert(uploadedFile, selection, this.targetImageWidth, this.targetImageHeight);
				setValue(converted);
				onValueChanged.fire(converted);
				imageCropperWindow.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		rotateButton.onClicked.addListener(() -> {
			try {
				File rotatedImageFile = rotateImage(uploadedFile.getAsFile());
				imageCropper.setImageUrl(getSessionContext().createFileLink(rotatedImageFile));
				this.uploadedFile = new UploadedFile(uploadedFile.getUuid(), uploadedFile.getName(), uploadedFile.getSizeInBytes(), uploadedFile.getMimeType(), () -> createInputStream(rotatedImageFile), () -> rotatedImageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		cancelButton.onClicked.addListener(() -> {
			imageCropperWindow.close();
		});

		fileTooLargeMessage = getSessionContext().getLocalized(TeamAppsDictionary.FILE_TOO_LARGE_SHORT_MESSAGE.getKey());
		uploadErrorMessage = getSessionContext().getLocalized(TeamAppsDictionary.UPLOAD_ERROR_SHORT_MESSAGE.getKey());
	}

	private InputStream createInputStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object convertUxValueToUiValue(Resource resource) {
		return getSessionContext().createResourceLink(resource);
	}

	private Resource cropAndConvertImage(UploadedFile uploadedFile, ImageCropperSelection selection, int targetWidth, int targetHeight) throws IOException {
		File tempFile = Files.createTempFile("cropped-image", ".jpg").toFile();
		Thumbnails.of(uploadedFile.getAsInputStream()).sourceRegion(selection.getLeft(), selection.getTop(), selection.getWidth(), selection.getHeight()).size(targetWidth, targetHeight).outputFormat("jpg").toFile(tempFile);
		return new FileResource(tempFile);
	}

	private File rotateImage(File imageFile) throws IOException {
		File newImageFile = File.createTempFile("rotated-image", ".jpg");
		Thumbnails.of(imageFile).scale(1).rotate(90).toFile(newImageFile);
		return newImageFile;
	}

	@Override
	public DtoField createDto() {
		DtoPictureChooser uiField = new DtoPictureChooser();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setUploadUrl(uploadUrl);
		uiField.setMaxFileSize(maxFileSize);
		uiField.setFileTooLargeMessage(fileTooLargeMessage);
		uiField.setUploadErrorMessage(uploadErrorMessage);
		uiField.setBrowseButtonIcon(getSessionContext().resolveIcon(browseButtonIcon));
		uiField.setDeleteButtonIcon(getSessionContext().resolveIcon(deleteButtonIcon));
		uiField.setImageDisplayWidth(imageDisplayWidth != -1 ? imageDisplayWidth : targetImageWidth);
		uiField.setImageDisplayHeight(imageDisplayHeight != -1 ? imageDisplayHeight : targetImageHeight);
		return uiField;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoPictureChooser.UploadTooLargeEvent.TYPE_ID -> {
				var tooLargeEvent = event.as(DtoPictureChooser.UploadTooLargeEventWrapper.class);
				this.onUploadTooLarge.fire(new UploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
			}
			case DtoPictureChooser.UploadStartedEvent.TYPE_ID -> {
				var uploadStartedEvent = event.as(DtoPictureChooser.UploadStartedEventWrapper.class);
				this.onUploadStarted.fire(new UploadStartedEventData(uploadStartedEvent.getFileName(), uploadStartedEvent.getMimeType(), uploadStartedEvent.getSizeInBytes(), null /*TODO*/));

			}
			case DtoPictureChooser.UploadCanceledEvent.TYPE_ID -> {
				var canceledEvent = event.as(DtoPictureChooser.UploadCanceledEventWrapper.class);
				this.onUploadCanceled.fire(new UploadCanceledEventData(canceledEvent.getFileName(), canceledEvent.getMimeType(), canceledEvent.getSizeInBytes()));
			}
			case DtoPictureChooser.UploadFailedEvent.TYPE_ID -> {
				var failedEvent = event.as(DtoPictureChooser.UploadFailedEventWrapper.class);
				this.onUploadFailed.fire(new UploadFailedEventData(failedEvent.getFileName(), failedEvent.getMimeType(), failedEvent.getSizeInBytes()));

			}
			case DtoPictureChooser.UploadSuccessfulEvent.TYPE_ID -> {
				var uploadedEvent = event.as(DtoPictureChooser.UploadSuccessfulEventWrapper.class);
				this.uploadedFile = new UploadedFile(uploadedEvent.getUploadedFileUuid(), uploadedEvent.getFileName(), uploadedEvent.getSizeInBytes(), uploadedEvent.getMimeType(),
						() -> {
							try {
								return new FileInputStream(getSessionContext().getUploadedFileByUuid(uploadedEvent.getUploadedFileUuid()));
							} catch (FileNotFoundException e) {
								throw new UploadedFileAccessException(e);
							}
						},
						() -> getSessionContext().getUploadedFileByUuid(uploadedEvent.getUploadedFileUuid())
				);
				onUploadSuccessful.fire(uploadedFile);
				showImageCropperWindow();
			}
		}
	}

	public void setImageCropperSelectionMode(ImageCropperSelectionMode selectionMode) {
		imageCropper.setSelectionMode(selectionMode);
	}

	private void showImageCropperWindow() {
		imageCropper.setImageUrl(getSessionContext().createFileLink(uploadedFile.getAsFile()));
		getSessionContext().showWindow(imageCropperWindow, 200);
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
		sendCommandIfRendered(() -> new DtoPictureChooser.SetMaxFileSizeCommand(maxFileSize));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		sendCommandIfRendered(() -> new DtoPictureChooser.SetUploadUrlCommand(uploadUrl));
	}

	public String getFileTooLargeMessage() {
		return fileTooLargeMessage;
	}

	public void setFileTooLargeMessage(String fileTooLargeMessage) {
		this.fileTooLargeMessage = fileTooLargeMessage;
		sendCommandIfRendered(() -> new DtoPictureChooser.SetFileTooLargeMessageCommand(fileTooLargeMessage));
	}

	public String getUploadErrorMessage() {
		return uploadErrorMessage;
	}

	public void setUploadErrorMessage(String uploadErrorMessage) {
		this.uploadErrorMessage = uploadErrorMessage;
		sendCommandIfRendered(() -> new DtoPictureChooser.SetUploadErrorMessageCommand(uploadErrorMessage));
	}

	public Icon<?, ?> getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon<?, ?> browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
	}

	public Icon<?, ?> getDeleteButtonIcon() {
		return deleteButtonIcon;
	}

	public void setDeleteButtonIcon(Icon<?, ?> deleteButtonIcon) {
		this.deleteButtonIcon = deleteButtonIcon;
	}

	public int getTargetImageWidth() {
		return targetImageWidth;
	}

	public void setTargetImageWidth(int targetImageWidth) {
		this.targetImageWidth = targetImageWidth;
		imageCropper.setAspectRatio(targetImageWidth / (float) targetImageHeight);
	}

	public int getTargetImageHeight() {
		return targetImageHeight;
	}

	public void setTargetImageHeight(int targetImageHeight) {
		this.targetImageHeight = targetImageHeight;
		imageCropper.setAspectRatio(targetImageWidth / (float) targetImageHeight);
	}

	public void setTargetImageSize(int width, int height) {
		this.targetImageWidth = width;
		this.targetImageHeight = height;
		imageCropper.setAspectRatio(width / (float) height);
	}

	public int getImageDisplayWidth() {
		return imageDisplayWidth;
	}

	public void setImageDisplayWidth(int imageDisplayWidth) {
		this.imageDisplayWidth = imageDisplayWidth;
	}

	public int getImageDisplayHeight() {
		return imageDisplayHeight;
	}

	public void setImageDisplayHeight(int imageDisplayHeight) {
		this.imageDisplayHeight = imageDisplayHeight;
	}

	public void setImageDisplaySize(int imageDisplayWidth, int imageDisplayHeight) {
		this.imageDisplayWidth = imageDisplayWidth;
		this.imageDisplayHeight = imageDisplayHeight;
	}

	public interface ImageCropperConverter {
		Resource convert(UploadedFile uploadedFile, ImageCropperSelection selection, int targetWidth, int targetHeight) throws IOException;
	}
}
