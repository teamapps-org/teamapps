/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
declare namespace shaka  {
	let polyfill: {
		installAll: () => void
	};

	class Player {
		constructor(video: HTMLMediaElement, dependencyInjector?: (player: shaka.Player) => void);

		static isBrowserSupported(): boolean;

		addEventListener(eventType: string, handler: (event: Event) => void): void;

		load(manifestUri: string, startTime?: number, manifestParserFactory?: any): Promise<any>;

		unload(): Promise<any>;

		getMediaElement(): HTMLMediaElement;

		getTracks(): Track[];

		destroy(): Promise<any>;
	}

	namespace util {
		interface Error {
			category: number;
			code: number;
			data: any[];
			severity: number;
		}
	}

	interface Track {
		id: number;
		active: boolean;
		type: string;
		bandwidth: number;
		language: string;
		label?: string;
		kind?: string;
		width?: number;
		height?: number;
		frameRate?: number;
		mimeType?: string;
		codecs?: string;
		audioCodec?: string;
		videoCodec?: string;
		primary: boolean;
		roles: string[];
		videoId?: number;
		audioId?: number
	}
}

export = shaka;
