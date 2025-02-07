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
package org.teamapps.projector.icon.composite;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.IconStyle;
import org.teamapps.projector.icon.spi.annotation.IconLibrary;

@IconLibrary(CompositeIconLibrary.class)
public class CompositeIcon implements Icon {

	private final Icon baseIcon;
	private	final Icon bottomRightIcon;
	private	final Icon bottomLeftIcon;
	private	final Icon topLeftIcon;
	private	final Icon topRightIcon;

	public static CompositeIcon of(Icon baseIcon, Icon bottomRightIcon) {
		return new CompositeIcon(baseIcon, bottomRightIcon, null, null, null);
	}

	public static CompositeIcon of(Icon baseIcon, Icon bottomRightIcon, Icon bottomLeftIcon, Icon topLeftIcon, Icon topRightIcon) {
		return new CompositeIcon(baseIcon, bottomRightIcon, bottomLeftIcon, topLeftIcon, topRightIcon);
	}

	private CompositeIcon(Icon baseIcon, Icon bottomRightIcon, Icon bottomLeftIcon, Icon topLeftIcon, Icon topRightIcon) {
		this.baseIcon = baseIcon;
		this.bottomRightIcon = bottomRightIcon;
		this.bottomLeftIcon = bottomLeftIcon;
		this.topLeftIcon = topLeftIcon;
		this.topRightIcon = topRightIcon;
	}

	public Icon getBaseIcon() {
		return baseIcon;
	}

	public Icon getBottomRightIcon() {
		return bottomRightIcon;
	}

	public Icon getBottomLeftIcon() {
		return bottomLeftIcon;
	}

	public Icon getTopLeftIcon() {
		return topLeftIcon;
	}

	public Icon getTopRightIcon() {
		return topRightIcon;
	}

	@Override
	public IconStyle<CompositeIcon> getStyle() {
		return null;
	}

	@Override
	public Icon unstyled() {
		return this;
	}
}
