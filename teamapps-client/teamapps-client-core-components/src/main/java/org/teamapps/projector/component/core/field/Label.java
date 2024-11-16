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
package org.teamapps.projector.component.core.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoLabel;
import org.teamapps.projector.component.core.DtoLabelClientObjectChannel;
import org.teamapps.projector.component.core.DtoLabelEventHandler;
import org.teamapps.projector.event.ProjectorEvent;

// TODO label is NOT a field!
@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Label extends AbstractField<String> implements DtoLabelEventHandler {

	private final DtoLabelClientObjectChannel clientObjectChannel = new DtoLabelClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClick = new ProjectorEvent<>(clientObjectChannel::toggleClickEvent);

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
		uiLabel.setTargetComponent(targetComponent != null ? targetComponent : null);
		return uiLabel;
	}

	@Override
	public void handleClick() {
		this.onClick.fire();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		clientObjectChannel.setCaption(caption);
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		clientObjectChannel.setIcon(getSessionContext().resolveIcon(icon));
	}

	public Component getTargetComponent() {
		return targetComponent;
	}

	public Label setTargetComponent(Component targetComponent) {
		if (targetComponent == this) {
			throw new IllegalArgumentException("Labels may not reference themselves!");
		}
		this.targetComponent = targetComponent;
		clientObjectChannel.setTargetComponent(targetComponent);
		return this;
	}

	@Override
	public String doConvertClientValueToServerValue(JsonNode value) {
		return getValue(); // the ui does not pass the value anyway
	}
}
