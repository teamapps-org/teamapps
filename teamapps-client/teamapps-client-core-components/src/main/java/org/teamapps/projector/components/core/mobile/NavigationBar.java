/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.components.core.mobile;

import org.teamapps.common.format.Color;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.progress.DefaultMultiProgressDisplay;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.projector.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class NavigationBar<RECORD> extends AbstractComponent implements Component {

	public ProjectorEvent<NavigationBarButton> onButtonClick = createProjectorEventBoundToUiEvent(DtoNavigationBar.ButtonClickedEvent.TYPE_ID);

	private Template buttonTemplate = BaseTemplate.NAVIGATION_BAR_ICON_ONLY;
	private List<NavigationBarButton<RECORD>> buttons = new ArrayList<>();
	private Color backgroundColor;
	private Color borderColor;
	private final List<Component> fanOutComponents = new ArrayList<>();
	private Component activeFanOutComponent;
	private int buttonClientIdCounter = 0;
	private MultiProgressDisplay multiProgressDisplay = new DefaultMultiProgressDisplay();

	public NavigationBar() {
		super();
	}

	@Override
	public DtoComponent createConfig() {
		DtoNavigationBar uiNavigationBar = new DtoNavigationBar(buttonTemplate != null ? buttonTemplate.createClientReference() : null);
		mapAbstractUiComponentProperties(uiNavigationBar);
		uiNavigationBar.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiNavigationBar.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		if (buttons != null) {
			List<DtoNavigationBarButton> uiNavigationBarButtons = createUiButtons();
			uiNavigationBar.setButtons(uiNavigationBarButtons);
		}
		if (fanOutComponents != null) {
			List<DtoReference> uiComponents = fanOutComponents.stream()
					.map(component -> component.createClientReference())
					.collect(Collectors.toList());
			uiNavigationBar.setFanOutComponents(uiComponents);
		}
		uiNavigationBar.setMultiProgressDisplay(multiProgressDisplay.createClientReference());
		return uiNavigationBar;
	}

	private List<DtoNavigationBarButton> createUiButtons() {
		return buttons.stream()
				.map(navigationBarButton -> navigationBarButton.createUiNavigationBarButton())
				.collect(Collectors.toList());
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoNavigationBar.ButtonClickedEvent.TYPE_ID -> {
				var clickedEvent = event.as(DtoNavigationBar.ButtonClickedEventWrapper.class);
				String buttonId = clickedEvent.getButtonId();
				buttons.stream()
						.filter(btn -> btn.getClientId().equals(buttonId))
						.forEach(button -> {
							onButtonClick.fire(button);
							button.onClick.fire(null);
						});
			}
			case DtoNavigationBar.FanoutClosedDueToClickOutsideFanoutEvent.TYPE_ID -> {
				var e = event.as(DtoNavigationBar.FanoutClosedDueToClickOutsideFanoutEventWrapper.class);
				this.activeFanOutComponent = null;
			}
		}
	}

	public NavigationBar<RECORD> addButton(NavigationBarButton<RECORD> button) {
		return addButton(button, false);
	}

	public NavigationBar<RECORD> addButton(NavigationBarButton<RECORD> button, boolean left) {
		button.setClientId("" + ++buttonClientIdCounter);
		button.setContainer(this);
		if (left) {
			buttons.add(0, button);
		} else {
			buttons.add(button);
		}
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetButtonsCommand(createUiButtons()), null);
		return this;
	}

	/*package-private*/ void handleButtonVisibilityChanged(NavigationBarButton<RECORD> button) {
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetButtonVisibleCommand(button.getClientId(), button.isVisible()), null);
	}

	public void removeButton(NavigationBarButton<RECORD> button) {
		buttons.remove(button);
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetButtonsCommand(createUiButtons()), null);
	}

	/**
	 * May be used for client-side performance reasons.
	 */
	// TODO #componentRef still needed after component reference implementation??
	public void preloadFanOutComponent(Component component) {
		fanOutComponents.add(component);
		if (component != null) {
			component.setParent(this);
		}
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.AddFanOutComponentCommand(component != null ? component.createClientReference() : null), null);
	}

	public void showFanOutComponent(Component component) {
		if (!fanOutComponents.contains(component)) {
			preloadFanOutComponent(component);
		}
		activeFanOutComponent = component;
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.ShowFanOutComponentCommand(component != null ? component.createClientReference() : null), null);
	}

	public void hideFanOutComponent() {
		if (activeFanOutComponent == null) {
			return;
		}
		activeFanOutComponent = null;
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.HideFanOutComponentCommand(), null);
	}

	public void showOrHideFanoutComponent(Component component) {
		if (activeFanOutComponent == component) {
			hideFanOutComponent();
		} else {
			showFanOutComponent(component);
		}
	}

	public Template getButtonTemplate() {
		return buttonTemplate;
	}

	public void setButtonTemplate(Template buttonTemplate) {
		this.buttonTemplate = buttonTemplate;
	}

	public List<NavigationBarButton<RECORD>> getButtons() {
		return buttons;
	}

	public void setButtons(List<NavigationBarButton<RECORD>> buttons) {
		buttons.forEach(button -> button.setContainer(this));
		this.buttons = buttons;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetBackgroundColorCommand(backgroundColor != null ? backgroundColor.toHtmlColorString() : null), null);
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetBorderColorCommand(borderColor != null ? borderColor.toHtmlColorString() : null), null);
	}

	public List<Component> getFanOutComponents() {
		return fanOutComponents;
	}

	public Component getActiveFanOutComponent() {
		return activeFanOutComponent;
	}

	public void setMultiProgressDisplay(MultiProgressDisplay multiProgressDisplay) {
		this.multiProgressDisplay = multiProgressDisplay;
		getClientObjectChannel().sendCommandIfRendered(new DtoNavigationBar.SetMultiProgressDisplayCommand(multiProgressDisplay.createClientReference()), null);
	}

	public MultiProgressDisplay getMultiProgressDisplay() {
		return multiProgressDisplay;
	}
}
