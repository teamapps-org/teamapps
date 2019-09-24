package org.teamapps.ux.application;

import org.teamapps.ux.component.Component;

public abstract class AbstractApplication implements Application {

	private Component ui;

	@Override
	public Component getUi() {
		if (ui == null) {
			ui = createUi();
		}
		return ui;
	}

	protected abstract Component createUi();
}
