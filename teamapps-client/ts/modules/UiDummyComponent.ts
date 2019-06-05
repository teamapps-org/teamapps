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

import * as moment from "moment-timezone";

import Moment = moment.Moment;
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiDummyComponent_ClickedEvent, UiDummyComponentCommandHandler, UiDummyComponentConfig, UiDummyComponentEventSource} from "../generated/UiDummyComponentConfig";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";

export class UiDummyComponent extends UiComponent<UiDummyComponentConfig> implements UiDummyComponentCommandHandler, UiDummyComponentEventSource {

	public readonly onClicked: TeamAppsEvent<UiDummyComponent_ClickedEvent> = new TeamAppsEvent<UiDummyComponent_ClickedEvent>(this);

	private static allDummies: UiDummyComponent[] = [];

	private $panel: HTMLElement;
	private lastResize: Moment;
	private resizeCount: number = 0;
	private destroyed: boolean = false;
	private clickCount: number = 0;
	private jsClickCount: number = 0;
	private hasBeenAttachedToDom: boolean = false;
	private commandCount: number = 0;
	private text: string = "";

	constructor(config: UiDummyComponentConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = parseHtml('<div class="UiDummyComponent" id="' + config.id + '"></div>');
		this.$panel.addEventListener("click", () => {
			this.clickCount++;
			this.onClicked.fire(EventFactory.createUiDummyComponent_ClickedEvent(this.getId(), this.clickCount));
			this.updateContent();
		});
		this.$panel.addEventListener('click', () => {
			this.jsClickCount++;
			this.updateContent();
		});
		this.text = config.text;
		this.updateContent();
		UiDummyComponent.allDummies.push(this);
	}


	public getMainDomElement(): HTMLElement {
		return this.$panel;
	}

	public destroy(): void {
		this.destroyed = true;
		this.updateContent();
	}


	private generateText() {
		return `
This is a dummy component!<br>
id: <span>${this.getId()}</span><br>
text: <span>${this.text}</span><br>
clickCount: <span>${this.clickCount}</span><br>
jsClickCount: <span>${this.jsClickCount}</span><br>
commandCount: <span>${this.commandCount}</span><br>
attached: <span class="${this.attachedToDom ? '' : 'text-danger blink text-bold'}">${this.attachedToDom}</span><br>
hasBeenAttached: ${this.hasBeenAttachedToDom}<br>
resizeCount: ${this.resizeCount}<br>
lastResize: ${this.lastResize ? this.lastResize.format('HH:mm:ss.SSS') : '-'}<br>
size: ${this.getWidth()} x ${this.getHeight()}<br> 
destroyed: <span class="${this.destroyed ? 'text-danger blink text-bold' : ''}">${this.destroyed}</span><br>
`;
	}

	public onResize(): void {
		this.lastResize = moment();
		this.resizeCount++;
		this.updateContent();
	}

	protected onAttachedToDom(): void {
		this.hasBeenAttachedToDom = true;
		this.updateContent();
		document.body.addEventListener("DOMNodeRemoved", (e: Event) => {
			let selfOrParent: Node = this.getMainDomElement();
			while (selfOrParent != null) {
				if (selfOrParent === e.target) {
					setTimeout(() => this.updateContent());
					break;
				}
				selfOrParent = selfOrParent.parentNode;
			}
		});
	}

	private updateContent() {
		this.$panel.innerHTML = this.generateText();
	}

	setText(text: string): void {
		this.commandCount++;
		this.text = text;
		this.updateContent();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiDummyComponent", UiDummyComponent);
