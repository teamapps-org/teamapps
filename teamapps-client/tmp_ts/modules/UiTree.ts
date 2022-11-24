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
	UiTree_NodeSelectedEvent,
	UiTree_RequestTreeDataEvent,
	UiTree_TextInputEvent,
	UiTreeCommandHandler,
	DtoTree,
	UiTreeEventSource
} from "../generated/DtoTree";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TrivialTree} from "./trivial-components/TrivialTree";
import {AbstractComponent} from "teamapps-client-core";
import {buildObjectTree, NodeWithChildren, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {DtoTreeRecord} from "../generated/DtoTreeRecord";
import {DtoComboBoxTreeRecord} from "../generated/DtoComboBoxTreeRecord";
import {DtoTemplate} from "../generated/DtoTemplate";
import {loadSensitiveThrottling} from "./util/throttle";


export class UiTree extends AbstractComponent<DtoTree> implements UiTreeCommandHandler, UiTreeEventSource {

	public readonly onTextInput: TeamAppsEvent<UiTree_TextInputEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 500});
	public readonly onNodeSelected: TeamAppsEvent<UiTree_NodeSelectedEvent> = new TeamAppsEvent();
	public readonly onRequestTreeData: TeamAppsEvent<UiTree_RequestTreeDataEvent> = new TeamAppsEvent();

	private $panel: HTMLElement;
	private trivialTree: TrivialTree<DtoTreeRecord>;
	private nodes: DtoTreeRecord[];
	private templateRenderers: { [name: string]: Renderer };


	constructor(config: DtoTree, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = parseHtml('<div class="UiTree">');

		this.templateRenderers = context.templateRegistry.createTemplateRenderers(config.templates);

		this.nodes = config.initialData;

		this.trivialTree = new TrivialTree<DtoTreeRecord>({
			entries: buildObjectTree(config.initialData, "id", "parentId"),
			selectedEntryId: config.selectedNodeId,
			childrenProperty: "__children",
			expandedProperty: "expanded",
			entryRenderingFunction: entry => this.renderRecord(entry),
			lazyChildrenFlag: 'lazyChildren',
			lazyChildrenQueryFunction: async (node) => {
				this.onRequestTreeData.fire({
					parentNodeId: node && node.id
				});
				return []; // TODO this will not show a spinner...
			},
			spinnerTemplate: `<div class="UiSpinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			showExpanders: config.showExpanders,
			expandOnSelection: config.openOnSelection,
			enforceSingleExpandedPath: config.enforceSingleExpandedPath,
			idFunction: entry => entry && entry.id,
			indentation: config.indentation,
			animationDuration: config.animate ? 120 : 0,
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

	private renderRecord(record: NodeWithChildren<DtoComboBoxTreeRecord>): string {
		if (record.displayTemplateId != null && this.templateRenderers[record.displayTemplateId] != null) {
			const renderer = this.templateRenderers[record.displayTemplateId];
			return renderer.render(record.values);
		} else {
			return `<div class="string-template">${record.asString}</div>`;
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$panel;
	}

	@loadSensitiveThrottling(100, 10, 3000)
	replaceData(nodes: DtoTreeRecord[]): void {
		this.nodes = nodes;
		this.trivialTree.updateEntries(buildObjectTree(nodes, "id", "parentId"));
	}

	bulkUpdate(nodesToBeRemoved: number[], nodesToBeAdded: DtoTreeRecord[]): void {
		this.nodes = this.nodes.filter(node => nodesToBeRemoved.indexOf(node.id) === -1);
		this.nodes.push(...nodesToBeAdded);
		nodesToBeRemoved.forEach(nodeId => this.trivialTree.removeNode(nodeId));
		nodesToBeAdded.forEach(node => this.trivialTree.addOrUpdateNode(node.parentId, node, false));
	}

	public setSelectedNode(recordId: number | null): void {
		this.trivialTree.selectNodeById(recordId);
	}

	registerTemplate(id: string, template: DtoTemplate): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

}


