package org.teamapps.ux.component.webrtc.apiclient;

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