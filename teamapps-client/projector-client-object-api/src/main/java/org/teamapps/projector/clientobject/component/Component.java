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
package org.teamapps.projector.clientobject.component;

import org.teamapps.projector.clientobject.ClientObject;

public interface Component extends ClientObject {

	@Override
	ComponentConfig createConfig();

	boolean isVisible();

	void setVisible(boolean visible);

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

	default void setAriaLabel(String ariaLabel) {
		setAttribute("aria-label", ariaLabel);
	}

	default void setHtmlTitle(String title) {
		setAttribute("title", title);
	}

	// == static methods ==

}
