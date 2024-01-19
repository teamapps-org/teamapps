/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.imagecropper;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiImageCropper;
import org.teamapps.dto.UiImageCropperSelection;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

public class ImageCropper extends AbstractComponent {

	public final Event<ImageCropperSelection> onSelectionChanged = new Event<>();

	private String imageUrl;
	private ImageCropperSelectionMode selectionMode = ImageCropperSelectionMode.RECTANGLE;
	private float aspectRatio = 1;
	private ImageCropperSelection selection;

	public ImageCropper() {
	}

	@Override
	public UiComponent createUiComponent() {
		UiImageCropper uiImageCropper = new UiImageCropper(imageUrl, selectionMode.toUiImageCropperSelectionMode(), aspectRatio);
		mapAbstractUiComponentProperties(uiImageCropper);
		return uiImageCropper;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch(event.getUiEventType()) {
			case UI_IMAGE_CROPPER_SELECTION_CHANGED: {
				UiImageCropperSelection uiSelection = ((UiImageCropper.SelectionChangedEvent) event).getSelection();
				ImageCropperSelection selection = new ImageCropperSelection(uiSelection.getLeft(), uiSelection.getTop(), uiSelection.getWidth(), uiSelection.getHeight());
				this.selection = selection;
				this.onSelectionChanged.fire(selection);
				break;
			}
		}
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		queueCommandIfRendered(() -> new UiImageCropper.SetImageUrlCommand(getId(), imageUrl));
	}

	public ImageCropperSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(ImageCropperSelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		queueCommandIfRendered(() -> new UiImageCropper.SetSelectionModeCommand(getId(), selectionMode.toUiImageCropperSelectionMode()));
	}

	public Float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Float aspectRatio) {
		this.aspectRatio = aspectRatio;
		queueCommandIfRendered(() -> new UiImageCropper.SetAspectRatioCommand(getId(), aspectRatio));
	}

	public ImageCropperSelection getSelection() {
		return selection;
	}

	public void setSelection(ImageCropperSelection selection) {
		this.selection = selection;
		queueCommandIfRendered(() -> new UiImageCropper.SetSelectionCommand(getId(), selection.createUiImageCropperSelection()));
	}

}
