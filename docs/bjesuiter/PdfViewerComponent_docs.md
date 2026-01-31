# PdfViewerComponent Implementation

## Parts 

DTO: `/teamapps-ui-api/src/main/dto/UiPdfViewer.dto`

Java Impl: `teamapps-ux/src/main/java/org/teamapps/ux/component/pdfviewer`

Typescript Impl: `teamapps-client/ts/modules`

Testfile (for anybody working in this repo): 
`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`

## Testfile Content for testing PdfViewer

```java
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
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.pdfviewer.PdfViewer;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.Table;
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

			String testPdfLink = sessionContext.createResourceLink(new ClassPathResource("test.pdf", "application/pdf" ));
			PdfViewer component = new PdfViewer(testPdfLink);
			rootPanel.setContent(component);
		};

		TeamAppsJettyEmbeddedServer.builder(controller)
				.setPort(8082)
				.build()
				.start();
	}


}

```

## Run the Test environment 

Preparation: 
- mvn clean install (aka TeamApps - clean + install)
  - installs/compiles everything in project 

1. Run the Testfile (`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`) via IntelliJ (Green Arrow in UI)
2. Goto teamapps-client in a shell
3. Run yarn install 
4. Run `./start-dev-server.sh 8082` (because the Jetty starts at that port)
5. Goto http://localhost:9000 to see/debug the component

## Instructions for agents

- Use http://localhost:9000/ to access the dev server.
- Restore a "blank slate" for testing by reloading the page.
- The buttons in class `toolbar-button-group` are controlled by the server, written in `teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`.
- The buttons in class `dev-toolbar` are inside the component in `teamapps-client/ts/modules/UiPdfViewer.ts`.
- Do not touch untracked files. Never commit them without approval. Only commit already tracked files without approval.
- When committing, use prefix: UiPdfViewer:

## Compile the DTO again 

- run clean + install for the Teamapps UX Subpackage

## Specification

### Public Interface (Java API)

#### Constructors
| Constructor | Description |
|-------------|-------------|
| `PdfViewer()` | Creates an empty PDF viewer |
| `PdfViewer(String url)` | Creates a PDF viewer with the given PDF URL |

#### Properties (Getters/Setters)

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `url` | `String` | `null` | URL of the PDF document to display |
| `showDevTools` | `boolean` | `false` | Shows a development toolbar with page navigation and zoom controls |
| `viewMode` | `UiPdfViewMode` | `null` | How pages are displayed (SINGLE_PAGE or CONTINUOUS) |
| `zoomMode` | `UiPdfZoomMode` | `TO_WIDTH` | How zoom is calculated (TO_HEIGHT, TO_WIDTH, MANUAL) |
| `zoomFactor` | `float` | `1.0` | Manual zoom factor (only works when zoomMode is MANUAL) |
| `padding` | `int` | `0` | Padding around the PDF canvas in pixels |
| `pageSpacing` | `int` | `5` | Spacing between pages in CONTINUOUS mode (not yet implemented) |
| `backgroundColor` | `String` | `null` | CSS color for the canvas container background |
| `borderColor` | `String` | `null` | CSS color for the canvas container border |
| `pageBorder` | `UiBorder` | `null` | Border configuration for PDF pages |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `onPdfInitialized` | `PdfInitializedEvent` (contains `numberOfPages`) | Fired when PDF document is loaded and first page is rendered |
| `onZoomFactorAutoChanged` | `ZoomFactorAutoChangedEvent` (contains `zoomFactor`) | Fired when zoom factor is auto-calculated by TO_WIDTH or TO_HEIGHT modes |

#### Enums

**UiPdfViewMode:**
| Value | Description |
|-------|-------------|
| `SINGLE_PAGE` | Display one page at a time |
| `CONTINUOUS` | Display all pages in a scrollable view (⚠️ NOT YET IMPLEMENTED) |

**UiPdfZoomMode:**
| Value | Description |
|-------|-------------|
| `TO_HEIGHT` | Auto-scale PDF page to fit container height |
| `TO_WIDTH` | Auto-scale PDF page to fit container width |
| `MANUAL` | Use the `zoomFactor` property for scaling |

### Implementation Status

#### ✅ Fully Implemented Features

| Feature | DTO | Java | TypeScript | Notes |
|---------|-----|------|------------|-------|
| `setUrl` | ✅ | ✅ | ✅ | Load PDF from URL |
| `setShowDevTools` | ✅ | ✅ | ✅ | Toggle dev toolbar |
| `setViewMode` | ✅ | ✅ | ✅ | Only SINGLE_PAGE works |
| `setZoomFactor` | ✅ | ✅ | ✅ | Manual zoom control |
| `setZoomMode` | ✅ | ✅ | ✅ | Auto-zoom modes |
| `setPadding` | ✅ | ✅ | ✅ | Canvas container padding |
| `setPageSpacing` | ✅ | ✅ | ✅ | For CONTINUOUS mode |
| `setBackgroundColor` | ✅ | ✅ | ✅ | Container background |
| `setBorderColor` | ✅ | ✅ | ✅ | Container border |
| `setPageBorder` | ✅ | ✅ | ✅ | Page border config |
| `pdfInitialized` event | ✅ | ✅ | ✅ | PDF load notification |
| `zoomFactorAutoChanged` event | ✅ | ✅ | ✅ | Auto-zoom notification |

#### ⚠️ Partially Implemented / Missing Features

| Feature | DTO | Java | TypeScript | Issue |
|---------|-----|------|------------|-------|
| `showPage(int page)` | ✅ | ❌ | ✅ | **Missing Java method** - command defined in DTO but no Java setter to call it |
| `CONTINUOUS` viewMode | ✅ | ✅ | ❌ | TypeScript throws error: "not supported yet" |
| `pageShadow` | ❌ | ✅ | ❌ | Property exists in Java but not in DTO or TypeScript |

### Usage Example

```java
// Create PDF viewer
String testPdfLink = sessionContext.createResourceLink(
    new ClassPathResource("test.pdf", "application/pdf")
);
PdfViewer pdfViewer = new PdfViewer();

// Configure appearance
pdfViewer.setPadding(10);
pdfViewer.setShowDevTools(true);
pdfViewer.setZoomMode(UiPdfZoomMode.TO_HEIGHT);
pdfViewer.setBackgroundColor("oklch(0.74 0.1 218.65)");
pdfViewer.setBorderColor("#ff0000");

// Load PDF (can be done later, e.g., on button click)
pdfViewer.setUrl(testPdfLink);

// Listen for events
pdfViewer.onPdfInitialized.addListener((initEvent) -> {
    System.out.println("PDF loaded, pages: " + initEvent.getNumberOfPages());
});

pdfViewer.onZoomFactorAutoChanged.addListener((zoomEvent) -> {
    System.out.println("Zoom factor changed: " + zoomEvent.getZoomFactor());
});

// Add to panel
panel.setContent(pdfViewer);
```

## Todos

- In PdfViewer.java: update all setters to update ts client
- **Add `showPage(int page)` method to PdfViewer.java** - command exists in DTO and TypeScript but missing in Java
- **Add `pageShadow` to DTO** - property exists in Java but not exposed
- **Implement CONTINUOUS viewMode in TypeScript** - currently throws error

## Implementation Plan: Continuous Rendering Mode

### Step 1: Refactor - Extract Zoom Calculation

**New method:** `calculateZoomScale(referencePage: PDFPageProxy)`

Extract zoom logic (lines 285-332) into reusable function:
- Input: PDF page (always first page for now)
- Returns: `{ scale: number, viewport: PageViewport }`
- Handles TO_WIDTH, TO_HEIGHT, MANUAL modes
- Fires `onZoomFactorAutoChanged` when auto-calculating
- Sets `zoomMode` to MANUAL after auto-calc

### Step 2: Implement `renderPdfContinuousMode()`

- **Console warning** if `maxPageNumber > 5`
- Get first page, call `calculateZoomScale(firstPage)`
- Clear `#pagesContainer` (remove old canvases)
- Loop through all pages (1 to maxPageNumber):
  - Create canvas element per page
  - Apply `pageBorder` styling to each canvas
  - Render page with calculated scale
- Apply `pageSpacing` as CSS gap on `#pagesContainer`

### Step 3: Update `renderPdfDocument()`

- Remove `throw Error` for CONTINUOUS
- Route: `CONTINUOUS` → `renderPdfContinuousMode()`
- Route: `SINGLE_PAGE` → `renderPdfSinglePageMode()`

### Step 4: CSS Changes

Update `#pagesContainer` styles:
```css
display: flex;
flex-flow: column nowrap;
align-items: center;
gap: ${pageSpacing}px;
```

### Step 5: Update `renderPdfSinglePageMode()`

- Call `calculateZoomScale()` instead of inline logic
- Keep single canvas rendering as-is

### Design Decisions

- **Zoom reference:** Always uses first page dimensions
- **TO_HEIGHT in continuous mode:** Fits first page to container height, other pages scroll
- **No virtual scrolling:** All pages rendered immediately (warn if >5 pages)
