/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.itemview;

import org.teamapps.dto.UiIdentifiableClientRecord;

import java.util.function.Consumer;

public interface ItemGroupContainer<HEADERRECORD, RECORD> {

	UiIdentifiableClientRecord createHeaderClientRecord(HEADERRECORD headerRecord);

	void handleAddItem(UiIdentifiableClientRecord clientRecord, Consumer<Void> uiCommandCallback);
	void handleRemoveItem(int itemClientRecordId, Consumer<Void> uiCommandCallback);
	void handleRefreshRequired();
}
