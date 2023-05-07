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

import {ProgressIndicator} from "./ProgressIndicator";
import {generateUUID, parseSvg} from "teamapps-client-core";

export class ProgressCircle implements ProgressIndicator {
	private $mainDomElement: HTMLElement;
	private $circle: HTMLElement;
	private $caption: HTMLElement;
	private circleRadius: any;
	private svgCircleRadius: number;
	private caption: string | undefined;

	/**
	 * @param circleRadius in px
	 * @param initialProgress Between 0 and 1
	 * @param initialCaption
	 * @param circleStrokeWidth
	 * @param transitionTime
	 */
	constructor(initialProgress: number, {
		circleRadius = 47,
		circleStrokeWidth = 4,
		transitionTime = 700
	}) {
		this.circleRadius = circleRadius;
		let uuid = generateUUID();
		let size = circleRadius * 2;
		let svgCircleStrokeWidth = circleStrokeWidth * 100 / size;
		let svgCircleRadius = 50 - svgCircleStrokeWidth / 2;
		let textSize = 0.25 * circleRadius + 24;
		this.svgCircleRadius = svgCircleRadius;
		this.$mainDomElement = parseSvg(`
                <svg id="c-${uuid}" class="ProgressCircle" width="${size}" height="${size}" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                 <g>
                  <circle class="countdown-circle-background" r="${this.svgCircleRadius}" cy="50" cx="50" stroke-width="${svgCircleStrokeWidth}" stroke="#dddddd" fill="none"></circle>
                  <circle class="countdown-circle" r="${this.svgCircleRadius}" cy="50" cx="50" stroke-width="${svgCircleStrokeWidth}" stroke="#0f74bd" stroke-dashoffset="0" stroke-dasharray="${this.calculateStrokeOffset(0)} ${this.calculateStrokeOffset(0)}" fill="none"></circle>
                  <text class="countdown-circle-text" x="50" y="${50 + textSize / 3}" font-size="${textSize}" text-anchor="middle"></text>
                 </g>
                 <style>
                   #c-${uuid} .countdown-circle {        
                    transition: stroke-dashoffset ${transitionTime}ms linear;
                   } 
                 </style>
                </svg>
            `);

		this.$circle = this.$mainDomElement.querySelector<HTMLElement>(":scope .countdown-circle");
		this.$caption = this.$mainDomElement.querySelector<HTMLElement>(":scope .countdown-circle-text");

		this.setProgress(initialProgress)
	}

	/**
	 * @param progress Between 0 and 1
	 * @param caption
	 */
	public setProgress(progress: number) {
		this.$circle.style.strokeDashoffset = "" + this.calculateStrokeOffset(progress);
		let percentString = Math.ceil(progress * 100) + "%";
		this.$caption.textContent = percentString;
	}

	setErrorMessage(message: string | null): void {
		this.$mainDomElement.classList.toggle('error', !!message);
		if (message != null) {
			this.$caption.textContent = '!';
			this.$caption.title = message;
		} else {
			this.$caption.textContent = this.caption;
		}
	}

	private calculateStrokeOffset(progress: number) {
		return (1 - progress) * this.svgCircleRadius * 2 * Math.PI;
	}

	public getMainDomElement() {
		return this.$mainDomElement;
	}
}
