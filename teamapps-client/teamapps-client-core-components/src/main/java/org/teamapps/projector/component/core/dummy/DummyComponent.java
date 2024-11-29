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
package org.teamapps.projector.component.core.dummy;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoDummyComponent;
import org.teamapps.projector.component.core.DtoDummyComponentClientObjectChannel;
import org.teamapps.projector.component.core.DtoDummyComponentEventHandler;
import org.teamapps.projector.event.ProjectorEvent;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class DummyComponent extends AbstractComponent implements DtoDummyComponentEventHandler {

	private final DtoDummyComponentClientObjectChannel clientObjectChannel = new DtoDummyComponentClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClick = new ProjectorEvent<>(clientObjectChannel::toggleClickEvent);

	private String text;

	public DummyComponent(String text) {
		this.text = text;
	}

	public DummyComponent() {
		this(null);
	}

	@Override
	public DtoComponent createConfig() {
		DtoDummyComponent dummyComponent = new DtoDummyComponent();
		mapAbstractConfigProperties(dummyComponent);
		dummyComponent.setText(text);
		return dummyComponent;
	}

	@Override
	public void handleClick(int clickCount) {
		onClick.fire();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		clientObjectChannel.setText(text);
	}
}