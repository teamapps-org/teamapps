/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.rootpanel;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RootPanel extends AbstractComponent implements Container {

	private Component content;
	private List<Component> components = new ArrayList<>();

	@Override
	protected void doDestroy() {
		this.components.forEach(Component::destroy);
	}

	@Override
	public UiComponent createUiComponent() {
		UiRootPanel uiRootPanel = new UiRootPanel(getId());
		mapAbstractUiComponentProperties(uiRootPanel);
		if (content != null) {
			uiRootPanel.setVisibleChildComponentId(content.getId());
		}
		if (!components.isEmpty()) {
			List<UiComponentReference> uiComponents = components.stream()
					.map(component -> component.createUiComponentReference())
					.collect(Collectors.toList());
			uiRootPanel.setChildComponents(uiComponents);
		}
		return uiRootPanel;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		// no ui events for this component
	}

	// TODO #componentRef still needed after component reference implementation??
	public void preloadContent(Component component) {
		if (!components.contains(component)) {
			this.components.add(component);
			component.setParent(this);
			queueCommandIfRendered(() -> new UiRootPanel.AddChildComponentCommand(getId(), component.createUiComponentReference(), false));
		}
	}

	public void setContent(Component component) {
		setContent(component, Animation.NONE, 0);
	}

	public void setContent(Component component, Animation animation, long animationDuration) {
		if (component != null) {
			preloadContent(component);
		}
		content = component;
		queueCommandIfRendered(() -> new UiRootPanel.SetVisibleChildComponentCommand(getId(), component != null ? component.getId() : null, animation.toUiComponentRevealAnimation(), animationDuration));
	}

	public void removeComponent(Component component) {
		components.remove(component);
		if (content == component) {
			setContent(components.isEmpty() ? null : components.get(0));
		}
		queueCommandIfRendered(() -> new UiRootPanel.RemoveChildComponentCommand(getId(), component.getId()));
	}

	public void removeAllComponents() {
		components.stream().forEach(component -> {
			queueCommandIfRendered(() -> new UiRootPanel.RemoveChildComponentCommand(getId(), component.getId()));
		});
		components.clear();
		setContent(null);
	}

	public Component getContent() {
		return content;
	}

	public List<Component> getComponents() {
		return components;
	}

	@Override
	public boolean isEffectivelyVisible() {
		return isRendered() && isVisible();
	}

	@Override
	public boolean isChildVisible(Component child) {
		return isVisible() && content == child;
	}
}
