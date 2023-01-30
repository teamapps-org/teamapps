/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.component.panel;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiElegantPanel;
import org.teamapps.dto.UiEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.Spacing;

public class ElegantPanel extends AbstractComponent {

	private Color bodyBackgroundColor = RgbaColor.WHITE.withAlpha(.4f);
	private AbstractComponent content;
	private HorizontalElementAlignment horizontalContentAlignment = HorizontalElementAlignment.CENTER;
	private Spacing padding = new Spacing(20, 10, 20, 10);
	private int maxContentWidth = 0;

	public ElegantPanel() {
		this(null);
	}

	public ElegantPanel(AbstractComponent content) {
		this.content = content;
	}

	@Override
	public UiComponent createUiComponent() {
		UiElegantPanel uiElegantPanel = new UiElegantPanel();
		mapAbstractUiComponentProperties(uiElegantPanel);
		uiElegantPanel.setBodyBackgroundColor(bodyBackgroundColor != null ? bodyBackgroundColor.toHtmlColorString() : null);
		uiElegantPanel.setContent(content.createUiReference());
		uiElegantPanel.setMaxContentWidth(maxContentWidth);
		uiElegantPanel.setPadding(padding.createUiSpacing());
		uiElegantPanel.setHorizontalContentAlignment(horizontalContentAlignment.toUiHorizontalElementAlignment());
		return uiElegantPanel;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public Color getBodyBackgroundColor() {
		return bodyBackgroundColor;
	}

	public void setBodyBackgroundColor(Color bodyBackgroundColor) {
		this.bodyBackgroundColor = bodyBackgroundColor;
	}

	public AbstractComponent getContent() {
		return content;
	}

	public void setContent(AbstractComponent content) {
		this.content = content;
	}

	public Spacing getPadding() {
		return padding;
	}

	public void setPadding(Spacing padding) {
		this.padding = padding;
	}

	public int getMaxContentWidth() {
		return maxContentWidth;
	}

	public void setMaxContentWidth(int maxContentWidth) {
		this.maxContentWidth = maxContentWidth;
	}

	public HorizontalElementAlignment getHorizontalContentAlignment() {
		return horizontalContentAlignment;
	}

	public void setHorizontalContentAlignment(HorizontalElementAlignment horizontalContentAlignment) {
		this.horizontalContentAlignment = horizontalContentAlignment;
	}
}
