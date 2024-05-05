package org.teamapps.projector.clientobject;

import java.util.Map;

public interface ComponentConfig {

	public boolean isVisible();

	public Map<String, Map<String, String>> getStylesBySelector();

	public Map<String, Map<String, Boolean>> getClassNamesBySelector();

	public Map<String, Map<String, String>> getAttributesBySelector();

}
