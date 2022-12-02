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
package org.teamapps.ux.component.field;

import org.teamapps.dto.DtoDisplayField;
import org.teamapps.dto.DtoField;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class DisplayField extends AbstractField<String> {

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
	public DtoField createUiClientObject() {
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
		sendCommandIfRendered(() -> new DtoDisplayField.SetShowBorderCommand(showBorder));
		return this;
	}

	public boolean isShowHtml() {
		return showHtml;
	}

	public DisplayField setShowHtml(boolean showHtml) {
		this.showHtml = showHtml;
		sendCommandIfRendered(() -> new DtoDisplayField.SetShowHtmlCommand(showHtml));
		return this;
	}

	public boolean isRemoveStyleTags() {
		return removeStyleTags;
	}

	public DisplayField setRemoveStyleTags(boolean removeStyleTags) {
		this.removeStyleTags = removeStyleTags;
		sendCommandIfRendered(() -> new DtoDisplayField.SetRemoveStyleTagsCommand(removeStyleTags));
		return this;
	}
}
