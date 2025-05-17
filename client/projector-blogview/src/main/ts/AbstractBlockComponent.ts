import {DtoBlock} from "./generated";

export abstract class AbstractBlockComponent<C extends DtoBlock> {
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