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
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class Div extends AbstractComponent {

	private org.teamapps.ux.component.Component content;
	private String innerHtml;

	public Div(org.teamapps.ux.component.Component content) {
		this.content = content;
	}

	public Div(String innerHtml) {
		this.innerHtml = innerHtml;
	}

	public Div() {
	}

	@Override
	public DtoDiv createConfig() {
		DtoDiv ui = new DtoDiv();
		mapAbstractUiComponentProperties(ui);
		ui.setContent(content != null ? content.createClientReference() : null);
		ui.setInnerHtml(innerHtml);
		return ui;
	}

	public org.teamapps.ux.component.Component getContent() {
		return content;
	}

	public void setContent(org.teamapps.ux.component.Component content) {
		this.content = content;
		sendCommandIfRendered(() -> new DtoDiv.SetContentCommand(content != null ? content.createClientReference() : null));
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
		sendCommandIfRendered(() -> new DtoDiv.SetInnerHtmlCommand(innerHtml));
	}
}
