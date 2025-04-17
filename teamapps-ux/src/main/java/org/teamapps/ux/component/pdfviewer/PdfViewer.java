package org.teamapps.ux.component.pdfviewer;

import org.teamapps.dto.*;
import org.teamapps.ux.component.AbstractComponent;

import java.util.stream.Collectors;

public class PdfViewer extends AbstractComponent {

    protected String url;
    protected UiPdfViewMode viewMode;
    protected int padding = 0;
    protected int pageSpacing = 5;
    protected float zoomFactor = 1f;
    protected boolean zoomByAvailableWidth = true;
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
        uiPdfViewer.setUrl(url);
        uiPdfViewer.setViewMode(viewMode);
        uiPdfViewer.setPadding(padding);
        uiPdfViewer.setPageSpacing(pageSpacing);
        uiPdfViewer.setZoomFactor(zoomFactor);
        uiPdfViewer.setZoomByAvailableWidth(zoomByAvailableWidth);
        uiPdfViewer.setPageBorder(pageBorder);
        uiPdfViewer.setPageShadow(pageShadow);
        return uiPdfViewer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        // TODO @bjesuiter: update all setters like this
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
        queueCommandIfRendered(() -> new UiPdfViewer.SetZoomFactorCommand(getId(), zoomFactor, this.isZoomByAvailableWidth()));
    }

    public boolean isZoomByAvailableWidth() {
        return zoomByAvailableWidth;
    }

    public void setZoomByAvailableWidth(boolean zoomByAvailableWidth) {
        this.zoomByAvailableWidth = zoomByAvailableWidth;
        queueCommandIfRendered(() -> new UiPdfViewer.SetZoomFactorCommand(getId(), this.getPadding(), zoomByAvailableWidth ));
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
