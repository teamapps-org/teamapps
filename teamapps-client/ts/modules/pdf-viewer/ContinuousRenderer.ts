import type {PDFPageProxy, PageViewport} from "pdfjs-dist";
import {UiBorderConfig} from "../../generated/UiBorderConfig";
import type {BaseRenderContext} from "./BaseRenderContext";

export type ContinuousRenderContext = BaseRenderContext & {
	maxPageNumber: number;
	pagesContainer: HTMLDivElement;
	uuidClass: string;
	pageBorder?: UiBorderConfig;
};

export type ContinuousRendererCallbacks = {
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	applyPageBorderToCanvas: (canvas: HTMLCanvasElement) => void;
	updateDevRenderStats: () => void;
};

export class ContinuousRenderer {
	private readonly callbacks: ContinuousRendererCallbacks;

	constructor(callbacks: ContinuousRendererCallbacks) {
		this.callbacks = callbacks;
	}

	public async render(context: ContinuousRenderContext) {
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}

		const maxPageNumber = context.maxPageNumber;
		if (maxPageNumber > 5) {
			console.warn(`PdfViewer: CONTINUOUS mode with ${maxPageNumber} pages may impact performance. Consider using SINGLE_PAGE mode.`);
		}

		const firstPage = await context.pdfDocument.getPage(1);
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}
		const {scale} = this.callbacks.calculateZoomScale(firstPage);
		const $pagesContainer = context.pagesContainer;
		$pagesContainer.innerHTML = '';

		for (let pageNum = 1; pageNum <= maxPageNumber; pageNum++) {
			if (context.requestId !== context.getCurrentRenderRequestId()) {
				return;
			}
			const page = await context.pdfDocument.getPage(pageNum);
			if (context.requestId !== context.getCurrentRenderRequestId()) {
				return;
			}
			const viewport = page.getViewport({scale});
			const hiDPIScale = window.devicePixelRatio || 1;

			const canvas = document.createElement('canvas');
			canvas.className = context.uuidClass;
			const canvasContext = canvas.getContext('2d');

			canvas.width = Math.floor(viewport.width * hiDPIScale);
			canvas.height = Math.floor(viewport.height * hiDPIScale);
			canvas.style.width = Math.floor(viewport.width) + "px";
			canvas.style.height = Math.floor(viewport.height) + "px";

			if (context.pageBorder) {
				this.callbacks.applyPageBorderToCanvas(canvas);
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
			this.callbacks.updateDevRenderStats();
		}
	}
}
