package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.teamapps.ux.component.webrtc.apiclient.MediaSoupV3ApiAction.*;

/**
 * This class is thread-safe (since {@link Socket} is thread-safe). It can be used by multiple threads concurrently.
 */
public class MediaSoupV3SocketIoApiClient implements MediaSoupV3ApiClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	private final Socket socket;
	private final String serverSecret;

	public MediaSoupV3SocketIoApiClient(String serverUrl, String serverSecret) {
		this.socket = createSocket(serverUrl);
		this.serverSecret = serverSecret;
		socket.connect();
	}

	public CompletableFuture<JsonNode> getServerConfigs() {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(0, GET_SERVER_CONFIGS, token, null, new TypeReference<>() {
		});
	}

	public CompletableFuture<JsonNode> producersStats(StatsInput statsInput) {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(0, PRODUCERS_STATS, token, statsInput, new TypeReference<>() {
		});
	}

	public CompletableFuture<JsonNode> consumersStats(StatsInput statsInput) {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(0, CONSUMERS_STATS, token, statsInput, new TypeReference<>() {
		});
	}

	public CompletableFuture<JsonNode> transportStats(List<String> ids) {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(0, TRANSPORT_STATS, token, new StatsInput(ids), new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<Double> getWorkerLoad(int workerId) {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(workerId, WORKER_LOAD, token, null, new TypeReference<JsonNode>() {
		}).thenApply(treeNode -> OBJECT_MAPPER.convertValue(treeNode.get("currentLoad"), Double.class));
	}

	@Override
	public CompletableFuture<Integer> getNumberOfWorkers() {
		String token = MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365));
		return call(0, NUM_WORKERS, token, null, new TypeReference<JsonNode>() {
		}).thenApply(treeNode -> OBJECT_MAPPER.convertValue(treeNode.get("num"), Integer.class));
	}

	@Override
	public CompletableFuture<Void> startRecording(String streamUuid) {
		return startRecording(streamUuid, null);
	}

	@Override
	public CompletableFuture<Void> startRecording(String streamUuid, Set<MediaKind> kinds) {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, START_RECORDING, token, new StreamAndKinds(streamUuid, kinds), new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<Void> stopRecording(String streamUuid) {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, STOP_RECORDING, token, new StreamAndKinds(streamUuid, Set.of(MediaKind.AUDIO, MediaKind.VIDEO)), new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<Void> startFileStreaming(int workerId, StreamFileRequest streamFileRequest) {
		String token = MediaSoupV3TokenGenerator.generateStreamingJwtToken(streamFileRequest.getStreamUuid(), serverSecret, Duration.ofDays(365));
		return call(workerId, FILE_STREAMING, token, streamFileRequest, new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<Void> stopFileStreaming(int workerId, String streamUuid) {
		String token = MediaSoupV3TokenGenerator.generateStreamingJwtToken(streamUuid, serverSecret, Duration.ofDays(365));
		return call(workerId, STOP_FILE_STREAMING, token, new StreamData(streamUuid), new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<List<String>> getRecordedStreamUuids() {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, RECORDED_STREAMS, token, null, new TypeReference<JsonNode>() {
		}).thenApply(treeNode -> OBJECT_MAPPER.convertValue(treeNode.get("list"), new TypeReference<>() {
		}));
	}

	@Override
	public CompletableFuture<List<String>> getStreamRecordingsForUuid(String streamUuid) {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, STREAM_RECORDINGS, token, new StreamData(streamUuid), new TypeReference<JsonNode>() {
		}).thenApply(treeNode -> OBJECT_MAPPER.convertValue(treeNode.get("list"), new TypeReference<>() {
		}));
	}

	@Override
	public CompletableFuture<Void> deleteStreamRecordings(String streamUuid) {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, DELETE_STREAM_RECORDINGS, token, new StreamData(streamUuid), new TypeReference<>() {
		});
	}

	@Override
	public CompletableFuture<Void> deleteRecording(String recordingName) {
		String token = MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365));
		return call(0, DELETE_RECORDING, token, new FilePathInput(recordingName), new TypeReference<>() {
		});
	}

	public <R> CompletableFuture<R> call(int worker, MediaSoupV3ApiAction action, String token, Object parametersObject, TypeReference<R> responseType) {
		String paramsJson;
		String metadataJson;
		try {
			paramsJson = OBJECT_MAPPER.writeValueAsString(parametersObject);
			metadataJson = OBJECT_MAPPER.writeValueAsString(new RequestMetadata(worker, token));
		} catch (JsonProcessingException e) {
			return CompletableFuture.failedFuture(e);
		}

		CompletableFuture<R> future = new CompletableFuture<>();
		socket.emit(action.getName(), new Object[]{paramsJson, metadataJson}, responsePayload -> {
			LOGGER.info("Got response from mediasoup: " + responsePayload[0]);
			if (responsePayload[0] == null) {
				future.complete(null);
			} else {
				JsonNode jsonNode = parseJson(responsePayload[0].toString());
				if (jsonNode.get("errorId") != null) {
					future.completeExceptionally(new MediaSoupV3ApiClientException("Error while calling " + action.getName() + "! Code: " + jsonNode.get("errorId")));
				} else {
					try {
						future.complete(OBJECT_MAPPER.readValue(responsePayload[0].toString(), responseType));
					} catch (JsonProcessingException e) {
						future.completeExceptionally(e);
					}
				}
			}
		});
		return future;
	}

	public void close() {
		socket.close();
	}

	private Socket createSocket(String serverUrl)  {
		Socket socket;
		try {
			socket = IO.socket(serverUrl);
		} catch (URISyntaxException e) {
			throw new MediaSoupV3ApiClientException(e);
		}
		socket.on(Socket.EVENT_CONNECT, args13 -> {
			LOGGER.info("EVENT_CONNECT: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_CONNECTING, args13 -> {
			LOGGER.info("EVENT_CONNECTING: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_DISCONNECT, args13 -> {
			LOGGER.info("EVENT_DISCONNECT: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_ERROR, args13 -> {
			LOGGER.error("EVENT_ERROR: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_MESSAGE, args13 -> {
			LOGGER.info("EVENT_MESSAGE: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_CONNECT_ERROR, args13 -> {
			LOGGER.info("EVENT_CONNECT_ERROR: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_CONNECT_TIMEOUT, args13 -> {
			LOGGER.info("EVENT_CONNECT_TIMEOUT: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_RECONNECT, args13 -> {
			LOGGER.info("EVENT_RECONNECT: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_RECONNECT_ERROR, args13 -> {
			LOGGER.info("EVENT_RECONNECT_ERROR: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_RECONNECT_FAILED, args13 -> {
			LOGGER.info("EVENT_RECONNECT_FAILED: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_RECONNECT_ATTEMPT, args13 -> {
			LOGGER.info("EVENT_RECONNECT_ATTEMPT: {}", Arrays.toString(args13));
		}).on(Socket.EVENT_RECONNECTING, args13 -> {
			LOGGER.info("EVENT_RECONNECTING: {}", Arrays.toString(args13));
		});
		return socket;
	}

	private static JsonNode parseJson(Object o) {
		if (o == null) {
			return NullNode.getInstance();
		}
		try {
			return OBJECT_MAPPER.readTree(((String) o));
		} catch (JsonProcessingException e) {
			return NullNode.getInstance();
		}
	}

}