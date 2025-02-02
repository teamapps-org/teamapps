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
	DtoTree,
	DtoTree_NodeSelectedEvent,
	DtoTreeCommandHandler,
	DtoTreeEventSource, DtoTreeRecord, DtoTreeServerObjectChannel
} from "./generated";
import {TrivialTree} from "./trivial-components/TrivialTree";
import {AbstractLegacyComponent, loadSensitiveThrottling, parseHtml, TeamAppsEvent, Template} from "projector-client-object-api";
import {buildObjectTree, NodeWithChildren} from "./util";

export class Tree extends AbstractLegacyComponent<DtoTree> implements DtoTreeCommandHandler, DtoTreeEventSource {

	public readonly onNodeSelected: TeamAppsEvent<DtoTree_NodeSelectedEvent> = new TeamAppsEvent();

	private $panel: HTMLElement;
	private trivialTree: TrivialTree<DtoTreeRecord>;
	private nodes: DtoTreeRecord[];

	constructor(config: DtoTree, private soc: DtoTreeServerObjectChannel) {
		super(config);
		this.$panel = parseHtml('<div class="UiTree">');

		this.nodes = config.initialData;

		this.trivialTree = new TrivialTree<DtoTreeRecord>({
			entries: buildObjectTree(config.initialData, "id", "parentId"),
			selectedEntryId: config.selectedNodeId,
			childrenProperty: "__children",
			expandedProperty: "expanded",
			entryRenderingFunction: entry => this.renderRecord(entry),
			lazyChildrenFlag: 'lazyChildren',
			lazyChildrenQueryFunction: async (node) => await this.soc.sendQuery("lazyLoadChildren", node?.id),
			spinnerTemplate: `<div class="UiSpinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			showExpanders: config.expandersVisible,
			expandOnSelection: config.expandOnSelection,
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
	setExpandOnSelection(expandOnSelection: boolean) {
		this.trivialTree.setExpandOnSelection(expandOnSelection);
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

}


