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
package org.teamapps.projector.component.core.absolutelayout;

import org.teamapps.projector.animation.AnimationEasing;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class AbsoluteLayout extends AbstractComponent implements DtoAbsoluteLayoutEventHandler {

	private final DtoAbsoluteLayoutClientObjectChannel clientObjectChannel = new DtoAbsoluteLayoutClientObjectChannel(getClientObjectChannel());

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
		clientObjectChannel.update(createDtoAbsolutePositionedComponents(), animationDuration, animationEasing);
	}

	private List<DtoAbsolutePositionedComponent> createDtoAbsolutePositionedComponents() {
		return positionsByComponent.entrySet().stream()
				.map(entry -> {
					Component component = entry.getKey();
					AbsolutePosition position = entry.getValue();
					DtoAbsolutePositioning dtoPos = new DtoAbsolutePositioning();
					dtoPos.setTopCss(position.getTop() != null ? position.getTop().toCssString() : null);
					dtoPos.setRightCss (position.getRight() != null ? position.getRight().toCssString() : null);
					dtoPos.setBottomCss (position.getBottom() != null ? position.getBottom().toCssString() : null);
					dtoPos.setLeftCss (position.getLeft() != null ? position.getLeft().toCssString() : null);
					dtoPos.setWidthCss (position.getWidth() != null ? position.getWidth().toCssString() : null);
					dtoPos.setHeightCss (position.getHeight() != null ? position.getHeight().toCssString() : null);
					dtoPos.setZIndex (position.getZIndex());
					DtoAbsolutePositionedComponent ui = new DtoAbsolutePositionedComponent();
					ui.setComponent(component);
					ui.setPosition(dtoPos);
					return ui;
				})
				.collect(Collectors.toList());
	}

	@Override
	public DtoAbsoluteLayout createDto() {
		DtoAbsoluteLayout uiAbsoluteLayout = new DtoAbsoluteLayout();
		mapAbstractConfigProperties(uiAbsoluteLayout);
		uiAbsoluteLayout.setComponents(createDtoAbsolutePositionedComponents());
		return uiAbsoluteLayout;
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
