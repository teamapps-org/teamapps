export interface CustomElement extends HTMLElement {
	/**
	 * Invoked each time the custom element is appended into a document-connected element.
	 * This will happen each time the node is moved, and may happen before the element's contents have been fully parsed.
	 * This especially, it means that the attributes might not have been read.
	 *
	 * Note: connectedCallback may be called once your element is no longer connected, use Node.isConnected to make sure.
	 */
	connectedCallback?(): void;

	/**
	 * nvoked each time the custom element is disconnected from the document's DOM.
	 */
	disconnectedCallback?(): void;

	/**
	 * Invoked each time the custom element is moved to a new document.
	 */
	adoptedCallback?(): void;
	/**
	 * Invoked each time one of the custom element's attributes is added, removed, or changed.
	 *
	 * Don't forget to define observedAttributes static get method. (public static get observedAttributes(): string[])
	 */
	attributeChangedCallback?(attributeName: string, oldValue: string, newValue: string): void;
}