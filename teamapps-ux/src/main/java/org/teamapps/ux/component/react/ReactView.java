/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.react;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiReactView;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReactView extends AbstractComponent {

	private String jsx;
	private final Map<String, Component> componentByContainerRefName = new HashMap<>();
	private final Map<String, String> propValueByPropName = new HashMap<>();

	public ReactView() {
		this(null, Collections.emptyMap(), Collections.emptyMap());
	}

	public ReactView(String jsx) {
		this(jsx, Collections.emptyMap(), Collections.emptyMap());
	}

	public ReactView(String jsx, Map<String, Component> componentByContainerRefName, Map<String, String> propValueByPropName) {
		this.jsx = jsx;
		this.componentByContainerRefName.putAll(componentByContainerRefName);
		this.propValueByPropName.putAll(propValueByPropName);
	}

	@Override
	public UiComponent createUiComponent() {
		UiReactView ui = new UiReactView();
		mapAbstractUiComponentProperties(ui);
		ui.setJsx(jsx);
		ui.setComponentByRefName(componentByContainerRefName.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createUiReference())));
		ui.setPropValueByPropName(propValueByPropName.entrySet().stream()
				.filter(entry -> entry.getValue() != null) // Map.copyOf() does not support null values!
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)));
		return ui;
	}

	public String getJsx() {
		return jsx;
	}

	public void setJsx(String jsx) {
		this.jsx = jsx;
	}

	public void addComponent(String containerRefName, Component component) {
		this.componentByContainerRefName.put(containerRefName, component);
		this.queueCommandIfRendered(() -> new UiReactView.AddComponentCommand(getId(), containerRefName, component.createUiReference()));
	}

	public void removeComponent(Component component) {
		componentByContainerRefName.entrySet().removeIf(entry -> entry.getValue() == component);
		this.queueCommandIfRendered(() -> new UiReactView.RemoveComponentCommand(getId(), component.createUiReference()));
	}

	public void setProp(String propName, String value) {
		propValueByPropName.put(propName, value);
		this.queueCommandIfRendered(() -> new UiReactView.SetPropCommand(getId(), propName, value));
	}


}
