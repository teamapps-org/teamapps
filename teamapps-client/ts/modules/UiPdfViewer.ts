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

import * as pdfjsLib from "pdfjs-dist";
import {UiBorderConfig} from "../generated/UiBorderConfig";
import {UiPdfViewerCommandHandler, UiPdfViewerConfig} from "../generated/UiPdfViewerConfig";
import {UiPdfViewMode} from "../generated/UiPdfViewMode";
import {UiShadowConfig} from "../generated/UiShadowConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";

/**
 * Docs for Mozillas pdf.js: https://mozilla.github.io/pdf.js/
 * NPM Package: pdfjs-dist (CAUTION: pdfjs (without -dist) is another package!)
 */
export class UiPdfViewer extends AbstractUiComponent<UiPdfViewerConfig> implements UiPdfViewerCommandHandler {
    private config: UiPdfViewerConfig;

    // internal state
    private uuidClass: string;
    private $main: HTMLDivElement;
    private pdfDocument: any;
    private currentPage: number;

    constructor(config: UiPdfViewerConfig, context: TeamAppsUiContext) {
        super(config, context);

        this.uuidClass = `UiPdfViewer-${generateUUID()}`;
        this.config = config;
        this.$main = parseHtml(`<div id="${this.uuidClass}"></div>`);

        this.setUrl(config.url);

        // this.$componentWrapper = parseHtml(`<div class="UiDocumentViewer ${this.uuidClass}">
        //     <style></style>
        // 	<div class="toolbar-container"></div>
        // 	<div class="pages-container-wrapper">
        // 	    <div class="pages-container"></div>
        //     </div>
        // </div>`);

    }

    protected doGetMainElement(): HTMLElement {
        return this.$main;
    }

    /**
     * bjesuiter  2025-03-28: only supports page-based rendering for now, sicne it's easier to implement.
     * Continuous pdf page rendering is on the roadmap, but delayed indefinitely for now.
     *
     * @private
     */
    private renderPdfDocument() {
        throw new Error("Method not implemented.");
    }

    public setUrl(url: string) {
        pdfjsLib.getDocument(url).then((pdf) => {
            this.pdfDocument = pdf;
            this.renderPdfDocument();
        });
        throw new Error("Method not implemented.");
    }

    public setViewMode(viewMode: UiPdfViewMode) {
        this.config.viewMode = viewMode;
        this.renderPdfDocument();
    }

    public showPage(page: number) {
        this.currentPage = page;
        // TODO: switch viewer to the new page
    }

    public setZoomFactor(zoomFactor: number, zoomByAvailableWidth: boolean) {
        this.config.zoomFactor = zoomFactor;
        this.config.zoomByAvailableWidth = zoomByAvailableWidth;
        this.renderPdfDocument();
    }

    public setPageBorder(pageBorder: UiBorderConfig) {
        this.config.pageBorder = pageBorder;
        this.renderPdfDocument();
    }

    public setPageShadow(pageShadow: UiShadowConfig) {
        this.config.pageShadow = pageShadow;
        this.renderPdfDocument();
    }

    public setPadding(padding: number) {
        this.config.padding = padding;
        this.renderPdfDocument();
    }

    public setPageSpacing(pageSpacing: number) {
        this.config.pageSpacing = pageSpacing;
        this.renderPdfDocument();
    }

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPdfViewer", UiPdfViewer);
