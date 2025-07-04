import {type ClientObject} from "projector-client-object-api";
import {type DtoLinkStyleSheet, type DtoLinkStyleSheetCommandHandler} from "./generated";

export class LinkStyleSheet implements ClientObject, DtoLinkStyleSheetCommandHandler {
	private linkElement: HTMLLinkElement;

	constructor(config: DtoLinkStyleSheet) {
		this.linkElement = document.createElement('link');
		this.linkElement.rel = 'stylesheet';
		this.linkElement.href = config.href;
		let head = document.querySelector('head')!;
		head.appendChild(this.linkElement);
	}

	destroy(): void {
		this.linkElement.remove();
	}

	public setHref(href: string) {
		this.linkElement.href = href;
	}

}
