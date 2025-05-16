import {ClientObject, ServerObjectChannel} from "projector-client-object-api";
import {DtoInlineStyleSheet, DtoInlineStyleSheetCommandHandler} from "./generated";

export class InlineStyleSheet implements ClientObject, DtoInlineStyleSheetCommandHandler {
	private stylesheetElement: HTMLStyleElement;

	constructor(config: DtoInlineStyleSheet, serverObjectChannel: ServerObjectChannel) {
		this.stylesheetElement = document.createElement('style');
		this.stylesheetElement.innerText = config.styleSheet;
		let head = document.querySelector('head');
		head.appendChild(this.stylesheetElement);
	}

	destroy(): void {
		this.stylesheetElement.remove();
	}

	public setStyleSheet(styleSheet: string) {
		this.stylesheetElement.innerText = styleSheet;
	}

}
