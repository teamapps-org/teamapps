/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import * as log from "loglevel";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {generateUUID} from "./Common";
import ResizeObserver from 'resize-observer-polyfill';
import {debounce, DebounceMode} from "./util/debounce";
import {DeferredExecutor} from "./util/DeferredExecutor";

export abstract class AbstractUiComponent<C extends UiComponentConfig = UiComponentConfig> implements AbstractUiComponent<C> {

	protected readonly logger: log.Logger = log.getLogger((<any>(this.constructor)).name || this.constructor.toString().match(/\w+/g)[1]);

	public readonly onVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);
	public readonly deFactoVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);
	public readonly onResized: TeamAppsEvent<{width: number; height: number}> = new TeamAppsEvent(this);

	private width: number = 0;
	private height: number = 0;

	private visible = true;

	public displayedDeferredExecutor = new DeferredExecutor();

	constructor(protected _config: C,
	            protected _context: TeamAppsUiContext) {
		if (_config.id == null) {
			_config.id = generateUUID();
		}

		// do this with timeout since the main dom element does not yet exist when executing this (subclass constructor gets called after this)
		setTimeout(() => {
			this.setVisible(_config.visible, false);
			if (_config.stylesBySelector != null) { // might be null when used via JavaScript API!
				Object.keys(_config.stylesBySelector).forEach(selector => this.setStyle(selector, _config.stylesBySelector[selector]));
			}

			let debouncedRelayout = debounce((entry: ResizeObserverEntry) => {
				this.reLayout(entry.contentRect.width, entry.contentRect.height);
			}, 300, DebounceMode.BOTH);
			const resizeObserver = new ResizeObserver(entries => {
				for (let entry of entries) {
					debouncedRelayout(entry);
				}
			});
			resizeObserver.observe(this.getMainDomElement());
		}, 0);
	}

	public getId(): string {
		return this._config.id;
	}

	public getTeamAppsType(): string {
		return this._config._type;
	}

	protected reLayout(width: number, height: number): void {
		let hasSize = width > 0 || height > 0;
		this.displayedDeferredExecutor.ready = hasSize;
		if (hasSize) {
			this.logger.trace("resize: " + this.getId());
			this.width = width;
			this.height = height;
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
		// empty default implementation
	}

	/**
	 * @return The main DOM element of this component.
	 */
	public abstract getMainDomElement(): HTMLElement;

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
		if (this.getMainDomElement() != null) { // might not have been rendered yet, if setVisible is already called in the constructor/initializer
			this.getMainDomElement().classList.toggle("invisible-component", !visible);
		}
		if (fireEvent) {
			this.onVisibilityChanged.fire(visible);
		}
	}

	// TODO change this implementation to generate style sheets instead of setting styles!
	public setStyle(selector:string, style: {[property: string]: string}) {
		let targetElement: HTMLElement[];
		if (!selector) {
			targetElement = [this.getMainDomElement()];
		} else {
			targetElement = Array.from((this.getMainDomElement() as HTMLElement).querySelectorAll(":scope " + selector));
		}
		if (targetElement.length === 0) {
			this.logger.error("Cannot set style on non-existing element. Selector: " + selector);
		} else {
			targetElement.forEach(t => Object.assign(t.style, style));
		}
	}

}
