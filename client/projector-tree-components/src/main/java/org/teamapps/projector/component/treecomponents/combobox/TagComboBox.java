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
public class TagComboBox<RECORD> extends AbstractComboBox<RECORD, List<RECORD>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DtoTagComboBoxClientObjectChannel clientObjectChannel = new DtoTagComboBoxClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onFreeTextEntered = new ProjectorEvent<>();
	public final ProjectorEvent<String> onFreeTextRemoved = new ProjectorEvent<>();

	private int maxEntries; // if 0, then the list is unbounded
	private TagComboBoxWrappingMode wrappingMode = TagComboBoxWrappingMode.MULTI_LINE;
	private boolean distinct = false; // if true, do not allow the same entry to be selected multiple times!
	private boolean twoStepDeletionEnabled = false; // This will cause tags to not directly be deleted when pressing the backspace or delete key, but first marked for deletion.
	private boolean deleteButtonsEnabled = false;

	private List<String> freeTextEntries = new ArrayList<>();

	public TagComboBox() {
		init();
	}

	public TagComboBox(Template template) {
		setTemplate(template);
		init();
	}

	public TagComboBox(ComboBoxModel<RECORD> model) {
		super(model);
		init();
	}

	@Override
	protected Set<RECORD> getSelectedRecords() {
		List<RECORD> value = getValue();
		return value != null ? Set.copyOf(value) : Set.of();
	}

	private void init() {
		recordCache.setPurgeDecider((record, clientId) -> !(getValue() != null && getValue().contains(record)));
	}

	public static <R> TagComboBox<R> createForList(List<R> staticData) {
		return createForList(staticData, (Template) null);
	}

	public static <R> TagComboBox<R> createForList(List<R> staticData, Template template) {
		TagComboBox<R> comboBox = new TagComboBox<>(template);
		comboBox.setModel(query -> staticData.stream()
				.filter(record -> comboBox.getRecordToStringFunction().apply(record).toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList()));
		return comboBox;
	}

	public static <R> TagComboBox<R> createForList(List<R> staticData, TreeNodeInfoExtractor<R> treeNodeInfoExtractor) {
		TagComboBox<R> comboBox = new TagComboBox<>();
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

	public static <ENUM extends Enum> TagComboBox<ENUM> createForEnum(Class<ENUM> enumClass) {
		TagComboBox<ENUM> tagComboBox = createForList(Arrays.asList(enumClass.getEnumConstants()));
		tagComboBox.setClearButtonEnabled(true);
		return tagComboBox;
	}

	@Override
	public DtoAbstractField createDto() {
		DtoTagComboBox comboBox = new DtoTagComboBox();
		mapCommonUiComboBoxProperties(comboBox);
		comboBox.setMaxEntries(maxEntries);
		comboBox.setWrappingMode(this.wrappingMode);
		comboBox.setDistinct(distinct);
		comboBox.setTwoStepDeletionEnabled(twoStepDeletionEnabled);
		comboBox.setDeleteButtonsEnabled(deleteButtonsEnabled);
		return comboBox;
	}

	@Override
	public void setValue(List<RECORD> records) {
		super.setValue(records);
		this.freeTextEntries.clear();
	}

	@Override
	public List<RECORD> doConvertClientValueToServerValue(JsonNode node) {
		if (node != null && !node.isArray()) {
			throw new IllegalArgumentException("Invalid TagComboBox value coming from ui: " + node);
		}

		Iterable<JsonNode> values;
		if (node == null || node.isNull()) {
			values = Collections.emptyList();
		} else {
			values = node;
		}

		List<RECORD> records = new ArrayList<>();
		List<String> uiFreeTextEntries = new ArrayList<>();
		for (JsonNode entry : values) {
			if (entry.isNumber()) {
				RECORD recordFromSelectedRecordCache = recordCache.getRecordByClientId(entry.intValue());
				if (recordFromSelectedRecordCache == null) {
					LOGGER.error("Could not find record in client record cache: " + entry);
				} else {
					records.add(recordFromSelectedRecordCache);
				}
			} else {
				uiFreeTextEntries.add(entry.textValue());
			}
		}

		List<String> newFreeTextEntries = uiFreeTextEntries.stream().filter(uiFreeTextEntry -> !this.freeTextEntries.contains(uiFreeTextEntry)).collect(Collectors.toList());
		List<String> removedFreeTextEntries = this.freeTextEntries.stream().filter(existingFreeTextEntry -> !uiFreeTextEntries.contains(existingFreeTextEntry)).collect(Collectors.toList());

		if (!newFreeTextEntries.isEmpty()) {
			newFreeTextEntries.forEach(newFreeText -> {
				if (freeTextRecordFactory != null) {
					RECORD record = freeTextRecordFactory.apply(newFreeText);
					CacheManipulationHandle<DtoComboBoxTreeRecord> cacheResponse = recordCache.addRecord(record);
					records.add(record);

					boolean sent = clientObjectChannel.replaceFreeTextEntry(newFreeText, cacheResponse.getAndClearResult(), unused -> cacheResponse.commit());
					if (!sent) {
						cacheResponse.commit();
					}
					
					uiFreeTextEntries.remove(newFreeText);
				} else {
					this.onFreeTextEntered.fire(newFreeText);
				}
			});
			this.freeTextEntries = uiFreeTextEntries;
		}
		if (removedFreeTextEntries.size() > 0) {
			removedFreeTextEntries.forEach(removedEntryText -> {
				this.onFreeTextRemoved.fire(removedFreeTextEntries.get(0));
			});
			this.freeTextEntries = uiFreeTextEntries;
		}

		recordCache.replaceRecords(records).commit();

		return records;
	}

	@Override
	protected boolean isEmptyValue(List<RECORD> records) {
		return records == null || records.isEmpty();
	}

	@Override
	public Object convertServerValueToClientValue(List<RECORD> uxValue) {
		if (uxValue == null) {
			return null;
		}
		CacheManipulationHandle<List<DtoComboBoxTreeRecord>> cacheResponse = recordCache.addRecords(uxValue);
		cacheResponse.commit();
		return cacheResponse.getAndClearResult();
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
		clientObjectChannel.setMaxEntries(maxEntries);
	}

	public TagComboBoxWrappingMode getWrappingMode() {
		return wrappingMode;
	}

	public void setWrappingMode(TagComboBoxWrappingMode wrappingMode) {
		this.wrappingMode = wrappingMode;
		clientObjectChannel.setWrappingMode(wrappingMode);
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
		clientObjectChannel.setDistinct(distinct);
	}

	public List<String> getFreeTextEntries() {
		return freeTextEntries;
	}

	public void setFreeTextEntries(List<String> freeTextEntries) {
		this.freeTextEntries = freeTextEntries;
	}

	public boolean isTwoStepDeletionEnabled() {
		return twoStepDeletionEnabled;
	}

	public void setTwoStepDeletionEnabled(boolean twoStepDeletionEnabled) {
		this.twoStepDeletionEnabled = twoStepDeletionEnabled;
		clientObjectChannel.setTwoStepDeletionEnabled(twoStepDeletionEnabled);
	}

	public boolean isDeleteButtonsEnabled() {
		return deleteButtonsEnabled;
	}

	public void setDeleteButtonsEnabled(boolean deleteButtonsEnabled) {
		this.deleteButtonsEnabled = deleteButtonsEnabled;
		clientObjectChannel.setDeleteButtonsEnabled(deleteButtonsEnabled);
	}
}
