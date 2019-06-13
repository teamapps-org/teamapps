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
import {UiComponentCommandHandler, UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

export abstract class UiComponent<C extends UiComponentConfig = UiComponentConfig> implements UiComponentCommandHandler {

	protected readonly logger: log.Logger = log.getLogger((<any>(this.constructor)).name || this.constructor.toString().match(/\w+/g)[1]);

	public readonly onVisibilityChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);
	public readonly onResized: TeamAppsEvent<{width: number; height: number}> = new TeamAppsEvent(this);
	public readonly onAttachedToDomChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);

	private _attachedToDom = false;
	private width: number = 0;
	private height: number = 0;
	private firstReLayout = true;

	private visible = true;

	constructor(protected _config: C,
	            protected _context: TeamAppsUiContext) {
		setTimeout(() => {
			this.setVisible(_config.visible, false);
			if (_config.stylesBySelector != null) { // might be null when used via JavaScript API!
				Object.keys(_config.stylesBySelector).forEach(selector => this.setStyle(selector, _config.stylesBySelector[selector]));
			}
		}, 0);
	}

	public getId(): string {
		return this._config.id;
	}

	public getTeamAppsType(): string {
		return this._config._type;
	}

	get attachedToDom() {
		return this._attachedToDom;
	}

	set attachedToDom(attachedToDom) {
		let wasAttachedToDom = this._attachedToDom;
		this._attachedToDom = attachedToDom;
		if (attachedToDom && !wasAttachedToDom) {
			this.width = this.getMainDomElement().offsetWidth;
			this.height = this.getMainDomElement().offsetHeight;
			this.onAttachedToDom();
			this.onAttachedToDomChanged.fire(attachedToDom);
			this.reLayout();
		}
	}

	/**
	 * This method should get called from a container component when the available size for a child changes.
	 */
	public reLayout(force?: boolean): void {
		if (this.attachedToDom) {
			let availableWidth = this.getMainDomElement().offsetWidth;
			let availableHeight = this.getMainDomElement().offsetHeight;
			if (this.firstReLayout || force || availableWidth !== this.width || availableHeight !== this.height) {
				this.logger.trace("resize: " + this.getId());
				this.firstReLayout = false;
				this.width = availableWidth;
				this.height = availableHeight;
				this.onResized.fire({width: this.width, height: this.height});
				this.onResize();
			}
		}
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
	 * This method is used by the main TeamApps UI mechanism to get the element and attach it to the DOM.
	 */
	public abstract getMainDomElement(): HTMLElement;

	/**
	 * Override this method to execute code that cannot be executed before the field is attached to the DOM.
	 *
	 * Example 1:
	 *   An HTML/Flash video player with autoplay = true will not be able to play until it is attached to the DOM.
	 *   So the code for starting the video will need to be executed in this (overwritten) method.
	 * Example 2:
	 *   UiTable uses slickgrid. slickgrid can only initialize correctly when attached to the DOM.
	 *
	 * Components containing other components should set attachedToDom on their child components.
	 * This will in turn invoke this method on the children, so the call propagates recursively.
	 *
	 * Example:
	 *   A component with child components will call "child.attachedToDom = true" on its children.
	 */
	protected onAttachedToDom(): void {
		// do nothing (default implementation)
	}

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
