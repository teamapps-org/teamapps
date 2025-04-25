/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.component.shakaplayer;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.session.SessionContext;

import java.util.List;

// #synched
public class ShakaPlayer extends AbstractComponent implements DtoShakaPlayerEventHandler {

    private final DtoShakaPlayerClientObjectChannel clientObjectChannel = new DtoShakaPlayerClientObjectChannel(getClientObjectChannel());

    public final ProjectorEvent<Void> onErrorLoading = new ProjectorEvent<>(clientObjectChannel::toggleErrorLoadingEvent);
    public final ProjectorEvent<DtoShakaManifest> onManifestLoaded = new ProjectorEvent<>(clientObjectChannel::toggleManifestLoadedEvent);
    public final ProjectorEvent<Long> onTimeUpdate = new ProjectorEvent<>(clientObjectChannel::toggleTimeUpdateEvent);
    public final ProjectorEvent<Void> onEnded = new ProjectorEvent<>(clientObjectChannel::toggleEndedEvent);
    public final ProjectorEvent<SkipClickedEvent> onSkipClicked = new ProjectorEvent<>();

    public static void setDistinctManifestAudioTracksFixEnabled(boolean enabled) {
        SessionContext.current().sendStaticCommand(ShakaPlayer.class, DtoShakaPlayer.SetDistinctManifestAudioTracksFixEnabledCommand.CMD_NAME,
                new DtoShakaPlayer.SetDistinctManifestAudioTracksFixEnabledCommand(enabled));
    }

    private String hlsUrl;
    private String dashUrl;
    private String posterImageUrl;
    private PosterImageSize posterImageSize = PosterImageSize.CONTAIN;
    private int timeUpdateEventThrottleMillis = 1000;
    private Color backgroundColor = Color.BLACK;
    private TrackLabelFormat trackLabelFormat = TrackLabelFormat.LABEL;
    private boolean videoDisabled = false;
    private String audioLanguage;
    private boolean bigPlayButtonEnabled = true;
    private int controlFadeDelaySeconds = 0; // 0 = default value (see shaka docs)
    private List<ControlPanelElementType> controlPanelElements = List.of(
            ControlPanelElementType.PLAY_PAUSE,
            ControlPanelElementType.TIME_AND_DURATION,
            ControlPanelElementType.SPACER,
            ControlPanelElementType.MUTE,
            ControlPanelElementType.VOLUME,
            ControlPanelElementType.FULLSCREEN,
            ControlPanelElementType.OVERFLOW_MENU
    );

    private long timeMillis = 0;

    public ShakaPlayer() {
    }

    public ShakaPlayer(String hlsUrl, String dashUrl) {
        this.hlsUrl = hlsUrl;
        this.dashUrl = dashUrl;
    }

    @Override
    public DtoComponentConfig createDto() {
        DtoShakaPlayer ui = new DtoShakaPlayer();
        mapAbstractConfigProperties(ui);
        ui.setHlsUrl(hlsUrl);
        ui.setDashUrl(dashUrl);
        ui.setPosterImageUrl(posterImageUrl);
        ui.setPosterImageSize(posterImageSize);
        ui.setTimeUpdateEventThrottleMillis(timeUpdateEventThrottleMillis);
        ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
        ui.setTrackLabelFormat(trackLabelFormat);
        ui.setVideoDisabled(videoDisabled);
        ui.setTimeMillis(timeMillis);
        ui.setPreferredAudioLanguage(audioLanguage);
        ui.setBigPlayButtonEnabled(bigPlayButtonEnabled);
        ui.setControlFadeDelaySeconds(controlFadeDelaySeconds);
        ui.setControlPanelElements(controlPanelElements);
        return ui;
    }

    @Override
    public void handleErrorLoading() {
        onErrorLoading.fire(null);
    }

    @Override
    public void handleManifestLoaded(DtoShakaManifestWrapper manifest) {
        onManifestLoaded.fire(manifest.unwrap());
    }

    @Override
    public void handleTimeUpdate(long timeMillis) {
        onTimeUpdate.fire(timeMillis);
        this.timeMillis = timeMillis;
    }

    @Override
    public void handleEnded() {
        onEnded.fire();
    }

    @Override
    public void handleSkipClicked(DtoShakaPlayer.SkipClickedEventWrapper event) {
        onSkipClicked.fire(new SkipClickedEvent(event.isForward(), event.getPlaybackTimeMillis()));
    }


    public void jumpTo(long timeMillis) {
        this.timeMillis = timeMillis;
        clientObjectChannel.jumpTo(timeMillis);
    }

    public long getTime() {
        return timeMillis;
    }

    public void setUrls(String hlsUrl, String dashUrl) {
        this.timeMillis = 0;
        this.hlsUrl = hlsUrl;
        this.dashUrl = dashUrl;
        clientObjectChannel.setUrls(hlsUrl, dashUrl);
    }

    public String getHlsUrl() {
        return hlsUrl;
    }

    public void setHlsUrl(String hlsUrl) {
        setUrls(hlsUrl, dashUrl);
    }

    public String getDashUrl() {
        return dashUrl;
    }

    public void setDashUrl(String dashUrl) {
        setUrls(hlsUrl, dashUrl);
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
        clientObjectChannel.setPosterImageUrl(posterImageUrl);
    }

    public PosterImageSize getPosterImageSize() {
        return posterImageSize;
    }

    public void setPosterImageSize(PosterImageSize posterImageSize) {
        this.posterImageSize = posterImageSize;
        clientObjectChannel.setPosterImageSize(posterImageSize);
    }

    public int getTimeUpdateEventThrottleMillis() {
        return timeUpdateEventThrottleMillis;
    }

    public void setTimeUpdateEventThrottleMillis(int timeUpdateEventThrottleMillis) {
        if (clientObjectChannel.isRendered()) {
            throw new IllegalStateException("This cannot be set after rendering the player!");
        }
        this.timeUpdateEventThrottleMillis = timeUpdateEventThrottleMillis;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        clientObjectChannel.setBackgroundColor(backgroundColor.toHtmlColorString());
    }

    public TrackLabelFormat getTrackLabelFormat() {
        return trackLabelFormat;
    }

    public void setTrackLabelFormat(TrackLabelFormat trackLabelFormat) {
        if (clientObjectChannel.isRendered()) {
            throw new IllegalStateException("This cannot be set after rendering the player!");
        }
        this.trackLabelFormat = trackLabelFormat;
    }

    public boolean isVideoDisabled() {
        return videoDisabled;
    }

    public void setVideoDisabled(boolean videoDisabled) {
        this.videoDisabled = videoDisabled;
        clientObjectChannel.setVideoDisabled(videoDisabled);
    }

    public void selectAudioLanguage(String language) {
        selectAudioLanguage(language, null);
    }

    public void selectAudioLanguage(String language, String role) {
        this.audioLanguage = language;
        clientObjectChannel.selectAudioLanguage(language, role);
    }

    public boolean isBigPlayButtonEnabled() {
        return bigPlayButtonEnabled;
    }

    public void setBigPlayButtonEnabled(boolean bigPlayButtonEnabled) {
        this.bigPlayButtonEnabled = bigPlayButtonEnabled;
        clientObjectChannel.setBigPlayButtonEnabled(bigPlayButtonEnabled);
    }

    public int getControlFadeDelaySeconds() {
        return controlFadeDelaySeconds;
    }

    public void setControlFadeDelaySeconds(int controlFadeDelaySeconds) {
        this.controlFadeDelaySeconds = controlFadeDelaySeconds;
        clientObjectChannel.setControlFadeDelaySeconds(controlFadeDelaySeconds);
    }


    public List<ControlPanelElementType> getControlPanelElements() {
        return controlPanelElements;
    }

    public void setControlPanelElements(List<ControlPanelElementType> controlPanelElements) {
        if (clientObjectChannel.isRendered()) {
            throw new IllegalStateException("This cannot be set after rendering the player!");
        }
        this.controlPanelElements = controlPanelElements;
    }

    public void play() {
        clientObjectChannel.play();
    }

    public void pause() {
        clientObjectChannel.pause();
    }
}
