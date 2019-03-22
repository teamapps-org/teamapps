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
package org.teamapps.data.value.filter;

import org.teamapps.data.value.DataRecord;

import static org.teamapps.data.value.filter.FilterType.ALL_MATCHING;

public class AllMatchingFilter implements Filter {

	@Override
	public FilterType getType() {
		return ALL_MATCHING;
	}

	@Override
	public String getProperty() {
		return null;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		return true;
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "ALL-MATCHING\n";
	}

	@Override
	public Filter and(Filter filter) {
		return filter == null ? this : filter;
	}
}
