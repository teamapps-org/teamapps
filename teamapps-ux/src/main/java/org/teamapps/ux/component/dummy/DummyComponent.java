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
package org.teamapps.ux.component.dummy;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoDummyComponent;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class DummyComponent extends AbstractComponent {

	public final ProjectorEvent<Void> onClick = createProjectorEventBoundToUiEvent(DtoDummyComponent.ClickedEvent.TYPE_ID);

	private String text;

	public DummyComponent(String text) {
		this.text = text;
	}

	public DummyComponent() {
		this(null);
	}

	@Override
	public DtoComponent createUiClientObject() {
		DtoDummyComponent dummyComponent = new DtoDummyComponent();
		mapAbstractUiComponentProperties(dummyComponent);
		dummyComponent.setText(text);
		return dummyComponent;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoDummyComponent.ClickedEvent.TYPE_ID -> {
				onClick.fire(null);
			}
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		sendCommandIfRendered(() -> new DtoDummyComponent.SetTextCommand(text));
	}
}
