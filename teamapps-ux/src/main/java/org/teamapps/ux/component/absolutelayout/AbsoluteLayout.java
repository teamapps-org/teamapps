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
package org.teamapps.ux.component.absolutelayout;

import org.teamapps.dto.DtoAbsoluteLayout;
import org.teamapps.dto.DtoAbsolutePositionedComponent;
import org.teamapps.dto.DtoAbsolutePositioning;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class AbsoluteLayout extends AbstractComponent {

	private Map<org.teamapps.ux.component.Component, AbsolutePosition> positionsByComponent = new HashMap<>();
	private AnimationEasing animationEasing = AnimationEasing.EASE;
	private int animationDuration = 200;

	public AbsoluteLayout() {
	}

	public void putComponent(org.teamapps.ux.component.Component component, AbsolutePosition positioning) {
		positionsByComponent.put(component, positioning);
		updateUiLayout();
	}

	public void putComponents(Map<org.teamapps.ux.component.Component, AbsolutePosition> positioningByComponent, boolean removeExisting) {
		if (removeExisting) {
			this.positionsByComponent.clear();
		}
		this.positionsByComponent.putAll(positioningByComponent);
		updateUiLayout();
	}

	public void removeComponent(org.teamapps.ux.component.Component component) {
		positionsByComponent.remove(component);
		updateUiLayout();
	}

	private void updateUiLayout() {
		sendCommandIfRendered(() -> new DtoAbsoluteLayout.UpdateCommand(createUiAbsolutePositionedComponents(), animationDuration, animationEasing.toUiAnimationEasing()));
	}

	private List<DtoAbsolutePositionedComponent> createUiAbsolutePositionedComponents() {
		return positionsByComponent.entrySet().stream()
				.map(entry -> {
					org.teamapps.ux.component.Component component = entry.getKey();
					AbsolutePosition position = entry.getValue();
					return new DtoAbsolutePositionedComponent(component.createDtoReference(), new DtoAbsolutePositioning(
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
	public DtoAbsoluteLayout createDto() {
		DtoAbsoluteLayout uiAbsoluteLayout = new DtoAbsoluteLayout();
		mapAbstractUiComponentProperties(uiAbsoluteLayout);
		uiAbsoluteLayout.setComponents(createUiAbsolutePositionedComponents());
		return uiAbsoluteLayout;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
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
