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
package org.teamapps.projector.components.trivial.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.dto.protocol.client.QueryWrapper;
import org.teamapps.event.Disposable;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoComboBoxTreeRecord;
import org.teamapps.projector.components.trivial.dto.DtoTree;
import org.teamapps.projector.components.trivial.dto.DtoTreeRecord;
import org.teamapps.projector.components.trivial.tree.model.TreeModel;
import org.teamapps.projector.components.trivial.tree.model.TreeNodeInfo;
import org.teamapps.projector.clientobject.AbstractComponent;
import org.teamapps.projector.clientobject.ProjectorComponent;
import org.teamapps.projector.template.Template;
import org.teamapps.ux.component.template.TemplateDecider;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.session.SessionContext;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ProjectorComponent(library = TrivialComponentsLibrary.class)
public class Tree<RECORD> extends AbstractComponent {

	private final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<RECORD> onNodeSelected = createProjectorEventBoundToUiEvent(DtoTree.NodeSelectedEvent.TYPE_ID);

	private TreeModel<RECORD> model;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();
	private RECORD selectedNode;

	private Template entryTemplate = null; // null: use toString()
	private TemplateDecider<RECORD> templateDecider = record -> entryTemplate;

	private int indentation = 15;
	private boolean expandAnimationEnabled = true;
	private boolean expandersVisible = true;
	private boolean expandOnSelection = false;
	private boolean enforceSingleExpandedPath = false;
	private Function<RECORD, String> recordToStringFunction = Object::toString;

	private int clientRecordIdCounter = 0;
	private final Map<RECORD, DtoTreeRecord> uiRecordsByRecord = new HashMap<>();

	private Disposable modelAllNodesChangedListener;
	private Disposable modelChangedListener;

	public Tree(TreeModel<RECORD> model) {
		super();
		this.model = model;
		registerModelListeners();
	}

	private void registerModelListeners() {
		modelAllNodesChangedListener = model.onAllNodesChanged().addListener(this::refresh);
		modelChangedListener = model.onChanged().addListener((changedEventData) -> {
			if (isRendered()) {
				List<Integer> removedUiIds = changedEventData.getRemovedNodes().stream()
						.map(key -> uiRecordsByRecord.remove(key).getId())
						.collect(Collectors.toList());
				List<DtoTreeRecord> addedOrUpdatedUiTreeRecords = createOrUpdateUiRecords(changedEventData.getAddedOrUpdatedNodes());
				SessionContext sessionContext = getSessionContext();
				sessionContext.sendCommandIfRendered(this, (DtoCommand<?>) new DtoTree.BulkUpdateCommand(removedUiIds, addedOrUpdatedUiTreeRecords), null);
			}
		});
	}

	private void unregisterMutableTreeModelListeners() {
		if (modelAllNodesChangedListener != null) {
			modelAllNodesChangedListener.dispose();
		}
		if (modelChangedListener != null) {
			modelChangedListener.dispose();
		}
	}

	protected List<DtoTreeRecord> createOrUpdateUiRecords(List<RECORD> records) {
		if (records == null) {
			return Collections.emptyList();
		}
		ArrayList<DtoTreeRecord> uiRecords = new ArrayList<>();
		for (RECORD record : records) {
			DtoTreeRecord uiRecord = createUiTreeRecordWithoutParentRelation(record);
			uiRecordsByRecord.put(record, uiRecord);
			uiRecords.add(uiRecord);
		}
		for (RECORD record : records) {
			addParentLinkToUiRecord(record, uiRecordsByRecord.get(record));
		}
		return uiRecords;
	}

	protected DtoTreeRecord createUiTreeRecordWithoutParentRelation(RECORD record) {
		if (record == null) {
			return null;
		}
		Template template = getTemplateForRecord(record);
		List<String> propertyNames = template != null ? template.getPropertyNames() : Collections.emptyList();
		Map<String, Object> values = propertyProvider.getValues(record, propertyNames);

		DtoTreeRecord uiTreeRecord;
		if (uiRecordsByRecord.containsKey(record)) {
			uiTreeRecord = uiRecordsByRecord.get(record);
		} else {
			uiTreeRecord = new DtoComboBoxTreeRecord();
			uiTreeRecord.setId(++clientRecordIdCounter);
		}
		uiTreeRecord.setValues(values);
		uiTreeRecord.setDisplayTemplate(template != null ? template.createClientReference() : null);
		uiTreeRecord.setAsString(this.recordToStringFunction.apply(record));

		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			uiTreeRecord.setExpanded(treeNodeInfo.isExpanded());
			uiTreeRecord.setLazyChildren(treeNodeInfo.isLazyChildren());
			uiTreeRecord.setSelectable(treeNodeInfo.isSelectable());
		}

		return uiTreeRecord;
	}

	protected void addParentLinkToUiRecord(RECORD record, DtoTreeRecord uiTreeRecord) {
		TreeNodeInfo treeNodeInfo = model.getTreeNodeInfo(record);
		if (treeNodeInfo != null) {
			RECORD parent = (RECORD) treeNodeInfo.getParent();
			if (parent != null) {
				DtoTreeRecord uiParent = uiRecordsByRecord.get(parent);
				if (uiParent != null) {
					uiTreeRecord.setParentId(uiParent.getId());
				}
			}
		}
	}

	private Template getTemplateForRecord(RECORD record) {
		Template templateFromDecider = templateDecider.getTemplate(record);
		Template template = templateFromDecider != null ? templateFromDecider : entryTemplate;
		return template;
	}

	@Override
	public DtoTree createConfig() {
		DtoTree uiTree = new DtoTree();
		mapAbstractUiComponentProperties(uiTree);
		List<RECORD> records = model.getRecords();
		if (records != null) {
			uiTree.setInitialData(createOrUpdateUiRecords(records));
		}

		if (this.selectedNode != null) {
			uiTree.setSelectedNodeId(uiRecordsByRecord.get(this.selectedNode).getId());
		}

		uiTree.setExpandersVisible(expandersVisible);
		uiTree.setExpandAnimationEnabled(expandAnimationEnabled);
		uiTree.setExpandOnSelection(expandOnSelection);
		uiTree.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
		uiTree.setIndentation(indentation);
		return uiTree;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoTree.NodeSelectedEvent.TYPE_ID -> {
				var nodeSelectedEvent = event.as(DtoTree.NodeSelectedEventWrapper.class);
				RECORD record = getRecordByUiId(nodeSelectedEvent.getNodeId());
				selectedNode = record;
				if (record != null) {
					onNodeSelected.fire(record);
				}
			}
		}
	}

	@Override
	public Object handleUiQuery(QueryWrapper query) {
		return switch (query.getTypeId()) {
			case DtoTree.LazyLoadChildrenQuery.TYPE_ID -> {
				var requestTreeDataEvent = query.as(DtoTree.LazyLoadChildrenQueryWrapper.class);
				RECORD parentNode = getRecordByUiId(requestTreeDataEvent.getParentNodeId());
				if (parentNode != null) {
					List<RECORD> children = model.getChildRecords(parentNode);
					yield createOrUpdateUiRecords(children);
				} else {
					yield List.of();
				}
			}
			default -> null;
		};
	}

	public RECORD getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(RECORD selectedNode) {
		setSelectedNode(selectedNode, true);
	}

	public void setSelectedNode(RECORD selectedNode, boolean reveal) {
		int uiRecordId = uiRecordsByRecord.get(selectedNode) != null ? uiRecordsByRecord.get(selectedNode).getId() : -1;
		this.selectedNode = selectedNode;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetSelectedNodeIdCommand(uiRecordId, reveal), null);
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

	public void refresh() {
		if (isRendered()) {
			uiRecordsByRecord.clear();
			List<DtoTreeRecord> uiRecords = createOrUpdateUiRecords(model.getRecords());
			SessionContext sessionContext = getSessionContext();
			sessionContext.sendCommandIfRendered(this, (DtoCommand<?>) new DtoTree.ReplaceNodesCommand(uiRecords), null);
		}
	}

	public boolean isExpandAnimationEnabled() {
		return expandAnimationEnabled;
	}

	public void setExpandAnimationEnabled(boolean expandAnimationEnabled) {
		this.expandAnimationEnabled = expandAnimationEnabled;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetExpandAnimationEnabledCommand(expandAnimationEnabled), null);
	}

	public boolean isExpandersVisible() {
		return expandersVisible;
	}

	public void setExpandersVisible(boolean expandersVisible) {
		this.expandersVisible = expandersVisible;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetExpandersVisibleCommand(expandersVisible), null);
	}

	public boolean isExpandOnSelection() {
		return expandOnSelection;
	}

	public void setExpandOnSelection(boolean expandOnSelection) {
		this.expandOnSelection = expandOnSelection;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetExpandOnSelectionCommand(expandOnSelection), null);
	}

	public boolean isEnforceSingleExpandedPath() {
		return enforceSingleExpandedPath;
	}

	public void setEnforceSingleExpandedPath(boolean enforceSingleExpandedPath) {
		this.enforceSingleExpandedPath = enforceSingleExpandedPath;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetEnforceSingleExpandedPathCommand(enforceSingleExpandedPath), null);
	}

	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		this.indentation = indentation;
		getClientObjectChannel().sendCommandIfRendered(new DtoTree.SetIndentationCommand(indentation), null);
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
		refresh();
	}

	public TemplateDecider<RECORD> getTemplateDecider() {
		return templateDecider;
	}

	public void setTemplateDecider(TemplateDecider<RECORD> templateDecider) {
		this.templateDecider = templateDecider;
		refresh();
	}

	private RECORD getRecordByUiId(int uiRecordId) {
		// no fast implementation needed! only called on user click
		return uiRecordsByRecord.keySet().stream()
				.filter(rr -> uiRecordsByRecord.get(rr).getId() == uiRecordId)
				.findFirst().orElse(null);
	}

}
