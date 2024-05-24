package org.teamapps.projector.clientobject.component;

import java.util.Map;

public interface ComponentConfig {

	boolean isVisible();

	Map<String, Map<String, String>> getStylesBySelector();

	Map<String, Map<String, Boolean>> getClassNamesBySelector();

	Map<String, Map<String, String>> getAttributesBySelector();

}
