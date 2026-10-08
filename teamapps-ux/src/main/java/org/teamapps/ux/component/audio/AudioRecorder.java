package org.teamapps.ux.component.audio;

import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.event.Disposable;
import org.teamapps.ux.component.AbstractComponent;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/** Session-bound recording. Bounded chunks travel over the authenticated component channel.
 * No globally reusable upload UUID is exposed. The owner must close the recorder on dismissal. */
public final class AudioRecorder extends AbstractComponent implements AutoCloseable {
    public record Recording(File file, String extension) { }
    public final Event<String> onStateChanged = new Event<>();
    public final Event<Recording> onRecordingUploaded = new Event<>();
    public record InputDevice(String deviceId, String label) { }
    /** Browser-reported diagnostics, not trusted media validation. Zero means unavailable. */
    public record CaptureSettings(String deviceId, String label, int sampleRate, int sampleSize, int channelCount,
                                  int audioBitsPerSecond, int echoCancellation, int noiseSuppression, int autoGainControl, String mimeType) { }
    public final Event<List<InputDevice>> onDevicesChanged = new Event<>();
    public final Event<CaptureSettings> onCaptureSettingsChanged = new Event<>();
    public final Event<String> onSetupStateChanged = new Event<>();
    public final Event<String> onWarning = new Event<>();
    private UiAudioRecordingOptions recordingOptions;
    private UiAudioRecorderSetup setup;
    private final int maxBytes;
    private final int maxDurationMillis;
    private final String startCaption, recordingCaption, readyCaption, errorCaption, finishCaption;
    private final Disposable sessionCleanup;
    private String requestId;
    private int sequence;
    private File file;
    private OutputStream output;
    private long size;
    private String extension;
    private boolean closed;

    public AudioRecorder(int maxBytes, int maxDurationMillis, String startCaption, String recordingCaption, String readyCaption, String errorCaption) {
        this(maxBytes, maxDurationMillis, startCaption, recordingCaption, readyCaption, errorCaption, null);
    }

    /** An optional finish caption enables one combined start/finish button. */
    public AudioRecorder(int maxBytes, int maxDurationMillis, String startCaption, String recordingCaption, String readyCaption, String errorCaption, String finishCaption) {
        if (maxBytes <= 0 || maxDurationMillis <= 0) throw new IllegalArgumentException();
        this.maxBytes = maxBytes;
        this.maxDurationMillis = maxDurationMillis;
        this.startCaption = startCaption;
        this.recordingCaption = recordingCaption;
        this.readyCaption = readyCaption;
        this.errorCaption = errorCaption;
        this.finishCaption = finishCaption;
        sessionCleanup = getSessionContext().onDestroyed.addListener(this::close, false);
    }

    @Override public UiAudioRecorder createUiComponent() {
        UiAudioRecorder ui = new UiAudioRecorder();
        mapAbstractUiComponentProperties(ui);
        ui.setMaxBytes(maxBytes).setMaxDurationMillis(maxDurationMillis).setStartCaption(startCaption)
                .setFinishCaption(finishCaption).setRecordingCaption(recordingCaption).setReadyCaption(readyCaption).setErrorCaption(errorCaption);
        ui.setRecordingOptions(recordingOptions).setSetup(setup);
        return ui;
    }

    /** Opt in before first rendering. Captures a snapshot; later mutations of options have no effect. */
    public AudioRecorder setRecordingOptions(AudioRecordingOptions options) {
        if (isRendered()) throw new IllegalStateException("Use configureRecording on an already enhanced recorder");
        recordingOptions = Objects.requireNonNull(options).toUi();
        return this;
    }

    /** Opt-in local controls for device selection, pre-recording metering, clipping and download. */
    public AudioRecorder setSetup(AudioRecorderSetup setup) {
        if (isRendered()) throw new IllegalStateException("Configure setup before rendering");
        this.setup = Objects.requireNonNull(setup).toUi();
        return this;
    }

    /** Apply a new profile while idle. Rejected while recording/uploading, reported via onWarning.
     * A localDraftKey belongs to the component lifetime and cannot be changed here. */
    public void configureRecording(AudioRecordingOptions options) {
        requireEnhanced();
        UiAudioRecordingOptions next = Objects.requireNonNull(options).toUi();
        String draftKey = recordingOptions == null ? null : recordingOptions.getLocalDraftKey();
        if (!Objects.equals(draftKey, next.getLocalDraftKey())) throw new IllegalArgumentException("localDraftKey is immutable after rendering");
        if (!isRendered()) recordingOptions = next;
        else queueCommandIfRendered(() -> new UiAudioRecorder.ConfigureRecordingCommand(getId(), next));
    }
    public void refreshDevices() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.RefreshDevicesCommand(getId())); }
    public void selectDevice(String deviceId) { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.SelectDeviceCommand(getId(), deviceId)); }
    /** Call from an explicit user action. The built-in test button also works without a server roundtrip. */
    public void startMicrophoneTest() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.StartMicrophoneTestCommand(getId())); }
    public void stopMicrophoneTest() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.StopMicrophoneTestCommand(getId())); }
    /** Call ONLY after the application has persisted the uploaded recording. Clears its local draft. */
    public void acknowledgeRecording() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.AcknowledgeRecordingCommand(getId())); }
    public void restoreLocalDraft() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.RestoreLocalDraftCommand(getId())); }
    public void deleteLocalDraft() { requireEnhanced(); queueCommandIfRendered(() -> new UiAudioRecorder.DeleteLocalDraftCommand(getId())); }
    private void requireEnhanced() {
        if (closed || (recordingOptions == null && setup == null)) throw new IllegalStateException("Enhanced recorder is not enabled or is closed");
    }

    public void finish() { queueCommandIfRendered(() -> new UiAudioRecorder.FinishCommand(getId())); }

    public void upload() {
        if (closed || requestId != null) return;
        deleteUpload();
        requestId = UUID.randomUUID().toString();
        sequence = 0;
        nextChunk();
    }

    private void nextChunk() {
        queueCommandIfRendered(() -> new UiAudioRecorder.RequestChunkCommand(getId(), requestId, sequence));
    }

    @Override public void handleUiEvent(UiEvent event) {
        if (closed) return;
        if (event instanceof UiAudioRecorder.DevicesChangedEvent changed && (recordingOptions != null || setup != null)) {
            if (changed.getDevices() != null) onDevicesChanged.fire(changed.getDevices().stream().filter(Objects::nonNull).limit(128)
                    .map(d -> new InputDevice(d.getDeviceId(), d.getLabel())).toList());
        } else if (event instanceof UiAudioRecorder.CaptureSettingsChangedEvent changed && (recordingOptions != null || setup != null)) {
            UiAudioCaptureSettings s = changed.getSettings();
            if (s != null) onCaptureSettingsChanged.fire(new CaptureSettings(s.getDeviceId(), s.getLabel(), s.getSampleRate(), s.getSampleSize(),
                    s.getChannelCount(), s.getAudioBitsPerSecond(), s.getEchoCancellation(), s.getNoiseSuppression(), s.getAutoGainControl(), s.getMimeType()));
        } else if (event instanceof UiAudioRecorder.SetupStateChangedEvent changed && (recordingOptions != null || setup != null)) {
            if (Set.of("idle", "opening", "testing", "configured", "draftAvailable", "localSaved", "restored", "acknowledged").contains(Objects.toString(changed.getState(), ""))) onSetupStateChanged.fire(changed.getState());
        } else if (event instanceof UiAudioRecorder.WarningEvent warning && (recordingOptions != null || setup != null)) {
            if (Set.of("configurationRejected", "deviceDisconnected", "constraintsUnsupported", "testEnded", "inactivity", "localSaveError", "deviceListError", "meterUnavailable").contains(Objects.toString(warning.getCode(), ""))) onWarning.fire(warning.getCode());
        } else if (event instanceof UiAudioRecorder.StateChangedEvent state) {
            if (state.getState() != null && Set.of("recording", "ready", "error", "limit", "unsupported").contains(state.getState())) {
                if (Set.of("error", "limit", "unsupported").contains(state.getState())) { requestId = null; deleteUpload(); }
                onStateChanged.fire(state.getState());
            }
        } else if (event instanceof UiAudioRecorder.ChunkEvent chunk) {
            if (requestId == null || !requestId.equals(chunk.getRequestId())) return;
            try {
                if (chunk.getSequence() != sequence || chunk.getData() == null || chunk.getData().length() > 350000
                        || chunk.getExtension() == null || !Set.of("webm", "mp4", "ogg", "wav").contains(chunk.getExtension())
                        || (extension != null && !extension.equals(chunk.getExtension()))) throw new IOException("Invalid recording chunk");
                byte[] bytes = Base64.getDecoder().decode(chunk.getData());
                if (bytes.length == 0 || size + bytes.length > maxBytes) throw new IOException("Recording limit");
                if (output == null) {
                    extension = chunk.getExtension();
                    file = Files.createTempFile("teamapps-recording-", "." + chunk.getExtension()).toFile();
                    output = new BufferedOutputStream(new FileOutputStream(file));
                }
                output.write(bytes);
                size += bytes.length;
                if (chunk.getLast()) {
                    output.close();
                    output = null;
                    requestId = null;
                    onRecordingUploaded.fire(new Recording(file, chunk.getExtension()));
                } else {
                    sequence++;
                    nextChunk();
                }
            } catch (IOException | IllegalArgumentException failure) {
                requestId = null;
                deleteUpload();
                onStateChanged.fire("error");
            }
        }
    }

    private void deleteUpload() {
        try { if (output != null) output.close(); } catch (IOException ignored) { }
        output = null;
        if (file != null) { try { Files.deleteIfExists(file.toPath()); } catch (IOException ignored) { } }
        file = null;
        size = 0;
        extension = null;
    }

    @Override public void close() {
        if (closed) return;
        closed = true;
        requestId = null;
        deleteUpload();
        sessionCleanup.dispose();
        // Session destruction already destroys the client; no commands outside its context.
        if (org.teamapps.ux.session.SessionContext.currentOrNull() == getSessionContext())
            queueCommandIfRendered(() -> new UiAudioRecorder.DiscardCommand(getId()));
    }
}
