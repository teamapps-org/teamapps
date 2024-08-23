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
package org.teamapps.projector.component.essential.html;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.essential.DtoHtmlView;
import org.teamapps.projector.component.essential.DtoHtmlViewClientObjectChannel;
import org.teamapps.projector.component.essential.DtoHtmlViewEventHandler;

import java.util.*;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class HtmlView extends AbstractComponent implements DtoHtmlViewEventHandler {

	private final DtoHtmlViewClientObjectChannel clientObjectChannel = new DtoHtmlViewClientObjectChannel(getClientObjectChannel());

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
	public DtoComponent createConfig() {
		DtoHtmlView ui = new DtoHtmlView();
		mapAbstractConfigProperties(ui);
		ui.setHtml(html);
		ui.setComponentsByContainerElementSelector(componentsByContainerElementSelector.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(c -> c).collect(Collectors.toList()))));
		ui.setContentHtmlByContainerElementSelector(contentHtmlByContainerElementSelector.entrySet().stream()
				.filter(entry -> entry.getValue() != null) // Map.copyOf() does not support null values!
				.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue)));
		return ui;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void addComponent(String containerSelector, Component component) {
		addComponent(containerSelector, component, false);
	}

	public void addComponent(String containerSelector, Component component, boolean clearContainer) {
		this.componentsByContainerElementSelector.computeIfAbsent(containerSelector, s -> new ArrayList<>())
				.add(component);
		clientObjectChannel.addComponent(containerSelector, component, clearContainer);
	}

	public void removeComponent(Component component) {
		componentsByContainerElementSelector.entrySet().removeIf(entry -> entry.getValue() == component);
		clientObjectChannel.removeComponent(component);
	}

	public void setContentHtml(String containerElementSelector, String html) {
		contentHtmlByContainerElementSelector.put(containerElementSelector, html);
		clientObjectChannel.setContentHtml(containerElementSelector, html);
	}
}
