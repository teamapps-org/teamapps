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
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.ux.resource.InputStreamResource;
import org.teamapps.ux.resource.Resource;

import java.net.URL;

public class UiPdfViwerTestHarness extends Panel {

	public UiPdfViwerTestHarness() {
		super(null, "PDF Viewer");

		PdfViewer pdfViewer = new PdfViewer();
		pdfViewer.setPadding(10);
		pdfViewer.setPageSpacing(8);
		pdfViewer.setShowDevTools(false);
		pdfViewer.setViewMode(UiPdfViewMode.SINGLE_PAGE);
		pdfViewer.setZoomMode(UiPdfZoomMode.TO_HEIGHT);
		pdfViewer.setBackgroundColor("oklch(0.74 0.1 218.65)");
		pdfViewer.setBorderColor("#ff0000");

		Toolbar toolbar = new Toolbar();
		ToolbarButtonGroup buttonGroup = toolbar.addButtonGroup(new ToolbarButtonGroup());

		ToolbarButton showPdfButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.PICTURE_AS_PDF, "Show PDF", "Show the PDF document"));
		ToolbarButton continuousModeButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.VIEW_STREAM, "Continuous Mode", "Switch to continuous scrolling mode"));
		ToolbarButton continuousVirtualModeButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.VIEW_STREAM, "Continuous Virtual", "Switch to continuous virtual mode"));
		ToolbarButton singlePageModeButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.VIEW_DAY, "Single Page Mode", "Switch to single page mode"));
		ToolbarButton decreasePageButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.NAVIGATE_BEFORE, "Previous Page", "Show previous page"));
		ToolbarButton increasePageButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.NAVIGATE_NEXT, "Next Page", "Show next page"));
		ToolbarButton[] pageIndicatorButton = new ToolbarButton[1];
		ToolbarButton zoomInButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.ZOOM_IN, "Zoom in", "Increase zoom factor"));
		ToolbarButton zoomOutButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.ZOOM_OUT, "Zoom out", "Decrease zoom factor"));
		ToolbarButton zoomToWidthButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.BORDER_HORIZONTAL, "Zoom to width", "Auto-zoom to container width"));
		ToolbarButton zoomToHeightButton = buttonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.BORDER_VERTICAL, "Zoom to height", "Auto-zoom to container height"));

		int[] currentPage = {1};
		int[] maxPageNumber = {0};
		boolean[] singlePageControlsVisible = {true};
		Runnable updatePageIndicator = () -> {
			String caption = maxPageNumber[0] > 0 ? currentPage[0] + " / " + maxPageNumber[0] : String.valueOf(currentPage[0]);
			ToolbarButton indicator = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_SMALL, new BaseTemplateRecord<>((org.teamapps.icons.Icon) null, (String) null, caption, "Current page", null));
			indicator.setVisible(singlePageControlsVisible[0]);
			if (pageIndicatorButton[0] != null) {
				buttonGroup.removeButton(pageIndicatorButton[0]);
			}
			pageIndicatorButton[0] = indicator;
			buttonGroup.addButton(indicator, increasePageButton, true);
		};
		Runnable updateSinglePageControlsVisibility = () -> {
			decreasePageButton.setVisible(singlePageControlsVisible[0]);
			increasePageButton.setVisible(singlePageControlsVisible[0]);
			if (pageIndicatorButton[0] != null) {
				pageIndicatorButton[0].setVisible(singlePageControlsVisible[0]);
			}
		};

		String defaultPdfUrl = "https://www.princexml.com/samples/wind-in-willows/b-format.pdf";
		TextField pdfUrlField = new TextField();
		pdfUrlField.setShowClearButton(true);
		pdfUrlField.setEmptyText("PDF URL");
		pdfUrlField.setValue(defaultPdfUrl);

		String testPdfLink = getSessionContext().createResourceLink(new ClassPathResource("test.pdf", "application/pdf"));

		showPdfButton.onClick.addListener(() -> {
			String url = pdfUrlField.getValue();
			pdfViewer.setUrl(resolvePdfUrl(url, testPdfLink));
		});
		pdfUrlField.onSpecialKeyPressed.addListener((key) -> {
			if (key == SpecialKey.ENTER) {
				String url = pdfUrlField.getValue();
				pdfViewer.setUrl(resolvePdfUrl(url, testPdfLink));
			}
		});

		continuousModeButton.onClick.addListener(() -> {
			pdfViewer.setViewMode(UiPdfViewMode.CONTINUOUS);
			singlePageControlsVisible[0] = false;
			updateSinglePageControlsVisibility.run();
			System.out.println("Switched to CONTINUOUS mode");
		});

		continuousVirtualModeButton.onClick.addListener(() -> {
			pdfViewer.setViewMode(UiPdfViewMode.CONTINUOUS_VIRTUAL);
			singlePageControlsVisible[0] = false;
			updateSinglePageControlsVisibility.run();
			System.out.println("Switched to CONTINUOUS_VIRTUAL mode");
		});

		singlePageModeButton.onClick.addListener(() -> {
			pdfViewer.setViewMode(UiPdfViewMode.SINGLE_PAGE);
			singlePageControlsVisible[0] = true;
			updateSinglePageControlsVisibility.run();
			System.out.println("Switched to SINGLE_PAGE mode");
		});

		decreasePageButton.onClick.addListener(() -> {
			pdfViewer.previousPage();
			if (currentPage[0] > 1) {
				currentPage[0]--;
				updatePageIndicator.run();
			}
		});

		increasePageButton.onClick.addListener(() -> {
			pdfViewer.nextPage();
			if (maxPageNumber[0] == 0 || currentPage[0] < maxPageNumber[0]) {
				currentPage[0]++;
				updatePageIndicator.run();
			}
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
			maxPageNumber[0] = initEvent.getNumberOfPages();
			currentPage[0] = 1;
			updatePageIndicator.run();
		});

		pdfViewer.onZoomFactorAutoChanged.addListener((zoomFactorChangeEvent) -> {
			System.out.println("Zoom factor changed: " + zoomFactorChangeEvent.getZoomFactor());
		});

		pdfViewer.setUrl(defaultPdfUrl);
		updatePageIndicator.run();

		setToolbar(toolbar);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponentAutoSize(pdfUrlField);
		layout.addComponentFillRemaining(pdfViewer);
		setContent(layout);
	}

	private String resolvePdfUrl(String url, String fallbackUrl) {
		if (url == null || url.isBlank()) {
			return fallbackUrl;
		}
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return getSessionContext().createResourceLink(createPdfProxyResource(url));
		}
		return url;
	}

	private Resource createPdfProxyResource(String url) {
		return new InputStreamResource(() -> {
			try {
				return new URL(url).openStream();
			} catch (Exception e) {
				throw new RuntimeException("Failed to open PDF URL: " + url, e);
			}
		}, "proxied.pdf").withMimeType("application/pdf");
	}
}
