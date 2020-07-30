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
package org.teamapps.ux.component.form;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.BeanPropertyInjector;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyInjector;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.ux.component.field.AbstractField;

import java.util.HashMap;
import java.util.Map;

public class LogicalForm<RECORD> {

	private final Map<String, AbstractField<?>> fieldsByPropertyName = new HashMap<>();
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();
	private PropertyInjector<RECORD> propertyInjector = new BeanPropertyInjector<>();

	public LogicalForm() {
	}

	public LogicalForm(PropertyProvider<RECORD> propertyProvider, PropertyInjector<RECORD> propertyInjector) {
		this.propertyProvider = propertyProvider;
		this.propertyInjector = propertyInjector;
	}

	public LogicalForm(PropertyExtractor<RECORD> propertyExtractor, PropertyInjector<RECORD> propertyInjector) {
		this((PropertyProvider<RECORD>) propertyExtractor, propertyInjector);
	}

	public LogicalForm(Map<String, AbstractField<?>> fieldsByPropertyName) {
		this.fieldsByPropertyName.putAll(fieldsByPropertyName);
	}

	public LogicalForm<RECORD> addField(String propertyName, AbstractField<?> field) {
		this.fieldsByPropertyName.put(propertyName, field);
		return this;
	}

	public LogicalForm<RECORD> removeField(AbstractField<?> field) {
		this.fieldsByPropertyName.remove(field);
		return this;
	}

	public void applyRecordValuesToFields(RECORD record) {
		Map<String, Object> values = propertyProvider.getValues(record, fieldsByPropertyName.keySet());
		values.forEach((dataKey, value) -> ((AbstractField) fieldsByPropertyName.get(dataKey)).setValue(value));
	}

	public void applyFieldValuesToRecord(RECORD record) {
		Map<String, Object> fieldValues = fieldsByPropertyName.entrySet().stream()
				.collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().getValue()), Map::putAll);
		propertyInjector.setValues(record, fieldValues);
	}

	public Map<String, AbstractField<?>> getFields() {
		return fieldsByPropertyName;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
	}

	public PropertyInjector<RECORD> getPropertyInjector() {
		return propertyInjector;
	}

	public void setPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		this.propertyInjector = propertyInjector;
	}
}
