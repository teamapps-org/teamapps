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
package org.teamapps.projector.component.common.chat;

import org.teamapps.ux.resolvable.Resolvable;

import java.util.List;

public class SimpleChatMessage implements ChatMessage {

	private final int id;
	private final Resolvable userImage;
	private final String userNickname;
	private final String text;
	private final List<ChatPhoto> photos;
	private final List<ChatFile> files;
	private final boolean deleted;

	public SimpleChatMessage(int id, Resolvable userImage, String userNickname, String text) {
		this(id, userImage, userNickname, text, null, null, false);
	}

	public SimpleChatMessage(int id, Resolvable userImage, String userNickname, String text, List<ChatPhoto> photos, List<ChatFile> files, boolean deleted) {
		this.id = id;
		this.userImage = userImage;
		this.userNickname = userNickname;
		this.text = text;
		this.photos = photos;
		this.files = files;
		this.deleted = deleted;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Resolvable getUserImage() {
		return userImage;
	}

	@Override
	public String getUserNickname() {
		return userNickname;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public List<ChatPhoto> getPhotos() {
		return photos;
	}

	@Override
	public List<ChatFile> getFiles() {
		return files;
	}

	public boolean isDeleted() {
		return deleted;
	}
}
