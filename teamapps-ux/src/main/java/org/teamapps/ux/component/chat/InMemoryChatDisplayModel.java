/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import org.teamapps.event.Event;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryChatDisplayModel extends AbstractChatDisplayModel {

	private final CopyOnWriteArrayList<ChatMessage> messages = new CopyOnWriteArrayList<>();

	public InMemoryChatDisplayModel() {
	}

	public InMemoryChatDisplayModel(CopyOnWriteArrayList<ChatMessage> messages) {
		this.messages.addAll(messages);
	}

	@Override
	public Event<ChatMessageBatch> onMessagesAdded() {
		return onMessagesAdded;
	}

	@Override
	public Event<Void> onAllDataChanged() {
		return onAllDataChanged;
	}


	@Override
	public ChatMessageBatch getPreviousMessages(String earliestKnownMessageId, int numberOfMessages) {
		if (earliestKnownMessageId == null) {
			int startIndex = Math.max(0, messages.size() - numberOfMessages);
			return new ChatMessageBatch(messages.subList(startIndex, messages.size()), startIndex == 0);
		} else {
			int i = indexOfMessageById(earliestKnownMessageId);
			if (i == -1) {
				return new ChatMessageBatch(Collections.emptyList(), false);
			} else {
				int startIndex = Math.max(0, i - numberOfMessages);
				return new ChatMessageBatch(messages.subList(startIndex, i), startIndex == 0);
			}
		}
	}

	@Override
	public ChatMessage getChatMessageById(String id) {
		return messages.get(indexOfMessageById(id));
	}

	private int indexOfMessageById(String earliestKnownMessageId) {
		for (int i = messages.size() - 1; i > 0; i--) {
			if (messages.get(i).getId().equals(earliestKnownMessageId)) {
				return i;
			}
		}
		return -1;
	}

	public void addMessage(ChatMessage message) {
		boolean firstMessage = this.messages.size() == 0;
		this.messages.add(message);
		this.onMessagesAdded.fire(new ChatMessageBatch(Collections.singletonList(message), firstMessage));
	}

	public void replaceAllMessages(List<ChatMessage> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		this.onAllDataChanged.fire(null);
	}

	public void deleteMessage(String messageId) {
		ChatMessage message = this.messages.get(indexOfMessageById(messageId));
	}
}
