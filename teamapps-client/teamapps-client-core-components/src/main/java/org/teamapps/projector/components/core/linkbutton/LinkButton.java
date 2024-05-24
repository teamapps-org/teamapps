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
package org.teamapps.projector.components.core.linkbutton;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.dto.DtoLinkButton;
import org.teamapps.projector.dto.DtoLinkButtonClientObjectChannel;
import org.teamapps.projector.dto.DtoLinkButtonEventHandler;
import org.teamapps.projector.event.ProjectorEvent;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class LinkButton extends AbstractComponent implements DtoLinkButtonEventHandler {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoLinkButton.ClickedEvent.TYPE_ID);

	private final DtoLinkButtonClientObjectChannel clientObjectChannel = new DtoLinkButtonClientObjectChannel(getClientObjectChannel());

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
	public DtoLinkButton createConfig() {
		DtoLinkButton ui = new DtoLinkButton();
		mapAbstractUiComponentProperties(ui);
		ui.setText(text);
		ui.setUrl(url);
		ui.setTarget(target.toUiLinkTarget());
		ui.setOnClickJavaScript(onClickJavaScript);
		return ui;
	}

	@Override
	public void handleClicked() {
		onClicked.fire();
	}

	private void update() {
		clientObjectChannel.update(createConfig());
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
