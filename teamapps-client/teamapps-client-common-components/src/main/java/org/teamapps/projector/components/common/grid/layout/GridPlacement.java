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
package org.teamapps.projector.components.common.grid.layout;

import org.teamapps.projector.components.common.dto.DtoGridPlacement;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.format.HorizontalElementAlignment;
import org.teamapps.ux.format.VerticalElementAlignment;

import java.util.List;

public interface GridPlacement {

	int getRow();

	int getColumn();

	int getRowSpan();

	int getColSpan();

	int getMinWidth();

	int getMaxWidth();

	int getMinHeight();

	int getMaxHeight();

	VerticalElementAlignment getVerticalAlignment();

	HorizontalElementAlignment getHorizontalAlignment();

	DtoGridPlacement createUiGridPlacement();

	List<Component> getComponents();

}
