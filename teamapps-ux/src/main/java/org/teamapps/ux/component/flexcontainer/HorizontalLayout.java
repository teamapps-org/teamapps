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
package org.teamapps.ux.component.flexcontainer;

import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.css.CssFlexDirection;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class HorizontalLayout extends FlexContainer {

	public HorizontalLayout() {
		setFlexDirection(CssFlexDirection.ROW);
	}

	public void addComponentFillRemaining(Component component) {
		addComponent(component, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1));
	}

	public void addComponentAutoSize(Component component) {
		addComponent(component, new FlexSizingPolicy( 0, 0));
	}


}
