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

import {TrivialTreeBox, TrivialTreeBoxConfig} from "./TrivialTreeBox";
import {
	DEFAULT_TEMPLATES,
	TrivialComponent,
	unProxyEntry
} from "./TrivialCore";
import KeyDownEvent = JQuery.KeyDownEvent;
import {parseHtml, TeamAppsEvent} from "projector-client-object-api";

export interface TrivialTreeConfig<E> extends TrivialTreeBoxConfig<E> {
    directSelectionViaArrowKeys?: boolean
}

export class TrivialTree<E> implements TrivialComponent{

    private config: TrivialTreeConfig<E>;

    public readonly onSelectedEntryChanged = new TeamAppsEvent<E>();
    public readonly onNodeExpansionStateChanged = new TeamAppsEvent<{node: E, expanded: boolean}>();

    private treeBox: TrivialTreeBox<E>;
    private entries: E[];

    private $componentWrapper: HTMLElement;

    constructor(options: TrivialTreeConfig<E>) {
	    let defaultIdFunction = (entry:E) => entry ? (entry as any).id : null;
	    this.config = {
            idFunction: defaultIdFunction,
            childrenProperty: "children",
		    lazyChildrenFlag: "hasLazyChildren",
            lazyChildrenQueryFunction: async () => [],
            expandedProperty: 'expanded',
            spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
            noEntriesTemplate: DEFAULT_TEMPLATES.defaultNoEntriesTemplate,
            entries: null,
            selectedEntryId: null,
            matchingOptions: {
                matchingMode: 'contains',
                ignoreCase: true
            },
            directSelectionViaArrowKeys: false,
            ...options
        };

        this.entries = this.config.entries;

        this.$componentWrapper = parseHtml('<div class="tr-tree" tabindex="0"></div>');
        this.$componentWrapper.addEventListener("keydown", (e:KeyboardEvent) => {
            if (e.key == "ArrowUp" || e.key == "ArrowDown") {
                const direction = e.key == "ArrowUp" ? -1 : 1;
                if (this.entries != null) {
                    if (this.config.directSelectionViaArrowKeys) {
                        this.treeBox.selectNextEntry(direction, false, false, () => true, true, e);
                    } else {
                        this.treeBox.selectNextEntry(direction, false);
                    }
                    return false; // some browsers move the caret to the beginning on up key
                }
            } else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
                this.treeBox.setSelectedNodeExpanded(e.key == "ArrowRight");
            } else if (e.key == "Enter") {
                this.fireChangeEvents(this.treeBox.getSelectedEntry());
            }
        });

        this.treeBox = new TrivialTreeBox(this.config);
        this.$componentWrapper.append(this.treeBox.getMainDomElement());
        this.treeBox.onNodeExpansionStateChanged.addListener((e)=> {
            this.onNodeExpansionStateChanged.fire(e);
        });
        this.treeBox.onSelectedEntryChanged.addListener(() => {
            const selectedTreeBoxEntry = this.treeBox.getSelectedEntry();
            if (selectedTreeBoxEntry) {
                this.setSelectedEntry(selectedTreeBoxEntry);
            }
        });

        this.setSelectedEntry((this.config.selectedEntryId !== undefined && this.config.selectedEntryId !== null) ? this.findEntryById(this.config.selectedEntryId) : null);
    }

    public updateEntries(newEntries: E[]) {
        this.entries = newEntries;
        this.treeBox.setEntries(newEntries);
    }

    private findEntries(filterFunction: ((node: E) => boolean)) {
        let findEntriesInSubTree = (node: E, listOfFoundEntries: E[]) => {
            if (filterFunction.call(this, node)) {
                listOfFoundEntries.push(node);
            }
            if ((node as any)[this.config.childrenProperty]) {
                for (let i = 0; i < (node as any)[this.config.childrenProperty].length; i++) {
                    const child = (node as any)[this.config.childrenProperty][i];
                    findEntriesInSubTree(child, listOfFoundEntries);
                }
            }
        };

        const matchingEntries: E[] = [];
        for (let i = 0; i < this.entries.length; i++) {
            const rootEntry = this.entries[i];
            findEntriesInSubTree(rootEntry, matchingEntries);
        }
        return matchingEntries;
    }

    private findEntryById(id: string | number) {
        return this.findEntries((entry: E) => {
            return this.config.idFunction(entry) === id
        })[0];
    }

    private setSelectedEntry(entry: E) {
        this.config.selectedEntryId = entry ? this.config.idFunction(entry) : null;
        this.fireChangeEvents(entry);
    }

    private fireChangeEvents(entry: E) {
        this.onSelectedEntryChanged.fire(unProxyEntry(entry));
    }

    public getSelectedEntry() {
    	unProxyEntry(this.treeBox.getSelectedEntry());
    };

    public updateChildren(parentNodeId: any, children: E[]) {
        this.treeBox.updateChildren(parentNodeId, children)
    };

    public updateNode(node: E) {
        this.treeBox.updateNode(node)
    };

    public removeNode(nodeId: string | number) {
        this.treeBox.removeNode(nodeId)
    };

    public addNode(parentNodeId: string | number, node: E) {
        this.treeBox.addNode(parentNodeId, node)
    };

    public addOrUpdateNode(parentNodeId: string | number, node: E, recursiveUpdate = false) {
        this.treeBox.addOrUpdateNode(parentNodeId, node, recursiveUpdate);
    }

    public getTreeBox() {
        return this.treeBox;
    }

    setShowExpanders(showExpanders: boolean) {
        this.treeBox.setShowExpanders(showExpanders);
    }

    setAnimationDuration(animationDuration: number) {
        this.treeBox.setAnimationDuration(animationDuration);
    }


    setSelectedEntryById(nodeId: number | string, reveal: boolean) {
        this.treeBox.setSelectedEntryById(nodeId, reveal);
    }

    setExpandOnSelection(expandOnSelection: boolean) {
        this.treeBox.setExpandOnSelection(expandOnSelection);
    }

    setEnforceSingleExpandedPath(enforceSingleExpandedPath: boolean) {
        this.treeBox.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
    }

    setIndentationWidth(indentationWidth: number) {
        this.treeBox.setIndentationWidth(indentationWidth);
    }

    public destroy() {
        this.$componentWrapper.remove();
    };

    getMainDomElement(): HTMLElement {
        return this.$componentWrapper;
    }
}
