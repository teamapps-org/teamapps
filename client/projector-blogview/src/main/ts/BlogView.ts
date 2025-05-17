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
	executeAfterAttached,
	insertAfter,
	insertBefore,
	parseHtml,
	ServerObjectChannel
} from "projector-client-object-api";
import {BlockAlignment, DtoBlock, DtoBlogView} from "./generated";
import {MessageBlock} from "./MessageBlock";
import {CitationBlock} from "./CitationBlock";
import {AbstractBlockComponent} from "./AbstractBlockComponent";
import {ComponentBlock} from "./ComponentBlock";

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

var blockTypes = {
	"DtoMessageBlock": MessageBlock,
	"DtoCitationBlock": CitationBlock,
	"DtoComponentBlock": ComponentBlock
}

export class BlogView extends AbstractComponent<DtoBlogView> {

	private $component: HTMLElement;
	private rows: Row[] = [];

	constructor(config: DtoBlogView, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$component = parseHtml(`<div class="BlogView"></div>`);

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



