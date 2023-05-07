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
package org.teamapps.projector.components.common.chat;

import org.teamapps.ux.resolvable.Resolvable;

public class SimpleChatPhoto implements ChatPhoto {

	private final String fileName;
	private final Resolvable thumbnail;
	private final Resolvable image;

	public SimpleChatPhoto(String fileName, Resolvable thumbnail, Resolvable image) {
		this.fileName = fileName;
		this.thumbnail = thumbnail;
		this.image = image;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public Resolvable getThumbnail() {
		return thumbnail;
	}

	@Override
	public Resolvable getImage() {
		return image;
	}
}
