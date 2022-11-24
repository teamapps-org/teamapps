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
package org.teamapps.ux.component;

import org.teamapps.dto.DtoClientObjectReference;
import org.teamapps.ux.component.absolutelayout.Length;
import org.teamapps.ux.component.format.Shadow;
import org.teamapps.ux.component.format.Spacing;

public interface Component extends ClientObject {

	/**
	 * Used internally for setting the component's container. May only be invoked by the new container!!
	 */
	void setParent(Component container);

	Component getParent();

	boolean isVisible();

	void setVisible(boolean visible);

	// ===== CSS =====

	void toggleCssClass(String selector, String className, boolean enabled);

	default void toggleCssClass(String className, boolean enabled) {
		toggleCssClass(null, className, enabled);
	}

	void setCssStyle(String selector, String propertyName, String value);

	default void setCssStyle(String propertyName, String value) {
		setCssStyle(null, propertyName, value);
	}

	void setAttribute(String selector, String attributeName, String value);

	default void setAttribute(String attributeName, String value) {
		setAttribute(null, attributeName, value);
	}

	default void setMinWidth(Length minWidth) {
		setCssStyle("min-width", minWidth.toCssString());
	}

	default void setMaxWidth(Length maxWidth) {
		setCssStyle("max-width", maxWidth.toCssString());
	}

	default void setMinHeight(Length minHeight) {
		setCssStyle("min-height", minHeight.toCssString());
	}

	default void setMaxHeight(Length maxHeight) {
		setCssStyle("max-height", maxHeight.toCssString());
	}

	default void setMargin(Spacing margin) {
		setCssStyle("margin", margin.toCssString());
	}

	default void setShadow(Shadow shadow) {
		setCssStyle("box-shadow", shadow.toCssString());
	}

	default void setAriaLabel(String ariaLabel) {
		setAttribute("aria-label", ariaLabel);
	}

	default void setHtmlTitle(String title) {
		setAttribute("title", title);
	}

	// == static methods ==

	static DtoClientObjectReference createUiClientObjectReference(Component component) {
		if (component == null) {
			return null;
		}
		return component.createUiReference();
	}

}
