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
package org.teamapps.ux.component.linkbutton;

import org.teamapps.dto.UiEventWrapper;
import org.teamapps.dto.UiLinkButton;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class LinkButton extends AbstractComponent {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(UiLinkButton.ClickedEvent.TYPE_ID);

	private String text;
	private String url;
	private LinkTarget target = LinkTarget.BLANK;
	private String onClickJavaScript;

	public LinkButton(String text) {
		this(text, null);
	}

	public LinkButton(String text, String url) {
		this.text = text;
		this.url = url;
	}

	@Override
	public UiLinkButton createUiClientObject() {
		UiLinkButton ui = new UiLinkButton();
		mapAbstractUiComponentProperties(ui);
		ui.setText(text);
		ui.setUrl(url);
		ui.setTarget(target.toUiLinkTarget());
		ui.setOnClickJavaScript(onClickJavaScript);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiLinkButton.ClickedEvent.TYPE_ID -> {
				onClicked.fire();
			}
		}
	}

	private void update() {
		sendCommandIfRendered(() -> new UiLinkButton.UpdateCommand(createUiClientObject()));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		update();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		update();
	}

	public LinkTarget getTarget() {
		return target;
	}

	public void setTarget(LinkTarget target) {
		this.target = target;
		update();
	}

	public String getOnClickJavaScript() {
		return onClickJavaScript;
	}

	public void setOnClickJavaScript(String onClickJavaScript) {
		this.onClickJavaScript = onClickJavaScript;
		update();
	}
}
