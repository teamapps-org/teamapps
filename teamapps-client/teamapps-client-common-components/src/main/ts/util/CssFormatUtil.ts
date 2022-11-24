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
import {
	DtoBorder,
	DtoFontStyle,
	DtoHorizontalElementAlignment,
	DtoImageSizing,
	DtoLine,
	DtoLineType,
	DtoShadow,
	DtoSizeType,
	DtoSizingPolicy,
	DtoSpacing,
	DtoTextAlignment,
	DtoVerticalElementAlignment
} from "../generated";

export type CssPropertyObject = { [cssProperty: string]: string };

export const cssUnitByUiSizeType = {
	[DtoSizeType.FIXED]: 'px',
	[DtoSizeType.FRACTION]: 'fr',
	[DtoSizeType.RELATIVE]: '%'
};

export const cssHorizontalAlignmentByUiVerticalAlignment = {
	[DtoHorizontalElementAlignment.LEFT]: 'start',
	[DtoHorizontalElementAlignment.CENTER]: 'center',
	[DtoHorizontalElementAlignment.RIGHT]: 'end',
	[DtoHorizontalElementAlignment.STRETCH]: 'stretch'
};

export const cssVerticalAlignmentByUiVerticalAlignment = {
	[DtoVerticalElementAlignment.TOP]: 'start',
	[DtoVerticalElementAlignment.CENTER]: 'center',
	[DtoVerticalElementAlignment.BOTTOM]: 'end',
	[DtoVerticalElementAlignment.STRETCH]: 'stretch'
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

export function createTextAlignmentCssString(alignment: DtoTextAlignment) {
	return alignment == null ? '' : 'text-align:' + DtoTextAlignment[alignment].toLowerCase() + ';';
}

export function createUiFontStyleCssString(fontStyle: DtoFontStyle) {
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

export function createUiLineCssString(lineConfig: DtoLine) {
	return lineConfig != null ? `${lineConfig.thickness}px ${DtoLineType[lineConfig.type]} ${(lineConfig.color ?? '')}` : '';
}

export function createUiBorderCssObject(borderConfig: DtoBorder): CssPropertyObject {
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

export function createUiBorderCssString(borderConfig: DtoBorder) {
	if (borderConfig == null) {
		return '';
	} else {
		let cssObject = createUiBorderCssObject(borderConfig);
		return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
	}
}

export function createUiShadowCssObject(shadowConfig: DtoShadow): CssPropertyObject {
	if (shadowConfig == null) {
		return {};
	} else {
		return {
			"boxShadow": `${shadowConfig.offsetX || 0}px ${shadowConfig.offsetY || 0}px ${shadowConfig.blur}px ${shadowConfig.spread || 0}px ${(shadowConfig.color ?? '')}`
		}
	}
}

export function createUiShadowCssString(shadowConfig: DtoShadow): string {
	return cssObjectToString(createUiShadowCssObject(shadowConfig));
}

export function createImageSizingCssObject(imageSizing: DtoImageSizing): CssPropertyObject {
	if (imageSizing == null ) {
		return {};
	} else {
		let backgroundSize: string;
		if (imageSizing === DtoImageSizing.ORIGINAL) {
			backgroundSize = "auto";
		} else if (imageSizing === DtoImageSizing.STRETCH) {
			backgroundSize = "100% 100%";
		} else if (imageSizing === DtoImageSizing.CONTAIN) {
			backgroundSize = "contain";
		} else if (imageSizing ===DtoImageSizing.COVER) {
			backgroundSize = "cover";
		}
		return {
			"backgroundSize": backgroundSize
		}
	}
}

export function createUiSpacingValueCssString(spacingConfig: DtoSpacing) {
	return spacingConfig != null ? `${spacingConfig.top}px ${spacingConfig.right}px ${spacingConfig.bottom}px ${spacingConfig.left}px` : null;
}

export function createUiSpacingCssObject(cssProperty: string, spacingConfig: DtoSpacing) {
	if (spacingConfig == null) {
		return {};
	} else {
		return {
			[cssProperty]: createUiSpacingValueCssString(spacingConfig)
		};
	}
}

export function createUiSpacingCssString(cssProperty: string, spacingConfig: DtoSpacing): string {
	if (spacingConfig == null) {
		return '';
	} else {
		return cssObjectToString(createUiSpacingCssObject(cssProperty, spacingConfig));
	}
}

export function createCssGridRowOrColumnString(sizePolicy: DtoSizingPolicy) {
	let maxSizeString: string;
	if (sizePolicy.type === DtoSizeType.AUTO) {
		maxSizeString = 'auto';
	} else if (sizePolicy.type === DtoSizeType.RELATIVE) {
		maxSizeString = (sizePolicy.value * 100) + cssUnitByUiSizeType[sizePolicy.type];
	} else {
		maxSizeString = sizePolicy.value + cssUnitByUiSizeType[sizePolicy.type];
	}
	return sizePolicy.minAbsoluteSize ? `minmax(${sizePolicy.minAbsoluteSize}px, ${maxSizeString})` : maxSizeString;
}

export function cssObjectToString(cssObject: CssPropertyObject) {
	return Object.keys(cssObject).map(key => `${key}:${cssObject[key]};`).join("");
}
