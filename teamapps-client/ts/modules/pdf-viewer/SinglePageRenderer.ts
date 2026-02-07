import type {PDFDocumentProxy, PDFPageProxy, PageViewport} from "pdfjs-dist";

export interface ISinglePageRendererHost {
	getRenderRequestId: () => number;
	getPdfDocument: () => PDFDocumentProxy;
	getCurrentPageNumber: () => number;
	getPagesContainer: () => HTMLDivElement;
	getCanvas: () => HTMLCanvasElement;
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	updateDevRenderStats: () => void;
}

export class SinglePageRenderer {
	private readonly host: ISinglePageRendererHost;

	constructor(host: ISinglePageRendererHost) {
		this.host = host;
	}

	public async render(requestId: number) {
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}

		const $pagesContainer = this.host.getPagesContainer();
		const canvas = this.host.getCanvas();
		$pagesContainer.innerHTML = '';
		$pagesContainer.appendChild(canvas);

		const page = await this.host.getPdfDocument().getPage(this.host.getCurrentPageNumber());
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}
		const {viewport: pdfViewport} = this.host.calculateZoomScale(page);
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
		this.host.updateDevRenderStats();
	}
}
