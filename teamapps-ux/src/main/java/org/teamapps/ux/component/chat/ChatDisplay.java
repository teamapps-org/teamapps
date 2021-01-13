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

import org.teamapps.dto.*;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatDisplay extends AbstractComponent {

	private final ChatDisplayModel model;
	private int messagesFetchSize = 30;

	public ChatDisplay(ChatDisplayModel model) {
		this.model = model;
		model.onMessagesAdded().addListener(chatMessages -> {
			queueCommandIfRendered(() -> new UiChatDisplay.AddChatMessagesCommand(getId(), createUiChatMessages(chatMessages.getMessages()), false, chatMessages.isIncludesFirstMessage()));
		});
		model.onAllDataChanged().addListener(aVoid -> {
			ChatMessageBatch messageBatch = this.getModel().getLastChatMessages(this.messagesFetchSize);
			queueCommandIfRendered(() -> new UiChatDisplay.ReplaceChatMessagesCommand(getId(), createUiChatMessages(messageBatch.getMessages()), messageBatch.isIncludesFirstMessage()));
		});
	}

	@Override
	public UiChatDisplay createUiComponent() {
		UiChatDisplay uiChatDisplay = new UiChatDisplay();
		mapAbstractUiComponentProperties(uiChatDisplay);
		ChatMessageBatch modelResponse = model.getLastChatMessages(messagesFetchSize);
		uiChatDisplay.setMessages(createUiChatMessages(modelResponse.getMessages()));
		uiChatDisplay.setIncludesFirstMessage(modelResponse.isIncludesFirstMessage());
		return uiChatDisplay;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_CHAT_DISPLAY_PREVIOUS_MESSAGES_REQUESTED:
				UiChatDisplay.PreviousMessagesRequestedEvent requestedEvent = (UiChatDisplay.PreviousMessagesRequestedEvent) event;
				ChatMessageBatch response = model.getPreviousMessages(requestedEvent.getEarliestKnownMessageId(), messagesFetchSize);
				queueCommandIfRendered(() -> new UiChatDisplay.AddChatMessagesCommand(getId(), createUiChatMessages(response.getMessages()), true, response.isIncludesFirstMessage()));
		}
	}

	private List<UiChatMessage> createUiChatMessages(List<ChatMessage> chatMessages) {
		return chatMessages.stream()
				.map(message -> createUiChatMessage(message))
				.collect(Collectors.toList());
	}

	private UiChatMessage createUiChatMessage(ChatMessage message) {
		UiChatMessage uiChatMessage = new UiChatMessage();
		uiChatMessage.setId(message.getId());
		uiChatMessage.setUserNickname(message.getUserNickname());
		uiChatMessage.setUserImageUrl(message.getUserImage().getUrl(getSessionContext()));
		uiChatMessage.setText(message.getText());
		uiChatMessage.setPhotos(message.getPhotos() != null ? message.getPhotos().stream()
				.map(photo -> createUiChatPhoto(photo))
				.collect(Collectors.toList()) : null);
		uiChatMessage.setFiles(message.getFiles() != null ? message.getFiles().stream()
				.map(file -> createUiChatFile(file))
				.collect(Collectors.toList()) : null);
		return uiChatMessage;
	}

	private UiChatPhoto createUiChatPhoto(ChatPhoto photo) {
		UiChatPhoto uiChatPhoto = new UiChatPhoto();
		uiChatPhoto.setThumbnailUrl(photo.getThumbnail() != null ? photo.getThumbnail().getUrl(getSessionContext()) : null);
		uiChatPhoto.setImageUrl(photo.getImage().getUrl(getSessionContext()));
		return uiChatPhoto;
	}

	private UiChatFile createUiChatFile(ChatFile file) {
		UiChatFile uiChatFile = new UiChatFile();
		uiChatFile.setName(file.getName());
		uiChatFile.setIcon(getSessionContext().resolveIcon(file.getIcon()));
		uiChatFile.setLength(file.getLength());
		uiChatFile.setThumbnailUrl(file.getThumbnail() != null ? file.getThumbnail().getUrl(getSessionContext()) : null);
		uiChatFile.setDownloadUrl(file.getDownload().getUrl(getSessionContext()));
		return uiChatFile;
	}

	public ChatDisplayModel getModel() {
		return model;
	}

	public int getMessagesFetchSize() {
		return messagesFetchSize;
	}

	public void setMessagesFetchSize(int messagesFetchSize) {
		this.messagesFetchSize = messagesFetchSize;
		reRenderIfRendered();
	}

}
