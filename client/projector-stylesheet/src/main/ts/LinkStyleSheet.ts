import {ClientObject, ServerObjectChannel} from "projector-client-object-api";
import {DtoLinkStyleSheet, DtoLinkStyleSheetCommandHandler} from "./generated";

export class LinkStyleSheet implements ClientObject, DtoLinkStyleSheetCommandHandler {
	private linkElement: HTMLLinkElement;

	constructor(config: DtoLinkStyleSheet, serverObjectChannel: ServerObjectChannel) {
		this.linkElement = document.createElement('link');
		this.linkElement.rel = 'stylesheet';
		this.linkElement.href = config.href;
		let head = document.querySelector('head');
		head.appendChild(this.linkElement);
	}

	destroy(): void {
		this.linkElement.remove();
	}

	public setHref(href: string) {
		this.linkElement.href = href;
	}

}
