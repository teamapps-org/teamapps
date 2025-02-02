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
package org.teamapps.projector.component.core.flexcontainer;

import org.teamapps.projector.annotation.ClientObjectTypeName;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.format.FlexDirection;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
@ClientObjectTypeName("FlexContainer")
public class VerticalLayout extends FlexContainer {

	public VerticalLayout() {
		setFlexDirection(FlexDirection.COLUMN);
	}

	public void addComponentFillRemaining(Component component) {
		addComponent(component, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1));
	}

	public void addComponentAutoSize(Component component) {
		addComponent(component, new FlexSizingPolicy(0, 0));
	}

}
