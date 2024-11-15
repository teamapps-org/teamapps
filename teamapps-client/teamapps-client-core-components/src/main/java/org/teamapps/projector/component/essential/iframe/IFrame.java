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
package org.teamapps.projector.component.essential.iframe;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.essential.DtoIFrame;
import org.teamapps.projector.component.essential.DtoIFrameClientObjectChannel;
import org.teamapps.projector.component.essential.DtoIFrameEventHandler;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class IFrame extends AbstractComponent implements DtoIFrameEventHandler {

	private final DtoIFrameClientObjectChannel clientObjectChannel = new DtoIFrameClientObjectChannel(getClientObjectChannel());

	private String url;

	public IFrame() {
		this(null);
	}

	public IFrame(String url) {
		this.url = url;
	}

	@Override
	public DtoComponent createConfig() {
		DtoIFrame uiIFrame = new DtoIFrame().setUrl(url);
		mapAbstractConfigProperties(uiIFrame);
		return uiIFrame;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		clientObjectChannel.setUrl(url);
	}
}