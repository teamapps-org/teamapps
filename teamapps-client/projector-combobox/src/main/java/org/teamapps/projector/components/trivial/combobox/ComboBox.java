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
package org.teamapps.projector.components.trivial.combobox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.DtoAbstractField;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoComboBox;
import org.teamapps.projector.components.trivial.dto.DtoComboBoxTreeRecord;
import org.teamapps.projector.components.trivial.tree.model.ComboBoxModel;
import org.teamapps.projector.components.trivial.tree.model.TreeNodeInfo;
import org.teamapps.projector.components.trivial.tree.model.TreeNodeInfoExtractor;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.component.template.Template;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ProjectorComponent(library = TrivialComponentsLibrary.class)
public class ComboBox<RECORD> extends AbstractComboBox<RECORD, RECORD> implements TextInputHandlingField {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<String> onFreeTextEntered = new ProjectorEvent<>();

	private String freeTextEntry;

	public ComboBox() {
	}

	@Override
	protected Set<RECORD> getSelectedRecords() {
		RECORD value = getValue();
		return value != null ? Set.of(value) : Set.of();
	}

	public ComboBox(ComboBoxModel<RECORD> model) {
		super(model);
	}

	public ComboBox(Template template) {
		setTemplate(template);
	}

	public static <R> ComboBox<R> createForList(List<R> staticData) {
		return createForList(staticData, (Template) null);
	}

	public static <R> ComboBox<R> createForList(List<R> staticData, Template template) {
		ComboBox<R> comboBox = new ComboBox<>(template);
		comboBox.setModel(query -> staticData.stream()
				.filter(record -> comboBox.getRecordToStringFunction().apply(record).toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList()));
		return comboBox;
	}

	public static <R> ComboBox<R> createForList(List<R> staticData, TreeNodeInfoExtractor<R> treeNodeInfoExtractor) {
		ComboBox<R> comboBox = new ComboBox<>();
		comboBox.setModel(new ComboBoxModel<>() {
			@Override
			public List<R> getRecords(String query) {
				return staticData.stream()
						.filter(record -> comboBox.getRecordToStringFunction().apply(record).toLowerCase().contains(query.toLowerCase()))
						.collect(Collectors.toList());
			}

			@Override
			public TreeNodeInfo getTreeNodeInfo(R r) {
				return treeNodeInfoExtractor.getTreeNodeInfo(r);
			}
		});
		return comboBox;
	}

	public static <ENUM extends Enum> ComboBox<ENUM> createForEnum(Class<ENUM> enumClass) {
		return ComboBox.createForList(Arrays.asList(enumClass.getEnumConstants()));
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoComboBox comboBox = new DtoComboBox();
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
				return record;
			} else {
				this.freeTextEntry = freeText;
				onFreeTextEntered.fire(freeText);
				return null;
			}
		} else {
			throw new IllegalArgumentException("Unknown ui value type: " + value);
		}
	}

	@Override
	public Object convertUxValueToUiValue(RECORD record) {
		if (record == null) {
			return null;
		}
		CacheManipulationHandle<DtoComboBoxTreeRecord> handle = recordCache.addRecord(record);
		handle.commit(); // directly committing only works because client-side changes are blocked during server-side changes
		return handle.getAndClearResult();
	}

	public String getFreeText() {
		return freeTextEntry;
	}

}
