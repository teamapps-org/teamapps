/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.animation.PageTransition;

public class RootPanel extends AbstractComponent implements Component {

	private Component content;

	@Override
	public UiComponent createUiComponent() {
		UiRootPanel uiRootPanel = new UiRootPanel();
		mapAbstractUiComponentProperties(uiRootPanel);
		uiRootPanel.setContent(content != null ? content.createUiReference() : null);
		return uiRootPanel;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		// no ui events for this component
	}

	public void preloadContent(Component component) {
		component.render();
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, long animationDuration) {
		if (component != null) {
			preloadContent(component);
		}
		content = component;
		if (component != null) {
			component.setParent(this);
		}
		queueCommandIfRendered(() -> new UiRootPanel.SetContentCommand(getId(), component != null ? component.createUiReference() : null, animation != null ? animation.toUiPageTransition() : null, animationDuration));
	}

	public Component getContent() {
		return content;
	}

}
