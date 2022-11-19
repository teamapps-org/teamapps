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
package org.teamapps.ux.component.iframe;

import org.teamapps.dto.DtoEventWrapper;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.DtoIFrame;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class IFrame extends AbstractComponent {

	private String url;

	public IFrame() {
		this(null);
	}

	public IFrame(String url) {
		this.url = url;
	}

	@Override
	public UiComponent createUiClientObject() {
		DtoIFrame uiIFrame = new DtoIFrame().setUrl(url);
		mapAbstractUiComponentProperties(uiIFrame);
		return uiIFrame;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		sendCommandIfRendered(() -> new DtoIFrame.SetUrlCommand(url));
	}
}
