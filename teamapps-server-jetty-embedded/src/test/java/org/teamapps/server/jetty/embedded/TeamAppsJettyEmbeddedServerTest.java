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
package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.UiPdfViewMode;
import org.teamapps.dto.UiPdfZoomMode;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.pdfviewer.PdfViewer;
import org.teamapps.ux.component.playground.Playground;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.webcontroller.WebController;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {
		WebController controller = sessionContext -> {
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootPanel(null, rootPanel);

			Panel panel = new Panel(null, "PDF Viewer");
			
			// Create toolbar with buttons
			Toolbar toolbar = new Toolbar();
			ToolbarButtonGroup buttonGroup = toolbar.addButtonGroup(new ToolbarButtonGroup());
			
			ToolbarButton showPdfButton = buttonGroup.addButton(ToolbarButton.create(null, "Show PDF", "Show the PDF document"));
			ToolbarButton continuousModeButton = buttonGroup.addButton(ToolbarButton.create(null, "Continuous Mode", "Switch to continuous scrolling mode"));
			ToolbarButton singlePageModeButton = buttonGroup.addButton(ToolbarButton.create(null, "Single Page Mode", "Switch to single page mode"));

			String testPdfLink = sessionContext.createResourceLink(new ClassPathResource("test.pdf", "application/pdf" ));
			PdfViewer pdfViewer = new PdfViewer();
			pdfViewer.setPadding(10);
			pdfViewer.setShowDevTools(true);
			pdfViewer.setZoomMode(UiPdfZoomMode.TO_HEIGHT);
			pdfViewer.setBackgroundColor("oklch(0.74 0.1 218.65)");
			pdfViewer.setBorderColor("#ff0000");

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

			pdfViewer.onPdfInitialized.addListener((initEvent) -> {
				System.out.println("PDF viewer rendered, page number: " + initEvent.getNumberOfPages());
			});

			pdfViewer.onZoomFactorAutoChanged.addListener((zoomFactorChangeEvent) -> {
				System.out.println("Zoom factor changed: " + zoomFactorChangeEvent.getZoomFactor());
			});

			panel.setToolbar(toolbar);
			panel.setContent(pdfViewer);
			rootPanel.setContent(panel);
		};

		TeamAppsJettyEmbeddedServer.builder(controller)
				.setPort(8082)
				.build()
				.start();
	}


}
