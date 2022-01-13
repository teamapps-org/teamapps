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

import jsQR from 'jsqr-es6';

let inversionAttempts: "dontInvert" | "onlyInvert" | "attemptBoth" | "invertFirst" = 'dontInvert';
let grayscaleWeights = {
	// weights for quick luma integer approximation (https://en.wikipedia.org/wiki/YUV#Full_swing_for_BT.601)
	red: 77,
	green: 150,
	blue: 29,
	useIntegerApproximation: true,
};

self.onmessage = event => {
	const type = event['data']['type'];
	const data = event['data']['data'];

	switch (type) {
		case 'decode':
			decode(data);
			break;
		case 'grayscaleWeights':
			setGrayscaleWeights(data);
			break;
		case 'inversionMode':
			setInversionMode(data);
			break;
		case 'close':
			// close after earlier messages in the event loop finished processing
			self.close();
			break;
	}
};

function decode(data) {
	const rgbaData = data['data'];
	const width = data['width'];
	const height = data['height'];
	const result = jsQR(rgbaData, width, height, {
		inversionAttempts: inversionAttempts,
		greyScaleWeights: grayscaleWeights,
	});
	self.postMessage({
		type: 'qrResult',
		data: result? result.data : null,
	}, undefined);
}

function setGrayscaleWeights(data) {
	// update grayscaleWeights in a closure compiler compatible fashion
	grayscaleWeights.red = data['red'];
	grayscaleWeights.green = data['green'];
	grayscaleWeights.blue = data['blue'];
	grayscaleWeights.useIntegerApproximation = data['useIntegerApproximation'];
}

function setInversionMode(inversionMode) {
	switch (inversionMode) {
		case 'original':
			inversionAttempts = 'dontInvert';
			break;
		case 'invert':
			// TODO mode 'onlyInvert' is currently broken in jsQR. Enable when fixed.
			inversionAttempts = 'attemptBoth';
			break;
		case 'both':
			inversionAttempts = 'attemptBoth';
			break;
		default:
			throw new Error('Invalid inversion mode');
	}
}
