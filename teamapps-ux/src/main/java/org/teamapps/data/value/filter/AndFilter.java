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

import java.util.ArrayList;
import java.util.List;

public class AndFilter implements Filter {

	private List<Filter> filters = new ArrayList<>();

	public AndFilter() {

	}

	public AndFilter(Filter filterA, Filter filterB) {
		filters.add(filterA);
		filters.add(filterB);
	}

	@Override
	public FilterType getType() {
		return FilterType.AND;
	}

	@Override
	public String getProperty() {
		return null;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		for (Filter filter : filters) {
			if (!filter.matches(record, treatNullAsDefaultValue)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String explain(int filterLevel) {
		StringBuilder sb = new StringBuilder();
		sb.append(createLevelIndentString(filterLevel));
		sb.append("AND(").append("\n");
		for (Filter filter : filters) {
			sb.append(filter.explain(filterLevel + 1));
		}
		sb.append(createLevelIndentString(filterLevel));
		sb.append(")").append("\n");
		return sb.toString();
	}

	@Override
	public Filter and(Filter filter) {
		if (filter != null) {
			filters.add(filter);
		}
		return this;
	}
}
