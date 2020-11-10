import NoSleep from 'nosleep.js';
import * as log from "loglevel";

const LOGGER: log.Logger = log.getLogger("WakeLock");
const wakeLocksByUuid: { [uuid: string]: WakeLockSentinel } = {};

export async function requestWakeLock(uuid: string) {
	try {
		let wakeLock = await (navigator as WakeLockCapableNavigator).wakeLock.request('screen');
		LOGGER.info(`WakeLock acquired: ${uuid}`);
		wakeLocksByUuid[uuid] = wakeLock;
		wakeLock.addEventListener('release', (e) => {
			LOGGER.info(`WakeLock released: ${uuid}`);
		});
		document.addEventListener('visibilitychange', async () => {
			if (wakeLocksByUuid[uuid] !== null && document.visibilityState === 'visible') {
				if (isWakeLockCapableNavigator(navigator)) {
					wakeLocksByUuid[uuid] = await navigator.wakeLock.request('screen');
				}
			}
		});
		return true;
	} catch (e) {
		LOGGER.error(`Could not acquire WakeLock: ${uuid}`, e)
		return false;
	}
}

export async function releaseWakeLock(uuid: string) {
	return wakeLocksByUuid[uuid]?.release();
}

function isWakeLockCapableNavigator(navigator: Navigator): navigator is WakeLockCapableNavigator {
	return 'wakeLock' in (navigator as WakeLockCapableNavigator) && 'request' in (navigator as WakeLockCapableNavigator).wakeLock;
}

// WakeLock polyfill using nosleep.js
if (!isWakeLockCapableNavigator(navigator)) {
	(window.navigator as WakeLockCapableNavigator).wakeLock = {
		request: () => new Promise((resolve) => {
			console.log("Requesting wakelock via nosleep.js polyfill.")
			const noSleep = new NoSleep();
			let listeners: EventListener[] = []
			const responseObject = {
				released: false,
				type: 'screen' as 'screen',
				release: async () => {
					noSleep.disable()
					listeners.forEach((fn) => fn(null))
				},
				addEventListener: (type: string, callback: EventListener) => {
					listeners.push(callback)
				},
				removeEventListener: (type: string, callback: EventListener) => {
					listeners = listeners.filter((fn) => fn !== callback)
				},
				dispatchEvent: () => true
			}

			function enableHandler() {
				document.removeEventListener('click', enableHandler, false)
				noSleep.enable()
				resolve(responseObject)
			}

			document.addEventListener('click', enableHandler, false)
		})
	};
	(window.navigator as any).wakeLock.isPolyfill = true;
}

interface WakeLockCapableNavigator extends Navigator {
	wakeLock: {
		request: (type: 'screen') => Promise<WakeLockSentinel>;
	}
}

interface WakeLockSentinel extends EventTarget {
	released: boolean;
	type: 'screen';
	release(): Promise<void>;
}