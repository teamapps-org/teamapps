package org.teamapps.ux.component;

import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiEvent;

public interface ClientObject {

	String getId();

	void render();

	void unrender();

	boolean isRendered();

	UiClientObjectReference createUiReference();

	default void handleUiEvent(UiEvent event) {
	}

}
