package org.teamapps.ux.component.audio;

import org.teamapps.dto.UiAudioRecordingOptions;
import java.util.*;

/** Optional capture profile; existing recorders do not use it unless explicitly configured.
 * Numeric capture settings are preferences unless exactConstraints is enabled. Bitrate is always
 * an encoder target. Browser/device support and actual settings must be inspected after opening.
 * Local drafts are opt-in, stored on this browser's origin, and contain unencrypted audio.
 * Use a non-sensitive opaque key scoped to the current user and report; clear it after saving. */
public final class AudioRecordingOptions {
    public enum Processing {
        BROWSER_DEFAULT(-1), DISABLED(0), ENABLED(1);
        private final int value;
        Processing(int value) { this.value = value; }
    }
    private int sampleRate = 0;
    private int sampleSize = 0;
    private int channelCount = 1;
    private int audioBitsPerSecond = 64000;
    private Processing echoCancellation = Processing.ENABLED;
    private Processing noiseSuppression = Processing.BROWSER_DEFAULT;
    private Processing autoGainControl = Processing.BROWSER_DEFAULT;
    private String deviceId = null;
    private boolean exactConstraints = false;
    private boolean stopWhenHidden = true;
    private int microphoneTestMaxDurationMillis = 300000;
    private int inactivityTimeoutMillis = 300000;
    private double silenceThreshold = 0.005;
    private double clippingThreshold = 0.98;
    private String localDraftKey = null;
    private List<String> mimeTypes;

    public int getSampleRate() { return sampleRate; }
    public AudioRecordingOptions setSampleRate(int value) { sampleRate = range(value, 0, 384000, "sampleRate"); return this; }
    public int getSampleSize() { return sampleSize; }
    public AudioRecordingOptions setSampleSize(int value) { sampleSize = range(value, 0, 64, "sampleSize"); return this; }
    public int getChannelCount() { return channelCount; }
    public AudioRecordingOptions setChannelCount(int value) { channelCount = range(value, 1, 8, "channelCount"); return this; }
    public int getAudioBitsPerSecond() { return audioBitsPerSecond; }
    public AudioRecordingOptions setAudioBitsPerSecond(int value) { audioBitsPerSecond = range(value, 6000, 2000000, "audioBitsPerSecond"); return this; }
    public Processing getEchoCancellation() { return echoCancellation; }
    public AudioRecordingOptions setEchoCancellation(Processing value) { echoCancellation = Objects.requireNonNull(value); return this; }
    public Processing getNoiseSuppression() { return noiseSuppression; }
    public AudioRecordingOptions setNoiseSuppression(Processing value) { noiseSuppression = Objects.requireNonNull(value); return this; }
    public Processing getAutoGainControl() { return autoGainControl; }
    public AudioRecordingOptions setAutoGainControl(Processing value) { autoGainControl = Objects.requireNonNull(value); return this; }
    public String getDeviceId() { return deviceId; }
    public AudioRecordingOptions setDeviceId(String value) { deviceId = value; return this; }
    public boolean getExactConstraints() { return exactConstraints; }
    public AudioRecordingOptions setExactConstraints(boolean value) { exactConstraints = value; return this; }
    public boolean getStopWhenHidden() { return stopWhenHidden; }
    public AudioRecordingOptions setStopWhenHidden(boolean value) { stopWhenHidden = value; return this; }
    public int getMicrophoneTestMaxDurationMillis() { return microphoneTestMaxDurationMillis; }
    public AudioRecordingOptions setMicrophoneTestMaxDurationMillis(int value) { microphoneTestMaxDurationMillis = range(value, 1000, 300000, "microphoneTestMaxDurationMillis"); return this; }
    public int getInactivityTimeoutMillis() { return inactivityTimeoutMillis; }
    /** Enhanced mode only: finish after this much silence; zero explicitly disables the silence limit. */
    public AudioRecordingOptions setInactivityTimeoutMillis(int value) { inactivityTimeoutMillis = range(value, 0, 1800000, "inactivityTimeoutMillis"); return this; }
    public double getSilenceThreshold() { return silenceThreshold; }
    public AudioRecordingOptions setSilenceThreshold(double value) {
        if (!Double.isFinite(value) || value < 0 || value > 1) throw new IllegalArgumentException("silenceThreshold");
        silenceThreshold = value; return this;
    }
    public double getClippingThreshold() { return clippingThreshold; }
    public AudioRecordingOptions setClippingThreshold(double value) { clippingThreshold = threshold(value); return this; }
    public String getLocalDraftKey() { return localDraftKey; }
    public AudioRecordingOptions setLocalDraftKey(String value) { localDraftKey = draftKey(value); return this; }

    public List<String> getMimeTypes() { return mimeTypes; }
    /** Null selects the recorder's original browser fallback order; a list sets an explicit allowlist. */
    public AudioRecordingOptions setMimeTypes(List<String> value) {
        if (value != null && (value.isEmpty() || value.size() > 16 || value.stream().anyMatch(v -> v == null || !v.startsWith("audio/") || v.length() > 128)))
            throw new IllegalArgumentException("mimeTypes");
        mimeTypes = value == null ? null : List.copyOf(value);
        return this;
    }
    UiAudioRecordingOptions toUi() {
        UiAudioRecordingOptions ui = new UiAudioRecordingOptions();
        ui.setSampleRate(sampleRate);
        ui.setSampleSize(sampleSize);
        ui.setChannelCount(channelCount);
        ui.setAudioBitsPerSecond(audioBitsPerSecond);
        ui.setEchoCancellation(echoCancellation.value);
        ui.setNoiseSuppression(noiseSuppression.value);
        ui.setAutoGainControl(autoGainControl.value);
        ui.setDeviceId(deviceId);
        ui.setExactConstraints(exactConstraints);
        ui.setStopWhenHidden(stopWhenHidden);
        ui.setMicrophoneTestMaxDurationMillis(microphoneTestMaxDurationMillis);
        ui.setInactivityTimeoutMillis(inactivityTimeoutMillis);
        ui.setSilenceThreshold(silenceThreshold);
        ui.setClippingThreshold(clippingThreshold);
        ui.setLocalDraftKey(localDraftKey);
        ui.setMimeTypes(mimeTypes == null ? null : new ArrayList<>(mimeTypes));
        return ui;
    }
    private static int range(int value, int min, int max, String name) {
        if (value < min || value > max) throw new IllegalArgumentException(name);
        return value;
    }
    private static double threshold(double value) {
        if (!Double.isFinite(value) || value < 0.5 || value > 1) throw new IllegalArgumentException("clippingThreshold");
        return value;
    }
    private static String draftKey(String value) {
        if (value != null && (value.isBlank() || value.length() > 256)) throw new IllegalArgumentException("localDraftKey");
        return value;
    }
}
