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
package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Set;

import static org.teamapps.ux.component.webrtc.apiclient.MediaKind.AUDIO;
import static org.teamapps.ux.component.webrtc.apiclient.MediaKind.VIDEO;

public class MediaSoupV3ApiClientTest {

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		MediaSoupV3SocketIoApiClient client = new MediaSoupV3SocketIoApiClient("http://127.0.0.1:8723", "asdf");

		client.startRecording("myStream", Set.of(AUDIO, VIDEO)).thenAccept(r -> System.out.println("Response: " + r));
		client.startRecording("myStream").thenAccept(r -> System.out.println("Response: " + r));
		client.stopRecording("myStream").thenAccept(r -> System.out.println("Response: " + r));
		client.getServerConfigs().thenAccept(r -> System.out.println("Response: " + r));
		client.getWorkerLoad(0).thenAccept(r -> System.out.println("Response: " + r));
		client.getNumberOfWorkers().thenAccept(r -> System.out.println("Response: " + r));
		client.getRecordedStreamUuids().thenAccept(r -> System.out.println("Response: " + r));
		client.getStreamRecordingsForUuid("myStream").thenAccept(r -> System.out.println("Response: " + r));
		client.startFileStreaming(1, new StreamFileRequestBuilder("myFileStream", Set.of(AUDIO, VIDEO), "http://localhost:8081/resources/asdf.mp4", false).build());
		client.stopFileStreaming(1, "myFileStream");

		Thread.sleep(1000);
		client.close();
		System.exit(0);
	}

	@Test
	public void testSerialization() throws Exception {
		Assert.assertEquals("{\"stream\":\"x\"}", new ObjectMapper().writeValueAsString(new StreamData("x")));
	}

}
