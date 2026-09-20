import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiAudioPlayerConfig} from "../generated/UiAudioPlayerConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {CompactAudioPlayer} from "./micro-components/CompactAudioPlayer";

export class UiAudioPlayer extends AbstractUiComponent<UiAudioPlayerConfig> {
    private player: CompactAudioPlayer;
    constructor(config: UiAudioPlayerConfig, context: TeamAppsUiContext) { super(config, context); this.player = new CompactAudioPlayer(config.source); }
    doGetMainElement() { return this.player.element; }
    destroy() { this.player.destroy(); super.destroy(); }
}
TeamAppsUiComponentRegistry.registerComponentClass("UiAudioPlayer", UiAudioPlayer);
