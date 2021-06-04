/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import "shaka-player/dist/controls.css"
import "@less/components/UiShakaPlayer.less"

import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiShakaPlayer_EndedEvent,
	UiShakaPlayer_ErrorLoadingEvent,
	UiShakaPlayer_TimeUpdateEvent,
	UiShakaPlayerCommandHandler,
	UiShakaPlayerConfig,
	UiShakaPlayerEventSource,
} from "../generated/UiShakaPlayerConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {UiPosterImageSize} from "../generated/UiPosterImageSize";
import {throttle} from "./util/throttle";
import {UiTrackLabelFormat} from "../generated/UiTrackLabelFormat";

(window as any).shaka = require("shaka-player");
(window as any).shaka = require("../../node_modules/shaka-player/dist/shaka-player.ui");

import Player = shaka.Player;
import UIConfiguration = shaka.extern.UIConfiguration;
import Overlay = shaka.ui.Overlay;
import ManifestConfiguration = shaka.extern.ManifestConfiguration;
import ManifestParser = shaka.extern.ManifestParser;
import PlayerInterface = shaka.extern.ManifestParser.PlayerInterface;
import TrackLabelFormat = shaka.ui.Overlay.TrackLabelFormat;

export class UiShakaPlayer extends AbstractUiComponent<UiShakaPlayerConfig> implements UiShakaPlayerCommandHandler, UiShakaPlayerEventSource {

	public readonly onTimeUpdate: TeamAppsEvent<UiShakaPlayer_TimeUpdateEvent> = new TeamAppsEvent(this);
	public readonly onEnded: TeamAppsEvent<UiShakaPlayer_EndedEvent> = new TeamAppsEvent(this);
	public readonly onErrorLoading: TeamAppsEvent<UiShakaPlayer_ErrorLoadingEvent> = new TeamAppsEvent(this);

	private $componentWrapper: HTMLElement;
	private $video: HTMLMediaElement;
	private player: Player;

	// private playerInitializedDeferredExecutor: DeferredExecutor = new DeferredExecutor();

	constructor(config: UiShakaPlayerConfig, context: TeamAppsUiContext) {
		super(config, context);

		console.log(config.hlsUrl, config.dashUrl, config.posterImageUrl)

		const posterImageSizeCssClass = `poster-${UiPosterImageSize[config.posterImageSize].toLowerCase()}`;
		this.$componentWrapper = parseHtml(
			`<div id="${config.id}" class="UiShakaPlayer" style="background-color: ${config.backgroundColor}">
				<video id="video" poster="${config.posterImageUrl}" class="${posterImageSizeCssClass}"></video>
			</div>`);
		this.$video = this.$componentWrapper.querySelector(":scope video");

		this.$video.addEventListener("timeupdate", throttle(e => this.onTimeUpdate.fire({timeMillis: this.$video.currentTime * 1000}), config.timeUpdateEventThrottleMillis))
		this.$video.addEventListener("ended", e => this.onEnded.fire({}))

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			this.player = new shaka.Player(this.$video);
			this.player.addEventListener('error', () => this.onErrorLoading.fire({}));

			const ui: Overlay = new shaka.ui.Overlay(this.player, this.$componentWrapper, this.$video);
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
				overflowMenuButtons: [
					"captions",
					"cast",
					"quality",
					"language",
					"picture_in_picture",
					"playback_rate",
				],
				seekBarColors: undefined,
				showUnbufferedStart: false,
				trackLabelFormat: config.trackLabelFormat == UiTrackLabelFormat.LABEL ? TrackLabelFormat.LABEL
					: config.trackLabelFormat == UiTrackLabelFormat.LANGUAGE ? TrackLabelFormat.LANGUAGE
						: config.trackLabelFormat == UiTrackLabelFormat.ROLE ? TrackLabelFormat.ROLE
							: config.trackLabelFormat == UiTrackLabelFormat.LANGUAGE_ROLE ? TrackLabelFormat.LANGUAGE_ROLE
								: undefined,
				volumeBarColors: undefined,
				addSeekBar: true
			};
			ui.configure(uiConfig as UIConfiguration);
		});

		this.setUrls(config.hlsUrl, config.dashUrl);
	}

	public doGetMainElement(): HTMLElement {
		return this.$componentWrapper;
	}

	public jumpTo(timeMillis: number) {
		this.$video.currentTime = timeMillis / 1000;
	}

	public onResize(): void {

	}

	public destroy(): void {
	}

	@executeWhenFirstDisplayed(true)
	async setUrls(hlsUrl: string, dashUrl: string) {
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

	static setDistinctManifestLanguageFixEnabled(enabled: boolean) {
		const dashParserFactory = enabled ? () => new DistinctLanguageManifestParserDecorator(new shaka.dash.DashParser()) : () => new shaka.dash.DashParser();
		shaka.media.ManifestParser.registerParserByExtension('mpd', dashParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/dash+xml', dashParserFactory);
		shaka.media.ManifestParser.registerParserByMime('video/vnd.mpeg.dash.mpd', dashParserFactory);
		const hlsParserFactory = enabled ? () => new DistinctLanguageManifestParserDecorator(new shaka.hls.HlsParser()) : () => new shaka.hls.HlsParser();
		shaka.media.ManifestParser.registerParserByExtension('m3u8', hlsParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/x-mpegurl', hlsParserFactory);
		shaka.media.ManifestParser.registerParserByMime('application/vnd.apple.mpegurl', hlsParserFactory);
	}
}

class DistinctLanguageManifestParserDecorator implements ManifestParser {
	constructor(private delegate: ManifestParser) {
	}

	configure(config: ManifestConfiguration) {
		return this.delegate.configure(config);
	}

	async start(uri: string, playerInterface: PlayerInterface) {
		let manifest = await this.delegate.start(uri, playerInterface);
		manifest.variants.forEach((variant, i) => variant.language = variant.language + i)
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

TeamAppsUiComponentRegistry.registerComponentClass("UiShakaPlayer", UiShakaPlayer);
