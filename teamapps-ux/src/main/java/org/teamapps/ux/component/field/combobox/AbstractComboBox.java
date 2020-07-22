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
package org.teamapps.ux.component.field.combobox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiComboBox;
import org.teamapps.dto.UiComboBoxTreeRecord;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTextInputHandlingField;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractComboBox<COMPONENT extends AbstractComboBox, RECORD, VALUE> extends AbstractField<VALUE> implements TextInputHandlingField {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComboBox.class);

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	protected final ClientRecordCache<RECORD, UiComboBoxTreeRecord> recordCache;

	private ComboBoxModel<RECORD> model;
	private List<RECORD> staticData = new ArrayList<>(); //if available, use this as data source
	private TextMatchingMode textMatchingMode = TextMatchingMode.CONTAINS; // only filter on client-side if staticData != null. SIMILARITY_MATCH allows levenshtein distance of 3
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();

	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;
	private Template selectedEntryTemplate = null; // null: use toString();
	private Template dropDownTemplate = null; // null: use toString();
	private TemplateDecider<RECORD> selectedEntryTemplateDecider = record -> selectedEntryTemplate;
	private TemplateDecider<RECORD> dropdownTemplateDecider = record -> dropDownTemplate;

	private boolean showDropDownButton = true;
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
	
	private AbstractComboBox(List<RECORD> staticData, ComboBoxModel<RECORD> model) {
		super();
		this.model = model;
		if (staticData != null) {
			this.staticData = new ArrayList<>(staticData);
		}
		this.recordCache = new ClientRecordCache<>(this::createUiTreeRecordWithoutParentRelation, this::addParentLinkToUiRecord);
	}

	protected AbstractComboBox(ComboBoxModel<RECORD> model) {
		this(null, model);
	}

	public AbstractComboBox(List<RECORD> staticData) {
		this(staticData, null);
	}

	protected void mapCommonUiComboBoxProperties(UiComboBox comboBox) {
		mapAbstractFieldAttributesToUiField(comboBox);
		if (this.staticData != null) {
			CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.replaceRecords(this.staticData);
			cacheResponse.commit();
			comboBox.setStaticData(cacheResponse.getAndClearResult());
		}

		// Note: it is important that the uiTemplates get set after the uiRecords are created, because custom templates (templateDecider) may lead to additional template registrations.
		comboBox.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));
		
		comboBox.setTextMatchingMode(textMatchingMode.toUiTextMatchingMode());
		comboBox.setShowDropDownButton(showDropDownButton);
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
				if (model != null) {
					List<RECORD> records = model.getRecords(keyStrokeEvent.getEnteredString());
					CacheManipulationHandle<List<UiComboBoxTreeRecord>> cacheResponse = recordCache.replaceRecords(records);
					if (isRendered()) {
						getSessionContext().queueCommand(new UiComboBox.SetDropDownDataCommand(getId(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
					} else {
						cacheResponse.commit();
					}
				}
				this.onTextInput.fire(keyStrokeEvent.getEnteredString());
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
		Map<String, Object> values = propertyExtractor.getValues(record, templateDataKeys);
		UiComboBoxTreeRecord uiTreeRecord = new UiComboBoxTreeRecord();
		uiTreeRecord.setValues(values);

		uiTreeRecord.setDisplayTemplateId(templateIdsByTemplate.get(displayTemplate));
		uiTreeRecord.setDropDownTemplateId(templateIdsByTemplate.get(dropdownTemplate));
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
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

	public List<RECORD> getStaticData() {
		return staticData;
	}

	public void setStaticData(List<RECORD> staticData) {
		this.staticData = staticData;
		this.model = null;
		reRenderIfRendered();
	}

	public void addDropDownItem(RECORD record) {
		if (staticData == null) {
			staticData = new ArrayList<>();
		}
		this.staticData.add(record);
		reRenderIfRendered();
	}

	public void addDropDownItems(RECORD... records) {
		if (staticData == null) {
			staticData = new ArrayList<>();
		}
		this.staticData.addAll(Arrays.asList(records));
		reRenderIfRendered();
	}

	public void addDropDownItems(List<RECORD> records) {
		if (staticData == null) {
			staticData = new ArrayList<>();
		}
		this.staticData.addAll(records);
		reRenderIfRendered();
	}

	public ComboBoxModel getModel() {
		return model;
	}

	public void setModel(ComboBoxModel<RECORD> model) {
		this.model = model;
		this.staticData = null;
		reRenderIfRendered();
	}

	public TextMatchingMode getTextMatchingMode() {
		return textMatchingMode;
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
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

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
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
		this.templateIdsByTemplate.put(selectedEntryTemplate, "" + templateIdCounter++);
		reRenderIfRendered();
	}

	public void setDropDownTemplate(Template dropDownTemplate) {
		this.dropDownTemplate = dropDownTemplate;
		this.templateIdsByTemplate.put(dropDownTemplate, "" + templateIdCounter++);
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

	public PropertyExtractor<RECORD> getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
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
