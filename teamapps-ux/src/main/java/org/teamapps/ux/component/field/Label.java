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

import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoLabel;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Label extends AbstractField<String> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoLabel.ClickedEvent.TYPE_ID);

	private String caption;
	private Icon<?, ?> icon;
	private Component targetComponent;

	public Label(String caption) {
		this(caption, null, null);
	}

	public Label(String caption, Component targetComponent) {
		this(caption, null, targetComponent);
	}

	public Label(String caption, Icon<?, ?> icon) {
		this(caption, icon, null);
	}

	public Label(String caption, Icon<?, ?> icon, Component targetComponent) {
		this.caption = caption;
		this.icon = icon;
		this.targetComponent = targetComponent;
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoLabel uiLabel = new DtoLabel(caption);
		mapAbstractFieldAttributesToUiField(uiLabel);
		uiLabel.setIcon(getSessionContext().resolveIcon(icon));
		uiLabel.setTargetComponent(targetComponent != null ? targetComponent.createClientReference() : null);
		return uiLabel;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoLabel.SetCaptionCommand(caption), null);
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		getClientObjectChannel().sendCommandIfRendered(new DtoLabel.SetIconCommand(getSessionContext().resolveIcon(icon)), null);
	}

	public Component getTargetComponent() {
		return targetComponent;
	}

	public Label setTargetComponent(Component targetComponent) {
		if (targetComponent == this) {
			throw new IllegalArgumentException("Labels may not reference themselves!");
		}
		this.targetComponent = targetComponent;
		getClientObjectChannel().sendCommandIfRendered(new DtoLabel.SetTargetComponentCommand(ClientObject.createClientReference(targetComponent)), null);
		return this;
	}
}
