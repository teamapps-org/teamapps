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

import org.teamapps.common.format.Color;
import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.DtoRootPanel;
import org.teamapps.ux.component.*;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.annotations.ProjectorComponent;

import java.time.Duration;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class RootPanel extends AbstractComponent implements Component {

	private String backgroundImageUrl;
	private String blurredBackgroundImageUrl;
	private Color backgroundColor;
	private Component content;

	@Override
	public DtoComponent createConfig() {
		DtoRootPanel uiRootPanel = new DtoRootPanel();
		mapAbstractUiComponentProperties(uiRootPanel);
		uiRootPanel.setBackgroundImageUrl(backgroundImageUrl);
		uiRootPanel.setBlurredBackgroundImageUrl(blurredBackgroundImageUrl);
		uiRootPanel.setBackgroundColor(backgroundColor.toHtmlColorString());
		uiRootPanel.setContent(content);
		return uiRootPanel;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		// no ui events for this component
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, long animationDuration) {
		content = component;
		if (component != null) {
			component.setParent(this);
		}
		sendCommandIfRendered(() -> new DtoRootPanel.SetContentCommand(component, animation != null ? animation.toUiPageTransition() : null, animationDuration));
	}

	public void setBackground(String backgroundImageUrl, String blurredBackgroundImageUrl, Color backgroundColor, Duration animationDuration) {
		sendCommandIfRendered(() -> new DtoRootPanel.SetBackgroundCommand(backgroundImageUrl, blurredBackgroundImageUrl, backgroundColor.toHtmlColorString(), (int) animationDuration.toMillis()).getParameters());
	}

	public Component getContent() {
		return content;
	}

}
