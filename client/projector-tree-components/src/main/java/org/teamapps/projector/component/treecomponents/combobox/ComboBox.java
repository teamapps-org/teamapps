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
package org.teamapps.projector.component.treecomponents.combobox;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientrecordcache.CacheManipulationHandle;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.component.treecomponents.TreeComponentsLibrary;
import org.teamapps.projector.component.treecomponents.tree.model.ComboBoxModel;
import org.teamapps.projector.component.treecomponents.tree.model.TreeNodeInfo;
import org.teamapps.projector.component.treecomponents.tree.model.TreeNodeInfoExtractor;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = TreeComponentsLibrary.class)
public class ComboBox<RECORD> extends AbstractComboBox<RECORD, RECORD> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DtoComboBoxClientObjectChannel clientObjectChannel = new DtoComboBoxClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onFreeTextEntered = new ProjectorEvent<>();

	private String freeTextEntry;

	private final List<ToolButton> toolButtons = new ArrayList<>();

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
	public DtoAbstractField createDto() {
		DtoComboBox comboBox = new DtoComboBox();
		mapCommonUiComboBoxProperties(comboBox);
		comboBox.setToolButtons(List.copyOf(this.toolButtons));
		return comboBox;
	}

	@Override
	public void setValue(RECORD record) {
		super.setValue(record);
		this.freeTextEntry = null;
	}

	@Override
	public RECORD doConvertClientValueToServerValue(JsonNode jsonNode) {
		this.freeTextEntry = null;

		if (jsonNode.isNull()) {
			return null;
		} else if (jsonNode.isNumber()) {
			this.freeTextEntry = null;
			// do not change the cache here ;-) No need.
			return recordCache.getRecordByClientId(jsonNode.numberValue().intValue());
		} else if (jsonNode.isTextual()) {
			String freeText = jsonNode.textValue();
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
			throw new IllegalArgumentException("Unknown ui value type: " + jsonNode);
		}
	}

	@Override
	public Object convertServerValueToClientValue(RECORD record) {
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

	public void addToolButton(ToolButton toolButton) {
		toolButtons.add(toolButton);
		updateToolButtons();
	}

	public void removeToolButton(ToolButton toolButton) {
		toolButtons.remove(toolButton);
		updateToolButtons();
	}

	private void updateToolButtons() {
		clientObjectChannel.setToolButtons(List.copyOf(this.toolButtons));
	}
}
