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
package org.teamapps.projector.component.blogview;

import org.teamapps.projector.component.core.toolbutton.ToolButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractBlock {

	private final String clientId = UUID.randomUUID().toString();
	private BlockAlignment alignment = BlockAlignment.FULL;
	private List<ToolButton> toolButtons = new ArrayList<>();

	public AbstractBlock() {
	}

	public AbstractBlock(BlockAlignment alignment) {
		this.alignment = alignment;
	}

	public abstract DtoBlock createDtoBlock();

	protected void mapAbstractBlockAttributes(DtoBlock uiBlock) {
		uiBlock.setId(clientId);
		uiBlock.setAlignment(alignment);
		uiBlock.setToolButtons(List.copyOf(toolButtons));
	}

	/*package-private*/ String getClientId() {
		return clientId;
	}

	public BlockAlignment getAlignment() {
		return alignment;
	}

	public AbstractBlock setAlignment(BlockAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public AbstractBlock setToolButtons(List<ToolButton> toolButtons) {
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
