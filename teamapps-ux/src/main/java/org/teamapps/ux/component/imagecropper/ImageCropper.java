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
package org.teamapps.ux.component.imagecropper;

import org.teamapps.dto.*;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;

public class ImageCropper extends AbstractComponent {

	public final ProjectorEvent<ImageCropperSelection> onSelectionChanged = createProjectorEventBoundToUiEvent(UiImageCropper.SelectionChangedEvent.TYPE_ID);

	private String imageUrl;
	private ImageCropperSelectionMode selectionMode = ImageCropperSelectionMode.RECTANGLE;
	private float aspectRatio = 1;
	private ImageCropperSelection selection;

	public ImageCropper() {
	}

	@Override
	public UiComponent createUiClientObject() {
		UiImageCropper uiImageCropper = new UiImageCropper(imageUrl, selectionMode.toUiImageCropperSelectionMode(), aspectRatio);
		mapAbstractUiComponentProperties(uiImageCropper);
		return uiImageCropper;
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiImageCropper.SelectionChangedEvent.TYPE_ID -> {
				UiImageCropperSelectionWrapper uiSelection = event.as(UiImageCropper.SelectionChangedEventWrapper.class).getSelection();
				ImageCropperSelection selection = new ImageCropperSelection(uiSelection.getLeft(), uiSelection.getTop(), uiSelection.getWidth(), uiSelection.getHeight());
				this.selection = selection;
				this.onSelectionChanged.fire(selection);
			}
		}
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		sendCommandIfRendered(() -> new UiImageCropper.SetImageUrlCommand(imageUrl));
	}

	public ImageCropperSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(ImageCropperSelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		sendCommandIfRendered(() -> new UiImageCropper.SetSelectionModeCommand(selectionMode.toUiImageCropperSelectionMode()));
	}

	public Float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Float aspectRatio) {
		this.aspectRatio = aspectRatio;
		sendCommandIfRendered(() -> new UiImageCropper.SetAspectRatioCommand(aspectRatio));
	}

	public ImageCropperSelection getSelection() {
		return selection;
	}

	public void setSelection(ImageCropperSelection selection) {
		this.selection = selection;
		sendCommandIfRendered(() -> new UiImageCropper.SetSelectionCommand(selection.createUiImageCropperSelection()));
	}

}
