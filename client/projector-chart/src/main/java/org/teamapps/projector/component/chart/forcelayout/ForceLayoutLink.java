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
package org.teamapps.projector.component.chart.forcelayout;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.common.format.Color;

public class ForceLayoutLink<RECORD> {

	private final ForceLayoutNode<RECORD> source;
	private final ForceLayoutNode<RECORD> target;

	private float lineWidth = 1f;
	private Color lineColor = RgbaColor.MATERIAL_GREY_500;
	private String lineDashArray;
	private int linkLength = 150;

	public ForceLayoutLink(ForceLayoutNode<RECORD> source, ForceLayoutNode<RECORD> target) {
		this.source = source;
		this.target = target;
	}

	public DtoNetworkLink toDtoNetworkLink() {
		DtoNetworkLink ui = new DtoNetworkLink();
		ui.setSource(source.getId());
		ui.setTarget(target.getId());
		ui.setLineWidth(lineWidth);
		ui.setLineColor(lineColor != null ? lineColor.toHtmlColorString(): null);
		ui.setLineDashArray(lineDashArray);
		ui.setLinkLength(linkLength);
		return ui;
	}

	public ForceLayoutNode<RECORD> getSource() {
		return source;
	}

	public ForceLayoutNode<RECORD> getTarget() {
		return target;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public String getLineDashArray() {
		return lineDashArray;
	}

	public void setLineDashArray(String lineDashArray) {
		this.lineDashArray = lineDashArray;
	}

	public int getLinkLength() {
		return linkLength;
	}

	public void setLinkLength(int linkLength) {
		this.linkLength = linkLength;
	}
}
