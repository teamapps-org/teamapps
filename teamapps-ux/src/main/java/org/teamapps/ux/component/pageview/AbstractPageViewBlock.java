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
package org.teamapps.ux.component.pageview;

import org.teamapps.dto.UiPageViewBlock;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractPageViewBlock {

	private final String clientId = UUID.randomUUID().toString();
	private PageViewBlockAlignment alignment = PageViewBlockAlignment.FULL;
	private List<ToolButton> toolButtons = new ArrayList<>();

	public AbstractPageViewBlock() {
	}

	public AbstractPageViewBlock(PageViewBlockAlignment alignment) {
		this.alignment = alignment;
	}

	public abstract UiPageViewBlock createUiBlock();

	protected void mapAbstractPageViewBlockAttributes(UiPageViewBlock uiBlock) {
		uiBlock.setId(clientId);
		uiBlock.setAlignment(alignment.toUiAlignment());
		uiBlock.setToolButtons(toolButtons.stream()
				.map(button -> button.createUiReference())
				.collect(Collectors.toList()));
	}

	/*package-private*/ String getClientId() {
		return clientId;
	}

	public PageViewBlockAlignment getAlignment() {
		return alignment;
	}

	public AbstractPageViewBlock setAlignment(PageViewBlockAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public AbstractPageViewBlock setToolButtons(List<ToolButton> toolButtons) {
		this.toolButtons = toolButtons;
		return this;
	}

	public void addToolButton(ToolButton toolButton) {
		this.toolButtons.add(toolButton);
	}

	public void removeToolButton(ToolButton toolButton) {
		this.toolButtons.remove(toolButton);
	}
}
