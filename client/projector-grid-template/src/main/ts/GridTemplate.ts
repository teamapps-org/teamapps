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
	createTextAlignmentCssString,
	createBorderCssString,
	createFontStyleCssString,
	createShadowCssString,
	createSpacingCssString,
	cssObjectToString, CssPropertyObject,
} from "projector-client-object-api";
import {
	DtoAbstractGridTemplateElement,
	DtoBadgeElement,
	DtoFloatingElement,
	DtoGlyphIconElement,
	DtoGridTemplate,
	DtoIconElement,
	DtoImageElement, ImageSizing, SizeType, DtoSizingPolicy,
	DtoTextElement
} from "./generated";
import {Template} from "projector-client-object-api";

type RenderingFunction = (data: any) => string;

export class GridTemplate implements Template {
	private renderers: ((data: any) => string)[];
	private gridCss: string;

	constructor(private config: DtoGridTemplate) {
		this.renderers = config.elements.map(element => {

			let startColumn = config.columns[element.column];
			let endColumn = config.columns[element.column + element.colSpan - 1];
			let startRow = config.rows[element.row];
			let endRow = config.rows[element.row + element.rowSpan - 1];

			let marginCss: string;
			if (startColumn == null || endColumn == null || startRow == null || endRow == null) {
				console.error(`Element is placed (or spans) out of defined rows: col:${element.column};span:${element.colSpan}, row:${element.row};span:${element.rowSpan}.
			The resulting template renderer will skip some formattings for this element!`);
			} else {
				let marginTop = (element.margin && element.margin.top || 0) + (startRow.topPadding || 0);
				let marginRight = (element.margin && element.margin.right || 0) + (endColumn.rightPadding || 0);
				let marginBottom = (element.margin && element.margin.bottom || 0) + (endRow.bottomPadding || 0);
				let marginLeft = (element.margin && element.margin.left || 0) + (startColumn.leftPadding || 0);
				marginCss = `margin: ${marginTop}px ${marginRight}px ${marginBottom}px ${marginLeft}px;`;
			}

			// TODO handle element's own padding!!!

			return createElementRenderer(element, marginCss);
		});

		const gridTemplateColumnsString = 'grid-template-columns:' + config.columns.map(column => createCssGridRowOrColumnString(column.widthPolicy)).join(" ") + ';';
		const gridTemplateRowsString = 'grid-template-rows:' + config.rows.map(row => createCssGridRowOrColumnString(row.heightPolicy)).join(" ") + ';';
		const paddingCss = createSpacingCssString("padding", config.padding);
		const gridGapCss = 'grid-gap:' + config.gridGap + 'px;';
		const maxWidthCss = config.maxWidth >= 0 ? `max-width: ${config.maxWidth}px;` : '';
		const maxHeightCss = config.maxHeight >= 0? `max-height: ${config.maxHeight}px;` : '';
		const minWidthCss = config.minWidth ? `min-width: ${config.minWidth}px;` : '';
		const minHeightCss = config.minHeight ? `min-height: ${config.minHeight}px;` : '';
		const backgroundColorCss = config.backgroundColor ? (`background-color: ${(config.backgroundColor ?? '')};`) : '';
		const borderCss = createBorderCssString(config.border);
		this.gridCss = `${gridTemplateColumnsString} ${gridTemplateRowsString} ${paddingCss} ${gridGapCss} ${minWidthCss} ${minHeightCss} ${maxWidthCss} ${maxHeightCss} ${backgroundColorCss} ${borderCss}`;
	}

	invoke(name: string, params: any[]): Promise<any> {
		// nothing to do with a GridTemplate at this moment
        throw new Error("Method not implemented.");
    }

	render(data: any) {
		if (data == null) {
			return '';
		} else {
			let ariaLabel = data[this.config.ariaLabelProperty];
			let title = data[this.config.titleProperty];
			return `<div class="GridTemplate" style="${this.gridCss}" ${ariaLabel != null ? `aria-label="${ariaLabel}"` : ''} ${title != null ? `title="${title}"` : ""}>
	${this.renderers.map(renderer => renderer(data)).join("")}
</div>`;
		}
	}

	destroy(): void {
		// nothing to do
	}

}

function createTextElementRenderer(element: DtoTextElement, additionalCssClass?: string, additionalStyles?: string): RenderingFunction {
	const fontStyleCssString = createFontStyleCssString(element.fontStyle);
	const elementStyleCssString = (element.lineHeight ? ('line-height:' + element.lineHeight + ';') : '')
		+ (`white-space:${element.wrapLines ? 'normal' : 'nowrap'};`)
		+ (createSpacingCssString("padding", element.padding))
		+ (createTextAlignmentCssString(element.textAlignment));
	const backgroundColorCss = element.backgroundColor ? (`background-color: ${(element.backgroundColor ?? '')};`) : '';
	return (data: any) => {
		let value = data[element.property];
		if (value == null) {
			return "";
		} else if (element.fontStyle != null && element.fontStyle.backgroundColor) {
			return `<div class="grid-template-element TextElement wrapper ${additionalCssClass || ''}" style="${elementStyleCssString} ${backgroundColorCss} ${additionalStyles || ''}">
	<span data-key="${element.property}" style="${fontStyleCssString}">${value || ''}</span>
</div>`;
		} else {
			return `<span data-key="${element.property}" class="grid-template-element TextElement ${additionalCssClass || ''}" style="${fontStyleCssString} ${elementStyleCssString} ${backgroundColorCss} ${additionalStyles || ''}">${value || ''}</span>`;
		}
	};
}

function createBadgeElementRenderer(element: DtoBadgeElement, additionalCss: string): RenderingFunction {
	const borderStyle = `border: 1px solid ${element.borderColor ? element.borderColor ?? '' : 'transparent'};`;
	return createTextElementRenderer(element, 'DtoBadgeElement', borderStyle + additionalCss);
}

function createFloatingElementRenderer(element: DtoFloatingElement, additionalCss: string): RenderingFunction {
	const elementRenderers = element.elements.map(subElement => createElementRenderer(subElement));
	const wrapCss = `flex-wrap: ${element.wrap ? 'wrap' : 'nowrap'};`;
	const backgroundColorCss = element.backgroundColor ? (`background-color: ${(element.backgroundColor ?? '')};`) : '';
	const alignItemsCss = `align-items: ${element.alignItems ?? 'flex-start'};`;
	const justifyContentCss = `justify-content: ${element.justifyContent ?? 'start'};`;
	return (data: any) => {
		return `<div class='grid-template-element DtoFloatingElement' style="${wrapCss} ${backgroundColorCss} ${alignItemsCss} ${justifyContentCss} ${additionalCss}">${elementRenderers.map(renderer => renderer(data)).join('')}</div>`;
	};
}

function createImageElementRenderer(element: DtoImageElement, additionalCss: string): RenderingFunction {
	let style = (element.width ? ('width:' + element.width + 'px;') : '')
		+ (element.height ? ('height:' + element.height + 'px;') : '')
		+ createBorderCssString(element.border)
		+ createSpacingCssString("padding", element.padding)
		+ createShadowCssString(element.shadow)
		+ cssObjectToString(createImageSizingCssObject(element.imageSizing))
		+ (element.backgroundColor ? (`background-color: ${(element.backgroundColor ?? '')};`) : '');
	return (data: any) => {
		let value = data[element.property];
		if (value != null) {
			const backgroundImage = `background-image: url('${value}');`;
			return `<div data-key="${element.property}" class="grid-template-element ImageElement" style="${style} ${backgroundImage} ${additionalCss}"></div>`;
		} else {
			return '';
		}
	};
}

function createIconElementRenderer(element: DtoIconElement, additionalCss: string): RenderingFunction {
	const style = `width:${element.size}px; height:${element.size}px; background-size:${element.size}px;`;
	const backgroundColorCss = element.backgroundColor ? (`background-color: ${(element.backgroundColor ?? '')};`) : '';
	return (data: any) => {
		let value = data[element.property];
		if (value != null) {
			const backgroundImage = value ? `background-image: url('${value}');` : '';
			return `<div data-key="${element.property}" class="grid-template-element IconElement" style="${style} ${backgroundImage} ${backgroundColorCss} ${additionalCss}"></div>`;
		} else {
			return '';
		}
	};
}

function createGlyphIconElementRenderer(element: DtoGlyphIconElement, additionalCss: string): RenderingFunction {
	const style = `font-size:${element.size}px; text-align: center;`;
	const backgroundColorCss = element.backgroundColor ? (`background-color: ${(element.backgroundColor ?? '')};`) : '';
	return (data: any) => {
		let value = data[element.property];
		if (value == null) {
			return "";
		} else {
			return `<div data-key="${element.property}" class="grid-template-element GlyphIconElement fas fa-${value}" style="${style} ${backgroundColorCss} ${additionalCss}')"></div>`;
		}
	};
}

function createElementRenderer(element: DtoAbstractGridTemplateElement, additionalCss = ""): RenderingFunction {
	const column = `grid-column: ${element.column + 1} / ${element.column + element.colSpan + 1};`;
	const row = `grid-row: ${element.row + 1} / ${element.row + element.rowSpan + 1};`;
	const horizontalAlignmentCss = `justify-self: ${element.horizontalAlignment};`;
	const verticalAlignmentCss = `align-self: ${element.verticalAlignment};`;

	let marginCss: string = "";
	if (additionalCss.indexOf("margin:") === -1) {
		let marginTop = (element.margin && element.margin.top || 0);
		let marginRight = (element.margin && element.margin.right || 0);
		let marginBottom = (element.margin && element.margin.bottom || 0);
		let marginLeft = (element.margin && element.margin.left || 0);
		marginCss = `margin: ${marginTop}px ${marginRight}px ${marginBottom}px ${marginLeft}px;`;
	}

	let totalCss = marginCss + additionalCss + column + row + horizontalAlignmentCss + verticalAlignmentCss;

	if (isTextElement(element)) {
		return createTextElementRenderer(element, null, totalCss);
	} else if (isBadgeElement(element)) {
		return createBadgeElementRenderer(element, totalCss);
	} else if (isFloatingElement(element)) {
		return createFloatingElementRenderer(element, totalCss);
	} else if (isImageElement(element)) {
		return createImageElementRenderer(element, totalCss);
	} else if (isIconElement(element)) {
		return createIconElementRenderer(element, totalCss);
	} else if (isGlyphIconElement(element)) {
		return createGlyphIconElementRenderer(element, totalCss);
	}
}

export function isTextElement(element: DtoAbstractGridTemplateElement): element is DtoTextElement {
	return element._type === "DtoTextElement";
}

export function isBadgeElement(element: DtoAbstractGridTemplateElement): element is DtoBadgeElement {
	return element._type === "DtoBadgeElement";
}

export function isFloatingElement(element: DtoAbstractGridTemplateElement): element is DtoFloatingElement {
	return element._type === "DtoFloatingElement";
}

export function isImageElement(element: DtoAbstractGridTemplateElement): element is DtoImageElement {
	return element._type === "DtoImageElement";
}

export function isIconElement(element: DtoAbstractGridTemplateElement): element is DtoIconElement {
	return element._type === "DtoIconElement";
}

export function isGlyphIconElement(element: DtoAbstractGridTemplateElement): element is DtoGlyphIconElement {
	return element._type === "DtoGlyphIconElement";
}

export function createImageSizingCssObject(imageSizing: ImageSizing): CssPropertyObject {
	if (imageSizing == null ) {
		return {};
	} else {
		let backgroundSize: string;
		if (imageSizing === ImageSizing.ORIGINAL) {
			backgroundSize = "auto";
		} else if (imageSizing === ImageSizing.STRETCH) {
			backgroundSize = "100% 100%";
		} else if (imageSizing === ImageSizing.CONTAIN) {
			backgroundSize = "contain";
		} else if (imageSizing ===ImageSizing.COVER) {
			backgroundSize = "cover";
		}
		return {
			"background-size": backgroundSize
		}
	}
}

export function createCssGridRowOrColumnString(sizePolicy: DtoSizingPolicy) {
	let maxSizeString: string;
	if (sizePolicy.type === SizeType.AUTO) {
		maxSizeString = 'auto';
	} else if (sizePolicy.type === SizeType.RELATIVE) {
		maxSizeString = (sizePolicy.value * 100) + sizePolicy.type;
	} else {
		maxSizeString = sizePolicy.value + sizePolicy.type;
	}
	return sizePolicy.minAbsoluteSize ? `minmax(${sizePolicy.minAbsoluteSize}px, ${maxSizeString})` : maxSizeString;
}
