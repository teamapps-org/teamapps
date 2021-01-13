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
import {UiTextAlignment} from "../../generated/UiTextAlignment";
import {UiVerticalElementAlignment} from "../../generated/UiVerticalElementAlignment";
import {UiHorizontalElementAlignment} from "../../generated/UiHorizontalElementAlignment";
import {UiSizeType} from "../../generated/UiSizeType";
import {UiFontStyleConfig} from "../../generated/UiFontStyleConfig";
import {UiLineConfig} from "../../generated/UiLineConfig";
import {UiLineType} from "../../generated/UiLineType";
import {UiBorderConfig} from "../../generated/UiBorderConfig";
import {UiShadowConfig} from "../../generated/UiShadowConfig";
import {UiSpacingConfig} from "../../generated/UiSpacingConfig";
import {UiSizingPolicyConfig} from "../../generated/UiSizingPolicyConfig";
import {UiImageSizing} from "../../generated/UiImageSizing";

export type CssPropertyObject = { [cssProperty: string]: string };

export const cssUnitByUiSizeType = {
	[UiSizeType.FIXED]: 'px',
	[UiSizeType.FRACTION]: 'fr',
	[UiSizeType.RELATIVE]: '%'
};

export const cssHorizontalAlignmentByUiVerticalAlignment = {
	[UiHorizontalElementAlignment.LEFT]: 'start',
	[UiHorizontalElementAlignment.CENTER]: 'center',
	[UiHorizontalElementAlignment.RIGHT]: 'end',
	[UiHorizontalElementAlignment.STRETCH]: 'stretch'
};

export const cssVerticalAlignmentByUiVerticalAlignment = {
	[UiVerticalElementAlignment.TOP]: 'start',
	[UiVerticalElementAlignment.CENTER]: 'center',
	[UiVerticalElementAlignment.BOTTOM]: 'end',
	[UiVerticalElementAlignment.STRETCH]: 'stretch'
};

export function createUiColorCssObject(cssProperty: string, uiColor: string) {
	if (uiColor == null) {
		return {};
	} else {
		return {
			[cssProperty]: uiColor
		}
	}
}

export function createTextAlignmentCssString(alignment: UiTextAlignment) {
	return alignment == null ? '' : 'text-align:' + UiTextAlignment[alignment].toLowerCase() + ';';
}

export function createUiFontStyleCssString(fontStyle: UiFontStyleConfig) {
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

export function createUiLineCssString(lineConfig: UiLineConfig) {
	return lineConfig != null ? `${lineConfig.thickness}px ${UiLineType[lineConfig.type]} ${(lineConfig.color ?? '')}` : '';
}

export function createUiBorderCssObject(borderConfig: UiBorderConfig): CssPropertyObject {
	if (borderConfig == null) {
		return {};
	} else {
		let css: CssPropertyObject = {};
		if (borderConfig.top) {
			css['border-top'] = createUiLineCssString(borderConfig.top);
		}
		if (borderConfig.left) {
			css['border-left'] = createUiLineCssString(borderConfig.left);
		}
		if (borderConfig.bottom) {
			css['border-bottom'] = createUiLineCssString(borderConfig.bottom);
		}
		if (borderConfig.right) {
			css['border-right'] = createUiLineCssString(borderConfig.right);
		}
		if (borderConfig.borderRadius) {
			css['border-radius'] = borderConfig.borderRadius + 'px';
		}
		return css;
	}
}

export function createUiBorderCssString(borderConfig: UiBorderConfig) {
	if (borderConfig == null) {
		return '';
	} else {
		let cssObject = createUiBorderCssObject(borderConfig);
		return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
	}
}

export function createUiShadowCssObject(shadowConfig: UiShadowConfig): CssPropertyObject {
	if (shadowConfig == null) {
		return {};
	} else {
		return {
			"boxShadow": `${shadowConfig.offsetX || 0}px ${shadowConfig.offsetY || 0}px ${shadowConfig.blur}px ${shadowConfig.spread || 0}px ${(shadowConfig.color ?? '')}`
		}
	}
}

export function createUiShadowCssString(shadowConfig: UiShadowConfig): string {
	return cssObjectToString(createUiShadowCssObject(shadowConfig));
}

export function createImageSizingCssObject(imageSizing: UiImageSizing): CssPropertyObject {
	if (imageSizing == null ) {
		return {};
	} else {
		let backgroundSize: string;
		if (imageSizing === UiImageSizing.ORIGINAL) {
			backgroundSize = "auto";
		} else if (imageSizing === UiImageSizing.STRETCH) {
			backgroundSize = "100% 100%";
		} else if (imageSizing === UiImageSizing.CONTAIN) {
			backgroundSize = "contain";
		} else if (imageSizing ===UiImageSizing.COVER) {
			backgroundSize = "cover";
		}
		return {
			"backgroundSize": backgroundSize
		}
	}
}

export function createUiSpacingValueCssString(spacingConfig: UiSpacingConfig) {
	return spacingConfig != null ? `${spacingConfig.top}px ${spacingConfig.right}px ${spacingConfig.bottom}px ${spacingConfig.left}px` : null;
}

export function createUiSpacingCssObject(cssProperty: string, spacingConfig: UiSpacingConfig) {
	if (spacingConfig == null) {
		return {};
	} else {
		return {
			[cssProperty]: createUiSpacingValueCssString(spacingConfig)
		};
	}
}

export function createUiSpacingCssString(cssProperty: string, spacingConfig: UiSpacingConfig): string {
	if (spacingConfig == null) {
		return '';
	} else {
		return cssObjectToString(createUiSpacingCssObject(cssProperty, spacingConfig));
	}
}

export function createCssGridRowOrColumnString(sizePolicy: UiSizingPolicyConfig) {
	let maxSizeString: string;
	if (sizePolicy.type === UiSizeType.AUTO) {
		maxSizeString = 'auto';
	} else if (sizePolicy.type === UiSizeType.RELATIVE) {
		maxSizeString = (sizePolicy.value * 100) + cssUnitByUiSizeType[sizePolicy.type];
	} else {
		maxSizeString = sizePolicy.value + cssUnitByUiSizeType[sizePolicy.type];
	}
	return sizePolicy.minAbsoluteSize ? `minmax(${sizePolicy.minAbsoluteSize}px, ${maxSizeString})` : maxSizeString;
}

export function cssObjectToString(cssObject: CssPropertyObject) {
	return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
}
