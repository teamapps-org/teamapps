/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.teamapps.ux.resource.Resource;

import java.util.List;

public class SimpleChatMessage implements ChatMessage {

	private String id;
	private Resource userImage;
	private String userNickname;
	private String text;
	private List<ChatPhoto> photos;
	private List<ChatFile> files;

	public SimpleChatMessage(String id, Resource userImage, String userNickname, String text) {
		this.id = id;
		this.userImage = userImage;
		this.userNickname = userNickname;
		this.text = text;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Resource getUserImage() {
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

	public void setId(String id) {
		this.id = id;
	}

	public void setUserImage(Resource userImage) {
		this.userImage = userImage;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setPhotos(List<ChatPhoto> photos) {
		this.photos = photos;
	}

	public void setFiles(List<ChatFile> files) {
		this.files = files;
	}
}
