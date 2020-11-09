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
package org.teamapps.ux.application;

import org.teamapps.icons.Icon;

public interface ApplicationGroup {

	String getId();

	Icon getIcon();

	String getCaption();

	static ApplicationGroup create(String id, Icon icon, String caption) {
		return new ApplicationGroup() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public Icon getIcon() {
				return icon;
			}

			@Override
			public String getCaption() {
				return caption;
			}
		};
	}
}
