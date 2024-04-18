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
package org.teamapps.ux.component.html;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoHtmlView;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.ux.component.*;
import org.teamapps.ux.component.annotations.ProjectorComponent;

import java.util.*;
import java.util.stream.Collectors;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class HtmlView extends AbstractComponent {

	private String html;
	private final Map<String, List<org.teamapps.ux.component.Component>> componentsByContainerElementSelector = new HashMap<>();
	private final Map<String, String> contentHtmlByContainerElementSelector = new HashMap<>();

	public HtmlView() {
		this(null, Collections.emptyMap(), Collections.emptyMap());
	}

	public HtmlView(String html) {
		this(html, Collections.emptyMap(), Collections.emptyMap());
	}

	public HtmlView(String html, Map<String, List<org.teamapps.ux.component.Component>> componentsByContainerElementSelector, Map<String, String> contentHtmlByContainerElementSelector) {
		this.html = html;
		this.componentsByContainerElementSelector.putAll(componentsByContainerElementSelector);
		this.contentHtmlByContainerElementSelector.putAll(contentHtmlByContainerElementSelector);
	}

	@Override
	public DtoComponent createConfig() {
		DtoHtmlView ui = new DtoHtmlView();
		mapAbstractUiComponentProperties(ui);
		ui.setHtml(html);
		ui.setComponentsByContainerElementSelector(componentsByContainerElementSelector.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(c -> c.createClientReference()).collect(Collectors.toList()))));
		ui.setContentHtmlByContainerElementSelector(contentHtmlByContainerElementSelector.entrySet().stream()
				.filter(entry -> entry.getValue() != null) // Map.copyOf() does not support null values!
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)));
		return ui;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {

	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void addComponent(String containerSelector, org.teamapps.ux.component.Component component) {
		addComponent(containerSelector, component, false);
	}

	public void addComponent(String containerSelector, org.teamapps.ux.component.Component component, boolean clearContainer) {
		this.componentsByContainerElementSelector.computeIfAbsent(containerSelector, s -> new ArrayList<>())
				.add(component);
		this.sendCommandIfRendered(() -> new DtoHtmlView.AddComponentCommand(containerSelector, component.createClientReference(), clearContainer));
	}

	public void removeComponent(org.teamapps.ux.component.Component component) {
		componentsByContainerElementSelector.entrySet().removeIf(entry -> entry.getValue() == component);
		this.sendCommandIfRendered(() -> new DtoHtmlView.RemoveComponentCommand(component.createClientReference()));
	}

	public void setContentHtml(String containerElementSelector, String html) {
		contentHtmlByContainerElementSelector.put(containerElementSelector, html);
		this.sendCommandIfRendered(() -> new DtoHtmlView.SetContentHtmlCommand(containerElementSelector, html));
	}
}
