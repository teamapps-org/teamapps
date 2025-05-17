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
package org.teamapps.projector.component.chart.common;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.chart.ChartLibrary;
import org.teamapps.projector.component.chart.tree.DtoTreeGraphNodeIcon;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.session.CurrentSessionContext;

@ClientObjectLibrary(ChartLibrary.class)
public class GraphNodeIcon {

	private final Icon icon;
	private final int size;

	public GraphNodeIcon(Icon icon, int size) {
		this.icon = icon;
		this.size = size;
	}

	public DtoTreeGraphNodeIcon createDtoTreeGraphNodeIcon() {
		return new DtoTreeGraphNodeIcon(CurrentSessionContext.get().resolveIcon(icon), size);
	}

	public Icon getIcon() {
		return icon;
	}

	public int getSize() {
		return size;
	}
}
