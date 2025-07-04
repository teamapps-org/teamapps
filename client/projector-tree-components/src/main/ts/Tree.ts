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

import {
	type DtoTree,
	type DtoTree_ContextMenuRequestedEvent,
	type DtoTree_NodeExpansionChangedEvent,
	type DtoTree_NodeSelectedEvent,
	type DtoTreeCommandHandler,
	type DtoTreeEventSource,
	type DtoTreeRecord,
	type DtoTreeServerObjectChannel
} from "./generated";
import {TrivialTree} from "./trivial-components/TrivialTree";
import {
	AbstractComponent,
	type Component,
	loadSensitiveThrottling,
	parseHtml,
	ProjectorEvent,
	type Template
} from "projector-client-object-api";
import {buildObjectTree, type NodeWithChildren} from "./util";
import {ContextMenu} from "projector-client-core-components";

export class Tree extends AbstractComponent<DtoTree> implements DtoTreeCommandHandler, DtoTreeEventSource {

	public readonly onNodeSelected: ProjectorEvent<DtoTree_NodeSelectedEvent> = new ProjectorEvent();
	public readonly onNodeExpansionChanged: ProjectorEvent<DtoTree_NodeExpansionChangedEvent> = new ProjectorEvent();
	public readonly onContextMenuRequested: ProjectorEvent<DtoTree_ContextMenuRequestedEvent> = new ProjectorEvent();

	private $panel: HTMLElement;
	private trivialTree: TrivialTree<DtoTreeRecord>;
	private nodes: DtoTreeRecord[];
	private contextMenu: ContextMenu;

	private soc: DtoTreeServerObjectChannel;

	constructor(config: DtoTree, soc: DtoTreeServerObjectChannel) {
		super(config);
		this.soc = soc;
		this.$panel = parseHtml('<div class="Tree">');

		this.nodes = config.initialData;

		this.trivialTree = new TrivialTree<DtoTreeRecord>({
			entries: buildObjectTree(config.initialData, "id", "parentId"),
			selectedEntryId: config.selectedNodeId,
			childrenProperty: "__children",
			expandedProperty: "expanded",
			entryRenderingFunction: entry => this.renderRecord(entry),
			lazyChildrenFlag: 'lazyChildren',
			lazyChildrenQueryFunction: async (node) => await this.soc.sendQuery("lazyLoadChildren", node?.id),
			spinnerTemplate: `<div class="Spinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			showExpanders: config.expandersVisible,
			toggleExpansionOnClick: config.toggleExpansionOnClick,
			enforceSingleExpandedPath: config.enforceSingleExpandedPath,
			idFunction: entry => entry && entry.id,
			indentation: config.indentation,
			animationDuration: config.expandAnimationEnabled ? 120 : 0,
			directSelectionViaArrowKeys: true
		});
		this.trivialTree.onSelectedEntryChanged.addListener((entry) => {
			this.onNodeSelected.fire({
				nodeId: entry.id
			});
		});
		this.$panel.appendChild(this.trivialTree.getMainDomElement());

		if (config.selectedNodeId != null) {
			this.trivialTree.getTreeBox().revealSelectedEntry(false);
		}
		this.contextMenu = new ContextMenu();
		this.trivialTree.onContextMenu.addListener(contextMenuEvent => {
			if (this.config.contextMenuEnabled) {
				this.contextMenu.open(contextMenuEvent.event, requestId => this.onContextMenuRequested.fire({
					recordId: (contextMenuEvent.entry as DtoTreeRecord).id,
					requestId
				}));
			}
		})
	}

	private renderRecord(record: NodeWithChildren<DtoTreeRecord>): string {
		const template = record.displayTemplate as Template;
		if (template != null) {
			return template.render(record.values);
		} else {
			return `<div class="string-template">${record.asString}</div>`;
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$panel;
	}

	setExpandersVisible(expandersVisible: boolean) {
		this.trivialTree.setShowExpanders(expandersVisible);
	}
	setExpandAnimationEnabled(expandAnimationEnabled: boolean) {
		this.trivialTree.setAnimationDuration(expandAnimationEnabled ? 120 : 0);
	}
	setToggleExpansionOnClick(expandOnSelection: boolean) {
		this.trivialTree.setToggleExpansionOnClick(expandOnSelection);
	}
	setEnforceSingleExpandedPath(enforceSingleExpandedPath: boolean) {
		this.trivialTree.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
	}
	setIndentation(indentation: number) {
		this.trivialTree.setIndentationWidth(indentation);
	}
	setSelectedNodeId(selectedNodeId: number, reveal: boolean) {
		this.trivialTree.setSelectedEntryById(selectedNodeId, reveal);
	}

	@loadSensitiveThrottling(100, 10, 3000)
	replaceNodes(nodes: DtoTreeRecord[]): void {
		this.nodes = nodes;
		this.trivialTree.updateEntries(buildObjectTree(nodes, "id", "parentId"));
	}

	bulkUpdate(nodesToBeRemoved: number[], nodesToBeAdded: DtoTreeRecord[]): void {
		this.nodes = this.nodes.filter(node => nodesToBeRemoved.indexOf(node.id) === -1);
		this.nodes.push(...nodesToBeAdded);
		nodesToBeRemoved.forEach(nodeId => this.trivialTree.removeNode(nodeId));
		nodesToBeAdded.forEach(node => this.trivialTree.addOrUpdateNode(node.parentId, node, false));
	}

	setContextMenuContent(requestId: number, component: unknown): void {
		this.contextMenu.setContent(component as Component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

	setContextMenuEnabled(contextMenuEnabled: boolean): any {
		this.config.contextMenuEnabled = contextMenuEnabled;
	}

}


