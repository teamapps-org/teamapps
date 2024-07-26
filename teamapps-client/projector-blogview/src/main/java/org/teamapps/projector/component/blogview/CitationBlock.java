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
package org.teamapps.projector.component.blogview;

public class CitationBlock extends AbstractBlock {
	
	private String creatorImageUrl;
	private CreatorImageAlignment creatorImageAlignment = CreatorImageAlignment.LEFT;
	private String citation;
	private String author;

	public CitationBlock() {
	}

	public CitationBlock(String creatorImageUrl, String citation, String author) {
		this.creatorImageUrl = creatorImageUrl;
		this.citation = citation;
		this.author = author;
	}

	public DtoCitationBlock createUiBlock() {
		DtoCitationBlock uiBlock = new DtoCitationBlock();
		mapAbstractBlockAttributes(uiBlock);
		uiBlock.setCreatorImageUrl(creatorImageUrl);
		uiBlock.setCreatorImageAlignment(creatorImageAlignment);
		uiBlock.setCitation(citation);
		uiBlock.setAuthor(author);
		return uiBlock;
	}

	public String getCreatorImageUrl() {
		return creatorImageUrl;
	}

	public CitationBlock setCreatorImageUrl(String creatorImageUrl) {
		this.creatorImageUrl = creatorImageUrl;
		return this;
	}

	public CreatorImageAlignment getCreatorImageAlignment() {
		return creatorImageAlignment;
	}

	public CitationBlock setCreatorImageAlignment(CreatorImageAlignment creatorImageAlignment) {
		this.creatorImageAlignment = creatorImageAlignment;
		return this;
	}

	public String getCitation() {
		return citation;
	}

	public CitationBlock setCitation(String citation) {
		this.citation = citation;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public CitationBlock setAuthor(String author) {
		this.author = author;
		return this;
	}
}
