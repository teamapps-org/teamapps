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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icon.material.MaterialIconStyles;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatDisplay extends AbstractComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final Event<Integer> onMessageViewed = new Event<>();
	public final Event<PhotoClickedEventData> onPhotoClicked = new Event<>();

	private final ChatDisplayModel model;
	private int messagesFetchSize = 50;
	private int initialTopMessageId = -1;
	private int earliestKnownMessageId = Integer.MAX_VALUE;

	private Icon<?, ?> deletedMessageIcon = MaterialIcon.DELETE.withStyle(MaterialIconStyles.OUTLINE_GREY_900);

	private Function<ChatMessage, Component> contextMenuProvider = null;
	private final Map<Integer, List<Component>> renderedContentComponentsByMessageId = new HashMap<>();

	public ChatDisplay(ChatDisplayModel model) {
		this.model = model;
		model.onMessagesAdded().addListener(chatMessages -> {
			updateEarliestKnownMessageId(chatMessages);
			queueCommandIfRendered(() -> new UiChatDisplay.AddMessagesCommand(getId(), createUiChatMessageBatch(chatMessages)));
		});
		model.onMessageChanged().addListener((chatMessage) -> {
			if (earliestKnownMessageId <= chatMessage.getId()) {
				queueCommandIfRendered(() -> new UiChatDisplay.UpdateMessageCommand(getId(), createUiChatMessage(chatMessage)));
			}
		});
		model.onMessageDeleted().addListener((messageId) -> {
			if (earliestKnownMessageId <= messageId) {
				unrenderContentComponents(messageId);
				queueCommandIfRendered(() -> new UiChatDisplay.DeleteMessageCommand(getId(), messageId));
			}
		});
		model.onAllDataChanged().addListener(aVoid -> {
			ChatMessageBatch messageBatch = getInitialMessageBatch();
			this.earliestKnownMessageId = Integer.MAX_VALUE;
			updateEarliestKnownMessageId(messageBatch);
			unrenderAllContentComponents();
			queueCommandIfRendered(() -> new UiChatDisplay.ClearMessagesCommand(getId(), createUiChatMessageBatch(messageBatch)));
		});
	}

	@Override
	public UiChatDisplay createUiComponent() {
		UiChatDisplay uiChatDisplay = new UiChatDisplay();
		mapAbstractUiComponentProperties(uiChatDisplay);
		ChatMessageBatch modelResponse = getInitialMessageBatch();
		this.earliestKnownMessageId = Integer.MAX_VALUE;
		updateEarliestKnownMessageId(modelResponse);
		uiChatDisplay.setInitialMessages(createUiChatMessageBatch(modelResponse));
		uiChatDisplay.setInitialTopMessageId(initialTopMessageId);
		uiChatDisplay.setContextMenuEnabled(contextMenuProvider != null);
		uiChatDisplay.setDeletedMessageIcon(getSessionContext().resolveIcon(deletedMessageIcon));
		return uiChatDisplay;
	}

	private ChatMessageBatch getInitialMessageBatch() {
		ChatMessageBatch lastMessages = model.getLastChatMessages(messagesFetchSize);
		if (initialTopMessageId <= 0 || lastMessages.containsMessage(initialTopMessageId)) {
			return lastMessages;
		}
		return model.getLastChatMessages(initialTopMessageId, 5);
	}

	private void updateEarliestKnownMessageId(ChatMessageBatch response) {
		earliestKnownMessageId = response.getEarliestMessageId() != null && response.getEarliestMessageId() < this.earliestKnownMessageId ? response.getEarliestMessageId() : earliestKnownMessageId;
	}

	@Override
	public Object handleUiQuery(UiQuery query) {
		switch (query.getUiQueryType()) {
			case UI_CHAT_DISPLAY_REQUEST_PREVIOUS_MESSAGES: {
				ChatMessageBatch response = model.getPreviousMessages(earliestKnownMessageId, messagesFetchSize);
				updateEarliestKnownMessageId(response);
				return createUiChatMessageBatch(response);
			}
			case UI_CHAT_DISPLAY_REQUEST_CONTEXT_MENU: {
				UiChatDisplay.RequestContextMenuQuery q = (UiChatDisplay.RequestContextMenuQuery) query;
				ChatMessage chatMessage = model.getChatMessageById(q.getChatMessageId());
				if (chatMessage != null) {
					Component component = contextMenuProvider.apply(chatMessage);
					return component != null ? component.createUiReference() : null;
				}
			}
			default:
				return super.handleUiQuery(query);
		}
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_CHAT_DISPLAY_MESSAGE_VIEWED:
				UiChatDisplay.MessageViewedEvent messageViewedEvent = (UiChatDisplay.MessageViewedEvent) event;
				onMessageViewed.fire(messageViewedEvent.getMessageId());
				break;
			case UI_CHAT_DISPLAY_PHOTO_CLICKED:
				UiChatDisplay.PhotoClickedEvent photoClickedEvent = (UiChatDisplay.PhotoClickedEvent) event;
				onPhotoClicked.fire(new PhotoClickedEventData(photoClickedEvent.getMessageId(), photoClickedEvent.getPhotoIndex()));
				break;
		}
	}

	private List<UiChatMessage> createUiChatMessages(List<ChatMessage> chatMessages) {
		return chatMessages.stream()
				.map(this::createUiChatMessage)
				.collect(Collectors.toList());
	}

	private UiChatMessageBatch createUiChatMessageBatch(ChatMessageBatch batch) {
		List<UiChatMessage> uiMessages = batch.getMessages().stream().map(this::createUiChatMessage).collect(Collectors.toList());
		return new UiChatMessageBatch(uiMessages, batch.isContainsFirstMessage());
	}

	private UiChatMessage createUiChatMessage(ChatMessage message) {
		UiChatMessage uiChatMessage = new UiChatMessage();
		uiChatMessage.setId(message.getId());
		uiChatMessage.setUserNickname(message.getUserNickname());
		uiChatMessage.setUserImageUrl(message.getUserImage().getUrl(getSessionContext()));
		uiChatMessage.setText(message.getText());
		uiChatMessage.setContent(createUiChatMessageContent(message));
		uiChatMessage.setPhotos(message.getPhotos() != null ? message.getPhotos().stream()
				.map(this::createUiChatPhoto)
				.collect(Collectors.toList()) : null);
		uiChatMessage.setFiles(message.getFiles() != null ? message.getFiles().stream()
				.map(this::createUiChatFile)
				.collect(Collectors.toList()) : null);
		uiChatMessage.setDeleted(message.isDeleted());
		return uiChatMessage;
	}

	private List<UiChatMessageContent> createUiChatMessageContent(ChatMessage message) {
		List<ChatMessageContent> content = message.getContent() == null ? List.of() : message.getContent();
		List<Component> contentComponents = content.stream()
				.filter(ChatMessageComponentContent.class::isInstance)
				.map(ChatMessageComponentContent.class::cast)
				.map(ChatMessageComponentContent::component)
				.filter(Objects::nonNull)
				.toList();
		syncRenderedContentComponents(message.getId(), contentComponents);
		return content.stream()
				.map(contentPart -> createUiChatMessageContent(message.getId(), contentPart))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private UiChatMessageContent createUiChatMessageContent(int messageId, ChatMessageContent contentPart) {
		UiChatMessageContent uiContent = new UiChatMessageContent();
		if (contentPart instanceof ChatMessageTextContent textContent) {
			uiContent.setText(textContent.text());
			return uiContent;
		} else if (contentPart instanceof ChatMessageComponentContent componentContent && componentContent.component() != null) {
			uiContent.setComponent(createContentComponentReference(messageId, componentContent.component()));
			return uiContent;
		}
		return null;
	}

	private void syncRenderedContentComponents(int messageId, List<Component> contentComponents) {
		List<Component> previousComponents = renderedContentComponentsByMessageId.get(messageId);
		if (previousComponents != null && !previousComponents.equals(contentComponents)) {
			unrenderContentComponents(messageId);
		}
		if (contentComponents.isEmpty()) {
			renderedContentComponentsByMessageId.remove(messageId);
		} else {
			contentComponents.forEach(component -> component.setParent(this));
			renderedContentComponentsByMessageId.put(messageId, new ArrayList<>(contentComponents));
		}
	}

	private UiClientObjectReference createContentComponentReference(int messageId, Component contentComponent) {
		contentComponent.setParent(this);
		return contentComponent.createUiReference();
	}

	private void unrenderContentComponents(int messageId) {
		List<Component> contentComponents = renderedContentComponentsByMessageId.remove(messageId);
		if (contentComponents != null) {
			contentComponents.stream()
					.filter(Component::isRendered)
					.forEach(Component::unrender);
		}
	}

	private void unrenderAllContentComponents() {
		renderedContentComponentsByMessageId.keySet().stream().toList().forEach(this::unrenderContentComponents);
	}

	private UiChatPhoto createUiChatPhoto(ChatPhoto photo) {
		UiChatPhoto uiChatPhoto = new UiChatPhoto();
		uiChatPhoto.setFileName(photo.getFileName());
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
		boolean changed = messagesFetchSize != this.messagesFetchSize;
		this.messagesFetchSize = messagesFetchSize;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public int getInitialTopMessageId() {
		return initialTopMessageId;
	}

	public void setInitialTopMessageId(int initialTopMessageId) {
		boolean changed = initialTopMessageId != this.initialTopMessageId;
		this.initialTopMessageId = initialTopMessageId;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public Function<ChatMessage, Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<ChatMessage, Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		queueCommandIfRendered(() -> new UiChatDisplay.CloseContextMenuCommand(getId()));
	}

	public void scrollToMessage(int messageId) {
		queueCommandIfRendered(() -> new UiChatDisplay.ScrollToMessageCommand(getId(), messageId));
	}

	public Icon<?, ?> getDeletedMessageIcon() {
		return deletedMessageIcon;
	}

	public void setDeletedMessageIcon(Icon<?, ?> deletedMessageIcon) {
		this.deletedMessageIcon = deletedMessageIcon;
	}
}
