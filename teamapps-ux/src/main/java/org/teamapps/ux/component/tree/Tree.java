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
package org.teamapps.ux.component.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiComboBoxTreeRecord;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTree;
import org.teamapps.dto.UiTreeRecord;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.model.TreeModel;
import org.teamapps.ux.model.TreeModelChangedEventData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Tree<RECORD> extends AbstractComponent {

	private final Logger LOGGER = LoggerFactory.getLogger(Tree.class);

	public final Event<RECORD> onNodeSelected = new Event<>();
	public final Event<TreeNodeExpansionEvent<RECORD>> onNodeExpansionChanged = new Event<>();
	public final Event<String> onTextInput = new Event<>();

	private TreeModel<RECORD> model;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();
	private RECORD selectedNode;

	private Template entryTemplate = null; // null: use toString()
	private TemplateDecider<RECORD> templateDecider = record -> entryTemplate;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;

	private int indentation = 15;
	private boolean animated = true;
	private boolean showExpanders = true;
	private boolean openOnSelection = false;
	private boolean enforceSingleExpandedPath = false;
	private Function<RECORD, String> recordToStringFunction = Object::toString;

	private int clientRecordIdCounter = 0;
	private final Map<RECORD, UiTreeRecord> uiRecordsByRecord = new HashMap<>();

	private final Runnable modelAllNodesChangedListener = () -> {
		if (isRendered()) {
			uiRecordsByRecord.clear();
			List<UiTreeRecord> uiRecords = createOrUpdateUiRecords(model.getRecords());
			getSessionContext().queueCommand(new UiTree.ReplaceDataCommand(getId(), uiRecords));
		}
	};

	private final Consumer<TreeModelChangedEventData<RECORD>> modelChangedListener = (changedEventData) -> {
		if (isRendered()) {
			List<Integer> removedUiIds = changedEventData.getRemovedNodes().stream()
					.map(key -> uiRecordsByRecord.remove(key).getId())
					.collect(Collectors.toList());
			List<UiTreeRecord> addedOrUpdatedUiTreeRecords = createOrUpdateUiRecords(changedEventData.getAddedOrUpdatedNodes());
			getSessionContext().queueCommand(new UiTree.BulkUpdateCommand(getId(), removedUiIds, addedOrUpdatedUiTreeRecords));
		}
	};

	public Tree(TreeModel<RECORD> model) {
		super();
		this.model = model;
		registerModelListeners();
	}

	private void registerModelListeners() {
		model.onAllNodesChanged().addListener(modelAllNodesChangedListener);
		model.onChanged().addListener(modelChangedListener);
	}

	private void unregisterMutableTreeModelListeners() {
		model.onAllNodesChanged().removeListener(modelAllNodesChangedListener);
		model.onChanged().removeListener(modelChangedListener);
	}

	protected List<UiTreeRecord> createOrUpdateUiRecords(List<RECORD> records) {
		if (records == null) {
			return Collections.emptyList();
		}
		ArrayList<UiTreeRecord> uiRecords = new ArrayList<>();
		for (RECORD record : records) {
			UiTreeRecord uiRecord = createUiTreeRecordWithoutParentRelation(record);
			uiRecordsByRecord.put(record, uiRecord);
			uiRecords.add(uiRecord);
		}
		for (RECORD record : records) {
			addParentLinkToUiRecord(record, uiRecordsByRecord.get(record));
		}
		return uiRecords;
	}

	protected UiTreeRecord createUiTreeRecordWithoutParentRelation(RECORD record) {
		if (record == null) {
			return null;
		}
		Template template = getTemplateForRecord(record);
		List<String> propertyNames = template != null ? template.getPropertyNames() : Collections.emptyList();
		Map<String, Object> values = propertyProvider.getValues(record, propertyNames);

		UiTreeRecord uiTreeRecord;
		if (uiRecordsByRecord.containsKey(record)) {
			uiTreeRecord = uiRecordsByRecord.get(record);
		} else {
			uiTreeRecord = new UiComboBoxTreeRecord();
			uiTreeRecord.setId(++clientRecordIdCounter);
		}
		uiTreeRecord.setValues(values);
		uiTreeRecord.setDisplayTemplateId(templateIdsByTemplate.get(template));
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
			uiTreeRecord.setSelectable(treeNodeInfo.isSelectable());
		}

		return uiTreeRecord;
	}

	protected void addParentLinkToUiRecord(RECORD record, UiTreeRecord uiTreeRecord) {
		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			RECORD parent = (RECORD) treeNodeInfo.getParent();
			if (parent != null) {
				UiTreeRecord uiParent = uiRecordsByRecord.get(parent);
				if (uiParent != null) {
					uiTreeRecord.setParentId(uiParent.getId());
				}
			}
		}
	}

	private Template getTemplateForRecord(RECORD record) {
		Template templateFromDecider = templateDecider.getTemplate(record);
		Template template = templateFromDecider != null ? templateFromDecider : entryTemplate;
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			queueCommandIfRendered(() -> new UiTree.RegisterTemplateCommand(getId(), uuid, template.createUiTemplate()));
		}
		return template;
	}

	@Override
	public UiComponent createUiComponent() {
		UiTree uiTree = new UiTree();
		mapAbstractUiComponentProperties(uiTree);
		List<RECORD> records = model.getRecords();
		if (records != null) {
			uiTree.setInitialData(createOrUpdateUiRecords(records));
		}

		if (this.selectedNode != null) {
			uiTree.setSelectedNodeId(uiRecordsByRecord.get(this.selectedNode).getId());
		}

		// Note: it is important that the uiTemplates get set after the uiRecords are created, because custom templates (templateDecider) may lead to additional template registrations.
		uiTree.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));

		uiTree.setDefaultTemplateId(templateIdsByTemplate.get(entryTemplate));
		uiTree.setAnimate(animated);
		uiTree.setShowExpanders(showExpanders);
		uiTree.setOpenOnSelection(openOnSelection);
		uiTree.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
		uiTree.setIndentation(indentation);
		return uiTree;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TREE_NODE_SELECTED: {
				UiTree.NodeSelectedEvent nodeSelectedEvent = (UiTree.NodeSelectedEvent) event;
				RECORD record = getRecordByUiId(nodeSelectedEvent.getNodeId());
				selectedNode = record;
				if (record != null) {
					onNodeSelected.fire(record);
				}
				break;
			}
			case UI_TREE_NODE_EXPANSION_CHANGED: {
				UiTree.NodeExpansionChangedEvent e = (UiTree.NodeExpansionChangedEvent) event;
				RECORD record = getRecordByUiId(e.getNodeId());
				selectedNode = record;
				if (record != null) {
					onNodeExpansionChanged.fire(new TreeNodeExpansionEvent<>(record, e.getExpanded()));
				}
				break;
			}
			case UI_TREE_REQUEST_TREE_DATA: {
				UiTree.RequestTreeDataEvent requestTreeDataEvent = (UiTree.RequestTreeDataEvent) event;
				RECORD parentNode = getRecordByUiId(requestTreeDataEvent.getParentNodeId());
				if (parentNode != null) {
					List<RECORD> children = model.getChildRecords(parentNode);
					List<UiTreeRecord> uiChildren = createOrUpdateUiRecords(children);
					if (isRendered()) {
						getSessionContext().queueCommand(new UiTree.BulkUpdateCommand(getId(), Collections.emptyList(), uiChildren));
					}
				}
				break;
			}
		}
	}

	public RECORD getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(RECORD selectedNode) {
		int uiRecordId = uiRecordsByRecord.get(selectedNode) != null ? uiRecordsByRecord.get(selectedNode).getId() : -1;
		this.selectedNode = selectedNode;
		queueCommandIfRendered(() -> new UiTree.SetSelectedNodeCommand(getId(), uiRecordId));
	}

	public TreeModel<RECORD> getModel() {
		return model;
	}

	public void setModel(TreeModel<RECORD> model) {
		this.unregisterMutableTreeModelListeners();
		this.model = model;
		this.registerModelListeners();
		this.refresh();
	}

	private void refresh() {
		modelAllNodesChangedListener.run();
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		boolean changed = animated != this.animated;
		this.animated = animated;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public boolean isShowExpanders() {
		return showExpanders;
	}

	public void setShowExpanders(boolean showExpanders) {
		boolean changed = showExpanders != this.showExpanders;
		this.showExpanders = showExpanders;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public boolean isOpenOnSelection() {
		return openOnSelection;
	}

	public void setOpenOnSelection(boolean openOnSelection) {
		boolean changed = openOnSelection != this.openOnSelection;
		this.openOnSelection = openOnSelection;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public boolean isEnforceSingleExpandedPath() {
		return enforceSingleExpandedPath;
	}

	public void setEnforceSingleExpandedPath(boolean enforceSingleExpandedPath) {
		boolean changed = enforceSingleExpandedPath != this.enforceSingleExpandedPath;
		this.enforceSingleExpandedPath = enforceSingleExpandedPath;
		if (changed) {
			reRenderIfRendered();
		}
	}

	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		boolean changed = indentation != this.indentation;
		this.indentation = indentation;
		if (changed) {
			reRenderIfRendered();
		}
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

	public Template getEntryTemplate() {
		return entryTemplate;
	}

	public void setEntryTemplate(Template entryTemplate) {
		this.entryTemplate = entryTemplate;
		reRenderIfRendered();
	}

	public TemplateDecider<RECORD> getTemplateDecider() {
		return templateDecider;
	}

	public void setTemplateDecider(TemplateDecider<RECORD> templateDecider) {
		this.templateDecider = templateDecider;
		reRenderIfRendered();
	}

	public Function<RECORD, String> getRecordToStringFunction() {
		return recordToStringFunction;
	}

	public void setRecordToStringFunction(Function<RECORD, String> recordToStringFunction) {
		this.recordToStringFunction = recordToStringFunction;
		reRenderIfRendered();
	}

	private RECORD getRecordByUiId(int uiRecordId) {
		// no fast implementation needed! only called on user click
		return uiRecordsByRecord.keySet().stream()
				.filter(rr -> uiRecordsByRecord.get(rr).getId() == uiRecordId)
				.findFirst().orElse(null);
	}

}
