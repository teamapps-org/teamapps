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
package org.teamapps.projector.component;

import org.teamapps.projector.clientobject.ClientObject;

/**
 * Represents a UI component that can be rendered on the client side.
 * All components have a corresponding representation in the client's DOM.
 */
public interface Component extends ClientObject {

	/**
	 * Creates a data transfer object (DTO) that represents this component's configuration.
	 * This DTO is sent to the client for rendering the component.
	 *
	 * @return A configuration object for this component
	 */
	@Override
	DtoComponentConfig createDto();

	/**
	 * Checks if this component is currently visible.
	 *
	 * @return true if the component is visible, false otherwise
	 */
	boolean isVisible();

	/**
	 * Sets the visibility of this component.
	 *
	 * @param visible true to make the component visible, false to hide it
	 */
	void setVisible(boolean visible);

	/**
	 * Toggles a CSS class on a specific element within this component.
	 *
	 * @param selector The CSS selector to target a specific element within the component, or null to target the component's root element
	 * @param className The name of the CSS class to toggle
	 * @param enabled true to add the class, false to remove it
	 */
	void toggleCssClass(String selector, String className, boolean enabled);

	/**
	 * Toggles a CSS class on the component's root element.
	 *
	 * @param className The name of the CSS class to toggle
	 * @param enabled true to add the class, false to remove it
	 */
	default void toggleCssClass(String className, boolean enabled) {
		toggleCssClass(null, className, enabled);
	}

	/**
	 * Sets a CSS style property on a specific element within this component.
	 *
	 * @param selector The CSS selector to target a specific element within the component, or null to target the component's root element
	 * @param propertyName The name of the CSS property to set
	 * @param value The value to set for the CSS property
	 */
	void setCssStyle(String selector, String propertyName, String value);

	/**
	 * Sets a CSS style property on the component's root element.
	 *
	 * @param propertyName The name of the CSS property to set
	 * @param value The value to set for the CSS property
	 */
	default void setCssStyle(String propertyName, String value) {
		setCssStyle(null, propertyName, value);
	}

	/**
	 * Sets an HTML attribute on a specific element within this component.
	 *
	 * @param selector The CSS selector to target a specific element within the component, or null to target the component's root element
	 * @param attributeName The name of the HTML attribute to set
	 * @param value The value to set for the HTML attribute
	 */
	void setAttribute(String selector, String attributeName, String value);

	/**
	 * Sets an HTML attribute on the component's root element.
	 *
	 * @param attributeName The name of the HTML attribute to set
	 * @param value The value to set for the HTML attribute
	 */
	default void setAttribute(String attributeName, String value) {
		setAttribute(null, attributeName, value);
	}

	/**
	 * Sets the ARIA label for accessibility purposes.
	 * This is a convenience method that sets the "aria-label" attribute.
	 *
	 * @param ariaLabel The accessibility label to set
	 */
	default void setAriaLabel(String ariaLabel) {
		setAttribute("aria-label", ariaLabel);
	}

	/**
	 * Sets the HTML title attribute, which typically displays as a tooltip.
	 * This is a convenience method that sets the "title" attribute.
	 *
	 * @param title The title text to set
	 */
	default void setHtmlTitle(String title) {
		setAttribute("title", title);
	}

}
