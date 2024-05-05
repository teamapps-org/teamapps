export function extractCssValues(el: HTMLElement, properties: string[]) {
	return properties.reduce((properties, cssPropertyName) => {
		properties[cssPropertyName] = (el.style as CSSStyleDeclaration)[cssPropertyName] ?? null;
		return properties;
	}, {} as { [x: string]: string });
}

export function applyCss(el: HTMLElement, values: object) {
	Object.assign(el.style, values);
}

export function contentWidth(el: HTMLElement) {
	var styles = getComputedStyle(el)
	return el.clientWidth - parseFloat(styles.paddingLeft) - parseFloat(styles.paddingRight)
}