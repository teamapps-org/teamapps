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
package org.teamapps.projector.components.common.charting.tree;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.projector.components.common.charting.common.GraphNodeIcon;
import org.teamapps.projector.components.common.charting.common.GraphNodeImage;
import org.teamapps.projector.template.Template;

import java.util.UUID;

public class BaseTreeGraphNode<RECORD> {
	private final String id = UUID.randomUUID().toString();
	private int width;
	private int height;
	private Color backgroundColor = new RgbaColor(255, 255, 255);
	private Color borderColor = new RgbaColor(100, 100, 100);
	private float borderWidth = 1;
	private float borderRadius = 3;
	private GraphNodeImage image;
	private GraphNodeIcon icon;
	private Template template;
	private RECORD record;
	private Color connectorLineColor = new RgbaColor(100, 100, 100);
	private int connectorLineWidth;
	private String dashArray;

	protected String getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public BaseTreeGraphNode<RECORD> setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public BaseTreeGraphNode<RECORD> setHeight(int height) {
		this.height = height;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public BaseTreeGraphNode<RECORD> setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public BaseTreeGraphNode<RECORD> setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public BaseTreeGraphNode<RECORD> setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public float getBorderRadius() {
		return borderRadius;
	}

	public BaseTreeGraphNode<RECORD> setBorderRadius(float borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public GraphNodeImage getImage() {
		return image;
	}

	public BaseTreeGraphNode<RECORD> setImage(GraphNodeImage image) {
		this.image = image;
		return this;
	}

	public GraphNodeIcon getIcon() {
		return icon;
	}

	public BaseTreeGraphNode<RECORD> setIcon(GraphNodeIcon icon) {
		this.icon = icon;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public BaseTreeGraphNode<RECORD> setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public RECORD getRecord() {
		return record;
	}

	public BaseTreeGraphNode<RECORD> setRecord(RECORD record) {
		this.record = record;
		return this;
	}

	public Color getConnectorLineColor() {
		return connectorLineColor;
	}

	public BaseTreeGraphNode<RECORD> setConnectorLineColor(Color connectorLineColor) {
		this.connectorLineColor = connectorLineColor;
		return this;
	}

	public int getConnectorLineWidth() {
		return connectorLineWidth;
	}

	public BaseTreeGraphNode<RECORD> setConnectorLineWidth(int connectorLineWidth) {
		this.connectorLineWidth = connectorLineWidth;
		return this;
	}

	public String getDashArray() {
		return dashArray;
	}

	public BaseTreeGraphNode<RECORD> setDashArray(String dashArray) {
		this.dashArray = dashArray;
		return this;
	}
}
