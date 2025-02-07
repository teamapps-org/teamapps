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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.clientrecordcache.CacheManipulationHandle;
import org.teamapps.projector.clientrecordcache.ClientRecordCache;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.treecomponents.tree.model.ComboBoxModel;
import org.teamapps.projector.component.treecomponents.tree.model.TreeNodeInfo;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.TemplateDecider;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractComboBox<RECORD, VALUE> extends AbstractField<VALUE> implements DtoComboBoxEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DtoComboBoxClientObjectChannel clientObjectChannel = new DtoComboBoxClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);

	protected final ClientRecordCache<RECORD, DtoComboBoxTreeRecord> recordCache;

	private ComboBoxModel<RECORD> model;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	private Template selectedEntryTemplate = null; // null: use recordToStringFunction;
	private Template dropDownTemplate = null; // null: use recordToStringFunction;
	private TemplateDecider<RECORD> selectedEntryTemplateDecider = record -> selectedEntryTemplate;
	private TemplateDecider<RECORD> dropdownTemplateDecider = record -> dropDownTemplate;

	private boolean dropDownButtonVisible = true;
	private boolean showDropDownAfterResultsArrive = false;
	private boolean firstEntryAutoHighlight = true;
	private boolean autoCompletionEnabled = true; // if true, by typing any letter, the first matching will be selected (keeping all not yet entered letters int the text box selected)
	private boolean textHighlightingEnabled = false;
	private int textHighlightingEntryLimit = 100;
	private boolean freeTextEnabled;
	private boolean clearButtonEnabled;
	private boolean expandAnimationEnabled = true;
	private boolean expandersVisible = false;
	private String placeholderText;
	private Integer dropDownMinWidth;
	private Integer dropDownMaxHeight;

	/**
	 * If true, already selected entries will be filtered out from model query results.
	 *
	 * @apiNote While this is handy when dealing with list-style models (no hierarchy), it might cause unintended behavior with tree-style
	 * models. This option will not pay any attention to hierarchical structures.
	 * If the parent <code>P</code> of a child node <code>C</code> is filtered out due to this option,
	 * then <code>C</code> will appear as a root node on the client side.
	 */
	private boolean distinctModelResultFiltering = false;

	private Function<RECORD, String> recordToStringFunction = Object::toString;
	protected Function<String, RECORD> freeTextRecordFactory = null;

	protected AbstractComboBox(ComboBoxModel<RECORD> model) {
		this.model = model;
		this.recordCache = new ClientRecordCache<>(this::createDtoTreeRecordWithoutParentRelation, this::addParentLinkToUiRecord);
	}

	protected AbstractComboBox() {
		this(query -> Collections.emptyList());
	}

	protected void mapCommonUiComboBoxProperties(DtoComboBox ui) {
		mapAbstractFieldAttributesToUiField(ui);
		ui.setDropDownButtonVisible(dropDownButtonVisible);
		ui.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
		ui.setFirstEntryAutoHighlight(firstEntryAutoHighlight);
		ui.setAutoCompletionEnabled(autoCompletionEnabled);
		ui.setTextHighlightingEntryLimit(textHighlightingEnabled ? textHighlightingEntryLimit : 0);
		ui.setFreeTextEnabled(freeTextEnabled);
		ui.setClearButtonEnabled(clearButtonEnabled);
		ui.setExpandAnimationEnabled(expandAnimationEnabled);
		ui.setExpandersVisible(expandersVisible);
		ui.setPlaceholderText(placeholderText);
		ui.setDropDownMinWidth(dropDownMinWidth);
		ui.setDropDownMaxHeight(dropDownMaxHeight);
	}

	@Override
	public void handleTextInput(String enteredString) {
		this.onTextInput.fire(enteredString);
	}

	@Override
	public List<DtoComboBoxTreeRecord> handleLazyChildren(int parentId) {
		RECORD parentRecord = recordCache.getRecordByClientId(parentId);
		if (parentRecord != null) {
			if (model != null) {
				List<RECORD> childRecords = model.getChildRecords(parentRecord);
				if (distinctModelResultFiltering) {
					childRecords = filterOutSelected(childRecords);
				}
				CacheManipulationHandle<List<DtoComboBoxTreeRecord>> cacheResponse = recordCache.addRecords(childRecords);
				cacheResponse.commit();
				return cacheResponse.getAndClearResult();
			}
		}
		// else
		return Collections.emptyList();
	}

	@Override
	public List<DtoComboBoxTreeRecord> handleRetrieveDropdownEntries(String queryString) {
		String string = queryString != null ? queryString : "";
		if (model != null) {
			List<RECORD> resultRecords = model.getRecords(string);
			if (distinctModelResultFiltering) {
				resultRecords = filterOutSelected(resultRecords);
			}
			CacheManipulationHandle<List<DtoComboBoxTreeRecord>> cacheResponse = recordCache.replaceRecords(resultRecords);
			cacheResponse.commit();
			return cacheResponse.getAndClearResult();
		} else {
			return List.of();
		}
	}

	private List<RECORD> filterOutSelected(List<RECORD> resultRecords) {
		Set<RECORD> selectedRecords = getSelectedRecords();
		resultRecords = resultRecords.stream()
				.filter(r -> !selectedRecords.contains(r))
				.collect(Collectors.toList());
		return resultRecords;
	}

	protected abstract Set<RECORD> getSelectedRecords();

	protected DtoComboBoxTreeRecord createDtoTreeRecordWithoutParentRelation(RECORD record) {
		if (record == null) {
			return null;
		}
		// do not look for objects inside the cache here. they are sent to the client anyway. Also, values like expanded would have to be updated in any case.

		Template displayTemplate = getTemplateForRecord(record, selectedEntryTemplateDecider, selectedEntryTemplate);
		Template dropdownTemplate = getTemplateForRecord(record, dropdownTemplateDecider, dropDownTemplate);

		HashSet<String> templatePropertyNames = new HashSet<>();
		templatePropertyNames.addAll(displayTemplate != null ? displayTemplate.getPropertyNames() : Collections.emptySet());
		templatePropertyNames.addAll(dropdownTemplate != null ? dropdownTemplate.getPropertyNames() : Collections.emptySet());
		Map<String, Object> values = propertyProvider.getValues(record, templatePropertyNames);
		DtoComboBoxTreeRecord uiTreeRecord = new DtoComboBoxTreeRecord();
		uiTreeRecord.setValues(values);

		uiTreeRecord.setDisplayTemplate(displayTemplate != null ? displayTemplate : null);
		uiTreeRecord.setDropDownTemplate(dropdownTemplate != null ? dropdownTemplate : null);
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
			uiTreeRecord.setSelectable(treeNodeInfo.isSelectable());
		}

		return uiTreeRecord;
	}

	protected void addParentLinkToUiRecord(RECORD record, DtoComboBoxTreeRecord uiTreeRecord, Map<RECORD, DtoComboBoxTreeRecord> othersCurrentlyBeingAddedToCache) {
		TreeNodeInfo<RECORD> treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			RECORD parent = treeNodeInfo.getParent();
			if (parent != null) {
				DtoComboBoxTreeRecord uiParentFromOthers = othersCurrentlyBeingAddedToCache.get(parent);
				if (uiParentFromOthers != null) {
					uiTreeRecord.setParentId(uiParentFromOthers.getId());
				} else {
					Integer cachedParentUiRecordId = recordCache.getUiRecordIdOrNull(parent); // selectedRecordCache data is not hierarchical, so this is ok.
					if (cachedParentUiRecordId != null) {
						uiTreeRecord.setParentId(cachedParentUiRecordId);
					}
				}
			}
		}
	}

	private Template getTemplateForRecord(RECORD record, TemplateDecider<RECORD> templateDecider, Template defaultTemplate) {
		Template templateFromDecider = templateDecider.getTemplate(record);
		return templateFromDecider != null ? templateFromDecider : defaultTemplate;
	}

	protected boolean isFreeTextEntry(DtoComboBoxTreeRecord uiTreeRecord) {
		return uiTreeRecord.getId() < 0;
	}

	public boolean isExpandAnimationEnabled() {
		return expandAnimationEnabled;
	}

	public ComboBoxModel<RECORD> getModel() {
		return model;
	}

	public void setModel(ComboBoxModel<RECORD> model) {
		this.model = model;
	}

	public boolean isDropDownButtonVisible() {
		return dropDownButtonVisible;
	}

	public boolean isShowDropDownAfterResultsArrive() {
		return showDropDownAfterResultsArrive;
	}

	public boolean isFirstEntryAutoHighlight() {
		return firstEntryAutoHighlight;
	}

	public boolean isAutoCompletionEnabled() {
		return autoCompletionEnabled;
	}

	public boolean isTextHighlightingEnabled() {
		return textHighlightingEnabled;
	}

	public int getTextHighlightingEntryLimit() {
		return textHighlightingEntryLimit;
	}

	public boolean isFreeTextEnabled() {
		return freeTextEnabled;
	}

	public boolean isClearButtonEnabled() {
		return clearButtonEnabled;
	}

	public void setDropDownButtonVisible(boolean dropDownButtonVisible) {
		this.dropDownButtonVisible = dropDownButtonVisible;
		clientObjectChannel.setDropDownButtonVisible(dropDownButtonVisible);
	}

	public void setShowDropDownAfterResultsArrive(boolean showDropDownAfterResultsArrive) {
		this.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
		clientObjectChannel.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
	}

	public void setFirstEntryAutoHighlight(boolean firstEntryAutoHighlight) {
		this.firstEntryAutoHighlight = firstEntryAutoHighlight;
		clientObjectChannel.setFirstEntryAutoHighlight(firstEntryAutoHighlight);
	}

	public void setAutoCompletionEnabled(boolean autoCompletionEnabled) {
		this.autoCompletionEnabled = autoCompletionEnabled;
		clientObjectChannel.setAutoCompletionEnabled(autoCompletionEnabled);
	}

	public void setTextHighlightingEnabled(boolean textHighlightingEnabled) {
		this.textHighlightingEnabled = textHighlightingEnabled;
		clientObjectChannel.setTextHighlightingEntryLimit(textHighlightingEnabled ? textHighlightingEntryLimit : 0);
	}

	public void setTextHighlightingEntryLimit(int textHighlightingEntryLimit) {
		this.textHighlightingEntryLimit = textHighlightingEntryLimit;
		clientObjectChannel.setTextHighlightingEntryLimit(textHighlightingEnabled ? textHighlightingEntryLimit : 0);
	}

	public void setFreeTextEnabled(boolean freeTextEnabled) {
		this.freeTextEnabled = freeTextEnabled;
		clientObjectChannel.setFreeTextEnabled(freeTextEnabled);
	}

	public void setClearButtonEnabled(boolean clearButtonEnabled) {
		this.clearButtonEnabled = clearButtonEnabled;
		clientObjectChannel.setClearButtonEnabled(clearButtonEnabled);
	}

	public void setExpandAnimationEnabled(boolean expandAnimationEnabled) {
		this.expandAnimationEnabled = expandAnimationEnabled;
		clientObjectChannel.setExpandAnimationEnabled(expandAnimationEnabled);
	}

	public boolean isExpandersVisible() {
		return expandersVisible;
	}

	public void setExpandersVisible(boolean expandersVisible) {
		this.expandersVisible = expandersVisible;
		clientObjectChannel.setExpandersVisible(expandersVisible);
	}

	public void setSelectedEntryTemplate(Template selectedEntryTemplate) {
		this.selectedEntryTemplate = selectedEntryTemplate;
	}

	public void setDropDownTemplate(Template dropDownTemplate) {
		this.dropDownTemplate = dropDownTemplate;
	}

	public void setTemplate(Template template) {
		setSelectedEntryTemplate(template);
		setDropDownTemplate(template);
	}

	public Template getSelectedEntryTemplate() {
		return selectedEntryTemplate;
	}

	public Template getDropDownTemplate() {
		return dropDownTemplate;
	}

	public TemplateDecider<RECORD> getSelectedEntryTemplateDecider() {
		return selectedEntryTemplateDecider;
	}

	public void setSelectedEntryTemplateDecider(TemplateDecider<RECORD> selectedEntryTemplateDecider) {
		this.selectedEntryTemplateDecider = selectedEntryTemplateDecider;
	}

	public TemplateDecider<RECORD> getDropdownTemplateDecider() {
		return dropdownTemplateDecider;
	}

	public void setDropdownTemplateDecider(TemplateDecider<RECORD> dropdownTemplateDecider) {
		this.dropdownTemplateDecider = dropdownTemplateDecider;
	}

	public void setTemplateDecider(TemplateDecider<RECORD> templateDecider) {
		this.selectedEntryTemplateDecider = templateDecider;
		this.dropdownTemplateDecider = templateDecider;
	}

	public Function<String, RECORD> getFreeTextRecordFactory() {
		return freeTextRecordFactory;
	}

	public void setFreeTextRecordFactory(Function<String, RECORD> freeTextRecordFactory) {
		this.freeTextRecordFactory = freeTextRecordFactory;
	}

	public Function<RECORD, String> getRecordToStringFunction() {
		return recordToStringFunction;
	}

	public void setRecordToStringFunction(Function<RECORD, String> recordToStringFunction) {
		this.recordToStringFunction = recordToStringFunction;
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

	public String getPlaceholderText() {
		return placeholderText;
	}

	public void setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
		clientObjectChannel.setPlaceholderText(placeholderText);
	}

	public boolean isDistinctModelResultFiltering() {
		return distinctModelResultFiltering;
	}

	public void setDistinctModelResultFiltering(boolean distinctModelResultFiltering) {
		this.distinctModelResultFiltering = distinctModelResultFiltering;
	}

	public Integer getDropDownMinWidth() {
		return dropDownMinWidth;
	}

	public void setDropDownMinWidth(Integer dropDownMinWidth) {
		this.dropDownMinWidth = dropDownMinWidth;
		clientObjectChannel.setDropDownMinWidth(dropDownMinWidth);
	}

	public Integer getDropDownMaxHeight() {
		return dropDownMaxHeight;
	}

	public void setDropDownMaxHeight(Integer dropDownMaxHeight) {
		this.dropDownMaxHeight = dropDownMaxHeight;
		clientObjectChannel.setDropDownMaxHeight(dropDownMaxHeight);
	}

}
