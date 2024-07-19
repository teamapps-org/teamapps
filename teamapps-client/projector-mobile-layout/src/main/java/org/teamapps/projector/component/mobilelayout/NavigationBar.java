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
package org.teamapps.projector.component.mobilelayout;

import org.teamapps.common.format.Color;
import org.teamapps.commons.util.CollectionCastUtil;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.progress.MultiProgressDisplay;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class NavigationBar extends AbstractComponent implements DtoNavigationBarEventHandler {

	private final DtoNavigationBarClientObjectChannel clientObjectChannel = new DtoNavigationBarClientObjectChannel(getClientObjectChannel());

	public ProjectorEvent<NavigationBarButton> onButtonClick = new ProjectorEvent<>(clientObjectChannel::toggleButtonClickedEvent);

	private List<NavigationBarButton> buttons = new ArrayList<>();
	private Color backgroundColor;
	private Color borderColor;
	private final List<Component> fanOutComponents = new ArrayList<>();
	private Component activeFanOutComponent;
	private int buttonClientIdCounter = 0;
	private MultiProgressDisplay multiProgressDisplay = new MultiProgressDisplay();

	public NavigationBar() {
		super();
	}

	@Override
	public DtoComponent createConfig() {
		DtoNavigationBar uiNavigationBar = new DtoNavigationBar();
		mapAbstractUiComponentProperties(uiNavigationBar);
		uiNavigationBar.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiNavigationBar.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		if (buttons != null) {
			List<DtoNavigationBarButton> uiNavigationBarButtons = createUiButtons();
			uiNavigationBar.setButtons(uiNavigationBarButtons);
		}
		if (fanOutComponents != null) {
			uiNavigationBar.setFanOutComponents(CollectionCastUtil.castList(fanOutComponents));
		}
		uiNavigationBar.setMultiProgressDisplay(multiProgressDisplay);
		return uiNavigationBar;
	}

	private List<DtoNavigationBarButton> createUiButtons() {
		return buttons.stream()
				.map(navigationBarButton -> navigationBarButton.createUiNavigationBarButton())
				.collect(Collectors.toList());
	}

	@Override
	public void handleButtonClicked(DtoNavigationBar.ButtonClickedEventWrapper event) {
		buttons.stream()
				.filter(btn -> btn.getClientId().equals(event.getButtonId()))
				.forEach(button -> {
					onButtonClick.fire(button);
					button.onClick.fire(null);
				});
	}

	@Override
	public void handleFanoutClosedDueToClickOutsideFanout() {
		this.activeFanOutComponent = null;
	}

	public NavigationBar addButton(NavigationBarButton button) {
		return addButton(button, false);
	}

	public NavigationBar addButton(NavigationBarButton button, boolean left) {
		button.setClientId("" + ++buttonClientIdCounter);
		button.setContainer(this);
		if (left) {
			buttons.add(0, button);
		} else {
			buttons.add(button);
		}
		clientObjectChannel.setButtons(createUiButtons());
		return this;
	}

	/*package-private*/ void handleButtonVisibilityChanged(NavigationBarButton button) {
		clientObjectChannel.setButtonVisible(button.getClientId(), button.isVisible());
	}

	public void removeButton(NavigationBarButton button) {
		buttons.remove(button);
		clientObjectChannel.setButtons(createUiButtons());
	}

	/**
	 * May be used for client-side performance reasons.
	 */
	// TODO #componentRef still needed after component reference implementation??
	public void preloadFanOutComponent(Component component) {
		fanOutComponents.add(component);
		clientObjectChannel.addFanOutComponent(component);
	}

	public void showFanOutComponent(Component component) {
		if (!fanOutComponents.contains(component)) {
			preloadFanOutComponent(component);
		}
		activeFanOutComponent = component;
		clientObjectChannel.showFanOutComponent(component);
	}

	public void hideFanOutComponent() {
		if (activeFanOutComponent == null) {
			return;
		}
		activeFanOutComponent = null;
		clientObjectChannel.hideFanOutComponent();
	}

	public void showOrHideFanoutComponent(Component component) {
		if (activeFanOutComponent == component) {
			hideFanOutComponent();
		} else {
			showFanOutComponent(component);
		}
	}

	public List<NavigationBarButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<NavigationBarButton> buttons) {
		buttons.forEach(button -> button.setContainer(this));
		this.buttons = buttons;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		clientObjectChannel.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		clientObjectChannel.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
	}

	public List<Component> getFanOutComponents() {
		return fanOutComponents;
	}

	public Component getActiveFanOutComponent() {
		return activeFanOutComponent;
	}

	public void setMultiProgressDisplay(MultiProgressDisplay multiProgressDisplay) {
		this.multiProgressDisplay = multiProgressDisplay;
		clientObjectChannel.setMultiProgressDisplay(multiProgressDisplay);
	}

	public MultiProgressDisplay getMultiProgressDisplay() {
		return multiProgressDisplay;
	}

}
