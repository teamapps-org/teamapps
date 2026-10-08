import {UiAudioRecorderConfig} from "../../generated/UiAudioRecorderConfig";
import {UiAudioRecordingOptionsConfig} from "../../generated/UiAudioRecordingOptionsConfig";
import {UiAudioCaptureSettingsConfig} from "../../generated/UiAudioCaptureSettingsConfig";
import {UiAudioInputDeviceConfig} from "../../generated/UiAudioInputDeviceConfig";
import {UiAudioRecorderSetupConfig} from "../../generated/UiAudioRecorderSetupConfig";
import {audioConstraints, recordingOptions} from "./AudioRecordingConstraints";
import {AudioRecordingDraft, AudioRecordingDraftStore} from "./AudioRecordingDraftStore";

interface RecorderEvents {
    state(state: string): void;
    setupState(state: string): void;
    warning(code: string): void;
    devices(devices: UiAudioInputDeviceConfig[]): void;
    settings(settings: UiAudioCaptureSettingsConfig): void;
    chunk(chunk: any): void;
}

/** Explicit opt-in controller. The original recorder remains on its unchanged code path. */
export class EnhancedAudioRecorder {
    readonly element = document.createElement("div");
    private options: UiAudioRecordingOptionsConfig;
    private startButton = document.createElement("button");
    private testButton = document.createElement("button");
    private deviceSelect = document.createElement("select");
    private status = document.createElement("div");
    private warningText = document.createElement("div");
    private settingsText = document.createElement("div");
    private localStatus = document.createElement("div");
    private clipText = document.createElement("div");
    private meter = document.createElement("meter");
    private elapsedText = document.createElement("output");
    private preview = document.createElement("audio");
    private download = document.createElement("a");
    private restoreButton = document.createElement("button");
    private discardDraftButton = document.createElement("button");
    private stream: MediaStream;
    private recorder: MediaRecorder;
    private audioContext: AudioContext;
    private analyser: AnalyserNode;
    private timer: number;
    private mode: "idle" | "opening" | "testing" | "recording" | "stopping" | "ready" = "idle";
    private generation = 0;
    private disposed = false;
    private started = 0;
    private lastClipped = -Infinity;
    private lastSignal = 0;
    private chunks: Blob[] = [];
    private size = 0;
    private blob: Blob;
    private extension: string;
    private url: string;
    private uploadId: string;
    private settings: UiAudioCaptureSettingsConfig;
    private store: AudioRecordingDraftStore;
    private draft: AudioRecordingDraft;
    private storedDraft: AudioRecordingDraft;
    private draftLoading = false;
    private saving: Promise<void> = Promise.resolve();
    private readonly visibility = () => {
        if (document.hidden && this.options.stopWhenHidden) {
            if (this.mode === "testing" || this.mode === "opening") this.stopMicrophoneTest();
            else if (this.mode === "recording") this.finish();
        }
        // A background timer is not a dependable clock; recheck deadlines on return as well.
        this.tick();
    };
    private readonly deviceChange = () => { void this.refreshDevices(); };
    private readonly unloading = () => this.discard();

    constructor(private config: UiAudioRecorderConfig, private fallbackMimeTypes: string[], private events: RecorderEvents) {
        this.options = recordingOptions(config.recordingOptions);
        this.element.className = "UiAudioRecorder enhanced-audio-recorder";
        this.startButton.type = this.testButton.type = this.restoreButton.type = this.discardDraftButton.type = "button";
        this.startButton.textContent = config.startCaption;
        this.startButton.addEventListener("click", () => {
            if (this.mode === "recording") this.finish();
            else void this.open(false);
        });
        this.status.setAttribute("role", "status"); this.status.setAttribute("aria-live", "polite");
        this.warningText.setAttribute("role", "alert");
        this.warningText.className = "audio-warning";
        this.localStatus.className = "audio-local-status";
        this.clipText.className = "audio-clipping"; this.clipText.hidden = true;
        this.meter.min = 0; this.meter.max = 1; this.meter.value = 0;
        this.meter.setAttribute("aria-label", config.recordingCaption || "");
        this.elapsedText.textContent = "0:00";
        this.preview.controls = true; this.preview.preload = "none"; this.preview.hidden = true;
        this.settingsText.className = "audio-capture-settings";
        if (config.setup) {
            const setup = document.createElement("div"); setup.className = "audio-setup";
            const label = document.createElement("label");
            const caption = document.createElement("span"); caption.textContent = config.setup.microphoneCaption;
            this.deviceSelect.setAttribute("aria-label", config.setup.microphoneCaption);
            this.deviceSelect.addEventListener("change", () => this.selectDevice(this.deviceSelect.value));
            label.append(caption, this.deviceSelect);
            this.testButton.textContent = config.setup.testCaption;
            this.testButton.addEventListener("click", () => {
                if (this.mode === "testing" || this.mode === "opening") this.stopMicrophoneTest();
                else this.startMicrophoneTest();
            });
            setup.append(label, this.testButton); this.element.append(setup);
            this.clipText.textContent = config.setup.clippingCaption;
            this.settingsText.setAttribute("aria-label", config.setup.settingsCaption);
            this.download.textContent = config.setup.downloadCaption;
            this.restoreButton.textContent = config.setup.restoreCaption;
            this.discardDraftButton.textContent = config.setup.discardDraftCaption;
        }
        this.download.hidden = true;
        this.restoreButton.hidden = this.discardDraftButton.hidden = true;
        this.restoreButton.addEventListener("click", () => this.restoreDraft());
        this.discardDraftButton.addEventListener("click", () => { void this.deleteStoredDraft(); });
        this.element.append(this.startButton, this.status, this.warningText, this.meter, this.clipText,
            this.elapsedText, this.settingsText, this.preview, this.download, this.localStatus, this.restoreButton, this.discardDraftButton);
        this.showDevices([]);
        document.addEventListener("visibilitychange", this.visibility);
        window.addEventListener("beforeunload", this.unloading);
        navigator.mediaDevices?.addEventListener?.("devicechange", this.deviceChange);
        if (this.options.localDraftKey) {
            this.store = new AudioRecordingDraftStore(this.options.localDraftKey);
            this.draftLoading = true;
            this.updateControls();
            void this.loadDraft();
        }
        // Construction never prompts for permission or opens a microphone.
    }

    private warn(code: string) {
        if (this.disposed) return;
        const labels = this.config.setup;
        this.warningText.textContent = labels?.[(code + "Caption") as keyof UiAudioRecorderSetupConfig] as string || this.config.errorCaption || "";
        this.events.warning(code);
    }

    private updateControls() {
        const busy = ["opening", "recording", "stopping", "ready"].includes(this.mode);
        this.deviceSelect.disabled = busy || this.draftLoading || !!this.storedDraft;
        this.testButton.disabled = this.mode === "recording" || this.mode === "stopping" || this.mode === "ready" || this.draftLoading || !!this.storedDraft;
        this.testButton.textContent = this.mode === "testing" || this.mode === "opening" ? this.config.setup?.stopTestCaption : this.config.setup?.testCaption;
        this.startButton.hidden = this.mode === "ready" || (this.mode === "recording" && !this.config.finishCaption);
        this.startButton.disabled = this.mode === "opening" || this.mode === "stopping" || this.draftLoading || !!this.storedDraft;
        this.startButton.textContent = this.mode === "recording" ? this.config.finishCaption : this.config.startCaption;
        this.element.classList.toggle("recording", this.mode === "recording");
        this.meter.hidden = this.mode === "ready";
    }

    configureRecording(value: UiAudioRecordingOptionsConfig) {
        if (this.disposed) return;
        if (this.mode !== "idle" || this.uploadId || this.storedDraft || this.options.localDraftKey !== value?.localDraftKey) {
            this.warn("configurationRejected"); return;
        }
        this.options = recordingOptions(value);
        this.deviceSelect.value = this.options.deviceId || "";
        this.events.setupState("configured");
    }

    async refreshDevices() {
        if (this.disposed) return;
        try {
            const devices = (await navigator.mediaDevices.enumerateDevices()).filter(d => d.kind === "audioinput")
                .map(d => ({deviceId: d.deviceId, label: d.label} as UiAudioInputDeviceConfig));
            if (this.disposed) return;
            this.showDevices(devices); this.events.devices(devices);
        } catch (_) { this.warn("deviceListError"); }
    }

    private showDevices(devices: UiAudioInputDeviceConfig[]) {
        const selected = this.options.deviceId || "";
        this.deviceSelect.textContent = "";
        const option = (value: string, label: string) => {
            const o = document.createElement("option"); o.value = value; o.textContent = label; this.deviceSelect.append(o);
        };
        option("", this.config.setup?.defaultMicrophoneCaption || "");
        devices.filter(d => !!d.deviceId && d.deviceId !== "default").forEach((d, i) => option(d.deviceId, d.label || `${this.config.setup?.microphoneCaption || ""} ${i + 1}`));
        // Keep an unavailable selection visible; never silently switch to a different device.
        if (selected && !devices.some(d => d.deviceId === selected && d.deviceId !== "default")) option(selected, this.settings?.label || selected);
        this.deviceSelect.value = selected;
    }

    selectDevice(deviceId: string) {
        if (this.disposed) return;
        if (this.mode !== "idle" && this.mode !== "testing") {
            this.deviceSelect.value = this.options.deviceId || "";
            this.warn("configurationRejected"); return;
        }
        const restartTest = this.mode === "testing";
        if (restartTest) this.stopMicrophoneTest();
        this.options = {...this.options, deviceId: deviceId || null};
        this.deviceSelect.value = deviceId || "";
        this.events.setupState("configured");
        if (restartTest) this.startMicrophoneTest();
    }

    startMicrophoneTest() { void this.open(true); }
    stopMicrophoneTest() {
        if (this.mode !== "testing" && this.mode !== "opening") return;
        ++this.generation;
        this.releaseCapture(); this.mode = "idle";
        this.status.textContent = ""; this.updateControls(); this.events.setupState("idle");
    }

    private async open(test: boolean) {
        if (this.disposed || this.draftLoading || this.storedDraft || (this.mode !== "idle" && this.mode !== "testing")) return;
        if (this.mode === "testing") this.stopMicrophoneTest();
        const generation = ++this.generation;
        this.mode = "opening"; this.warningText.textContent = ""; this.updateControls(); this.events.setupState("opening");
        try {
            if (!window.isSecureContext || !navigator.mediaDevices?.getUserMedia || (!test && typeof MediaRecorder === "undefined")) throw new Error("unsupported");
            const mimeType = test ? null : (this.options.mimeTypes || this.fallbackMimeTypes).find(type => MediaRecorder.isTypeSupported(type));
            if (!test && !mimeType) throw new Error("unsupported");
            const constraints = audioConstraints(this.options, navigator.mediaDevices.getSupportedConstraints());
            const stream = await navigator.mediaDevices.getUserMedia({audio: constraints, video: false});
            if (this.disposed || generation !== this.generation) { stream.getTracks().forEach(t => t.stop()); return; }
            this.stream = stream;
            const tracks = stream.getAudioTracks();
            if (!tracks.length || tracks.some(t => t.readyState === "ended")) throw new Error("error");
            tracks.forEach(track => track.addEventListener("ended", () => {
                if (generation !== this.generation || this.disposed) return;
                if (this.mode === "recording") this.finish();
                else if (this.mode === "testing") this.stopMicrophoneTest();
                this.warn("deviceDisconnected");
            }));
            this.started = performance.now(); this.lastSignal = this.started; this.lastClipped = -Infinity;
            this.mode = test ? "testing" : "recording";
            if (test) {
                this.status.textContent = this.config.setup?.testingCaption || "";
                this.events.setupState("testing");
            } else {
                this.recorder = new MediaRecorder(stream, {mimeType, audioBitsPerSecond: this.options.audioBitsPerSecond});
                this.chunks = []; this.size = 0;
                this.recorder.ondataavailable = event => {
                    if (this.disposed || generation !== this.generation || !event.data.size) return;
                    this.size += event.data.size;
                    if (this.size > this.config.maxBytes) { this.fail("limit"); return; }
                    this.chunks.push(event.data);
                };
                this.recorder.onerror = () => { if (!this.disposed && generation === this.generation) this.fail("error"); };
                this.recorder.onstop = () => {
                    if (this.disposed || generation !== this.generation) return;
                    const type = this.recorder.mimeType || mimeType;
                    this.releaseCapture();
                    this.blob = new Blob(this.chunks, {type}); this.chunks = [];
                    if (!this.blob.size) { this.fail("error"); return; }
                    this.extension = type.includes("mp4") ? "mp4" : type.includes("ogg") ? "ogg" : type.includes("wav") ? "wav" : "webm";
                    this.draft = {id: `${Date.now()}-${Math.random().toString(36).slice(2)}`, blob: this.blob, extension: this.extension, settings: this.settings};
                    this.showPreview();
                    if (this.store) this.saving = this.store.put(this.draft).then(() => {
                        if (!this.disposed) { this.localStatus.textContent = this.config.setup?.locallySavedCaption || ""; this.events.setupState("localSaved"); }
                    }).catch(() => {
                        if (!this.disposed) { this.localStatus.textContent = this.config.setup?.localSaveErrorCaption || ""; this.events.warning("localSaveError"); }
                    });
                    this.events.state("ready");
                };
                this.recorder.start(250);
                this.status.textContent = this.config.recordingCaption;
                this.events.state("recording");
            }
            this.reportSettings(tracks[0], test ? null : this.recorder);
            this.updateControls(); this.timer = window.setInterval(() => this.tick(), 100);
            void this.refreshDevices();
            // Deadlines already run even if the browser delays AudioContext.resume().
            try {
                this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
                await this.audioContext.resume();
                if (this.disposed || generation !== this.generation || !["testing", "recording"].includes(this.mode)) return;
                this.analyser = this.audioContext.createAnalyser(); this.analyser.fftSize = 2048;
                this.audioContext.createMediaStreamSource(stream).connect(this.analyser);
            } catch (_) { this.warn("meterUnavailable"); }
        } catch (error) {
            if (this.disposed || generation !== this.generation) return;
            if (error?.message === "constraintsUnsupported" || error?.name === "OverconstrainedError") this.warn("constraintsUnsupported");
            this.fail(error?.message === "unsupported" ? "unsupported" : "error");
        }
    }

    private reportSettings(track: MediaStreamTrack, recorder: MediaRecorder) {
        const s = track.getSettings() as MediaTrackSettings & {channelCount?: number};
        const flag = (name: "echoCancellation" | "noiseSuppression" | "autoGainControl") => typeof s[name] === "boolean" ? (s[name] ? 1 : 0) : -1;
        this.settings = {deviceId: s.deviceId || "", label: track.label || "", sampleRate: s.sampleRate || 0,
            sampleSize: s.sampleSize || 0, channelCount: s.channelCount || 0,
            audioBitsPerSecond: recorder?.audioBitsPerSecond || 0, mimeType: recorder?.mimeType || "",
            echoCancellation: flag("echoCancellation"), noiseSuppression: flag("noiseSuppression"), autoGainControl: flag("autoGainControl")} as UiAudioCaptureSettingsConfig;
        this.showSettings(); this.events.settings(this.settings);
    }
    private showSettings() {
        if (!this.config.setup || !this.settings) return;
        const s = this.settings;
        this.settingsText.textContent = [s.label, s.sampleRate ? `${s.sampleRate} Hz` : "", s.sampleSize ? `${s.sampleSize} bit` : "",
            s.channelCount ? `${s.channelCount} ch` : "", s.audioBitsPerSecond ? `${s.audioBitsPerSecond / 1000} kbit/s` : "", s.mimeType].filter(Boolean).join(" · ");
    }
    private tick() {
        if (!this.stream || !["testing", "recording"].includes(this.mode)) return;
        const elapsed = performance.now() - this.started;
        const seconds = Math.floor(elapsed / 1000);
        this.elapsedText.textContent = `${Math.floor(seconds / 60)}:${String(seconds % 60).padStart(2, "0")}`;
        if (this.analyser) {
            const values = new Float32Array(this.analyser.fftSize);
            this.analyser.getFloatTimeDomainData(values);
            let peak = 0, squares = 0;
            values.forEach(v => { peak = Math.max(peak, Math.abs(v)); squares += v * v; });
            const rms = Math.sqrt(squares / values.length);
            this.meter.value = Math.min(1, rms * 3);
            if (rms > this.options.silenceThreshold) this.lastSignal = performance.now();
            if (peak >= this.options.clippingThreshold) this.lastClipped = performance.now();
            this.clipText.hidden = !this.config.setup || performance.now() - this.lastClipped > 1500;
            if (this.mode === "recording" && this.options.inactivityTimeoutMillis > 0
                && performance.now() - this.lastSignal >= this.options.inactivityTimeoutMillis) {
                this.finish(); this.warn("inactivity"); return;
            }
        }
        if (this.mode === "testing" && elapsed >= this.options.microphoneTestMaxDurationMillis) { this.stopMicrophoneTest(); this.warn("testEnded"); }
        else if (this.mode === "recording" && elapsed >= this.config.maxDurationMillis) this.finish();
    }

    finish() {
        if (this.mode === "recording" && this.recorder?.state === "recording") {
            this.mode = "stopping"; this.updateControls(); this.recorder.stop();
        }
    }
    private showPreview() {
        this.mode = "ready";
        if (this.url) URL.revokeObjectURL(this.url);
        this.url = URL.createObjectURL(this.blob);
        this.preview.src = this.url; this.preview.hidden = false;
        this.download.href = this.url; this.download.download = `recording.${this.extension}`;
        this.download.hidden = !this.config.setup;
        this.status.textContent = this.config.readyCaption; this.updateControls();
    }
    async requestChunk(requestId: string, sequence: number) {
        if (this.disposed || !this.blob || this.mode !== "ready") { this.events.state("error"); return; }
        this.uploadId = requestId;
        const offset = sequence * 192 * 1024;
        if (!Number.isInteger(sequence) || offset < 0 || offset >= this.blob.size) return;
        try {
            const bytes = new Uint8Array(await this.blob.slice(offset, offset + 192 * 1024).arrayBuffer());
            if (this.disposed || this.uploadId !== requestId) return;
            let binary = ""; bytes.forEach(b => binary += String.fromCharCode(b));
            this.events.chunk({requestId, sequence, extension: this.extension, data: btoa(binary), last: offset + bytes.length >= this.blob.size});
        } catch (_) { this.events.state("error"); }
    }
    async acknowledgeRecording() {
        if (this.disposed || !this.draft) return;
        try {
            await this.saving;
            await this.store?.remove(this.draft.id);
            if (!this.disposed) { this.uploadId = null; this.localStatus.textContent = ""; this.events.setupState("acknowledged"); }
        } catch (_) { this.warn("localSaveError"); }
    }
    private async loadDraft() {
        try {
            const draft = await this.store.get();
            if (this.disposed) return;
            if (draft) {
                if (!(draft.blob instanceof Blob) || !draft.blob.size || draft.blob.size > this.config.maxBytes || !["webm", "mp4", "ogg", "wav"].includes(draft.extension)) throw new Error("Invalid local draft");
                this.storedDraft = draft;
                this.restoreButton.hidden = this.discardDraftButton.hidden = !this.config.setup;
                this.events.setupState("draftAvailable");
            }
        } catch (_) { this.warn("localSaveError"); }
        finally { if (!this.disposed) { this.draftLoading = false; this.updateControls(); } }
    }
    restoreDraft() {
        if (this.disposed || !this.storedDraft || this.mode !== "idle") return;
        this.draft = this.storedDraft; this.storedDraft = null;
        this.blob = this.draft.blob; this.extension = this.draft.extension; this.settings = this.draft.settings;
        this.restoreButton.hidden = this.discardDraftButton.hidden = true;
        this.showPreview(); this.showSettings();
        if (this.settings) this.events.settings(this.settings);
        this.events.setupState("restored"); this.events.state("ready");
    }
    async deleteStoredDraft() {
        if (this.disposed || !this.storedDraft || this.mode !== "idle") return;
        this.draftLoading = true; this.updateControls();
        try {
            await this.store.remove(this.storedDraft.id);
            if (this.disposed) return;
            this.storedDraft = null; this.restoreButton.hidden = this.discardDraftButton.hidden = true;
        } catch (_) { this.warn("localSaveError"); }
        finally { if (!this.disposed) { this.draftLoading = false; this.updateControls(); } }
    }
    private fail(state: string) {
        ++this.generation;
        if (this.recorder?.state === "recording") this.recorder.stop();
        this.releaseCapture(); this.chunks = []; this.blob = null;
        this.mode = "idle"; this.status.textContent = this.config.errorCaption;
        this.updateControls(); this.events.state(state);
    }
    private releaseCapture() {
        window.clearInterval(this.timer);
        this.analyser = null;
        this.stream?.getTracks().forEach(t => t.stop()); this.stream = null;
        if (this.audioContext && this.audioContext.state !== "closed") this.audioContext.close().catch(() => {});
        this.audioContext = null; this.meter.value = 0; this.clipText.hidden = true;
    }
    discard() {
        this.disposed = true; ++this.generation; this.uploadId = null;
        if (this.recorder?.state === "recording") this.recorder.stop();
        this.releaseCapture(); this.preview.pause(); this.preview.removeAttribute("src"); this.preview.load();
        if (this.url) URL.revokeObjectURL(this.url);
        this.blob = null; this.chunks = []; this.url = null;
        document.removeEventListener("visibilitychange", this.visibility);
        window.removeEventListener("beforeunload", this.unloading);
        navigator.mediaDevices?.removeEventListener?.("devicechange", this.deviceChange);
        // Opt-in persisted drafts survive dismissal. Only an explicit delete or acknowledgement removes them.
    }
}
