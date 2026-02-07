import type {PDFPageProxy, PageViewport} from "pdfjs-dist";
import {Virtualizer, elementScroll, observeElementOffset, observeElementRect} from "@tanstack/virtual-core";
import type {VirtualItem} from "@tanstack/virtual-core";
import {UiPdfViewerConfig} from "../../generated/UiPdfViewerConfig";
import type {BaseRenderContext} from "./BaseRenderContext";

export type ContinuousVirtualRenderContext = BaseRenderContext & {
	maxPageNumber: number;
	config: UiPdfViewerConfig;
	pagesContainer: HTMLDivElement;
	canvasContainer: HTMLDivElement;
	uuidClass: string;
};

export type ContinuousVirtualRendererCallbacks = {
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	applyPageBorderToCanvas: (canvas: HTMLCanvasElement) => void;
	updateDevRenderStats: () => void;
};

export class ContinuousVirtualRenderer {
	private readonly callbacks: ContinuousVirtualRendererCallbacks;
	private activeContext: ContinuousVirtualRenderContext = null;
	private virtualizer: Virtualizer<HTMLDivElement, HTMLCanvasElement> = null;
	private $virtualInner: HTMLDivElement = null;
	private readonly virtualOverscan = 2;
	private virtualizerScale: number = null;
	private virtualizerHiDpiScale: number = 1;
	private virtualEstimatePageHeight: number = 0;
	private virtualPageCanvases = new Map<number, HTMLCanvasElement>();
	private virtualPageRenderTasks = new Map<number, { scale: number, task: any }>();
	private virtualPageRenderScales = new Map<number, number>();
	private virtualMeasuredPageHeights = new Map<number, number>();
	private forceVirtualizerRecreate = false;
	private observedScrollElement: HTMLDivElement = null;
	private scrollResizeObserver: ResizeObserver = null;
	private scheduledRenderAnimationFrameId: number = null;

	constructor(callbacks: ContinuousVirtualRendererCallbacks) {
		this.callbacks = callbacks;
	}

	public async render(context: ContinuousVirtualRenderContext) {
		this.activeContext = context;
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}

		const firstPage = await context.pdfDocument.getPage(1);
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}
		const {scale} = this.callbacks.calculateZoomScale(firstPage);
		const viewport = firstPage.getViewport({scale});
		this.virtualEstimatePageHeight = Math.floor(viewport.height);
		this.virtualizerHiDpiScale = window.devicePixelRatio || 1;

		this.cancelRenderTasks();
		if (this.forceVirtualizerRecreate || (this.virtualizerScale != null && this.virtualizerScale !== scale)) {
			this.virtualPageRenderScales.clear();
			this.virtualMeasuredPageHeights.clear();
		}
		this.virtualizerScale = scale;
		this.forceVirtualizerRecreate = false;

		this.ensureVirtualInnerContainer();
		this.ensureScrollObservers();
		this.renderVirtualItems(context.requestId);
	}

	public teardown() {
		this.activeContext = null;
		this.detachScrollObservers();
		this.cancelScheduledVirtualRender();
		if (this.virtualizer) {
			this.virtualizer = null;
		}
		if (this.$virtualInner && this.$virtualInner.parentElement) {
			this.$virtualInner.remove();
		}
		this.$virtualInner = null;
		this.virtualPageCanvases.clear();
		this.virtualPageRenderScales.clear();
		this.virtualMeasuredPageHeights.clear();
		this.virtualPageRenderTasks.forEach((task) => {
			if (task?.task && typeof task.task.cancel === "function") {
				task.task.cancel();
			}
		});
		this.virtualPageRenderTasks.clear();
		this.virtualizerScale = null;
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
		if (index < 0) {
			return;
		}
		const context = this.activeContext;
		if (!context) {
			return;
		}
		const scrollElement = context.canvasContainer;
		if (!scrollElement) {
			return;
		}
		scrollElement.scrollTop = this.getEstimatedOffsetForIndex(index);
		this.scheduleVirtualRender();
	}

	private getEstimatedOffsetForIndex(index: number): number {
		const context = this.activeContext;
		if (!context) {
			return 0;
		}
		const maxPageNumber = context.maxPageNumber;
		const clampedIndex = Math.max(0, Math.min(index, Math.max(0, maxPageNumber - 1)));
		const gap = Math.max(0, context.config.pageSpacing || 0);
		let offset = 0;

		for (let i = 0; i < clampedIndex; i++) {
			offset += this.getVirtualEstimateSize(i + 1);
			if (i < maxPageNumber - 1) {
				offset += gap;
			}
		}

		return Math.max(0, offset);
	}

	private ensureVirtualInnerContainer() {
		if (!this.$virtualInner) {
			this.$virtualInner = document.createElement('div');
			this.$virtualInner.className = `${this.activeContext?.uuidClass ?? ""} virtual-inner`;
			this.$virtualInner.style.position = "relative";
			this.$virtualInner.style.width = "100%";
		}

		const context = this.activeContext;
		if (!context) {
			return;
		}
		const $pagesContainer = context.pagesContainer;
		if (this.$virtualInner.parentElement !== $pagesContainer) {
			$pagesContainer.innerHTML = '';
			$pagesContainer.appendChild(this.$virtualInner);
		}
	}

	private ensureScrollObservers() {
		const context = this.activeContext;
		if (!context) {
			return;
		}
		const scrollElement = context.canvasContainer;
		if (!scrollElement || this.observedScrollElement === scrollElement) {
			return;
		}

		this.detachScrollObservers();
		this.observedScrollElement = scrollElement;
		this.observedScrollElement.addEventListener("scroll", this.onScroll, {passive: true});
		window.addEventListener("resize", this.onWindowResize);

		if (typeof ResizeObserver !== "undefined") {
			this.scrollResizeObserver = new ResizeObserver(() => {
				this.scheduleVirtualRender();
			});
			this.scrollResizeObserver.observe(this.observedScrollElement);
		}
	}

	private detachScrollObservers() {
		if (this.observedScrollElement) {
			this.observedScrollElement.removeEventListener("scroll", this.onScroll);
			this.observedScrollElement = null;
		}
		window.removeEventListener("resize", this.onWindowResize);
		if (this.scrollResizeObserver) {
			this.scrollResizeObserver.disconnect();
			this.scrollResizeObserver = null;
		}
	}

	private readonly onScroll = () => {
		this.scheduleVirtualRender();
	};

	private readonly onWindowResize = () => {
		this.scheduleVirtualRender();
	};

	private scheduleVirtualRender() {
		if (this.scheduledRenderAnimationFrameId != null) {
			return;
		}
		this.scheduledRenderAnimationFrameId = window.requestAnimationFrame(() => {
			this.scheduledRenderAnimationFrameId = null;
			const context = this.activeContext;
			if (!context) {
				return;
			}
			this.renderVirtualItems(context.getCurrentRenderRequestId());
		});
	}

	private cancelScheduledVirtualRender() {
		if (this.scheduledRenderAnimationFrameId == null) {
			return;
		}
		window.cancelAnimationFrame(this.scheduledRenderAnimationFrameId);
		this.scheduledRenderAnimationFrameId = null;
	}

	private createVirtualizerSnapshot(): Virtualizer<HTMLDivElement, HTMLCanvasElement> | null {
		const context = this.activeContext;
		if (!context) {
			return null;
		}
		const scrollElement = context.canvasContainer;
		const maxPageNumber = context.maxPageNumber;
		if (!scrollElement || maxPageNumber <= 0) {
			return null;
		}

		const rect = scrollElement.getBoundingClientRect();
		return new Virtualizer({
			count: maxPageNumber,
			getScrollElement: () => scrollElement,
			estimateSize: (index) => this.getVirtualEstimateSize(index + 1),
			gap: context.config.pageSpacing,
			overscan: this.virtualOverscan,
			getItemKey: (index) => index + 1,
			observeElementRect,
			observeElementOffset,
			scrollToFn: elementScroll,
			initialRect: {
				width: Math.max(0, rect.width),
				height: Math.max(0, rect.height)
			},
			initialOffset: scrollElement.scrollTop
		});
	}

	private getVirtualEstimateSize(pageNumber: number): number {
		const measuredHeight = this.virtualMeasuredPageHeights.get(pageNumber);
		if (measuredHeight != null) {
			return Math.max(1, measuredHeight);
		}
		return Math.max(1, this.virtualEstimatePageHeight || 1);
	}

	private renderVirtualItems(requestId: number) {
		const context = this.activeContext;
		if (!context || requestId !== context.getCurrentRenderRequestId() || !this.$virtualInner) {
			return;
		}

		const virtualizer = this.createVirtualizerSnapshot();
		if (!virtualizer) {
			return;
		}
		this.virtualizer = virtualizer;

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
		this.callbacks.updateDevRenderStats();
	}

	private getOrCreateVirtualCanvas(pageNumber: number): HTMLCanvasElement {
		let canvas = this.virtualPageCanvases.get(pageNumber);
		if (!canvas) {
			canvas = document.createElement('canvas');
			canvas.className = this.activeContext?.uuidClass ?? "";
			canvas.dataset.index = String(pageNumber - 1);
			if (this.activeContext?.config.pageBorder) {
				this.callbacks.applyPageBorderToCanvas(canvas);
			}
			this.virtualPageCanvases.set(pageNumber, canvas);
		}
		return canvas;
	}

	private renderVisibleVirtualPages(virtualItems: VirtualItem[], requestId: number) {
		const context = this.activeContext;
		if (!context || requestId !== context.getCurrentRenderRequestId()) {
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
		const context = this.activeContext;
		if (!context || requestId !== context.getCurrentRenderRequestId()) {
			return;
		}

		const page = await this.loadPageForRender(pageNumber, requestId);
		if (!page) {
			return;
		}
		if (requestId !== context.getCurrentRenderRequestId()) {
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
			if (requestId !== context.getCurrentRenderRequestId()) {
				return;
			}
			this.virtualPageRenderTasks.delete(pageNumber);
			this.virtualPageRenderScales.set(pageNumber, this.virtualizerScale);
			const measuredHeight = Math.max(1, Math.floor(viewport.height));
			const previousHeight = this.virtualMeasuredPageHeights.get(pageNumber);
			this.virtualMeasuredPageHeights.set(pageNumber, measuredHeight);
			canvas.style.visibility = "visible";
			if (previousHeight !== measuredHeight) {
				this.scheduleVirtualRender();
			}
			this.callbacks.updateDevRenderStats();
		} catch (error) {
			this.virtualPageRenderTasks.delete(pageNumber);
		}
	}

	private async loadPageForRender(pageNumber: number, requestId: number): Promise<PDFPageProxy | null> {
		const context = this.activeContext;
		if (!context || requestId !== context.getCurrentRenderRequestId()) {
			return null;
		}
		return context.pdfDocument.getPage(pageNumber);
	}
}
