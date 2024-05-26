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
package org.teamapps.projector.components.common.documentviewer;

import org.teamapps.projector.components.common.dto.DtoComponent;
import org.teamapps.projector.components.common.dto.DtoDocumentViewer;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.BoxShadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentViewer extends AbstractComponent {

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
	public DtoComponent createDto() {
		DtoDocumentViewer documentViewer = new DtoDocumentViewer();
		mapAbstractUiComponentProperties(documentViewer);
		documentViewer.setPageUrls(pageUrls);
		documentViewer.setDisplayMode(displayMode.toUiPageDisplayMode());
		documentViewer.setZoomFactor(zoomFactor);
		documentViewer.setPageBorder(pageBorder != null ? pageBorder.createUiBorder() : null);
		documentViewer.setPageShadow(pageShadow != null ? pageShadow.createUiShadow() : null);
		documentViewer.setPadding(padding);
		documentViewer.setPageSpacing(pageSpacing);
		return documentViewer;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		// no events so far...
	}

	public List<String> getPageUrls() {
		return pageUrls;
	}

	public void setPageUrls(List<String> pageUrls) {
		this.pageUrls.clear();
		this.pageUrls.addAll(pageUrls);
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPageUrlsCommand(this.pageUrls));
	}

	public void setPageUrls(String... pageUrls) {
		setPageUrls(Arrays.asList(pageUrls));
	}

	public void addPageUrl(String pageUrl) {
		this.pageUrls.add(pageUrl);
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPageUrlsCommand(this.pageUrls));
	}

	public PageDisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(PageDisplayMode displayMode) {
		this.displayMode = displayMode;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetDisplayModeCommand(displayMode.toUiPageDisplayMode(), zoomFactor));
	}

	public float getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetZoomFactorCommand(zoomFactor));
	}

	public Border getPageBorder() {
		return pageBorder;
	}

	public void setPageBorder(Border pageBorder) {
		this.pageBorder = pageBorder;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPageBorderCommand(pageBorder != null ? pageBorder.createUiBorder(): null));
	}

	public BoxShadow getPageShadow() {
		return pageShadow;
	}

	public void setPageShadow(BoxShadow pageShadow) {
		this.pageShadow = pageShadow;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPageShadowCommand(pageShadow != null ? pageShadow.createUiShadow(): null));
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPadddingCommand(padding));
	}

	public int getPageSpacing() {
		return pageSpacing;
	}

	public void setPageSpacing(int pageSpacing) {
		this.pageSpacing = pageSpacing;
		sendCommandIfRendered(() -> new DtoDocumentViewer.SetPageSpacingCommand(pageSpacing));
	}

}