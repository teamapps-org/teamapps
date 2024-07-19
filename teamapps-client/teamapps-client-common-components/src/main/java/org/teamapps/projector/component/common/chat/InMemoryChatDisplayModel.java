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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class InMemoryChatDisplayModel extends AbstractChatDisplayModel {

	private final AtomicInteger chatMessageIdCounter = new AtomicInteger();
	private final transient Object lock = new Object();
	private final ArrayList<ChatMessage> mutableList = new ArrayList<>();
	private volatile List<ChatMessage> snapshot = List.copyOf(mutableList);

	public InMemoryChatDisplayModel() {
	}

	private <T> T readFromSnapshot(Function<List<ChatMessage>, T> readOperation) {
		return readOperation.apply(snapshot);
	}

	private <T> T transformList(Function<List<ChatMessage>, T> operation) {
		T result;
		synchronized (lock) {
			result = operation.apply(mutableList);
			snapshot = List.copyOf(mutableList);
		}
		return result;
	}

	public InMemoryChatDisplayModel(CopyOnWriteArrayList<ChatMessage> messages) {
		transformList(chatMessages -> chatMessages.addAll(messages));
	}

	@Override
	public ChatMessage getChatMessageById(int id) {
		return readFromSnapshot(chatMessages -> {
			int index = Collections.binarySearch(chatMessages, new BinarySearchReferenceChatMessage(id), Comparator.comparing(ChatMessage::getId));
			return index >= 0 ? chatMessages.get(index) : null;
		});
	}

	@Override
	public ChatMessageBatch getPreviousMessages(Integer earliestKnownMessageId, int numberOfMessages) {
		if (earliestKnownMessageId == null) {
			return readFromSnapshot(chatMessages -> {
				int startIndex = Math.max(0, chatMessages.size() - numberOfMessages);
				return new ChatMessageBatch(chatMessages.subList(startIndex, chatMessages.size()), startIndex == 0);
			});
		} else {
			return readFromSnapshot(chatMessages -> {
				int searchResultIndex = Collections.binarySearch(chatMessages, new BinarySearchReferenceChatMessage(earliestKnownMessageId), Comparator.comparing(ChatMessage::getId));
				int firstKnownIndex = searchResultIndex >= 0 ? searchResultIndex : -(searchResultIndex + 1);
				int startIndex = Math.max(0, firstKnownIndex - numberOfMessages);
				return new ChatMessageBatch(chatMessages.subList(startIndex, firstKnownIndex), startIndex == 0);
			});
		}
	}

	public ChatMessage addMessage(Resolvable userImage, String userNickname, String text) {
		return addMessage(userImage, userNickname, text, null, null, false);
	}

	public ChatMessage addMessage(Resolvable userImage, String userNickname, String text, List<ChatPhoto> photos, List<ChatFile> files, boolean deleted) {
		ChatMessageBatch chatMessageBatch = transformList(chatMessages -> {
			boolean firstMessage = chatMessages.size() == 0;
			SimpleChatMessage message = new SimpleChatMessage(chatMessageIdCounter.incrementAndGet(), userImage, userNickname, text, photos, files, deleted);
			chatMessages.add(message);
			return new ChatMessageBatch(Collections.singletonList(message), firstMessage);
		});
		this.onMessagesAdded.fire(chatMessageBatch);
		return chatMessageBatch.getMessages().get(0);
	}

	public void replaceAllMessages(List<ChatMessage> messages) {
		boolean wasChanged = transformList(chatMessages -> {
			boolean changed = !(chatMessages.size() == 0 && messages.size() == 0);
			chatMessages.clear();
			chatMessages.addAll(messages);
			return changed;
		});
		if (wasChanged) {
			this.onAllDataChanged.fire(null);
		}
	}

	public void deleteMessage(int messageId) {
		boolean wasRemoved = transformList(chatMessages -> {
			int searchResultIndex = Collections.binarySearch(chatMessages, new BinarySearchReferenceChatMessage(messageId), Comparator.comparing(ChatMessage::getId));
			if (searchResultIndex >= 0) {
				chatMessages.remove(searchResultIndex);
				return true;
			} else {
				return false;
			}
		});
		if (wasRemoved) {
			this.onMessageDeleted.fire(messageId);
		}
	}

	public void updateMessage(ChatMessage chatMessage) {
		boolean wasChanged = transformList(chatMessages -> {
			int searchResultIndex = Collections.binarySearch(chatMessages, chatMessage, Comparator.comparing(ChatMessage::getId));
			if (searchResultIndex >= 0) {
				chatMessages.set(searchResultIndex, chatMessage);
				return true;
			} else {
				return false;
			}
		});
		if (wasChanged) {
			this.onMessageChanged().fire(chatMessage);
		}
	}

	private static class BinarySearchReferenceChatMessage implements ChatMessage {
		private final int id;

		public BinarySearchReferenceChatMessage(int id) {
			this.id = id;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Resolvable getUserImage() {
			return null;
		}

		@Override
		public String getUserNickname() {
			return null;
		}

		@Override
		public String getText() {
			return null;
		}

	}

}
