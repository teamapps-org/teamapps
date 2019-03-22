/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import * as $ from "jquery";
import {Player, polyfill, util} from "../../custom-declarations/shaka-player";
require("shaka-player");
import {UiSpinner} from "../micro-components/UiSpinner";
import {UiComponent} from "../UiComponent";
import {LiveStreamPlayer} from "./LiveStreamPlayer";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiMpegDashPlayerConfig} from "../../generated/UiMpegDashPlayerConfig";

export class UiMpegDashPlayer extends UiComponent<UiMpegDashPlayerConfig> implements LiveStreamPlayer {

	private browserSupported = Player.isBrowserSupported();

	private $main: JQuery;
	private $videoContainer: any;
	private $notSupportedMessage: any;
	private shouldBePlayingUnlessUserPressedPause: boolean;
	private $spinnerContainer: JQuery;
	private resetTimer: number = null;
	private player: Player;
	private url: string;

	constructor(config: UiMpegDashPlayerConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = $(`
<div class="UiMpegDashPlayer">
    <div class="not-supported-message">
        MPEG Dash does not seem to be supported by your browser. 
    </div>
    <div class="video-container">
    	<div class="spinner-container hidden"></div>	
        <video x-webkit-airplay="allow" autoplay controls/>
    </div>
</div>			
`);
		this.$spinnerContainer = this.$main.find('.spinner-container');
		this.$spinnerContainer.append(new UiSpinner().getMainDomElement());
		this.$notSupportedMessage = this.$main.find('.not-supported-message');
		this.$videoContainer = this.$main.find('.video-container');

		let video = this.$main.find('video')[0] as HTMLVideoElement;
		video.addEventListener('playing', () => this.onPlaying());

		this.$notSupportedMessage.toggleClass('hidden', this.browserSupported);
		this.$videoContainer.toggleClass('hidden', !this.browserSupported);
		if (this.browserSupported) {
			this.player = new Player(video);
			this.player.addEventListener('error', (e) => this.onError((e as any).detail));
		}
	}

	public play(url: string) {
		this.stop();
		this.shouldBePlayingUnlessUserPressedPause = true;
		this.url = url;
		this.retryPlaying();
	}

	private retryPlaying() {
		clearTimeout(this.resetTimer);
		this.resetTimer = null;
		let shouldRetry = this.shouldBePlayingUnlessUserPressedPause;
		this.logger.debug("retryPlaying: " + shouldRetry + " " + this.url);
		if (shouldRetry) {
			this.player.load(this.url).catch(e => this.onError(e));
			this.resetTimer = window.setTimeout(() => this.retryPlaying(), 15000); // if it did not work after 15 seconds, retry again
		}
	}

	public stop() {
		this.shouldBePlayingUnlessUserPressedPause = false;
		this.player.unload();
	}

	public isPlaying(): boolean {
		return this.shouldBePlayingUnlessUserPressedPause;
	}

	public setVolume(volume: number): void {
		this.player.getMediaElement().volume = volume;
	}

	private onPlaying() {
		clearTimeout(this.resetTimer);
		this.resetTimer = null;
		this.$spinnerContainer.toggleClass("hidden", true);
	}

	private onError(e: util.Error) {
		this.logger.warn('Error while playing video: code', e.code, 'object', JSON.stringify(e));
		this.$spinnerContainer.toggleClass("hidden", !this.shouldBePlayingUnlessUserPressedPause);
		clearTimeout(this.resetTimer);
		this.resetTimer = window.setTimeout(() => this.retryPlaying(), 5000);
	}

	getMainDomElement(): JQuery {
		return this.$main;
	}

	destroy(): void {
		this.player && this.player.destroy();
	}
}

polyfill.installAll();
