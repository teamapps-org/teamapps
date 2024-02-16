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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.DtoCommand;
import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.css.CssStyles;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.Globals;
import org.teamapps.ux.session.SessionContext;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractComponent implements Component {

	public static final String DELETED_ATTRIBUTE = "__ta-deleted-attribute__";

	private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String debuggingId = "";
	private final String id = getClass().getSimpleName() + "-" + UUID.randomUUID();
	private final SessionContext sessionContext;
	private Component parent;

	private boolean visible = true;
	private Set<String> listeningEventNames = new HashSet<>();
	private final Map<String, Map<String, Boolean>> cssClassesBySelector = new HashMap<>(0);
	private final Map<String, CssStyles> stylesBySelector = new HashMap<>(0);
	private final Map<String, Map<String, String>> attributesBySelector = new HashMap<>(0);

	public AbstractComponent() {
		this.sessionContext = CurrentSessionContext.get();
	}

	protected void mapAbstractUiComponentProperties(DtoComponent uiComponent) {
		uiComponent.setId(id);
		uiComponent.setDebuggingId(debuggingId);
		uiComponent.setVisible(visible);
		uiComponent.setListeningEvents(List.copyOf(listeningEventNames));
		uiComponent.setStylesBySelector((Map) stylesBySelector);
		uiComponent.setClassNamesBySelector(cssClassesBySelector);
		uiComponent.setAttributesBySelector(attributesBySelector);
	}

	protected <T> ProjectorEvent<T> createProjectorEventBoundToUiEvent(String qualifiedEventName) {
		return createProjectorEventBoundToUiEvent(qualifiedEventName, false);
	}

	protected <T> ProjectorEvent<T> createProjectorEventBoundToUiEvent(String qualifiedEventName, boolean registerAlways) {
		if (registerAlways) {
			toggleEventListening(qualifiedEventName, true);
			return new ProjectorEvent<>();
		} else {
			return new ProjectorEvent<>(hasListeners -> toggleEventListening(qualifiedEventName, hasListeners));
		}
	}

	protected void toggleEventListening(String name, boolean shouldListen) {
		boolean changed;
		if (shouldListen) {
			changed = listeningEventNames.add(name);
		} else {
			changed = listeningEventNames.remove(name);
		}
		if (changed) {
			sendGlobalStaticCommandIfRendered(() -> new DtoGlobals.ToggleEventListeningCommand(null, getId(), name, shouldListen));
		}
	}

	@Override
	public String getId() {
		return id;
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
			sendCommandIfRendered(() -> new DtoComponent.SetVisibleCommand(visible));
		}
	}

	private void sendGlobalStaticCommandIfRendered(Supplier<DtoCommand<?>> commandSupplier) {
		SessionContext sessionContext = getSessionContext();
		if (sessionContext.isRendering(this)) {
			// wait until finished rendering for sending this command!
			sessionContext.runWithContext(() -> sessionContext.sendStaticCommand(Globals.class, commandSupplier.get()), true);
		} else if (sessionContext.isRendered(this)) {
			sessionContext.sendStaticCommand(Globals.class, commandSupplier.get());
		}
	}

	protected void sendCommandIfRendered(Supplier<DtoCommand<?>> commandSupplier) {
		sessionContext.sendCommandIfRendered(this, commandSupplier.get());
	}

	@Override
	public void setCssStyle(String selector, String propertyName, String value) {
		if (selector == null) {
			selector = "";
		}
		CssStyles styles = this.stylesBySelector.computeIfAbsent(selector, s -> new CssStyles());
		styles.put(propertyName, value);

		final String selector2 = selector;
		sendCommandIfRendered(() -> new DtoComponent.SetStyleCommand(selector2, styles));
	}

	@Override
	public void toggleCssClass(String selector, String className, boolean enabled) {
		if (selector == null) {
			selector = "";
		}
		Map<String, Boolean> classNames = cssClassesBySelector.computeIfAbsent(selector, s -> new HashMap<>());
		classNames.put(className, enabled);

		final String selector2 = selector;
		sendCommandIfRendered(() -> new DtoComponent.SetClassNamesCommand(selector2, classNames));
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
		sendCommandIfRendered(() -> new DtoComponent.SetAttributesCommand(selector2, attributes));
	}

	//	@Override
//	public void updateEffectiveVisibility() {
//		onEffectiveVisibilityChanged.fireIfChanged(isEffectivelyVisible());
//	}

//	@Override
//	public Event<Boolean> onEffectiveVisibilityChanged() {
//		return onEffectiveVisibilityChanged;
//	}

	@Override
	public void setParent(Component container) {
		this.parent = container;
	}

	@Override
	public Component getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return id;
	}

	public String getDebuggingId() {
		return debuggingId;
	}

	public void setDebuggingId(String debuggingId) {
		this.debuggingId = debuggingId;
	}

}
