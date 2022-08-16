package org.teamapps.custom;

import org.teamapps.custom.dto.UiMyComponent;
import org.teamapps.dto.UiComponent;
import org.teamapps.ux.component.AbstractComponent;

public class MyComponent extends AbstractComponent {
	@Override
	public UiComponent createUiComponent() {
		return new UiMyComponent();
	}
}
