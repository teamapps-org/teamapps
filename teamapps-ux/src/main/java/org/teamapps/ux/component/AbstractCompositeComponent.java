/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component;

import org.teamapps.dto.UiClientObjectReference;

/**
 * A base class that can be used by application developers in order to create their own components that consist of a composition of
 * other components.
 * <p>
 * Example: A VerticalLayout containing toolbar with three buttons and a table, interacting in a certain manner.
 * <p>
 * Using this superclass, the composite component is a {@link Component} but not a VerticalLayout (see example above).
 * <p>
 * In particular, this means that this component can be used in the same way as any other component, including being added as child
 * to other components.
 */
public abstract class AbstractCompositeComponent implements Component {

	/**
	 * @return the top-level component of this composite component
	 */
	abstract public Component getMainComponent();

	@Override
	public String getId() {
		return getMainComponent().getId();
	}

	@Override
	public void render() {
		getMainComponent().render();
	}

	@Override
	public void unrender() {
		getMainComponent().unrender();
	}

	@Override
	public boolean isRendered() {
		return getMainComponent().isRendered();
	}

	@Override
	public UiClientObjectReference createUiReference() {
		return getMainComponent().createUiReference();
	}

	@Override
	public void setParent(Component container) {
		getMainComponent().setParent(container);
	}

	@Override
	public Component getParent() {
		return getMainComponent().getParent();
	}

	@Override
	public boolean isVisible() {
		return getMainComponent().isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		getMainComponent().setVisible(visible);
	}

	@Override
	public void toggleCssClass(String selector, String className, boolean enabled) {
		getMainComponent().toggleCssClass(selector, className, enabled);
	}

	@Override
	public void setCssStyle(String selector, String propertyName, String value) {
		getMainComponent().setCssStyle(selector, propertyName, value);
	}

	@Override
	public void setAttribute(String selector, String attributeName, String value) {
		getMainComponent().setAttribute(selector, attributeName, value);
	}
}
