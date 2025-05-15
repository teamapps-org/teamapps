/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamFileRequest {
	@JsonProperty("stream")
	private final String streamUuid;
	private final Set<MediaKind> kinds;
	private final String filePath;
	private final boolean relativePath;
	private final boolean restartOnExit;
	private final Integer videoBitrate;
	private final Integer width;
	private final Integer height;
	private final Float frameRate;
	private final Integer audioSampleRate;
	private final Integer audioChannels;
	private final String[] additionalInputOptions;

	/**
	 * @deprecated Use {@link StreamFileRequest#builder(String, Set, String, boolean)} instead.
	 */
	@Deprecated
	public StreamFileRequest(String streamUuid, Set<MediaKind> kinds, String filePath, boolean relativePath,
	                         boolean restartOnExit, Integer videoBitrate, Integer width, Integer height,
	                         Float frameRate,
	                         Integer audioSampleRate, Integer audioChannels, String[] additionalInputOptions) {
		this.streamUuid = streamUuid;
		this.kinds = kinds;
		this.filePath = filePath;
		this.relativePath = relativePath;
		this.restartOnExit = restartOnExit;
		this.videoBitrate = videoBitrate;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.audioSampleRate = audioSampleRate;
		this.audioChannels = audioChannels;
		this.additionalInputOptions = additionalInputOptions;
	}

	public static StreamFileRequestBuilder builder(String streamUuid, Set<MediaKind> kinds, String filePath, boolean relativePath) {
		return new StreamFileRequestBuilder(streamUuid, kinds, filePath, relativePath);
	}

	public String getStreamUuid() {
		return streamUuid;
	}

	public Set<MediaKind> getKinds() {
		return kinds;
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isRelativePath() {
		return relativePath;
	}

	public boolean isRestartOnExit() {
		return restartOnExit;
	}

	public Integer getVideoBitrate() {
		return videoBitrate;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Float getFrameRate() {
		return frameRate;
	}

	public Integer getAudioSampleRate() {
		return audioSampleRate;
	}

	public Integer getAudioChannels() {
		return audioChannels;
	}

	public String[] getAdditionalInputOptions() {
		return additionalInputOptions;
	}
}
