/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.event.Event;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.css.CssStyles;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractComponent implements Component {

	private enum RenderingState {
		NOT_RENDERED,
		RENDERING,
		RENDERED
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractComponent.class);

	public final Event<Void> onRendered = new Event<>();

	private String debuggingId = "";
	private final String id;
	private final SessionContext sessionContext;
	private RenderingState renderingState = RenderingState.NOT_RENDERED;
	private Component parent;

	private boolean visible = true;
	private final Map<String, Map<String, Boolean>> cssClassesBySelector = new HashMap<>(0);
	private final Map<String, CssStyles> stylesBySelector = new HashMap<>(0);

	public AbstractComponent() {
		this.sessionContext = CurrentSessionContext.get();
		id = getClass().getSimpleName() + "-" + UUID.randomUUID().toString();
	}

	protected void mapAbstractUiComponentProperties(UiComponent uiComponent) {
		uiComponent.setId(id);
		uiComponent.setDebuggingId(debuggingId);
		uiComponent.setVisible(visible);
		uiComponent.setStylesBySelector((Map) stylesBySelector);
	}

	@Override
	public String getId() {
		return id;
	}

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	@Override
	public boolean isRendered() {
		return renderingState == RenderingState.RENDERED;
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
			queueCommandIfRendered(() -> new UiComponent.SetVisibleCommand(getId(), visible));
		}
	}

	@Override
	public final void render() {
		if (renderingState == RenderingState.RENDERED) {
			return; // already rendered!
		}
		if (!(this instanceof RootPanel) && this.getParent() == null) {
			LOGGER.debug("Rendering component (" + getClass().getSimpleName() + ") that has no parent set. This is a temporary logging message to be able to find Containers that do not register "
					+ "themselves as \"parent\" of their children.");
		}
		LOGGER.debug("render: " + getId());
		sessionContext.registerClientObject(this);

		this.renderingState = RenderingState.RENDERING;
		UiComponent uiComponent = createUiComponent();
		sessionContext.queueCommand(new UiRootPanel.CreateComponentCommand(uiComponent));
		this.renderingState = RenderingState.RENDERED; // NOTE: after queuing creation! otherwise commands might be queued for this component before it creation is queued!
		onRendered.fire(null);
	}

	@Override
	public final void unrender() {
		sessionContext.unregisterClientObject(this);
		sessionContext.queueCommand(new UiRootPanel.DestroyComponentCommand(getId()));
		renderingState = RenderingState.NOT_RENDERED;
	}

	abstract public UiComponent createUiComponent();

	@Override
	public UiClientObjectReference createUiReference() {
		LOGGER.debug("createUiClientObjectReference: " + getId());
		if (!isRendered()) {
			render();
		}
		return new UiClientObjectReference(getId());
	}

	public void reRenderIfRendered() {
		if (renderingState == RenderingState.RENDERED) {
			sessionContext.queueCommand(new UiRootPanel.RefreshComponentCommand(createUiComponent()));
		}
	}

	protected void queueCommandIfRendered(Supplier<UiCommand<?>> commandSupplier) {
		if (renderingState == RenderingState.RENDERED) {
			sessionContext.queueCommand(commandSupplier.get());
		} else if (renderingState == RenderingState.RENDERING) {
			/*
			This accounts for a very rare case. A component that is rendering itself may, while one of its children is rendered, be changed due to a thrown event. This change must be transported to the client
			as command (since the corresponding setter of the parent's UiComponent has possibly already been set). However, this command must be enqueued after the component is rendered on the client
			side! Therefore, sending the command must be forcibly enqueued.

			Example: A panel contains a table. The panel's title is bound to the table's "count" ObservableValue. When the panel is rendered, the table also is rendered (as part of rendering the
			panel). While rendering, the table sets its "count" value, so the panel's title is changed. However, the UiPanel's setTitle() method already has been invoked, so the change will not have
			any effect on the initialization of the UiPanel. Therefore, the change must be sent as a command. Sending the command directly however would make it arrive at the client before
			the panel was rendered (which is only after completing its createUiComponent() method).
			 */
			sessionContext.runWithContext(() -> {
				sessionContext.queueCommand(commandSupplier.get());
			}, true);
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
		queueCommandIfRendered(() -> new UiComponent.SetStyleCommand(getId(), selector2, styles));
	}

	@Override
	public void toggleCssClass(String selector, String className, boolean enabled) {
		if (selector == null) {
			selector = "";
		}
		Map<String, Boolean> classNames = cssClassesBySelector.computeIfAbsent(selector, s -> new HashMap<>());
		classNames.put(className, enabled);

		final String selector2 = selector;
		queueCommandIfRendered(() -> new UiComponent.SetClassNamesCommand(getId(), selector2, classNames));
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
