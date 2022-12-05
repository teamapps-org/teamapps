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
package org.teamapps.ux.component.field.datetime;

import org.teamapps.dto.DtoField;
import org.teamapps.dto.DtoLocalTimeField;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

import java.time.LocalTime;
import java.util.List;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class LocalTimeField extends AbstractTimeField<LocalTime> {

	public LocalTimeField() {
		super();
	}

	@Override
	public DtoField createDto() {
		DtoLocalTimeField uiTimeField = new DtoLocalTimeField();
		mapAbstractTimeFieldUiValues(uiTimeField);
		return uiTimeField;
	}

	@Override
	public LocalTime convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else {
			List<Integer> values = (List<Integer>) value;
			return LocalTime.of(values.get(0), values.get(1), values.get(2), values.get(3));
		}
	}
}
