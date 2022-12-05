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
package org.teamapps.ux.component.pageview;

import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;
import org.teamapps.dto.DtoClientRecord;
import org.teamapps.dto.DtoMessagePageViewBlock;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.List;

public class MessagePageViewBlock<RECORD> extends AbstractPageViewBlock {

	private Template topTemplate = BaseTemplate.LIST_ITEM_LARGE_ICON_TWO_LINES;
	private RECORD topRecord;
	private PropertyProvider<RECORD> topRecordPropertyProvider = new BeanPropertyExtractor<>();
	private HorizontalElementAlignment topRecordAlignment = HorizontalElementAlignment.LEFT;
	private String html;
	private List<String> imageUrls;

	public MessagePageViewBlock() {
	}

	public MessagePageViewBlock(Template topTemplate, RECORD topRecord) {
		this.topTemplate = topTemplate;
		this.topRecord = topRecord;
	}

	public MessagePageViewBlock(PageViewBlockAlignment alignment, Template topTemplate, RECORD topRecord) {
		super(alignment);
		this.topTemplate = topTemplate;
		this.topRecord = topRecord;
	}

	public DtoMessagePageViewBlock createUiBlock() {
		DtoMessagePageViewBlock uiBlock = new DtoMessagePageViewBlock();
		mapAbstractPageViewBlockAttributes(uiBlock);
		uiBlock.setTopTemplate(topTemplate.createDtoReference());
		uiBlock.setTopRecord(topRecord != null ? new DtoClientRecord().setValues(topRecordPropertyProvider.getValues(topRecord, topTemplate.getPropertyNames())) : null);
		uiBlock.setTopRecordAlignment(topRecordAlignment.toUiHorizontalElementAlignment());
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

	public HorizontalElementAlignment getTopRecordAlignment() {
		return topRecordAlignment;
	}

	public void setTopRecordAlignment(HorizontalElementAlignment topRecordAlignment) {
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
