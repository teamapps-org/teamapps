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
import {createUiBorderCssString, createUiShadowCssString, CssPropertyObject} from "./util/CssFormatUtil";
import type {PDFDocumentProxy} from "pdfjs-dist"

// @ts-ignore
// import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.mjs';
// pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;
// console.log(pdfjsWorker);

// import pdfjsWorker = require('url-loader!pdfjs-dist/build/pdf.worker.mjs');
// pdfjsLib.GlobalWorkerOptions.workerSrc = "resources/pdf.worker.mjs";
pdfjsLib.GlobalWorkerOptions.workerSrc = "static/pdf.worker.mjs";

/**
 * Docs for Mozillas pdf.js: https://mozilla.github.io/pdf.js/
 * NPM Package: pdfjs-dist (CAUTION: pdfjs (without -dist) is another package!)
 */
export class UiPdfViewer extends AbstractUiComponent<UiPdfViewerConfig> implements UiPdfViewerCommandHandler {
    private config: UiPdfViewerConfig;

    // internal state
    private uuidClass: string;
    private $main: HTMLDivElement;
    private $canvasTag: HTMLCanvasElement;
    private $styleTag: HTMLElement;
    private pdfDocument: PDFDocumentProxy;
    private currentPageNumber: number = 0;

    constructor(config: UiPdfViewerConfig, context: TeamAppsUiContext) {
        super(config, context);

        this.uuidClass = `UiPdfViewer-${generateUUID()}`;
        this.config = config;
        this.$main = parseHtml(`
        <div class="${this.uuidClass}">
            <style class="${this.uuidClass}"></style>
            <canvas class="${this.uuidClass}"></canvas>
        </div>`);
        this.$canvasTag = this.$main.querySelector<HTMLCanvasElement>(`canvas.${this.uuidClass}`);
        this.$styleTag = this.$main.querySelector<HTMLElement>(`style.${this.uuidClass}`);

        // Load the pdf by setting it's url
        this.setUrl(config.url);
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
    private async renderPdfDocument() {
        // Step 1: Validate configs
        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS) {
            // TODO @bjesuiter: how to do logging in these components idiomatically?
            throw new Error(`UiPdfViewMode.CONTINUOUS is not supported yet`);
        }

        // viewMode is SINGLE_PAGE from here on
        await this.renderPdfSinglePageMode();
    }

    /**
     * Based on Example:
     * https://mozilla.github.io/pdf.js/examples/#:~:text=page*%20here%0A%7D)%3B-,Rendering%20the%20Page,-Each%20PDF%20page
     * @private
     */
    private async renderPdfSinglePageMode() {
        const page = await this.pdfDocument.getPage(this.currentPageNumber);
        // TODO @bjesuiter: figure out what the result is for setting scale to 1.5
        const pdfViewport = page.getViewport({scale: 1});
        const hiDPIScale = window.devicePixelRatio || 1;

        const canvas = this.$canvasTag;
        const canvasContext = this.$canvasTag.getContext('2d');

        canvas.width = Math.floor(pdfViewport.width * hiDPIScale);
        canvas.height = Math.floor(pdfViewport.height * hiDPIScale);
        canvas.style.width = Math.floor(pdfViewport.width) + "px";
        canvas.style.height = Math.floor(pdfViewport.height) + "px";

        const transform = hiDPIScale !== 1 ?
            [hiDPIScale, 0, 0, hiDPIScale, 0, 0] :
            null;

        const renderContext = {
            canvasContext,
            transform,
            viewport: pdfViewport
        };

        page.render(renderContext);
    }

    private updateStyles() {
        const uiBorderCssString = createUiBorderCssString(this.config.pageBorder);
        const uiShadowCssString = createUiShadowCssString(this.config.pageShadow);
        this.$styleTag.innerHTML = '';
        // this.$styleTag.innerText = `
        // .${this.uuidClass} .page {
        //     ${createUiBorderCssString(this.pageBorder)}
        //     ${createUiShadowCssString(this.pageShadow)}
        // }
        // .${this.uuidClass} .page:not(:last-child) {
        //     margin-bottom: ${this.pageSpacing}px !important;
        // }`;
    }

    // Setters for Server API
    // -----------------------™

    public async setUrl(url: string) {
        const pdf = await pdfjsLib.getDocument(url).promise
        this.pdfDocument = pdf;
        this.renderPdfDocument();
    }

    public async setViewMode(viewMode: UiPdfViewMode) {
        this.config.viewMode = viewMode;
        this.renderPdfDocument();
    }

    public async showPage(page: number) {
        this.currentPageNumber = page;
        this.renderPdfDocument();
    }

    public async setZoomFactor(zoomFactor: number, zoomByAvailableWidth: boolean) {
        this.config.zoomFactor = zoomFactor;
        this.config.zoomByAvailableWidth = zoomByAvailableWidth;
        this.renderPdfDocument();
    }

    public async setPageBorder(pageBorder: UiBorderConfig) {
        this.config.pageBorder = pageBorder;
        this.renderPdfDocument();
    }

    public async setPageShadow(pageShadow: UiShadowConfig) {
        this.config.pageShadow = pageShadow;
        this.renderPdfDocument();
    }

    public async setPadding(padding: number) {
        this.config.padding = padding;
        this.renderPdfDocument();
    }

    public async setPageSpacing(pageSpacing: number) {
        this.config.pageSpacing = pageSpacing;
        this.renderPdfDocument();
    }

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPdfViewer", UiPdfViewer);
