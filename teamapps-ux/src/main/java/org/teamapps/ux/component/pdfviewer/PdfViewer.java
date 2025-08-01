package org.teamapps.ux.component.pdfviewer;

import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

public class PdfViewer extends AbstractComponent {
    public final Event<UiPdfViewer.PdfInitializedEvent> onPdfInitialized = new Event<>();
    public final Event<UiPdfViewer.ZoomFactorAutoChangedEvent> onZoomFactorAutoChanged = new Event<>();

    protected String url;
    protected UiPdfViewMode viewMode;
    protected UiPdfZoomMode zoomMode = UiPdfZoomMode.TO_WIDTH;
    protected boolean showDevTools = false;
    protected int padding = 0;
    protected int pageSpacing = 5;
    protected float zoomFactor = 1f;
    protected UiBorder pageBorder;
    protected UiShadow pageShadow;
    protected String backgroundColor;

    public PdfViewer() {
        this(null);
    }

    public PdfViewer(String url) {
        this.url = url;
    }

    @Override
    public UiComponent createUiComponent() {
        UiPdfViewer uiPdfViewer = new UiPdfViewer();
        mapAbstractUiComponentProperties(uiPdfViewer);

        uiPdfViewer.setUrl(url);
        uiPdfViewer.setShowDevTools(showDevTools);
        uiPdfViewer.setViewMode(viewMode);
        uiPdfViewer.setPadding(padding);
        uiPdfViewer.setPageSpacing(pageSpacing);
        uiPdfViewer.setZoomFactor(zoomFactor);
        uiPdfViewer.setPageBorder(pageBorder);
        uiPdfViewer.setPageShadow(pageShadow);
        uiPdfViewer.setZoomMode(zoomMode);
        uiPdfViewer.setBackgroundColor(backgroundColor);

        return uiPdfViewer;
    }

    @Override
    public void handleUiEvent(UiEvent event) {
        // TODO: This ui event does not arrive on the server yet. => Check with yann!
        switch (event.getUiEventType()) {
            case UI_PDF_VIEWER_PDF_INITIALIZED: {
                UiPdfViewer.PdfInitializedEvent initEvent = (UiPdfViewer.PdfInitializedEvent) event;
                this.onPdfInitialized.fire(initEvent);
                break;
            }
            case UI_PDF_VIEWER_ZOOM_FACTOR_AUTO_CHANGED: {
                UiPdfViewer.ZoomFactorAutoChangedEvent zoomChangedEvent = (UiPdfViewer.ZoomFactorAutoChangedEvent) event;
                this.onZoomFactorAutoChanged.fire(zoomChangedEvent);
                break;
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        queueCommandIfRendered(() -> new UiPdfViewer.SetUrlCommand(getId(), url));
    }

    public boolean getShowDevTools() {
        return showDevTools;
    }

    public void setShowDevTools(boolean showDevTools) {
        this.showDevTools = showDevTools;
        queueCommandIfRendered(() -> new UiPdfViewer.SetShowDevToolsCommand(getId(), showDevTools));
    }

    public UiPdfViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(UiPdfViewMode viewMode) {
        this.viewMode = viewMode;
        queueCommandIfRendered(() -> new UiPdfViewer.SetViewModeCommand(getId(), viewMode));
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        queueCommandIfRendered(() -> new UiPdfViewer.SetPaddingCommand(getId(), padding));
    }

    public int getPageSpacing() {
        return pageSpacing;
    }

    public void setPageSpacing(int pageSpacing) {
        this.pageSpacing = pageSpacing;
        queueCommandIfRendered(() -> new UiPdfViewer.SetPageSpacingCommand(getId(), pageSpacing));
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    /**
     * CAUTION: This does only work in UiPdfZoomMode.MANUAL!
     * The modes UiPdfZoomMode.TO_WIDTH and UiPdfZoomMode.TO_HEIGHT
     * will calculate the ZoomFactor for themselves and set the mode to MANUAL afterwards.
     * @param zoomFactor the zoom factor to set
     */
    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
        queueCommandIfRendered(() -> new UiPdfViewer.SetZoomFactorCommand(getId(), zoomFactor));
    }

    public UiPdfZoomMode getZoomMode() {
        return this.zoomMode;
    }

    public void setZoomMode(UiPdfZoomMode zoomMode) {
        this.zoomMode = zoomMode;
        queueCommandIfRendered(() -> new UiPdfViewer.SetZoomModeCommand(getId(), this.getZoomMode() ));
    }

    public UiBorder getPageBorder() {
        return pageBorder;
    }

    public void setPageBorder(UiBorder pageBorder) {
        this.pageBorder = pageBorder;
        queueCommandIfRendered(() -> new UiPdfViewer.SetPageBorderCommand(getId(), pageBorder));
    }

    public UiShadow getPageShadow() {
        return pageShadow;
    }

    public void setPageShadow(UiShadow pageShadow) {
        this.pageShadow = pageShadow;
        queueCommandIfRendered(() -> new UiPdfViewer.SetPageShadowCommand(getId(), pageShadow));
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String cssColor) {
        this.backgroundColor = cssColor;
        queueCommandIfRendered(() -> new UiPdfViewer.SetBackgroundColorCommand(getId(), cssColor));
    }
}
