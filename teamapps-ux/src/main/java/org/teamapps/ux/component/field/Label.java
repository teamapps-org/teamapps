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

import org.teamapps.dto.DtoAbstractField;
import org.teamapps.dto.DtoLabel;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class Label extends AbstractField<String> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoLabel.ClickedEvent.TYPE_ID);

	private String caption;
	private Icon<?, ?> icon;
	private org.teamapps.ux.component.Component targetComponent;

	public Label(String caption) {
		this(caption, null, null);
	}

	public Label(String caption, org.teamapps.ux.component.Component targetComponent) {
		this(caption, null, targetComponent);
	}

	public Label(String caption, Icon<?, ?> icon) {
		this(caption, icon, null);
	}

	public Label(String caption, Icon<?, ?> icon, org.teamapps.ux.component.Component targetComponent) {
		this.caption = caption;
		this.icon = icon;
		this.targetComponent = targetComponent;
	}

	@Override
	public DtoAbstractField createDto() {
		DtoLabel uiLabel = new DtoLabel(caption);
		mapAbstractFieldAttributesToUiField(uiLabel);
		uiLabel.setIcon(getSessionContext().resolveIcon(icon));
		uiLabel.setTargetComponent(targetComponent != null ? targetComponent.createDtoReference() : null);
		return uiLabel;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoLabel.ClickedEvent.TYPE_ID -> {
				this.onClicked.fire();
			}
		}
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		sendCommandIfRendered(() -> new DtoLabel.SetCaptionCommand(caption));
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		sendCommandIfRendered(() -> new DtoLabel.SetIconCommand(getSessionContext().resolveIcon(icon)));
	}

	public org.teamapps.ux.component.Component getTargetComponent() {
		return targetComponent;
	}

	public Label setTargetComponent(org.teamapps.ux.component.Component targetComponent) {
		if (targetComponent == this) {
			throw new IllegalArgumentException("Labels may not reference themselves!");
		}
		this.targetComponent = targetComponent;
		sendCommandIfRendered(() -> new DtoLabel.SetTargetComponentCommand(ClientObject.createDtoReference(targetComponent)));
		return this;
	}
}
