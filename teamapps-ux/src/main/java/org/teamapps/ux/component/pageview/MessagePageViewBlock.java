/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.pageview;

import org.teamapps.dto.UiMessagePageViewBlock;

import java.util.List;

public class MessagePageViewBlock extends AbstractPageViewBlock {

	private String creatorImageUrl;
	private CreatorImageAlignment creatorImageAlignment = CreatorImageAlignment.LEFT;
	private String headLine;
	private String richText;
	private List<String> imageUrls;

	public UiMessagePageViewBlock createUiBlock() {
		UiMessagePageViewBlock uiBlock = new UiMessagePageViewBlock();
		mapAbstractPageViewBlockAttributes(uiBlock);
		uiBlock.setCreatorImageUrl(creatorImageUrl);
		uiBlock.setCreatorImageAlignment(creatorImageAlignment.toUiImageAligment());
		uiBlock.setHeadLine(headLine);
		uiBlock.setText(richText);
		uiBlock.setImageUrls(imageUrls);
		return uiBlock;
	}

	public String getCreatorImageUrl() {
		return creatorImageUrl;
	}

	public MessagePageViewBlock setCreatorImageUrl(String creatorImageUrl) {
		this.creatorImageUrl = creatorImageUrl;
		return this;
	}

	public CreatorImageAlignment getCreatorImageAlignment() {
		return creatorImageAlignment;
	}

	public MessagePageViewBlock setCreatorImageAlignment(CreatorImageAlignment creatorImageAlignment) {
		this.creatorImageAlignment = creatorImageAlignment;
		return this;
	}

	public String getHeadLine() {
		return headLine;
	}

	public MessagePageViewBlock setHeadLine(String headLine) {
		this.headLine = headLine;
		return this;
	}

	public String getRichText() {
		return richText;
	}

	public MessagePageViewBlock setRichText(String richText) {
		this.richText = richText;
		return this;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public MessagePageViewBlock setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
		return this;
	}
}
