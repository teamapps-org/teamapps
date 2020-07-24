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
		MediaSoupV3ApiClient client = new MediaSoupV3ApiClient("http://127.0.0.1:8723", "asdf");

		client.startRecording(0, "myStream", Set.of(AUDIO, VIDEO)).thenAccept(r -> System.out.println("Response: " + r));
		client.stopRecording(0, "myStream").thenAccept(r -> System.out.println("Response: " + r));
		client.getServerConfigs().thenAccept(r -> System.out.println("Response: " + r));
		client.workerLoad(0).thenAccept(r -> System.out.println("Response: " + r));
		client.numWorkers().thenAccept(r -> System.out.println("Response: " + r));
		client.getRecordedStreamUuids().thenAccept(r -> System.out.println("Response: " + r));
		client.getStreamRecordingsForUuid("myStream").thenAccept(r -> System.out.println("Response: " + r));
		client.startFileStreaming(1, new StreamFileRequestBuilder("myFileStream", Set.of(AUDIO, VIDEO), "http://localhost:8081/resources/Fathers.mp4", false).build());
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