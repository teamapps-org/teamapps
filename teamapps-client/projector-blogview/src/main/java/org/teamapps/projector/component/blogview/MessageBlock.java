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

import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.record.DtoClientRecord;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.util.List;

public class MessageBlock<RECORD> extends AbstractBlock {

	private Template topTemplate = BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES;
	private RECORD topRecord;
	private PropertyProvider<RECORD> topRecordPropertyProvider = new BeanPropertyExtractor<>();
	private TopRecordElementAlignment topRecordAlignment = TopRecordElementAlignment.LEFT;
	private String html;
	private List<String> imageUrls;

	public MessageBlock() {
	}

	public MessageBlock(Template topTemplate, RECORD topRecord) {
		this.topTemplate = topTemplate;
		this.topRecord = topRecord;
	}

	public MessageBlock(BlockAlignment alignment, Template topTemplate, RECORD topRecord) {
		super(alignment);
		this.topTemplate = topTemplate;
		this.topRecord = topRecord;
	}

	public DtoMessageBlock createUiBlock() {
		DtoMessageBlock uiBlock = new DtoMessageBlock();
		mapAbstractBlockAttributes(uiBlock);
		uiBlock.setTopTemplate(topTemplate != null ? topTemplate : null);
		uiBlock.setTopRecord(topRecord != null ? new DtoClientRecord().setValues(topRecordPropertyProvider.getValues(topRecord, topTemplate.getPropertyNames())) : null);
		uiBlock.setTopRecordAlignment(topRecordAlignment);
		uiBlock.setHtml(html);
		uiBlock.setImageUrls(imageUrls);
		return uiBlock;
	}

	public Template getTopTemplate() {
		return topTemplate;
	}

	public void setTopTemplate(Template topTemplate) {
		this.topTemplate = topTemplate;
	}

	public RECORD getTopRecord() {
		return topRecord;
	}

	public void setTopRecord(RECORD topRecord) {
		this.topRecord = topRecord;
	}

	public PropertyProvider<RECORD> getTopRecordPropertyProvider() {
		return topRecordPropertyProvider;
	}

	public void setTopRecordPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.topRecordPropertyProvider = propertyProvider;
	}

	public void setTopRecordPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setTopRecordPropertyProvider(propertyExtractor);
	}

	public TopRecordElementAlignment getTopRecordAlignment() {
		return topRecordAlignment;
	}

	public void setTopRecordAlignment(TopRecordElementAlignment topRecordAlignment) {
		this.topRecordAlignment = topRecordAlignment;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
}
