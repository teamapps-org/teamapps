/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {ImageRenderer, Renderer, RenderingFunction, stableSort} from "../Common";
import {UiTextCellTemplateElement_Align, UiTextCellTemplateElementConfig} from "../../generated/UiTextCellTemplateElementConfig";
import {UiTextCellTemplateTextElementConfig} from "../../generated/UiTextCellTemplateTextElementConfig";
import {IconPathProvider} from "../TeamAppsUiContext";
import {UiTextCellTemplateImageElementConfig} from "../../generated/UiTextCellTemplateImageElementConfig";
import {UiIconPosition} from "../../generated/UiIconPosition";
import {UiTextCellTemplate_JustifyLines, UiTextCellTemplateConfig} from "../../generated/UiTextCellTemplateConfig";
import {UiImageTemplateConfig} from "../../generated/UiImageTemplateConfig";
import {UiImageFormattingConfig, UiImageFormatting_CornerShape} from "../../generated/UiImageFormattingConfig";
import CornerShape = UiImageFormatting_CornerShape;


let justifyLines2CssClass: { [justifyLines: number]: string } = {
	[UiTextCellTemplate_JustifyLines.JUSTIFY_TOP]: "justify-lines-top",
	[UiTextCellTemplate_JustifyLines.JUSTIFY_CENTER]: "justify-lines-center",
	[UiTextCellTemplate_JustifyLines.JUSTIFY_CENTER_OVERFLOW_BOTTOM]: "justify-lines-center-overflow-bottom"
};

export function createTextCellTemplateRenderer(template: UiTextCellTemplateConfig, iconPathProvider: IconPathProvider, idPropertyName?: string): Renderer {
	const minHeightStyle = (template.minHeight ? 'min-height:' + template.minHeight + 'px;' : '');
	const maxHeightStyle = (template.maxHeight ? 'max-height:' + template.maxHeight + 'px;' : '');
	const paddingStyle = `padding: ${template.verticalPadding != null ? template.verticalPadding + "px" : ""} ${template.horizontalPadding != null ? template.horizontalPadding + "px" : ""};`;
	const iconPositionFlexDirection = 'flex-direction:' + {
		[UiIconPosition.LEFT_TOP]: "row",
		[UiIconPosition.LEFT_MIDDLE]: "row",
		[UiIconPosition.LEFT_BOTTOM]: "row",
		[UiIconPosition.TOP_LEFT]: "column",
		[UiIconPosition.TOP_CENTER]: "column",
		[UiIconPosition.TOP_RIGHT]: "column",
		[UiIconPosition.RIGHT_TOP]: "row-reverse",
		[UiIconPosition.RIGHT_MIDDLE]: "row-reverse",
		[UiIconPosition.RIGHT_BOTTOM]: "row-reverse"
	}[template.iconPosition] + ';';

	let textElementLines: UiTextCellTemplateElementConfig[][] = [];
	for (let i = 0; i < template.textElements.length; i++) {
		let textElement = template.textElements[i];
		const lineIndex = textElement.line;
		if (!textElementLines[lineIndex]) {
			textElementLines[lineIndex] = [];
		}
		textElementLines[lineIndex].push(textElement);
	}
	textElementLines = textElementLines.filter(line => !!line); // remove unused (skipped) line indexes
	textElementLines.forEach(line => stableSort(line, (a, b) => a.align - b.align));

	let staticElementStyles: string[][] = [];
	let cellImageRenderingFunctions: ImageRenderer[][] = [];
	for (let i = 0; i < textElementLines.length; i++) {
		for (let j = 0; j < textElementLines[i].length; j++) {
			const element = textElementLines[i][j];

			if (!staticElementStyles[i]) staticElementStyles[i] = [];
			if (!cellImageRenderingFunctions[i]) cellImageRenderingFunctions[i] = [];

			staticElementStyles[i][j] =
				(element.marginTop ? `margin-top: ${element.marginTop}px;` : '') +
				(element.marginLeft ? `margin-left: ${element.marginLeft}px;` : '') +
				(element.marginBottom ? `margin-bottom: ${element.marginBottom}px;` : '') +
				(element.marginRight ? `margin-right: ${element.marginRight}px;` : '');

			if (isTextElement(element)) {
				staticElementStyles[i][j] += ('font-size: ' + (element.fontSize * 100) + '%;') +
					(element.underline ? 'text-decoration: underline;' : '') +
					(element.italic ? 'font-style: italic;' : '') +
					(element.bold ? 'font-weight: 700;' : '') +
					(element.noWrap ? 'white-space: nowrap;' : 'white-space: normal;')
			} else if (isImageElement(element)) {
				cellImageRenderingFunctions[i][j] = createImageTemplateRenderer(element.imageTemplate, iconPathProvider);
			}
		}
	}

	let additionalImageStyle = "";
	if (template.imageSpacing) {
		additionalImageStyle += ({
			[UiIconPosition.LEFT_TOP]: "margin-right",
			[UiIconPosition.LEFT_MIDDLE]: "margin-right",
			[UiIconPosition.LEFT_BOTTOM]: "margin-right",
			[UiIconPosition.TOP_LEFT]: "margin-bottom",
			[UiIconPosition.TOP_CENTER]: "margin-bottom",
			[UiIconPosition.TOP_RIGHT]: "margin-bottom",
			[UiIconPosition.RIGHT_TOP]: "margin-left",
			[UiIconPosition.RIGHT_MIDDLE]: "margin-left",
			[UiIconPosition.RIGHT_BOTTOM]: "margin-left"
		}[template.iconPosition]) + ": " + template.imageSpacing + "px; ";
	}
	additionalImageStyle += ({
		[UiIconPosition.LEFT_TOP]: "align-self: flex-start",
		[UiIconPosition.LEFT_MIDDLE]: "align-self: center",
		[UiIconPosition.LEFT_BOTTOM]: "align-self: flex-end",
		[UiIconPosition.TOP_LEFT]: "align-self: flex-start",
		[UiIconPosition.TOP_CENTER]: "align-self: center",
		[UiIconPosition.TOP_RIGHT]: "align-self: flex-end",
		[UiIconPosition.RIGHT_TOP]: "align-self: flex-start",
		[UiIconPosition.RIGHT_MIDDLE]: "align-self: center",
		[UiIconPosition.RIGHT_BOTTOM]: "align-self: flex-en"
	}[template.iconPosition]) + "; ";

	let imageTemplateRenderingFunction: RenderingFunction = template.imageTemplate ? createImageTemplateRenderer(template.imageTemplate, iconPathProvider, additionalImageStyle) : () => "";

	let renderingFunction = (view: any) => {
		function value(propertyName: string): any {
			if (view == null || propertyName == null) {
				return null;
			} else {
				return view[propertyName];
			}
		}

		let textElementsString = '';
		for (let i = 0; i < textElementLines.length; i++) {
			textElementsString += '<div class="line">';
			let currentElementAlign = UiTextCellTemplateElement_Align.LEFT;
			for (let j = 0; j < textElementLines[i].length; j++) {
				const element = textElementLines[i][j];

				if (currentElementAlign < element.align) {
					textElementsString += '<div class="line-spacer"></div>';
					currentElementAlign = element.align;
				}

				if (isTextElement(element)) {
					const textColor = (element.color && !element.badge ? 'color: ' + element.color + ';' : '');

					let badgeColorCss;
					if (element.badgeColorProperty) {
						badgeColorCss = value(element.badgeColorProperty) ? `background-color: ${value(element.badgeColorProperty)}` : '';
						if (element.color) {
							badgeColorCss += value(element.badgeColorProperty) ? '' : `background-color: ${element.color};`;
						}
					} else if (element.color) {
						badgeColorCss = `background-color: ${element.color};`;
					} else {
						badgeColorCss = '';
					}

					const lineHeight = ('line-height: ' + element.lineHeight + ';');
					if (value(element.propertyName)) {
						let displayedValue = value(element.propertyName);
						const textElementContentString = element.badge ? `<span class="badge" style="${textColor} ${badgeColorCss} ${lineHeight}">${displayedValue}</span>` : displayedValue;
						textElementsString += `<span class="element text-element ${element.noHorizontalSpace ? 'no-horizontal-space' : ''} ${UiTextCellTemplateElement_Align[element.align].toLowerCase()}" data-fieldname="${element.propertyName}" style="${lineHeight}${textColor}${staticElementStyles[i][j]}">${textElementContentString}</span>`;
					}
				} else if (isImageElement(element)) {
					textElementsString += `<span class="element image-element" data-fieldname="' + element.fieldName + '" style="${staticElementStyles[i][j]}">${cellImageRenderingFunctions[i][j](value(element.propertyName))}</span>`
				}
			}
			if (currentElementAlign < UiTextCellTemplateElement_Align.RIGHT) {
				textElementsString += '<div class="line-spacer"></div>';
			}
			textElementsString += "</div>";
		}

		return `<div class="custom-entry-template-wrapper" ${value(idPropertyName) ? `data-id="${value(idPropertyName)}"` : ''} style="${maxHeightStyle}">           
    <div class="custom-entry-template" style="${minHeightStyle} ${paddingStyle} ${iconPositionFlexDirection}">
        ${imageTemplateRenderingFunction(value(template.imagePropertyName))}
        <div class="content-wrapper tr-editor-area ${justifyLines2CssClass[template.justifyLines]}">
            <div class="lines-justify-wrapper">
                ${textElementsString}
            </div>
        </div>
    </div>
</div>`;
	};
	return {
		render: renderingFunction,
		template
	};
}

function getStylesForImageFormatting(imageFormatting: UiImageFormattingConfig) {
	let borderRadius;
	if (imageFormatting.cornerShape === CornerShape.CIRCLE) {
		borderRadius = Math.max(imageFormatting.width, imageFormatting.height) + 'px';
	} else if (imageFormatting.cornerShape === CornerShape.ROUNDED) {
		borderRadius = '3px';
	} else {
		borderRadius = '0';
	}
	return `width: ${imageFormatting.width ? imageFormatting.width + "px" : "auto"};`
		+ `height: ${imageFormatting.height ? imageFormatting.height + "px" : "auto"};`
		+ `${imageFormatting.borderWidth ? 'border-width: ' + imageFormatting.borderWidth + 'px' : 0};`
		+ `border-radius: ${borderRadius};`
}

export function createImageTemplateRenderer(imageTemplate: UiImageTemplateConfig, iconPathProvider: IconPathProvider, additionalStyles?: string): ImageRenderer {
	const staticImageFormattingStyles = getStylesForImageFormatting(imageTemplate.defaultFormatting);
	const staticIconFormattingStyles = imageTemplate.iconFormatting && getStylesForImageFormatting(imageTemplate.iconFormatting) || staticImageFormattingStyles;

	return (imageIdentifier) => {
		imageIdentifier = imageIdentifier || imageTemplate.emptyImage;
		let isIcon = imageIdentifier && imageIdentifier.indexOf('icon:') === 0;
		let formatting = isIcon && imageTemplate.iconFormatting ? imageTemplate.iconFormatting : imageTemplate.defaultFormatting;
		let imageUrl = isIcon ? iconPathProvider.getIconPath(imageIdentifier, formatting.width) : imageIdentifier;

		const styles = `border-color: ${imageUrl && formatting.borderColor || 'transparent'};`
			+ `box-shadow: ${imageUrl && formatting.shadow ? '1px 1px 3px 0 rgba(0, 0, 0, .2)' : 'none'};`
			+ ` ${isIcon ? staticIconFormattingStyles : staticImageFormattingStyles}`
			+ ` ${additionalStyles || ''}`;

		if (!formatting.width && !formatting.height) {
			return `<img class="image-template" style="${styles}" src="${imageUrl || "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAC0lEQVQYV2NkgAIAABIAAibpyBYAAAAASUVORK5CYII="}"/>`;
		} else {
			return `<div class="image-template" style="${styles}; background-image: url(${imageUrl || "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAC0lEQVQYV2NkgAIAABIAAibpyBYAAAAASUVORK5CYII="}); "></div>`;
		}
	}
}

function isTextElement(element: UiTextCellTemplateElementConfig): element is UiTextCellTemplateTextElementConfig {
	return element._type === "UiTextCellTemplateTextElement";
}

function isImageElement(element: UiTextCellTemplateElementConfig): element is UiTextCellTemplateImageElementConfig {
	return element._type === "UiTextCellTemplateImageElement";
}
