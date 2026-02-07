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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {floorToPrecision} from "./util/precise-float-math";
import {UiPdfZoomMode} from "../generated/UiPdfZoomMode";
import {ContinuousVirtualRenderer} from "./pdf-viewer/ContinuousVirtualRenderer";
import type {IContinuousVirtualRenderContext} from "./pdf-viewer/ContinuousVirtualRenderer";
import {SinglePageRenderer} from "./pdf-viewer/SinglePageRenderer";
import type {ISinglePageRenderContext} from "./pdf-viewer/SinglePageRenderer";
import {ContinuousRenderer} from "./pdf-viewer/ContinuousRenderer";
import type {IContinuousRenderContext} from "./pdf-viewer/ContinuousRenderer";

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
	private readonly singlePageRenderer: SinglePageRenderer;
	private readonly continuousRenderer: ContinuousRenderer;
	private readonly continuousVirtualRenderer: ContinuousVirtualRenderer;
	// Monotonic token for setUrl() calls: only the most recent async load may apply state/events.
	private documentLoadRequestId: number = 0;

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

		this.singlePageRenderer = new SinglePageRenderer({
			calculateZoomScale: (page) => this.calculateZoomScale(page),
			updateDevRenderStats: () => this.updateDevRenderStats()
		});

		this.continuousRenderer = new ContinuousRenderer({
			calculateZoomScale: (page) => this.calculateZoomScale(page),
			applyPageBorderToCanvas: (canvas) => this.applyPageBorderToCanvas(canvas),
			updateDevRenderStats: () => this.updateDevRenderStats()
		});

		this.continuousVirtualRenderer = new ContinuousVirtualRenderer({
			calculateZoomScale: (page) => this.calculateZoomScale(page),
			applyPageBorderToCanvas: (canvas) => this.applyPageBorderToCanvas(canvas),
			updateDevRenderStats: () => this.updateDevRenderStats()
        });

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
		const zoomModeAtInvocation = this.config.zoomMode;
		const zoomResult = this.computeZoomScale(page, zoomModeAtInvocation, this.config.zoomFactor ?? 1.0);
		this.applyAutoZoomResult(zoomResult, zoomModeAtInvocation);
		return {scale: zoomResult.scale, viewport: zoomResult.viewport};
	}

	private computeZoomScale(page: PDFPageProxy, zoomMode: UiPdfZoomMode, zoomFactor: number): {
		scale: number,
		viewport: PageViewport,
		autoZoomFactor?: number
	} {
		let scale = [UiPdfZoomMode.TO_HEIGHT, UiPdfZoomMode.TO_WIDTH].includes(zoomMode)
			? 1.0 : zoomFactor;
		let viewport = page.getViewport({scale});

		if (zoomMode === UiPdfZoomMode.TO_WIDTH) {
			const containerWidth = this.$canvasContainer.clientWidth;
			if (containerWidth <= 0) {
				return {scale, viewport};
			}
			let autoZoomFactor = containerWidth / viewport.width;
			autoZoomFactor = floorToPrecision(autoZoomFactor, 2);
			autoZoomFactor = Math.max(0.1, autoZoomFactor - 0.1);
			scale = autoZoomFactor;
			viewport = page.getViewport({scale});
			return {scale, viewport, autoZoomFactor};
		}

		if (zoomMode === UiPdfZoomMode.TO_HEIGHT) {
			const containerHeight = this.$canvasContainer.clientHeight;
			const availableHeight = containerHeight - this.config.padding * 2;
			if (availableHeight <= 0) {
				return {scale, viewport};
			}
			let autoZoomFactor = availableHeight / viewport.height;
			autoZoomFactor = floorToPrecision(autoZoomFactor, 2);
			scale = autoZoomFactor;
			viewport = page.getViewport({scale});
			return {scale, viewport, autoZoomFactor};
		}

		return {scale, viewport};
	}

	private applyAutoZoomResult(zoomResult: { autoZoomFactor?: number }, zoomModeAtInvocation: UiPdfZoomMode) {
		if (zoomResult.autoZoomFactor == null) {
			return;
		}
		// Ignore stale updates when zoom mode changed concurrently.
		if (this.config.zoomMode !== zoomModeAtInvocation) {
			return;
		}
		this.config.zoomFactor = zoomResult.autoZoomFactor;
		this.config.zoomMode = UiPdfZoomMode.MANUAL;
		this.onZoomFactorAutoChanged.fire({
			zoomFactor: zoomResult.autoZoomFactor
		});
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
		await this.singlePageRenderer.render(this.createSinglePageRenderContext(requestId));
	}

	private async renderPdfContinuousMode(requestId: number) {
		if (requestId !== this.renderRequestId) {
			return;
		}

		this.applyPagesContainerLayoutForMode(UiPdfViewMode.CONTINUOUS);
		await this.continuousRenderer.render(this.createContinuousRenderContext(requestId));
	}

	private async renderPdfContinuousVirtualMode(requestId: number) {
		if (requestId !== this.renderRequestId) {
			return;
		}

		this.applyPagesContainerLayoutForMode(UiPdfViewMode.CONTINUOUS_VIRTUAL);
		await this.continuousVirtualRenderer.render(this.createContinuousVirtualRenderContext(requestId));
	}

	private createSinglePageRenderContext(requestId: number): ISinglePageRenderContext {
		return {
			requestId,
			getCurrentRenderRequestId: () => this.renderRequestId,
			pdfDocument: this.pdfDocument,
			currentPageNumber: this.currentPageNumber,
			pagesContainer: this.$pagesContainer,
			canvas: this.$canvas
		};
	}

	private createContinuousRenderContext(requestId: number): IContinuousRenderContext {
		return {
			requestId,
			getCurrentRenderRequestId: () => this.renderRequestId,
			pdfDocument: this.pdfDocument,
			maxPageNumber: this.maxPageNumber,
			pagesContainer: this.$pagesContainer,
			uuidClass: this.uuidClass,
			pageBorder: this.config.pageBorder
		};
	}

	private createContinuousVirtualRenderContext(requestId: number): IContinuousVirtualRenderContext {
		return {
			requestId,
			getCurrentRenderRequestId: () => this.renderRequestId,
			pdfDocument: this.pdfDocument,
			maxPageNumber: this.maxPageNumber,
			config: this.config,
			pagesContainer: this.$pagesContainer,
			canvasContainer: this.$canvasContainer,
			uuidClass: this.uuidClass
		};
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
        this.continuousVirtualRenderer.teardown();
    }

    private async runVirtualAutoZoomStabilizationPass(zoomMode: UiPdfZoomMode) {
        if (this.config.viewMode !== UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            return;
        }
        if (zoomMode !== UiPdfZoomMode.TO_WIDTH && zoomMode !== UiPdfZoomMode.TO_HEIGHT) {
            return;
        }
        // IMPORTANT:
        // Do not restore/preserve scroll position around virtual auto-zoom transitions.
        // Attempts to keep scroll anchors here caused unstable/broken virtual rendering.
        await new Promise<void>((resolve) => requestAnimationFrame(() => resolve()));
        this.config.zoomMode = zoomMode;
        this.teardownContinuousVirtualMode();
        await this.renderPdfDocument();
    }

    private async rerenderAfterZoomConfigurationChange(zoomModeForOptionalStabilization?: UiPdfZoomMode) {
        if (this.config.viewMode === UiPdfViewMode.CONTINUOUS_VIRTUAL) {
            this.teardownContinuousVirtualMode();
        } else {
            this.continuousVirtualRenderer.markForRecreate();
        }
        await this.renderPdfDocument();
        if (zoomModeForOptionalStabilization != null) {
            await this.runVirtualAutoZoomStabilizationPass(zoomModeForOptionalStabilization);
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
		// Every setUrl() starts a new load generation and invalidates all previous in-flight loads.
		const loadRequestId = ++this.documentLoadRequestId;
		try {
			const pdfDocument = await pdfjsLib.getDocument(url).promise;
			// Ignore stale completion from an older setUrl() call.
			if (loadRequestId !== this.documentLoadRequestId) {
				return;
			}

			this.pdfDocument = pdfDocument;
			this.maxPageNumber = this.pdfDocument.numPages;
			this.currentPageNumber = Math.max(1, Math.min(this.currentPageNumber, this.maxPageNumber));
			this.updateDevRenderStats();

			await this.renderPdfDocument();
			// Rendering may finish after a newer setUrl() call has started.
			if (loadRequestId !== this.documentLoadRequestId) {
				return;
			}

			// Tell the server the document has loaded after the first renderPdfDocument
			// since it is async, I can simply wait for it to finish
			this.onPdfInitialized.fire({
				numberOfPages: this.maxPageNumber,
			});
		} catch (error) {
			// Report only if this is still the active load request.
			if (loadRequestId !== this.documentLoadRequestId) {
				return;
			}
			console.error("UiPdfViewer: failed to load PDF URL", {url, error});
		}
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
                if (!this.continuousVirtualRenderer.hasVirtualizer()) {
                    await this.renderPdfDocument();
                }
                this.continuousVirtualRenderer.scrollToIndex(page - 1);
                return;
            }
            await this.renderPdfDocument();
        }
    }

    public async setZoomFactor(zoomFactor: number) {
        this.config.zoomFactor = zoomFactor;
        await this.rerenderAfterZoomConfigurationChange();
    }

    public async setZoomMode(zoomMode:UiPdfZoomMode) {
        this.config.zoomMode = zoomMode;
        await this.rerenderAfterZoomConfigurationChange(zoomMode);
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
