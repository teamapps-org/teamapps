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

    @Test public void legacyConstructionKeepsOptionsAbsentAndDoesNotExposeEnhancedEvents() {
        assertNull(recorder.createUiComponent().getRecordingOptions());
        assertNull(recorder.createUiComponent().getSetup());
        List<String> received = new ArrayList<>();
        recorder.onSetupStateChanged.addListener(value -> { received.add(value); });
        recorder.handleUiEvent(new UiAudioRecorder.SetupStateChangedEvent(recorder.getId(), "testing"));
        assertTrue(received.isEmpty());
        assertThrows(IllegalStateException.class, recorder::startMicrophoneTest);
    }

    @Test public void optionsAndLocalizedSetupAreSnapshottedWithoutChangingOriginalConstructors() {
        AudioRecordingOptions options = new AudioRecordingOptions().setSampleRate(48000).setAudioBitsPerSecond(192000)
                .setNoiseSuppression(AudioRecordingOptions.Processing.DISABLED).setMimeTypes(List.of("audio/webm;codecs=opus"));
        try (AudioRecorder enhanced = new AudioRecorder(10000, 60000, "Start", "Recording", "Ready", "Error", "Finish")
                .setRecordingOptions(options).setSetup(new AudioRecorderSetup().setMicrophoneCaption("Mikrofon"))) {
            options.setSampleRate(44100);
            assertEquals(48000, enhanced.createUiComponent().getRecordingOptions().getSampleRate());
            assertEquals(192000, enhanced.createUiComponent().getRecordingOptions().getAudioBitsPerSecond());
            assertEquals(0, enhanced.createUiComponent().getRecordingOptions().getNoiseSuppression());
            assertEquals("Mikrofon", enhanced.createUiComponent().getSetup().getMicrophoneCaption());
            enhanced.render();
            assertThrows(IllegalStateException.class, () -> enhanced.setSetup(new AudioRecorderSetup()));
            assertThrows(IllegalStateException.class, () -> enhanced.setRecordingOptions(options));
            enhanced.configureRecording(new AudioRecordingOptions().setSampleRate(44100));
            assertTrue(commands.get(commands.size() - 1) instanceof UiAudioRecorder.ConfigureRecordingCommand);
        }
    }

    @Test public void enhancedEventsRemainSeparateFromRecordingAndUploadLifecycle() {
        try (AudioRecorder enhanced = new AudioRecorder(10000, 60000, "Start", "Recording", "Ready", "Error")
                .setSetup(new AudioRecorderSetup())) {
            List<String> legacy = new ArrayList<>(), setup = new ArrayList<>(), warnings = new ArrayList<>();
            List<AudioRecorder.CaptureSettings> settings = new ArrayList<>();
            List<List<AudioRecorder.InputDevice>> devices = new ArrayList<>();
            enhanced.onStateChanged.addListener(value -> { legacy.add(value); });
            enhanced.onSetupStateChanged.addListener(value -> { setup.add(value); });
            enhanced.onWarning.addListener(value -> { warnings.add(value); });
            enhanced.onCaptureSettingsChanged.addListener(value -> { settings.add(value); });
            enhanced.onDevicesChanged.addListener(value -> { devices.add(value); });
            enhanced.handleUiEvent(new UiAudioRecorder.SetupStateChangedEvent(enhanced.getId(), "testing"));
            enhanced.handleUiEvent(new UiAudioRecorder.WarningEvent(enhanced.getId(), "configurationRejected"));
            enhanced.handleUiEvent(new UiAudioRecorder.DevicesChangedEvent(enhanced.getId(), List.of(new UiAudioInputDevice().setDeviceId("usb").setLabel("USB"))));
            enhanced.handleUiEvent(new UiAudioRecorder.CaptureSettingsChangedEvent(enhanced.getId(), new UiAudioCaptureSettings().setSampleRate(48000).setChannelCount(1)));
            assertTrue(legacy.isEmpty());
            assertEquals(List.of("testing"), setup);
            assertEquals(List.of("configurationRejected"), warnings);
            assertEquals("usb", devices.get(0).get(0).deviceId());
            assertEquals(48000, settings.get(0).sampleRate());
            enhanced.handleUiEvent(new UiAudioRecorder.WarningEvent(enhanced.getId(), "invented"));
            enhanced.handleUiEvent(new UiAudioRecorder.SetupStateChangedEvent(enhanced.getId(), null));
            assertEquals(1, warnings.size()); assertEquals(1, setup.size());
        }
    }

    @Test public void invalidOptionsFailEarlyAndDraftIdentityCannotChange() {
        assertThrows(IllegalArgumentException.class, () -> new AudioRecordingOptions().setSampleRate(-1));
        assertThrows(IllegalArgumentException.class, () -> new AudioRecordingOptions().setAudioBitsPerSecond(0));
        assertThrows(IllegalArgumentException.class, () -> new AudioRecordingOptions().setClippingThreshold(Float.NaN));
        assertThrows(IllegalArgumentException.class, () -> new AudioRecordingOptions().setMimeTypes(List.of("video/mp4")));
        assertThrows(IllegalArgumentException.class, () -> new AudioRecordingOptions().setLocalDraftKey(" "));
        try (AudioRecorder enhanced = new AudioRecorder(10000, 60000, "Start", "Recording", "Ready", "Error")
                .setRecordingOptions(new AudioRecordingOptions().setLocalDraftKey("user/report"))) {
            enhanced.render();
            assertThrows(IllegalArgumentException.class, () -> enhanced.configureRecording(new AudioRecordingOptions().setLocalDraftKey("other/report")));
            enhanced.acknowledgeRecording();
            assertTrue(commands.get(commands.size() - 1) instanceof UiAudioRecorder.AcknowledgeRecordingCommand);
        }
    }
}
