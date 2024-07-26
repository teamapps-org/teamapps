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
package org.teamapps.projector.component.filefield.imagecropper;

import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.component.common.*;
import org.teamapps.projector.event.ProjectorEvent;

public class ImageCropper extends AbstractComponent implements DtoImageCropperEventHandler {

	private final DtoImageCropperClientObjectChannel clientObjectChannel = new DtoImageCropperClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<ImageCropperSelection> onSelectionChanged = new ProjectorEvent<>(clientObjectChannel::toggleSelectionChangedEvent);

	private String imageUrl;
	private ImageCropperSelectionMode selectionMode = ImageCropperSelectionMode.RECTANGLE;
	private float aspectRatio = 1;
	private ImageCropperSelection selection;

	public ImageCropper() {
	}

	@Override
	public ComponentConfig createConfig() {
		DtoImageCropper uiImageCropper = new DtoImageCropper(imageUrl, selectionMode, aspectRatio);
		mapAbstractUiComponentProperties(uiImageCropper);
		return uiImageCropper;
	}

	@Override
	public void handleSelectionChanged(ImageCropperSelectionWrapper selection) {
		this.selection = selection.unwrap();
		this.onSelectionChanged.fire(this.selection);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		clientObjectChannel.setImageUrl(imageUrl);
	}

	public ImageCropperSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(ImageCropperSelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		clientObjectChannel.setSelectionMode(selectionMode);
	}

	public Float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Float aspectRatio) {
		this.aspectRatio = aspectRatio;
		clientObjectChannel.setAspectRatio(aspectRatio);
	}

	public ImageCropperSelection getSelection() {
		return selection;
	}

	public void setSelection(ImageCropperSelection selection) {
		this.selection = selection;
		clientObjectChannel.setSelection(selection);
	}

}
