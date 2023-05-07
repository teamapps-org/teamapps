/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
import {DtoComponent as DtoComponentConfig} from "../generated/DtoComponent";
import {generateUUID} from "../util/string-util";
import {debounce, DebounceMode} from "../util/debounce";
import {DeferredExecutor} from "../util/DeferredExecutor";
import {Component} from "./Component";
import {StyleManager} from "../util/StyleManager";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export abstract class AbstractComponent<C extends DtoComponentConfig = DtoComponentConfig> implements Component<C> {

	public readonly onVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();
	public readonly deFactoVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();
	public readonly onResized: TeamAppsEvent<{ width: number; height: number }> = new TeamAppsEvent();

	private width: number = 0;
	private height: number = 0;

	private visible = true;

	public displayedDeferredExecutor = new DeferredExecutor();

	protected styleManager: StyleManager;

	constructor(protected config: C) {
		if (config.id == null) {
			config.id = generateUUID();
		}

		this.styleManager = new StyleManager(() => this.getMainElement(), `[data-teamapps-id=${this.getId()}]`, `[data-teamapps-id=${this.getId()}]`);
		this.displayedDeferredExecutor.invokeOnceWhenReady(() => this.styleManager.apply());

		this.visible = config.visible ?? false;
	}

	public getId(): string {
		return this.config.id ?? "";
	}

	public getTeamAppsType(): string {
		return this.config._type;
	}

	protected reLayout(width: number, height: number): void {
		let hasSize = width > 0 || height > 0;
		if (this.width === width && this.height === height) {
			return;
		}
		this.width = width;
		this.height = height;
		this.displayedDeferredExecutor.ready = hasSize;
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

	/**
	 * This method is called when the component gets destroyed.
	 * It should be used to release any resources the component holds. This can be:
	 *   - additional DOM elements that are not children of the component's main DOM element, e.g. attached to the document's body.
	 *   - other resources that are not released just by removing the component's main DOM element
	 */
	public destroy() {
		this.getMainElement().remove();
		this.styleManager.destroy();
	}

	private firstTimeGetMainElementCalled = true;

	/**
	 * @return The main DOM element of this component.
	 */
	public getMainElement(): HTMLElement {
		let element = this.doGetMainElement();

		if (this.firstTimeGetMainElementCalled) {
			this.firstTimeGetMainElementCalled = false;

			element.setAttribute("data-teamapps-id", this.config.id);

			if (this.config.debuggingId != null) {
				element.setAttribute("data-teamapps-debugging-id", this.config.debuggingId);
			}

			element.classList.toggle("invisible-component", this.visible == null ? false : !this.visible);
			if (this.config.stylesBySelector != null) {
				Object.keys(this.config.stylesBySelector).forEach(selector => this.setStyle(selector, this.config.stylesBySelector[selector]));
			}
			if (this.config.classNamesBySelector != null) {
				Object.keys(this.config.classNamesBySelector).forEach(selector => this.setClassNames(selector, this.config.classNamesBySelector[selector]));
			}
			if (this.config.attributesBySelector != null) {
				Object.keys(this.config.attributesBySelector).forEach(selector => this.setAttributes(selector, this.config.attributesBySelector[selector]));
			}

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
			resizeObserver.observe(element);
			setTimeout(() => {
				// It seems like the resize observer does not always get called when the element gets attached
				if (!relayoutCalled) {
					debouncedRelayout({width: element.offsetWidth, height: element.offsetHeight});
				}
			}, 300); // TODO remove when problems with missing resizeObserver calls are solved...
		}

		return element;
	};

	protected abstract doGetMainElement(): HTMLElement;

	public getWidth(): number {
		return this.width;
	}

	public getHeight(): number {
		return this.height;
	}

	public isVisible() {
		return this.visible;
	}

	public setVisible(visible: boolean = true /*undefined == true!!*/, fireEvent = true) {
		this.visible = visible;
		if (this.getMainElement() != null) { // might not have been rendered yet, if setVisible is already called in the constructor/initializer
			this.getMainElement().classList.toggle("invisible-component", !visible);
		}
		if (fireEvent) {
			this.onVisibilityChanged.fire(visible);
		}
	}

	public setStyle(selector: string, style: { [property: string]: string }) {
		this.styleManager.setStyle(selector, style, true);
		this.styleManager.apply()
	}

	public setClassNames(selector: string, classNames: { [className: string]: boolean }) {
		let targetElement = this.getElementForSelector(selector);
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
		let targetElement = this.getElementForSelector(selector);
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

	private getElementForSelector(selector: string) {
		let targetElement: HTMLElement[];
		if (!selector) {
			targetElement = [this.getMainElement()];
		} else {
			targetElement = Array.from((this.getMainElement() as HTMLElement).querySelectorAll(":scope " + selector));
		}
		return targetElement;
	}
}
