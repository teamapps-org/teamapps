/** @jest-environment jsdom */
import {UiChatInput} from "../modules/UiChatInput";
import {UiAudioRecorder} from "../modules/UiAudioRecorder";
import {UiChatDisplay} from "../modules/UiChatDisplay";
import {CompactAudioPlayer} from "../modules/micro-components/CompactAudioPlayer";
(global as any).$ = require('jquery');
const createInput = (extra = {}) => new UiChatInput({messageLengthLimit: 1000, attachmentsEnabled: true, ...extra} as any, {} as any);
const textInput = (input: UiChatInput) => input.doGetMainElement().querySelector('textarea');
beforeEach(() => {
    HTMLMediaElement.prototype.pause = jest.fn(); HTMLMediaElement.prototype.load = jest.fn();
    (global as any).ResizeObserver = class { observe() {} disconnect() {} };
});
afterEach(() => { document.body.innerHTML = ''; });
test('legacy chat still sends once and clears immediately; optional microphone preserves text', () => {
    const input = createInput(); const sent = jest.fn(); input.onMessageSent.addListener(sent);
    textInput(input).value = 'existing draft'; input.setMicrophone(true, 'Record');
    expect(textInput(input).value).toBe('existing draft');
    input.doGetMainElement().querySelector<HTMLElement>('.send-button').click();
    expect(sent).toHaveBeenCalledTimes(1); expect(textInput(input).value).toBe('');
});
test('acknowledged send retains draft on failure and ignores repeated send', () => {
    const input = createInput({acknowledgedSending: true}); const sent = jest.fn(); input.onMessageSent.addListener(sent);
    textInput(input).value = 'keep this'; const button = input.doGetMainElement().querySelector<HTMLElement>('.send-button');
    button.click(); button.click(); expect(sent).toHaveBeenCalledTimes(1);
    expect(textInput(input).value).toBe('keep this'); expect(textInput(input).disabled).toBe(true);
    input.completeSend(false); expect(textInput(input).value).toBe('keep this'); expect(textInput(input).disabled).toBe(false);
    button.click(); input.completeSend(true); expect(textInput(input).value).toBe('');
});
test('late microphone permission after dismissal immediately releases tracks', async () => {
    let resolve: (stream: any) => void; const stop = jest.fn();
    Object.defineProperty(window, 'isSecureContext', {value: true, configurable: true});
    Object.defineProperty(navigator, 'mediaDevices', {value: {getUserMedia: () => new Promise(r => resolve = r)}, configurable: true});
    (global as any).MediaRecorder = {isTypeSupported: () => true};
    const recorder = new UiAudioRecorder({maxBytes: 10000, maxDurationMillis: 1000} as any, {} as any);
    recorder.doGetMainElement().querySelector('button').click(); recorder.discard();
    resolve({getTracks: () => [{stop}]}); await Promise.resolve(); await Promise.resolve();
    expect(stop).toHaveBeenCalledTimes(1);
});
test('chat update preserves playing audio node; deletion and clear release it', () => {
    const source = {mediaId: 'one', audioUrl: '/audio'};
    const message = {id: 1, text: 'Transcript', audios: [source], photos: [], files: []};
    const display = new UiChatDisplay({initialMessages: {messages: [message], containsFirstMessage: true}} as any, {} as any);
    const audio = display.doGetMainElement().querySelector('audio'); audio.currentTime = .5;
    display.updateMessage({...message, text: 'Updated metadata'} as any);
    expect(display.doGetMainElement().querySelector('audio')).toBe(audio); expect(audio.currentTime).toBe(.5);
    display.deleteMessage(1); expect(HTMLMediaElement.prototype.pause).toHaveBeenCalled(); expect(audio.hasAttribute('src')).toBe(false);
    display.addMessages({messages: [{...message, id: 2}], containsFirstMessage: true} as any);
    const second = display.doGetMainElement().querySelector('audio');
    display.clearMessages({messages: [], containsFirstMessage: true} as any);
    expect(second.hasAttribute('src')).toBe(false);
});
test('playing another voice message pauses only the previous voice player', () => {
    const first = new CompactAudioPlayer({audioUrl: '/one'} as any);
    const second = new CompactAudioPlayer({audioUrl: '/two'} as any);
    first.audio.pause = jest.fn(); second.audio.pause = jest.fn();
    first.audio.dispatchEvent(new Event('play')); second.audio.dispatchEvent(new Event('play'));
    expect(first.audio.pause).toHaveBeenCalledTimes(1); expect(second.audio.pause).not.toHaveBeenCalled();
    first.destroy(); second.destroy();
});
test('time limit and release work while audio metering is waiting to resume', async () => {
    jest.useFakeTimers(); const stopTrack = jest.fn(); let resume: () => void;
    const track = {stop: stopTrack, addEventListener: jest.fn()};
    Object.defineProperty(window, 'isSecureContext', {value: true, configurable: true});
    Object.defineProperty(navigator, 'mediaDevices', {value: {getUserMedia: async () => ({getTracks: () => [track]})}, configurable: true});
    (global as any).AudioContext = class { state = 'suspended'; resume() { return new Promise<void>(resolve => resume = resolve); } close() { return Promise.resolve(); } };
    let recording: any;
    (global as any).MediaRecorder = class {
        static isTypeSupported() { return true; }
        state = 'inactive'; onstop: () => void;
        constructor() { recording = this; }
        start() { this.state = 'recording'; }
        stop() { this.state = 'inactive'; this.onstop(); }
    };
    const recorder = new UiAudioRecorder({maxBytes: 10000, maxDurationMillis: 1000} as any, {} as any);
    recorder.doGetMainElement().querySelector('button').click(); await Promise.resolve(); await Promise.resolve();
    const clock = jest.spyOn(performance, 'now').mockReturnValue(10000);
    jest.advanceTimersByTime(1100);
    expect(recording.state).toBe('inactive'); expect(stopTrack).toHaveBeenCalledTimes(1);
    resume(); await Promise.resolve(); await Promise.resolve();
    expect(jest.getTimerCount()).toBe(0);
    recorder.discard(); clock.mockRestore(); jest.useRealTimers();
});

test('native voice player uses browser controls without additional requests', () => {
    const fetch = jest.fn(); (global as any).fetch = fetch;
    const player = new CompactAudioPlayer({audioUrl: '/voice.mp3', caption: 'Voice'} as any);
    expect(player.audio.controls).toBe(true);
    expect(player.element.querySelector('canvas, button')).toBeNull();
    expect(fetch).not.toHaveBeenCalled();
    player.destroy();
});
test('combined recorder button finishes once and shows local preview without requesting upload', async () => {
    const stopTrack = jest.fn();
    const track = {stop: stopTrack, addEventListener: jest.fn()};
    Object.defineProperty(window, 'isSecureContext', {value: true, configurable: true});
    Object.defineProperty(navigator, 'mediaDevices', {value: {getUserMedia: async () => ({getTracks: () => [track]})}, configurable: true});
    (global as any).AudioContext = class { state = 'running'; resume() { return Promise.resolve(); } close() { return Promise.resolve(); } };
    URL.createObjectURL = jest.fn(() => 'blob:preview'); URL.revokeObjectURL = jest.fn();
    let stop = jest.fn();
    (global as any).MediaRecorder = class {
        static isTypeSupported() { return true; }
        state = 'inactive'; onstop: () => void; ondataavailable: (event: any) => void;
        start() { this.state = 'recording'; }
        stop() { stop(); this.state = 'inactive'; this.ondataavailable({data: new Blob(['audio'])}); this.onstop(); }
    };
    const recorder = new UiAudioRecorder({maxBytes: 10000, maxDurationMillis: 1000, startCaption: 'Start', finishCaption: 'Finish'} as any, {} as any);
    const states = jest.fn(), chunks = jest.fn(); recorder.onStateChanged.addListener(states); recorder.onChunk.addListener(chunks);
    const button = recorder.doGetMainElement().querySelector('button');
    button.click(); await Promise.resolve(); await Promise.resolve();
    expect(button.textContent).toBe('Finish'); expect(button.hidden).toBe(false); expect(button.disabled).toBe(false);
    button.click();
    expect(stop).toHaveBeenCalledTimes(1); expect(stopTrack).toHaveBeenCalledTimes(1);
    expect(states.mock.calls.map(call => call[0].state)).toEqual(['recording', 'ready']);
    expect(button.hidden).toBe(true);
    expect(recorder.doGetMainElement().querySelector('audio').hidden).toBe(false);
    expect(chunks).not.toHaveBeenCalled();
    recorder.discard(); expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:preview');
});
