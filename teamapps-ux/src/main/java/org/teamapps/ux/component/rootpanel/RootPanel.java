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
package org.teamapps.ux.component.rootpanel;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoRootPanel;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.*;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.annotations.ProjectorComponent;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class RootPanel extends AbstractComponent implements org.teamapps.ux.component.Component {

	private org.teamapps.ux.component.Component content;

	@Override
	public DtoComponent createDto() {
		DtoRootPanel uiRootPanel = new DtoRootPanel();
		mapAbstractUiComponentProperties(uiRootPanel);
		uiRootPanel.setContent(content != null ? content.createDtoReference() : null);
		return uiRootPanel;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		// no ui events for this component
	}

	public void setContent(org.teamapps.ux.component.Component component) {
		setContent(component, null, 0);
	}

	public void setContent(org.teamapps.ux.component.Component component, PageTransition animation, long animationDuration) {
		content = component;
		if (component != null) {
			component.setParent(this);
		}
		sendCommandIfRendered(() -> new DtoRootPanel.SetContentCommand(component != null ? component.createDtoReference() : null, animation != null ? animation.toUiPageTransition() : null, animationDuration));
	}

	public org.teamapps.ux.component.Component getContent() {
		return content;
	}

}
