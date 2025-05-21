/* tslint:disable:indent */
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
    UiPlayground_RenderedEvent,
    UiPlaygroundCommandHandler,
    UiPlaygroundConfig
} from "../generated/UiPlaygroundConfig";
import {UiShadowConfig} from "../generated/UiShadowConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {createUiBorderCssString, createUiShadowCssString} from "./util/CssFormatUtil";
import {TeamAppsEvent} from "./util/TeamAppsEvent";


export class UiPlayground extends AbstractUiComponent<UiPlaygroundConfig> implements UiPlaygroundCommandHandler {
    onRendered: TeamAppsEvent<UiPlayground_RenderedEvent> = new TeamAppsEvent();

    private config: UiPlaygroundConfig;

    // internal state
    private uuidClass: string;
    private $main: HTMLDivElement;

    constructor(config: UiPlaygroundConfig, context: TeamAppsUiContext) {
        super(config, context);

        this.uuidClass = `UiPlayground-${generateUUID()}`;
        this.config = config;
        this.$main = parseHtml(`<div class="${this.uuidClass}">
            <h1>Playground</h1>
            <p>Title: ${this.config.title}</p>
        </div>`);
    }

    public doGetMainElement(): HTMLElement {
        return this.$main;
    }

    // Setters for Server API
    // -----------------------

    public setTitle(url: string) {
        this.config.title = url;
    }

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPlayground", UiPlayground);
