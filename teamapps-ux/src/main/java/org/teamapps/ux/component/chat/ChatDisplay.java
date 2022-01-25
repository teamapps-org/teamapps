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
package org.teamapps.ux.component.chat;

import org.teamapps.dto.*;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icon.material.MaterialIconStyles;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatDisplay extends AbstractComponent {

	private final ChatDisplayModel model;
	private int messagesFetchSize = 50;
	private Integer earliestKnownMessageId = null;

	private Icon<?, ?> deletedMessageIcon = MaterialIcon.DELETE.withStyle(MaterialIconStyles.OUTLINE_GREY_900);

	private Function<ChatMessage, CompletableFuture<Component>> contextMenuProvider = null;

	public ChatDisplay(ChatDisplayModel model) {
		this.model = model;
		model.onMessagesAdded().addListener(chatMessages -> {
			queueCommandIfRendered(() -> new UiChatDisplay.AddMessagesCommand(getId(), createUiChatMessageBatch(chatMessages)));
		});
		model.onMessageChanged().addListener((chatMessage) -> {
			queueCommandIfRendered(() -> new UiChatDisplay.UpdateMessageCommand(getId(), createUiChatMessage(chatMessage)));
		});
		model.onMessageDeleted().addListener((messageId) -> {
			queueCommandIfRendered(() -> new UiChatDisplay.DeleteMessageCommand(getId(), messageId));
		});
		model.onAllDataChanged().addListener(aVoid -> {
			ChatMessageBatch messageBatch = this.getModel().getLastChatMessages(this.messagesFetchSize);
			queueCommandIfRendered(() -> new UiChatDisplay.ClearMessagesCommand(getId(), createUiChatMessageBatch(messageBatch)));
		});
	}

	@Override
	public UiChatDisplay createUiComponent() {
		UiChatDisplay uiChatDisplay = new UiChatDisplay();
		mapAbstractUiComponentProperties(uiChatDisplay);
		ChatMessageBatch modelResponse = model.getLastChatMessages(messagesFetchSize);
		updateEarliestKnownMessageId(modelResponse);
		uiChatDisplay.setInitialMessages(createUiChatMessageBatch(modelResponse));
		uiChatDisplay.setContextMenuEnabled(contextMenuProvider != null);
		uiChatDisplay.setDeletedMessageIcon(getSessionContext().resolveIcon(deletedMessageIcon));
		return uiChatDisplay;
	}

	private void updateEarliestKnownMessageId(ChatMessageBatch response) {
		earliestKnownMessageId = response.getEarliestMessageId() != null ? response.getEarliestMessageId() : earliestKnownMessageId;
	}

	@Override
	public CompletableFuture<?> handleUiQuery(UiQuery query) {
		switch (query.getUiQueryType()) {
			case UI_CHAT_DISPLAY_REQUEST_PREVIOUS_MESSAGES: {
				ChatMessageBatch response = model.getPreviousMessages(earliestKnownMessageId, messagesFetchSize);
				updateEarliestKnownMessageId(response);
				return CompletableFuture.completedFuture(createUiChatMessageBatch(response));
			}
			case UI_CHAT_DISPLAY_REQUEST_CONTEXT_MENU: {
				UiChatDisplay.RequestContextMenuQuery q = (UiChatDisplay.RequestContextMenuQuery) query;
				ChatMessage chatMessage = model.getChatMessageById(q.getChatMessageId());
				if (chatMessage != null) {
					return contextMenuProvider.apply(chatMessage)
							.thenApply(ClientObject::createUiReference);
				}
			}
			default:
				return CompletableFuture.failedFuture(new IllegalArgumentException("unknown query"));
		}
	}

	private List<UiChatMessage> createUiChatMessages(List<ChatMessage> chatMessages) {
		return chatMessages.stream()
				.map(message -> createUiChatMessage(message))
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
		uiChatMessage.setPhotos(message.getPhotos() != null ? message.getPhotos().stream()
				.map(photo -> createUiChatPhoto(photo))
				.collect(Collectors.toList()) : null);
		uiChatMessage.setFiles(message.getFiles() != null ? message.getFiles().stream()
				.map(file -> createUiChatFile(file))
				.collect(Collectors.toList()) : null);
		uiChatMessage.setDeleted(message.isDeleted());
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

	public Function<ChatMessage, CompletableFuture<Component>> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<ChatMessage, CompletableFuture<Component>> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		queueCommandIfRendered(() -> new UiChatDisplay.CloseContextMenuCommand(getId()));
	}

	public Icon<?, ?> getDeletedMessageIcon() {
		return deletedMessageIcon;
	}

	public void setDeletedMessageIcon(Icon<?, ?> deletedMessageIcon) {
		this.deletedMessageIcon = deletedMessageIcon;
	}
}
