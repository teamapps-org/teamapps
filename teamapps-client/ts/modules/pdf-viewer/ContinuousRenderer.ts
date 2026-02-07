import type {PDFDocumentProxy, PDFPageProxy, PageViewport} from "pdfjs-dist";
import {UiPdfViewerConfig} from "../../generated/UiPdfViewerConfig";

export interface IContinuousRendererHost {
	getRenderRequestId: () => number;
	getPdfDocument: () => PDFDocumentProxy;
	getMaxPageNumber: () => number;
	getPagesContainer: () => HTMLDivElement;
	getUuidClass: () => string;
	getConfig: () => UiPdfViewerConfig;
	calculateZoomScale: (page: PDFPageProxy) => { scale: number, viewport: PageViewport };
	applyPageBorderToCanvas: (canvas: HTMLCanvasElement) => void;
	updateDevRenderStats: () => void;
}

export class ContinuousRenderer {
	private readonly host: IContinuousRendererHost;

	constructor(host: IContinuousRendererHost) {
		this.host = host;
	}

	public async render(requestId: number) {
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}

		const maxPageNumber = this.host.getMaxPageNumber();
		if (maxPageNumber > 5) {
			console.warn(`PdfViewer: CONTINUOUS mode with ${maxPageNumber} pages may impact performance. Consider using SINGLE_PAGE mode.`);
		}

		const firstPage = await this.host.getPdfDocument().getPage(1);
		if (requestId !== this.host.getRenderRequestId()) {
			return;
		}
		const {scale} = this.host.calculateZoomScale(firstPage);
		const $pagesContainer = this.host.getPagesContainer();
		$pagesContainer.innerHTML = '';

		for (let pageNum = 1; pageNum <= maxPageNumber; pageNum++) {
			if (requestId !== this.host.getRenderRequestId()) {
				return;
			}
			const page = await this.host.getPdfDocument().getPage(pageNum);
			if (requestId !== this.host.getRenderRequestId()) {
				return;
			}
			const viewport = page.getViewport({scale});
			const hiDPIScale = window.devicePixelRatio || 1;

			const canvas = document.createElement('canvas');
			canvas.className = this.host.getUuidClass();
			const canvasContext = canvas.getContext('2d');

			canvas.width = Math.floor(viewport.width * hiDPIScale);
			canvas.height = Math.floor(viewport.height * hiDPIScale);
			canvas.style.width = Math.floor(viewport.width) + "px";
			canvas.style.height = Math.floor(viewport.height) + "px";

			if (this.host.getConfig().pageBorder) {
				this.host.applyPageBorderToCanvas(canvas);
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
			this.host.updateDevRenderStats();
		}
	}
}
