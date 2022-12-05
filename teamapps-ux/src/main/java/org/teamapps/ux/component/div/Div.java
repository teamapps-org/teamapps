/*-
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
package org.teamapps.ux.component.div;

import org.teamapps.dto.DtoDiv;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class Div extends AbstractComponent {

	private Component content;
	private String innerHtml;

	public Div(Component content) {
		this.content = content;
	}

	public Div(String innerHtml) {
		this.innerHtml = innerHtml;
	}

	public Div() {
	}

	@Override
	public DtoDiv createDto() {
		DtoDiv ui = new DtoDiv();
		mapAbstractUiComponentProperties(ui);
		ui.setContent(content != null ? content.createDtoReference() : null);
		ui.setInnerHtml(innerHtml);
		return ui;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
		sendCommandIfRendered(() -> new DtoDiv.SetContentCommand(content != null ? content.createDtoReference() : null));
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
		sendCommandIfRendered(() -> new DtoDiv.SetInnerHtmlCommand(innerHtml));
	}
}
