/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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

public interface ChatDisplayModel {

	/**
	 * Fires when new messages were appended to the chat.
	 */
	Event<ChatMessageBatch> onMessagesAdded();

	/**
	 * Fires when a message was deleted.
	 */
	Event<Integer> onMessageDeleted();

	/**
	 * Fires when an already known message changed.
	 */
	Event<ChatMessage> onMessageChanged();

	/**
	 * Fires when the backing data changed in a way that requires the display to reload its current initial range.
	 */
	Event<Void> onAllDataChanged();

	/**
	 * Finds a single chat message by id.
	 *
	 * @param id message id
	 * @return the matching message, or {@code null} if no such message exists
	 */
	ChatMessage getChatMessageById(int id);

	/**
	 * Loads messages older than the earliest message currently known by the client.
	 * <p>
	 * The returned batch must not include {@code earliestKnownMessageId}; otherwise paging upwards would duplicate
	 * the first known message.
	 *
	 * @param earliestKnownMessageId first message id currently known by the client, or {@code null} to load the newest messages
	 * @param numberOfMessages maximum number of older messages to load
	 * @return message batch in ascending chronological order
	 */
	default ChatMessageBatch getPreviousMessages(Integer earliestKnownMessageId, int numberOfMessages) {
		return getChatMessages(earliestKnownMessageId, null, numberOfMessages);
	}

	/**
	 * Loads the newest messages of the chat.
	 *
	 * @param numberOfMessages maximum number of messages to load
	 * @return message batch in ascending chronological order
	 */
	default ChatMessageBatch getLastChatMessages(int numberOfMessages) {
		return getChatMessages(null, null, numberOfMessages);
	}

	/**
	 * Loads a range ending at the newest message while ensuring {@code oldestMessageId} is included, plus a buffer of
	 * messages older than that anchor. This is used for initial anchor scrolling: the anchor can be positioned at the
	 * viewport top while the user still has normally loaded messages above it.
	 *
	 * @param oldestMessageId message id that must be included in the returned batch
	 * @param bufferNumberOfMessages maximum number of messages to include before {@code oldestMessageId}
	 * @return message batch in ascending chronological order
	 */
	default ChatMessageBatch getLastChatMessages(int oldestMessageId, int bufferNumberOfMessages) {
		return getChatMessages(null, oldestMessageId, bufferNumberOfMessages);
	}

	/**
	 * Loads a contiguous range of chat messages in ascending chronological order.
	 * <p>
	 * The two optional id parameters define the upper and lower boundaries:
	 * <ul>
	 *     <li>{@code exclusiveStartingMessageId}: exclusive upper boundary. If set, only messages older than this id are returned.</li>
	 *     <li>{@code inclusiveAnchorMessageId}: inclusive lower anchor. If set, the returned batch includes this message and all
	 *     newer messages up to {@code exclusiveStartingMessageId} or the current newest message.</li>
	 * </ul>
	 * The {@code numberOfOlderMessages} parameter adds a bounded buffer before the active lower boundary. If
	 * {@code inclusiveAnchorMessageId} is set, the buffer is loaded before that message. Otherwise, the buffer is loaded before
	 * {@code exclusiveStartingMessageId}, or before the newest message when no upper boundary is set.
	 *
	 * @param exclusiveStartingMessageId exclusive upper message id, or {@code null} to include messages up to the current newest message
	 * @param inclusiveAnchorMessageId inclusive anchor message id, or {@code null} when no anchor needs to be forced into the result
	 * @param numberOfOlderMessages maximum number of messages to include before the active lower boundary
	 * @return message batch in ascending chronological order
	 */
	ChatMessageBatch getChatMessages(Integer exclusiveStartingMessageId, Integer inclusiveAnchorMessageId, int numberOfOlderMessages);

}
