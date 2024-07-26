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
package org.teamapps.projector.component.essential.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.essential.DtoDisplayField;
import org.teamapps.projector.component.essential.DtoDisplayFieldClientObjectChannel;
import org.teamapps.projector.component.essential.DtoDisplayFieldEventHandler;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class DisplayField extends AbstractField<String> implements DtoDisplayFieldEventHandler {

	private final DtoDisplayFieldClientObjectChannel clientObjectChannel = new DtoDisplayFieldClientObjectChannel(getClientObjectChannel());

	private boolean showBorder;
	private boolean showHtml;
	private boolean removeStyleTags = true;

	public DisplayField() {
		super();
	}

	public DisplayField(boolean showBorder, boolean showHtml) {
		this();
		this.showBorder = showBorder;
		this.showHtml = showHtml;
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoDisplayField uiDisplayField = new DtoDisplayField();
		mapAbstractFieldAttributesToUiField(uiDisplayField);
		uiDisplayField.setShowBorder(showBorder);
		uiDisplayField.setShowHtml(showHtml);
		uiDisplayField.setRemoveStyleTags(removeStyleTags);
		return uiDisplayField;
	}

	public boolean isShowBorder() {
		return showBorder;
	}

	public DisplayField setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
		clientObjectChannel.setShowBorder(showBorder);
		return this;
	}

	public boolean isShowHtml() {
		return showHtml;
	}

	public DisplayField setShowHtml(boolean showHtml) {
		this.showHtml = showHtml;
		clientObjectChannel.setShowHtml(showHtml);
		return this;
	}

	public boolean isRemoveStyleTags() {
		return removeStyleTags;
	}

	public DisplayField setRemoveStyleTags(boolean removeStyleTags) {
		this.removeStyleTags = removeStyleTags;
		clientObjectChannel.setRemoveStyleTags(removeStyleTags);
		return this;
	}

	@Override
	public String doConvertClientValueToServerValue(JsonNode value) {
		return value.textValue();
	}
}
