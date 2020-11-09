/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.panel;

import org.teamapps.icons.Icon;
import org.teamapps.ux.component.field.AbstractField;

public class HeaderField {

	private final AbstractField<?> field;
	private final Icon icon;
	private final int minWidth;
	private final int maxWidth;

	public HeaderField(AbstractField<?> field, Icon icon) {
		this(field, icon, 50, 200);
	}

	public HeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		this.field = field;
		this.icon = icon;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
	}

	public AbstractField<?> getField() {
		return field;
	}

	public Icon getIcon() {
		return icon;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public int getMaxWidth() {
		return maxWidth;
	}


}
