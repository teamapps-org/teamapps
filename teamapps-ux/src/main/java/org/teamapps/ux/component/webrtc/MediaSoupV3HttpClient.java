/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

package org.teamapps.ux.component.webrtc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MediaSoupV3HttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static CompletableFuture<Integer> getNumberOfWorkers(String serverUrl, String serverSecret) {
		String token = MediaSoupV3WebRtcClient.generatePublicRestApiToken(serverSecret, Duration.ofMinutes(10));
		String workerUrl = serverUrl + "/0";
		LOGGER.info("Requesting number of workers from {} with token {}", workerUrl, token);
		return post(workerUrl, "numWorkers", token, "{}")
				.thenApply(json -> {
					try {
						JsonNode rootNode = new ObjectMapper().readTree(json);
						if (!rootNode.isObject() || rootNode.get("num") == null || !rootNode.get("num").isNumber()) {
							LOGGER.error("Could not retrieve number of workers for server {} using token {}. Wrong JSON format!", serverUrl, token);
							throw new RuntimeException("Root node must be of form {num: <number>} but was " + json);
						}
						return rootNode.get("num").asInt();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	public static CompletableFuture<Void> setRecordingEnabled(String workerUrl, String serverSecret, String uid, boolean enabled) {
		String jwtToken = MediaSoupV3WebRtcClient.generateRecordingJwtToken(serverSecret, Duration.ofMinutes(10));
		if (enabled) {
			LOGGER.info("Starting recording to {} for {} on server {} with token {}", enabled, uid, workerUrl, jwtToken);
			return post(workerUrl, "startRecording", jwtToken, "{\"stream\": \"" + uid + "\", \"kinds\": [\"audio\", \"video\"]}")
					.thenApply(s -> null);
		} else {
			LOGGER.info("Stopping recording to {} for {} on server {} with token {}", enabled, uid, workerUrl, jwtToken);
			return post(workerUrl, "stopRecording", jwtToken, "{\"stream\": \"" + uid + "\", \"kinds\": [\"audio\", \"video\"]}")
					.thenApply(s -> null);
		}
	}

	public static CompletableFuture<List<String>> listRecordings(String workerUrl, String serverSecret, String uid) {
		String jwtToken = MediaSoupV3WebRtcClient.generateRecordingJwtToken(serverSecret, Duration.ofMinutes(10));
		LOGGER.info("Listing videos for {} on server {} with token {}", uid, workerUrl, jwtToken);
		return post(workerUrl, "streamRecordings", jwtToken, "{\"stream\": \"" + uid + "\"}")
				.thenApply(json -> {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(json);
						if (!rootNode.isObject() || rootNode.get("list") == null || !rootNode.get("list").isArray()) {
							throw new RuntimeException("Root node must be of form {num: <number>}");
						}
						return objectMapper.readerFor(new TypeReference<List<String>>() {
						}).readValue(rootNode.get("list"));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	public static CompletableFuture<Void> startStreamingFile(String workerUrl, String serverSecret, String videoPath, String uid) {
		String jwtToken = MediaSoupV3WebRtcClient.generateStreamingJwtToken(serverSecret, Duration.ofMinutes(10));
		String json = "{\"stream\":\"" + uid + "\",\"relativePath\":true,\"filePath\":\""+videoPath+"\",\"additionalOutputOptions\":[\"-b:v\",\"1M\"],"
				+ "\"additionalInputOptions\":[]}";
		LOGGER.info("Starting streamed video {} on server {} using token {}", uid, workerUrl, jwtToken);
		return post(workerUrl, "fileStreaming", jwtToken, json)
				.thenApply(s -> null);
	}

	public static CompletableFuture<Void> startStreamingUrl(String workerUrl, String serverSecret, String videoUrl, String uid, boolean isVideo) {
		String jwtToken = MediaSoupV3WebRtcClient.generateStreamingJwtToken(serverSecret, Duration.ofMinutes(10));
		String json = "{\"stream\":\"" + uid + "\",\"kinds\": [\"audio\"" + (isVideo ? ", \"video\"" : "") + "],\"relativePath\":false,\"filePath\":\""+videoUrl+"\","
				+ "\"additionalInputOptions\":[]}";
		LOGGER.info("Starting streamed video {} on server {} using token {}", uid, workerUrl, jwtToken);
		return post(workerUrl, "fileStreaming", jwtToken, json)
				.thenApply(s -> null);
	}

	public static CompletableFuture<Void> stopStreaming(String workerUrl, String serverSecret, String uid) {
		String jwtToken = MediaSoupV3WebRtcClient.generateStreamingJwtToken(serverSecret, Duration.ofMinutes(10));
		String json = "{\"stream\":\"" + uid + "\"}";
		LOGGER.info("Stopping streamed video {} on server {} using token {}", uid, workerUrl, jwtToken);
		return post(workerUrl, "stopFileStreaming", jwtToken, json)
				.thenApply(s -> null);
	}

	private static CompletableFuture<String> post(String workerUrl, final String resource, String token, String json) {
		return CompletableFuture.supplyAsync(() -> {
			URI uri = URI.create(workerUrl + "/mediasoup/" + resource);
			HttpUriRequest request = RequestBuilder.post(uri)
					.setConfig(RequestConfig.custom().setSocketTimeout(10_000).build())
					.setEntity(new StringEntity(json, StandardCharsets.UTF_8))
					.addHeader("Authorization", "Bearer " + token)
					.addHeader("Content-Type", "application/json")
					.build();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			try {
				try (CloseableHttpResponse response = httpClient.execute(request)) {
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode != 200) {
						throw new RuntimeException("Request to " + uri + " failed with status code " + statusCode);
					} else {
						LOGGER.info("POST to {} successful (200)", uri);
						return EntityUtils.toString(response.getEntity());
					}
				}
			} catch (IOException e) {
				LOGGER.error("Could not POST {}", uri);
				throw new IllegalStateException("Request to " + uri + " failed", e);
			} finally {
				try {
					httpClient.close();
				} catch (IOException e) {
					LOGGER.error("Exception while closing httpClient", e);
				}
			}
		});
	}

	public static File downloadVideo(URL url, File downloadDirectory) throws IOException {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			LOGGER.info("Downloading video: {} to {}", url, downloadDirectory.getAbsolutePath());
			File resultFile = new File(downloadDirectory, FilenameUtils.getName(url.getPath()).replace(':', '-'));
			try (BufferedInputStream is = new BufferedInputStream(con.getInputStream());
			     BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(resultFile, false))) {
				IOUtils.copy(is, os);
			}
			return resultFile;
		} catch (IOException e) {
			LOGGER.error("Exception while downloading video: " + url, e);
			throw e;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	public static File downloadVideo(String url) throws IOException {
		return downloadVideo(new URL(url), Files.createTempDirectory("download").toFile());
	}

}
