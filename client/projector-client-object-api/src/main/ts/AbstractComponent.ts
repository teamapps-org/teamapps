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
import {Component} from "./Component";
import { ServerObjectChannel } from "./ClientObject";
import {DtoComponent} from "./generated";
import {
	capitalizeFirstLetter,
	debounce,
	DebounceMode,
	DeferredExecutor,
	generateUUID,
	isProjectorEvent,
	StyleManager,
	ProjectorEvent, uncapitalizeFirstLetter
} from "./util";

export abstract class AbstractComponent<C extends DtoComponent = DtoComponent> implements Component {

	public readonly onVisibilityChanged: ProjectorEvent<boolean> = new ProjectorEvent();
	public readonly deFactoVisibilityChanged: ProjectorEvent<boolean> = new ProjectorEvent();
	public readonly onResized: ProjectorEvent<{ width: number; height: number }> = new ProjectorEvent();

	private width: number = 0;
	private height: number = 0;

	private visible = true;

	public displayedDeferredExecutor = new DeferredExecutor();

	protected styleManager: StyleManager;
	private cssUuid: string;

	constructor(protected config: C) {
		this.cssUuid = generateUUID();

		this.styleManager = new StyleManager(() => this.getMainElement(), `[data-css-id="${this.cssUuid}"]`, `[data-css-id="${this.cssUuid}"]`);
		this.displayedDeferredExecutor.invokeOnceWhenReady(() => this.styleManager.apply());
		this.visible = config.visible ?? false;
	}

	init(config: C, serverObjectChannel: ServerObjectChannel): any {
		this.styleManager.setStylesBySelector(config.stylesBySelector ?? {});
		this.registerEvents(serverObjectChannel);
	}

	private registerEvents(serverObjectChannel: ServerObjectChannel) {
		for (const propertyName in this) {
			if (propertyName.startsWith("on")) {
				let propertyValue = this[propertyName];
				if (isProjectorEvent(propertyValue)) {
					let eventName = uncapitalizeFirstLetter(propertyName.substring(2));
					propertyValue.addListener(eventObject => serverObjectChannel.sendEvent(eventName, eventObject))
				}
			}
		}
	}

	async invoke(name: string, params: any[]): Promise<any> {
		return await (this[name] as Function)(...params);
	}

	public getCssUuid(): string {
		return this.cssUuid ?? "";
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

			element.setAttribute("data-css-id", this.cssUuid);

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
			}, 100, DebounceMode.BOTH);
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
		this.styleManager.setStyle(selector, style);
		this.styleManager.apply()
	}

	public setClassNames(selector: string, classNames: { [className: string]: boolean }) {
		let targetElements = this.getElementForSelector(selector);
		if (targetElements.length === 0) {
			console.error("Cannot set css class on non-existing element. Selector: " + selector);
		}
		targetElements.forEach(el => {
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
