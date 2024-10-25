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

import org.teamapps.commons.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(ChatLibrary.class)
public class ChatInput extends AbstractComponent implements DtoChatInputEventHandler {

	private final DtoChatInputClientObjectChannel clientObjectChannel = new DtoChatInputClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<NewChatMessageData> onMessageSent = new ProjectorEvent<>(clientObjectChannel::toggleMessageSentEvent);
	public final Event<DtoChatInput.UploadTooLargeEventWrapper> onUploadTooLarge = new Event<>();
	public final Event<DtoChatInput.UploadStartedEventWrapper> onUploadStarted = new Event<>();
	public final Event<DtoChatInput.UploadCanceledEventWrapper> onUploadCanceled = new Event<>();
	public final Event<DtoChatInput.UploadFailedEventWrapper> onUploadFailed = new Event<>();
	public final Event<DtoChatInput.UploadSuccessfulEventWrapper> onUploadSuccessful = new Event<>();
	public final Event<String> onFileItemClicked = new Event<>();
	public final Event<String> onFileItemRemoved = new Event<>();

	private long maxBytesPerUpload = 5000000;
	private String uploadUrl = "/upload";
	private Icon<?, ?> defaultAttachmentIcon = MaterialIcon.ATTACHMENT;
	private int messageLengthLimit = 10_000; // 10k characters, < 0 for no limit
	private boolean attachmentsEnabled = true;

	@Override
	public ComponentConfig createConfig() {
		DtoChatInput uiChatInput = new DtoChatInput();
		mapAbstractConfigProperties(uiChatInput);
		uiChatInput.setDefaultFileIcon(getSessionContext().resolveIcon(defaultAttachmentIcon));
		uiChatInput.setMaxBytesPerUpload(maxBytesPerUpload);
		uiChatInput.setUploadUrl(uploadUrl);
		uiChatInput.setMessageLengthLimit(messageLengthLimit);
		uiChatInput.setAttachmentsEnabled(attachmentsEnabled);
		return uiChatInput;
	}

	@Override
	public void handleMessageSent(DtoNewChatMessageWrapper message) {
		String text = message.getText();
		if (messageLengthLimit > 0 && text.length() > messageLengthLimit) {
			text = text.substring(0, messageLengthLimit);
		}
		List<NewChatMessageData.UploadedFile> uploadedFiles = message.getUploadedFiles().stream()
				.map(uiFile -> new NewChatMessageData.UploadedFile(uiFile.getUploadedFileUuid(), uiFile.getFileName()))
				.collect(Collectors.toList());
		NewChatMessageData newChatMessageData = new NewChatMessageData(text, uploadedFiles);
		onMessageSent.fire(newChatMessageData);
	}

	@Override
	public void handleUploadTooLarge(DtoChatInput.UploadTooLargeEventWrapper event) {
		      onUploadTooLarge.fire(event);
	}

	@Override
	public void handleUploadStarted(DtoChatInput.UploadStartedEventWrapper event) {
		    onUploadStarted.fire(event);
	}

	@Override
	public void handleUploadCanceled(DtoChatInput.UploadCanceledEventWrapper event) {
		     onUploadCanceled.fire(event);
	}

	@Override
	public void handleUploadFailed(DtoChatInput.UploadFailedEventWrapper event) {
		      onUploadFailed.fire(event);
	}

	@Override
	public void handleUploadSuccessful(DtoChatInput.UploadSuccessfulEventWrapper event) {
		       onUploadSuccessful.fire(event);
	}

	@Override
	public void handleFileItemClicked(String fileItemUuid) {
		      onFileItemClicked.fire(fileItemUuid);
	}

	@Override
	public void handleFileItemRemoved(String fileItemUuid) {
		       onFileItemRemoved.fire(fileItemUuid);
	}

	public long getMaxBytesPerUpload() {
		return maxBytesPerUpload;
	}

	public void setMaxBytesPerUpload(long maxBytesPerUpload) {
		this.maxBytesPerUpload = maxBytesPerUpload;
		clientObjectChannel.setMaxBytesPerUpload(maxBytesPerUpload);
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		clientObjectChannel.setUploadUrl(uploadUrl);
	}

	public Icon<?, ?> getDefaultAttachmentIcon() {
		return defaultAttachmentIcon;
	}

	public void setDefaultAttachmentIcon(Icon<?, ?> defaultAttachmentIcon) {
		this.defaultAttachmentIcon = defaultAttachmentIcon;
		clientObjectChannel.setDefaultFileIcon(getSessionContext().resolveIcon(defaultAttachmentIcon));
	}

	public int getMessageLengthLimit() {
		return messageLengthLimit;
	}

	public void setMessageLengthLimit(int messageLengthLimit) {
		this.messageLengthLimit = messageLengthLimit;
		clientObjectChannel.setMessageLengthLimit(messageLengthLimit);
	}

	public boolean isAttachmentsEnabled() {
		return attachmentsEnabled;
	}

	public void setAttachmentsEnabled(boolean attachmentsEnabled) {
		if (attachmentsEnabled != this.attachmentsEnabled) {
			this.attachmentsEnabled = attachmentsEnabled;
			clientObjectChannel.setAttachmentsEnabled(attachmentsEnabled);
		}

	}
}
