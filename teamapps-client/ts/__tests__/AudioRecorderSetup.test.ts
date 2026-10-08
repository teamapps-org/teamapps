/** @jest-environment jsdom */
import {UiAudioRecorder, RECORDING_MIME_TYPES} from "../modules/UiAudioRecorder";
import {audioConstraints, recordingOptions} from "../modules/audio/AudioRecordingConstraints";
import {AudioRecordingDraftStore} from "../modules/audio/AudioRecordingDraftStore";
(global as any).$ = require('jquery');

const labels = {microphoneCaption: "Microphone", defaultMicrophoneCaption: "Default", testCaption: "Test", stopTestCaption: "Stop test", testingCaption: "Testing", settingsCaption: "Settings", clippingCaption: "Clipping", deviceDisconnectedCaption: "Disconnected", configurationRejectedCaption: "Locked", testEndedCaption: "Test ended", downloadCaption: "Download", restoreCaption: "Restore", discardDraftCaption: "Delete draft", locallySavedCaption: "Saved locally", localSaveErrorCaption: "Save failed"};
const flush = async () => { for (let i = 0; i < 8; i++) await Promise.resolve(); };
let instances: UiAudioRecorder[], streams: any[], recorders: any[], captures: jest.Mock, devices: any;
let level = 0, now = 0;
function stream(id = 'mic1') {
    const t = new EventTarget() as any;
    t.stop = jest.fn(); t.label = id === 'mic2' ? 'USB microphone' : 'Built-in microphone'; t.readyState = 'live';
    t.getSettings = () => ({deviceId: id, sampleRate: 48000, channelCount: 1, echoCancellation: false});
    const s = {getTracks: () => [t], getAudioTracks: () => [t]}; streams.push(s); return s;
}
const create = (options = {}, setup = true) => {
    const r = new UiAudioRecorder({maxBytes: 10000, maxDurationMillis: 60000, startCaption: 'Start', finishCaption: 'Finish', readyCaption: 'Ready', errorCaption: 'Error', recordingOptions: options, setup: setup ? labels : undefined} as any, {} as any);
    instances.push(r); return r;
};
const button = (r: UiAudioRecorder, caption: string) => Array.from(r.doGetMainElement().querySelectorAll('button')).find(b => b.textContent === caption);
const states = (r: UiAudioRecorder) => { const f = jest.fn(); r.onStateChanged.addListener(f); return f; };
beforeEach(() => {
    jest.useFakeTimers(); instances = []; streams = []; recorders = []; now = 0; level = 0;
    jest.spyOn(performance, 'now').mockImplementation(() => now);
    HTMLMediaElement.prototype.pause = jest.fn(); HTMLMediaElement.prototype.load = jest.fn();
    URL.createObjectURL = jest.fn(() => 'blob:recording'); URL.revokeObjectURL = jest.fn();
    Object.defineProperty(window, 'isSecureContext', {value: true, configurable: true});
    const supported = {sampleRate: true, sampleSize: true, channelCount: true, echoCancellation: true, noiseSuppression: true, autoGainControl: true, deviceId: true};
    captures = jest.fn(async c => stream(c.audio.deviceId?.exact || 'mic1'));
    devices = Object.assign(new EventTarget(), {getUserMedia: captures, getSupportedConstraints: () => supported, enumerateDevices: jest.fn(async () => [
        {kind: 'audioinput', deviceId: 'default', label: 'Default'}, {kind: 'audioinput', deviceId: 'mic1', label: 'Built-in microphone'}, {kind: 'audioinput', deviceId: 'mic2', label: 'USB microphone'}, {kind: 'videoinput', deviceId: 'camera', label: 'Camera'}])});
    Object.defineProperty(navigator, 'mediaDevices', {value: devices, configurable: true});
    (global as any).AudioContext = class {
        state = 'running'; resume() { return Promise.resolve(); } close() { this.state = 'closed'; return Promise.resolve(); }
        createMediaStreamSource() { return {connect: jest.fn()}; }
        createAnalyser() { return {fftSize: 2048, getFloatTimeDomainData: a => a.fill(level)}; }
    };
    (global as any).MediaRecorder = class {
        static isTypeSupported(t: string) { return RECORDING_MIME_TYPES.includes(t); }
        state = 'inactive'; mimeType: string; audioBitsPerSecond: number;
        onstop: () => void; ondataavailable: (e: any) => void; onerror: () => void;
        constructor(s: any, o: any) { this.mimeType = o.mimeType; this.audioBitsPerSecond = o.audioBitsPerSecond; recorders.push(this); }
        start() { this.state = 'recording'; }
        stop() { this.state = 'inactive'; this.ondataavailable({data: new Blob(['audio'])}); this.onstop(); }
    };
});
afterEach(() => { instances.forEach(r => r.discard()); jest.restoreAllMocks(); jest.useRealTimers(); });

test('setup construction does not request permission; test meters without recording or upload', async () => {
    const r = create(), state = states(r), chunks = jest.fn(); r.onChunk.addListener(chunks);
    expect(captures).not.toHaveBeenCalled();
    button(r, 'Test').click(); await flush();
    expect(recorders).toHaveLength(0); expect(state).not.toHaveBeenCalled();
    level = .3; jest.advanceTimersByTime(100);
    expect(r.doGetMainElement().querySelector('meter').value).toBeCloseTo(.9);
    expect(r.doGetMainElement().textContent).toContain('48000 Hz');
    expect(r.doGetMainElement().querySelector('select').options.length).toBe(3);
    expect(chunks).not.toHaveBeenCalled();
    button(r, 'Stop test').click(); expect(streams[0].getTracks()[0].stop).toHaveBeenCalledTimes(1);
    expect(jest.getTimerCount()).toBe(0);
});

test('device switch during test releases old stream and requests the exact selected microphone', async () => {
    const r = create(); r.startMicrophoneTest(); await flush();
    const select = r.doGetMainElement().querySelector('select'); select.value = 'mic2'; select.dispatchEvent(new Event('change')); await flush();
    expect(streams[0].getTracks()[0].stop).toHaveBeenCalledTimes(1);
    expect(captures.mock.calls[1][0].audio.deviceId).toEqual({exact: 'mic2'});
    expect(r.doGetMainElement().textContent).toContain('USB microphone');
});

test('quality options reach capture/encoder; actual settings returned and changes locked during recording', async () => {
    const r = create({sampleRate: 44100, audioBitsPerSecond: 192000, echoCancellation: 0, noiseSuppression: 0, autoGainControl: 0});
    const actual = jest.fn(), warnings = jest.fn(), chunks = jest.fn();
    r.onCaptureSettingsChanged.addListener(actual); r.onWarning.addListener(warnings); r.onChunk.addListener(chunks);
    button(r, 'Start').click(); await flush();
    expect(captures.mock.calls[0][0].audio.sampleRate).toEqual({ideal: 44100});
    expect(captures.mock.calls[0][0].audio.noiseSuppression).toEqual({ideal: false});
    expect(recorders[0].audioBitsPerSecond).toBe(192000);
    expect(actual.mock.calls[0][0].settings.sampleRate).toBe(48000);
    expect(r.doGetMainElement().querySelector('select').disabled).toBe(true);
    r.selectDevice('mic2'); r.configureRecording({audioBitsPerSecond: 64000} as any);
    expect(warnings.mock.calls.map(c => c[0].code)).toEqual(['configurationRejected', 'configurationRejected']);
    expect(captures).toHaveBeenCalledTimes(1); expect(chunks).not.toHaveBeenCalled();
});

test('clipping indication holds briefly and clears; test deadline releases microphone', async () => {
    const r = create({microphoneTestMaxDurationMillis: 1000}); r.startMicrophoneTest(); await flush();
    level = 1; now = 100; jest.advanceTimersByTime(100);
    expect(r.doGetMainElement().querySelector<HTMLElement>('.audio-clipping').hidden).toBe(false);
    level = 0; now = 1200; jest.advanceTimersByTime(100);
    expect(streams[0].getTracks()[0].stop).toHaveBeenCalledTimes(1);
    expect(r.doGetMainElement().textContent).toContain('Test ended');
});

test('disconnected microphone finalizes captured audio and warns without discarding the take', async () => {
    const r = create(), state = states(r); button(r, 'Start').click(); await flush();
    streams[0].getTracks()[0].dispatchEvent(new Event('ended')); await flush();
    expect(state.mock.calls.map(c => c[0].state)).toEqual(['recording', 'ready']);
    expect(r.doGetMainElement().querySelector('audio').hidden).toBe(false);
    expect(r.doGetMainElement().textContent).toContain('Disconnected');
    expect(r.doGetMainElement().querySelector('a').download).toBe('recording.webm');
});

test('optional inactivity deadline preserves take, resets on signal and can be disabled', async () => {
    const r = create({inactivityTimeoutMillis: 1000}), state = states(r), warnings = jest.fn();
    r.onWarning.addListener(warnings); button(r, 'Start').click(); await flush();
    level = .1; now = 900; jest.advanceTimersByTime(100);
    level = 0; now = 1500; jest.advanceTimersByTime(100);
    expect(state.mock.calls.map(c => c[0].state)).toEqual(['recording']);
    now = 2000; jest.advanceTimersByTime(100);
    expect(state.mock.calls.map(c => c[0].state)).toEqual(['recording', 'ready']);
    expect(warnings).toHaveBeenCalledWith({code: 'inactivity'});
    expect(r.doGetMainElement().querySelector('audio').hidden).toBe(false);
    const unlimited = create({inactivityTimeoutMillis: 0}), otherState = states(unlimited);
    button(unlimited, 'Start').click(); await flush();
    now = 4000; jest.advanceTimersByTime(100);
    expect(otherState.mock.calls.map(c => c[0].state)).toEqual(['recording']);
});

test('late permission after test cancellation cannot resurrect microphone or change newer state', async () => {
    let resolve: (s: any) => void; captures.mockImplementationOnce(() => new Promise(r => resolve = r));
    const r = create(); r.startMicrophoneTest(); r.stopMicrophoneTest();
    resolve(stream()); await flush();
    expect(streams[0].getTracks()[0].stop).toHaveBeenCalledTimes(1); expect(recorders).toHaveLength(0);
    expect(button(r, 'Start').disabled).toBe(false);
});

test('strict unsupported constraint fails explicitly without permission request', async () => {
    devices.getSupportedConstraints = () => ({deviceId: true});
    const r = create({exactConstraints: true, sampleRate: 48000}), warning = jest.fn(); r.onWarning.addListener(warning);
    button(r, 'Start').click(); await flush();
    expect(captures).not.toHaveBeenCalled(); expect(warning).toHaveBeenCalledWith({code: 'constraintsUnsupported'});
});

test('idle configuration update works without microphone access; options-only use needs no setup panel', async () => {
    const r = create({}, false), setup = jest.fn(); r.onSetupStateChanged.addListener(setup);
    r.configureRecording({sampleRate: 48000, audioBitsPerSecond: 128000} as any);
    expect(captures).not.toHaveBeenCalled(); expect(setup).toHaveBeenCalledWith({state: 'configured'});
    button(r, 'Start').click(); await flush(); expect(recorders[0].audioBitsPerSecond).toBe(128000);
    expect(r.doGetMainElement().querySelector('select')).toBeNull();
});

test('explicit MIME allowlist never silently falls back to another encoding', async () => {
    const r = create({mimeTypes: ['audio/not-supported']}), state = states(r);
    button(r, 'Start').click(); await flush();
    expect(state).toHaveBeenCalledWith({state: 'unsupported'}); expect(captures).not.toHaveBeenCalled();
});

test('size limit and destroy release capture; ordinary ready/upload events remain separate', async () => {
    const r = create(), state = states(r); button(r, 'Start').click(); await flush();
    recorders[0].ondataavailable({data: new Blob(['x'.repeat(10001)])});
    expect(state.mock.calls.map(c => c[0].state)).toEqual(['recording', 'limit']);
    expect(streams[0].getTracks()[0].stop).toHaveBeenCalledTimes(1);
    expect(r.doGetMainElement().querySelector('audio').hidden).toBe(true);
});

test('completed draft is kept on close and removed only on explicit persistence acknowledgement', async () => {
    jest.spyOn(AudioRecordingDraftStore.prototype, 'get').mockResolvedValue(undefined);
    const put = jest.spyOn(AudioRecordingDraftStore.prototype, 'put').mockResolvedValue(undefined);
    const remove = jest.spyOn(AudioRecordingDraftStore.prototype, 'remove').mockResolvedValue(undefined);
    const r = create({localDraftKey: 'user-report'}); await flush();
    button(r, 'Start').click(); await flush(); button(r, 'Finish').click(); await flush();
    expect(put).toHaveBeenCalledTimes(1); expect(remove).not.toHaveBeenCalled();
    await r.acknowledgeRecording(); expect(remove).toHaveBeenCalledWith(put.mock.calls[0][0].id);
    r.discard(); expect(remove).toHaveBeenCalledTimes(1);
});

test('restoring an existing local draft never requests microphone and prevents accidental overwrite', async () => {
    jest.spyOn(AudioRecordingDraftStore.prototype, 'get').mockResolvedValue({id: 'saved', blob: new Blob(['saved']), extension: 'webm', settings: null});
    const r = create({localDraftKey: 'user-report'}), state = states(r); await flush();
    expect(button(r, 'Start').disabled).toBe(true); button(r, 'Restore').click();
    expect(state).toHaveBeenCalledWith({state: 'ready'}); expect(captures).not.toHaveBeenCalled();
    expect(r.doGetMainElement().querySelector('audio').hidden).toBe(false);
});

test('failed local persistence leaves playable/downloadable recording and reports failure', async () => {
    jest.spyOn(AudioRecordingDraftStore.prototype, 'get').mockResolvedValue(undefined);
    jest.spyOn(AudioRecordingDraftStore.prototype, 'put').mockRejectedValue(new Error('quota'));
    const r = create({localDraftKey: 'user-report'}), warnings = jest.fn(); r.onWarning.addListener(warnings); await flush();
    button(r, 'Start').click(); await flush(); button(r, 'Finish').click(); await flush();
    expect(warnings).toHaveBeenCalledWith({code: 'localSaveError'});
    expect(r.doGetMainElement().querySelector('a').hidden).toBe(false);
    expect(r.doGetMainElement().querySelector('audio').hidden).toBe(false);
});

test('constraints distinguish browser defaults, preference and mandatory microphone', () => {
    const o = recordingOptions({sampleRate: 48000, deviceId: 'usb'} as any);
    expect(audioConstraints(o, {sampleRate: true, deviceId: true})).toEqual({sampleRate: {ideal: 48000}, deviceId: {exact: 'usb'}});
    expect(audioConstraints(recordingOptions({exactConstraints: true, channelCount: 2, echoCancellation: 0} as any), {channelCount: true, echoCancellation: true})).toEqual({channelCount: {exact: 2}, echoCancellation: {exact: false}});
});
