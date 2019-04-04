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
package org.teamapps.ux.component;

import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.ux.component.absolutelayout.Length;
import org.teamapps.ux.component.format.Shadow;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.session.SessionContext;

public interface Component {

	/**
	 * Used internally for setting the component's container. May only be invoked by the new container!!
	 */
	void setParent(Container container);

	Container getParent();

	String getId();

	SessionContext getSessionContext();

	void render();

	void unrender();

	UiComponentReference createUiComponentReference();

	boolean isRendered();

	void handleUiEvent(UiEvent event);

	boolean isDestroyed();

	void destroy();

	boolean isVisible();

	void setVisible(boolean visible);

	boolean isEffectivelyVisible();

	// ===== CSS =====

	void setMinWidth(Length minWidth);
	void setMaxWidth(Length maxWidth);
	void setMinHeight(Length minHeight);
	void setMaxHeight(Length maxHeight);
	void setMargin(Spacing margin);
	void setShadow(Shadow shadow);

	void setCssStyle(String selector, String propertyName, String value);
	
	default void setCssStyle(String propertyName, String value) {
		setCssStyle(null, propertyName, value);
	}

//	void updateEffectiveVisibility();
//	Event<Boolean> onEffectiveVisibilityChanged();

	// == static methods ==

	static UiComponentReference createUiComponentReference(Component component) {
		if (component == null) {
			return null;
		}
		return component.createUiComponentReference();
	}

}
