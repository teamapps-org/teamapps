import {type DtoBlock} from "./generated";

export abstract class AbstractBlockComponent<C extends DtoBlock> {
	protected config: C;

	constructor(config: C) {
		this.config = config;
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