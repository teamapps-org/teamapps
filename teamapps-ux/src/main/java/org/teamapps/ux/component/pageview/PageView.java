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
package org.teamapps.ux.component.pageview;

import org.teamapps.dto.DtoPageView;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PageView extends AbstractComponent {

	private List<AbstractPageViewBlock> blocks = new ArrayList<>();

	@Override
	public DtoPageView createDto() {
		DtoPageView uiPageView = new DtoPageView();
		mapAbstractUiComponentProperties(uiPageView);
		uiPageView.setBlocks(blocks.stream()
				.map(block -> block.createUiBlock())
				.collect(Collectors.toList()));
		return uiPageView;
	}

	public void addBlock(AbstractPageViewBlock block) {
		blocks.add(block);
		sendCommandIfRendered(() -> new DtoPageView.AddBlockCommand(block.createUiBlock(), false, null));
	}

	public void removeBlock(AbstractPageViewBlock block) {
		blocks.add(block);
		sendCommandIfRendered(() -> new DtoPageView.RemoveBlockCommand(block.getClientId()));
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		// none
	}
}
