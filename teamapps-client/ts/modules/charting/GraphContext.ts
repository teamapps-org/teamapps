export interface GraphContext {
	getPopperHandle(): {
		update(referenceElement: Element, content: Element|string): void;
		hide():void;
		destroy():void;
	};
}