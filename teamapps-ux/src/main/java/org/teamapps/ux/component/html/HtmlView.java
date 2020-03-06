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
package org.teamapps.ux.component.html;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiHtmlView;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HtmlView extends AbstractComponent {

	private String html;
	private final Map<String, List<Component>> componentsByContainerElementSelector = new HashMap<>();
	private final Map<String, String> contentHtmlByContainerElementSelector = new HashMap<>();

	public HtmlView() {
		this(null, Collections.emptyMap(), Collections.emptyMap());
	}

	public HtmlView(String html) {
		this(html, Collections.emptyMap(), Collections.emptyMap());
	}

	public HtmlView(String html, Map<String, List<Component>> componentsByContainerElementSelector, Map<String, String> contentHtmlByContainerElementSelector) {
		this.html = html;
		this.componentsByContainerElementSelector.putAll(componentsByContainerElementSelector);
		this.contentHtmlByContainerElementSelector.putAll(contentHtmlByContainerElementSelector);
	}

	@Override
	public UiComponent createUiComponent() {
		UiHtmlView ui = new UiHtmlView();
		mapAbstractUiComponentProperties(ui);
		ui.setHtml(html);
		ui.setComponentsByContainerElementSelector(componentsByContainerElementSelector.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(ClientObject::createUiReference).collect(Collectors.toList()))));
		ui.setContentHtmlByContainerElementSelector(Map.copyOf(contentHtmlByContainerElementSelector));
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void addComponent(String containerSelector, Component component) {
		this.componentsByContainerElementSelector.computeIfAbsent(containerSelector, s -> new ArrayList<>())
				.add(component);
		this.queueCommandIfRendered(() -> new UiHtmlView.AddComponentCommand(getId(), containerSelector, component.createUiReference()));
	}

	public void removeComponent(Component component) {
		componentsByContainerElementSelector.entrySet().removeIf(entry -> entry.getValue() == component);
		this.queueCommandIfRendered(() -> new UiHtmlView.RemoveComponentCommand(getId(), component.createUiReference()));
	}

	public void setContentHtml(String containerElementSelector, String html) {
		contentHtmlByContainerElementSelector.put(containerElementSelector, html);
		this.queueCommandIfRendered(() -> new UiHtmlView.SetContentHtmlCommand(getId(), containerElementSelector, html));
	}
}
