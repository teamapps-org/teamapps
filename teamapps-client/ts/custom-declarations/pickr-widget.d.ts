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
declare module "pickr-widget" {

	export interface PickrOptions {
		/**
		 * Selector or element which will be replaced with the actual color-picker.
		 * Can be a HTMLElement.
		 */
		el: string | Element,

		/**
		 * Using the 'el' Element as button, won't replace it with the pickr-button.
		 * If true, appendToBody will also be automatically true.
		 */
		useAsButton?: boolean,

		/**
		 * Start state. If true 'disabled' will be added to the button's classlist.
		 */
		disabled?: boolean,

		/**
		 * If set to false it would directly apply the selected color on the button and preview.
		 */
		comparison?: boolean,

		/**
		 * Default color
		 */
		default?: string,

		/**
		 * Default color representation.
		 * Valid options are 'HEX', 'RGBA', 'HSVA', 'HSLA' and 'CMYK'.
		 */
		defaultRepresentation?: 'HEX' | 'RGBA' | 'HSVA' | 'HSLA' | 'CMYK',

		/**
		 * Option to keep the color picker always visible. You can still hide / show it via
		 * 'pickr.hide()' and 'pickr.show()'. The save button keeps his functionality, so if
		 * you click it, it will fire the onSave event.
		 */
		showAlways?: boolean,

		/**
		 * Defines a parent for pickr, if useAsButton is true and a parent is NOT defined
		 * 'body' will be used as fallback.
		 */
		parent?: Element | null,

		/**
		 * Close pickr with this specific key.
		 * Default is 'Escape'. Can be the event key or code.
		 */
		closeWithKey?: 'Escape' | number,

		/**
		 * Defines the position of the color-picker. Available options are
		 * top, left and middle relativ to the picker button.
		 * If clipping occurs, the color picker will automatically choose his position.
		 */
		position?: 'top' | 'left' | 'middle',

		/**
		 * Enables the ability to change numbers in an input field with the scroll-wheel.
		 * To use it set the cursor on a position where a number is and scroll, use ctrl to make steps of five
		 */
		adjustableNumbers?: boolean,

		/**
		 * Show or hide specific components.
		 * By default only the palette (and the save button) is visible.
		 */
		components?: {
			/** Left side color comparison */
			preview?: boolean,
			/** Opacity slider */
			opacity?: boolean,
			/** Hue slider */
			hue?: boolean,

			/**
			 * Bottom interaction bar, theoretically you could use 'true' as propery.
			 * But this would also hide the save-button.
			 */
			interaction: {
				/** hex option  (hexadecimal representation of the rgba value) */
				hex?: boolean,
				/** rgba option (red green blue and alpha) */
				rgba?: boolean,
				/** hsla option (hue saturation lightness and alpha) */
				hsla?: boolean,
				/** hsva option (hue saturation value and alpha) */
				hsva?: boolean,
				/** cmyk option (cyan mangenta yellow key ) */
				cmyk?: boolean,
				/** input / output element */
				input?: boolean,
				/** Button which provides the ability to select no color, */
				clear?: boolean,
				/** Save button */
				save?: boolean
			},
		},

		/**
		 * Button strings, brings the possibility to use a language other than English.
		 */
		strings?: {
			/** Default for save button*/
			save?: string,
			/** Default for clear button*/
			clear?: string
		},

		// User has changed the color
		onChange?(hsva: HSVaColor, instance: Pickr): void,

		// User has clicked the save button
		onSave?(hsva: HSVaColor, instance: Pickr): void
	}

	export class Pickr {
		constructor(options: PickrOptions);

		hide(): void;

		show(): void;

		setHSVA(h?: number, s?: number, v?: number, a?: number): void;

		setColor(c: string): void;

		getColor(): HSVaColor;

		getRoot(): HTMLElement;

		destroy(): void;

		destroyAndRemove(): void;

		disable(): void;

		enable(): void;

		isOpen(): boolean;

		setColorRepresentation(type: string): void;
	}

	export class HSVaColor {

		h: number;
		s: number;
		v: number;
		a: number;

		constructor(h?: number, s?: number, v?: number, a?: number);

		toHSVA(): [number, number, number, number] & { toString(): string };

		toHSLA(): [number, number, number] & { toString(): string };

		toRGBA(): [number, number, number, number] & { toString(): string };

		toHEX(): [number, number, number] & { toString(): string };

		toCMYK(): [number, number, number, number] & { toString(): string };

		clone(): HSVaColor;
	}

	export function create(options: PickrOptions): Pickr;
}
