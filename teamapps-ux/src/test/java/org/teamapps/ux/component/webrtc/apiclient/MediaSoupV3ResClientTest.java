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
package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.teamapps.ux.component.webrtc.apiclient.MediaKind.AUDIO;
import static org.teamapps.ux.component.webrtc.apiclient.MediaKind.VIDEO;

public class MediaSoupV3ResClientTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		MediaSoupV3RestClient client = new MediaSoupV3RestClient("http://127.0.0.1:8723", "asdf");

		List<CompletableFuture<?>> futures = new ArrayList<>();

		futures.add(client.startRecording(0, "myStream", Set.of(AUDIO, VIDEO)).thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.startRecording(0, "myStream").thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.stopRecording(0, "myStream").thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.getWorkerLoad(0).thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.getNumberOfWorkers().thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.getRecordedStreamUuids().thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.getStreamRecordingsForUuid("myStream").thenAccept(r -> System.out.println("Response: " + r)));
		futures.add(client.startFileStreaming(1, new StreamFileRequestBuilder("myFileStream", Set.of(AUDIO, VIDEO), "http://localhost:8081/resources/asdf.mp4", false).build()));
		futures.add(client.stopFileStreaming(1, "myFileStream"));

		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
				.get(10, TimeUnit.SECONDS);
		System.exit(0);
	}

	@Test
	public void testSerialization() throws Exception {
		Assert.assertEquals("{\"stream\":\"x\"}", new ObjectMapper().writeValueAsString(new StreamData("x")));
	}

}
