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
	AbstractComponent,
	Component,
	executeAfterAttached,
	insertAfter,
	insertBefore,
	parseHtml,
	removeClassesByFunction,
	ServerObjectChannel,
	Template
} from "projector-client-object-api";
import {BlockAlignment, DtoBlock, DtoBlogView, DtoCitationBlock, DtoComponentBlock, DtoMessageBlock} from "./generated";
import {removeDangerousTags, ToolButton} from "projector-client-core-components";
import {fixed_partition} from "image-layout";


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
	block: AbstractBlockComponent<DtoBlock>;
}

export class BlogView extends AbstractComponent<DtoBlogView> {

	private $component: HTMLElement;
	private rows: Row[] = [];

	constructor(config: DtoBlogView, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$component = parseHtml(`<div class="UiPageView"></div>`);

		if (config.blocks) {
			for (var i = 0; i < config.blocks.length; i++) {
				this.addBlock(config.blocks[i], false);
			}
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$component;
	}

	@executeAfterAttached()
	public addBlock(blockConfig: DtoBlock, before: boolean, otherBlockId?: string) {
		let row;
		if (this.rows.length == 0) {
			row = this.addRow(false);
		} else if (before && otherBlockId == null) {
			row = this.rows[0];
			if (row.blocks[0].block.getAlignment() == BlockAlignment.FULL) {
				row = this.addRow(true);
			}
		} else if (!before && otherBlockId == null) {
			if (blockConfig.alignment === BlockAlignment.FULL) {
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
		let block = new (blockTypes[blockConfig._type as keyof typeof blockTypes] as any)(blockConfig) as AbstractBlockComponent<DtoBlock>;
		$blockContentContainer.appendChild(block.getMainDomElement());
		row.blocks.push({$blockWrapper, $blockContentContainer, block});

		// TODO prepend vs append vs insert...
		if (blockConfig.alignment === BlockAlignment.FULL) {
			row.$headerContainer.appendChild($blockWrapper);
		} else if (blockConfig.alignment === BlockAlignment.LEFT) {
			row.$leftColumn.appendChild($blockWrapper);
		} else if (blockConfig.alignment === BlockAlignment.RIGHT) {
			row.$rightColumn.appendChild($blockWrapper);
		}
	}

	private addRow(before: boolean, otherRowIndex?: number): Row {
		let $row = parseHtml('<div class="block-section clearfix row">');
		let $headerContainer = parseHtml('<div class="header-container col-md-12">');
		$row.appendChild($headerContainer);
		let $leftColumn = parseHtml('<div class="left-column clearfix col-md-8">');
		$row.appendChild($leftColumn);
		let $rightColumn = parseHtml('<div class="right-column clearfix col-md-4">');
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
		super.destroy();
		this.rows.forEach(row => {
			row.blocks.forEach(block => block.block.destroy());
		});
	}

}

abstract class AbstractBlockComponent<C extends DtoBlock> {
	constructor(protected config: C) {
	}

	public getAlignment() {
		return this.config.alignment;
	}

	abstract getMainDomElement(): HTMLElement;

	public reLayout() {
		// default implementation
	}

	public destroy() {
		// default implementation
	}
}

class UiMessageBlock extends AbstractBlockComponent<DtoMessageBlock> {
	private $main: HTMLElement;
	private $toolButtons: Element;
	private $topRecord: HTMLElement;
	private $htmlContainer: HTMLElement;
	private $images: HTMLElement;
	private images: {
		$img: HTMLImageElement,
		width: number,
		height: number
	}[] = [];

	private readonly minIdealImageHeight = 250;

	constructor(config: DtoMessageBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block UiMessageBlock">
	<div class="tool-buttons"></div>
	<div class="top-record"></div>
	<div class="html"></div>
	<div class="images"></div>
</div>`);
		this.$toolButtons = this.$main.querySelector(":scope .tool-buttons");
		this.$topRecord = this.$main.querySelector(":scope .top-record");
		this.$htmlContainer = this.$main.querySelector(":scope .html");
		this.$images = this.$main.querySelector(":scope .images");

		this.$toolButtons.innerHTML = '';
		config.toolButtons && config.toolButtons.forEach((tb: ToolButton) => {
			this.$toolButtons.appendChild(tb.getMainElement());
		});

		removeClassesByFunction(this.$topRecord.classList, className => className.startsWith("align-"));
		this.$topRecord.classList.add("align-" + config.topRecordAlignment);
		this.$topRecord.innerHTML = config.topRecord != null ? (config.topTemplate as Template).render(config.topRecord.values) : "";

		this.$htmlContainer.innerHTML = config.html != null ? removeDangerousTags(config.html) : "";

		if (config.imageUrls && config.imageUrls.length > 0) {
			for (var i = 0; i < this.config.imageUrls.length; i++) {
				const $image = new Image();
				let image = {
					width: this.minIdealImageHeight,
					height: this.minIdealImageHeight,
					$img: $image
				};
				this.images.push(image);
				$image.classList.add("image");
				$image.onload = (event: Event) => {
					image.width = (event.target as HTMLImageElement).naturalWidth;
					image.height = (event.target as HTMLImageElement).naturalHeight;
					this.reLayout();
				};
				$image.src = this.config.imageUrls[i];
				this.$images.appendChild($image);
			}
		}

	}

	reLayout() {
		if (this.images.length > 0) {
			let availableWidth = this.$images.clientWidth;
			let layout = fixed_partition(this.images, {
				containerWidth: availableWidth,
				idealElementHeight: Math.max(this.minIdealImageHeight, availableWidth / 3),
				align: 'center',
				spacing: 10
			});
			for (let i = 0; i < this.images.length; i++) {
				this.images[i].$img.style.left = layout.positions[i].x + "px";
				this.images[i].$img.style.top = layout.positions[i].y + "px";
				this.images[i].$img.style.width = layout.positions[i].width + "px";
				this.images[i].$img.style.height = layout.positions[i].height + "px";
			}
			this.$images.style.height = layout.height + "px";
		} else {
			this.$images.style.height = "0";
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
		// nothing to do
	}

}

class UiCitationBlock extends AbstractBlockComponent<DtoCitationBlock> {

	private $main: HTMLElement;
	private $toolButtons: Element;

	constructor(config: DtoCitationBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block UiCitationBlock">
    <div class="tool-buttons"></div>
    <div class="flex-container">
	    <div class="creator-image-wrapper align-${config.creatorImageAlignment}">
			${config.creatorImageUrl ? `<img class="creator-image" src="${config.creatorImageUrl}"></img>` : ''}
	    </div>
	    <div class="content-wrapper"></div>
	</div>
</div>`);
		let $contentWrapper = this.$main.querySelector<HTMLElement>(':scope .content-wrapper');
		$contentWrapper.appendChild(parseHtml(`<div class="citation">${removeDangerousTags(config.citation)}</div>`)[0]);
		$contentWrapper.appendChild(parseHtml(`<div class="author">${removeDangerousTags(config.author)}</div>`)[0]);

		this.$toolButtons = this.$main.querySelector(":scope .tool-buttons");
		this.$toolButtons.innerHTML = '';
		config.toolButtons && config.toolButtons.forEach((tb: ToolButton) => {
			this.$toolButtons.appendChild(tb.getMainElement());
		});

	}


	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public set attachedToDom(attachedToDom: boolean) {
		// do nothing
	}

	public destroy(): void {
		// nothing to do
	}
}

class UiComponentBlock extends AbstractBlockComponent<DtoComponentBlock> {

	private $main: HTMLElement;
	private component: Component;
	private $componentWrapper: HTMLElement;
	private $toolButtons: Element;

	constructor(config: DtoComponentBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block UiComponentBlock" style="height:${config.height}px">
	<div class="tool-buttons"></div>
                <div class="component-wrapper"></div>
            </div>`);
		this.$componentWrapper = this.$main.querySelector<HTMLElement>(':scope .component-wrapper');
		this.$toolButtons = this.$main.querySelector(":scope .tool-buttons");

		this.$toolButtons.innerHTML = '';
		config.toolButtons && config.toolButtons.forEach((tb: ToolButton) => {
			this.$toolButtons.appendChild(tb.getMainElement());
		});
		
		if (config.title) {
			this.$main.prepend(parseHtml(`<div class="title">${removeDangerousTags(config.title)}</div>`)[0]);
		}

		this.component = config.component as Component;
		this.$componentWrapper.appendChild(this.component.getMainElement());
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
	}
}

var blockTypes = {
	"DtoMessageBlock": UiMessageBlock,
	"DtoCitationBlock": UiCitationBlock,
	"DtoComponentBlock": UiComponentBlock
};


