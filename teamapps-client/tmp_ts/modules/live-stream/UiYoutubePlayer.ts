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
///<reference types="youtube"/>


import {LiveStreamPlayer} from "./LiveStreamPlayer";
import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {generateUUID, parseHtml} from "../Common";
import {DtoYoutubePlayer} from "../../generated/DtoYoutubePlayer";

export class UiYoutubePlayer extends AbstractLegacyComponent<DtoYoutubePlayer> implements LiveStreamPlayer {
	private static scriptTagAdded: boolean = false;
	private static scriptLoaded: boolean = false;
	private static commandsToInvokeWhenScripLoaded: Function[] = [];

	private playing: boolean = false;
	private $wrapper: HTMLElement;
	private $player: HTMLElement;
	private player: YT.Player;
	private playerReady: boolean;
	private commandsToInvokeWhenPlayerReady: Function[] = [];

	public static onYouTubeIframeAPIReady() {
		UiYoutubePlayer.scriptLoaded = true;
		for (let i = 0; i < UiYoutubePlayer.commandsToInvokeWhenScripLoaded.length; i++) {
			UiYoutubePlayer.commandsToInvokeWhenScripLoaded[i].call(null);
		}
		UiYoutubePlayer.commandsToInvokeWhenScripLoaded.length = 0;
	}

	private static invokeWhenScripLoaded(f: Function) {
		if (UiYoutubePlayer.scriptLoaded) {
			f.call(null);
		} else {
			UiYoutubePlayer.commandsToInvokeWhenScripLoaded.push(f);
		}
	}

	constructor(config: DtoYoutubePlayer, serverChannel: ServerChannel) {
		super(config, serverChannel);
		this.$wrapper = parseHtml("<div>");
		let elementUuid = generateUUID();
		// 1. The <iframe> (and video player) will replace this <div> tag.
		this.$player = parseHtml(`<div id="${elementUuid}">`);
		this.$wrapper.appendChild(this.$player);

		if (!UiYoutubePlayer.scriptTagAdded) {
			// 2. This code loads the IFrame Player API code asynchronously.
			const tag = document.createElement('script');
			tag.src = "//www.youtube.com/iframe_api";
			const firstScriptTag = document.getElementsByTagName('script')[0];
			firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
			UiYoutubePlayer.scriptTagAdded = true;
		}

		UiYoutubePlayer.invokeWhenScripLoaded(() => {
			this.player = new YT.Player(elementUuid, {
				height: '100%' as any as number, // the declarations are not complete here...
				width: '100%' as any as number, // the declarations are not complete here...
				events: {
					onReady: (e) => this.onPlayerReady(e),
					onStateChange: (e) => this.onPlayerStateChange(e)
				}
			})
		});
	}

	play(url: string) {
		this.playing = true;
		let protocolSeparatorIndex = url.indexOf(":");
		if (protocolSeparatorIndex != -1) {
			// make sure the video is loaded with the same protocol (http/https) the whole page is loaded.
			url = url.substr(protocolSeparatorIndex + 1);
		}
		this.invokeWhenPlayerReady(() => this.player.loadVideoByUrl(url))
	}

	stop() {
		this.invokeWhenPlayerReady(() => this.player.stopVideo());
		this.playing = false;
	}

	isPlaying(): boolean {
		return this.playing;
	}

	setVolume(volume: number): void {
		this.invokeWhenPlayerReady(() => this.player.setVolume(Math.round(100 * volume)));
	}

	private onPlayerReady(event: YT.PlayerEvent) {
		this.playerReady = true;
		for (let i = 0; i < this.commandsToInvokeWhenPlayerReady.length; i++) {
			this.commandsToInvokeWhenPlayerReady[i].call(null);
		}
		this.commandsToInvokeWhenPlayerReady.length = 0;
	}

	private invokeWhenPlayerReady(f: Function) {
		if (this.playerReady) {
			f.call(null);
		} else {
			this.commandsToInvokeWhenPlayerReady.push(f);
		}
	}

	private onPlayerStateChange(event: YT.OnStateChangeEvent) {
		if (event.data == YT.PlayerState.PLAYING) {
			console.debug("Youtube playing...")
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$wrapper;
	}

}

// global function called by the youtube api script...
(window as any).onYouTubeIframeAPIReady = function() {
	UiYoutubePlayer.onYouTubeIframeAPIReady();
};

