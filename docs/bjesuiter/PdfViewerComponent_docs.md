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

### Start/Restart Jetty Test Server (CLI, background)

This mirrors the IntelliJ run config:
- **Main class:** `org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServerTest`
- **Module classpath:** `teamapps-server-jetty-embedded`
- **JDK:** 21
- **Working dir:** repo root

**Start (background, exact IntelliJ command):**
```bash
nohup /Users/bjesuiter/Library/Java/JavaVirtualMachines/temurin-21.0.9/Contents/Home/bin/java \
  -javaagent:/Applications/IntelliJ\ IDEA.app/Contents/lib/idea_rt.jar=57827 \
  -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 \
  -classpath /Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-server-jetty-embedded/target/test-classes:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-server-jetty-embedded/target/classes:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/jetty-ee10-webapp/12.0.12/jetty-ee10-webapp-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-ee/12.0.12/jetty-ee-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-server/12.0.12/jetty-server-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-http/12.0.12/jetty-http-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-io/12.0.12/jetty-io-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-session/12.0.12/jetty-session-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-xml/12.0.12/jetty-xml-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-util/12.0.12/jetty-util-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/jetty-ee10-servlet/12.0.12/jetty-ee10-servlet-12.0.12.jar:/Users/bjesuiter/.m2/repository/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-security/12.0.12/jetty-security-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/websocket/jetty-ee10-websocket-jakarta-server/12.0.12/jetty-ee10-websocket-jakarta-server-12.0.12.jar:/Users/bjesuiter/.m2/repository/jakarta/websocket/jakarta.websocket-api/2.1.1/jakarta.websocket-api-2.1.1.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/jetty-ee10-annotations/12.0.12/jetty-ee10-annotations-12.0.12.jar:/Users/bjesuiter/.m2/repository/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/jetty-ee10-plus/12.0.12/jetty-ee10-plus-12.0.12.jar:/Users/bjesuiter/.m2/repository/jakarta/enterprise/jakarta.enterprise.cdi-api/4.0.1/jakarta.enterprise.cdi-api-4.0.1.jar:/Users/bjesuiter/.m2/repository/jakarta/el/jakarta.el-api/5.0.0/jakarta.el-api-5.0.0.jar:/Users/bjesuiter/.m2/repository/jakarta/enterprise/jakarta.enterprise.lang-model/4.0.1/jakarta.enterprise.lang-model-4.0.1.jar:/Users/bjesuiter/.m2/repository/jakarta/interceptor/jakarta.interceptor-api/2.1.0/jakarta.interceptor-api-2.1.0.jar:/Users/bjesuiter/.m2/repository/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-jndi/12.0.12/jetty-jndi-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-plus/12.0.12/jetty-plus-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/ow2/asm/asm/9.7/asm-9.7.jar:/Users/bjesuiter/.m2/repository/org/ow2/asm/asm-commons/9.7/asm-commons-9.7.jar:/Users/bjesuiter/.m2/repository/org/ow2/asm/asm-tree/9.7/asm-tree-9.7.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/websocket/jetty-ee10-websocket-jakarta-client/12.0.12/jetty-ee10-websocket-jakarta-client-12.0.12.jar:/Users/bjesuiter/.m2/repository/jakarta/websocket/jakarta.websocket-client-api/2.1.1/jakarta.websocket-client-api-2.1.1.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-client/12.0.12/jetty-client-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/jetty-alpn-client/12.0.12/jetty-alpn-client-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/websocket/jetty-ee10-websocket-jakarta-common/12.0.12/jetty-ee10-websocket-jakarta-common-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/websocket/jetty-websocket-core-client/12.0.12/jetty-websocket-core-client-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/websocket/jetty-websocket-core-common/12.0.12/jetty-websocket-core-common-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/ee10/websocket/jetty-ee10-websocket-servlet/12.0.12/jetty-ee10-websocket-servlet-12.0.12.jar:/Users/bjesuiter/.m2/repository/org/eclipse/jetty/websocket/jetty-websocket-core-server/12.0.12/jetty-websocket-core-server-12.0.12.jar:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-ux/target/classes:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-common/target/classes:/Users/bjesuiter/.m2/repository/org/teamapps/teamapps-commons/0.1.2/teamapps-commons-0.1.2.jar:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-icon/target/classes:/Users/bjesuiter/.m2/repository/net/coobird/thumbnailator/0.4.20/thumbnailator-0.4.20.jar:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-material-icon-provider/target/classes:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-ui-api/target/classes:/Users/bjesuiter/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.18.2/jackson-core-2.18.2.jar:/Users/bjesuiter/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.18.2/jackson-databind-2.18.2.jar:/Users/bjesuiter/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.18.2/jackson-annotations-2.18.2.jar:/Users/bjesuiter/.m2/repository/com/ibm/icu/icu4j/76.1/icu4j-76.1.jar:/Users/bjesuiter/.m2/repository/org/apache/commons/commons-lang3/3.17.0/commons-lang3-3.17.0.jar:/Users/bjesuiter/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar:/Users/bjesuiter/.m2/repository/com/google/guava/guava/33.4.0-jre/guava-33.4.0-jre.jar:/Users/bjesuiter/.m2/repository/com/google/guava/failureaccess/1.0.2/failureaccess-1.0.2.jar:/Users/bjesuiter/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/Users/bjesuiter/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/Users/bjesuiter/.m2/repository/org/checkerframework/checker-qual/3.43.0/checker-qual-3.43.0.jar:/Users/bjesuiter/.m2/repository/com/google/errorprone/error_prone_annotations/2.36.0/error_prone_annotations-2.36.0.jar:/Users/bjesuiter/.m2/repository/com/google/j2objc/j2objc-annotations/3.0.0/j2objc-annotations-3.0.0.jar:/Users/bjesuiter/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.18.2/jackson-databind-2.18.2.jar:/Users/bjesuiter/.m2/repository/it/unimi/dsi/fastutil/8.5.8/fastutil-8.5.8.jar:/Users/bjesuiter/.m2/repository/com/auth0/java-jwt/3.19.1/java-jwt-3.19.1.jar:/Users/bjesuiter/.m2/repository/org/glassfish/jersey/core/jersey-client/3.1.10/jersey-client-3.1.10.jar:/Users/bjesuiter/.m2/repository/jakarta/ws/rs/jakarta.ws.rs-api/3.1.0/jakarta.ws.rs-api-3.1.0.jar:/Users/bjesuiter/.m2/repository/org/glassfish/jersey/core/jersey-common/3.1.10/jersey-common-3.1.10.jar:/Users/bjesuiter/.m2/repository/org/glassfish/hk2/osgi-resource-locator/1.0.3/osgi-resource-locator-1.0.3.jar:/Users/bjesuiter/.m2/repository/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar:/Users/bjesuiter/.m2/repository/org/glassfish/jersey/media/jersey-media-json-jackson/3.1.10/jersey-media-json-jackson-3.1.10.jar:/Users/bjesuiter/.m2/repository/org/glassfish/jersey/ext/jersey-entity-filtering/3.1.10/jersey-entity-filtering-3.1.10.jar:/Users/bjesuiter/.m2/repository/com/fasterxml/jackson/module/jackson-module-jakarta-xmlbind-annotations/2.18.0/jackson-module-jakarta-xmlbind-annotations-2.18.0.jar:/Users/bjesuiter/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/4.0.2/jakarta.xml.bind-api-4.0.2.jar:/Users/bjesuiter/.m2/repository/jakarta/activation/jakarta.activation-api/2.1.3/jakarta.activation-api-2.1.3.jar:/Users/bjesuiter/.m2/repository/org/glassfish/jersey/inject/jersey-hk2/3.1.10/jersey-hk2-3.1.10.jar:/Users/bjesuiter/.m2/repository/org/glassfish/hk2/hk2-locator/3.0.6/hk2-locator-3.0.6.jar:/Users/bjesuiter/.m2/repository/org/glassfish/hk2/external/aopalliance-repackaged/3.0.6/aopalliance-repackaged-3.0.6.jar:/Users/bjesuiter/.m2/repository/org/glassfish/hk2/hk2-api/3.0.6/hk2-api-3.0.6.jar:/Users/bjesuiter/.m2/repository/org/glassfish/hk2/hk2-utils/3.0.6/hk2-utils-3.0.6.jar:/Users/bjesuiter/.m2/repository/org/javassist/javassist/3.30.2-GA/javassist-3.30.2-GA.jar:/Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-client/target/classes:/Users/bjesuiter/.m2/repository/commons-io/commons-io/2.18.0/commons-io-2.18.0.jar:/Users/bjesuiter/.m2/repository/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar:/Users/bjesuiter/.m2/repository/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar:/Users/bjesuiter/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar:/Users/bjesuiter/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/Users/bjesuiter/.m2/repository/org/assertj/assertj-core/3.26.3/assertj-core-3.26.3.jar:/Users/bjesuiter/.m2/repository/net/bytebuddy/byte-buddy/1.14.18/byte-buddy-1.14.18.jar:/Users/bjesuiter/.m2/repository/org/awaitility/awaitility/4.2.2/awaitility-4.2.2.jar:/Users/bjesuiter/.m2/repository/org/hamcrest/hamcrest/2.1/hamcrest-2.1.jar:/Users/bjesuiter/.m2/repository/org/mockito/mockito-core/5.13.0/mockito-core-5.13.0.jar:/Users/bjesuiter/.m2/repository/net/bytebuddy/byte-buddy-agent/1.15.0/byte-buddy-agent-1.15.0.jar:/Users/bjesuiter/.m2/repository/org/objenesis/objenesis/3.3/objenesis-3.3.jar \
  org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServerTest \
  > /tmp/teamapps-jetty.log 2>&1 &
echo $! > /tmp/teamapps-jetty.pid
```

**Note:** If startup fails with `NoClassDefFoundError: com/fasterxml/jackson/datatype/jsr310/JavaTimeModule`,
the classpath entry should be the actual jar:
`.../jackson-datatype-jsr310/2.18.2/jackson-datatype-jsr310-2.18.2.jar`
(the pasted IntelliJ command had `jackson-databind-2.18.2.jar` in that slot).

**Restart:**
```bash
kill -TERM "$(cat /tmp/teamapps-jetty.pid)" || true
rm -f /tmp/teamapps-jetty.pid
# then run the Start block again
```

**Tail logs:**
```bash
tail -f /tmp/teamapps-jetty.log
```

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
