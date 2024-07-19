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
package org.teamapps.projector.component.common.model;

public class NodeRelocatedEventData<RECORD> {
	private final RECORD record;
	private final RECORD parentRecord;

	public NodeRelocatedEventData(RECORD record, RECORD parentRecord) {
		this.record = record;
		this.parentRecord = parentRecord;
	}

	public RECORD getRecord() {
		return record;
	}

	public RECORD getParentRecord() {
		return parentRecord;
	}

}
