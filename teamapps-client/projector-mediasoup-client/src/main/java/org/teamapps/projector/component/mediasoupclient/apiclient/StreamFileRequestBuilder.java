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
package org.teamapps.projector.component.mediasoupclient.apiclient;

import java.util.Set;

public class StreamFileRequestBuilder {
	private String streamUuid;
	private Set<MediaKind> kinds;
	private String filePath;
	private boolean relativePath;
	private boolean restartOnExit = false;
	private Integer videoBitrate = null;
	private Integer width;
	private Integer height;
	private Float frameRate;
	private Integer audioSampleRate;
	private Integer audioChannels;
	private String[] additionalInputOptions;

	public StreamFileRequestBuilder(String streamUuid, Set<MediaKind> kinds, String filePath, boolean relativePath) {
		this.streamUuid = streamUuid;
		this.kinds = kinds;
		this.filePath = filePath;
		this.relativePath = relativePath;
	}

	public StreamFileRequestBuilder setStreamUuid(String streamUuid) {
		this.streamUuid = streamUuid;
		return this;
	}

	public StreamFileRequestBuilder setKinds(Set<MediaKind> kinds) {
		this.kinds = kinds;
		return this;
	}

	public StreamFileRequestBuilder setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public StreamFileRequestBuilder setRelativePath(boolean relativePath) {
		this.relativePath = relativePath;
		return this;
	}

	public StreamFileRequestBuilder setRestartOnExit(boolean restartOnExit) {
		this.restartOnExit = restartOnExit;
		return this;
	}

	public StreamFileRequestBuilder setVideoBitrate(int videoBitrate) {
		this.videoBitrate = videoBitrate;
		return this;
	}

	public StreamFileRequestBuilder setWidth(int width) {
		this.width = width;
		return this;
	}

	public StreamFileRequestBuilder setHeight(int height) {
		this.height = height;
		return this;
	}

	public StreamFileRequestBuilder setFrameRate(float frameRate) {
		this.frameRate = frameRate;
		return this;
	}

	public StreamFileRequestBuilder setAudioSampleRate(int audioSampleRate) {
		this.audioSampleRate = audioSampleRate;
		return this;
	}

	public StreamFileRequestBuilder setAudioChannels(int audioChannels) {
		this.audioChannels = audioChannels;
		return this;
	}

	public StreamFileRequestBuilder setAdditionalInputOptions(String[] additionalInputOptions) {
		this.additionalInputOptions = additionalInputOptions;
		return this;
	}

	public StreamFileRequest build() {
		return new StreamFileRequest(streamUuid, kinds, filePath, relativePath, restartOnExit, videoBitrate, width, height, frameRate, audioSampleRate, audioChannels, additionalInputOptions);
	}

}
