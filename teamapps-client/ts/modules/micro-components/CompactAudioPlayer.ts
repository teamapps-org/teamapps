import {UiAudioSourceConfig} from "../../generated/UiAudioSourceConfig";
import {parseHtml} from "../Common";

/** A stable native media element per message, shared by chat and detail views. */
export class CompactAudioPlayer {
    private static active: CompactAudioPlayer;
    readonly element: HTMLElement;
    readonly audio: HTMLAudioElement;
    constructor(readonly source: UiAudioSourceConfig) {
        this.element = parseHtml(`<div class="teamapps-compact-audio"><div class="audio-caption"></div><audio controls preload="none"></audio><div class="audio-error" role="status" hidden></div></div>`);
        this.element.querySelector(".audio-caption").textContent = source.caption || "";
        this.audio = this.element.querySelector("audio");
        this.audio.src = source.audioUrl;
        this.audio.setAttribute("aria-label", source.caption || "");
        this.audio.addEventListener("play", () => {
            if (CompactAudioPlayer.active && CompactAudioPlayer.active !== this) CompactAudioPlayer.active.audio.pause();
            CompactAudioPlayer.active = this;
        });
        this.audio.addEventListener("error", () => {
            const error = this.element.querySelector<HTMLElement>(".audio-error");
            error.textContent = source.errorCaption; error.hidden = false;
        });
    }
    destroy() {
        if (CompactAudioPlayer.active === this) CompactAudioPlayer.active = null;
        this.audio.pause(); this.audio.removeAttribute("src"); this.audio.load(); this.element.remove();
    }
}
