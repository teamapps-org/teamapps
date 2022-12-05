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

import org.teamapps.dto.DtoField;
import org.teamapps.dto.DtoLabel;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class Label extends AbstractField<String> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoLabel.ClickedEvent.TYPE_ID);

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
	public DtoField createDto() {
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

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		sendCommandIfRendered(() -> new DtoLabel.SetIconCommand(getSessionContext().resolveIcon(icon)));
	}

	public Component getTargetComponent() {
		return targetComponent;
	}

	public Label setTargetComponent(Component targetComponent) {
		if (targetComponent == this) {
			throw new IllegalArgumentException("Labels may not reference themselves!");
		}
		this.targetComponent = targetComponent;
		sendCommandIfRendered(() -> new DtoLabel.SetTargetComponentCommand(Component.createUiClientObjectReference(targetComponent)));
		return this;
	}
}
