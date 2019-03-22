/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.map;

import org.teamapps.dto.UiShapeProperties;
import org.teamapps.common.format.Color;

public class ShapeProperties {

	private Color strokeColor = Color.BLACK;
	private float strokeOpacity = 0.8f;
	private float strokeWeight = 2f;
	private String strokeDashArray;
	private Color fillColor = Color.BLUE;
	private float fillOpacity = 0.4f;

	public ShapeProperties(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public ShapeProperties(Color strokeColor, float strokeWeight) {
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
	}

	public ShapeProperties(Color strokeColor, float strokeWeight, String strokeDashArray) {
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
		this.strokeDashArray = strokeDashArray;
	}

	public ShapeProperties(Color strokeColor, float strokeOpacity, float strokeWeight, Color fillColor, float fillOpacity) {
		this.strokeColor = strokeColor;
		this.strokeOpacity = strokeOpacity;
		this.strokeWeight = strokeWeight;
		this.fillColor = fillColor;
		this.fillOpacity = fillOpacity;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public float getStrokeOpacity() {
		return strokeOpacity;
	}

	public void setStrokeOpacity(float strokeOpacity) {
		this.strokeOpacity = strokeOpacity;
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

	public float getFillOpacity() {
		return fillOpacity;
	}

	public void setFillOpacity(float fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	public String getStrokeDashArray() {
		return strokeDashArray;
	}

	public void setStrokeDashArray(String strokeDashArray) {
		this.strokeDashArray = strokeDashArray;
	}

	public UiShapeProperties createUiShapeProperties() {
		UiShapeProperties properties = new UiShapeProperties();
		properties.setFillColor(fillColor.toHtmlColorString());
		properties.setFillOpacity(fillOpacity);
		properties.setStrokeColor(strokeColor.toHtmlColorString());
		properties.setStrokeOpacity(strokeOpacity);
		properties.setStrokeWeight(strokeWeight);
		properties.setStrokeDashArray(strokeDashArray);
		return properties;
	}
}
