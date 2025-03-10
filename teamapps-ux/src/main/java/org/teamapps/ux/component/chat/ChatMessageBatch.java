/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import java.util.List;

public class ChatMessageBatch {

	private final List<ChatMessage> messages;
	private final boolean containsFirstMessage;

	public ChatMessageBatch(List<ChatMessage> messages, boolean containsFirstMessage) {
		this.messages = messages;
		this.containsFirstMessage = containsFirstMessage;
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public boolean isContainsFirstMessage() {
		return containsFirstMessage;
	}

	public Integer getEarliestMessageId() {
		return messages.size() > 0 ? messages.get(0).getId() : null;
	}

}
