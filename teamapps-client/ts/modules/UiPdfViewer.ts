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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiDocumentViewerCommandHandler, UiDocumentViewerConfig} from "../generated/UiDocumentViewerConfig";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {css, enableScrollViaDragAndDrop, generateUUID, parseHtml} from "./Common";
import {UiBorderConfig} from "../generated/UiBorderConfig";
import {createUiBorderCssString, createUiShadowCssString} from "./util/CssFormatUtil";
import {UiShadowConfig} from "../generated/UiShadowConfig";
import {UiPdfViewerCommandHandler, UiPdfViewerConfig} from "../generated/UiPdfViewerConfig";
import { UiPdfViewMode } from "../generated/UiPdfViewMode";


/**
 * Docs for Mozillas pdf.js: https://mozilla.github.io/pdf.js/
 * NPM Package: pdfjs-dist (CAUTION: pdfjs (without -dist) is another package!)
 */
export class UiPdfViewer extends AbstractUiComponent<UiPdfViewerConfig> implements UiPdfViewerCommandHandler {

	private uuidClass: string;

	constructor(config: UiPdfViewerConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.uuidClass = `UiPdfViewer-${generateUUID()}`;

		// this.$componentWrapper = parseHtml(`<div class="UiDocumentViewer ${this.uuidClass}">
		//     <style></style>
		// 	<div class="toolbar-container"></div>
		// 	<div class="pages-container-wrapper">
		// 	    <div class="pages-container"></div>
		//     </div>
	    // </div>`);

	}

	setUrl(url: string) {
        throw new Error("Method not implemented.");
    }
    setViewMode(viewMode: UiPdfViewMode) {
        throw new Error("Method not implemented.");
    }
    showPage(page: number) {
        throw new Error("Method not implemented.");
    }
    setZoomFactor(zoomFactor: number, zoomByAvailableWidth: boolean) {
        throw new Error("Method not implemented.");
    }
    setPageBorder(pageBorder: UiBorderConfig) {
        throw new Error("Method not implemented.");
    }
    setPageShadow(pageShadow: UiShadowConfig) {
        throw new Error("Method not implemented.");
    }
    setPadding(padding: number) {
        throw new Error("Method not implemented.");
    }
    setPageSpacing(pageSpacing: number) {
        throw new Error("Method not implemented.");
    }

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPdfViewer", UiPdfViewer);
