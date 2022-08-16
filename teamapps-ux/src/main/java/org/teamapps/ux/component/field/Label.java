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
package org.teamapps.ux.component.field;

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiLabel;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;

public class Label extends AbstractField<String> {

	public final Event<Void> onClicked = new Event<>();

	private String caption;
	private Icon icon;
	private Component targetComponent;

	public Label(String caption) {
		super();
		this.caption = caption;
	}

	public Label(String caption, Icon icon) {
		this(caption);
		this.icon = icon;
	}

	@Override
	public UiField createUiComponent() {
		UiLabel uiLabel = new UiLabel(caption);
		mapAbstractFieldAttributesToUiField(uiLabel);
		uiLabel.setIcon(getSessionContext().resolveIcon(icon));
		uiLabel.setTargetComponent(targetComponent != null ? targetComponent.createUiReference() : null);
		return uiLabel;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiLabel.ClickedEvent) {
			this.onClicked.fire();
		}
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		queueCommandIfRendered(() -> new UiLabel.SetCaptionCommand(getId(), caption));
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		queueCommandIfRendered(() -> new UiLabel.SetIconCommand(getId(), getSessionContext().resolveIcon(icon)));
	}

	public Component getTargetComponent() {
		return targetComponent;
	}

	public Label setTargetComponent(Component targetComponent) {
		if (targetComponent == this) {
			throw new IllegalArgumentException("Labels may not reference themselves!");
		}
		this.targetComponent = targetComponent;
		queueCommandIfRendered(() -> new UiLabel.SetTargetComponentCommand(getId(), Component.createUiClientObjectReference(targetComponent)));
		return this;
	}
}
