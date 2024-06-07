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
package org.teamapps.projector.components.common.imagecropper;

import org.teamapps.projector.components.common.dto.DtoComponent;
import org.teamapps.projector.components.common.dto.DtoImageCropper;
import org.teamapps.projector.components.common.dto.DtoImageCropperSelectionWrapper;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;

public class ImageCropper extends AbstractComponent {

	public final ProjectorEvent<ImageCropperSelection> onSelectionChanged = new ProjectorEvent<>(clientObjectChannel::toggleSelectionChangedEvent);

	private String imageUrl;
	private ImageCropperSelectionMode selectionMode = ImageCropperSelectionMode.RECTANGLE;
	private float aspectRatio = 1;
	private ImageCropperSelection selection;

	public ImageCropper() {
	}

	@Override
	public DtoComponent createDto() {
		DtoImageCropper uiImageCropper = new DtoImageCropper(imageUrl, selectionMode.toUiImageCropperSelectionMode(), aspectRatio);
		mapAbstractUiComponentProperties(uiImageCropper);
		return uiImageCropper;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoImageCropper.SelectionChangedEvent.TYPE_ID -> {
				DtoImageCropperSelectionWrapper uiSelection = event.as(DtoImageCropper.SelectionChangedEventWrapper.class).getSelection();
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
		clientObjectChannel.setImageUrl(ImageUrl);
	}

	public ImageCropperSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(ImageCropperSelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		clientObjectChannel.setSelectionMode(SelectionMode.ToUiImageCropperSelectionMode());
	}

	public Float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Float aspectRatio) {
		this.aspectRatio = aspectRatio;
		clientObjectChannel.setAspectRatio(AspectRatio);
	}

	public ImageCropperSelection getSelection() {
		return selection;
	}

	public void setSelection(ImageCropperSelection selection) {
		this.selection = selection;
		clientObjectChannel.setSelection(Selection.CreateUiImageCropperSelection());
	}

}
