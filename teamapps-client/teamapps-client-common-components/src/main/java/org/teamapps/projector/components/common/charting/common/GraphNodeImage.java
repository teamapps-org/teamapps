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
package org.teamapps.projector.components.common.charting.common;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.projector.components.common.dto.DtoTreeGraphNodeImage;
import org.teamapps.projector.components.common.dto.DtoTreeGraphNodeImageCornerShape;

public class GraphNodeImage {

	public enum CornerShape {
		ORIGINAL, ROUNDED, CIRCLE;

		public DtoTreeGraphNodeImageCornerShape toUiCornerShape() {
			return DtoTreeGraphNodeImageCornerShape.valueOf(this.name());
		}
	}
	private String url;

	private int width;
	private int height;
	private int centerTopDistance;
	private int centerLeftDistance;
	private CornerShape cornerShape = CornerShape.ROUNDED;
	private boolean shadow = false;
	private float borderWidth = 0;
	private Color borderColor = new RgbaColor(100, 100, 100);

	public GraphNodeImage(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
	}

	public DtoTreeGraphNodeImage createUiTreeGraphNodeImage() {
		DtoTreeGraphNodeImage ui = new DtoTreeGraphNodeImage(url, width, height);
		ui.setCenterTopDistance(centerTopDistance);
		ui.setCenterLeftDistance(centerLeftDistance);
		ui.setCornerShape(cornerShape.toUiCornerShape());
		ui.setShadow(shadow);
		ui.setBorderWidth(borderWidth);
		ui.setBorderColor(borderColor != null ? borderColor.toHtmlColorString(): null);
		return ui;
	}

	public String getUrl() {
		return url;
	}

	public GraphNodeImage setUrl(String url) {
		this.url = url;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public GraphNodeImage setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public GraphNodeImage setHeight(int height) {
		this.height = height;
		return this;
	}

	public int getCenterTopDistance() {
		return centerTopDistance;
	}

	public GraphNodeImage setCenterTopDistance(int centerTopDistance) {
		this.centerTopDistance = centerTopDistance;
		return this;
	}

	public int getCenterLeftDistance() {
		return centerLeftDistance;
	}

	public GraphNodeImage setCenterLeftDistance(int centerLeftDistance) {
		this.centerLeftDistance = centerLeftDistance;
		return this;
	}

	public CornerShape getCornerShape() {
		return cornerShape;
	}

	public GraphNodeImage setCornerShape(CornerShape cornerShape) {
		this.cornerShape = cornerShape;
		return this;
	}

	public boolean isShadow() {
		return shadow;
	}

	public GraphNodeImage setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public GraphNodeImage setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public GraphNodeImage setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}
}
