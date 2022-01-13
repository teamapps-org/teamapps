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
package org.teamapps.ux.application.filter;

import org.teamapps.event.Event;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.timegraph.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterProvider<RECORD> {

	public Event<String> onFullTextSearchUpdate = new Event<>();
	public Event<String> onTimeGraphIntervalUpdate = new Event<>();

	private List<TextField> fullTextSearchFields = new ArrayList<>();

	private String fullTextFilter;
	private Interval timeGraphFilter;
	private List<String> timeGraphProperties;

	private Map<String, String> tablePropertyFilter = new HashMap<>();


	public void createSearchHeaderField(View view, boolean leftSide) {
		Panel panel = view.getPanel();
		TextField fullTextSearchField = new TextField();
		fullTextSearchField.setEmptyText("Search..."); //todo: localize
		fullTextSearchField.setShowClearButton(true);
		fullTextSearchField.onTextInput.addListener(query -> handleFullTextSearchFieldUpdate(fullTextSearchField, query));
		if (leftSide) {
			if (panel.getLeftHeaderField() == null) {
				panel.setLeftHeaderField(fullTextSearchField);
			}
		} else {
			if (panel.getRightHeaderField() == null) {
				panel.setRightHeaderField(fullTextSearchField);
			}
		}
	}

	private void handleFullTextSearchFieldUpdate(TextField originatingField, String value) {
		for (TextField searchField : fullTextSearchFields) {
			if (!originatingField.equals(searchField)) {
				searchField.setValue(value);
			}
		}
		fullTextFilter = value;
		onFullTextSearchUpdate.fire(fullTextFilter);
	}

	public String getFullTextFilter() {
		return fullTextFilter;
	}

	public Interval getTimeGraphFilter() {
		return timeGraphFilter;
	}

	public void clearTablePropertyFilters() {
		tablePropertyFilter.clear();
	}

	public void setTablePropertyFilter(String propertyName, String filter) {
		tablePropertyFilter.put(propertyName, filter);
	}
}
