/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import {UiComboBox} from "./formfield/UiComboBox";

import {UiTree_NodeSelectedEvent, UiTree_RequestTreeDataEvent, UiTree_TextInputEvent, UiTreeCommandHandler, UiTreeConfig, UiTreeEventSource} from "../generated/UiTreeConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {defaultTreeQueryFunctionFactory, ResultCallback, trivialMatch, TrivialTree} from "trivial-components";
import {UiComponent} from "./UiComponent";
import {buildObjectTree, buildTreeEntryHierarchy, matchingModesMapping, NodeWithChildren, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTreeRecordConfig} from "../generated/UiTreeRecordConfig";
import {UiComboBoxTreeRecordConfig} from "../generated/UiComboBoxTreeRecordConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";


export class UiTree extends UiComponent<UiTreeConfig> implements UiTreeCommandHandler, UiTreeEventSource {

	public readonly onTextInput: TeamAppsEvent<UiTree_TextInputEvent> = new TeamAppsEvent(this, 250);
	public readonly onNodeSelected: TeamAppsEvent<UiTree_NodeSelectedEvent> = new TeamAppsEvent(this);
	public readonly onRequestTreeData: TeamAppsEvent<UiTree_RequestTreeDataEvent> = new TeamAppsEvent(this);

	private $panel: HTMLElement;
	private trivialTree: TrivialTree<UiTreeRecordConfig>;
	private lastResultCallback: (result: NodeWithChildren<UiTreeRecordConfig>[]) => void;
	private nodes: UiTreeRecordConfig[];
	private templateRenderers: { [name: string]: Renderer };


	constructor(config: UiTreeConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = parseHtml('<div class="UiTree" data-teamapps-id="${config.id}">');
		const $input = parseHtml('<input autocomplete="off"></input>');
		this.$panel.appendChild($input);

		this.templateRenderers = context.templateRegistry.createTemplateRenderers(config.templates);

		this.nodes = config.initialData;

		let localQueryFunction: Function;
		let queryFunction = (queryString: string, resultCallback: ResultCallback<NodeWithChildren<UiTreeRecordConfig>>) => {
			this.lastResultCallback = resultCallback;
			this.onTextInput.fire(EventFactory.createUiTree_TextInputEvent(this.getId(), queryString));
			if (localQueryFunction != null) {
				localQueryFunction(queryString, resultCallback);
			}
		};

		this.trivialTree = new TrivialTree<UiTreeRecordConfig>($input, {
			entries: buildObjectTree(config.initialData, "id", "parentId"),
			selectedEntryId: config.selectedNodeId,
			childrenProperty: "__children",
			expandedProperty: "expanded",
			entryRenderingFunction: entry => this.renderRecord(entry),
			searchBarMode: 'show-if-filled',
			lazyChildrenFlag: 'lazyChildren',
			lazyChildrenQueryFunction: (node, resultCallback) => {
				this.onRequestTreeData.fire(EventFactory.createUiTree_RequestTreeDataEvent(config.id, node && node.id))
			},
			spinnerTemplate: `<div class="UiSpinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			queryFunction: queryFunction,
			showExpanders: config.showExpanders,
			expandOnSelection: config.openOnSelection,
			enforceSingleExpandedPath: config.enforceSingleExpandedPath,
			inputValueFunction: entry => entry && entry.id,
			idFunction: entry => entry && entry.id,
			indentation: config.indentation,
			animationDuration: config.animate ? 70 : 0,
			matchingOptions: {
				matchingMode: matchingModesMapping[config.textMatchingMode]
			}
		});
		this.trivialTree.onSelectedEntryChanged.addListener((entry) => {
			this.onNodeSelected.fire(EventFactory.createUiTree_NodeSelectedEvent(config.id, entry.id));
		});

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

	public getMainDomElement(): HTMLElement {
		return this.$panel;
	}

	replaceData(nodes: UiTreeRecordConfig[]): void {
		this.nodes = nodes;
		this.trivialTree.updateEntries(buildObjectTree(nodes, "id", "parentId"));
	}

	bulkUpdate(nodesToBeRemoved: number[], nodesToBeAdded: UiTreeRecordConfig[]): void {
		this.nodes = this.nodes.filter(node => nodesToBeRemoved.indexOf(node.id) === -1);
		this.nodes.push(...nodesToBeAdded);
		nodesToBeRemoved.forEach(nodeId => this.trivialTree.removeNode(nodeId));
		nodesToBeAdded.forEach(node => this.trivialTree.addOrUpdateNode(node.parentId, node));
	}

	public setSelectedNode(recordId: number | null): void {
		this.trivialTree.selectNodeById(recordId);
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	public destroy(): void {
		// nothing to do
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiTree", UiTree);
