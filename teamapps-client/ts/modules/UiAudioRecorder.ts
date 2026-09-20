import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiAudioRecorderConfig, UiAudioRecorderCommandHandler, UiAudioRecorderEventSource, UiAudioRecorder_StateChangedEvent, UiAudioRecorder_ChunkEvent} from "../generated/UiAudioRecorderConfig";

export const RECORDING_MIME_TYPES = ["audio/webm;codecs=opus", "audio/mp4", "audio/ogg;codecs=opus", "audio/webm", "audio/wav"];

export class UiAudioRecorder extends AbstractUiComponent<UiAudioRecorderConfig> implements UiAudioRecorderCommandHandler, UiAudioRecorderEventSource {
    readonly onStateChanged = new TeamAppsEvent<UiAudioRecorder_StateChangedEvent>();
    readonly onChunk = new TeamAppsEvent<UiAudioRecorder_ChunkEvent>();
    private main: HTMLElement;
    private startButton: HTMLButtonElement;
    private status: HTMLElement;
    private meter: HTMLMeterElement;
    private preview: HTMLAudioElement;
    private recorder: MediaRecorder;
    private stream: MediaStream;
    private audioContext: AudioContext;
    private timer: number;
    private chunks: Blob[] = [];
    private blob: Blob;
    private url: string;
    private generation = 0;
    private disposed = false;
    private started = 0;
    private size = 0;
    private extension: string;
    private uploadId: string;
    private visibility = () => { if (document.hidden && this.recorder?.state === "recording") this.finish(); };

    constructor(config: UiAudioRecorderConfig, context: TeamAppsUiContext) {
        super(config, context);
        this.main = parseHtml(`<div class="UiAudioRecorder"><button type="button"></button><div role="status" aria-live="polite"></div><meter min="0" max="1" value="0"></meter><output>0:00</output><audio controls preload="none" hidden></audio></div>`);
        this.startButton = this.main.querySelector("button");
        this.startButton.textContent = config.startCaption;
        this.status = this.main.querySelector('[role="status"]');
        this.meter = this.main.querySelector("meter");
        this.meter.setAttribute("aria-label", config.recordingCaption);
        this.preview = this.main.querySelector("audio");
        this.startButton.addEventListener("click", () => {
            if (this._config.finishCaption && this.recorder?.state === "recording") this.finish();
            else this.start();
        });
        document.addEventListener("visibilitychange", this.visibility);
        // Permission is requested only by a user's explicit start action, also on mobile Safari.
    }

    doGetMainElement() { return this.main; }

    private async start() {
        if (this.disposed || this.startButton.disabled) return;
        const generation = ++this.generation;
        this.startButton.disabled = true;
        if (!window.isSecureContext || !navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === "undefined") {
            this.fail("unsupported"); return;
        }
        try {
            const mimeType = RECORDING_MIME_TYPES.find(type => MediaRecorder.isTypeSupported(type));
            if (!mimeType) { this.fail("unsupported"); return; }
            const stream = await navigator.mediaDevices.getUserMedia({audio: {channelCount: 1, echoCancellation: true}, video: false});
            if (this.disposed || generation !== this.generation) { stream.getTracks().forEach(track => track.stop()); return; }
            this.stream = stream;
            this.extension = mimeType.includes("mp4") ? "mp4" : mimeType.includes("ogg") ? "ogg" : mimeType.includes("wav") ? "wav" : "webm";
            this.recorder = new MediaRecorder(stream, {mimeType, audioBitsPerSecond: 64000});
            this.chunks = []; this.size = 0;
            this.recorder.ondataavailable = event => {
                if (this.disposed || generation !== this.generation || !event.data.size) return;
                this.size += event.data.size;
                if (this.size > this._config.maxBytes) { this.fail("limit"); return; }
                this.chunks.push(event.data);
            };
            this.recorder.onerror = () => { if (generation === this.generation && !this.disposed) this.fail("error"); };
            this.recorder.onstop = () => {
                if (this.disposed || generation !== this.generation) return;
                this.releaseCapture();
                this.blob = new Blob(this.chunks, {type: mimeType});
                this.chunks = [];
                if (!this.blob.size) { this.fail("error"); return; }
                this.url = URL.createObjectURL(this.blob);
                this.preview.src = this.url;
                this.preview.hidden = false;
                this.startButton.hidden = true;
                this.meter.hidden = true;
                this.main.classList.remove("recording");
                this.status.textContent = this._config.readyCaption;
                this.onStateChanged.fire({state: "ready"});
            };
            stream.getTracks().forEach(track => track.addEventListener("ended", () => { if (generation === this.generation) this.finish(); }));
            this.recorder.start(250);
            this.started = performance.now();
            this.status.textContent = this._config.recordingCaption;
            this.startButton.hidden = !this._config.finishCaption;
            this.startButton.textContent = this._config.finishCaption || this._config.startCaption;
            this.startButton.disabled = false;
            this.meter.hidden = false;
            this.main.classList.add("recording");
            this.onStateChanged.fire({state: "recording"});
            let analyser: AnalyserNode;
            const samples = new Uint8Array(256);
            this.timer = window.setInterval(() => {
                const elapsed = performance.now() - this.started;
                const seconds = Math.floor(elapsed / 1000);
                this.main.querySelector("output").textContent = Math.floor(seconds / 60) + ":" + String(seconds % 60).padStart(2, "0");
                if (analyser) {
                    analyser.getByteTimeDomainData(samples);
                    this.meter.value = Math.min(1, Math.sqrt(samples.reduce((sum, value) => sum + Math.pow((value - 128) / 128, 2), 0) / samples.length) * 3);
                }
                if (elapsed >= this._config.maxDurationMillis) this.finish();
            }, 100);
            try {
                this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
                await this.audioContext.resume();
                if (this.disposed || generation !== this.generation || this.recorder.state !== "recording") return;
                analyser = this.audioContext.createAnalyser();
                analyser.fftSize = 256;
                this.audioContext.createMediaStreamSource(stream).connect(analyser);
            } catch (_) { /* Duration limit and recording work even when metering is unavailable. */ }
        } catch (_) { this.fail("error"); }
    }

    finish() {
        if (this.recorder?.state === "recording") this.recorder.stop();
    }

    async requestChunk(requestId: string, sequence: number) {
        if (this.disposed || !this.blob) { this.onStateChanged.fire({state: "error"}); return; }
        this.uploadId = requestId;
        const chunkSize = 192 * 1024;
        const offset = sequence * chunkSize;
        if (offset < 0 || offset >= this.blob.size) return;
        try {
            const bytes = new Uint8Array(await this.blob.slice(offset, offset + chunkSize).arrayBuffer());
            if (this.disposed || this.uploadId !== requestId) return;
            let binary = "";
            bytes.forEach(byte => binary += String.fromCharCode(byte));
            this.onChunk.fire({requestId, sequence, extension: this.extension, data: btoa(binary), last: offset + bytes.length >= this.blob.size});
        } catch (_) { this.onStateChanged.fire({state: "error"}); }
    }

    private fail(state: string) {
        this.generation++;
        if (this.recorder?.state === "recording") this.recorder.stop();
        this.releaseCapture();
        this.chunks = []; this.blob = null;
        this.status.textContent = this._config.errorCaption;
        this.startButton.hidden = false; this.startButton.disabled = false;
        this.startButton.textContent = this._config.startCaption;
        this.main.classList.remove("recording");
        this.onStateChanged.fire({state});
    }

    private releaseCapture() {
        window.clearInterval(this.timer);
        this.stream?.getTracks().forEach(track => track.stop()); this.stream = null;
        if (this.audioContext && this.audioContext.state !== "closed") this.audioContext.close().catch(() => {});
        this.audioContext = null;
        this.meter.value = 0;
    }

    discard() {
        this.disposed = true; this.generation++; this.uploadId = null;
        if (this.recorder?.state === "recording") this.recorder.stop();
        this.releaseCapture();
        this.preview.pause(); this.preview.removeAttribute("src"); this.preview.load();
        if (this.url) URL.revokeObjectURL(this.url);
        this.url = null; this.blob = null; this.chunks = [];
        document.removeEventListener("visibilitychange", this.visibility);
    }
    destroy() { this.discard(); super.destroy(); }
}
TeamAppsUiComponentRegistry.registerComponentClass("UiAudioRecorder", UiAudioRecorder);
