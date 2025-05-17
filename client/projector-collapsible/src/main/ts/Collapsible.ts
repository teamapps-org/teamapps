/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import {
    AbstractComponent,
    Component,
    parseHtml, ProjectorEvent,
    ServerObjectChannel,
    toggleElementCollapsed
} from "projector-client-object-api";
import {
    DtoCollapsible,
    DtoCollapsible_CollapseStateChangedEvent,
    DtoCollapsibleCommandHandler,
    DtoCollapsibleEventSource
} from "./generated";

export class Collapsible extends AbstractComponent<DtoCollapsible> implements DtoCollapsibleCommandHandler, DtoCollapsibleEventSource {

    readonly onCollapseStateChanged: ProjectorEvent<DtoCollapsible_CollapseStateChangedEvent> = new ProjectorEvent();

    private $main: HTMLDivElement;
    private $icon: HTMLElement;
    private $caption: HTMLElement;
    private $body: HTMLElement;
    private $expander: Element;

    constructor(config: DtoCollapsible, serverObjectChannel: ServerObjectChannel) {
        super(config);
        this.$main = parseHtml(`<div class="Collapsible">
	<div class="collapsible-header">
		<div class="expander teamapps-expander ${config.collapsed ? '' : 'expanded'}"></div>
		<div class="icon img img-16"></div>
		<div class="caption"></div>
	</div>
	<div class="collapsible-body"></div>
</div>`);
        this.$expander = this.$main.querySelector(":scope .expander");
        this.$icon = this.$main.querySelector(":scope .icon");
        this.$caption = this.$main.querySelector(":scope .caption");
        this.$body = this.$main.querySelector(":scope .collapsible-body");

        this.setIcon(config.icon);
        this.setCaption(config.caption);
        this.setContent(config.content);
        this.setCollapsed(config.collapsed);

        this.$expander.addEventListener("click", evt => {
            let collapsed = !this.config.collapsed;
            this.setCollapsed(collapsed);
            this.onCollapseStateChanged.fire({collapsed});
        });
    }

    public doGetMainElement(): HTMLElement {
        return this.$main;
    }

    setContent(content: unknown): void {
        this.$body.innerHTML = "";
        if (content != null) {
            this.$body.append((content as Component).getMainElement());
        }
    }

    setCollapsed(collapsed: boolean): any {
        this.config.collapsed = collapsed;
        this.$main.classList.toggle("collapsed", collapsed);
        this.$expander.classList.toggle("expanded", !collapsed);

        this.$main.classList.add("expanding")
        toggleElementCollapsed(this.$body, collapsed, 300, "hidden", () => {
            this.$main.classList.remove("expanding");
        });
    }

    setCaption(caption: string): any {
        this.config.caption = caption;
        this.$caption.innerText = caption;
    }

    setIcon(icon: string): any {
        this.config.icon = icon;
        this.$icon.style.backgroundImage = `url('${icon}')`;
    }

}
