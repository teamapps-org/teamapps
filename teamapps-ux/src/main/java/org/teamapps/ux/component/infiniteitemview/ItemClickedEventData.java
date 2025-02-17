/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.infiniteitemview;

public class ItemClickedEventData<RECORD> {

	private final RECORD record;
	private final boolean isDoubleClick;

	public ItemClickedEventData(RECORD record, boolean isDoubleClick) {
		this.record = record;
		this.isDoubleClick = isDoubleClick;
	}

	public RECORD getRecord() {
		return record;
	}

	public boolean isDoubleClick() {
		return isDoubleClick;
	}

}
