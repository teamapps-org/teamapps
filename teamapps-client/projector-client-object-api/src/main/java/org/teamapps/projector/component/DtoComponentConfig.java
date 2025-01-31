package org.teamapps.projector.component;

import java.util.Map;

public interface DtoComponentConfig {

	boolean isVisible();

	Map<String, Map<String, String>> getStylesBySelector();

	Map<String, Map<String, Boolean>> getClassNamesBySelector();

	Map<String, Map<String, String>> getAttributesBySelector();

}
