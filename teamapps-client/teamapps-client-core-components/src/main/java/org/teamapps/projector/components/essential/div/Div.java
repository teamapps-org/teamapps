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
package org.teamapps.projector.components.essential.div;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.components.essential.CoreComponentLibrary;
import org.teamapps.projector.components.essential.dto.DtoDiv;
import org.teamapps.projector.components.essential.dto.DtoDivClientObjectChannel;
import org.teamapps.projector.components.essential.dto.DtoDivEventHandler;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Div extends AbstractComponent implements DtoDivEventHandler {

	private final DtoDivClientObjectChannel clientObjectChannel = new DtoDivClientObjectChannel(getClientObjectChannel());

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
	public DtoDiv createConfig() {
		DtoDiv ui = new DtoDiv();
		mapAbstractUiComponentProperties(ui);
		ui.setContent(content != null ? content : null);
		ui.setInnerHtml(innerHtml);
		return ui;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
		clientObjectChannel.setContent(content);
	}

	public String getInnerHtml() {
		return innerHtml;
	}

	public void setInnerHtml(String innerHtml) {
		this.innerHtml = innerHtml;
		clientObjectChannel.setInnerHtml(innerHtml);
	}
}
