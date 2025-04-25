export class StyleManager {

	private sheet: CSSStyleSheet;
	private styleElement: HTMLStyleElement;

	constructor(private parentElementSupplier: ()=>Node&ParentNode, private selectorPrefix, private fallbackSelector: string) {
		this.styleElement = document.createElement("style");
		document.head.appendChild(this.styleElement);
		this.sheet = this.styleElement.sheet;
		this.styleElement.remove();
	}

	public setStylesBySelector(stylesBySelector: { [selector: string]: {[name: string]: string}}) {
		for (const [selector, styles] of Object.entries(stylesBySelector)) {
			this.setStyle(selector, styles);
		}
	}

	public setStyle(selector: string, style: { [property: string]: string }) {
		if (selector == null || selector === '') {
			selector = this.fallbackSelector;
		} else {
			selector = `${this.selectorPrefix} ${selector}`;
		}

		const ruleIndex = Array.from(this.sheet.cssRules)
			.findIndex(rule => this.isStyleRule(rule) && rule.selectorText === selector);
		let rule: CSSStyleRule;
		if (ruleIndex >= 0) {
			rule = this.sheet.cssRules[ruleIndex] as CSSStyleRule;
		}
		if (rule == null) {
			let index = this.sheet.insertRule(selector + "{}");
			rule = this.sheet.cssRules[index] as CSSStyleRule;
		}
		// Object.assign(rule.style, style); does not seem to work with CSS variables...
		for (const [name, value] of Object.entries(style)) {
			rule.style.setProperty(name, value);
		}
	}

	private isStyleRule(rule: CSSRule): rule is CSSStyleRule {
		return rule.constructor.name === 'CSSStyleRule';
	}

	public apply() {
		let parentElement = this.parentElementSupplier();
		if (this.styleElement.parentElement != parentElement) {
			parentElement.appendChild(this.styleElement);
		}
		this.styleElement.innerHTML = styleSheetToString(this.sheet);
	}

	public destroy() {
		this.styleElement.remove();
	}

}

function styleSheetToString(stylesheet: CSSStyleSheet) {
	return stylesheet.cssRules
		? Array.from(stylesheet.cssRules)
			.map(rule => rule.cssText || '')
			.join('\n')
		: ''
}