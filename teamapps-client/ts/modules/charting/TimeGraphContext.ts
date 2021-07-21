export interface TimeGraphContext {
	showPopover(referenceElement: Element, content: Element|string): void;
	hidePopover(): void;
}