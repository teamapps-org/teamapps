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
/*-
MIT License

Copyright (c) 2017 Nimiq, danimoh
Copyright (c) 2019 Yann Massard (yann.massard@teamapps.org) - migration to typescript

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import QrWorker = require('worker-loader!./worker');

type Rect = { x: number; width: number; y: number; height: number };
type ImageSource = HTMLCanvasElement | HTMLVideoElement | HTMLImageElement | URL | File | string;
type ImageSourceElement = HTMLCanvasElement | HTMLVideoElement | HTMLImageElement;

export class QrScanner {

	private $video: HTMLVideoElement;
	private $canvas: HTMLCanvasElement;

	private onDecode: (s: string) => void;

	private active = false;
	private paused = false;

	private sourceRect: Rect;
	private qrWorker: Worker;


	constructor(video: HTMLVideoElement, onDecode: (s: string) => void, canvasSize = 400) {
		this.$video = video;
		this.$canvas = document.createElement('canvas');
		this.onDecode = onDecode;

		this.$canvas.width = canvasSize;
		this.$canvas.height = canvasSize;
		this.sourceRect = {
			x: 0,
			y: 0,
			width: canvasSize,
			height: canvasSize
		};

		this._onCanPlay = this._onCanPlay.bind(this);
		this._onPlay = this._onPlay.bind(this);
		this._onVisibilityChange = this._onVisibilityChange.bind(this);

		this.$video.addEventListener('canplay', this._onCanPlay);
		this.$video.addEventListener('play', this._onPlay);
		document.addEventListener('visibilitychange', this._onVisibilityChange);

		this.qrWorker = new QrWorker();
	}

	destroy() {
		this.$video.removeEventListener('canplay', this._onCanPlay);
		this.$video.removeEventListener('play', this._onPlay);
		document.removeEventListener('visibilitychange', this._onVisibilityChange);

		this.stop();
		this.qrWorker.postMessage({
			type: 'close'
		});
	}

	/* async */
	start(deviceId: string = null) {
		if (this.active && !this.paused) {
			return Promise.resolve();
		}
		if (window.location.protocol !== 'https:') {
			// warn but try starting the camera anyways
			console.warn('The camera stream is only accessible if the page is transferred via https.');
		}
		this.active = true;
		this.paused = false;
		if (document.hidden) {
			// camera will be started as soon as tab is in foreground
			return Promise.resolve();
		}
		if (this.$video.srcObject) {
			// camera stream already/still set
			this.$video.play();
			return Promise.resolve();
		}

		let facingMode = 'environment';
		return this.getCameraStream(deviceId, 'environment', true)
			.catch(() => {
				// we (probably) don't have an environment camera
				facingMode = 'user';
				return this.getCameraStream(deviceId); // throws if camera is not accessible (e.g. due to not https)
			})
			.then(stream => {
				this.$video.srcObject = stream;
				this.setVideoMirror(facingMode);
			})
			.catch(e => {
				this.active = false;
				throw e;
			});
	}

	stop() {
		this.pause();
		this.active = false;
	}

	pause() {
		this.paused = true;
		if (!this.active) {
			return;
		}
		this.$video.pause();
		const track = this.$video.srcObject && (this.$video.srcObject as MediaStream).getTracks()[0];
		if (!track) return;
		track.stop();
		this.$video.srcObject = null;
	}

	/* async */
	static scanImage(imageOrFileOrUrl: ImageSource, sourceRect: Rect = null, worker: QrWorker = null, canvas: HTMLCanvasElement = null, fixedCanvasSize = false,
	                 alsoTryWithoutSourceRect = false) {
		let createdNewWorker = false;
		let promise = new Promise((resolve, reject) => {
			if (!worker) {
				worker = new QrWorker();
				createdNewWorker = true;
				worker.postMessage({type: 'inversionMode', data: 'both'}); // scan inverted color qr codes too
			}
			let timeout: number,
				onMessage: EventListener,
				onError: EventListener;
			onMessage = (event: MessageEvent) => {
				if (event.data.type !== 'qrResult') {
					return;
				}
				worker.removeEventListener('message', onMessage);
				worker.removeEventListener('error', onError);
				clearTimeout(timeout);
				if (event.data.data !== null) {
					resolve(event.data.data);
				} else {
					reject('QR code not found.');
				}
			};
			onError = (e: ErrorEvent) => {
				worker.removeEventListener('message', onMessage);
				worker.removeEventListener('error', onError);
				clearTimeout(timeout);
				const errorMessage = !e ? 'Unknown Error' : (e.message || e);
				reject('Scanner error: ' + errorMessage);
			};
			worker.addEventListener('message', onMessage);
			worker.addEventListener('error', onError);
			timeout = self.setTimeout(() => onError(new ErrorEvent('timeout', {message: 'timeout'})), 3000);
			_loadImage(imageOrFileOrUrl).then(image => {
				const imageData = _getImageData(image, sourceRect, canvas, fixedCanvasSize);
				worker.postMessage({
					type: 'decode',
					data: imageData
				}, [imageData.data.buffer]);
			}).catch(onError);
		});

		if (sourceRect && alsoTryWithoutSourceRect) {
			promise = promise.catch(() => QrScanner.scanImage(imageOrFileOrUrl, null, worker, canvas, fixedCanvasSize));
		}

		promise = promise.finally(() => {
			if (!createdNewWorker) return;
			worker.postMessage({
				type: 'close'
			});
		});

		return promise;
	}

	setGrayscaleWeights(red: number, green: number, blue: number, useIntegerApproximation = true) {
		this.qrWorker.postMessage({
			type: 'grayscaleWeights',
			data: {red, green, blue, useIntegerApproximation}
		});
	}

	setInversionMode(inversionMode: "original" | "invert" | "both") {
		this.qrWorker.postMessage({
			type: 'inversionMode',
			data: inversionMode
		});
	}

	private _onCanPlay() {
		this._updateSourceRect();
		this.$video.play();
	}

	private _onPlay() {
		this._updateSourceRect();
		this._scanFrame();
	}

	private _onVisibilityChange() {
		if (document.hidden) {
			this.pause();
		} else if (this.active) {
			this.start();
		}
	}

	private _updateSourceRect() {
		const smallestDimension = Math.min(this.$video.videoWidth, this.$video.videoHeight);
		const sourceRectSize = Math.round(2 / 3 * smallestDimension);
		this.sourceRect.width = this.sourceRect.height = sourceRectSize;
		this.sourceRect.x = (this.$video.videoWidth - sourceRectSize) / 2;
		this.sourceRect.y = (this.$video.videoHeight - sourceRectSize) / 2;
	}

	private _scanFrame() {
		if (!this.active || this.$video.paused || this.$video.ended) return false;
		// using requestAnimationFrame to avoid scanning if tab is in background
		requestAnimationFrame(() => {
			QrScanner.scanImage(this.$video, this.sourceRect, this.qrWorker, this.$canvas, true)
				.then(this.onDecode, error => {
					if (this.active && error !== 'QR code not found.') {
						console.error(error);
					}
				})
				.then(() => this._scanFrame());
		});
	}

	private getCameraStream(deviceId: string, facingMode: any = null, exact = false) {
		const constraintsToTry: MediaTrackConstraints[] = [{
			width: {min: 1024}
		}, {
			width: {min: 768}
		}, {}];

		if (deviceId) {
			constraintsToTry.forEach(c => c.deviceId = deviceId);
		}

		if (facingMode) {
			if (exact) {
				facingMode = {exact: facingMode};
			}
			constraintsToTry.forEach((constraint: MediaTrackConstraints) => constraint.facingMode = facingMode);
		}
		return this.getMatchingCameraStream(constraintsToTry);
	}

	private async getMatchingCameraStream(constraintsToTry: any): Promise<MediaStream> {
		if (constraintsToTry.length === 0) {
			return Promise.reject('Camera not found.');
		}
		try {
			return await navigator.mediaDevices.getUserMedia({
				video: constraintsToTry.shift()
			});
		} catch (e) {
			return await this.getMatchingCameraStream(constraintsToTry);
		}
	}

	private setVideoMirror(facingMode: any) {
		// in user facing mode mirror the video to make it easier for the user to position the QR code
		const scaleFactor = facingMode === 'user' ? -1 : 1;
		this.$video.style.transform = 'scaleX(' + scaleFactor + ')';
	}

}

async function hasCamera() {
	return await navigator.mediaDevices.enumerateDevices()
		.then(devices => devices.some(device => device.kind === 'videoinput'))
		.catch(() => false);
}

function _getImageData(image: ImageSourceElement, sourceRect: Rect = null, canvas: HTMLCanvasElement = null, fixedCanvasSize = false) {
	canvas = canvas || document.createElement('canvas');
	const sourceRectX = sourceRect && sourceRect.x ? sourceRect.x : 0;
	const sourceRectY = sourceRect && sourceRect.y ? sourceRect.y : 0;
	const sourceRectWidth = sourceRect && sourceRect.width ? sourceRect.width : image.width || (image as HTMLVideoElement).videoWidth;
	const sourceRectHeight = sourceRect && sourceRect.height ? sourceRect.height : image.height || (image as HTMLVideoElement).videoHeight;
	if (!fixedCanvasSize && (canvas.width !== sourceRectWidth || canvas.height !== sourceRectHeight)) {
		canvas.width = sourceRectWidth;
		canvas.height = sourceRectHeight;
	}
	const context = canvas.getContext('2d', {alpha: false});
	context.imageSmoothingEnabled = false; // gives less blurry images
	context.drawImage(image, sourceRectX, sourceRectY, sourceRectWidth, sourceRectHeight, 0, 0, canvas.width, canvas.height);
	return context.getImageData(0, 0, canvas.width, canvas.height);
}

function _loadImage(imageOrFileOrUrl: ImageSource): Promise<ImageSourceElement> {
	if (imageOrFileOrUrl instanceof HTMLCanvasElement || imageOrFileOrUrl instanceof HTMLVideoElement) {
	return Promise.resolve(imageOrFileOrUrl);
} else if (imageOrFileOrUrl instanceof HTMLImageElement) {
	return _awaitImageLoad(imageOrFileOrUrl).then(() => imageOrFileOrUrl);
} else if (imageOrFileOrUrl instanceof File || imageOrFileOrUrl instanceof URL
	|| typeof (imageOrFileOrUrl) === 'string') {
	const image = new Image();
	if (imageOrFileOrUrl instanceof File) {
		image.src = URL.createObjectURL(imageOrFileOrUrl);
	} else {
		image.src = imageOrFileOrUrl.toString();
	}
	return _awaitImageLoad(image).then(() => {
		if (imageOrFileOrUrl instanceof File) {
			URL.revokeObjectURL(image.src);
		}
		return image;
	});
} else {
	return Promise.reject('Unsupported image type.');
}
}

function _awaitImageLoad(image: HTMLImageElement) {
	return new Promise<void>((resolve, reject) => {
		if (image.complete && image.naturalWidth !== 0) {
			// already loaded
			resolve();
		} else {
			let onLoad: EventListener, onError: EventListener;
			onLoad = () => {
				image.removeEventListener('load', onLoad);
				image.removeEventListener('error', onError);
				resolve();
			};
			onError = () => {
				image.removeEventListener('load', onLoad);
				image.removeEventListener('error', onError);
				reject('Image load error');
			};
			image.addEventListener('load', onLoad);
			image.addEventListener('error', onError);
		}
	});
}
