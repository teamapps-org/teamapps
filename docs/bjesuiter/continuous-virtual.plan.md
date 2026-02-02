# Continuous-Virtual Plan (UiPdfViewer)

Goal: add a third view mode `CONTINUOUS_VIRTUAL` (or name TBD) that virtualizes PDF pages so only visible/nearby canvases exist in the DOM at once, improving memory usage for large documents.

## 1) Dependency + API choice
- Use TanStack Virtual core (`@tanstack/virtual-core`) since TeamApps uses vanilla TS and no framework adapters.
- Build a `Virtualizer<HTMLDivElement, HTMLCanvasElement>` instance with:
  - `count: numPages`
  - `getScrollElement: () => this.$canvasContainer` (or an inner scrolling element if you introduce one)
  - `estimateSize: (index) => estimatedPageHeightPx` (based on first page viewport * current scale)
  - `gap: this.config.pageSpacing` to model page spacing between items.
  - `overscan: this.config.virtualOverscan ?? 2` to render extra pages above/below the viewport.
  - `getItemKey: (index) => index + 1` for stable page numbers.
  - `measureElement: (el) => { ... }` for dynamic page heights when zoom or page sizes vary.

## 2) DTO + generated types
- Update `teamapps-ui-api/src/main/dto/UiPdfViewer.dto`:
  - Add enum value `CONTINUOUS_VIRTUAL` to `UiPdfViewMode`.
  - Optional: add new config fields for virtualization tuning (e.g., `virtualOverscan`, `virtualPageEstimateMode`).
- Re-run DTO generation (`mvn clean install -pl teamapps-ui-api`) to update generated TS/Java enums.
- Update usages:
  - `teamapps-client/ts/generated/UiPdfViewMode.ts`
  - `teamapps-ux` DTO classes (generated).

## 3) UI structure changes in UiPdfViewer (client)
- Keep `this.$canvasContainer` as the scroll element (it already has `overflow-y: auto`).
- Create a dedicated `this.$virtualInner` container inside `#pagesContainer` that:
  - Has `position: relative; width: 100%;`
  - Has height set to `virtualizer.getTotalSize()` on each render.
- Render only `virtualizer.getVirtualItems()`:
  - For each virtual item:
    - Position a wrapper at `translateY(virtualItem.start)` or `top: virtualItem.start` (use `start` from `VirtualItem`). citeturn0view0
    - Insert a canvas for that page index if not already present.
    - Call `virtualizer.measureElement(canvas)` once the canvas is in the DOM.
    - Use `virtualItem.size` to set wrapper height if needed (estimated size before measurement, actual after). citeturn0view0

## 4) Rendering pipeline changes
- Split current `renderPdfContinuousMode` into:
  1) `renderPdfContinuousVirtualSkeleton()`:
     - Initialize virtualizer.
     - Set up scroll/resize observers (via virtualizer core).
     - Render/update virtual items list.
  2) `renderVisiblePages()`:
     - For each virtual item, fetch the page and render into its canvas.
     - Cache per-page render tasks to avoid redundant render calls when scrolling back.
     - When virtual items unmount, optionally cancel in-flight render tasks.
- Ensure `renderRequestId` invalidates async rendering when mode changes or URL changes.

## 5) Scroll + zoom handling
- On zoom change or `TO_WIDTH/TO_HEIGHT` changes:
  - Recompute `estimateSize` using first page’s new viewport.
  - Call `virtualizer.measure()` to invalidate cached sizes.
  - Re-render visible pages with updated scale.
- On `setPageSpacing` update:
  - Update virtualizer `gap` option (or re-create virtualizer).

## 6) Page sizing strategy
- Use first page viewport as baseline estimate.
- When rendering a page, compute its actual height and either call `virtualizer.resizeItem(index, actualHeight)` or `virtualizer.measureElement(canvas)` once the canvas is sized.
- For PDFs with mixed page sizes, measurement will stabilize layout after first render.
- Keep in mind that `VirtualItem.size` starts as the estimate and updates after measurement, so it is safe to rely on it for layout once items have been measured. citeturn0view0

## 7) Memory management
- Keep a small LRU cache for rendered page bitmaps or PDF.js render tasks:
  - Size based on `overscan` or a max page count (e.g., 8–12 pages).
- When a page leaves the virtual window:
  - Optionally clear canvas or drop its element so GC can reclaim memory.

## 8) Server-side updates
- Update `PdfViewer.java` and any switch statements to accept the new enum value.
- Update `UiPdfViwerTestHarness.java` to add a button or toggle for `CONTINUOUS_VIRTUAL`.

## 9) Edge cases + UX
- Guard against `getScrollElement` returning null before render.
- Ensure `this.$canvasContainer` has a stable height (already enforced by `min-height: 0` and parent flex).
- Verify `showPage()` behavior:
  - In virtual mode, scroll to the requested page via the virtualizer instance (e.g., `scrollToIndex` if available).
- Add a warning if `CONTINUOUS_VIRTUAL` is used with extremely large PDFs and `overscan` is high.

## 10) Tests / manual verification
- Manual: open `UiPdfViwerTestHarness` and verify:
  - Smooth scrolling, no giant DOM.
  - Zoom to width/height updates layout.
  - Switching modes from SINGLE_PAGE <-> CONTINUOUS_VIRTUAL works.
- Memory: use Chrome devtools to confirm DOM node count and canvas memory stay bounded.

## 11) Rollout considerations
- Default to existing `CONTINUOUS` until virtual mode proves stable.
- Add config flags in DTO for optional opt-in:
  - `viewMode = CONTINUOUS_VIRTUAL`
  - `virtualOverscan = 2` (or similar).
