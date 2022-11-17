export class StyleManager {

	private sheet: CSSStyleSheet;

	constructor(private parentElementSupplier: ()=>Node&ParentNode, private selectorPrefix, private fallbackSelector: string) {
		const tmpStyleElement = document.createElement("style");
		document.head.appendChild(tmpStyleElement);
		this.sheet = tmpStyleElement.sheet;
		tmpStyleElement.remove();
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
		let customStylesElement: HTMLStyleElement = parentElement.querySelector(":scope > .ta-custom-component-styles");
		if (customStylesElement == null) {
			customStylesElement = document.createElement("style");
			customStylesElement.classList.add("ta-custom-component-styles");
			parentElement.appendChild(customStylesElement);
		}

		customStylesElement.innerHTML = styleSheetToString(this.sheet);
	}

}

function styleSheetToString(stylesheet: CSSStyleSheet) {
	return stylesheet.cssRules
		? Array.from(stylesheet.cssRules)
			.map(rule => rule.cssText || '')
			.join('\n')
		: ''
}