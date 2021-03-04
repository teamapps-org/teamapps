/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.component.field.TextMatchingMode;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.model.ComboBoxModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractComboBox<RECORD, VALUE> extends AbstractField<VALUE> implements TextInputHandlingField {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComboBox.class);

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	protected final ClientRecordCache<RECORD, UiComboBoxTreeRecord> recordCache;

	private ComboBoxModel<RECORD> model;
	private TextMatchingMode textMatchingMode = TextMatchingMode.CONTAINS; // only filter on client-side if staticData != null. SIMILARITY_MATCH allows levenshtein distance of 3
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;
	private Template selectedEntryTemplate = null; // null: use recordToStringFunction;
	private Template dropDownTemplate = null; // null: use recordToStringFunction;
	private TemplateDecider<RECORD> selectedEntryTemplateDecider = record -> selectedEntryTemplate;
	private TemplateDecider<RECORD> dropdownTemplateDecider = record -> dropDownTemplate;

	private boolean dropDownButtonVisible = true;
	private boolean showDropDownAfterResultsArrive = false;
	private boolean highlightFirstResultEntry = true;
	private boolean autoComplete = true; // if true, by typing any letter, the first matching will be selected (keeping all not yet entered letters int the text box selected)
	private boolean showHighlighting = false; // TODO highlight any line of the template, but only corresponding to the textMatchingMode
	private int textHighlightingEntryLimit = 100;
	private boolean allowFreeText;
	private boolean showClearButton;
	private boolean animate = true;
	private boolean showExpanders = false;

	private Function<RECORD, String> recordToStringFunction = Object::toString;
	protected Function<String, RECORD> freeTextRecordFactory = null;
	
	protected AbstractComboBox(ComboBoxModel<RECORD> model) {
		this.model = model;
		this.recordCache = new ClientRecordCache<>(this::createUiTreeRecordWithoutParentRelation, this::addParentLinkToUiRecord);
	}

	protected AbstractComboBox() {
		this(query -> Collections.emptyList());
	}

	protected void mapCommonUiComboBoxProperties(UiComboBox comboBox) {
		mapAbstractFieldAttributesToUiField(comboBox);

		// Note: it is important that the uiTemplates get set after the uiRecords are created, because custom templates (templateDecider) may lead to additional template registrations.
		comboBox.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));
		
		comboBox.setTextMatchingMode(textMatchingMode.toUiTextMatchingMode());
		comboBox.setShowDropDownButton(dropDownButtonVisible);
		comboBox.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
		comboBox.setHighlightFirstResultEntry(highlightFirstResultEntry);
		comboBox.setShowHighlighting(showHighlighting);
		comboBox.setAutoComplete(autoComplete);
		comboBox.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
		comboBox.setAllowAnyText(allowFreeText);
		comboBox.setShowClearButton(showClearButton);
		comboBox.setAnimate(animate);
		comboBox.setShowExpanders(showExpanders);
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		switch (event.getUiEventType()) {
			case UI_TEXT_INPUT_HANDLING_FIELD_TEXT_INPUT:
				UiTextInputHandlingField.TextInputEvent keyStrokeEvent = (UiTextInputHandlingField.TextInputEvent) event;
				String string = keyStrokeEvent.getEnteredString() != null ? keyStrokeEvent.getEnteredString() : ""; // prevent NPEs in combobox model implementations
				this.onTextInput.fire(string);
				break;
			case UI_TEXT_INPUT_HANDLING_FIELD_SPECIAL_KEY_PRESSED:
				UiTextInputHandlingField.SpecialKeyPressedEvent specialKeyPressedEvent = (UiTextInputHandlingField.SpecialKeyPressedEvent) event;
				this.onSpecialKeyPressed.fire(SpecialKey.valueOf(specialKeyPressedEvent.getKey().name()));
				break;
			case UI_COMBO_BOX_LAZY_CHILD_DATA_REQUESTED:
				UiComboBox.LazyChildDataRequestedEvent lazyChildDataRequestedEvent = (UiComboBox.LazyChildDataRequestedEvent) event;
				RECORD parentRecord = recordCache.getRecordByClientId(lazyChildDataRequestedEvent.getParentId());
				if (parentRecord != null) {
					if (model != null) {
						List<RECORD> childRecords = model.getChildRecords(parentRecord);
						CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.addRecords(childRecords);
						if (isRendered()) {
							getSessionContext().queueCommand(new UiComboBox.SetChildNodesCommand(getId(), lazyChildDataRequestedEvent.getParentId(), cacheResponse.getAndClearResult()),
									aVoid -> cacheResponse.commit());
						} else {
							cacheResponse.commit();
						}
					}
				}
				break;
		}
	}

	@Override
	public CompletableFuture<?> handleUiQuery(UiQuery query) {
		switch (query.getUiQueryType()) {
			case UI_COMBO_BOX_RETRIEVE_DROPDOWN_ENTRIES: {
				final UiComboBox.RetrieveDropdownEntriesQuery q = (UiComboBox.RetrieveDropdownEntriesQuery) query;
				String string = q.getQueryString() != null ? q.getQueryString() : ""; // prevent NPEs in combobox model implementations
				if (model != null) {
					List<RECORD> records = model.getRecords(string);
					CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.replaceRecords(records);
					cacheResponse.commit();
					return CompletableFuture.completedFuture(cacheResponse.getAndClearResult());
				} else {
					return CompletableFuture.completedFuture(List.of());
				}
			}
			default:
				return CompletableFuture.failedFuture(new IllegalArgumentException("unknown query"));
		}
	}

	protected UiComboBoxTreeRecord createUiTreeRecordWithoutParentRelation(RECORD record) {
		if (record == null) {
			return null;
		}
		// do not look for objects inside the cache here. they are sent to the client anyway. Also, values like expanded would have to be updated in any case.

		Template displayTemplate = getTemplateForRecord(record, selectedEntryTemplateDecider, selectedEntryTemplate);
		Template dropdownTemplate = getTemplateForRecord(record, dropdownTemplateDecider, dropDownTemplate);

		HashSet<String> templateDataKeys = new HashSet<>();
		templateDataKeys.addAll(displayTemplate != null ? displayTemplate.getDataKeys() : Collections.emptySet());
		templateDataKeys.addAll(dropdownTemplate != null ? dropdownTemplate.getDataKeys() : Collections.emptySet());
		Map<String, Object> values = propertyProvider.getValues(record, templateDataKeys);
		UiComboBoxTreeRecord uiTreeRecord = new UiComboBoxTreeRecord();
		uiTreeRecord.setValues(values);

		uiTreeRecord.setDisplayTemplateId(templateIdsByTemplate.get(displayTemplate));
		uiTreeRecord.setDropDownTemplateId(templateIdsByTemplate.get(dropdownTemplate));
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
			uiTreeRecord.setSelectable(treeNodeInfo.isSelectable());
		}

		return uiTreeRecord;
	}

	protected void addParentLinkToUiRecord(RECORD record, UiComboBoxTreeRecord uiTreeRecord, Map<RECORD, UiComboBoxTreeRecord> othersCurrentlyBeingAddedToCache) {
		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			RECORD parent = (RECORD) treeNodeInfo.getParent();
			if (parent != null) {
				UiComboBoxTreeRecord uiParentFromOthers = othersCurrentlyBeingAddedToCache.get(parent);
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
		Template template = templateFromDecider != null ? templateFromDecider : defaultTemplate;
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			queueCommandIfRendered(() -> new UiComboBox.RegisterTemplateCommand(getId(), uuid, template.createUiTemplate()));
		}
		return template;
	}

	protected boolean isFreeTextEntry(UiComboBoxTreeRecord uiTreeRecord) {
		return uiTreeRecord.getId() < 0;
	}

	public boolean isAnimate() {
		return animate;
	}

	public ComboBoxModel<RECORD> getModel() {
		return model;
	}

	public void setModel(ComboBoxModel<RECORD> model) {
		this.model = model;
		reRenderIfRendered();
	}

	public TextMatchingMode getTextMatchingMode() {
		return textMatchingMode;
	}

	public boolean isDropDownButtonVisible() {
		return dropDownButtonVisible;
	}

	public boolean isShowDropDownAfterResultsArrive() {
		return showDropDownAfterResultsArrive;
	}

	public boolean isHighlightFirstResultEntry() {
		return highlightFirstResultEntry;
	}

	public boolean isAutoComplete() {
		return autoComplete;
	}

	public boolean isShowHighlighting() {
		return showHighlighting;
	}

	public int getTextHighlightingEntryLimit() {
		return textHighlightingEntryLimit;
	}

	public boolean isAllowFreeText() {
		return allowFreeText;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setDropDownButtonVisible(boolean dropDownButtonVisible) {
		this.dropDownButtonVisible = dropDownButtonVisible;
		reRenderIfRendered();
	}

	public void setShowDropDownAfterResultsArrive(boolean showDropDownAfterResultsArrive) {
		this.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
		reRenderIfRendered();
	}

	public void setHighlightFirstResultEntry(boolean highlightFirstResultEntry) {
		this.highlightFirstResultEntry = highlightFirstResultEntry;
		reRenderIfRendered();
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
		reRenderIfRendered();
	}

	public void setShowHighlighting(boolean showHighlighting) {
		this.showHighlighting = showHighlighting;
		reRenderIfRendered();
	}

	public void setAllowFreeText(boolean allowFreeText) {
		this.allowFreeText = allowFreeText;
		reRenderIfRendered();
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		reRenderIfRendered();
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
		reRenderIfRendered();
	}

	public boolean isShowExpanders() {
		return showExpanders;
	}

	public void setShowExpanders(boolean showExpanders) {
		this.showExpanders = showExpanders;
		reRenderIfRendered();
	}

	public void setTextMatchingMode(TextMatchingMode textMatchingMode) {
		this.textMatchingMode = textMatchingMode;
		reRenderIfRendered();
	}

	public void setSelectedEntryTemplate(Template selectedEntryTemplate) {
		this.selectedEntryTemplate = selectedEntryTemplate;
		if (selectedEntryTemplate != null) {
			this.templateIdsByTemplate.put(selectedEntryTemplate, "" + templateIdCounter++);
		}
		reRenderIfRendered();
	}

	public void setDropDownTemplate(Template dropDownTemplate) {
		this.dropDownTemplate = dropDownTemplate;
		if (dropDownTemplate != null) {
			this.templateIdsByTemplate.put(dropDownTemplate, "" + templateIdCounter++);
		}
		reRenderIfRendered();
	}

	public void setTemplate(Template template) {
		setSelectedEntryTemplate(template);
		setDropDownTemplate(template);
	}

	public void setTextHighlightingEntryLimit(int textHighlightingEntryLimit) {
		this.textHighlightingEntryLimit = textHighlightingEntryLimit;
		reRenderIfRendered();
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
		reRenderIfRendered();
	}

	public Function<String, RECORD> getFreeTextRecordFactory() {
		return freeTextRecordFactory;
	}

	public void setFreeTextRecordFactory(Function<String, RECORD> freeTextRecordFactory) {
		this.freeTextRecordFactory = freeTextRecordFactory;
		reRenderIfRendered();
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

	@Override
	public Event<String> onTextInput() {
		return this.onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return this.onSpecialKeyPressed;
	}

}
