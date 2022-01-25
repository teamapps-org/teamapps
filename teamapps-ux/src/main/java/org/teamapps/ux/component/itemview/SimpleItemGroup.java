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
package org.teamapps.ux.component.itemview;

import org.teamapps.icons.Icon;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

public class SimpleItemGroup<PAYLOAD> extends ItemGroup<BaseTemplateRecord, SimpleItem<PAYLOAD>> {

	public static SimpleItemGroup singleColumnGroup(Icon icon, String title) {
		SimpleItemGroup group = new SimpleItemGroup<>(icon, title);
		group.setButtonWidth(0);
		return group;
	}

	public static SimpleItemGroup twoColumnGroup(Icon icon, String title) {
		SimpleItemGroup group = new SimpleItemGroup<>(icon, title);
		group.setButtonWidth(0.5f);
		return group;
	}

	public SimpleItemGroup(Icon icon, String title) {
		this(icon, title, BaseTemplate.MENU_ITEM);
	}

	public SimpleItemGroup(Icon icon, String title, Template template) {
		super(new BaseTemplateRecord(icon, title), template);
	}


	public SimpleItem<PAYLOAD> addItem(Icon icon, String title, String caption) {
		SimpleItem<PAYLOAD> item = new SimpleItem<>(icon, title, caption);
		addItem(item);
		return item;
	}

	public SimpleItem<PAYLOAD> addItem(Icon icon, String title, String caption, PAYLOAD payload) {
		SimpleItem<PAYLOAD> item = new SimpleItem<>(icon, title, caption, payload);
		addItem(item);
		return item;
	}

}
