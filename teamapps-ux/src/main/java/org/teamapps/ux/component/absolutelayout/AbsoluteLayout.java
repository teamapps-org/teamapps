/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.absolutelayout;

import org.teamapps.dto.UiAbsoluteLayout;
import org.teamapps.dto.UiAbsolutePositionedComponent;
import org.teamapps.dto.UiAbsolutePositioning;
import org.teamapps.dto.UiEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AbsoluteLayout extends AbstractComponent {

	private Map<Component, AbsolutePosition> positionsByComponent = new HashMap<>();
	private AnimationEasing animationEasing = AnimationEasing.EASE;
	private int animationDuration = 200;

	public AbsoluteLayout() {
	}

	public void putComponent(Component component, AbsolutePosition positioning) {
		positionsByComponent.put(component, positioning);
		updateUiLayout();
	}

	public void putComponents(Map<Component, AbsolutePosition> positioningByComponent, boolean removeExisting) {
		if (removeExisting) {
			this.positionsByComponent.clear();
		}
		this.positionsByComponent.putAll(positioningByComponent);
		updateUiLayout();
	}

	public void removeComponent(Component component) {
		positionsByComponent.remove(component);
		updateUiLayout();
	}

	private void updateUiLayout() {
		queueCommandIfRendered(() -> new UiAbsoluteLayout.UpdateCommand(getId(), createUiAbsolutePositionedComponents(), animationDuration, animationEasing.toUiAnimationEasing()));
	}

	private List<UiAbsolutePositionedComponent> createUiAbsolutePositionedComponents() {
		return positionsByComponent.entrySet().stream()
				.map(entry -> {
					Component component = entry.getKey();
					AbsolutePosition position = entry.getValue();
					return new UiAbsolutePositionedComponent(component.createUiReference(), new UiAbsolutePositioning(
							position.getTop() != null ? position.getTop().toCssString(): null,
							position.getRight() != null ? position.getRight().toCssString(): null,
							position.getBottom() != null ? position.getBottom().toCssString(): null,
							position.getLeft() != null ? position.getLeft().toCssString(): null,
							position.getWidth() != null ? position.getWidth().toCssString(): null,
							position.getHeight() != null ? position.getHeight().toCssString(): null,
							position.getZIndex()
					));
				})
				.collect(Collectors.toList());
	}

	@Override
	public UiAbsoluteLayout createUiComponent() {
		UiAbsoluteLayout uiAbsoluteLayout = new UiAbsoluteLayout();
		mapAbstractUiComponentProperties(uiAbsoluteLayout);
		uiAbsoluteLayout.setComponents(createUiAbsolutePositionedComponents());
		return uiAbsoluteLayout;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		// none
	}

	public AnimationEasing getAnimationEasing() {
		return animationEasing;
	}

	public void setAnimationEasing(AnimationEasing animationEasing) {
		this.animationEasing = animationEasing;
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}
}
