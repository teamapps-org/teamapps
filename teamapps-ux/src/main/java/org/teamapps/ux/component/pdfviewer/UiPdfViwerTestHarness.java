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
package org.teamapps.ux.component.pdfviewer;

import org.teamapps.dto.UiPdfViewMode;
import org.teamapps.dto.UiPdfZoomMode;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.resource.ClassPathResource;

public class UiPdfViwerTestHarness extends Panel {

	public UiPdfViwerTestHarness() {
		super(null, "PDF Viewer");

		PdfViewer pdfViewer = new PdfViewer();
		pdfViewer.setPadding(10);
		pdfViewer.setPageSpacing(8);
		pdfViewer.setShowDevTools(false);
		pdfViewer.setZoomMode(UiPdfZoomMode.TO_HEIGHT);
		pdfViewer.setBackgroundColor("oklch(0.74 0.1 218.65)");
		pdfViewer.setBorderColor("#ff0000");

		Toolbar toolbar = new Toolbar();
		ToolbarButtonGroup buttonGroup = toolbar.addButtonGroup(new ToolbarButtonGroup());

		ToolbarButton showPdfButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.PICTURE_AS_PDF, "Show PDF", "Show the PDF document"));
		ToolbarButton continuousModeButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.VIEW_STREAM, "Continuous Mode", "Switch to continuous scrolling mode"));
		ToolbarButton singlePageModeButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.VIEW_DAY, "Single Page Mode", "Switch to single page mode"));
		ToolbarButton decreasePageButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.NAVIGATE_BEFORE, "Decrease Page", "Show previous page"));
		ToolbarButton increasePageButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.NAVIGATE_NEXT, "Increase Page", "Show next page"));
		ToolbarButton zoomInButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.ZOOM_IN, "Zoom in", "Increase zoom factor"));
		ToolbarButton zoomOutButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.ZOOM_OUT, "Zoom out", "Decrease zoom factor"));
		ToolbarButton zoomToWidthButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.BORDER_HORIZONTAL, "Zoom to width", "Auto-zoom to container width"));
		ToolbarButton zoomToHeightButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.BORDER_VERTICAL, "Zoom to height", "Auto-zoom to container height"));

		String testPdfLink = getSessionContext().createResourceLink(new ClassPathResource("test.pdf", "application/pdf"));

		showPdfButton.onClick.addListener(() -> {
			pdfViewer.setUrl(testPdfLink);
		});

		continuousModeButton.onClick.addListener(() -> {
			pdfViewer.setViewMode(UiPdfViewMode.CONTINUOUS);
			System.out.println("Switched to CONTINUOUS mode");
		});

		singlePageModeButton.onClick.addListener(() -> {
			pdfViewer.setViewMode(UiPdfViewMode.SINGLE_PAGE);
			System.out.println("Switched to SINGLE_PAGE mode");
		});

		decreasePageButton.onClick.addListener(() -> {
			pdfViewer.previousPage();
		});

		increasePageButton.onClick.addListener(() -> {
			pdfViewer.nextPage();
		});

		zoomInButton.onClick.addListener(() -> {
			pdfViewer.zoomIn();
		});

		zoomOutButton.onClick.addListener(() -> {
			pdfViewer.zoomOut();
		});

		zoomToWidthButton.onClick.addListener(() -> {
			pdfViewer.zoomToWidth();
		});

		zoomToHeightButton.onClick.addListener(() -> {
			pdfViewer.zoomToHeight();
		});

		pdfViewer.onPdfInitialized.addListener((initEvent) -> {
			System.out.println("PDF viewer rendered, page number: " + initEvent.getNumberOfPages());
		});

		pdfViewer.onZoomFactorAutoChanged.addListener((zoomFactorChangeEvent) -> {
			System.out.println("Zoom factor changed: " + zoomFactorChangeEvent.getZoomFactor());
		});

		setToolbar(toolbar);
		setContent(pdfViewer);
	}
}
