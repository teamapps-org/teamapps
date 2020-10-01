/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.mobile;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNavigationBar;
import org.teamapps.dto.UiNavigationBarButton;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.progress.DefaultMultiProgressDisplay;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NavigationBar<RECORD> extends AbstractComponent implements Container {

	public Event<NavigationBarButton> onButtonClick = new Event<>();

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
	public UiComponent createUiComponent() {
		UiNavigationBar uiNavigationBar = new UiNavigationBar(buttonTemplate.createUiTemplate());
		mapAbstractUiComponentProperties(uiNavigationBar);
		uiNavigationBar.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiNavigationBar.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		if (buttons != null) {
			List<UiNavigationBarButton> uiNavigationBarButtons = createUiButtons();
			uiNavigationBar.setButtons(uiNavigationBarButtons);
		}
		if (fanOutComponents != null) {
			List<UiClientObjectReference> uiComponents = fanOutComponents.stream()
					.map(component -> component.createUiReference())
					.collect(Collectors.toList());
			uiNavigationBar.setFanOutComponents(uiComponents);
		}
		uiNavigationBar.setMultiProgressDisplay(multiProgressDisplay.createUiReference());
		return uiNavigationBar;
	}

	private List<UiNavigationBarButton> createUiButtons() {
		return buttons.stream()
				.map(navigationBarButton -> navigationBarButton.createUiNavigationBarButton())
				.collect(Collectors.toList());
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_NAVIGATION_BAR_BUTTON_CLICKED:
				UiNavigationBar.ButtonClickedEvent clickedEvent = (UiNavigationBar.ButtonClickedEvent) event;
				String buttonId = clickedEvent.getButtonId();
				buttons.stream()
						.filter(btn -> btn.getClientId().equals(buttonId))
						.forEach(button -> {
							onButtonClick.fire(button);
							button.onClick.fire(null);
						});
				break;
			case UI_NAVIGATION_BAR_FANOUT_CLOSED_DUE_TO_CLICK_OUTSIDE_FANOUT:
				this.activeFanOutComponent = null;
				break;
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
		queueCommandIfRendered(() -> new UiNavigationBar.SetButtonsCommand(getId(), createUiButtons()));
		return this;
	}

	/*package-private*/ void handleButtonVisibilityChanged(NavigationBarButton<RECORD> button) {
		queueCommandIfRendered(() -> new UiNavigationBar.SetButtonVisibleCommand(getId(), button.getClientId(), button.isVisible()));
	}

	public void removeButton(NavigationBarButton<RECORD> button) {
		buttons.remove(button);
		queueCommandIfRendered(() -> new UiNavigationBar.SetButtonsCommand(getId(), createUiButtons()));
	}

	/**
	 * May be used for client-side performance reasons.
	 */
	// TODO #componentRef still needed after component reference implementation??
	public void preloadFanOutComponent(Component component) {
		fanOutComponents.add(component);
		component.setParent(this);
		queueCommandIfRendered(() -> new UiNavigationBar.AddFanOutComponentCommand(getId(), component.createUiReference()));
	}

	public void showFanOutComponent(Component component) {
		if (!fanOutComponents.contains(component)) {
			preloadFanOutComponent(component);
		}
		activeFanOutComponent = component;
		queueCommandIfRendered(() -> new UiNavigationBar.ShowFanOutComponentCommand(getId(), component.createUiReference()));
	}

	public void hideFanOutComponent() {
		if (activeFanOutComponent == null) {
			return;
		}
		activeFanOutComponent = null;
		queueCommandIfRendered(() -> new UiNavigationBar.HideFanOutComponentCommand(getId()));
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
		queueCommandIfRendered(() -> new UiNavigationBar.SetBackgroundColorCommand(getId(), backgroundColor != null ? backgroundColor.toHtmlColorString() : null));
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		queueCommandIfRendered(() -> new UiNavigationBar.SetBorderColorCommand(getId(), borderColor != null ? borderColor.toHtmlColorString() : null));
	}

	public List<Component> getFanOutComponents() {
		return fanOutComponents;
	}

	public Component getActiveFanOutComponent() {
		return activeFanOutComponent;
	}

	@Override
	public boolean isChildVisible(Component child) {
		return this.isEffectivelyVisible() && this.activeFanOutComponent == child;
	}

	public void setMultiProgressDisplay(MultiProgressDisplay multiProgressDisplay) {
		this.multiProgressDisplay = multiProgressDisplay;
		queueCommandIfRendered(() -> new UiNavigationBar.SetMultiProgressDisplayCommand(getId(), multiProgressDisplay.createUiReference()));
	}

	public MultiProgressDisplay getMultiProgressDisplay() {
		return multiProgressDisplay;
	}
}
