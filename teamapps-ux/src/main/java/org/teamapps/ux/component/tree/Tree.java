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
package org.teamapps.ux.component.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiComboBoxTreeRecord;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTree;
import org.teamapps.dto.UiTreeRecord;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.TextMatchingMode;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.node.TreeNode;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.model.TreeModel;
import org.teamapps.ux.model.TreeModelChangedEventData;

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
	public final Event<String> onTextInput = new Event<>();

	private TreeModel<RECORD> model;
	private final ClientRecordCache<RECORD, UiTreeRecord> recordCache = new ClientRecordCache<>(this::createUiTreeRecordWithoutParentRelation, this::addParentLinkToUiRecord);
	private TextMatchingMode textMatchingMode = TextMatchingMode.CONTAINS;
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();
	private RECORD selectedNode;

	private Template entryTemplate = null; // null: use toString()
	private TemplateDecider<RECORD> templateDecider = record -> entryTemplate;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;

	private int indentation = 15;
	private boolean animate = true;
	private boolean showExpanders = true;
	private boolean openOnSelection = false;
	private boolean enforceSingleExpandedPath = false;

	private Function<RECORD, String> recordToStringFunction = Object::toString;
	private TreeNodeInfoExtractor<RECORD> treeNodeInfoExtractor = record -> {
		if (record instanceof TreeNode) {
			return (TreeNode) record;
		} else {
			return null;
		}
	};

	private String lastQuery;

	private final Runnable modelAllNodesChangedListener = () -> {
		if (isRendered()) {
			CacheManipulationHandle<List<UiTreeRecord>> cacheResponse = recordCache.replaceRecords(model.getRecords(lastQuery));
			getSessionContext().queueCommand(new UiTree.ReplaceDataCommand(getId(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
		}
	};
	private final Consumer<TreeModelChangedEventData<RECORD>> modelChangedListener = (changedEventData) -> {
		List<RECORD> nodesRemoved = changedEventData.getNodesRemoved();
		List<RECORD> nodesAdded = changedEventData.getNodesAddedOrUpdated();
		if (isRendered()) {
			CacheManipulationHandle<List<Integer>> cacheRemoveResponse = recordCache.removeRecords(nodesRemoved);
			CacheManipulationHandle<List<UiTreeRecord>> cacheAddResponse = recordCache.addRecords(nodesAdded);
			getSessionContext().queueCommand(new UiTree.BulkUpdateCommand(getId(), cacheRemoveResponse.getAndClearResult(), cacheAddResponse.getAndClearResult()), aVoid -> {
				cacheRemoveResponse.commit();
				cacheAddResponse.commit();
			});
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

	protected UiTreeRecord createUiTreeRecordWithoutParentRelation(RECORD record) {
		if (record == null) {
			return null;
		}
		// do not look for objects inside the cache here. they are sent to the client anyway. Also, values like expanded would have to be updated in any case.

		Template template = getTemplateForRecord(record);
		List<String> dataKeys = template != null ? template.getDataKeys() : Collections.emptyList();
		Map<String, Object> values = propertyExtractor.getValues(record, dataKeys);
		UiComboBoxTreeRecord uiTreeRecord = new UiComboBoxTreeRecord();
		uiTreeRecord.setValues(values);
		uiTreeRecord.setDisplayTemplateId(templateIdsByTemplate.get(template));
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = treeNodeInfoExtractor.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
		}

		return uiTreeRecord;
	}

	protected void addParentLinkToUiRecord(RECORD record, UiTreeRecord uiTreeRecord, Map<RECORD, UiTreeRecord> othersCurrentlyBeingAddedToCache) {
		TreeNodeInfo treeNodeInfo = treeNodeInfoExtractor.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			RECORD parent = (RECORD) treeNodeInfo.getParent();
			if (parent != null) {
				UiTreeRecord uiParentFromOthers = othersCurrentlyBeingAddedToCache.get(parent);
				if (uiParentFromOthers != null) {
					uiTreeRecord.setParentId(uiParentFromOthers.getId());
				} else {
					Integer cachedParentUiRecordId = recordCache.getUiRecordIdOrNull(parent);
					if (cachedParentUiRecordId != null) {
						uiTreeRecord.setParentId(cachedParentUiRecordId);
					}
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
		List<RECORD> records = model.getRecords(lastQuery);
		if (records != null) {
			CacheManipulationHandle<List<UiTreeRecord>> cacheResponse = recordCache.replaceRecords(records);
			cacheResponse.commit();
			uiTree.setInitialData(cacheResponse.getAndClearResult());
		}

		uiTree.setSelectedNodeId(this.recordCache.getUiRecordIdOrNull(this.selectedNode));

		// Note: it is important that the uiTemplates get set after the uiRecords are created, because custom templates (templateDecider) may lead to additional template registrations.
		uiTree.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));

		uiTree.setDefaultTemplateId(templateIdsByTemplate.get(entryTemplate));
		uiTree.setAnimate(animate);
		uiTree.setShowExpanders(showExpanders);
		uiTree.setOpenOnSelection(openOnSelection);
		uiTree.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
		uiTree.setTextMatchingMode(textMatchingMode.toUiTextMatchingMode());
		uiTree.setIndentation(indentation);
		return uiTree;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TREE_NODE_SELECTED:
				UiTree.NodeSelectedEvent nodeSelectedEvent = (UiTree.NodeSelectedEvent) event;
				RECORD record = recordCache.getRecordByClientId(nodeSelectedEvent.getNodeId());
				selectedNode = record;
				onNodeSelected.fire(record);
				break;
			case UI_TREE_REQUEST_TREE_DATA:
				UiTree.RequestTreeDataEvent requestTreeDataEvent = (UiTree.RequestTreeDataEvent) event;
				RECORD parentNode = recordCache.getRecordByClientId(requestTreeDataEvent.getParentNodeId());
				if (parentNode != null) {
					List<RECORD> children = model.getChildRecords(parentNode);
					CacheManipulationHandle<List<UiTreeRecord>> cacheResponse = recordCache.addRecords(children);
					if (isRendered()) {
						getSessionContext().queueCommand(new UiTree.BulkUpdateCommand(getId(), Collections.emptyList(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
					} else {
						cacheResponse.commit();
					}
				}
				break;
			case UI_TREE_TEXT_INPUT:
				UiTree.TextInputEvent textInputEvent = (UiTree.TextInputEvent) event;
				if (model != null) {
					lastQuery = textInputEvent.getText();
					List<RECORD> records = model.getRecords(lastQuery);
					CacheManipulationHandle<List<UiTreeRecord>> cacheResponse = recordCache.replaceRecords(records);
					if (isRendered()) {
						getSessionContext().queueCommand(new UiTree.ReplaceDataCommand(getId(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
					} else {
						cacheResponse.commit();
					}
				}
				this.onTextInput.fire(textInputEvent.getText());

		}
	}

	public RECORD getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(RECORD selectedNode) {
		Integer uiRecord = recordCache.getUiRecordIdOrNull(selectedNode);
		this.selectedNode = selectedNode;
		queueCommandIfRendered(() -> new UiTree.SetSelectedNodeCommand(getId(), uiRecord));
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
		if (isRendered()) {
			lastQuery = null;
			List<RECORD> records = model.getRecords(lastQuery);
			CacheManipulationHandle<List<UiTreeRecord>> cacheResponse = recordCache.replaceRecords(records);
			getSessionContext().queueCommand(new UiTree.ReplaceDataCommand(getId(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
		}
	}

	public boolean isAnimate() {
		return animate;
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

	public boolean isOpenOnSelection() {
		return openOnSelection;
	}

	public void setOpenOnSelection(boolean openOnSelection) {
		this.openOnSelection = openOnSelection;
		reRenderIfRendered();
	}

	public boolean isEnforceSingleExpandedPath() {
		return enforceSingleExpandedPath;
	}

	public void setEnforceSingleExpandedPath(boolean enforceSingleExpandedPath) {
		this.enforceSingleExpandedPath = enforceSingleExpandedPath;
		reRenderIfRendered();
	}

	public TextMatchingMode getTextMatchingMode() {
		return textMatchingMode;
	}

	public void setTextMatchingMode(TextMatchingMode textMatchingMode) {
		this.textMatchingMode = textMatchingMode;
		reRenderIfRendered();
	}

	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		this.indentation = indentation;
		reRenderIfRendered();
	}

	public PropertyExtractor<RECORD> getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
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

	public TreeNodeInfoExtractor<RECORD> getTreeNodeInfoExtractor() {
		return treeNodeInfoExtractor;
	}

	public void setTreeNodeInfoExtractor(TreeNodeInfoExtractor<RECORD> treeNodeInfoExtractor) {
		this.treeNodeInfoExtractor = treeNodeInfoExtractor;
	}
}
