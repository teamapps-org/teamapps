/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.map.shape;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiShapeProperties;

import java.util.List;

public class ShapeProperties {

	private Color strokeColor;
	private float strokeWeight = 2f;
	private List<Float> strokeDashArray;
	private Color fillColor = Color.TRANSPARENT;

	public ShapeProperties(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public ShapeProperties(Color strokeColor, float strokeWeight) {
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
	}

	public ShapeProperties(Color strokeColor, float strokeWeight, List<Float> strokeDashArray) {
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
		this.strokeDashArray = strokeDashArray;
	}

	public ShapeProperties(Color strokeColor, float strokeWeight, Color fillColor) {
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
		this.fillColor = fillColor;
	}

	public ShapeProperties(UiShapeProperties uiProperties) {
		if (uiProperties.getStrokeColor() != null) {
			this.strokeColor = Color.fromHtmlString(uiProperties.getStrokeColor());
		}
		if (uiProperties.getFillColor() != null) {
			this.fillColor = Color.fromHtmlString(uiProperties.getFillColor());
		}
		this.strokeWeight = uiProperties.getStrokeWeight();
		this.strokeDashArray = uiProperties.getStrokeDashArray();
	}
	                                                                                                                      					
	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public float getStrokeWeight() {
		return strokeWeight;
	}

	public void setStrokeWeight(float strokeWeight) {
		this.strokeWeight = strokeWeight;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public List<Float> getStrokeDashArray() {
		return strokeDashArray;
	}

	public void setStrokeDashArray(List<Float> strokeDashArray) {
		this.strokeDashArray = strokeDashArray;
	}

	public UiShapeProperties createUiShapeProperties() {
		UiShapeProperties properties = new UiShapeProperties();
		properties.setFillColor(fillColor != null ? fillColor.toHtmlColorString() : null);
		properties.setStrokeColor(strokeColor != null ? strokeColor.toHtmlColorString() : null);
		properties.setStrokeWeight(strokeWeight);
		properties.setStrokeDashArray(strokeDashArray);
		return properties;
	}
}
