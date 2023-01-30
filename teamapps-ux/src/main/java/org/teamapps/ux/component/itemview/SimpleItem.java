/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.template.BaseTemplateRecord;

public class SimpleItem<PAYLOAD> extends BaseTemplateRecord<PAYLOAD>{

	public Event<Void> onClick = new Event<>();


	public SimpleItem() {
	}

	public SimpleItem(String caption) {
		this(null, null, caption, null, (String) null);
	}

	public SimpleItem(Icon icon) {
		this(icon, null);
	}

	public SimpleItem(String caption, PAYLOAD payload) {
		this(null, null, caption, null, null, payload);
	}

	public SimpleItem(Icon icon, String caption) {
		this(icon, null, caption, null, null);
	}

	public SimpleItem(Icon icon, String caption, PAYLOAD payload) {
		this(icon, null, caption, null, null, payload);
	}

	public SimpleItem(Icon icon, String caption, String description) {
		this(icon, null, caption, description, null);
	}

	public SimpleItem(Icon icon, String caption, String description, PAYLOAD payload) {
		this(icon, null, caption, description, null, payload);
	}

	public SimpleItem(Icon icon, String caption, String description, String badge) {
		this(icon, null, caption, description, badge);
	}

	public SimpleItem(String image, String caption) {
		this(null, image, caption, null, (String) null);
	}

	public SimpleItem(String image, String caption, PAYLOAD payload) {
		this(null, image, caption, null, payload);
	}

	public SimpleItem(String image, String caption, String description) {
		this(null, image, caption, description, (String) null);
	}

	public SimpleItem(String image, String caption, String description, PAYLOAD payload) {
		this(null, image, caption, description, payload);
	}

	public SimpleItem(String image, String caption, String description, String badge) {
		this(null, image, caption, description, badge);
	}

	public SimpleItem(String image, String caption, String description, String badge, PAYLOAD payload) {
		this(null, image, caption, description, null, payload);
	}

	public SimpleItem(Icon icon, String image, String caption, String description, String badge) {
		this(icon, image, caption, description, badge, null);
	}

	public SimpleItem(Icon icon, String image, String caption, String description, String badge, PAYLOAD payload) {
		super(icon, image, caption, description, badge, payload);
	}


}
