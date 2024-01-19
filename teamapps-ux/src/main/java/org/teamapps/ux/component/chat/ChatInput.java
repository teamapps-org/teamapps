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

import org.teamapps.dto.UiChatInput;
import org.teamapps.dto.UiEvent;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatInput extends AbstractComponent {

	public final Event<NewChatMessageData> onMessageSent = new Event<>();

	private long maxBytesPerUpload = 5000000;
	private String uploadUrl = "/upload";
	private Icon defaultAttachmentIcon = MaterialIcon.ATTACHMENT;
	private int messageLengthLimit = 10_000; // 10k characters, < 0 for no limit
	private boolean attachmentsEnabled = true;

	@Override
	public UiChatInput createUiComponent() {
		UiChatInput uiChatInput = new UiChatInput(getSessionContext().resolveIcon(defaultAttachmentIcon));
		mapAbstractUiComponentProperties(uiChatInput);
		uiChatInput.setMaxBytesPerUpload(maxBytesPerUpload);
		uiChatInput.setUploadUrl(uploadUrl);
		uiChatInput.setMessageLengthLimit(messageLengthLimit);
		uiChatInput.setAttachmentsEnabled(attachmentsEnabled);
		return uiChatInput;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_CHAT_INPUT_MESSAGE_SENT:
				UiChatInput.MessageSentEvent messageSentEvent = (UiChatInput.MessageSentEvent) event;
				String text = messageSentEvent.getMessage().getText();
				if (messageLengthLimit > 0 && text.length() > messageLengthLimit) {
					text = text.substring(0, messageLengthLimit);
				}
				List<NewChatMessageData.UploadedFile> uploadedFiles = messageSentEvent.getMessage().getUploadedFiles().stream()
						.map(uiFile -> new NewChatMessageData.UploadedFile(uiFile.getUploadedFileUuid(), uiFile.getFileName()))
						.collect(Collectors.toList());
				NewChatMessageData newChatMessageData = new NewChatMessageData(text, uploadedFiles);
				onMessageSent.fire(newChatMessageData);
				break;
			case UI_CHAT_INPUT_UPLOAD_TOO_LARGE:
				break;
			case UI_CHAT_INPUT_UPLOAD_STARTED:
				break;
			case UI_CHAT_INPUT_UPLOAD_CANCELED:
				break;
			case UI_CHAT_INPUT_UPLOAD_FAILED:
				break;
			case UI_CHAT_INPUT_UPLOAD_SUCCESSFUL:
				break;
		}
	}

	public long getMaxBytesPerUpload() {
		return maxBytesPerUpload;
	}

	public void setMaxBytesPerUpload(long maxBytesPerUpload) {
		boolean changed = maxBytesPerUpload != this.maxBytesPerUpload;
		this.maxBytesPerUpload = maxBytesPerUpload;
		if (changed) {
			reRenderIfRendered();
		} // TODO
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		boolean changed = !Objects.equals(uploadUrl, this.uploadUrl);
		this.uploadUrl = uploadUrl;
		if (changed) {
			reRenderIfRendered();
		} // TODO
	}

	public Icon getDefaultAttachmentIcon() {
		return defaultAttachmentIcon;
	}

	public void setDefaultAttachmentIcon(Icon defaultAttachmentIcon) {
		this.defaultAttachmentIcon = defaultAttachmentIcon;
	}

	public int getMessageLengthLimit() {
		return messageLengthLimit;
	}

	public void setMessageLengthLimit(int messageLengthLimit) {
		this.messageLengthLimit = messageLengthLimit;
	}

	public boolean isAttachmentsEnabled() {
		return attachmentsEnabled;
	}

	public void setAttachmentsEnabled(boolean attachmentsEnabled) {
		if (attachmentsEnabled != this.attachmentsEnabled) {
			this.attachmentsEnabled = attachmentsEnabled;
			queueCommandIfRendered(() -> new UiChatInput.SetAttachmentsEnabledCommand(getId(), attachmentsEnabled));
		}
	}
}
