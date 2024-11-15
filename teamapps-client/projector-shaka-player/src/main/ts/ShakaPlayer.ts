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

import "shaka-player";
import "shaka-player/dist/shaka-player.ui";
import {
	DtoShakaManifest,
	DtoShakaPlayer,
	DtoShakaPlayer_EndedEvent,
	DtoShakaPlayer_ErrorLoadingEvent,
	DtoShakaPlayer_ManifestLoadedEvent,
	DtoShakaPlayer_TimeUpdateEvent,
	DtoShakaPlayerCommandHandler,
	DtoShakaPlayerEventSource,
	PosterImageSize,
	TrackLabelFormat as DtoTrackLabelFormat
} from "./generated";
import {
	AbstractLegacyComponent,
	executeWhenFirstDisplayed,
	parseHtml,
	removeClassesByFunction,
	ServerObjectChannel,
	TeamAppsEvent,
	throttle
} from "projector-client-object-api";
import Player = shaka.Player;
import UIConfiguration = shaka.extern.UIConfiguration;
import Overlay = shaka.ui.Overlay;
import ManifestConfiguration = shaka.extern.ManifestConfiguration;
import ManifestParser = shaka.extern.ManifestParser;
import PlayerInterface = shaka.extern.ManifestParser.PlayerInterface;
import TrackLabelFormat = shaka.ui.Overlay.TrackLabelFormat;
import Manifest = shaka.extern.Manifest;

export class ShakaPlayer extends AbstractLegacyComponent<DtoShakaPlayer> implements DtoShakaPlayerCommandHandler, DtoShakaPlayerEventSource {

	public readonly onManifestLoaded: TeamAppsEvent<DtoShakaPlayer_ManifestLoadedEvent> = new TeamAppsEvent();       
	public readonly onTimeUpdate: TeamAppsEvent<DtoShakaPlayer_TimeUpdateEvent> = new TeamAppsEvent();
	public readonly onEnded: TeamAppsEvent<DtoShakaPlayer_EndedEvent> = new TeamAppsEvent();
	public readonly onErrorLoading: TeamAppsEvent<DtoShakaPlayer_ErrorLoadingEvent> = new TeamAppsEvent();

	private $componentWrapper: HTMLElement;
	private $video: HTMLVideoElement;
	private player: Player;

	private ui: Overlay;

	constructor(config: DtoShakaPlayer, serverObjectChannel: ServerObjectChannel) {
		super(config);

		console.log(config.hlsUrl, config.dashUrl, config.posterImageUrl)

		this.$componentWrapper = parseHtml(
			`<div class="UiShakaPlayer">
				<video id="video" ></video>
			</div>`);

		this.$video = this.$componentWrapper.querySelector(":scope video");
		this.$video.addEventListener("timeupdate", throttle(e => this.onTimeUpdate.fire({timeMillis: this.$video.currentTime * 1000}), config.timeUpdateEventThrottleMillis))
		this.$video.addEventListener("ended", e => this.onEnded.fire({}))

		this.setPosterImageUrl(config.posterImageUrl);
		this.setPosterImageSize(config.posterImageSize);
		this.setBackgroundColor(config.backgroundColor);

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			this.player = new shaka.Player(this.$video);
			this.reconfigurePlayer();
			this.player.addEventListener('error', () => this.onErrorLoading.fire({}));
			this.player.addEventListener('streaming', () => {
				this.reconfigureUi();
				this.setTime(this.config.timeMillis);
				this.onManifestLoaded.fire({manifest: this.createUiManifest(this.player.getManifest())});
			});

			this.ui = new shaka.ui.Overlay(this.player, this.$componentWrapper, this.$video);
			const uiConfig: Partial<UIConfiguration> = {
				addBigPlayButton: true,
				controlPanelElements: [
					"play_pause",
					"time_and_duration",
					"spacer",
					"mute",
					"volume",
					"fullscreen",
					"overflow_menu",
				],
				doubleClickForFullscreen: true,
				enableFullscreenOnRotation: false,
				enableKeyboardPlaybackControls: true,
				fadeDelay: 0,
				forceLandscapeOnFullscreen: true,
				overflowMenuButtons: this.getOverflowMenuButtonsConfig(),
				seekBarColors: undefined,
				showUnbufferedStart: false,
				trackLabelFormat: config.trackLabelFormat == DtoTrackLabelFormat.LABEL ? TrackLabelFormat.LABEL
					: config.trackLabelFormat == DtoTrackLabelFormat.LANGUAGE ? TrackLabelFormat.LANGUAGE
						: config.trackLabelFormat == DtoTrackLabelFormat.ROLE ? TrackLabelFormat.ROLE
							: config.trackLabelFormat == DtoTrackLabelFormat.LANGUAGE_ROLE ? TrackLabelFormat.LANGUAGE_ROLE
								: undefined,
				volumeBarColors: undefined,
				addSeekBar: true
			};
			this.ui.configure(uiConfig as UIConfiguration);
		});

		this.setUrls(config.hlsUrl, config.dashUrl);
	}

	private createUiManifest(manifest: Manifest): DtoShakaManifest {
		return {
			variants: manifest.variants.map(v => {
				return {
					id: v.id,
					audio: {
						id: v.audio.id,
						originalId: v.audio.originalId,
						mimeType: v.audio.mimeType,
						codecs: v.audio.codecs,
						bandwidth: v.audio.bandwidth,
						label: v.audio.label,
						roles: v.audio.roles,
						language: v.audio.language,
						channelsCount: v.audio.channelsCount,
						audioSamplingRate: v.audio.audioSamplingRate
					},
					video: {
						id: v.video.id,
						originalId: v.video.originalId,
						mimeType: v.video.mimeType,
						codecs: v.video.codecs,
						bandwidth: v.video.bandwidth,
						label: v.video.label,
						roles: v.video.roles,
						frameRate: v.video.frameRate,
						pixelAspectRatio: v.video.pixelAspectRatio,
						width: v.video.width,
						height: v.video.height
					},
					bandwidth: v.bandwidth
				}
			})
		};
	}

	private reconfigurePlayer() {
		this.player.configure({
			manifest: {
				disableVideo: this.config.videoDisabled // will advice the manifest parser to set all videos to null. Note that the quality selection button must be removed!
			},
			preferredAudioLanguage: this.config.preferredAudioLanguage
		} );
	}

	private reconfigureUi() {
		this.ui.configure({
			overflowMenuButtons: this.getOverflowMenuButtonsConfig()
		} as Partial<UIConfiguration>);
	}

	private getOverflowMenuButtonsConfig() {
		let manifest = this.player.getManifest();
		const overflowButtons = [
			"playback_rate",
			"captions",
			// "picture_in_picture",
		];
		if (manifest != null && manifest.variants.some(v => v.video != null)) {
			overflowButtons.unshift("quality");
		}
		if (manifest != null && manifest.variants.some(v => v.language && v.language !== "und")) {
			overflowButtons.unshift("language");
		}
		return overflowButtons;
	}

	public doGetMainElement(): HTMLElement {
		return this.$componentWrapper;
	}

	public setTime(timeMillis: number) {
		this.$video.currentTime = timeMillis / 1000;
	}

	public selectAudioLanguage(language: string, role: string): any {
		this.config.preferredAudioLanguage = language;
		this.player.selectAudioLanguage(language, role);
	}

	setPosterImageUrl(posterImageUrl: string) {
		this.config.posterImageUrl = posterImageUrl;
		this.$video.poster = posterImageUrl;
	}
	setPosterImageSize(posterImageSize: PosterImageSize) {
		this.config.posterImageSize = posterImageSize;
		removeClassesByFunction(this.$video.classList, className => className.startsWith("poster-"));
		this.$video.classList.add(`poster-${this.config.posterImageSize}`)
	}
	setBackgroundColor(backgroundColor: string) {
		this.$componentWrapper.style.backgroundColor = backgroundColor;
	}
	setVideoDisabled(videoDisabled: boolean) {
		this.config.videoDisabled = videoDisabled;
		this.reconfigurePlayer();
	}

	public onResize(): void {

	}

	public destroy(): void {
	}

	@executeWhenFirstDisplayed()
	async setUrls(hlsUrl: string, dashUrl: string): Promise<void> {
		const support = await shaka.Player.probeSupport();
		let url = support.manifest.mpd && dashUrl ? dashUrl : hlsUrl;
		console.log(url)
		try {
			await this.player.load(url);
			console.log(this.player.getConfiguration().preferredAudioLanguage)
		} catch (e) {
			console.error(e);
			this.onErrorLoading.fire({});
		}
	}

	static setDistinctManifestAudioTracksFixEnabled(enabled: boolean) {
		const dashParserFactory = enabled ? () => new DistinctAudioTracksManifestParserDecorator(new shaka.dash.DashParser()) : () => new shaka.dash.DashParser();
		shaka.media.ManifestParser.registerParserByExtension('mpd', dashParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/dash+xml', dashParserFactory);
		shaka.media.ManifestParser.registerParserByMime('video/vnd.mpeg.dash.mpd', dashParserFactory);
		const hlsParserFactory = enabled ? () => new DistinctAudioTracksManifestParserDecorator(new shaka.hls.HlsParser()) : () => new shaka.hls.HlsParser();
		shaka.media.ManifestParser.registerParserByExtension('m3u8', hlsParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/x-mpegurl', hlsParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/vnd.apple.mpegurl', hlsParserFactory);
	}
}

class DistinctAudioTracksManifestParserDecorator implements ManifestParser {
	constructor(private delegate: ManifestParser) {
	}

	configure(config: ManifestConfiguration) {
		return this.delegate.configure(config);
	}

	async start(uri: string, playerInterface: PlayerInterface) {
		let manifest = await this.delegate.start(uri, playerInterface);
		// manifest.variants.forEach((variant, i) => variant.language = variant.language + i)
		manifest.variants.forEach((variant, i) => variant.audio.roles.push("role" + i));
		return manifest;
	}

	async stop() {
		return this.delegate.stop();
	}

	onExpirationUpdated(sessionId: string, expiration: number): any {
		return this.delegate.onExpirationUpdated(sessionId, expiration);
	}

	update(): any {
		return this.delegate.update();
	}
}

shaka.polyfill.installAll();

