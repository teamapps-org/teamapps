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

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(BlogViewLibrary.class)
public class BlogView extends AbstractComponent implements DtoBlogViewEventHandler{

	private final DtoBlogViewClientObjectChannel clientObjectChannel = new DtoBlogViewClientObjectChannel(getClientObjectChannel());

	private final List<AbstractBlock> blocks = new ArrayList<>();

	@Override
	public DtoBlogView createDto() {
		DtoBlogView uiPageView = new DtoBlogView();
		mapAbstractConfigProperties(uiPageView);
		uiPageView.setBlocks(blocks.stream()
				.map(block -> block.createDtoBlock())
				.collect(Collectors.toList()));
		return uiPageView;
	}

	public void addBlock(AbstractBlock block) {
		blocks.add(block);
		clientObjectChannel.addBlock(block.createDtoBlock(), false, null);
	}

	public void removeBlock(AbstractBlock block) {
		blocks.add(block);
		clientObjectChannel.removeBlock(block.getClientId());
	}

}
