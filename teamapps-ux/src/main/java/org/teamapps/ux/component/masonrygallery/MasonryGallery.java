/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.masonrygallery;

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMasonryGallery;
import org.teamapps.dto.UiMasonryGalleryImage;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;

public class MasonryGallery extends AbstractComponent {

	public final Event<Integer> onImageClicked = new Event<>();

	private List<MasonryGalleryImage> images;

	public MasonryGallery(List<MasonryGalleryImage> images) {
		this.images = copyImages(images);
	}

	@Override
	public UiMasonryGallery createUiComponent() {
		UiMasonryGallery uiMasonryGallery = new UiMasonryGallery(createUiImages());
		mapAbstractUiComponentProperties(uiMasonryGallery);
		return uiMasonryGallery;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MASONRY_GALLERY_IMAGE_CLICKED:
				UiMasonryGallery.ImageClickedEvent clickedEvent = (UiMasonryGallery.ImageClickedEvent) event;
				onImageClicked.fire(clickedEvent.getImageIndex());
				break;
		}
	}

	public List<MasonryGalleryImage> getImages() {
		return images;
	}

	public void setImages(List<MasonryGalleryImage> images) {
		this.images = copyImages(images);
		queueCommandIfRendered(() -> new UiMasonryGallery.SetImagesCommand(getId(), createUiImages()));
	}

	private List<UiMasonryGalleryImage> createUiImages() {
		return images.stream().map(image -> new UiMasonryGalleryImage(image.getImage().getUrl(getSessionContext()))
				.setFileName(image.getFileName())
				.setThumbnailUrl(image.getThumbnail() != null ? image.getThumbnail().getUrl(getSessionContext()) : null)
		).toList();
	}

	private static List<MasonryGalleryImage> copyImages(List<MasonryGalleryImage> images) {
		return images != null ? List.copyOf(images) : List.of();
	}
}
