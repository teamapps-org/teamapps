package org.teamapps.projector.clientobject;

import java.util.Map;

public abstract class AbstractComponentConfig implements ComponentConfig {

	protected boolean visible;
	protected Map<String, Map<String, String>> stylesBySelector;
	protected Map<String, Map<String, Boolean>> classNamesBySelector;
	protected Map<String, Map<String, String>> attributesBySelector;

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public Map<String, Map<String, String>> getStylesBySelector() {
		return stylesBySelector;
	}

	public void setStylesBySelector(Map<String, Map<String, String>> stylesBySelector) {
		this.stylesBySelector = stylesBySelector;
	}

	@Override
	public Map<String, Map<String, Boolean>> getClassNamesBySelector() {
		return classNamesBySelector;
	}

	public void setClassNamesBySelector(Map<String, Map<String, Boolean>> classNamesBySelector) {
		this.classNamesBySelector = classNamesBySelector;
	}

	@Override
	public Map<String, Map<String, String>> getAttributesBySelector() {
		return attributesBySelector;
	}

	public void setAttributesBySelector(Map<String, Map<String, String>> attributesBySelector) {
		this.attributesBySelector = attributesBySelector;
	}
}