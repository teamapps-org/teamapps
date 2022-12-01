export class StyleManager {

	private sheet: CSSStyleSheet;
	private styleElement: HTMLStyleElement;

	constructor(private parentElementSupplier: ()=>Node&ParentNode, private selectorPrefix, private fallbackSelector: string) {
		this.styleElement = document.createElement("style");
		document.head.appendChild(this.styleElement);
		this.sheet = this.styleElement.sheet;
		this.styleElement.remove();
	}

	public setStyle(selector: string, style: { [property: string]: string }, replace?: boolean) {
		if (selector == null || selector === '') {
			selector = this.fallbackSelector;
		} else {
			selector = `${this.selectorPrefix} ${selector}`;
		}

		const ruleIndex = Array.from(this.sheet.rules)
			.findIndex(rule => this.isStyleRule(rule) && rule.selectorText === selector);
		let rule: CSSStyleRule;
		if (ruleIndex >= 0) {
			rule = this.sheet.rules[ruleIndex] as CSSStyleRule;
			if (replace) {
				this.sheet.removeRule(ruleIndex);
				rule = null;
			}
		}
		if (rule == null) {
			let index = this.sheet.insertRule(selector + "{}");
			rule = this.sheet.rules[index] as CSSStyleRule;
		}
		Object.assign(rule.style, style);
	}

	private isStyleRule(rule: CSSRule): rule is CSSStyleRule {
		return rule.type == rule.STYLE_RULE;
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