package org.teamapps.ux.component.audio;

import org.junit.*;
import org.teamapps.dto.*;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.CurrentSessionContextTestUtil;
import org.teamapps.ux.session.SessionContext;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AudioRecorderTest {
    private final List<UiCommand> commands = new ArrayList<>();
    private final List<AudioRecorder.Recording> recordings = new ArrayList<>();
    private AudioRecorder recorder;
    @Before public void setUp() {
        SessionContext context = spy(UxTestUtil.createDummySessionContext());
        doAnswer(call -> { commands.add(call.getArgument(0)); return null; }).when(context).queueCommand(any(UiCommand.class));
        CurrentSessionContextTestUtil.set(context);
        recorder = new AudioRecorder(10, 1000, "Start", "Recording", "Ready", "Error");
        recorder.onRecordingUploaded.addListener(recording -> { recordings.add(recording); });
        recorder.render();
    }
    @After public void tearDown() { recorder.close(); CurrentSessionContextTestUtil.unset(); }
    private UiAudioRecorder.RequestChunkCommand lastRequest() {
        return commands.stream().filter(UiAudioRecorder.RequestChunkCommand.class::isInstance)
                .map(UiAudioRecorder.RequestChunkCommand.class::cast).reduce((a, b) -> b).orElseThrow();
    }
    private void chunk(String request, int sequence, String extension, boolean last) {
        recorder.handleUiEvent(new UiAudioRecorder.ChunkEvent(recorder.getId(), request, sequence, extension, "AQID", last));
    }
    @Test public void combinedControlIsOptInAndMapsLocalizedCaption() {
        assertNull(recorder.createUiComponent().getFinishCaption());
        try (AudioRecorder combined = new AudioRecorder(10, 1000, "Start", "Recording", "Ready", "Error", "Finish")) {
            assertEquals("Finish", combined.createUiComponent().getFinishCaption());
        }
    }
    @Test public void failedUploadCanRetryAndLateChunksCannotCompleteTheNewUpload() {
        recorder.upload(); String old = lastRequest().getRequestId();
        chunk(old, 0, "webm", false);
        recorder.handleUiEvent(new UiAudioRecorder.StateChangedEvent(recorder.getId(), "error"));
        recorder.upload(); String current = lastRequest().getRequestId();
        assertNotEquals(old, current); assertEquals(0, lastRequest().getSequence());
        chunk(old, 1, "webm", true); assertTrue(recordings.isEmpty());
        chunk(current, 0, "webm", true); assertEquals(1, recordings.size());
        assertEquals(3, recordings.get(0).file().length());
        recorder.close(); assertFalse(recordings.get(0).file().exists());
    }
    @Test public void extensionChangesAndOversizeUploadsAreRejected() {
        recorder.upload(); String first = lastRequest().getRequestId();
        chunk(first, 0, "webm", false); chunk(first, 1, "mp4", true);
        assertTrue(recordings.isEmpty());
        recorder.upload(); String second = lastRequest().getRequestId();
        for (int i = 0; i < 4; i++) chunk(second, i, "webm", i == 3);
        assertTrue(recordings.isEmpty());
        recorder.upload(); assertNotEquals(second, lastRequest().getRequestId());
    }
}
