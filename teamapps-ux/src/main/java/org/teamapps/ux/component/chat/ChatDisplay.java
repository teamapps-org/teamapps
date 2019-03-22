/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.jetbrains.annotations.NotNull;
import org.teamapps.dto.UiChatDisplay;
import org.teamapps.dto.UiChatFile;
import org.teamapps.dto.UiChatMessage;
import org.teamapps.dto.UiChatPhoto;
import org.teamapps.dto.UiEvent;
import org.teamapps.event.EventListener;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.resource.Resource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatDisplay extends AbstractComponent {

	private final ChatDisplayModel model;
	private int messagesFetchSize = 20;

	private final EventListener<ChatMessageBatch> modelAddMessagesAddedListener = chatMessages -> {
		queueCommandIfRendered(() -> new UiChatDisplay.AddChatMessagesCommand(getId(), createUiChatMessages(chatMessages.getMessages()), false, chatMessages.isIncludesFirstMessage()));
	};
	private final EventListener<Void> modelAllDataChangedListener = aVoid -> {
		ChatMessageBatch messageBatch = this.getModel().getLastChatMessages(this.messagesFetchSize);
		queueCommandIfRendered(() -> new UiChatDisplay.ReplaceChatMessagesCommand(getId(), createUiChatMessages(messageBatch.getMessages()), messageBatch.isIncludesFirstMessage()));
	};

	private Function<Resource, String> resourceToUrlConverter = resource -> {
		if (resource != null) {
			return getSessionContext().createResourceLink(() -> resource.getInputStream(), resource.getLength(), resource.getName());
		} else {
			return null;
		}
	};

	public ChatDisplay(ChatDisplayModel model) {
		this.model = model;
		model.onMessagesAdded().addListener(modelAddMessagesAddedListener);
		model.onAllDataChanged().addListener(modelAllDataChangedListener);
	}

	@Override
	public UiChatDisplay createUiComponent() {
		UiChatDisplay uiChatDisplay = new UiChatDisplay(getId());
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

	@NotNull
	private List<UiChatMessage> createUiChatMessages(List<ChatMessage> chatMessages) {
		return chatMessages.stream()
				.map(message -> createUiChatMessage(message))
				.collect(Collectors.toList());
	}

	private UiChatMessage createUiChatMessage(ChatMessage message) {
		UiChatMessage uiChatMessage = new UiChatMessage();
		uiChatMessage.setId(message.getId());
		uiChatMessage.setUserNickname(message.getUserNickname());
		uiChatMessage.setUserImageUrl(resourceToUrlConverter.apply(message.getUserImage()));
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
		uiChatPhoto.setThumbnailUrl(resourceToUrlConverter.apply(photo.getThumbnail()));
		uiChatPhoto.setImageUrl(resourceToUrlConverter.apply(photo.getImage()));
		return uiChatPhoto;
	}

	private UiChatFile createUiChatFile(ChatFile file) {
		UiChatFile uiChatFile = new UiChatFile();
		uiChatFile.setName(file.getName());
		uiChatFile.setIcon(getSessionContext().resolveIcon(file.getIcon()));
		uiChatFile.setThumbnailUrl(resourceToUrlConverter.apply(file.getThumbnail()));
		uiChatFile.setDownloadUrl(resourceToUrlConverter.apply(file.getDownload()));
		return uiChatFile;
	}

	@Override
	protected void doDestroy() {
		this.model.onMessagesAdded().removeListener(modelAddMessagesAddedListener);
		this.model.onAllDataChanged().removeListener(modelAllDataChangedListener);
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

	public Function<Resource, String> getResourceToUrlConverter() {
		return resourceToUrlConverter;
	}

	public void setResourceToUrlConverter(Function<Resource, String> resourceToUrlConverter) {
		this.resourceToUrlConverter = resourceToUrlConverter;
	}
}
