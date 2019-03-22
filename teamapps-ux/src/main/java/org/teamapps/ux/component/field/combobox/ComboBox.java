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
package org.teamapps.ux.component.field.combobox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiComboBox;
import org.teamapps.dto.UiField;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.model.BaseTreeModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ComboBox<RECORD> extends AbstractComboBox<ComboBox, RECORD, RECORD> implements TextInputHandlingField {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComboBox.class);

	public final Event<String> onFreeTextEntered = new Event<>();
	
	private String freeTextEntry;

	public ComboBox(BaseTreeModel<RECORD> model) {
		super(model);
	}

	public ComboBox(List<RECORD> staticData) {
		super(staticData);
	}

	public ComboBox() {
		super((List<RECORD>) null);
	}

	public ComboBox(Template template) {
		super((List<RECORD>) null);
		setTemplate(template);
	}

	public static <ENUM extends Enum> ComboBox<ENUM> createForEnum(Class<ENUM> enumClass) {
		return new ComboBox<>(Arrays.asList(enumClass.getEnumConstants()));
	}

	@Override
	public UiField createUiComponent() {
		UiComboBox comboBox = new UiComboBox(getId());
		mapCommonUiComboBoxProperties(comboBox);
		return comboBox;
	}

	@Override
	public void setValue(RECORD record) {
		super.setValue(record);
		this.freeTextEntry = null;
	}

	@Override
	public RECORD convertUiValueToUxValue(Object value) {
		this.freeTextEntry = null;
		if (value == null) {
			return null;
		} else if (value instanceof Integer) {
			this.freeTextEntry = null;
			RECORD record = recordCache.getRecordByClientId(((Integer) value));
			// do not change the cache here ;-) No need.
			return record;
		} else if (value instanceof String) {
			String freeText = (String) value;
			if (this.freeTextRecordFactory != null) {
				RECORD record = freeTextRecordFactory.apply(freeText);
				recordCache.replaceRecords(Collections.singletonList(record)).commit();
			} else {
				this.freeTextEntry = freeText;
			}
			onFreeTextEntered.fire(freeText);
			return null;
		} else {
			throw new IllegalArgumentException("Unknown ui value type: " + value);
		}
	}

	@Override
	public Object convertUxValueToUiValue(RECORD record) {
		if (record == null) {
			return null;
		}
		recordCache.addRecord(record).commit(); // directly committing only works because client-side changes are block during server-side changes
		return this.createUiTreeRecordWithoutParentRelation(record);
	}

	public String getFreeText() {
		return freeTextEntry;
	}
	
}
