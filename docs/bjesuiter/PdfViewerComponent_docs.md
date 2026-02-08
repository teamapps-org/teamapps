# PdfViewerComponent Implementation

## Parts 

DTO: `/teamapps-ui-api/src/main/dto/UiPdfViewer.dto`

Java Impl: `teamapps-ux/src/main/java/org/teamapps/ux/component/pdfviewer`

Typescript Impl: `teamapps-client/ts/modules`

Testfile (for anybody working in this repo): 
`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`

## Current Test Harness

Do not copy a static code block from this document. Use the current source of truth:
`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`.

Current setup uses `UiPdfViwerTestHarness` mounted in the root panel.

## Run the Test environment 

Preparation: 
- Regenerate DTO artifacts first:
  - `mvn clean install -pl teamapps-ui-api -am`
- Build/install the frontend module:
  - `cd teamapps-client && mvn clean install`

1. Run the Testfile (`teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`) via IntelliJ (Green Arrow in UI)
2. Start the dev environment script (frontend dev server): `docs/bjesuiter/start_pdf_dev_env.sh`
3. Goto http://localhost:9000 to see/debug the component

## Instructions for agents

- Use http://localhost:9000/ to access the dev server.
- Restore a "blank slate" for testing by reloading the page.
- The buttons in class `toolbar-button-group` are controlled by the server, written in `teamapps-server-jetty-embedded/src/test/java/org/teamapps/server/jetty/embedded/TeamAppsJettyEmbeddedServerTest.java`.
- The buttons in class `dev-toolbar` are inside the component in `teamapps-client/ts/modules/UiPdfViewer.ts`.
- Do not control the Jetty server. Leave it to the human and IntelliJ IDEA.
- If you change something in the PDF test harness, notify the human to restart Jetty.
- Do not touch untracked files. Never commit them without approval. Only commit already tracked files without approval.
- When committing, use prefix: UiPdfViewer:

### Workflow: Adding a new event

When adding a new `UiPdfViewer` event, follow this exact order:

1. Update the `.dto` file.
2. Run `mvn clean install` in the repo root.
3. Update the corresponding `.java` file.
4. Update the corresponding `.ts` file.
5. Restart the dev server via `docs/bjesuiter/start_pdf_dev_env.sh`.

### Start/Restart Jetty Test Server (Human-only CLI reference)

Jetty is intended to be started and restarted by the human via IntelliJ run config:
- **Main class:** `org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServerTest`
- **Module classpath:** `teamapps-server-jetty-embedded`
- **JDK:** 21
- **Working dir:** repo root

If you change the PDF test harness, tell the human to restart Jetty in IntelliJ.

### Start Frontend Dev Server (CLI, bgproc)

Script details:
- **Wrapper script path:** `docs/bjesuiter/start_pdf_dev_env.sh`
- **Underlying dev script path:** `teamapps-client/start-dev-server.sh`
- **Note:** Frontend dev server runs on port `9000` and connects to app server port `8082`.

**Start (background, via script):**
```bash
docs/bjesuiter/start_pdf_dev_env.sh
```

**Status:**
```bash
bgproc status -n teamapps-frontend
```

**Logs:**
```bash
bgproc logs -n teamapps-frontend
```

**Stop:**
```bash
bgproc stop -n teamapps-frontend
```

## Post-Merge Regeneration Flow (Complete)

Use this exact flow after merging `master` into a feature branch, especially when merge conflicts touched `teamapps-client/package.json`, `teamapps-client/yarn.lock`, or anything under `teamapps-ui-api/src/main/dto`.

1. Confirm merge conflicts are resolved and staged.
2. Regenerate DTO outputs first (this is critical):
```bash
mvn clean install -pl teamapps-ui-api -am
```
This builds required upstream modules (`teamapps-ui-dsl`) and regenerates:
- Java DTOs in `teamapps-ui-api/target/generated-sources/dto`
- TypeScript DTO configs in `teamapps-client/ts/generated` (gitignored, local build artifacts)

3. Rebuild the client module:
```bash
cd teamapps-client
yarn install
mvn clean install
```

4. Verify merge/build state:
- No `UU` files in `git status --short`
- No `.git/MERGE_HEAD` file (merge concluded)
- `teamapps-client` Maven build ends with `BUILD SUCCESS`

### Troubleshooting

- If you see errors like:
  - `TS2307: Cannot find module '../generated/...`
  - `Property '...' does not exist on type 'UiMap2Config'` or `UiTreeConfig`
  this means `teamapps-client/ts/generated` is stale. Re-run step 2.

- `mvn clean install` inside `teamapps-client` runs `yarn sync-pom-version` and may update `teamapps-client/package.json` version. If this change is not intended for the commit, restore it:
```bash
git restore teamapps-client/package.json
```

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
| `pageSpacing` | `int` | `5` | Spacing between pages in continuous modes |
| `backgroundColor` | `String` | `null` | CSS color for the canvas container background |
| `borderColor` | `String` | `null` | CSS color for the canvas container border |
| `pageBorder` | `UiBorder` | `null` | Border configuration for PDF pages |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `onPdfInitialized` | `PdfInitializedEvent` (contains `numberOfPages`) | Fired when PDF document is loaded and first page is rendered |
| `onZoomFactorAutoChanged` | `ZoomFactorAutoChangedEvent` (contains `zoomFactor`) | Fired when zoom factor is auto-calculated by TO_WIDTH or TO_HEIGHT modes |

### Runtime Behavior Notes

- `setUrl(String url)` is **not** a one-time initializer. It may be called multiple times during a component lifetime.
- Each `setUrl(...)` call replaces the currently displayed document and starts a new async load/render cycle.
- `onPdfInitialized` is fired after each successful `setUrl(...)` load/render cycle, with the page count of the newly loaded document.

#### Enums

**UiPdfViewMode:**
| Value | Description |
|-------|-------------|
| `SINGLE_PAGE` | Display one page at a time |
| `CONTINUOUS` | Display all pages in a scrollable view |
| `CONTINUOUS_VIRTUAL` | Virtualized continuous rendering mode for large documents |

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
| `setViewMode` | ✅ | ✅ | ✅ | Supports SINGLE_PAGE, CONTINUOUS, CONTINUOUS_VIRTUAL |
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

- **Add `pageShadow` to DTO** - property exists in Java but not exposed
