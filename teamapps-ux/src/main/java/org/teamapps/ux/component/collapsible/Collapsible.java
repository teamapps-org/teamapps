/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.collapsible;

import org.teamapps.dto.UiCollapsible;
import org.teamapps.dto.UiEvent;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class Collapsible extends AbstractComponent {

	public final Event<Boolean> onCollapseStateChanged = new Event<>();

	private Icon<?, ?> icon;
	private String caption;
	private Component content;
	private boolean collapsed;

	public Collapsible() {
		this(null, null, null);
	}

	public Collapsible(Icon<?, ?> icon, String caption) {
		this(icon, caption, null);
	}

	public Collapsible(Icon<?, ?> icon, String caption, Component content) {
		this.icon = icon;
		this.caption = caption;
		this.content = content;
	}

	@Override
	public UiCollapsible createUiComponent() {
		UiCollapsible ui = new UiCollapsible();
		mapAbstractUiComponentProperties(ui);
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setCaption(caption);
		ui.setContent(content != null ? content.createUiReference() : null);
		ui.setCollapsed(collapsed);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_COLLAPSIBLE_COLLAPSE_STATE_CHANGED: {
				UiCollapsible.CollapseStateChangedEvent ce = (UiCollapsible.CollapseStateChangedEvent) event;
				onCollapseStateChanged.fire(ce.getCollapsed());
			}
		}
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
		queueCommandIfRendered(() -> new UiCollapsible.SetContentCommand(getId(), content != null ? content.createUiReference() : null));
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		queueCommandIfRendered(() -> new UiCollapsible.SetIconAndCaptionCommand(getId(), getSessionContext().resolveIcon(icon), caption));
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		queueCommandIfRendered(() -> new UiCollapsible.SetIconAndCaptionCommand(getId(), getSessionContext().resolveIcon(icon), caption));
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		queueCommandIfRendered(() -> new UiCollapsible.SetCollapsedCommand(getId(), collapsed));
	}
}
