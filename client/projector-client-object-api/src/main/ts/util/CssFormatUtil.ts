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
import {DtoBorder, DtoBoxShadow, DtoFontStyle, DtoLine, DtoSpacing, TextAlignment,} from "../generated";

export type CssPropertyObject = { [cssProperty: string]: string };

export function createTextAlignmentCssString(alignment: TextAlignment | null | undefined) {
	return alignment == null ? '' : 'text-align:' + alignment + ';';
}

export function createFontStyleCssString(fontStyle: DtoFontStyle | null | undefined) {
	if (fontStyle == null) {
		return '';
	} else {
		return (fontStyle.fontColor ? 'color:' + (fontStyle.fontColor ?? '') + ';' : '')
			+ (fontStyle.backgroundColor ? 'background-color:' + (fontStyle.backgroundColor ?? '') + ';' : '')
			+ (fontStyle.bold ? 'font-weight:700;' : '')
			+ (fontStyle.underline ? 'text-decoration:underline;' : '')
			+ (fontStyle.italic ? 'font-style:italic;' : '')
			+ (fontStyle.relativeFontSize ? 'font-size:' + fontStyle.relativeFontSize * 100 + '%;' : '');
	}
}

export function createLineCssString(lineConfig: DtoLine | null | undefined) {
	return lineConfig != null ? `${lineConfig.thickness}px ${lineConfig.type} ${(lineConfig.color ?? '')}` : '';
}

export function createBorderCssObject(borderConfig: DtoBorder | null | undefined): CssPropertyObject {
	if (borderConfig == null) {
		return {};
	} else {
		let css: CssPropertyObject = {};
		if (borderConfig.top) {
			css['border-top'] = createLineCssString(borderConfig.top);
		}
		if (borderConfig.left) {
			css['border-left'] = createLineCssString(borderConfig.left);
		}
		if (borderConfig.bottom) {
			css['border-bottom'] = createLineCssString(borderConfig.bottom);
		}
		if (borderConfig.right) {
			css['border-right'] = createLineCssString(borderConfig.right);
		}
		if (borderConfig.borderRadius) {
			css['border-radius'] = borderConfig.borderRadius + 'px';
		}
		return css;
	}
}

export function createBorderCssString(borderConfig: DtoBorder | null | undefined) {
	if (borderConfig == null) {
		return '';
	} else {
		let cssObject = createBorderCssObject(borderConfig);
		return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
	}
}

export function createShadowCssObject(shadowConfig: DtoBoxShadow | null | undefined): CssPropertyObject {
	if (shadowConfig == null) {
		return {};
	} else {
		return {
			"boxShadow": `${shadowConfig.offsetX || 0}px ${shadowConfig.offsetY || 0}px ${shadowConfig.blur}px ${shadowConfig.spread || 0}px ${(shadowConfig.color ?? '')}`
		}
	}
}

export function createShadowCssString(shadowConfig: DtoBoxShadow | null | undefined): string {
	return cssObjectToString(createShadowCssObject(shadowConfig));
}

export function createSpacingValueCssString(spacingConfig: DtoSpacing | null | undefined) {
	return spacingConfig != null ? `${spacingConfig.top}px ${spacingConfig.right}px ${spacingConfig.bottom}px ${spacingConfig.left}px` : null;
}

export function createSpacingCssObject(cssProperty: string, spacingConfig: DtoSpacing | null | undefined) {
	if (spacingConfig == null) {
		return {};
	} else {
		return {
			[cssProperty]: createSpacingValueCssString(spacingConfig)
		};
	}
}

export function createSpacingCssString(cssProperty: string, spacingConfig: DtoSpacing | null | undefined): string {
	if (spacingConfig == null) {
		return '';
	} else {
		return cssObjectToString(createSpacingCssObject(cssProperty, spacingConfig));
	}
}

export function cssObjectToString(cssObject: CssPropertyObject) {
	return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
}
