/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.ux.resolvable.Resolvable;

import java.util.List;

public interface ChatMessage {

	int getId();

	Resolvable getUserImage();

	String getUserNickname();

	String getText();

	default List<ChatPhoto> getPhotos() {
		return List.of();
	}

	default List<ChatFile> getFiles() {
		return List.of();
	}

	default boolean isDeleted() {
		return false;
	}

}
