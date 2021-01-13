/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.teamapps.ux.component.template.BaseTemplateRecord;

public class SimpleItemView<PAYLOAD> extends ItemView<BaseTemplateRecord, SimpleItem<PAYLOAD>> {


	public SimpleItemView() {
		onItemClicked.addListener(simpleItemItemClickedEventData -> {
			SimpleItem<PAYLOAD> item = simpleItemItemClickedEventData.getItem();
			item.onClick.fire(null);
		});
		setVerticalPadding(0);
		setHorizontalPadding(2);
		setGroupSpacing(2);
	}

	public SimpleItemGroup<PAYLOAD> addSingleColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = SimpleItemGroup.singleColumnGroup(icon, title);
		addGroup(group);
		return group;
	}

	public SimpleItemGroup<PAYLOAD> addTwoColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = SimpleItemGroup.twoColumnGroup(icon, title);
		addGroup(group);
		return group;
	}

}
