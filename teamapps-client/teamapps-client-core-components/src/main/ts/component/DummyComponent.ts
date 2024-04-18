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

import {AbstractLegacyComponent, parseHtml, ServerObjectChannel, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {
	DtoDummyComponent,
	DtoDummyComponent_ClickedEvent,
	DtoDummyComponentCommandHandler,
	DtoDummyComponentEventSource
} from "../generated";
import DateTimeFormatOptions = Intl.DateTimeFormatOptions;

export class DummyComponent extends AbstractLegacyComponent<DtoDummyComponent> implements DtoDummyComponentCommandHandler, DtoDummyComponentEventSource {

	public readonly onClicked: TeamAppsEvent<DtoDummyComponent_ClickedEvent> = new TeamAppsEvent<DtoDummyComponent_ClickedEvent>();

	private static allDummies: DummyComponent[] = [];

	private $panel: HTMLElement;
	private lastResize: Date;
	private resizeCount: number = 0;
	private destroyed: boolean = false;
	private clickCount: number = 0;
	private jsClickCount: number = 0;
	private commandCount: number = 0;
	private text: string = "";

	constructor(config: DtoDummyComponent, serverChannel: ServerObjectChannel) {
		super(config, serverChannel);
		this.$panel = parseHtml('<div class="DummyComponent" id="' + config.id + '"></div>');
		this.$panel.addEventListener("click", () => {
			this.clickCount++;
			this.onClicked.fire({
				clickCount: this.clickCount
			});
			this.updateContent();
		});
		this.$panel.addEventListener('click', () => {
			this.jsClickCount++;
			this.updateContent();
		});
		this.text = config.text;
		this.updateContent();
		DummyComponent.allDummies.push(this);
	}


	public doGetMainElement(): HTMLElement {
		return this.$panel;
	}

	public destroy(): void {
		super.destroy();
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
resizeCount: ${this.resizeCount}<br>
lastResize: ${this.lastResize ? this.lastResize.toLocaleString(undefined, {dateStyle: 'medium', timeStyle: 'medium'} as DateTimeFormatOptions) : '-'}<br>
size: ${this.getWidth()} x ${this.getHeight()}<br> 
destroyed: <span class="${this.destroyed ? 'text-danger blink text-bold' : ''}">${this.destroyed}</span><br>
`;
	}

	public onResize(): void {
		this.lastResize = new Date();
		this.resizeCount++;
		this.updateContent();
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


