import type {PDFPageProxy, PageViewport} from "pdfjs-dist";
import type {BaseRenderContext} from "./BaseRenderContext";

export type SinglePageRenderContext = BaseRenderContext & {
	currentPageNumber: number;
	pagesContainer: HTMLDivElement;
	canvas: HTMLCanvasElement;
};

export type SinglePageRendererCallbacks = {
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	updateDevRenderStats: () => void;
};

export class SinglePageRenderer {
	private readonly callbacks: SinglePageRendererCallbacks;

	constructor(callbacks: SinglePageRendererCallbacks) {
		this.callbacks = callbacks;
	}

	public async render(context: SinglePageRenderContext) {
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}

		const $pagesContainer = context.pagesContainer;
		const canvas = context.canvas;
		$pagesContainer.innerHTML = '';
		$pagesContainer.appendChild(canvas);

		const page = await context.pdfDocument.getPage(context.currentPageNumber);
		if (context.requestId !== context.getCurrentRenderRequestId()) {
			return;
		}
		const {viewport: pdfViewport} = this.callbacks.calculateZoomScale(page);
		const hiDPIScale = window.devicePixelRatio || 1;
		const canvasContext = canvas.getContext('2d');

		canvas.width = Math.floor(pdfViewport.width * hiDPIScale);
		canvas.height = Math.floor(pdfViewport.height * hiDPIScale);
		canvas.style.width = Math.floor(pdfViewport.width) + "px";
		canvas.style.height = Math.floor(pdfViewport.height) + "px";
		canvas.style.overflowX = "auto";

		const transform = hiDPIScale !== 1 ?
			[hiDPIScale, 0, 0, hiDPIScale, 0, 0] :
			null;

		const renderContext = {
			canvasContext,
			transform,
			viewport: pdfViewport
		};

		page.render(renderContext);
		this.callbacks.updateDevRenderStats();
	}
}
