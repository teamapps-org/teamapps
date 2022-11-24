package org.teamapps.custom;

import org.teamapps.custom.dto.UiMyComponent;
import org.teamapps.dto.DtoComponent;
import org.teamapps.ux.component.AbstractComponent;


public class MyComponent extends AbstractComponent {
	@Override
	public DtoComponent createUiClientObject() {
		return new UiMyComponent();
	}
}
