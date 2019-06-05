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
import {insertAtIndex} from "../Common";
import {TeamAppsEvent} from "./TeamAppsEvent";
import {UiComponent} from "../UiComponent";
import {SimpleObjectProxy} from "./SimpleObjectProxy";
import * as log from "loglevel";

const logger = log.getLogger("RefreshableComponentProxyHandle");

export class RefreshableComponentProxyHandle<T extends UiComponent> {
	public proxy: T;
	private _component: T;

	constructor(originalComponent: T) {
		this._component = originalComponent;
		this.proxy = new SimpleObjectProxy(originalComponent, {
			get: (targetToNotUse: T, property: string) => {
				const value = (this.component as any)[property];
				if (typeof value === "function") {
					return value.bind(this.component);
				} else {
					return value;
				}
			},
			set: (target: T, property: PropertyKey, value: any, receiver: any) => {
				(this.component as any)[property] = value;
				return true;
			}
		}) as T;
	}

	public get component() {
		return this._component;
	}

	public set component(newComponent: T) {
		const oldComponent = this._component;

		logger.debug(`copy old component's event listeners`);
		Object.keys(oldComponent).forEach((key: keyof UiComponent) => {
			if (key.indexOf("on") === 0 && oldComponent[key] instanceof TeamAppsEvent) {
				const event: TeamAppsEvent<any> = (oldComponent[key] as any);
				logger.debug(`copying ${event.getListeners().length} listeners for event: ${key}`);
				event.getListeners().forEach(listener => (newComponent as any)[key].addListener(listener));
			}
		});

		let parentElement: HTMLElement = oldComponent.getMainDomElement().parentElement;
		const elementIndex = this.getElementIndex(oldComponent.getMainDomElement());
		if (parentElement != null) {
			insertAtIndex(parentElement, newComponent.getMainDomElement(), elementIndex);
			newComponent.attachedToDom = true;
		}

		oldComponent.getMainDomElement().remove();
		oldComponent.destroy();
		this._component = newComponent;
	}

	private getElementIndex(childElement: HTMLElement) {
		let parentElement = childElement.parentElement;
		return parentElement != null ? Array.from(parentElement.children).indexOf(childElement) : null;
	}
}
