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
import NoSleep from 'nosleep.js';
import * as log from "loglevel";

const LOGGER: log.Logger = log.getLogger("WakeLock");

interface WakeLockApi {
	request: (type: 'screen') => Promise<WakeLockSentinel>;
	isPolyfill?: boolean;
}

interface WakeLockSentinel extends EventTarget {
	released: boolean;
	type: 'screen';

	release(): Promise<void>;
}

let wakeLockApi: WakeLockApi;

let counter = 0;

// WakeLock polyfill using nosleep.js
if ('wakeLock' in navigator && 'request' in (navigator as any).wakeLock) {
	wakeLockApi = (navigator as any).wakeLock;
} else {
	wakeLockApi = {
		request: () => new Promise((resolve, reject) => {
			const c = counter++;
			LOGGER.info("Requesting wakelock via nosleep.js polyfill.")
			const noSleep = new NoSleep();
			(noSleep as any).noSleepVideo.setAttribute("title", "");

			const enableHandler = async () => {
				document.removeEventListener('click', enableHandler, false);
				try {
					await noSleep.enable();
					resolve(responseObject);
				} catch (e) {
					reject(e);
				}
			}

			let listeners: EventListener[] = []
			const responseObject = {
				released: false,
				type: 'screen' as 'screen',
				release: async () => {
					noSleep.disable();
					reject("released before clicked");
					document.removeEventListener('click', enableHandler, false);

					// HACK: this is needed to make iOS not show the player on the lock screen!!
					document.body.append((noSleep as any).noSleepVideo);
					(noSleep as any).noSleepVideo.remove();
					// end HACK

					listeners.forEach((fn) => fn(null));
				},
				addEventListener: (type: string, callback: EventListener) => {
					listeners.push(callback)
				},
				removeEventListener: (type: string, callback: EventListener) => {
					listeners = listeners.filter((fn) => fn !== callback)
				},
				dispatchEvent: () => true,
				c
			}

			document.addEventListener('click', enableHandler, false);
		}),
		isPolyfill: true
	};
}

const wakeLocksByUuid: {
	[uuid: string]: {
		wakeLockPromise: Promise<WakeLockSentinel>,
		visibilityListener: (event: Event) => any
	}
} = {};

export async function requestWakeLock(uuid: string): Promise<boolean> {
	LOGGER.info(`requestWakeLock (${uuid})`)
	try {
		let visibilityListener = async () => {
			if (document.visibilityState === 'visible') {
				LOGGER.debug(`Visibility changed to visible. (${uuid})`);
				wakeLocksByUuid[uuid].wakeLockPromise = doRequestWakeLock(uuid);
			} else if (document.visibilityState === 'hidden') {
				LOGGER.debug(`Visibility changed to hidden. Releasing wakeLock. (${uuid})`);
				wakeLocksByUuid[uuid].wakeLockPromise.then(w => w.release());
			}
		};
		wakeLocksByUuid[uuid] = {
			wakeLockPromise: doRequestWakeLock(uuid),
			visibilityListener
		};
		document.addEventListener('visibilitychange', visibilityListener);
		return true;
	} catch (e) {
		LOGGER.error(`Could not acquire WakeLock! (${uuid})`, e)
		return false;
	}
}

async function doRequestWakeLock(uuid: string) {
	let wakeLockSentinelPromise = wakeLockApi.request('screen');
	const wakeLock = await wakeLockSentinelPromise;
	LOGGER.info(`WakeLock acquired. (${uuid})`);
	wakeLock.addEventListener('release', (e) => {
		LOGGER.info(`WakeLock released. (${uuid})`);
	});
	return wakeLock;
}

export async function releaseWakeLock(uuid: string) {
	LOGGER.info(`releaseWakeLock (${uuid})`)
	let wakeLocker = wakeLocksByUuid[uuid];
	if (wakeLocker != null) {
		wakeLocker.wakeLockPromise.then(w => w.release());
		document.removeEventListener("visibilitychange", wakeLocker.visibilityListener);
	}
}
