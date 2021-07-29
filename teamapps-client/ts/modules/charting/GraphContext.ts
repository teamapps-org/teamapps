export interface GraphContext {
	getPopperHandle(): PopperHandle;
}

export interface PopperHandle {
	update(referenceElement: Element, content: Element | string): void;
	hide(): void;
	destroy(): void;
}
