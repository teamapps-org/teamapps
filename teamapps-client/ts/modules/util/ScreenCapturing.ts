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
window.addEventListener('message', function (event) {
	if (event.origin !== window.location.origin) {
		return;
	}

	// "cancel" button is clicked
	if (event.data === 'PermissionDeniedError') {
		chromeMediaSource = event.data;
		if (screenCallback) return screenCallback(event.data);
		else throw new Error(event.data);
	}

	// extension notified his presence
	if (event.data === 'screen-sharing-extension-loaded') {
		chromeMediaSource = 'desktop';
	}

	// extension shared temp sourceId
	if (event.data.sourceId && screenCallback) {
		sourceId = event.data.sourceId;
		screenCallback(event.data.sourceId, event.data.canRequestAudioTrack === true);
	}
});

// global variables
var chromeMediaSource = 'screen';
var sourceId: string;
var screenCallback: Function;

// this method can be used to check if chrome extension is installed & enabled.
export async function checkChromeExtensionAvailable(): Promise<boolean> {
	if (chromeMediaSource === 'desktop') {
		return true;
	}

	// ask extension if it is available
	window.postMessage('are-you-there', '*');

	return new Promise<boolean>((resolve, reject) => {
		setTimeout(function () {
			resolve(chromeMediaSource !== 'screen');
		}, 1000);
	});
}

export async function getSourceId(): Promise<{ sourceId: string, canRequestAudioTrack: boolean }> {
	return new Promise<{ sourceId: string, canRequestAudioTrack: boolean }>((resolve, reject) => {
		window.postMessage('get-sourceId', '*');
		screenCallback = (sourceId: any, canRequestAudioTrack: boolean) => {
			resolve({sourceId, canRequestAudioTrack});
		};
	});
}

export async function getCustomSourceId(sourceTypes: string[]): Promise<{ sourceId: string, canRequestAudioTrack: boolean }> {
	return new Promise<{ sourceId: string, canRequestAudioTrack: boolean }>((resolve, reject) => {
		window.postMessage({'get-custom-sourceId': sourceTypes}, '*');
		screenCallback = (sourceId: any, canRequestAudioTrack: boolean) => {
			resolve({sourceId, canRequestAudioTrack});
		};
	});
}

async function getSourceIdWithAudio(): Promise<{ sourceId: string, canRequestAudioTrack: boolean }> {
	return new Promise<{ sourceId: string, canRequestAudioTrack: boolean }>((resolve, reject) => {
		window.postMessage('get-sourceId-with-audio', '*');
		screenCallback = (sourceId: any, canRequestAudioTrack: boolean) => {
			if (sourceId == "PermissionDeniedError") {
				reject(sourceId);
			} else {
				resolve({sourceId, canRequestAudioTrack});
			}
		};
	});
}

export const isFirefox = typeof (window as any).InstallTrigger !== 'undefined';
export const isOpera = !!(window as any).opera || navigator.userAgent.indexOf(' OPR/') >= 0;
export const isChrome = !!(window as any).chrome && !isOpera;

export function getChromeExtensionStatus(callback: Function, extensionid = 'ajhifddimkapgcifgcodmmfdlknahffk') {
	if (!isChrome) return callback('not-chrome');

	const image = document.createElement('img');
	image.src = 'chrome-extension://' + extensionid + '/icon.png';
	image.onload = function () {
		chromeMediaSource = 'screen';
		window.postMessage('are-you-there', '*');
		setTimeout(function () {
			if (chromeMediaSource === 'screen') {
				callback('installed-disabled');
			} else callback('installed-enabled');
		}, 2000);
	};
	image.onerror = function () {
		callback('not-installed');
	};
}

export async function getScreenConstraintsWithAudio() {
	return getScreenConstraints(true);
}

// this function explains how to use above methods/objects
export async function getScreenConstraints(captureSourceIdWithAudio: boolean = false): Promise<object> {
	if (isFirefox) {
		return {
			mozMediaSource: 'window',
			mediaSource: 'window'
		};
	}

	// this statement defines getUserMedia constraints
	// that will be used to capture content of screen
	const screenConstraints: any = {
		mandatory: {
			chromeMediaSource: chromeMediaSource,
			maxWidth: screen.width > 1920 ? screen.width : 1920,
			maxHeight: screen.height > 1080 ? screen.height : 1080
		},
		optional: []
	};

	// this statement verifies chrome extension availability
	// if installed and available then it will invoke extension API
	// otherwise it will fallback to command-line based screen capturing API
	if (chromeMediaSource === 'desktop') {
		if (captureSourceIdWithAudio) {
			const sourceIdWithAudio = await getSourceIdWithAudio();
			screenConstraints.mandatory.chromeMediaSourceId = sourceIdWithAudio.sourceId;
			if (sourceIdWithAudio.canRequestAudioTrack) {
				screenConstraints.canRequestAudioTrack = true;
			}
			return screenConstraints;
		} else {
			const sourceId = await getSourceId();
			screenConstraints.mandatory.chromeMediaSourceId = sourceId.sourceId;
			return screenConstraints;
		}
	} else {
		// now invoking native getUserMedia API
		return screenConstraints;
	}
}
