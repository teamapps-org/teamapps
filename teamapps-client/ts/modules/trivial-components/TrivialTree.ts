/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
	defaultEntryMatchingFunctionFactory,
	defaultTreeQueryFunctionFactory,
	HighlightDirection,
	QueryFunction,
	TrivialComponent,
	keyCodes,
	DEFAULT_RENDERING_FUNCTIONS, unProxyEntry
} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import KeyDownEvent = JQuery.KeyDownEvent;

export interface TrivialTreeConfig<E> extends TrivialTreeBoxConfig<E> {
    directSelectionViaArrowKeys?: boolean
}

export class TrivialTree<E> implements TrivialComponent{

    private config: TrivialTreeConfig<E>;

    public readonly onSelectedEntryChanged = new TrivialEvent<E>(this);
    public readonly onNodeExpansionStateChanged = new TrivialEvent<E>(this);

    private treeBox: TrivialTreeBox<E>;
    private entries: E[];
    private selectedEntryId: any;

    private $spinners = $();
    private $componentWrapper: JQuery;

    constructor(options: TrivialTreeConfig<E>) {
	    let defaultIdFunction = (entry:E) => entry ? (entry as any).id : null;
	    this.config = $.extend({
            idFunction: defaultIdFunction,
            inputValueFunction: defaultIdFunction,
            childrenProperty: "children",
		    lazyChildrenFlag: "hasLazyChildren",
            lazyChildrenQueryFunction: (node: E, resultCallback: Function) => {
                resultCallback([])
            },
            expandedProperty: 'expanded',
            spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
            noEntriesTemplate: DEFAULT_TEMPLATES.defaultNoEntriesTemplate,
            entries: null,
            selectedEntryId: null,
            matchingOptions: {
                matchingMode: 'contains',
                ignoreCase: true,
                maxLevenshteinDistance: 2
            },
            directSelectionViaArrowKeys: false,
            performanceOptimizationSettings: {
                toManyVisibleItemsRenderDelay: 750,
                toManyVisibleItemsThreshold: 75
            }
        }, options);

        this.entries = this.config.entries;

        this.$componentWrapper = $('<div class="tr-tree" tabindex="0"></div>');
        this.$componentWrapper.keydown((e:KeyDownEvent) => {
            if (e.which == keyCodes.up_arrow || e.which == keyCodes.down_arrow) {
                const direction = e.which == keyCodes.up_arrow ? -1 : 1;
                if (this.entries != null) {
                    if (this.config.directSelectionViaArrowKeys) {
                        this.treeBox.selectNextEntry(direction, false, () => true, true, e);
                    } else {
                        this.treeBox.selectNextEntry(direction, false);
                    }
                    return false; // some browsers move the caret to the beginning on up key
                }
            } else if (e.which == keyCodes.left_arrow || e.which == keyCodes.right_arrow) {
                this.treeBox.setSelectedNodeExpanded(e.which == keyCodes.right_arrow);
            } else if (e.which == keyCodes.enter) {
                this.treeBox.setSelectedEntryById(this.config.idFunction(this.treeBox.getSelectedEntry()), true);
            }
        });

        this.treeBox = new TrivialTreeBox(this.config);
        this.$componentWrapper.append(this.treeBox.getMainDomElement());
        this.treeBox.onNodeExpansionStateChanged.addListener((node: E)=> {
            this.onNodeExpansionStateChanged.fire(node);
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
        this.$spinners.remove();
        this.$spinners = $();
        this.treeBox.updateEntries(newEntries);
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
        this.selectedEntryId = entry ? this.config.idFunction(entry) : null;
        this.fireChangeEvents(entry);
    }

    private fireChangeEvents(entry: E) {
        this.$componentWrapper.trigger("change");
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

    public selectNodeById(nodeId: any) {
        this.treeBox.setSelectedEntryById(nodeId);
    };

    public getTreeBox() {
        return this.treeBox;
    }

    public destroy() {
        this.$componentWrapper.remove();
    };

    getMainDomElement(): HTMLElement {
        return this.$componentWrapper[0];
    }
}
