import type {PDFDocumentProxy} from "pdfjs-dist";

export type BaseRenderContext = {
	requestId: number;
	getCurrentRenderRequestId: () => number;
	pdfDocument: PDFDocumentProxy;
};
