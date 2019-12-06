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

import {LiveStreamPlayer} from "./LiveStreamPlayer";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiLiveStreamComPlayerConfig} from "../../generated/UiLiveStreamComPlayerConfig";
import {parseHtml} from "../Common";

export class UiLiveStreamComPlayer extends AbstractUiComponent<UiLiveStreamComPlayerConfig> implements LiveStreamPlayer {

	private playing: boolean = false;
	private $wrapper: HTMLElement;
	private player: Element;

	constructor(config: UiLiveStreamComPlayerConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$wrapper = parseHtml('<div class="livestreamcom-player-wrapper">');
	}

	play(url: string, streamName?: string) {
		this.playing = true;
		this.player = parseHtml(`<iframe style="width:100%;height:100%" src="${url}" frameborder="0" scrolling="no"></iframe>`);
		this.$wrapper.appendChild(this.player);
	}

	stop() {
		this.$wrapper.innerHTML = '';
		this.playing = false;
	}

	isPlaying(): boolean {
		return this.playing;
	}

	setVolume(volume: number): void {
		this.logger.warn("LiveStreamComPlayer: setVolume() not supported on livestream.com iframe player...")
	}

	doGetMainElement(): HTMLElement {
		return this.$wrapper;
	}

}
