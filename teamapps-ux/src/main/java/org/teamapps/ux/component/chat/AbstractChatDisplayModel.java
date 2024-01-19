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

import org.teamapps.event.Event;

public abstract class AbstractChatDisplayModel implements ChatDisplayModel {

	public final Event<ChatMessageBatch> onMessagesAdded = new Event<>();
	public final Event<Integer> onMessageDeleted = new Event<>();
	public final Event<ChatMessage> onMessageChanged = new Event<>();
	public final Event<Void> onAllDataChanged = new Event<>();

	@Override
	public Event<ChatMessageBatch> onMessagesAdded() {
		return onMessagesAdded;
	}

	@Override
	public Event<Integer> onMessageDeleted() {
		return onMessageDeleted;
	}

	@Override
	public Event<ChatMessage> onMessageChanged() {
		return onMessageChanged;
	}

	@Override
	public Event<Void> onAllDataChanged() {
		return onAllDataChanged;
	}
}
