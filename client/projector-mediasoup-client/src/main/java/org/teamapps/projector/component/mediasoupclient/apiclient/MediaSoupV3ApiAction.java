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

public enum MediaSoupV3ApiAction {
	GET_SERVER_CONFIGS("getServerConfigs"),
	CREATE_TRANSPORT("createTransport"),
	CONNECT_TRANSPORT("connectTransport"),
	CLOSE_TRANSPORT("closeTransport"),
	PRODUCE("produce", MediaSoupV3ApiOperation.PUBLISH),
	CONSUME("consume"),
	RESUME_CONSUMER("resumeConsumer"),
	PAUSE_CONSUMER("pauseConsumer"),
	CLOSE_CONSUMER("closeConsumer"),
	RESUME_PRODUCER("resumeProducer"),
	PAUSE_PRODUCER("pauseProducer"),
	CLOSE_PRODUCER("closeProducer"),
	FILE_STREAMING("fileStreaming", MediaSoupV3ApiOperation.STREAMING),
	LIVE_STREAMING("liveStreaming", MediaSoupV3ApiOperation.STREAMING),
	STOP_FILE_STREAMING("stopFileStreaming", MediaSoupV3ApiOperation.STREAMING),
	START_RECORDING("startRecording", MediaSoupV3ApiOperation.RECORDING),
	STOP_RECORDING("stopRecording", MediaSoupV3ApiOperation.RECORDING),
	CREATE_PIPE_TRANSPORT("createPipeTransport"),
	CONNECT_PIPE_TRANSPORT("connectPipeTransport"),
	SET_PREFERRED_LAYERS("setPreferredLayers"),
	SET_MAX_INCOMING_BITRATE("setMaxIncomingBitrate"),
	PRODUCERS_STATS("producersStats"),
	CONSUMERS_STATS("consumersStats"),
	TRANSPORT_STATS("transportStats"),
	PIPE_TO_REMOTE_PRODUCER("pipeToRemoteProducer"),
	PIPE_FROM_REMOTE_PRODUCER("pipeFromRemoteProducer"),
	WORKER_LOAD("workerLoad"),
	NUM_WORKERS("numWorkers"),
	RECORDED_STREAMS("recordedStreams", MediaSoupV3ApiOperation.RECORDING),
	STREAM_RECORDINGS("streamRecordings", MediaSoupV3ApiOperation.RECORDING),
	DELETE_STREAM_RECORDINGS("deleteStreamRecordings", MediaSoupV3ApiOperation.RECORDING),
	DELETE_RECORDING("deleteRecording"),
	PUSH_TO_SERVER_INPUTS("pushToServerInputs"),
	PULL_FROM_SERVER_INPUTS("pullFromServerInputs"),
	PUSH_TO_SERVER_OPTIONS("pushToServerOptions"),
	PUSH_TO_SERVER("pushToServer"),
	KINDS_BY_FILE("kindsByFile"),
	REQUEST_KEYFRAME("requestKeyframe"),
	LISTEN_STREAM_STARTED("listenStreamStarted"),
	LISTEN_STREAM_STOPPED("listenStreamStopped"),
	MIXER_START("mixerStart", MediaSoupV3ApiOperation.MIXER),
	MIXER_CLOSE("mixerClose", MediaSoupV3ApiOperation.MIXER),
	MIXER_ADD("mixerAdd", MediaSoupV3ApiOperation.MIXER),
	MIXER_REMOVE("mixerRemove", MediaSoupV3ApiOperation.MIXER),
	MIXER_UPDATE("mixerUpdate", MediaSoupV3ApiOperation.MIXER),
	MIXER_PIPE_START("mixerPipeStart", MediaSoupV3ApiOperation.MIXER),
	MIXER_PIPE_STOP("mixerPipeStop", MediaSoupV3ApiOperation.MIXER),
	LISTEN_MIXER_STOPPED("listenMixerStopped"),
	;

	private final String name;
	private final MediaSoupV3ApiOperation operation;

	MediaSoupV3ApiAction(String name) {
		this.name = name;
		this.operation = null;
	}

	MediaSoupV3ApiAction(String name, MediaSoupV3ApiOperation operation) {
		this.name = name;
		this.operation = operation;
	}

	public String getName() {
		return name;
	}
}
