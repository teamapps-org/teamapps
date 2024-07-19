/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.component.documentviewer;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.BoxShadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ClientObjectLibrary(DocumentViewerLibrary.class)
public class DocumentViewer extends AbstractComponent implements DtoDocumentViewerEventHandler{

	private final DtoDocumentViewerClientObjectChannel clientObjectChannel = new DtoDocumentViewerClientObjectChannel(getClientObjectChannel());

	private List<String> pageUrls = new ArrayList<>();
	private PageDisplayMode displayMode = PageDisplayMode.FIT_WIDTH;
	private float zoomFactor = 1;
	private Border pageBorder;
	private BoxShadow pageShadow;
	private int padding;
	private int pageSpacing = 5;

	public DocumentViewer() {
	}

	public DocumentViewer(List<String> pageUrls) {
		this.pageUrls.addAll(pageUrls);
	}

	@Override
	public ComponentConfig createConfig() {
		DtoDocumentViewer documentViewer = new DtoDocumentViewer();
		mapAbstractUiComponentProperties(documentViewer);
		documentViewer.setPageUrls(pageUrls);
		documentViewer.setDisplayMode(displayMode);
		documentViewer.setZoomFactor(zoomFactor);
		documentViewer.setPageBorder(pageBorder != null ? pageBorder.createUiBorder() : null);
		documentViewer.setPageShadow(pageShadow != null ? pageShadow.createUiShadow() : null);
		documentViewer.setPadding(padding);
		documentViewer.setPageSpacing(pageSpacing);
		return documentViewer;
	}

	public List<String> getPageUrls() {
		return pageUrls;
	}

	public void setPageUrls(List<String> pageUrls) {
		this.pageUrls.clear();
		this.pageUrls.addAll(pageUrls);
		clientObjectChannel.setPageUrls(this.pageUrls);
	}

	public void setPageUrls(String... pageUrls) {
		setPageUrls(Arrays.asList(pageUrls));
	}

	public void addPageUrl(String pageUrl) {
		this.pageUrls.add(pageUrl);
		clientObjectChannel.setPageUrls(this.pageUrls);
	}

	public PageDisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(PageDisplayMode displayMode) {
		this.displayMode = displayMode;
		clientObjectChannel.setDisplayMode(displayMode, zoomFactor);
	}

	public float getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		clientObjectChannel.setZoomFactor(zoomFactor);
	}

	public Border getPageBorder() {
		return pageBorder;
	}

	public void setPageBorder(Border pageBorder) {
		this.pageBorder = pageBorder;
		clientObjectChannel.setPageBorder(pageBorder != null ? pageBorder.createUiBorder(): null);
	}

	public BoxShadow getPageShadow() {
		return pageShadow;
	}

	public void setPageShadow(BoxShadow pageShadow) {
		this.pageShadow = pageShadow;
		clientObjectChannel.setPageShadow(pageShadow != null ? pageShadow.createUiShadow(): null);
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		clientObjectChannel.setPaddding(padding);
	}

	public int getPageSpacing() {
		return pageSpacing;
	}

	public void setPageSpacing(int pageSpacing) {
		this.pageSpacing = pageSpacing;
		clientObjectChannel.setPageSpacing(pageSpacing);
	}

}
