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
import {Virtualizer, elementScroll, observeElementOffset, observeElementRect} from "@tanstack/virtual-core";
import type {VirtualItem} from "@tanstack/virtual-core";

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
    private lastViewMode: UiPdfViewMode = null;

    // Dev/Helper elements
    private $devToolbar: HTMLElement;
    private $devRenderStats: HTMLDivElement;
    private devToolsInitialized: boolean = false;

    // UI / internal state
    private readonly uuidClass: string;
    private pdfDocument: PDFDocumentProxy;
    private currentPageNumber: number = 1;
    private maxPageNumber: number = 0;
    private virtualizer: Virtualizer<HTMLDivElement, HTMLCanvasElement> = null;
    private $virtualInner: HTMLDivElement = null;
    private virtualizerCleanup: (() => void) = null;
    private readonly virtualOverscan = 2;
    private virtualizerScale: number = null;
    private virtualizerHiDpiScale: number = 1;
    private virtualEstimatePageHeight: number = 0;
    private virtualEstimatePageWidth: number = 0;
    private virtualizerPageCount: number = null;
    private virtualizerPageSpacing: number = null;
    private virtualPageCanvases = new Map<number, HTMLCanvasElement>();
    private virtualPageRenderTasks = new Map<number, { scale: number, task: any }>();
    private virtualPageRenderScales = new Map<number, number>();
    private forceVirtualizerRecreate = false;

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
            <div id="dev-toolbar" class="${this.uuidClass}"></div>
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
                div.${this.uuidClass}#dev-render-stats {
                    position: absolute;
                    top: 2.25rem;
                    right: 2.5rem;
                    z-index: 2;
                    padding: 0.25rem 0.5rem;
                    border-radius: 0.35rem;
                    font-size: 0.85rem;
                    font-weight: 600;
                    color: #ffffff;
                    background: rgba(0, 0, 0, 0.5);
                    pointer-events: none;
                }
            </style>
        </div>`);
        this.$canvas = this.$main.querySelector<HTMLCanvasElement>(`canvas.${this.uuidClass}`);
        this.$canvasContainer = this.$main.querySelector<HTMLDivElement>(`div.canvas-container.${this.uuidClass}`);
        this.$pagesContainer = this.$main.querySelector<HTMLDivElement>(`#pagesContainer`);
        this.$styleTag = this.$main.querySelector<HTMLElement>(`style.${this.uuidClass}`);
        this.$devRenderStats = document.createElement("div");
        this.$devRenderStats.className = this.uuidClass;
        this.$devRenderStats.id = "dev-render-stats";
        this.$main.appendChild(this.$devRenderStats);

        // Ensure the component itself defines a height context for percentage/flex sizing.
        this.$main.style.display = "flex";
        this.$main.style.flexDirection = "column";
        this.$main.style.height = "100%";
        this.$main.style.minHeight = "0";
        this.$main.style.position = "relative";

        // Dev Helper UI
        this.$devToolbar = this.$main.querySelector<HTMLElement>('#dev-toolbar');
        this.renderDevToolsIfEnabled();

        this.renderCanvasContainer();
        this.updatePageSpacing();
        this.updateDevRenderStats();

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
        this.devToolsInitialized = true;
    }

    public renderDevToolsIfEnabled() {
        if (this.isDev()) {
            this.initDevToolsIfUninitialized();
            this.$devToolbar.style.display = "flex";
            this.$devRenderStats.style.display = "block";
            this.updateDevRenderStats();
        } else {
            this.$devToolbar.style.display = "none";
            this.$devRenderStats.style.display = "none";
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
            if (containerWidth <= 0) {
                return {scale, viewport: pdfViewport};
            }
            let newScale = containerWidth / pdfViewport.width;
            newScale = floorToPrecision(newScale, 2);
            // subtract save space for scrollbars
            newScale = Math.max(0.1, newScale - 0.1);

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
            const availableHeight = containerHeight - this.config.padding * 2;
            if (availableHeight <= 0) {
                return {scale, viewport: pdfViewport};
            }
            let newScale = availableHeight / pdfViewport.height;
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
        if (!this.pdfDocument) {
            return;
        }
        this.$canvasContainer.style.padding = `${this.config.padding}px`;
        const requestId = ++this.renderRequestId;
        this.updateDevRenderStats();

        if (this.lastViewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL && this.config.viewMode !== UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            this.teardownContinuousVirtualMode();
        }
        this.lastViewMode = this.config.viewMode;

        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            await this.renderPdfContinuousVirtualMode(requestId);
        } else if (this.config.viewMode === UiPdfViewMode.CONTINUOUS) {
            await this.renderPdfContinuousMode(requestId);
        } else {
            await this.renderPdfSinglePageMode(requestId);
        }
        this.updateDevRenderStats();
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

        this.applyPagesContainerLayoutForMode(UiPdfViewMode.SINGLE_PAGE);

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
        this.updateDevRenderStats();

    }

    private async renderPdfContinuousMode(requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        this.applyPagesContainerLayoutForMode(UiPdfViewMode.CONTINUOUS);

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
            this.updateDevRenderStats();
        }
    }

    private async renderPdfContinuousVirtualMode(requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        this.applyPagesContainerLayoutForMode(UiPdfViewMode.CONTINUOUS_VIRTUAL);

        const firstPage = await this.pdfDocument.getPage(1);
        if (requestId !== this.renderRequestId) {
            return;
        }
        const {scale} = this.calculateZoomScale(firstPage);
        const viewport = firstPage.getViewport({scale});
        this.virtualEstimatePageHeight = Math.floor(viewport.height);
        this.virtualEstimatePageWidth = Math.floor(viewport.width);
        this.virtualizerHiDpiScale = window.devicePixelRatio || 1;

        this.cancelVirtualRenderTasks();
        if (this.forceVirtualizerRecreate) {
            this.virtualPageRenderScales.clear();
        }
        this.ensureVirtualInnerContainer();
        this.ensureVirtualizer(scale);
        this.virtualizer._willUpdate();
        this.renderVirtualItems(requestId);
    }

    private ensureVirtualInnerContainer() {
        if (!this.$virtualInner) {
            this.$virtualInner = document.createElement('div');
            this.$virtualInner.className = `${this.uuidClass} virtual-inner`;
            this.$virtualInner.style.position = "relative";
            this.$virtualInner.style.width = "100%";
        }

        if (this.$virtualInner.parentElement !== this.$pagesContainer) {
            this.$pagesContainer.innerHTML = '';
            this.$pagesContainer.appendChild(this.$virtualInner);
        }
    }

    private ensureVirtualizer(scale: number) {
        const previousScale = this.virtualizerScale;
        const needsNewVirtualizer = !this.virtualizer
            || this.forceVirtualizerRecreate
            || previousScale !== scale
            || this.virtualizerPageCount !== this.maxPageNumber
            || this.virtualizerPageSpacing !== this.config.pageSpacing;

        this.virtualizerScale = scale;
        if (needsNewVirtualizer) {
            if (this.virtualizerCleanup) {
                this.virtualizerCleanup();
                this.virtualizerCleanup = null;
            }
            this.virtualizer = new Virtualizer({
                count: this.maxPageNumber,
                getScrollElement: () => this.$canvasContainer,
                estimateSize: () => this.getVirtualEstimateSize(),
                gap: this.config.pageSpacing,
                overscan: this.virtualOverscan,
                getItemKey: (index) => index + 1,
                observeElementRect,
                observeElementOffset,
                scrollToFn: elementScroll,
                onChange: () => this.renderVirtualItems(this.renderRequestId)
            });
            this.virtualizerCleanup = this.virtualizer._didMount();
            this.virtualizerPageCount = this.maxPageNumber;
            this.virtualizerPageSpacing = this.config.pageSpacing;
            this.forceVirtualizerRecreate = false;
        } else {
            this.virtualizer.setOptions({
                ...this.virtualizer.options,
                count: this.maxPageNumber,
                gap: this.config.pageSpacing,
                estimateSize: () => this.getVirtualEstimateSize()
            });
        }
    }

    private getVirtualEstimateSize(): number {
        return Math.max(1, this.virtualEstimatePageHeight || 1);
    }

    private renderVirtualItems(requestId: number) {
        if (requestId !== this.renderRequestId || !this.virtualizer || !this.$virtualInner) {
            return;
        }

        const virtualItems = this.virtualizer.getVirtualItems();
        this.$virtualInner.style.height = `${this.virtualizer.getTotalSize()}px`;
        this.$virtualInner.innerHTML = '';

        for (const virtualItem of virtualItems) {
            const pageNumber = virtualItem.index + 1;
            const canvas = this.getOrCreateVirtualCanvas(pageNumber);
            const wrapper = document.createElement('div');
            wrapper.dataset.index = String(virtualItem.index);
            wrapper.style.position = "absolute";
            wrapper.style.top = "0";
            wrapper.style.left = "0";
            wrapper.style.width = "100%";
            wrapper.style.display = "flex";
            wrapper.style.justifyContent = "center";
            wrapper.style.alignItems = "center";
            wrapper.style.transform = `translateY(${virtualItem.start}px)`;
            wrapper.style.height = `${Math.max(1, Math.floor(virtualItem.size))}px`;

            canvas.style.visibility = "visible";
            canvas.style.display = "block";
            canvas.style.margin = "0 auto";

            wrapper.appendChild(canvas);
            this.$virtualInner.appendChild(wrapper);
        }

        this.renderVisibleVirtualPages(virtualItems, requestId);
        this.updateDevRenderStats();
    }

    private getOrCreateVirtualCanvas(pageNumber: number): HTMLCanvasElement {
        let canvas = this.virtualPageCanvases.get(pageNumber);
        if (!canvas) {
            canvas = document.createElement('canvas');
            canvas.className = this.uuidClass;
            canvas.dataset.index = String(pageNumber - 1);
            if (this.config.pageBorder) {
                this.applyPageBorderToCanvas(canvas);
            }
            this.virtualPageCanvases.set(pageNumber, canvas);
        }
        return canvas;
    }

    private renderVisibleVirtualPages(virtualItems: VirtualItem[], requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        for (const virtualItem of virtualItems) {
            const pageNumber = virtualItem.index + 1;
            const canvas = this.virtualPageCanvases.get(pageNumber);
            if (!canvas) {
                continue;
            }
            const lastScale = this.virtualPageRenderScales.get(pageNumber);
            if (lastScale === this.virtualizerScale) {
                continue;
            }
            const inFlight = this.virtualPageRenderTasks.get(pageNumber);
            if (inFlight && inFlight.scale === this.virtualizerScale) {
                continue;
            }
            void this.renderVirtualPage(pageNumber, canvas, requestId);
        }
    }

    private async renderVirtualPage(pageNumber: number, canvas: HTMLCanvasElement, requestId: number) {
        if (requestId !== this.renderRequestId) {
            return;
        }

        const page = await this.loadPageForRender(pageNumber, requestId);
        if (!page) {
            return;
        }
        if (requestId !== this.renderRequestId) {
            return;
        }
        const viewport = page.getViewport({scale: this.virtualizerScale});
        const hiDPIScale = this.virtualizerHiDpiScale;
        const canvasContext = canvas.getContext('2d');

        canvas.width = Math.floor(viewport.width * hiDPIScale);
        canvas.height = Math.floor(viewport.height * hiDPIScale);
        canvas.style.width = Math.floor(viewport.width) + "px";
        canvas.style.height = Math.floor(viewport.height) + "px";
        if (canvasContext) {
            canvasContext.setTransform(1, 0, 0, 1, 0, 0);
            canvasContext.clearRect(0, 0, canvas.width, canvas.height);
        }

        const transform = hiDPIScale !== 1 ?
            [hiDPIScale, 0, 0, hiDPIScale, 0, 0] :
            null;

        const renderContext = {
            canvasContext,
            transform,
            viewport
        };

        const existingTask = this.virtualPageRenderTasks.get(pageNumber);
        if (existingTask && existingTask.task && typeof existingTask.task.cancel === "function") {
            existingTask.task.cancel();
        }

        const renderTask = page.render(renderContext);
        this.virtualPageRenderTasks.set(pageNumber, {scale: this.virtualizerScale, task: renderTask});
        try {
            await renderTask.promise;
            if (requestId !== this.renderRequestId) {
                return;
            }
            this.virtualPageRenderTasks.delete(pageNumber);
            this.virtualPageRenderScales.set(pageNumber, this.virtualizerScale);
            canvas.style.visibility = "visible";
            if (this.virtualizer) {
                this.virtualizer.measureElement(canvas);
            }
            this.updateDevRenderStats();
        } catch (error) {
            this.virtualPageRenderTasks.delete(pageNumber);
        }
    }

    private async loadPageForRender(pageNumber: number, requestId: number): Promise<PDFPageProxy | null> {
        if (requestId !== this.renderRequestId) {
            return null;
        }
        return this.pdfDocument.getPage(pageNumber);
    }

    private applyPagesContainerLayoutForMode(viewMode: UiPdfViewMode) {
        if (viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            this.$pagesContainer.style.display = "block";
            this.$pagesContainer.style.flexFlow = "";
            this.$pagesContainer.style.alignItems = "";
            this.$pagesContainer.style.position = "relative";
            this.$pagesContainer.style.gap = "";
        } else {
            this.$pagesContainer.style.display = "flex";
            this.$pagesContainer.style.flexFlow = "column nowrap";
            this.$pagesContainer.style.alignItems = "center";
            this.$pagesContainer.style.position = "";
            this.$pagesContainer.style.gap = `${this.config.pageSpacing}px`;
        }
    }

    private teardownContinuousVirtualMode() {
        if (this.virtualizerCleanup) {
            this.virtualizerCleanup();
            this.virtualizerCleanup = null;
        }
        if (this.virtualizer) {
            this.virtualizer = null;
        }
        if (this.$virtualInner && this.$virtualInner.parentElement) {
            this.$virtualInner.remove();
        }
        this.$virtualInner = null;
        this.virtualPageCanvases.clear();
        this.virtualPageRenderScales.clear();
        this.virtualPageRenderTasks.forEach((task) => {
            if (task?.task && typeof task.task.cancel === "function") {
                task.task.cancel();
            }
        });
        this.virtualPageRenderTasks.clear();
        this.virtualizerScale = null;
        this.virtualizerPageCount = null;
        this.virtualizerPageSpacing = null;
    }

    private cancelVirtualRenderTasks() {
        this.virtualPageRenderTasks.forEach((task) => {
            if (task?.task && typeof task.task.cancel === "function") {
                task.task.cancel();
            }
        });
        this.virtualPageRenderTasks.clear();
    }

    private async runVirtualAutoZoomStabilizationPass(zoomMode: UiPdfZoomMode) {
        if (this.config.viewMode !== UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            return;
        }
        if (zoomMode !== UiPdfZoomMode.TO_WIDTH && zoomMode !== UiPdfZoomMode.TO_HEIGHT) {
            return;
        }
        await new Promise<void>((resolve) => requestAnimationFrame(() => resolve()));
        this.config.zoomMode = zoomMode;
        this.teardownContinuousVirtualMode();
        await this.renderPdfDocument();
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

    private updateDevRenderStats() {
        if (!this.$devRenderStats) {
            return;
        }
        const canvasCount = this.$pagesContainer ? this.$pagesContainer.querySelectorAll("canvas").length : 0;
        const pageCount = this.pdfDocument?.numPages || this.maxPageNumber || 0;
        this.$devRenderStats.textContent = `${canvasCount} / ${pageCount}`;
    }

    // Setters for Server API
    // -----------------------™

    public async setUrl(url: string) {
        this.pdfDocument = await pdfjsLib.getDocument(url).promise;

        this.maxPageNumber = this.pdfDocument.numPages;
        this.updateDevRenderStats();

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
            this.updateDevRenderStats();
            if (this.config.viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
                if (!this.virtualizer) {
                    await this.renderPdfDocument();
                }
                if (this.virtualizer && (this.virtualizer as any).scrollToIndex) {
                    (this.virtualizer as any).scrollToIndex(page - 1);
                }
                return;
            }
            await this.renderPdfDocument();
        }
    }

    public async setZoomFactor(zoomFactor: number) {
        this.config.zoomFactor = zoomFactor;
        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            this.teardownContinuousVirtualMode();
        } else {
            this.forceVirtualizerRecreate = true;
        }
        await this.renderPdfDocument();
    }

    public async setZoomMode(zoomMode:UiPdfZoomMode) {
        this.config.zoomMode = zoomMode;
        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            this.teardownContinuousVirtualMode();
        } else {
            this.forceVirtualizerRecreate = true;
        }
        await this.renderPdfDocument();
        await this.runVirtualAutoZoomStabilizationPass(zoomMode);
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
