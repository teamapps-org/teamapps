package org.teamapps.ux.component.audio;

import org.teamapps.dto.UiAudioPlayer;
import org.teamapps.dto.UiAudioSource;
import org.teamapps.ux.component.AbstractComponent;

/** Compact audio player; transcript remains ordinary application text. */
public final class AudioPlayer extends AbstractComponent {
    private final UiAudioSource source;
    public AudioPlayer(UiAudioSource source) { this.source = source; }
    @Override public UiAudioPlayer createUiComponent() {
        UiAudioPlayer ui = new UiAudioPlayer();
        mapAbstractUiComponentProperties(ui);
        ui.setSource(source);
        return ui;
    }
}
