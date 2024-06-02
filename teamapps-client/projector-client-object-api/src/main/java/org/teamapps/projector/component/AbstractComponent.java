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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.clientobject.ClientObjectChannel;
import org.teamapps.projector.css.CssStyles;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractComponent implements Component {

	public static final String DELETED_ATTRIBUTE = "__ta-deleted-attribute__"; // null object

	private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final SessionContext sessionContext;
	private final ClientObjectChannel clientObjectChannel;

	private boolean visible = true;
	private final Map<String, Map<String, Boolean>> cssClassesBySelector = new HashMap<>(0);
	private final Map<String, CssStyles> stylesBySelector = new HashMap<>(0);
	private final Map<String, Map<String, String>> attributesBySelector = new HashMap<>(0);

	public AbstractComponent() {
		this.sessionContext = CurrentSessionContext.get();
		// This IS ok, since SessionContext does not do anything with "this" reference.
		// The only usage of the "this" reference is going to be triggered by this.
		// Go figure.
		this.clientObjectChannel = this.sessionContext.registerClientObject(this);
	}

	protected void mapAbstractUiComponentProperties(DtoComponent uiComponent) {
		uiComponent.setVisible(visible);
		uiComponent.setStylesBySelector((Map) stylesBySelector);
		uiComponent.setClassNamesBySelector(cssClassesBySelector);
		uiComponent.setAttributesBySelector(attributesBySelector);
	}

	protected ClientObjectChannel getClientObjectChannel() {
		return clientObjectChannel;
	}

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		boolean changed = visible != this.visible;
		this.visible = visible;
		if (changed) {
			getClientObjectChannel().sendCommandIfRendered("setVisibleCommand", visible);
		}
	}

	@Override
	public void setCssStyle(String selector, String propertyName, String value) {
		if (selector == null) {
			selector = "";
		}
		CssStyles styles = this.stylesBySelector.computeIfAbsent(selector, s -> new CssStyles());
		styles.put(propertyName, value);

		final String selector2 = selector;
		getClientObjectChannel().sendCommandIfRendered("setStyleCommand", selector2, styles);
	}

	@Override
	public void toggleCssClass(String selector, String className, boolean enabled) {
		if (selector == null) {
			selector = "";
		}
		Map<String, Boolean> classNames = cssClassesBySelector.computeIfAbsent(selector, s -> new HashMap<>());
		classNames.put(className, enabled);

		final String selector2 = selector;
		getClientObjectChannel().sendCommandIfRendered("setClassNamesCommand", selector2, classNames);
	}

	@Override
	public void setAttribute(String selector, String attributeName, String value) {
		if (selector == null) {
			selector = "";
		}
		Map<String, String> attributes = this.attributesBySelector.computeIfAbsent(selector, s -> new HashMap<>());
		if (value != null) {
			attributes.put(attributeName, value);
		} else {
			attributes.put(attributeName, DELETED_ATTRIBUTE);
		}

		final String selector2 = selector;
		getClientObjectChannel().sendCommandIfRendered("setAttributesCommand", selector2, attributes);
	}

}
