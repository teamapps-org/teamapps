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
import org.teamapps.dto.DtoClientObjectReference;
import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.dto.DtoCommand;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.css.CssStyles;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractComponent implements Component {

	public static final String DELETED_ATTRIBUTE = "__ta-deleted-attribute__";

	private enum RenderingState {
		NOT_RENDERED,
		RENDERING,
		RENDERED
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractComponent.class);

	public final ProjectorEvent<Void> onRendered = new ProjectorEvent<>();

	private String debuggingId = "";
	private final String id;
	private final SessionContext sessionContext;
	private RenderingState renderingState = RenderingState.NOT_RENDERED;
	private Component parent;

	private boolean visible = true;
	private Set<String> listeningEventNames = new HashSet<>();
	private Set<String> listeningQueryNames = new HashSet<>();
	private final Map<String, Map<String, Boolean>> cssClassesBySelector = new HashMap<>(0);
	private final Map<String, CssStyles> stylesBySelector = new HashMap<>(0);
	private final Map<String, Map<String, String>> attributesBySelector = new HashMap<>(0);

	public AbstractComponent() {
		this.sessionContext = CurrentSessionContext.get();
		id = getClass().getSimpleName() + "-" + UUID.randomUUID().toString();
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
		return new ProjectorEvent<>(hasListeners -> toggleEventListening(qualifiedEventName, hasListeners));
	}

	protected void toggleEventListening(String name, boolean listen) {
		boolean changed;
		if (listen) {
			changed = listeningEventNames.add(name);
		} else {
			changed = listeningEventNames.remove(name);
		}
		if (changed) {
			sendCommandIfRendered(null, () -> new DtoGlobals.ToggleEventListeningCommand(null, getId(), name, listen));
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
			sendCommandIfRendered(() -> new DtoComponent.SetVisibleCommand(visible));
		}
	}

	@Override
	public final void render() {
		if (renderingState == RenderingState.RENDERED || renderingState == RenderingState.RENDERING) {
			return; // already rendered!
		}

		this.renderingState = RenderingState.RENDERING;
		LOGGER.debug("rendering: " + getId());
		sessionContext.renderClientObject(this);
		this.renderingState = RenderingState.RENDERED; // NOTE: after queuing creation! otherwise commands might be queued for this component before it creation is queued!
		onRendered.fire(null);
	}

	@Override
	public final void unrender() {
		sessionContext.unrenderClientObject(this);
		renderingState = RenderingState.NOT_RENDERED;
	}

	@Override
	public DtoClientObjectReference createUiReference() {
		LOGGER.debug("createUiClientObjectReference: " + getId());
		if (!isRendered()) {
			render();
		}
		return new DtoClientObjectReference(getId());
	}

	public void reRenderIfRendered() {
		if (renderingState == RenderingState.RENDERED) {
			sessionContext.sendStaticCommand(RootPanel.class, new DtoGlobals.RefreshComponentCommand(createUiClientObject()));
		}
	}

	protected void sendCommandIfRendered(Supplier<DtoCommand<?>> commandSupplier) {
		this.sendCommandIfRendered(getId(), commandSupplier);
	}

	protected void sendCommandIfRendered(String clientObjectId, Supplier<DtoCommand<?>> commandSupplier) {
		if (renderingState == RenderingState.RENDERED) {
			sessionContext.sendCommand(clientObjectId, commandSupplier.get());
		} else if (renderingState == RenderingState.RENDERING) {
			/*
			This accounts for a very rare case. A component that is rendering itself may, while one of its children is rendered, be changed due to a thrown event. This change must be transported to the client
			as command (since the corresponding setter of the parent's DtoComponent has possibly already been set). However, this command must be enqueued after the component is rendered on the client
			side! Therefore, sending the command must be forcibly enqueued.

			Example: A panel contains a table. The panel's title is bound to the table's "count" ObservableValue. When the panel is rendered, the table also is rendered (as part of rendering the
			panel). While rendering, the table sets its "count" value, so the panel's title is changed. However, the DtoPanel's setTitle() method already has been invoked, so the change will not have
			any effect on the initialization of the DtoPanel. Therefore, the change must be sent as a command. Sending the command directly however would make it arrive at the client before
			the panel was rendered (which is only after completing its createUiComponent() method).
			 */
			sessionContext.runWithContext(() -> {
				sessionContext.sendCommand(clientObjectId, commandSupplier.get());
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
