package org.teamapps.ux.application.assembler;

import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;

public interface MobileApplicationNavigationController {

	Component getApplicationLauncher();

	View getApplicationMenuView();

	ButtonData getButtonData(ButtonDataType buttonDataType);

	boolean isBackOperationAvailable();

	void fireBackOperation();
}
