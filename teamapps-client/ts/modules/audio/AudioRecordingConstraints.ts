import {UiAudioRecordingOptionsConfig} from "../../generated/UiAudioRecordingOptionsConfig";

export function recordingOptions(options: UiAudioRecordingOptionsConfig = {} as any): UiAudioRecordingOptionsConfig {
    return {sampleRate: 0, sampleSize: 0, channelCount: 1, audioBitsPerSecond: 64000,
        echoCancellation: 1, noiseSuppression: -1, autoGainControl: -1, exactConstraints: false,
        stopWhenHidden: true, microphoneTestMaxDurationMillis: 300000, inactivityTimeoutMillis: 300000,
        silenceThreshold: .005, clippingThreshold: .98, ...options} as any;
}

/** Device choice is exact: never silently fall back to a different microphone. */
export function audioConstraints(options: UiAudioRecordingOptionsConfig, supported: MediaTrackSupportedConstraints & {channelCount?: boolean}): MediaTrackConstraints {
    const result: {[name: string]: {exact: number | boolean} | {ideal: number | boolean}} = {};
    const apply = (name: "sampleRate" | "sampleSize" | "channelCount" | "echoCancellation" | "noiseSuppression" | "autoGainControl", value: number | boolean) => {
        if (!supported[name]) {
            if (options.exactConstraints) throw new Error("constraintsUnsupported");
            return;
        }
        result[name] = options.exactConstraints ? {exact: value} : {ideal: value};
    };
    for (const name of ["sampleRate", "sampleSize", "channelCount"] as const) if (options[name] > 0) apply(name, options[name]);
    for (const name of ["echoCancellation", "noiseSuppression", "autoGainControl"] as const) {
        if (options[name] >= 0) apply(name, options[name] === 1);
    }
    if (options.deviceId) {
        if (!supported.deviceId) throw new Error("constraintsUnsupported");
        (result as MediaTrackConstraints).deviceId = {exact: options.deviceId};
    }
    return result;
}
