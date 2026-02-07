import type {PDFDocumentProxy, PDFPageProxy, PageViewport} from "pdfjs-dist";
import {Virtualizer, elementScroll, observeElementOffset, observeElementRect} from "@tanstack/virtual-core";
import type {VirtualItem} from "@tanstack/virtual-core";
import {UiPdfViewerConfig} from "../../generated/UiPdfViewerConfig";

export interface IContinuousVirtualRendererHost {
	getRenderRequestId: () => number;
	getPdfDocument: () => PDFDocumentProxy;
	getMaxPageNumber: () => number;
	getConfig: () => UiPdfViewerConfig;
	getPagesContainer: () => HTMLDivElement;
	getCanvasContainer: () => HTMLDivElement;
	getUuidClass: () => string;
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	applyPageBorderToCanvas: (canvas: HTMLCanvasElement) => void;
	updateDevRenderStats: () => void;
}

export class ContinuousVirtualRenderer {
	private readonly host: IContinuousVirtualRendererHost;
	private virtualizer: Virtualizer<HTMLDivElement, HTMLCanvasElement> = null;
	private $virtualInner: HTMLDivElement = null;
	private virtualizerCleanup: (() => void) = null;
	private readonly virtualOverscan = 2;
	private virtualizerScale: number = null;
	private virtualizerHiDpiScale: number = 1;
	private virtualEstimatePageHeight: number = 0;
	private virtualizerPageCount: number = null;
	private virtualizerPageSpacing: number = null;
	private virtualPageCanvases = new Map<number, HTMLCanvasElement>();
	private virtualPageRenderTasks = new Map<number, { scale: number, task: any }>();
	private virtualPageRenderScales = new Map<number, number>();
	private forceVirtualizerRecreate = false;

	constructor(host: IContinuousVirtualRendererHost) {
		this.host = host;
	}

	public async render(requestId: number) {
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}

		const firstPage = await this.host.getPdfDocument().getPage(1);
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}
		const {scale} = this.host.calculateZoomScale(firstPage);
		const viewport = firstPage.getViewport({scale});
		this.virtualEstimatePageHeight = Math.floor(viewport.height);
		this.virtualizerHiDpiScale = window.devicePixelRatio || 1;

		this.cancelRenderTasks();
		if (this.forceVirtualizerRecreate) {
			this.virtualPageRenderScales.clear();
		}
		this.ensureVirtualInnerContainer();
		this.ensureVirtualizer(scale);
		this.virtualizer._willUpdate();
		this.renderVirtualItems(requestId);
	}

	public teardown() {
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

	public cancelRenderTasks() {
		this.virtualPageRenderTasks.forEach((task) => {
			if (task?.task && typeof task.task.cancel === "function") {
				task.task.cancel();
			}
		});
		this.virtualPageRenderTasks.clear();
	}

	public markForRecreate() {
		this.forceVirtualizerRecreate = true;
	}

	public hasVirtualizer(): boolean {
		return this.virtualizer != null;
	}

	public scrollToIndex(index: number) {
		if (this.virtualizer && (this.virtualizer as any).scrollToIndex) {
			(this.virtualizer as any).scrollToIndex(index);
		}
	}

	private ensureVirtualInnerContainer() {
		if (!this.$virtualInner) {
			this.$virtualInner = document.createElement('div');
			this.$virtualInner.className = `${this.host.getUuidClass()} virtual-inner`;
			this.$virtualInner.style.position = "relative";
			this.$virtualInner.style.width = "100%";
		}

		const $pagesContainer = this.host.getPagesContainer();
		if (this.$virtualInner.parentElement !== $pagesContainer) {
			$pagesContainer.innerHTML = '';
			$pagesContainer.appendChild(this.$virtualInner);
		}
	}

	private ensureVirtualizer(scale: number) {
		const config = this.host.getConfig();
		const maxPageNumber = this.host.getMaxPageNumber();
		const previousScale = this.virtualizerScale;
		const needsNewVirtualizer = !this.virtualizer
			|| this.forceVirtualizerRecreate
			|| previousScale !== scale
			|| this.virtualizerPageCount !== maxPageNumber
			|| this.virtualizerPageSpacing !== config.pageSpacing;

		this.virtualizerScale = scale;
		if (needsNewVirtualizer) {
			if (this.virtualizerCleanup) {
				this.virtualizerCleanup();
				this.virtualizerCleanup = null;
			}
			this.virtualizer = new Virtualizer({
				count: maxPageNumber,
				getScrollElement: () => this.host.getCanvasContainer(),
				estimateSize: () => this.getVirtualEstimateSize(),
				gap: config.pageSpacing,
				overscan: this.virtualOverscan,
				getItemKey: (index) => index + 1,
				observeElementRect,
				observeElementOffset,
				scrollToFn: elementScroll,
				onChange: () => this.renderVirtualItems(this.host.getRenderRequestId())
			});
			this.virtualizerCleanup = this.virtualizer._didMount();
			this.virtualizerPageCount = maxPageNumber;
			this.virtualizerPageSpacing = config.pageSpacing;
			this.forceVirtualizerRecreate = false;
		} else {
			this.virtualizer.setOptions({
				...this.virtualizer.options,
				count: maxPageNumber,
				gap: config.pageSpacing,
				estimateSize: () => this.getVirtualEstimateSize()
			});
		}
	}

	private getVirtualEstimateSize(): number {
		return Math.max(1, this.virtualEstimatePageHeight || 1);
	}

	private renderVirtualItems(requestId: number) {
		if (requestId !== this.host.getRenderRequestId() || !this.virtualizer || !this.$virtualInner) {
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
		this.host.updateDevRenderStats();
	}

	private getOrCreateVirtualCanvas(pageNumber: number): HTMLCanvasElement {
		let canvas = this.virtualPageCanvases.get(pageNumber);
		if (!canvas) {
			canvas = document.createElement('canvas');
			canvas.className = this.host.getUuidClass();
			canvas.dataset.index = String(pageNumber - 1);
			if (this.host.getConfig().pageBorder) {
				this.host.applyPageBorderToCanvas(canvas);
			}
			this.virtualPageCanvases.set(pageNumber, canvas);
		}
		return canvas;
	}

	private renderVisibleVirtualPages(virtualItems: VirtualItem[], requestId: number) {
		if (requestId !== this.host.getRenderRequestId()) {
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
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}

		const page = await this.loadPageForRender(pageNumber, requestId);
		if (!page) {
			return;
		}
		if (requestId !== this.host.getRenderRequestId()) {
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
			if (requestId !== this.host.getRenderRequestId()) {
				return;
			}
			this.virtualPageRenderTasks.delete(pageNumber);
			this.virtualPageRenderScales.set(pageNumber, this.virtualizerScale);
			canvas.style.visibility = "visible";
			if (this.virtualizer) {
				this.virtualizer.measureElement(canvas);
			}
			this.host.updateDevRenderStats();
		} catch (error) {
			this.virtualPageRenderTasks.delete(pageNumber);
		}
	}

	private async loadPageForRender(pageNumber: number, requestId: number): Promise<PDFPageProxy | null> {
		if (requestId !== this.host.getRenderRequestId()) {
			return null;
		}
		return this.host.getPdfDocument().getPage(pageNumber);
	}
}
