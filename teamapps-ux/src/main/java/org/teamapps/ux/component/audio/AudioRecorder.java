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
        return ui;
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
        if (event instanceof UiAudioRecorder.StateChangedEvent state) {
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
