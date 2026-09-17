/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.mobile;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMobileLayout;
import org.teamapps.dto.UiMobileNavigationState;
import org.teamapps.event.Event;

import java.util.List;
import java.util.Objects;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.toolbar.Toolbar;

public class MobileLayout extends AbstractComponent implements Component {

	/** Optional structural navigation. No navigation is installed unless configured explicitly. */
	public final Event<String> onNavigationRequested = new Event<>();
	private UiMobileNavigationState navigationState;
	private int navigationRevision;

	protected Toolbar toolbar;
	protected Component content;
	protected NavigationBar navigationBar;

	public MobileLayout() {
		super();
	}

	@Override
	public UiComponent createUiComponent() {
		UiMobileLayout uiMobileLayout = new UiMobileLayout();
		mapAbstractUiComponentProperties(uiMobileLayout);
		if (content != null) {
			uiMobileLayout.setInitialView(content.createUiReference());
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar.createUiReference());
		}
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar.createUiReference() : null);
		uiMobileLayout.setNavigationState(navigationState);
		return uiMobileLayout;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiMobileLayout.NavigationRequestedEvent request && navigationState != null) {
			if (request.getRevision() == navigationState.getRevision() && navigationState.getEntries().contains(request.getEntry())) {
				onNavigationRequested.fire(request.getEntry());
			}
			// Also acknowledge stale requests; the client must not remain locked waiting for a reply.
			queueCommandIfRendered(() -> new UiMobileLayout.SetNavigationStateCommand(getId(), navigationState));
		}
	}

	/**
	 * Enables optional navigation through an ordered structural path (first entry is Home).
	 * Entry identifiers are opaque to the client. This does not change content or the existing
	 * showView contract; the owner handles onNavigationRequested using its normal navigation.
	 */
	public void setNavigationState(List<String> entries, String currentEntry, boolean browserHistory, boolean edgeSwipes) {
		Objects.requireNonNull(entries);
		if (entries.isEmpty() || !entries.contains(currentEntry) || entries.stream().anyMatch(Objects::isNull)
				|| entries.stream().distinct().count() != entries.size()) {
			throw new IllegalArgumentException("Navigation requires unique entries including the current entry");
		}
		UiMobileNavigationState state = new UiMobileNavigationState();
		state.setRevision(++navigationRevision);
		state.setEntries(List.copyOf(entries));
		state.setCurrentEntry(currentEntry);
		state.setBrowserHistory(browserHistory);
		state.setEdgeSwipes(edgeSwipes);
		navigationState = state;
		queueCommandIfRendered(() -> new UiMobileLayout.SetNavigationStateCommand(getId(), navigationState));
	}

	public void clearNavigationState() {
		navigationState = null;
		navigationRevision++;
		queueCommandIfRendered(() -> new UiMobileLayout.SetNavigationStateCommand(getId(), null));
	}

	public void preloadView(Component component) {
		component.render();
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, int animationDuration) {
		if (this.content != component) {
			content = component;
			component.setParent(this);
			queueCommandIfRendered(() -> new UiMobileLayout.ShowViewCommand(getId(), component.createUiReference(), animation != null ? animation.toUiPageTransition() : null,
					animationDuration));
		}
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public Component getContent() {
		return content;
	}

	public NavigationBar getNavigationBar() {
		return navigationBar;
	}

	public void setNavigationBar(NavigationBar navigationBar) {
		this.navigationBar = navigationBar;
		if (navigationBar != null) {
			navigationBar.setParent(this);
		}
		queueCommandIfRendered(() -> new UiMobileLayout.SetNavigationBarCommand(getId(), navigationBar != null ? navigationBar.createUiReference() : null));
	}

}
