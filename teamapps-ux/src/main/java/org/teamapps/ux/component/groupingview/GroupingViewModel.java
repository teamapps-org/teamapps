/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.groupingview;

import java.util.List;

public interface GroupingViewModel {

	int getCount(String groupingFieldName, GroupingType groupingType);

	List<GroupValue> getRecords(String groupingFieldName, GroupingType groupingType, int start, int length, List<String> fieldNames);

	public class GroupValue {
//		private DataRecord dataRecord; TODO use something else than DataRecord...
		private int count;
	}

}
