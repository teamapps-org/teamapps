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
package org.teamapps.projector.component.essential.rootpanel;

import org.teamapps.common.format.Color;
import org.teamapps.projector.animation.PageTransition;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.essential.DtoRootPanel;
import org.teamapps.projector.component.essential.DtoRootPanelClientObjectChannel;
import org.teamapps.projector.component.essential.DtoRootPanelEventHandler;

import java.time.Duration;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class RootPanel extends AbstractComponent implements DtoRootPanelEventHandler {

	private final DtoRootPanelClientObjectChannel clientObjectChannel = new DtoRootPanelClientObjectChannel(getClientObjectChannel());

	private String backgroundImageUrl;
	private String blurredBackgroundImageUrl;
	private Color backgroundColor;
	private Component content;

	@Override
	public DtoComponent createConfig() {
		DtoRootPanel uiRootPanel = new DtoRootPanel();
		mapAbstractConfigProperties(uiRootPanel);
		uiRootPanel.setBackgroundImageUrl(backgroundImageUrl);
		uiRootPanel.setBlurredBackgroundImageUrl(blurredBackgroundImageUrl);
		uiRootPanel.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString(): null);
		uiRootPanel.setContent(content);
		return uiRootPanel;
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, long animationDuration) {
		content = component;
		clientObjectChannel.setContent(component, animation, animationDuration);
	}

	public void setBackground(String backgroundImageUrl, String blurredBackgroundImageUrl, Color backgroundColor, Duration animationDuration) {
		clientObjectChannel.setBackground(backgroundImageUrl, blurredBackgroundImageUrl, backgroundColor.toHtmlColorString(), (int) animationDuration.toMillis());
	}

	public Component getContent() {
		return content;
	}

}
