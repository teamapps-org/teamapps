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
package org.teamapps.projector.components.common.chat;

import org.teamapps.projector.components.common.dto.DtoChatInput;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatInput extends AbstractComponent {

	public final ProjectorEvent<NewChatMessageData> onMessageSent = createProjectorEventBoundToUiEvent(DtoChatInput.MessageSentEvent.TYPE_ID);

	private long maxBytesPerUpload = 5000000;
	private String uploadUrl = "/upload";
	private Icon<?, ?> defaultAttachmentIcon = MaterialIcon.ATTACHMENT;
	private int messageLengthLimit = 10_000; // 10k characters, < 0 for no limit
	private boolean attachmentsEnabled = true;

	@Override
	public DtoChatInput createDto() {
		DtoChatInput uiChatInput = new DtoChatInput(getSessionContext().resolveIcon(defaultAttachmentIcon));
		mapAbstractUiComponentProperties(uiChatInput);
		uiChatInput.setMaxBytesPerUpload(maxBytesPerUpload);
		uiChatInput.setUploadUrl(uploadUrl);
		uiChatInput.setMessageLengthLimit(messageLengthLimit);
		uiChatInput.setAttachmentsEnabled(attachmentsEnabled);
		return uiChatInput;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoChatInput.MessageSentEvent.TYPE_ID -> {
				var messageSentEvent = event.as(DtoChatInput.MessageSentEventWrapper.class);
				String text = messageSentEvent.getMessage().getText();
				if (messageLengthLimit > 0 && text.length() > messageLengthLimit) {
					text = text.substring(0, messageLengthLimit);
				}
				List<NewChatMessageData.UploadedFile> uploadedFiles = messageSentEvent.getMessage().getUploadedFiles().stream()
						.map(uiFile -> new NewChatMessageData.UploadedFile(uiFile.getUploadedFileUuid(), uiFile.getFileName()))
						.collect(Collectors.toList());
				NewChatMessageData newChatMessageData = new NewChatMessageData(text, uploadedFiles);
				onMessageSent.fire(newChatMessageData);
			}
		}
		// TODO case UI_CHAT_INPUT_UPLOAD_TOO_LARGE:
		// TODO case UI_CHAT_INPUT_UPLOAD_STARTED:
		// TODO case UI_CHAT_INPUT_UPLOAD_CANCELED:
		// TODO case UI_CHAT_INPUT_UPLOAD_FAILED:
		// TODO case UI_CHAT_INPUT_UPLOAD_SUCCESSFUL:
	}

	public long getMaxBytesPerUpload() {
		return maxBytesPerUpload;
	}

	public void setMaxBytesPerUpload(long maxBytesPerUpload) {
		this.maxBytesPerUpload = maxBytesPerUpload;
		reRenderIfRendered(); // TODO
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		reRenderIfRendered(); // TODO
	}

	public Icon<?, ?> getDefaultAttachmentIcon() {
		return defaultAttachmentIcon;
	}

	public void setDefaultAttachmentIcon(Icon<?, ?> defaultAttachmentIcon) {
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
			sendCommandIfRendered(() -> new DtoChatInput.SetAttachmentsEnabledCommand(attachmentsEnabled));
		}
	}
}
