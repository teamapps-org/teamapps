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
package org.teamapps.projector.component.mapview.shape;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.mapview.DtoAbstractMapShape;
import org.teamapps.projector.component.mapview.DtoAbstractMapShapeChange;

import java.util.UUID;

public abstract class AbstractMapShape {

	protected String clientId = UUID.randomUUID().toString();

	protected final ShapeProperties properties; // TODO make changeable!
	protected MapShapeListener listener;

	public AbstractMapShape() {
		this(new ShapeProperties(RgbaColor.BLUE));
	}

	public AbstractMapShape(ShapeProperties properties) {
		this.properties = properties;
	}

	public abstract DtoAbstractMapShape createDtoMapShape();

	protected void mapAbstractUiShapeProperties(DtoAbstractMapShape uiShape) {
		uiShape.setShapeProperties(properties.createDtoShapeProperties());
	}


	public ShapeProperties getProperties() {
		return properties;
	}

	public void remove() {
		listener.handleShapeRemoved(this);
	}

	public void setListenerInternal(MapShapeListener listener) {
		this.listener = listener;
	}

	public String getClientIdInternal() {
		return clientId;
	}

	public void setClientIdInternal(String clientId) {
		this.clientId = clientId;
	}

	public interface MapShapeListener {
		void handleShapeChanged(AbstractMapShape shape);

		void handleShapeChanged(AbstractMapShape shape, DtoAbstractMapShapeChange change);

		void handleShapeRemoved(AbstractMapShape shape);
	}
}
