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

	/**
	 * @deprecated For internal use only!!
	 */
	@Deprecated
	public void setListener(MapShapeListener listener) {
		this.listener = listener;
	}

	/**
	 * @deprecated For internal use only!!
	 */
	@Deprecated
	public String getClientId() {
		return clientId;
	}

	/**
	 * @deprecated For internal use only!!
	 */
	@Deprecated
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public interface MapShapeListener {
		void handleChanged(AbstractMapShape shape);

		void handleRemoved(AbstractMapShape shape);
	}
}
