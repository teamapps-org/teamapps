/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
	UiTree_NodeExpansionChangedEvent,
	UiTree_NodeSelectedEvent,
	UiTree_RequestTreeDataEvent,
	UiTree_TextInputEvent,
	UiTreeCommandHandler,
	UiTreeConfig,
	UiTreeEventSource
} from "../generated/UiTreeConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TrivialTree} from "./trivial-components/TrivialTree";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {buildObjectTree, NodeWithChildren, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTreeRecordConfig} from "../generated/UiTreeRecordConfig";
import {UiComboBoxTreeRecordConfig} from "../generated/UiComboBoxTreeRecordConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {loadSensitiveThrottling} from "./util/throttle";


export class UiTree extends AbstractUiComponent<UiTreeConfig> implements UiTreeCommandHandler, UiTreeEventSource {

	public readonly onTextInput: TeamAppsEvent<UiTree_TextInputEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 500});
	public readonly onNodeSelected: TeamAppsEvent<UiTree_NodeSelectedEvent> = new TeamAppsEvent();
	public readonly onRequestTreeData: TeamAppsEvent<UiTree_RequestTreeDataEvent> = new TeamAppsEvent();
	public readonly onNodeExpansionChanged: TeamAppsEvent<UiTree_NodeExpansionChangedEvent> = new TeamAppsEvent();

	private $panel: HTMLElement;
	private trivialTree: TrivialTree<UiTreeRecordConfig>;
	private nodes: UiTreeRecordConfig[];
	private templateRenderers: { [name: string]: Renderer };


	constructor(config: UiTreeConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = parseHtml('<div class="UiTree">');

		this.templateRenderers = context.templateRegistry.createTemplateRenderers(config.templates);

		this.nodes = config.initialData;

		this.trivialTree = new TrivialTree<UiTreeRecordConfig>({
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
		this.trivialTree.onNodeExpansionStateChanged.addListener(({node, expanded}) => this.onNodeExpansionChanged.fire({nodeId: node.id, expanded}))
		this.$panel.appendChild(this.trivialTree.getMainDomElement());

		if (config.selectedNodeId != null) {
			this.trivialTree.getTreeBox().revealSelectedEntry(false);
		}
	}

	private renderRecord(record: NodeWithChildren<UiComboBoxTreeRecordConfig>): string {
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
	replaceData(nodes: UiTreeRecordConfig[]): void {
		this.nodes = nodes;
		this.trivialTree.updateEntries(buildObjectTree(nodes, "id", "parentId"));
	}

	bulkUpdate(nodesToBeRemoved: number[], nodesToBeAdded: UiTreeRecordConfig[]): void {
		this.nodes = this.nodes.filter(node => nodesToBeRemoved.indexOf(node.id) === -1);
		this.nodes.push(...nodesToBeAdded);
		nodesToBeRemoved.forEach(nodeId => this.trivialTree.removeNode(nodeId));
		nodesToBeAdded.forEach(node => this.trivialTree.addOrUpdateNode(node.parentId, node, false));
	}

	public setSelectedNode(recordId: number | null): void {
		this.trivialTree.selectNodeById(recordId);
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTree", UiTree);
