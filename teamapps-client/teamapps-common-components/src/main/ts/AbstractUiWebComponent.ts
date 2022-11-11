import {UiComponent} from "./UiComponent";
import {CustomElement} from "./custom-declarations/web-components";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiComponentConfig} from "./generated/UiComponentConfig";
import {debounce, DebounceMode} from "./util/debounce";
import {StyleManager} from "./util/StyleManager";

export abstract class AbstractUiWebComponent<C extends UiComponentConfig = UiComponentConfig> extends HTMLElement implements UiComponent<C>, CustomElement {

	readonly onVisibilityChanged: TeamAppsEvent<boolean>;
	readonly deFactoVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();
	readonly onResized: TeamAppsEvent<{ width: number; height: number }> = new TeamAppsEvent();

	private config: UiComponentConfig;
	private width: number = 0;
	private height: number = 0;
	protected styleManager: StyleManager;

	constructor() {
		super();
		this.attachShadow({mode: "open"});
		this.styleManager = new StyleManager(() => this.shadowRoot, '', ':host');
		let relayoutCalled = false;
		let debouncedRelayout: (size?: { width: number, height: number }) => void = debounce((size?: { width: number, height: number }) => {
			relayoutCalled = true;
			this.reLayout(size.width, size.height);
		}, 200, DebounceMode.BOTH);
		const resizeObserver = new ResizeObserver(entries => {
			for (let entry of entries) {
				debouncedRelayout({width: entry.contentRect.width, height: entry.contentRect.height});
			}
		});
		resizeObserver.observe(this);
		setTimeout(() => {
			// It seems like the resize observer does not always get called when the element gets attached
			if (!relayoutCalled) {
				debouncedRelayout({width: this.offsetWidth, height: this.offsetHeight});
			}
		}, 300); // TODO remove when problems with missing resizeObserver calls are solved...
	}

	protected reLayout(width: number, height: number): void {
		let hasSize = width > 0 || height > 0;
		if (this.width === width && this.height === height) {
			return;
		}
		this.width = width;
		this.height = height;
		if (hasSize) {
			console.debug("resize: " + this.getId());
			this.onResized.fire({width: this.width, height: this.height});
			this.onResize();
		}
		this.deFactoVisibilityChanged.fireIfChanged(hasSize);
	}

	/**
	 * This method gets called whenever the available size for this component changes.
	 */
	public onResize(): void {
		// do nothing (default implementation)
	}


	public getId(): string {
		return this.config.id ?? "";
	}

	public setConfig(config: C) {
		this.config = config;

		this.setAttribute("data-teamapps-id", this.config.id);

		if (this.config.debuggingId != null) {
			this.setAttribute("data-teamapps-debugging-id", this.config.debuggingId);
		}

		this.classList.toggle("invisible-component", this.config.visible == null ? false : !this.config.visible);
		if (this.config.stylesBySelector != null) { // might be null when used via JavaScript API!
			Object.keys(this.config.stylesBySelector).forEach(selector => this.setStyle(selector, this.config.stylesBySelector[selector]));
		}
		if (this.config.classNamesBySelector != null) { // might be null when used via JavaScript API!
			Object.keys(this.config.classNamesBySelector).forEach(selector => this.setClassNames(selector, this.config.classNamesBySelector[selector]));
		}
		if (this.config.attributesBySelector != null) { // might be null when used via JavaScript API!
			Object.keys(this.config.attributesBySelector).forEach(selector => this.setAttributes(selector, this.config.attributesBySelector[selector]));
		}
	}

	public setVisible(visible: boolean = true /*undefined == true!!*/, fireEvent = true) {
		this.classList.toggle("invisible-component", !visible);
		if (fireEvent) {
			this.onVisibilityChanged.fire(visible);
		}
	}

	isVisible(): boolean {
		return this.style.display !== 'none';
	}

	getMainElement(): HTMLElement {
		return this;
	}

	destroy(): void {
		this.remove();
	}

	public setStyle(selector: string, style: { [property: string]: string }) {
		this.styleManager.setStyle(selector, style, true);
		this.styleManager.apply()
	}

	connectedCallback() {
		this.styleManager.apply()
	}

	public setClassNames(selector: string, classNames: { [className: string]: boolean }) {
		let targetElement = this.getElementsForSelector(selector);
		if (targetElement.length === 0) {
			console.error("Cannot set css class on non-existing element. Selector: " + selector);
		}
		targetElement.forEach(el => {
			for (let [className, enabled] of Object.entries(classNames)) {
				el.classList.toggle(className, enabled);
			}
		});
	}

	public setAttributes(selector: string, attributes: { [property: string]: string }) {
		let targetElement = this.getElementsForSelector(selector);
		if (targetElement.length === 0) {
			console.error("Cannot set attribute on non-existing element. Selector: " + selector);
		}
		targetElement.forEach(el => {
			for (let [attribute, value] of Object.entries(attributes)) {
				if (value === "__ta-deleted-attribute__") {
					el.removeAttribute(attribute);
				} else {
					el.setAttribute(attribute, value);
				}
			}
		});
	}

	private getElementsForSelector(selector: string): HTMLElement[] {
		return !selector ? [this] : Array.from(this.shadowRoot.querySelectorAll(selector));
	}

}
