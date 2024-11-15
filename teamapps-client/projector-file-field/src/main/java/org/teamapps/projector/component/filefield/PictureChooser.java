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

import com.fasterxml.jackson.databind.JsonNode;
import net.coobird.thumbnailator.Thumbnails;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.component.common.ImageCropperSelection;
import org.teamapps.projector.component.common.ImageCropperSelectionMode;
import org.teamapps.projector.component.essential.field.Button;
import org.teamapps.projector.component.essential.flexcontainer.HorizontalLayout;
import org.teamapps.projector.component.essential.flexcontainer.VerticalLayout;
import org.teamapps.projector.component.essential.window.Window;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.filefield.imagecropper.ImageCropper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.format.JustifyContent;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;
import org.teamapps.projector.resource.FileResource;
import org.teamapps.projector.resource.Resource;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;

public class PictureChooser extends AbstractField<Resource> implements DtoPictureChooserEventHandler {

	private final DtoPictureChooserClientObjectChannel clientObjectChannel = new DtoPictureChooserClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<UploadTooLargeEventData> onUploadInitiatedByUser = new ProjectorEvent<>(clientObjectChannel::toggleUploadInitiatedByUserEvent);
	public final ProjectorEvent<UploadTooLargeEventData> onUploadTooLarge = new ProjectorEvent<>(clientObjectChannel::toggleUploadTooLargeEvent);
	public final ProjectorEvent<UploadStartedEventData> onUploadStarted = new ProjectorEvent<>(clientObjectChannel::toggleUploadStartedEvent);
	public final ProjectorEvent<UploadCanceledEventData> onUploadCanceled = new ProjectorEvent<>(clientObjectChannel::toggleUploadCanceledEvent);
	public final ProjectorEvent<UploadFailedEventData> onUploadFailed = new ProjectorEvent<>(clientObjectChannel::toggleUploadFailedEvent);
	public final ProjectorEvent<UploadedFile> onUploadSuccessful = new ProjectorEvent<>(clientObjectChannel::toggleUploadSuccessfulEvent);

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
		Button cancelButton = Button.create(getSessionContext().getLocalized(TeamAppsTranslationKeys.CANCEL.getKey()));
		cancelButton.setCssStyle("margin-right", "5px");
		Button rotateButton = Button.create(getSessionContext().getLocalized(TeamAppsTranslationKeys.ROTATE.getKey()));
		rotateButton.setCssStyle("margin-right", "5px");
		Button okButton = Button.create(getSessionContext().getLocalized(TeamAppsTranslationKeys.OK.getKey()));
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponentAutoSize(cancelButton);
		horizontalLayout.addComponentAutoSize(rotateButton);
		horizontalLayout.addComponentAutoSize(okButton);
		horizontalLayout.setJustifyContent(JustifyContent.END);
		horizontalLayout.setCssStyle("padding", "5px");
		verticalLayout.addComponentAutoSize(horizontalLayout);
		imageCropperWindow = new Window(MaterialIcon.IMAGE, getSessionContext().getLocalized(TeamAppsTranslationKeys.CROP_IMAGE.getKey()), 700, 500, verticalLayout);
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

		fileTooLargeMessage = getSessionContext().getLocalized(TeamAppsTranslationKeys.FILE_TOO_LARGE_SHORT_MESSAGE.getKey());
		uploadErrorMessage = getSessionContext().getLocalized(TeamAppsTranslationKeys.UPLOAD_ERROR_SHORT_MESSAGE.getKey());
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
	public Resource doConvertClientValueToServerValue(@Nonnull JsonNode value) {
		return null; // never needed. The value is only set by the server.
	}

	@Override
	public ComponentConfig createConfig() {
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
	public void handleUploadInitiatedByUser(DtoPictureChooser.UploadInitiatedByUserEventWrapper event) {
		// do nothing.
	}

	@Override
	public void handleUploadTooLarge(DtoPictureChooser.UploadTooLargeEventWrapper event) {
		this.onUploadTooLarge.fire(new UploadTooLargeEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadStarted(DtoPictureChooser.UploadStartedEventWrapper event) {
		this.onUploadStarted.fire(new UploadStartedEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes(), null /*TODO*/));

	}

	@Override
	public void handleUploadCanceled(DtoPictureChooser.UploadCanceledEventWrapper event) {
		this.onUploadCanceled.fire(new UploadCanceledEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadFailed(DtoPictureChooser.UploadFailedEventWrapper event) {
		this.onUploadFailed.fire(new UploadFailedEventData(event.getFileName(), event.getMimeType(), event.getSizeInBytes()));
	}

	@Override
	public void handleUploadSuccessful(DtoPictureChooser.UploadSuccessfulEventWrapper event) {
		this.uploadedFile = new UploadedFile(event.getUploadedFileUuid(), event.getFileName(), event.getSizeInBytes(), event.getMimeType(),
				() -> {
					try {
						return new FileInputStream(getSessionContext().getUploadedFileByUuid(event.getUploadedFileUuid()));
					} catch (FileNotFoundException e) {
						throw new UploadedFileAccessException(e);
					}
				},
				() -> getSessionContext().getUploadedFileByUuid(event.getUploadedFileUuid())
		);
		onUploadSuccessful.fire(uploadedFile);
		showImageCropperWindow();
	}

	@Override
	public Object convertServerValueToClientValue(Resource resource) {
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

	public void setImageCropperSelectionMode(ImageCropperSelectionMode selectionMode) {
		imageCropper.setSelectionMode(selectionMode);
	}

	private void showImageCropperWindow() {
		imageCropper.setImageUrl(getSessionContext().createFileLink(uploadedFile.getAsFile()));
		imageCropperWindow.show();
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
		clientObjectChannel.setMaxFileSize(maxFileSize);
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(uploadUrl);
	}

	public String getFileTooLargeMessage() {
		return fileTooLargeMessage;
	}

	public void setFileTooLargeMessage(String fileTooLargeMessage) {
		this.fileTooLargeMessage = fileTooLargeMessage;
		clientObjectChannel.setFileTooLargeMessage(fileTooLargeMessage);
	}

	public String getUploadErrorMessage() {
		return uploadErrorMessage;
	}

	public void setUploadErrorMessage(String uploadErrorMessage) {
		this.uploadErrorMessage = uploadErrorMessage;
		clientObjectChannel.setUploadErrorMessage(uploadErrorMessage);
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