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
package org.teamapps.projector.components.common.pageview;

import org.teamapps.projector.components.common.dto.DtoCitationPageViewBlock;

public class CitationPageViewBlock extends AbstractPageViewBlock {
	
	private String creatorImageUrl;
	private CreatorImageAlignment creatorImageAlignment = CreatorImageAlignment.LEFT;
	private String citation;
	private String author;

	public CitationPageViewBlock() {
	}

	public CitationPageViewBlock(String creatorImageUrl, String citation, String author) {
		this.creatorImageUrl = creatorImageUrl;
		this.citation = citation;
		this.author = author;
	}

	public DtoCitationPageViewBlock createUiBlock() {
		DtoCitationPageViewBlock uiBlock = new DtoCitationPageViewBlock();
		mapAbstractPageViewBlockAttributes(uiBlock);
		uiBlock.setCreatorImageUrl(creatorImageUrl);
		uiBlock.setCreatorImageAlignment(creatorImageAlignment.toUiImageAligment());
		uiBlock.setCitation(citation);
		uiBlock.setAuthor(author);
		return uiBlock;
	}

	public String getCreatorImageUrl() {
		return creatorImageUrl;
	}

	public CitationPageViewBlock setCreatorImageUrl(String creatorImageUrl) {
		this.creatorImageUrl = creatorImageUrl;
		return this;
	}

	public CreatorImageAlignment getCreatorImageAlignment() {
		return creatorImageAlignment;
	}

	public CitationPageViewBlock setCreatorImageAlignment(CreatorImageAlignment creatorImageAlignment) {
		this.creatorImageAlignment = creatorImageAlignment;
		return this;
	}

	public String getCitation() {
		return citation;
	}

	public CitationPageViewBlock setCitation(String citation) {
		this.citation = citation;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public CitationPageViewBlock setAuthor(String author) {
		this.author = author;
		return this;
	}
}
