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
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.model.ComboBoxModel;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractComboBox<RECORD, VALUE> extends AbstractField<VALUE> implements TextInputHandlingField {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComboBox.class);

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	protected final ClientRecordCache<RECORD, UiComboBoxTreeRecord> recordCache;

	private ComboBoxModel<RECORD> model;
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
	private String emptyText;

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
		this.recordCache = new ClientRecordCache<>(this::createUiTreeRecordWithoutParentRelation, this::addParentLinkToUiRecord);
	}

	protected AbstractComboBox() {
		this(query -> Collections.emptyList());
	}

	protected void mapCommonUiComboBoxProperties(UiComboBox ui) {
		mapAbstractFieldAttributesToUiField(ui);

		// Note: it is important that the uiTemplates get set after the uiRecords are created, because custom templates (templateDecider) may lead to additional template registrations.
		ui.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));

		ui.setShowDropDownButton(dropDownButtonVisible);
		ui.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
		ui.setHighlightFirstResultEntry(highlightFirstResultEntry);
		ui.setShowHighlighting(showHighlighting);
		ui.setAutoComplete(autoComplete);
		ui.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
		ui.setAllowAnyText(allowFreeText);
		ui.setShowClearButton(showClearButton);
		ui.setAnimate(animate);
		ui.setShowExpanders(showExpanders);
		ui.setPlaceholderText(emptyText);
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
		}
	}

	@Override
	public CompletableFuture<?> handleUiQuery(UiQuery query) {
		switch (query.getUiQueryType()) {
			case UI_COMBO_BOX_RETRIEVE_DROPDOWN_ENTRIES: {
				final UiComboBox.RetrieveDropdownEntriesQuery q = (UiComboBox.RetrieveDropdownEntriesQuery) query;
				String string = q.getQueryString() != null ? q.getQueryString() : ""; // prevent NPEs in combobox model implementations
				if (model != null) {
					List<RECORD> resultRecords = model.getRecords(string);
					if (distinctModelResultFiltering) {
						resultRecords = filterOutSelected(resultRecords);
					}
					CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.replaceRecords(resultRecords);
					cacheResponse.commit();
					return CompletableFuture.completedFuture(cacheResponse.getAndClearResult());
				} else {
					return CompletableFuture.completedFuture(List.of());
				}
			}
			case UI_COMBO_BOX_LAZY_CHILDREN: {
				UiComboBox.LazyChildrenQuery lazyChildrenQuery = (UiComboBox.LazyChildrenQuery) query;
				RECORD parentRecord = recordCache.getRecordByClientId(lazyChildrenQuery.getParentId());
				if (parentRecord != null) {
					if (model != null) {
						List<RECORD> childRecords = model.getChildRecords(parentRecord);
						if (distinctModelResultFiltering) {
							childRecords = filterOutSelected(childRecords);
						}
						CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.addRecords(childRecords);
						cacheResponse.commit();
						return CompletableFuture.completedFuture(cacheResponse.getAndClearResult());
					}
				} else {
					return CompletableFuture.completedFuture(Collections.emptyList());
				}
			}
			default:
				return CompletableFuture.failedFuture(new IllegalArgumentException("unknown query"));
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

	protected UiComboBoxTreeRecord createUiTreeRecordWithoutParentRelation(RECORD record) {
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

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		reRenderIfRendered();
	}

	public boolean isDistinctModelResultFiltering() {
		return distinctModelResultFiltering;
	}

	public void setDistinctModelResultFiltering(boolean distinctModelResultFiltering) {
		this.distinctModelResultFiltering = distinctModelResultFiltering;
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
