import {DtoComponentBlock} from "./generated";
import {Component, parseHtml, ServerObjectChannel} from "projector-client-object-api";
import {removeDangerousTags, ToolButton} from "projector-client-core-components";
import {AbstractBlockComponent} from "./AbstractBlockComponent";

export class ComponentBlock extends AbstractBlockComponent<DtoComponentBlock> {

	private $main: HTMLElement;
	private component: Component;
	private $componentWrapper: HTMLElement;
	private $toolButtons: Element;

	constructor(config: DtoComponentBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block ComponentBlock" style="height:${config.height}px">
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
			this.$main.prepend($(`<div class="title">${removeDangerousTags(config.title)}</div>`)[0]);
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
