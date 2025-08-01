package org.teamapps.ux.component.pdfviewer;

import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.panel.WindowButtonType;

import java.util.stream.Collectors;

public class PdfViewer extends AbstractComponent {
    public final Event<UiPdfViewer.PdfInitializedEvent> onPdfInitialized = new Event<>();
    public final Event<UiPdfViewer.ZoomFactorChangedEvent> onZoomFactorChanged = new Event<>();

    protected String url;
    protected UiPdfViewMode viewMode;
    protected int padding = 0;
    protected int pageSpacing = 5;
    protected float zoomFactor = 1f;
    protected UiPdfZoomMode initialZoom = UiPdfZoomMode.TO_WIDTH;
    protected UiBorder pageBorder;
    protected UiShadow pageShadow;

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
        uiPdfViewer.setViewMode(viewMode);
        uiPdfViewer.setPadding(padding);
        uiPdfViewer.setPageSpacing(pageSpacing);
        uiPdfViewer.setZoomFactor(zoomFactor);
        uiPdfViewer.setPageBorder(pageBorder);
        uiPdfViewer.setPageShadow(pageShadow);
        uiPdfViewer.setInitialZoom(initialZoom);

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
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        queueCommandIfRendered(() -> new UiPdfViewer.SetUrlCommand(getId(), url));
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

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
        queueCommandIfRendered(() -> new UiPdfViewer.SetZoomFactorCommand(getId(), zoomFactor));
    }

    public UiPdfZoomMode getInitialZoom() {
        return initialZoom;
    }

    public void setInitialZoom(UiPdfZoomMode initialZoom) {
        this.initialZoom = initialZoom;
        queueCommandIfRendered(() -> new UiPdfViewer.SetInitialZoomCommand(getId(), this.getInitialZoom() ));
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
}
