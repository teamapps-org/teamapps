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

import org.teamapps.projector.component.Component;

public class ComponentBlock extends AbstractBlock {
	private Component component;
	private int height = 400;
	private String title;

	public ComponentBlock(Component component, int height) {
		this.component = component;
		this.height = height;
	}

	public DtoComponentBlock createDtoBlock() {
		DtoComponentBlock uiBlock = new DtoComponentBlock();
		mapAbstractBlockAttributes(uiBlock);
		uiBlock.setComponent(component);
		uiBlock.setHeight(height);
		uiBlock.setTitle(title);
		return uiBlock;
	}

	public Component getComponent() {
		return component;
	}

	public ComponentBlock setComponent(Component component) {
		this.component = component;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public ComponentBlock setHeight(int height) {
		this.height = height;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ComponentBlock setTitle(String title) {
		this.title = title;
		return this;
	}
}
