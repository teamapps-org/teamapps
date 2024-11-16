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
package org.teamapps.projector.component.chat;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icon.material.MaterialIconStyles;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.ComponentConfig;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ClientObjectLibrary(ChatLibrary.class)
public class ChatDisplay extends AbstractComponent implements DtoChatDisplayEventHandler {

	private final DtoChatDisplayClientObjectChannel clientObjectChannel = new DtoChatDisplayClientObjectChannel(getClientObjectChannel());

	private final ChatDisplayModel model;
	private int messagesFetchSize = 50;
	private int earliestKnownMessageId = Integer.MAX_VALUE;

	private Icon<?, ?> deletedMessageIcon = MaterialIcon.DELETE.withStyle(MaterialIconStyles.OUTLINE_GREY_900);

	private Function<ChatMessage, Component> contextMenuProvider = null;

	public ChatDisplay(ChatDisplayModel model) {
		this.model = model;
		model.onMessagesAdded().addListener(chatMessages -> {
			updateEarliestKnownMessageId(chatMessages);
			clientObjectChannel.addMessages(createDtoChatMessageBatch(chatMessages));
		});
		model.onMessageChanged().addListener((chatMessage) -> {
			if (earliestKnownMessageId <= chatMessage.getId()) {
				clientObjectChannel.updateMessage(createDtoChatMessage(chatMessage));
			}
		});
		model.onMessageDeleted().addListener((messageId) -> {
			if (earliestKnownMessageId <= messageId) {
				clientObjectChannel.deleteMessage(messageId);
			}
		});
		model.onAllDataChanged().addListener(aVoid -> {
			ChatMessageBatch messageBatch = this.getModel().getLastChatMessages(this.messagesFetchSize);
			this.earliestKnownMessageId = Integer.MAX_VALUE;
			updateEarliestKnownMessageId(messageBatch);
			clientObjectChannel.clearMessages(createDtoChatMessageBatch(messageBatch));
		});
	}

	@Override
	public ComponentConfig createConfig() {
		DtoChatDisplay uiChatDisplay = new DtoChatDisplay();
		mapAbstractConfigProperties(uiChatDisplay);
		ChatMessageBatch modelResponse = model.getLastChatMessages(messagesFetchSize);
		updateEarliestKnownMessageId(modelResponse);
		uiChatDisplay.setInitialMessages(createDtoChatMessageBatch(modelResponse));
		uiChatDisplay.setContextMenuEnabled(contextMenuProvider != null);
		uiChatDisplay.setDeletedMessageIcon(getSessionContext().resolveIcon(deletedMessageIcon));
		return uiChatDisplay;
	}

	private void updateEarliestKnownMessageId(ChatMessageBatch response) {
		earliestKnownMessageId = response.getEarliestMessageId() != null && response.getEarliestMessageId() < this.earliestKnownMessageId ? response.getEarliestMessageId() : earliestKnownMessageId;
	}

	@Override
	public DtoChatMessageBatch handleRequestPreviousMessages() {
		ChatMessageBatch response = model.getPreviousMessages(earliestKnownMessageId, messagesFetchSize);
		updateEarliestKnownMessageId(response);
		return createDtoChatMessageBatch(response);
	}

	@Override
	public ClientObject handleRequestContextMenu(int chatMessageId) {
		ChatMessage chatMessage = model.getChatMessageById(chatMessageId);
		return chatMessage != null ? contextMenuProvider.apply(chatMessage) : null;
	}


	private DtoChatMessageBatch createDtoChatMessageBatch(ChatMessageBatch batch) {
		List<DtoChatMessage> uiMessages = batch.getMessages().stream().map(this::createDtoChatMessage).collect(Collectors.toList());
		return new DtoChatMessageBatch(uiMessages, batch.isContainsFirstMessage());
	}

	private DtoChatMessage createDtoChatMessage(ChatMessage message) {
		DtoChatMessage uiChatMessage = new DtoChatMessage();
		uiChatMessage.setId(message.getId());
		uiChatMessage.setUserNickname(message.getUserNickname());
		uiChatMessage.setUserImageUrl(message.getUserImage().getUrl(getSessionContext()));
		uiChatMessage.setText(message.getText());
		uiChatMessage.setPhotos(message.getPhotos() != null ? message.getPhotos().stream()
				.map(photo -> createDtoChatPhoto(photo))
				.collect(Collectors.toList()) : null);
		uiChatMessage.setFiles(message.getFiles() != null ? message.getFiles().stream()
				.map(file -> createDtoChatFile(file))
				.collect(Collectors.toList()) : null);
		uiChatMessage.setDeleted(message.isDeleted());
		return uiChatMessage;
	}

	private DtoChatPhoto createDtoChatPhoto(ChatPhoto photo) {
		DtoChatPhoto uiChatPhoto = new DtoChatPhoto();
		uiChatPhoto.setThumbnailUrl(photo.getThumbnail() != null ? photo.getThumbnail().getUrl(getSessionContext()) : null);
		uiChatPhoto.setImageUrl(photo.getImage().getUrl(getSessionContext()));
		return uiChatPhoto;
	}

	private DtoChatFile createDtoChatFile(ChatFile file) {
		DtoChatFile uiChatFile = new DtoChatFile();
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
	}

	public Function<ChatMessage, Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<ChatMessage, Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
		clientObjectChannel.setContextMenuEnabled(contextMenuProvider != null);
	}

	public void closeContextMenu() {
		clientObjectChannel.closeContextMenu();
	}

	public Icon<?, ?> getDeletedMessageIcon() {
		return deletedMessageIcon;
	}

	public void setDeletedMessageIcon(Icon<?, ?> deletedMessageIcon) {
		this.deletedMessageIcon = deletedMessageIcon;
		clientObjectChannel.setDeletedMessageIcon(getSessionContext().resolveIcon(deletedMessageIcon));
	}
}
