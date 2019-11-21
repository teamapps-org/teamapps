/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.chat;

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.resource.Resource;

public class SimpleChatFile implements ChatFile {

	private final String name;
	private final Icon icon;
	private final long size;
	private final Resource thumbnail;
	private final Resource download;

	public SimpleChatFile(String name, Icon icon, Resource thumbnail, Resource download) {
		this.name = name;
		this.icon = icon;
		this.size = download.getLength();
		this.thumbnail = thumbnail;
		this.download = download;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public long getLength() {
		return size;
	}

	@Override
	public Resource getThumbnail() {
		return thumbnail;
	}

	@Override
	public Resource getDownload() {
		return download;
	}
}
