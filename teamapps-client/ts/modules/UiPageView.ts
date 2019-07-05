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

import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiPageViewBlock_Alignment, UiPageViewBlockConfig} from "../generated/UiPageViewBlockConfig";
import {UiMessagePageViewBlockConfig} from "../generated/UiMessagePageViewBlockConfig";
import {insertAfter, insertBefore, parseHtml, removeDangerousTags} from "./Common";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiCitationPageViewBlockConfig} from "../generated/UiCitationPageViewBlockConfig";
import {UiComponentPageViewBlockConfig} from "../generated/UiComponentPageViewBlockConfig";
import {UiPageViewConfig} from "../generated/UiPageViewConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiPageViewBlockCreatorImageAlignment} from "../generated/UiPageViewBlockCreatorImageAlignment";
import {executeWhenAttached} from "./util/ExecuteWhenAttached";
// require("bootstrap/js/transition");
// require("bootstrap/js/carousel");


interface Row {
	$row: HTMLElement;
	$headerContainer: HTMLElement;
	$leftColumn: HTMLElement;
	$rightColumn: HTMLElement;
	blocks: Block[];
}

interface Block {
	$blockWrapper: HTMLElement;
	$blockContentContainer: HTMLElement;
	block: BlockComponent<UiPageViewBlockConfig>;
}

export class UiPageView extends UiComponent<UiPageViewConfig> {

	private $component: HTMLElement;
	private rows: Row[] = [];

	constructor(config: UiPageViewConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$component = parseHtml(`<div class="UiPageView"></div>`);

		if (config.blocks) {
			for (var i = 0; i < config.blocks.length; i++) {
				this.addBlock(config.blocks[i], false);
			}
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$component;
	}

	@executeWhenAttached()
	public addBlock(blockConfig: UiPageViewBlockConfig, before: boolean, otherBlockId?: string) {
		let row;
		if (this.rows.length == 0) {
			row = this.addRow(false);
		} else if (before && otherBlockId == null) {
			row = this.rows[0];
			if (row.blocks[0].block.getAlignment() == UiPageViewBlock_Alignment.FULL) {
				row = this.addRow(true);
			}
		} else if (!before && otherBlockId == null) {
			if (blockConfig.alignment === UiPageViewBlock_Alignment.FULL) {
				this.addRow(false);
			}
			row = this.rows[this.rows.length - 1];
		} else if (before && otherBlockId != null) {
			// TODO
		}

		let $blockWrapper = parseHtml(`<div class="block-wrapper teamapps-blurredBackgroundImage">
    <div class="background-color-div"></div>
</div>`);
		let $blockContentContainer = $blockWrapper.querySelector<HTMLElement>(':scope .background-color-div');
		let block = new (blockTypes[blockConfig._type as keyof typeof blockTypes] as any)(blockConfig, this._context) as BlockComponent<UiPageViewBlockConfig>;
		$blockContentContainer.appendChild(block.getMainDomElement());
		row.blocks.push({$blockWrapper, $blockContentContainer, block});

		// TODO prepend vs append vs insert...
		if (blockConfig.alignment === UiPageViewBlock_Alignment.FULL) {
			row.$headerContainer.appendChild($blockWrapper);
		} else if (blockConfig.alignment === UiPageViewBlock_Alignment.LEFT) {
			row.$leftColumn.appendChild($blockWrapper);
		} else if (blockConfig.alignment === UiPageViewBlock_Alignment.RIGHT) {
			row.$rightColumn.appendChild($blockWrapper);
		}
		block.attachedToDom = this.attachedToDom;
	}

	private addRow(before: boolean, otherRowIndex?: number): Row {
		let $row = parseHtml('<div class="block-section row">');
		let $headerContainer = parseHtml('<div class="header-container col-md-12">');
		$row.appendChild($headerContainer);
		let $leftColumn = parseHtml('<div class="left-column col-md-8">');
		$row.appendChild($leftColumn);
		let $rightColumn = parseHtml('<div class="right-column col-md-4">');
		$row.appendChild($rightColumn);
		let row: Row = {
			$row: $row,
			$leftColumn: $leftColumn,
			$rightColumn: $rightColumn,
			$headerContainer: $headerContainer,
			blocks: []
		};

		if (before && otherRowIndex == null) {
			this.rows.unshift(row);
			this.$component.prepend($row);
		} else if (!before && otherRowIndex == null) {
			this.rows.push(row);
			this.$component.appendChild($row);
		} else if (before && otherRowIndex != null) {
			this.rows.splice(otherRowIndex, 0, row);
			insertBefore($row, this.rows[otherRowIndex].$row)
		} else if (!before && otherRowIndex != null) {
			this.rows.splice(otherRowIndex + 1, 0, row);
			insertAfter($row, this.rows[otherRowIndex].$row)
		}

		return row;
	};

	onResize(): void {
		this.rows.forEach(row => {
			row.blocks.forEach(block => block.block.reLayout());
		});
	}

	public destroy(): void {
		this.rows.forEach(row => {
			row.blocks.forEach(block => block.block.destroy());
		});
	}

	protected onAttachedToDom(): void {
		this.rows.forEach(row => row.blocks.forEach(block => block.block.attachedToDom = true));
	}
}

abstract class BlockComponent<C extends UiPageViewBlockConfig> {
	constructor(protected config: C, protected context: TeamAppsUiContext) {
	}

	public getAlignment() {
		return this.config.alignment;
	}

	abstract getMainDomElement(): HTMLElement;

	abstract set attachedToDom(attached: boolean);

	public reLayout() {
		// default implementation
	}

	public destroy() {
		// default implementation
	}
}

class UiMessagePageViewBlock extends BlockComponent<UiMessagePageViewBlockConfig> {
	private $contentWrapper: HTMLElement;
	private $slider: HTMLElement;
	private $images: HTMLImageElement[] = [];
	private _attachedToDom: boolean;

	constructor(config: UiMessagePageViewBlockConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$contentWrapper = parseHtml(`<div class="UiMessagePageViewBlock"></div>`);
		if (config.creatorImageUrl) {
			this.$contentWrapper.appendChild(parseHtml(`<div class="creator-image-wrapper align-${UiPageViewBlockCreatorImageAlignment[config.creatorImageAlignment].toLowerCase()}">
	${config.creatorImageUrl ? `<img class="creator-image" src="${config.creatorImageUrl}"></img>` : ''}
</div>`))
		}
		if (config.headLine) {
			this.$contentWrapper.appendChild($(`<div class="headline">${removeDangerousTags(config.headLine)}</div>`)[0]);
		}
		if (config.text) {
			this.$contentWrapper.appendChild($(`<div class="text">${removeDangerousTags(config.text)}</div>`)[0]);
		}
		if (this.config.imageUrls && this.config.imageUrls.length === 1) {
			this.$contentWrapper.appendChild(parseHtml(`<div class="image"><img src="${this.config.imageUrls[0]}"></div>`));
		}
		if (config.imageUrls && config.imageUrls.length > 0) {
			this.$slider = parseHtml(`<div class="slider"></div>`);
			this.$contentWrapper.appendChild(this.$slider);

			if (this.config.imageUrls && this.config.imageUrls.length > 1) {

				$(this.$slider).slick({
					dots: true,
					infinite: true,
					speed: 300,
					slidesToShow: 1,
					centerMode: true,
					variableWidth: true,
					draggable: true,

				});

				for (var i = 0; i < this.config.imageUrls.length; i++) {
					const $sliderItem = document.createElement("div");
					$sliderItem.classList.add("slider-item");
					const $image = new Image();
					$sliderItem.appendChild($image);
					$image.classList.add("slider-item-img");
					$image.onload = () => {
						$(this.$slider).slick('slickAdd', $sliderItem);
						this.reLayout();
					};
					$image.src = this.config.imageUrls[i];
					this.$images.push($image);
				}
			}
		}
	}

	reLayout() {
		if (this._attachedToDom) {
			const minMaxDimensions = this.$images.reduce((minMaxDimensions, $img) => {
				if ($img.naturalWidth > 0 && $img.naturalHeight > 0) {
					minMaxDimensions.maxWidth = Math.max(minMaxDimensions.maxWidth, $img.naturalWidth);
					minMaxDimensions.maxHeight = Math.max(minMaxDimensions.maxHeight, $img.naturalHeight);
					minMaxDimensions.minWidth = Math.min(minMaxDimensions.minWidth, $img.naturalWidth);
					minMaxDimensions.minHeight = Math.min(minMaxDimensions.minHeight, $img.naturalHeight);
				}
				return minMaxDimensions;
			}, {minWidth: 10000000, minHeight: 1000000, maxWidth: 0, maxHeight: 0});
			const applicableWidth = this.$slider.offsetWidth - (this.$slider.offsetWidth * .2);
			let maxHeight = this.$images.reduce((maxHeight, $img) => {
				if ($img.naturalWidth > 0 && $img.naturalHeight > 0) {
					return Math.max(maxHeight, applicableWidth * $img.naturalHeight / $img.naturalWidth);
				} else {
					return maxHeight;
				}
			}, 0);
			maxHeight = Math.min(300, maxHeight);
			this.$images.forEach($img => {
				if ($img.naturalWidth > 0 && $img.naturalHeight > 0) {
					const aspectRatio = $img.naturalWidth / $img.naturalHeight;
					$img.style.height = maxHeight + "px";
					$img.style.width = (maxHeight * aspectRatio) + "px";
				}
			});
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$contentWrapper;
	}

	public set attachedToDom(attachedToDom: boolean) {
		this._attachedToDom = attachedToDom;
		this.reLayout();
	}

	public destroy(): void {
		// nothing to do
	}
}

class UiCitationPageViewBlock extends BlockComponent<UiCitationPageViewBlockConfig> {

	private $component: HTMLElement;

	constructor(config: UiCitationPageViewBlockConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$component = parseHtml(`<div class="UiCitationPageViewBlock">
    <div class="creator-image-wrapper align-${UiPageViewBlockCreatorImageAlignment[config.creatorImageAlignment].toLowerCase()}">
		${config.creatorImageUrl ? `<img class="creator-image" src="${config.creatorImageUrl}"></img>` : ''}
    </div>
    <div class="content-wrapper">

    </div>
</div>`);
		let $contentWrapper = this.$component.querySelector<HTMLElement>(':scope .content-wrapper');
		$contentWrapper.appendChild($(`<div class="citation">${removeDangerousTags(config.citation)}</div>`)[0]);
		$contentWrapper.appendChild($(`<div class="author">${removeDangerousTags(config.author)}</div>`)[0]);
	}


	public getMainDomElement(): HTMLElement {
		return this.$component;
	}

	public set attachedToDom(attachedToDom: boolean) {
		// do nothing
	}

	public destroy(): void {
		// nothing to do
	}
}

class UiComponentPageViewBlock extends BlockComponent<UiComponentPageViewBlockConfig> {

	private $div: HTMLElement;
	private component: UiComponent<UiComponentConfig>;
	private $componentWrapper: HTMLElement;

	constructor(config: UiComponentPageViewBlockConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$div = parseHtml(`<div class="UiComponentPageViewBlock" style="height:${config.height}px">
                <div class="component-wrapper"></div>
            </div>`);
		this.$componentWrapper = this.$div.querySelector<HTMLElement>(':scope .component-wrapper');

		if (config.title) {
			this.$div.prepend($(`<div class="title">${removeDangerousTags(config.title)}</div>`)[0]);
		}

		this.component = config.component as UiComponent;
		this.$componentWrapper.appendChild(this.component.getMainDomElement());
	}

	public getMainDomElement(): HTMLElement {
		return this.$div;
	}

	public set attachedToDom(attachedToDom: boolean) {
		this.component.attachedToDom = attachedToDom;
	}

	public destroy(): void {
	}
}

var blockTypes = {
	"UiMessagePageViewBlock": UiMessagePageViewBlock,
	"UiCitationPageViewBlock": UiCitationPageViewBlock,
	"UiComponentPageViewBlock": UiComponentPageViewBlock
};

TeamAppsUiComponentRegistry.registerComponentClass("UiPageView", UiPageView);
