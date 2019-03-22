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
import {LiveStreamPlayer} from "./LiveStreamPlayer";
import {UiComponent} from "../UiComponent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiLiveStreamComPlayerConfig} from "../../generated/UiLiveStreamComPlayerConfig";

export class UiLiveStreamComPlayer extends UiComponent<UiLiveStreamComPlayerConfig> implements LiveStreamPlayer {

	private playing: boolean = false;
	private $wrapper: JQuery;
	private player: Element;

	constructor(config: UiLiveStreamComPlayerConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$wrapper = $('<div class="livestreamcom-player-wrapper">');
	}

	play(url: string, streamName?: string) {
		this.playing = true;
		this.player = $(`<iframe style="width:100%;height:100%" src="${url}" frameborder="0" scrolling="no"></iframe>`)
			.appendTo(this.$wrapper)
			[0];
	}

	stop() {
		this.$wrapper[0].innerHTML = '';
		this.playing = false;
	}

	isPlaying(): boolean {
		return this.playing;
	}

	setVolume(volume: number): void {
		this.logger.warn("LiveStreamComPlayer: setVolume() not supported on livestream.com iframe player...")
	}

	getMainDomElement(): JQuery {
		return this.$wrapper;
	}


	public destroy(): void {
		// nothing to do
	}
}
