/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiPictureChooser;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.flexcontainer.HorizontalLayout;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.imagecropper.ImageCropper;
import org.teamapps.ux.component.imagecropper.ImageCropperSelection;
import org.teamapps.ux.component.imagecropper.ImageCropperSelectionMode;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.css.CssJustifyContent;
import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.Resource;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PictureChooser extends AbstractField<Resource> {

	public final Event<UploadTooLargeEventData> onUploadTooLarge = new Event<>();
	public final Event<UploadStartedEventData> onUploadStarted = new Event<>();
	public final Event<UploadCanceledEventData> onUploadCanceled = new Event<>();
	public final Event<UploadFailedEventData> onUploadFailed = new Event<>();
	public final Event<UploadedFile> onUploadSuccessful = new Event<>();

	private long maxFileSize = 10_000_000; // There is also a hard limitation! (see application.properties)
	private String uploadUrl = "/upload"; // May point anywhere.
	private String fileTooLargeMessage;
	private String uploadErrorMessage;

	private Icon browseButtonIcon = MaterialIcon.EDIT;
	private Icon deleteButtonIcon = MaterialIcon.DELETE;

	private int targetImageWidth = 240;
	private int targetImageHeight = 288;
	private int imageDisplayWidth = -1;
	private int imageDisplayHeight = -1;

	private ImageCropper imageCropper;
	private Window imageCropperWindow;
	private UploadedFile uploadedFile;

	private ImageCropperConverter imageCropperConverter = this::cropAndConvertImage;

	public PictureChooser() {
		imageCropper = new ImageCropper();
		imageCropper.setAspectRatio(targetImageWidth / (float) targetImageHeight);
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponentFillRemaining(imageCropper);
		Button<BaseTemplateRecord> cancelButton = Button.create(getSessionContext().getLocalized("dict.cancel"));
		cancelButton.setCssStyle("margin-right", "5px");
		Button<BaseTemplateRecord> okButton = Button.create(getSessionContext().getLocalized("dict.ok"));
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponentAutoSize(cancelButton);
		horizontalLayout.addComponentAutoSize(okButton);
		horizontalLayout.setJustifyContent(CssJustifyContent.FLEX_END);
		horizontalLayout.setCssStyle("padding", "5px");
		verticalLayout.addComponentAutoSize(horizontalLayout);
		imageCropperWindow = new Window(MaterialIcon.IMAGE, getSessionContext().getLocalized("ux.pictureChooser.cropImageWindowTitle"), 600, 400, verticalLayout);
		imageCropperWindow.setCloseable(true);
		imageCropperWindow.setMaximizable(true);
		imageCropperWindow.setModal(true);
		imageCropperWindow.setCloseOnEscape(true);

		okButton.onValueChanged.addListener(aBoolean -> {
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

		cancelButton.onValueChanged.addListener(aBoolean -> {
			imageCropperWindow.close();
		});

		fileTooLargeMessage = getSessionContext().getLocalized("ux.fileField.fileTooLarge_short");
		uploadErrorMessage = getSessionContext().getLocalized("ux.fileField.uploadError_short");
	}

	@Override
	public Object convertUxValueToUiValue(Resource resource) {
		return getSessionContext().createResourceLink(resource);
	}

	private Resource cropAndConvertImage(UploadedFile uploadedFile, ImageCropperSelection selection, int targetWidth, int targetHeight) throws IOException {
		BufferedImage image = ImageIO.read(uploadedFile.getAsInputStream());
		BufferedImage subImage = image.getSubimage(selection.getLeft(), selection.getTop(), selection.getWidth(), selection.getHeight());
		BufferedImage resultImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		resultImage.createGraphics().drawImage(subImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
		Path tempFile = Files.createTempFile("cropped-image", ".png");
		ImageIO.write(resultImage, "jpg", tempFile.toFile());
		return new FileResource(tempFile.toFile());
	}

	@Override
	public UiField createUiComponent() {
		UiPictureChooser uiField = new UiPictureChooser();
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
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		switch (event.getUiEventType()) {
			case UI_PICTURE_CHOOSER_UPLOAD_TOO_LARGE: {
				UiPictureChooser.UploadTooLargeEvent tooLargeEvent = (UiPictureChooser.UploadTooLargeEvent) event;
				this.onUploadTooLarge.fire(new UploadTooLargeEventData(tooLargeEvent.getFileName(), tooLargeEvent.getMimeType(), tooLargeEvent.getSizeInBytes()));
				break;
			}
			case UI_PICTURE_CHOOSER_UPLOAD_STARTED: {
				UiPictureChooser.UploadStartedEvent uploadStartedEvent = (UiPictureChooser.UploadStartedEvent) event;
				this.onUploadStarted.fire(new UploadStartedEventData(uploadStartedEvent.getFileName(), uploadStartedEvent.getMimeType(), uploadStartedEvent.getSizeInBytes()));
				break;
			}
			case UI_PICTURE_CHOOSER_UPLOAD_CANCELED: {
				UiPictureChooser.UploadCanceledEvent canceledEvent = (UiPictureChooser.UploadCanceledEvent) event;
				this.onUploadCanceled.fire(new UploadCanceledEventData(canceledEvent.getFileName(), canceledEvent.getMimeType(), canceledEvent.getSizeInBytes()));
				break;
			}
			case UI_PICTURE_CHOOSER_UPLOAD_FAILED: {
				UiPictureChooser.UploadFailedEvent failedEvent = (UiPictureChooser.UploadFailedEvent) event;
				this.onUploadFailed.fire(new UploadFailedEventData(failedEvent.getFileName(), failedEvent.getMimeType(), failedEvent.getSizeInBytes()));
				break;
			}
			case UI_PICTURE_CHOOSER_UPLOAD_SUCCESSFUL: {
				UiPictureChooser.UploadSuccessfulEvent uploadedEvent = (UiPictureChooser.UploadSuccessfulEvent) event;
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
				break;
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
		queueCommandIfRendered(() -> new UiPictureChooser.SetMaxFileSizeCommand(getId(), maxFileSize));
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		queueCommandIfRendered(() -> new UiPictureChooser.SetUploadUrlCommand(getId(), uploadUrl));
	}

	public String getFileTooLargeMessage() {
		return fileTooLargeMessage;
	}

	public void setFileTooLargeMessage(String fileTooLargeMessage) {
		this.fileTooLargeMessage = fileTooLargeMessage;
		queueCommandIfRendered(() -> new UiPictureChooser.SetFileTooLargeMessageCommand(getId(), fileTooLargeMessage));
	}

	public String getUploadErrorMessage() {
		return uploadErrorMessage;
	}

	public void setUploadErrorMessage(String uploadErrorMessage) {
		this.uploadErrorMessage = uploadErrorMessage;
		queueCommandIfRendered(() -> new UiPictureChooser.SetUploadErrorMessageCommand(getId(), uploadErrorMessage));
	}

	public Icon getBrowseButtonIcon() {
		return browseButtonIcon;
	}

	public void setBrowseButtonIcon(Icon browseButtonIcon) {
		this.browseButtonIcon = browseButtonIcon;
	}

	public Icon getDeleteButtonIcon() {
		return deleteButtonIcon;
	}

	public void setDeleteButtonIcon(Icon deleteButtonIcon) {
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
