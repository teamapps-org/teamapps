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

import type {PDFDocumentProxy, PDFPageProxy, PageViewport} from "pdfjs-dist";
import * as pdfjsLib from "pdfjs-dist";
import {UiBorderConfig} from "../generated/UiBorderConfig";
import {
    UiPdfViewer_PdfInitializedEvent, UiPdfViewer_ZoomFactorAutoChangedEvent,
    UiPdfViewerCommandHandler,
    UiPdfViewerConfig
} from "../generated/UiPdfViewerConfig";
import {UiPdfViewMode} from "../generated/UiPdfViewMode";
import {UiShadowConfig} from "../generated/UiShadowConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {floorToPrecision} from "./util/precise-float-math";
import {UiPdfZoomMode} from "../generated/UiPdfZoomMode";

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
    // the config will be set in the constructor
    private config: UiPdfViewerConfig;

    // UI Elements
    private $main: HTMLDivElement;
    private $canvas: HTMLCanvasElement;
    private $canvasContainer: HTMLDivElement;
    private $pagesContainer: HTMLDivElement;
    private $styleTag: HTMLElement;
    // Monotonic render token to ignore stale async renders when view mode changes.
    private renderRequestId = 0;

    // Dev/Helper elements
    private $devToolbar: HTMLElement;
    private $devCurrentPageNr: HTMLElement;
    private $devMaxPageNr: HTMLElement;
    private $devCurrentZoom: HTMLElement;
    private devToolsInitialized: boolean = false;

    // UI / internal state
    private readonly uuidClass: string;
    private pdfDocument: PDFDocumentProxy;
    private currentPageNumber: number = 1;
    private maxPageNumber: number = 0;

    // Events for the server
    // ---------------------
    public readonly onPdfInitialized: TeamAppsEvent<UiPdfViewer_PdfInitializedEvent> = new TeamAppsEvent();

    /**
     * onZoomFactorAutoChanged will only fire when the factor was changed automatically by
     * UiPdfZoomMode.TO_WIDTH or UiPdfZoomMode.TO_HEIGHT
     */
    public readonly onZoomFactorAutoChanged: TeamAppsEvent<UiPdfViewer_ZoomFactorAutoChangedEvent> = new TeamAppsEvent();

    // Constructor
    // -----------
    constructor(config: UiPdfViewerConfig, context: TeamAppsUiContext) {
        super(config, context);

        this.uuidClass = `UiPdfViewer-${generateUUID()}`;
        this.config = config;
        this.$main = parseHtml(`
        <div class="${this.uuidClass}">
            <h1 class="${this.uuidClass}">PDF Viewer</h1>
            <div id="dev-toolbar" class="${this.uuidClass}">
                <button id="decrease">Decrease Page </button>
                <div>
                  <span id="currentPageNr"></span>
                  <span>/</span>
                  <span id="maxPageNr"></span>
                </div>
                <button id="increase">Increase Page </button>
                <button id="zoomIn">Zoom in</button>
                <div>
                    <span id="currentZoom"></span>
                </div>
                <button id="zoomOut">Zoom out</button>
                <button id="zoomToWidth">Zoom to width</button>
                <button id="zoomToHeight">Zoom to height</button>
            </div>
             <div class="canvas-container ${this.uuidClass}">
                <div id="pagesContainer" class="${this.uuidClass}">
                    <canvas class="${this.uuidClass}"></canvas>
                </div>
            </div>
            <style class="${this.uuidClass}">
                canvas.${this.uuidClass} {
                    /*border: 1px solid red;*/
                    display: flex;
                    flex-flow: row nowrap;
                    justify-content: center;
                    
                    /*overflow-x: auto;*/
                }
                div.${this.uuidClass}.canvas-container {
                    background: oklch(0.3 0 298);
                    border: 1px solid oklch(0.2 0 298);
                    display: flex; 
                    flex-flow: column nowrap;
                    gap: 1rem;
                    
                    /* NOTE: 
                      "safe center" (instead of only "center") is needed, 
                      otherwise the pdf is not scrollable in x-axis anymore 
                      when pdf gets bigger than the width of the canvas-container
                      support for this is still kinda new (Chrome as of June 2023), see
                      https://caniuse.com/mdn-css_properties_align-items_flex_context_safe_unsafe
                      
                      Since this behavior can be fixed by simply zooming out, I (bjesuiter) leave it like this for now.
                     */
                    align-items: safe center;
                    
                    flex: 1 1 auto;
                    min-height: 0;
                    overflow-y: auto;
                }
                div.${this.uuidClass}#pagesContainer {
                    display: flex;
                    flex-flow: column nowrap;
                    align-items: center;
                }
                div.${this.uuidClass}#dev-toolbar {
                    display: flex;
                    flex-flow: row nowrap;
                    gap: 1rem;
                    align-items: center;
                    margin: 0.5rem;
                }
            </style>
        </div>`);
        this.$canvas = this.$main.querySelector<HTMLCanvasElement>(`canvas.${this.uuidClass}`);
        this.$canvasContainer = this.$main.querySelector<HTMLDivElement>(`div.canvas-container.${this.uuidClass}`);
        this.$pagesContainer = this.$main.querySelector<HTMLDivElement>(`#pagesContainer`);
        this.$styleTag = this.$main.querySelector<HTMLElement>(`style.${this.uuidClass}`);

        // Ensure the component itself defines a height context for percentage/flex sizing.
        this.$main.style.display = "flex";
        this.$main.style.flexDirection = "column";
        this.$main.style.height = "100%";
        this.$main.style.minHeight = "0";

        // Dev Helper UI
        this.$devToolbar = this.$main.querySelector<HTMLElement>('#dev-toolbar');
        this.renderDevToolsIfEnabled();

        this.renderCanvasContainer();
        this.updatePageSpacing();

        // Load the pdf by setting it's url
        setTimeout(async () => {
            await this.setUrl(config.url);
        }, 0);
    }

    public doGetMainElement(): HTMLElement {
        return this.$main;
    }

    // Helper Functions
    // ----------------

    public isDev() {
        return this.config.showDevTools === true;
    }

    public initDevToolsIfUninitialized() {
        if (this.devToolsInitialized) {
            return;
        }

      // else: init dev tools!
        this.$devCurrentPageNr = this.$devToolbar.querySelector<HTMLElement>(`#currentPageNr`);
        this.$devMaxPageNr = this.$devToolbar.querySelector<HTMLElement>(`#maxPageNr`);
        this.$devCurrentZoom = this.$devToolbar.querySelector<HTMLElement>(`#currentZoom`);
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#increase`)
            .addEventListener("click", async (e) => {
                await this.showPage(this.currentPageNumber + 1);
            });
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#decrease`)
            .addEventListener("click", async (e) => {
                await this.showPage(this.currentPageNumber - 1);
            });
        // @bjesuiter: this zoomIn button does not trigger this.onZoomFactorAutoChanged,
        // since it simulates zooming in via server command, so the server already knows the new zoom factor
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#zoomIn`)
            .addEventListener("click", async (e) => {
                await this.setZoomFactor(this.config.zoomFactor + 0.1);
            });
        // @bjesuiter: this zoomOut button does not trigger this.onZoomFactorAutoChanged,
        // since it simulates zooming in via server command, so the server already knows the new zoom factor
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#zoomOut`)
            .addEventListener("click", async (e) => {
                await this.setZoomFactor(this.config.zoomFactor - 0.1);
            });
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#zoomToWidth`)
            .addEventListener("click", async (e) => {
                await this.setZoomMode(UiPdfZoomMode.TO_WIDTH);
            });
        this.$devToolbar.querySelector<HTMLButtonElement>(`button#zoomToHeight`)
            .addEventListener("click", async (e) => {
                await this.setZoomMode(UiPdfZoomMode.TO_HEIGHT);
            });

        this.devToolsInitialized = true;
    }

    public renderDevToolsIfEnabled() {
        if (this.isDev()) {
            this.initDevToolsIfUninitialized();
            this.$devToolbar.style.display = "flex";
            this.$main.querySelector<HTMLElement>(`h1.${this.uuidClass}`).style.display = "block";
        } else {
            this.$devToolbar.style.display = "none";
            this.$main.querySelector<HTMLElement>(`h1.${this.uuidClass}`).style.display = "none";
        }
    }

    public renderCanvasContainer() {
        const bgColor = this.config.backgroundColor;
        if (typeof bgColor === "string" && bgColor.length > 0) {
            this.$canvasContainer.style.backgroundColor = bgColor
        }

        const borderColor = this.config.borderColor;
        if (typeof borderColor === "string" && borderColor.length > 0) {
            this.$canvasContainer.style.borderColor = borderColor;
        }
    }

    public updatePageSpacing() {
        this.$pagesContainer.style.gap = `${this.config.pageSpacing}px`;
    }

    // Core Class Logic
    // -----------------

    private calculateZoomScale(page: PDFPageProxy): { scale: number, viewport: PageViewport } {
        let scale = [UiPdfZoomMode.TO_HEIGHT, UiPdfZoomMode.TO_WIDTH].includes(this.config.zoomMode)
            ? 1.0 : (this.config.zoomFactor ?? 1.0);

        let pdfViewport = page.getViewport({
            scale
        });

        if (this.config.zoomMode === UiPdfZoomMode.TO_WIDTH) {
            // calc the scale based on available width
            const containerWidth = this.$canvasContainer.clientWidth;
            let newScale = containerWidth / pdfViewport.width;
            newScale = floorToPrecision(newScale, 2);
            // subtract save space for scrollbars
            newScale -= 0.1

            // write the right scale factor back to the config
            this.config.zoomFactor = newScale;
            // use the newScale
            scale = newScale;
            pdfViewport = page.getViewport({scale});
            // stop the calculation from happening again until it is requested again
            this.config.zoomMode = UiPdfZoomMode.MANUAL;
            // send the event of the new zoom factor to the server
            this.onZoomFactorAutoChanged.fire({
                zoomFactor: newScale
            })
        }

        if (this.config.zoomMode === UiPdfZoomMode.TO_HEIGHT) {
            // calc the scale based on available heigth
            const containerHeight = this.$canvasContainer.clientHeight;
            // remove padding from container height to make sure that pdf page fits into container with padding
            let newScale = (containerHeight - this.config.padding * 2) / pdfViewport.height;
            newScale = floorToPrecision(newScale, 2);

            // write the right scale factor back to the config
            this.config.zoomFactor = newScale;
            // use the newScale
            scale = newScale;
            pdfViewport = page.getViewport({scale});
            // stop the calculation from happening again until it is requested again
            this.config.zoomMode = UiPdfZoomMode.MANUAL;
            // send the event of the new zoom factor to the server
            this.onZoomFactorAutoChanged.fire({
                zoomFactor: newScale
            });
        }

        return {scale, viewport: pdfViewport};
    }

    /**
     * bjesuiter  2025-03-28: only supports page-based rendering for now, sicne it's easier to implement.
     * Continuous pdf page rendering is on the roadmap, but delayed indefinitely for now.
     *
     * @private
     */
    private async renderPdfDocument() {
        this.$canvasContainer.style.padding = `${this.config.padding}px`;
        const requestId = ++this.renderRequestId;

        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS) {
            await this.renderPdfContinuousMode(requestId);
        } else {
            await this.renderPdfSinglePageMode(requestId);
        }
    }

    /**
     * Based on Example:
     * https://mozilla.github.io/pdf.js/examples/#:~:text=page*%20here%0A%7D)%3B-,Rendering%20the%20Page,-Each%20PDF%20page
     * @private
     */
    private async renderPdfSinglePageMode(requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        // Ensure the single-page canvas is in the DOM (continuous mode replaces it)
        this.$pagesContainer.innerHTML = '';
        this.$pagesContainer.appendChild(this.$canvas);

        const page = await this.pdfDocument.getPage(this.currentPageNumber);
        if (requestId !== this.renderRequestId) {
            return;
        }
        const {scale, viewport: pdfViewport} = this.calculateZoomScale(page);
        const hiDPIScale = window.devicePixelRatio || 1;

        const canvas = this.$canvas;
        const canvasContext = this.$canvas.getContext('2d');

        canvas.width = Math.floor(pdfViewport.width * hiDPIScale);
        canvas.height = Math.floor(pdfViewport.height * hiDPIScale);
        canvas.style.width = Math.floor(pdfViewport.width) + "px";
        canvas.style.height = Math.floor(pdfViewport.height) + "px";
        canvas.style.overflowX = "auto";
        // canvas.width = Math.floor(pdfViewport.width);
        // canvas.height = Math.floor(pdfViewport.height);

        const transform = hiDPIScale !== 1 ?
            [hiDPIScale, 0, 0, hiDPIScale, 0, 0] :
            null;

        const renderContext = {
            canvasContext,
            transform,
            viewport: pdfViewport
        };

        page.render(renderContext);

        // update dev output
        if (this.isDev()) {
            this.$devCurrentPageNr.innerText = String(this.currentPageNumber);
            this.$devCurrentZoom.innerText = String(scale.toFixed(1));
        }
    }

    private async renderPdfContinuousMode(requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        if (this.maxPageNumber > 5) {
            console.warn(`PdfViewer: CONTINUOUS mode with ${this.maxPageNumber} pages may impact performance. Consider using SINGLE_PAGE mode.`);
        }

        const firstPage = await this.pdfDocument.getPage(1);
        const {scale} = this.calculateZoomScale(firstPage);

        const $pagesContainer = this.$main.querySelector<HTMLElement>('#pagesContainer');
        $pagesContainer.innerHTML = '';

        for (let pageNum = 1; pageNum <= this.maxPageNumber; pageNum++) {
            if (requestId !== this.renderRequestId) {
                return;
            }
            const page = await this.pdfDocument.getPage(pageNum);
            const viewport = page.getViewport({scale});
            const hiDPIScale = window.devicePixelRatio || 1;

            const canvas = document.createElement('canvas');
            canvas.className = this.uuidClass;
            const canvasContext = canvas.getContext('2d');

            canvas.width = Math.floor(viewport.width * hiDPIScale);
            canvas.height = Math.floor(viewport.height * hiDPIScale);
            canvas.style.width = Math.floor(viewport.width) + "px";
            canvas.style.height = Math.floor(viewport.height) + "px";

            if (this.config.pageBorder) {
                this.applyPageBorderToCanvas(canvas);
            }

            const transform = hiDPIScale !== 1 ?
                [hiDPIScale, 0, 0, hiDPIScale, 0, 0] :
                null;

            const renderContext = {
                canvasContext,
                transform,
                viewport
            };

            $pagesContainer.appendChild(canvas);
            page.render(renderContext);
        }
    }

    private applyPageBorderToCanvas(canvas: HTMLCanvasElement) {
        const border = this.config.pageBorder;
        if (border) {
            if (border.top) {
                canvas.style.borderTop = `${border.top.thickness}px solid ${border.top.color}`;
            }
            if (border.right) {
                canvas.style.borderRight = `${border.right.thickness}px solid ${border.right.color}`;
            }
            if (border.bottom) {
                canvas.style.borderBottom = `${border.bottom.thickness}px solid ${border.bottom.color}`;
            }
            if (border.left) {
                canvas.style.borderLeft = `${border.left.thickness}px solid ${border.left.color}`;
            }
            if (border.borderRadius) {
                canvas.style.borderRadius = `${border.borderRadius}px`;
            }
        }
    }

    // Setters for Server API
    // -----------------------™

    public async setUrl(url: string) {
        this.pdfDocument = await pdfjsLib.getDocument(url).promise;

        this.maxPageNumber = this.pdfDocument.numPages;
        if (this.isDev()) {
            this.$devMaxPageNr.innerText = `${this.maxPageNumber}`;
        }

        await this.renderPdfDocument();

        // Tell the server the document has loaded after the first renderPdfDocument
        // since it is async, I can simply wait for it to finish
        this.onPdfInitialized.fire({
            numberOfPages: this.maxPageNumber,
        })
    }

    public async setShowDevTools(showDevTools:boolean) {
        this.config.showDevTools = showDevTools;
        this.renderDevToolsIfEnabled();
    }

    public async setViewMode(viewMode: UiPdfViewMode) {
        this.config.viewMode = viewMode;
        await this.renderPdfDocument();
    }

    public async showPage(page: number) {
        if (page >= 1 && page <= this.maxPageNumber ) {
            this.currentPageNumber = page;
            await this.renderPdfDocument();
        }
    }

    public async setZoomFactor(zoomFactor: number) {
        this.config.zoomFactor = zoomFactor;
        await this.renderPdfDocument();
    }

    public async setZoomMode(zoomMode:UiPdfZoomMode) {
        this.config.zoomMode = zoomMode;
        await this.renderPdfDocument();
    }

    public async setPageBorder(pageBorder: UiBorderConfig) {
        this.config.pageBorder = pageBorder;
        await this.renderPdfDocument();
    }


    public async setPadding(padding: number) {
        this.config.padding = padding;
        await this.renderPdfDocument();
    }

    /**
     * This is only relevant in CONTINUOUS render mode
     * @param pageSpacing
     */
    public async setPageSpacing(pageSpacing: number) {
        this.config.pageSpacing = pageSpacing;
        this.updatePageSpacing();
        await this.renderPdfDocument();
    }

    public async setBackgroundColor(cssColor: string) {
        this.config.backgroundColor = cssColor;
        this.renderCanvasContainer();
    }

    public async setBorderColor(cssColor: string) {
        this.config.borderColor = cssColor;
        this.renderCanvasContainer();
    }

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPdfViewer", UiPdfViewer);
