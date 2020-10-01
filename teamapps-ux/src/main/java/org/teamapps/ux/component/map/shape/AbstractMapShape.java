/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.map.shape;

import org.teamapps.common.format.Color;
import org.teamapps.dto.AbstractUiMapShape;

import java.util.UUID;

public abstract class AbstractMapShape {

	protected String clientId = UUID.randomUUID().toString();

	protected final ShapeProperties properties; // TODO make changeable!
	protected MapShapeListener listener;

	public AbstractMapShape() {
		this(new ShapeProperties(Color.BLUE));
	}

	public AbstractMapShape(ShapeProperties properties) {
		this.properties = properties;
	}

	public abstract AbstractUiMapShape createUiMapShape();

	protected void mapAbstractUiShapeProperties(AbstractUiMapShape uiShape) {
		uiShape.setShapeProperties(properties.createUiShapeProperties());
	}


	public ShapeProperties getProperties() {
		return properties;
	}

	public void remove() {
		listener.handleRemoved(this);
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
		void handleChanged(AbstractMapShape shape);

		void handleRemoved(AbstractMapShape shape);
	}
}
