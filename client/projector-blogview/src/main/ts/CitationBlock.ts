import {type DtoCitationBlock} from "./generated";
import {parseHtml, type ServerObjectChannel} from "projector-client-object-api";
import {removeDangerousTags, ToolButton} from "projector-client-core-components";
import {AbstractBlockComponent} from "./AbstractBlockComponent";

export class CitationBlock extends AbstractBlockComponent<DtoCitationBlock> {

	private $main: HTMLElement;
	private $toolButtons: Element;

	constructor(config: DtoCitationBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block CitationBlock">
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
