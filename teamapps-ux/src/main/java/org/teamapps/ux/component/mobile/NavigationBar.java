/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.mobile;

import org.jetbrains.annotations.NotNull;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNavigationBar;
import org.teamapps.dto.UiNavigationBarButton;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.teamapps.util.UiUtil.createUiColor;

public class NavigationBar<RECORD> extends AbstractComponent implements Container {

	public Event<NavigationBarButton> onButtonClick = new Event<>();

	private Template buttonTemplate = BaseTemplate.NAVIGATION_BAR_ICON_ONLY;
	private List<NavigationBarButton<RECORD>> buttons = new ArrayList<>();
	private Color backgroundColor = new Color(255, 255, 255, 0.84f);
	private Color borderColor;
	private List<Component> fanOutComponents = new ArrayList<>();
	private Component activeFanOutComponent;
	private int buttonClientIdCounter = 0;

	public NavigationBar() {
		super();
	}

	@Override
	public UiComponent createUiComponent() {
		UiNavigationBar uiNavigationBar = new UiNavigationBar(buttonTemplate.createUiTemplate());
		mapAbstractUiComponentProperties(uiNavigationBar);
		uiNavigationBar.setBackgroundColor(backgroundColor != null ? createUiColor(backgroundColor) : null);
		uiNavigationBar.setBorderColor(borderColor != null ? createUiColor(borderColor) : null);
		if (buttons != null) {
			List<UiNavigationBarButton> uiNavigationBarButtons = createUiButtons();
			uiNavigationBar.setButtons(uiNavigationBarButtons);
		}
		if (fanOutComponents != null) {
			List<UiComponentReference> uiComponents = fanOutComponents.stream()
					.map(component -> component.createUiComponentReference())
					.collect(Collectors.toList());
			uiNavigationBar.setFanOutComponents(uiComponents);
		}
		return uiNavigationBar;
	}

	@NotNull
	private List<UiNavigationBarButton> createUiButtons() {
		return buttons.stream()
				.map(navigationBarButton -> navigationBarButton.createUiNavigationBarButton())
				.collect(Collectors.toList());
	}

	@Override
	protected void doDestroy() {

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
		button.setClientId("" + ++buttonClientIdCounter);
		button.setContainer(this);
		buttons.add(button);
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
		queueCommandIfRendered(() -> new UiNavigationBar.AddFanOutComponentCommand(getId(), component.createUiComponentReference()));
	}

	public void showFanOutComponent(Component component) {
		if (!fanOutComponents.contains(component)) {
			preloadFanOutComponent(component);
		}
		activeFanOutComponent = component;
		queueCommandIfRendered(() -> new UiNavigationBar.ShowFanOutComponentCommand(getId(), component.createUiComponentReference()));
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
		queueCommandIfRendered(() -> new UiNavigationBar.SetBackgroundColorCommand(getId(), backgroundColor != null ? createUiColor(backgroundColor) : null));
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		queueCommandIfRendered(() -> new UiNavigationBar.SetBorderColorCommand(getId(), borderColor != null ? createUiColor(borderColor) : null));
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
}
