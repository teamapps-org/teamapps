package org.teamapps.ux.component.audio;

import org.teamapps.dto.UiAudioRecorderSetup;
import java.util.Objects;

/** Optional built-in microphone setup panel. Supply localized captions from the owning application. */
public final class AudioRecorderSetup {
    private String microphoneCaption = "Microphone";
    private String defaultMicrophoneCaption = "System default";
    private String testCaption = "Test microphone";
    private String stopTestCaption = "Stop test";
    private String testingCaption = "Microphone test — no recording";
    private String settingsCaption = "Actual capture settings";
    private String clippingCaption = "Input is clipping";
    private String deviceDisconnectedCaption = "Microphone disconnected. Recording stopped.";
    private String configurationRejectedCaption = "Settings cannot be changed during recording or upload.";
    private String testEndedCaption = "Microphone test ended";
    private String inactivityCaption = "Recording stopped after prolonged silence.";
    private String downloadCaption = "Save audio locally";
    private String restoreCaption = "Restore local recording";
    private String discardDraftCaption = "Delete local draft";
    private String locallySavedCaption = "Recording saved in this browser";
    private String localSaveErrorCaption = "Local saving failed. Download the recording before leaving.";
    public AudioRecorderSetup setMicrophoneCaption(String value) { microphoneCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setDefaultMicrophoneCaption(String value) { defaultMicrophoneCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setTestCaption(String value) { testCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setStopTestCaption(String value) { stopTestCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setTestingCaption(String value) { testingCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setSettingsCaption(String value) { settingsCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setClippingCaption(String value) { clippingCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setDeviceDisconnectedCaption(String value) { deviceDisconnectedCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setConfigurationRejectedCaption(String value) { configurationRejectedCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setTestEndedCaption(String value) { testEndedCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setInactivityCaption(String value) { inactivityCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setDownloadCaption(String value) { downloadCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setRestoreCaption(String value) { restoreCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setDiscardDraftCaption(String value) { discardDraftCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setLocallySavedCaption(String value) { locallySavedCaption = Objects.requireNonNull(value); return this; }
    public AudioRecorderSetup setLocalSaveErrorCaption(String value) { localSaveErrorCaption = Objects.requireNonNull(value); return this; }
    UiAudioRecorderSetup toUi() {
        UiAudioRecorderSetup ui = new UiAudioRecorderSetup();
        ui.setMicrophoneCaption(microphoneCaption);
        ui.setDefaultMicrophoneCaption(defaultMicrophoneCaption);
        ui.setTestCaption(testCaption);
        ui.setStopTestCaption(stopTestCaption);
        ui.setTestingCaption(testingCaption);
        ui.setSettingsCaption(settingsCaption);
        ui.setClippingCaption(clippingCaption);
        ui.setDeviceDisconnectedCaption(deviceDisconnectedCaption);
        ui.setConfigurationRejectedCaption(configurationRejectedCaption);
        ui.setTestEndedCaption(testEndedCaption);
        ui.setInactivityCaption(inactivityCaption);
        ui.setDownloadCaption(downloadCaption);
        ui.setRestoreCaption(restoreCaption);
        ui.setDiscardDraftCaption(discardDraftCaption);
        ui.setLocallySavedCaption(locallySavedCaption);
        ui.setLocalSaveErrorCaption(localSaveErrorCaption);
        return ui;
    }
}
