/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import {DEFAULT_TEMPLATES, generateUUID, HighlightDirection, MatchingOptions, TrivialComponent} from "./TrivialCore";
import {highlightMatches} from "./util/highlight";
import {addDelegatedEventListener, closestAncestor, insertAfter, insertBefore, parseHtml, toggleElementCollapsed} from "../Common";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export interface TrivialTreeBoxConfig<E> {
	/**
	 * Calculates a unique value for an entry. Used to identify nodes in the tree.
	 */
	idFunction?: (entry: E) => number | string,

	/**
	 * Rendering function used to display a _suggested_ entry
	 * (i.e. an entry displayed in the dropdown).
	 *
	 * @param entry
	 * @param depth the depth of the entry in the tree
	 * @return HTML string
	 * @default Using the `image2LinesTemplate` from `TrivialCore`.
	 */
	entryRenderingFunction: (entry: E, depth: number) => string,

	/**
	 * The initially selected entry. (Caution: use `selectedEntries` for `TrivialTagBox`).
	 */
	selectedEntry?: E,

	/**
	 * The initial list of suggested entries.
	 */
	entries?: E[],

	/**
	 * Used for highlighting suggested entries. Also used by the default filtering functions int `TrivialCore`.
	 *
	 * @default `{ matchingMode: 'contains', ignoreCase: true, maxLevenshteinDistance: 1 }`
	 */
	matchingOptions?: MatchingOptions,

	/**
	 * Property used to retrieve the children of a node.
	 *
	 * @default `'children'`
	 */
	childrenProperty?: string,

	/**
	 * Property name or function used to determine whether a node has children that need to be lazy-loaded.
	 *
	 * @default `'hasLazyChildren'`
	 */
	lazyChildrenFlag?: string | ((entry: E) => boolean),

	/**
	 * Function for retrieving children of a node.
	 *
	 * @param node
	 * @param resultCallback
	 */
	lazyChildrenQueryFunction?: (entry: E) => Promise<E[]>,

	/**
	 * Property used to determine whether a node is expanded or not.
	 *
	 * Note: This is subject to being replaced by a function in future versions.
	 * @default `'expanded'`
	 */
	expandedProperty?: string,

	/**
	 * The ID of the initially selected entry in the tree.
	 */
	selectedEntryId?: number | string,

	/**
	 * Animation duration in milliseconds for expand and collapse animations.
	 */
	animationDuration?: number,

	/**
	 * Whether or not to show the expander controls for parent nodes.
	 */
	showExpanders?: boolean,

	/**
	 * Whether or not to expand a node when it is selected.
	 */
	toggleExpansionOnClick?: boolean,

	/**
	 * Special mode that allows only one path to be expanded.
	 * Expands all ancestor nodes of the selected node, as well as the selected node itself.
	 * Collapses all others.
	 */
	enforceSingleExpandedPath?: boolean // only one path is expanded at any time

	/**
	 * Html string defining what to display when the list of results from the `queryFunction` is empty.
	 */
	noEntriesTemplate?: string,

	/**
	 * HTML string defining the spinner to be displayed while lazy children are being retrieved.
	 */
	spinnerTemplate?: string,

	/**
	 * The indentation in pixels used.
	 *
	 * If null, use the default value given by the CSS rules.
	 * If number, indent each depth/level by the given number of pixels.
	 * If array, indent each depth by the corresponding value in the array.
	 * If function, indent each depth by the result of the given function (with depth starting at 0).
	 */
	indentation?: null | number | number[] | ((depth: number) => number),

	/**
	 * Function that decides whether a node can be selected.
	 */
	selectableDecider?: (entry: E) => boolean,

	selectOnHover?: boolean,

}

class EntryWrapper<E> {
	public entry: E;
	public $element: HTMLElement;
	public children: EntryWrapper<E>[];
	public readonly depth: number;
	private _id: string | number;

	constructor(entry: E, public readonly parent: EntryWrapper<E>, private config: TrivialTreeBoxConfig<E> & {
		elementFactory?: (entry: EntryWrapper<E>) => HTMLElement
	}) {
		this.entry = entry;
		this.depth = parent != null ? parent.depth + 1 : 0;
		let children: E[] = (this.entry as any)[config.childrenProperty];
		let hasLazyChildren: boolean = (typeof config.lazyChildrenFlag === 'string') ? !!(this.entry as any)[config.lazyChildrenFlag] : config.lazyChildrenFlag(entry);
		if (children == null && hasLazyChildren) {
			this.children = null;
		} else if (children == null) {
			this.children = [];
		} else {
			this.children = children.map(child => new EntryWrapper(child, this, config));
		}
		let id = config.idFunction(entry);
		this._id = id != null ? id : generateUUID();
	}

	public get id() {
		return this._id;
	}

	public async lazyLoadChildren(): Promise<E[]> {
		let entries = await this.config.lazyChildrenQueryFunction(this.entry);
		this.children = entries.map(child => new EntryWrapper(child, this, this.config));
		return entries;
	}

	public render() {
		if (!this.$element) {
			$(this.config.elementFactory(this));
		}
		return this.$element;
	}

	public isLeaf(): boolean {
		return this.children != null && this.children.length === 0;
	}

	public get expanded(): boolean {
		return !!(this.entry as any)[this.config.expandedProperty] || false;
	}

	public set expanded(expanded: boolean) {
		(this.entry as any)[this.config.expandedProperty] = expanded;
		if (this.$element) {
			this.$element.classList.toggle("expanded", expanded);
		}
	}

	public get $treeEntryAndExpanderWrapper() {
		return this.$element?.querySelector(":scope > .tr-tree-entry-and-expander-wrapper");
	}

	public get $childrenWrapper(): HTMLElement {
		return this.$element?.querySelector(":scope > .tr-tree-entry-children-wrapper")
	}
}

export class TrivialTreeBox<E> implements TrivialComponent {

	private config: TrivialTreeBoxConfig<E>;

	public readonly onSelectedEntryChanged = new TeamAppsEvent<E>();
	public readonly onNodeExpansionStateChanged = new TeamAppsEvent<{node: E, expanded: boolean}>();

	private $componentWrapper: HTMLElement;
	private $tree: HTMLElement;

	private entries: EntryWrapper<E>[];
	private selectedEntryId: string | number;

	constructor(options: TrivialTreeBoxConfig<E>) {
		this.config = $.extend({
			idFunction: (entry: E) => entry ? (entry as any).id : null,
			childrenProperty: "children",
			lazyChildrenFlag: "hasLazyChildren",
			lazyChildrenQueryFunction: function (node: E, resultCallback: Function) {
				resultCallback((node as any).children || []);
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
			animationDuration: 70,
			showExpanders: false,
			toggleExpansionOnClick: false, // open expandable nodes when they are selected
			enforceSingleExpandedPath: false, // only one path is expanded at any time
			indentation: null,
			selectableDecider: () => true,
			selectOnHover: false
		}, options);

		this.$componentWrapper = parseHtml('<div class="tr-treebox"></div>');
		this.$componentWrapper.classList.toggle("hide-expanders", !this.config.showExpanders);

		this.setEntries(this.config.entries || []);

		this.setSelectedEntryById(this.config.selectedEntryId);
	}

	private createEntryElement(entry: EntryWrapper<E>) {
		const $outerEntryWrapper = parseHtml(`<div class="tr-tree-entry-outer-wrapper ${(entry.isLeaf() ? '' : 'has-children')}" data-id="${entry.id}"></div>`);
		entry.$element = $outerEntryWrapper;
		const $entryAndExpanderWrapper = $('<div class="tr-tree-entry-and-expander-wrapper"></div>')
			.appendTo($outerEntryWrapper);
		($entryAndExpanderWrapper[0] as any).trivialEntryWrapper = entry;
		for (let k = 0; k < entry.depth; k++) {
			let indentationWidth: number;
			if (typeof this.config.indentation === 'number') {
				indentationWidth = this.config.indentation;
			} else if (Array.isArray(this.config.indentation)) {
				indentationWidth = this.config.indentation[Math.min(k, this.config.indentation.length - 1)];
			} else if (typeof this.config.indentation === 'function') {
				indentationWidth = this.config.indentation(k);
			}
			$entryAndExpanderWrapper.append(`<div class="tr-indent-spacer" ${indentationWidth != null ? `style="width:${indentationWidth}px"` : ''}></div>`);
		}
		$('<div class="tr-tree-expander"></div>')
			.appendTo($entryAndExpanderWrapper);
		let $entry = $(this.config.entryRenderingFunction(entry.entry, entry.depth));
		$entry.addClass("tr-tree-entry filterable-item").appendTo($entryAndExpanderWrapper);

		if (entry.id === this.selectedEntryId) {
			$entryAndExpanderWrapper.addClass("tr-selected-entry");
		}

		if (!entry.isLeaf()) {
			this.create$ChildrenWrapper(entry);
		}
		this.setNodeExpanded(entry, entry.expanded, false);
		return $outerEntryWrapper;
	}

	private create$ChildrenWrapper(entry: EntryWrapper<E>): HTMLElement {
		let $childrenWrapper: HTMLElement = entry.$childrenWrapper;
		if ($childrenWrapper == null) {
			$childrenWrapper = parseHtml('<div class="tr-tree-entry-children-wrapper"></div>');
			entry.$element.append($childrenWrapper);
			if (entry.children != null) {
				if (entry.expanded) {
					for (let i = 0; i < entry.children.length; i++) {
						$childrenWrapper.append(this.createEntryElement(entry.children[i]));
					}
				}
			} else {
				$childrenWrapper.append(this.config.spinnerTemplate);
			}
		}
		return $childrenWrapper;
	}

	private setNodeExpanded(node: EntryWrapper<E>, expanded: boolean, animate: boolean, fireEvent = true) {
		let expansionStateChange = node.expanded != expanded;

		if (expanded && this.config.enforceSingleExpandedPath) {
			const currentlyExpandedNodes = this.findEntries((n) => {
				return n.expanded;
			});
			const newExpandedPath = this.findPathToFirstMatchingNode((n) => {
				return n === node;
			});
			for (let i = 0; i < currentlyExpandedNodes.length; i++) {
				const currentlyExpandedNode = currentlyExpandedNodes[i];
				if (newExpandedPath.indexOf(currentlyExpandedNode) === -1) {
					this.setNodeExpanded(currentlyExpandedNode, false, true, fireEvent);
				}
			}
		}

		node.expanded = expanded;

		if (node.$element) {
			let nodeHasUnrenderedChildren = (node: EntryWrapper<E>) => {
				return node.children && node.children.some((child: EntryWrapper<E>) => {
					return !child.$element || !$.contains(document.documentElement, child.$element);
				});
			};

			if (expanded && node.children == null) {
				node.lazyLoadChildren()
					.then((children) => this.setChildren(node, children));
			} else if (expanded && nodeHasUnrenderedChildren(node)) {
				this.renderChildren(node);
			}
			if (expanded) {
				this.minimallyScrollTo(node.$element);
			}

			const $childrenWrapper: HTMLElement = node.$childrenWrapper;
			if ($childrenWrapper != null) {
				toggleElementCollapsed($childrenWrapper, !expanded, animate ? this.config.animationDuration : 0);
			}
		}

		if (expansionStateChange && fireEvent) {
			this.onNodeExpansionStateChanged.fire({node: node.entry, expanded: expanded});
		}
	}

	private setChildren(parentEntryWrapper: EntryWrapper<E>, children: E[]) {
		parentEntryWrapper.children = children.map(child => this.createEntryWrapper(child, parentEntryWrapper));
		this.renderChildren(parentEntryWrapper);
	}

	private renderChildren(node: EntryWrapper<E>) {
		const $childrenWrapper = this.create$ChildrenWrapper(node);
		$childrenWrapper.innerHTML = ''; // remove the spinner!
		if (node.children && node.children.length > 0) {
			node.children.forEach(child => {
				$childrenWrapper.append(child.render());
			});
		} else {
			node.$element.classList.remove('has-children', 'expanded');
		}
	}

	public setEntries(newEntries: E[]) {
		newEntries = newEntries || [];
		this.entries = newEntries.map(e => this.createEntryWrapper(e, null));

		this.$tree && this.$tree.remove();
		this.$tree = parseHtml('<div class="tr-tree-entryTree"></div>');
		addDelegatedEventListener(this.$tree, ".tr-tree-expander", "mousedown", (element, ev) => {
			ev.stopPropagation();
			ev.stopImmediatePropagation();
		}, true);
		addDelegatedEventListener(this.$tree, ".tr-tree-expander", "click", (element, ev) => {
			let entryWrapper = (closestAncestor(element, ".tr-tree-entry-and-expander-wrapper") as any).trivialEntryWrapper as EntryWrapper<E>;
			this.setNodeExpanded(entryWrapper, !entryWrapper.expanded, true);
		});
		addDelegatedEventListener(this.$tree, ".tr-tree-entry-and-expander-wrapper", "mousedown", (element, ev) => {
			let entryWrapper = (element as any).trivialEntryWrapper as EntryWrapper<E>;
			if (this.config.selectableDecider(entryWrapper.entry)) {
				this.setSelectedEntry(entryWrapper, null, true);
			}
			if (entryWrapper && this.config.toggleExpansionOnClick) {
				this.setNodeExpanded(entryWrapper, !entryWrapper.expanded, true);
			}
		});
		addDelegatedEventListener(this.$tree, ".tr-tree-entry-and-expander-wrapper", "mouseenter", (element, ev) => {
			let entryWrapper = (element as any).trivialEntryWrapper as EntryWrapper<E>;
			if (this.config.selectOnHover && this.config.selectableDecider(entryWrapper.entry)) {
				this.setSelectedEntry(entryWrapper, ev, false);
			}
		}, true);

		if (this.entries.length > 0) {
			this.entries.forEach(entry => this.$tree.append(entry.render()));
		} else {
			this.$tree.append(parseHtml(this.config.noEntriesTemplate));
		}
		this.$componentWrapper.append(this.$tree);

		const selectedEntry = this.findEntryById(this.selectedEntryId);
		if (selectedEntry) {
			// selected entry in filtered tree? then mark it as selected!
			this.markSelectedEntry(selectedEntry);
		}
	}

	private createEntryWrapper(e: E, parent: EntryWrapper<E>) {
		return new EntryWrapper(e, parent, {
			...this.config,
			elementFactory: (entry: EntryWrapper<E>) => this.createEntryElement(entry)
		});
	}

	private findEntries(filterFunction: ((node: EntryWrapper<E>) => boolean), visibleOnly: boolean = false) {
		let findEntriesInSubTree = (node: EntryWrapper<E>, listOfFoundEntries: EntryWrapper<E>[]) => {
			if (filterFunction.call(this, node)) {
				listOfFoundEntries.push(node);
			}
			if (!visibleOnly || node.expanded) {
				if (node.children) {
					for (let i = 0; i < node.children.length; i++) {
						const child = node.children[i];
						findEntriesInSubTree(child, listOfFoundEntries);
					}
				}
			}
		};

		const matchingEntries: EntryWrapper<E>[] = [];
		for (let i = 0; i < this.entries.length; i++) {
			const rootEntry = this.entries[i];
			findEntriesInSubTree(rootEntry, matchingEntries);
		}
		return matchingEntries;
	}

	private findPathToFirstMatchingNode(predicateFunction: ((node: EntryWrapper<E>, path: EntryWrapper<E>[]) => boolean)): EntryWrapper<E>[] {
		let searchInSubTree = (node: EntryWrapper<E>, path: EntryWrapper<E>[]): EntryWrapper<E>[] => {
			if (predicateFunction.call(this, node, path)) {
				path.push(node);
				return path;
			}
			if (node.children) {
				const newPath = path.slice();
				newPath.push(node);
				for (let i = 0; i < node.children.length; i++) {
					const child = node.children[i];
					const result = searchInSubTree(child, newPath);
					if (result) {
						return result;
					}
				}
			}
		};

		for (let i = 0; i < this.entries.length; i++) {
			const rootEntry = this.entries[i];
			let path = searchInSubTree(rootEntry, []);
			if (path) {
				return path;
			}
		}
	}

	private findEntryById(id: string | number): EntryWrapper<E> {
		if (id == null) {
			return null;
		}
		return this.findEntries((entry) => {
			return entry.id === id
		})[0];
	}

	private setSelectedEntry(entry: EntryWrapper<E>, originalEvent?: unknown, fireEvents = false) {
		this.selectedEntryId = entry && entry.id;
		this.markSelectedEntry(entry);
		if (fireEvents) {
			this.fireChangeEvents(entry);
		}
	}

	public setSelectedEntryById(nodeId: number | string, fireEvents = false) {
		let entry = this.findEntryById(nodeId);
		this.setSelectedEntry(entry, null, fireEvents);
	}

	private minimallyScrollTo($entryWrapper: HTMLElement) {
		$entryWrapper.querySelector(":scope > .tr-tree-entry-and-expander-wrapper").scrollIntoView({
			// behavior: "smooth",
			block: "nearest"
		});
	}

	private markSelectedEntry(entry: EntryWrapper<E>) {
		this.$tree.querySelectorAll(":scope .tr-selected-entry").forEach(e => e.classList.remove("tr-selected-entry"));
		if (entry && entry.$element) {
			const $entryWrapper = entry.$element.querySelector(':scope > .tr-tree-entry-and-expander-wrapper');
			$entryWrapper.classList.add("tr-selected-entry");
		}
	}

	private fireChangeEvents(entry: EntryWrapper<E>) {
		this.onSelectedEntryChanged.fire(entry.entry);
	}

	public selectNextEntry(direction: HighlightDirection, rollover = false, selectableOnly = true, matcher: (entry: E) => boolean = () => true, fireEvents = false, originalEvent?: unknown): E {
		const nextMatchingEntry = this.getNextVisibleEntry(this.getSelectedEntryWrapper(), direction, rollover, selectableOnly, matcher);
		if (nextMatchingEntry != null) {
			this.setSelectedEntry(nextMatchingEntry, originalEvent, fireEvents);
			this.minimallyScrollTo(nextMatchingEntry.$element);
		}
		return nextMatchingEntry?.entry;
	}

	private getNextVisibleEntry(currentEntry: EntryWrapper<E>, direction: HighlightDirection, rollover: boolean, selectableOnly = true, entryMatcher: (entry: E) => boolean = () => true) {
		let newSelectedElementIndex: number;
		const visibleEntriesAsList = this.findEntries((entry) => {
			if (!entry.$element) {
				return false;
			} else {
				return ((!selectableOnly || this.config.selectableDecider(entry.entry)) && entryMatcher(entry.entry)) || entry === currentEntry;
			}
		}, true);
		if (visibleEntriesAsList == null || visibleEntriesAsList.length == 0) {
			return null;
		} else if (currentEntry == null && direction > 0) {
			newSelectedElementIndex = -1 + direction;
		} else if (currentEntry == null && direction < 0) {
			newSelectedElementIndex = visibleEntriesAsList.length + direction;
		} else {
			const currentSelectedElementIndex = visibleEntriesAsList.indexOf(currentEntry);
			if (rollover) {
				newSelectedElementIndex = (currentSelectedElementIndex + visibleEntriesAsList.length + direction) % visibleEntriesAsList.length;
			} else {
				newSelectedElementIndex = Math.max(0, Math.min(visibleEntriesAsList.length - 1, currentSelectedElementIndex + direction));
			}
		}
		return visibleEntriesAsList[newSelectedElementIndex];
	}

	public highlightTextMatches(searchString: string) {
		this.$tree.remove();
		for (let i = 0; i < this.entries.length; i++) {
			const entry = this.entries[i];
			const $entryElement = entry.$element.querySelectorAll(':scope .tr-tree-entry');
			highlightMatches($entryElement, searchString, this.config.matchingOptions)
		}
		this.$componentWrapper.append(this.$tree);
	}

	public getSelectedEntry(): E | null {
		let selectedEntryWrapper = this.getSelectedEntryWrapper();
		return selectedEntryWrapper != null ? selectedEntryWrapper.entry : null;
	}

	private getSelectedEntryWrapper() {
		return (this.selectedEntryId !== undefined && this.selectedEntryId !== null) ? this.findEntryById(this.selectedEntryId) : null;
	}

	public revealSelectedEntry(animate: boolean = false) {
		let selectedEntry = this.getSelectedEntryWrapper();
		if (!selectedEntry) {
			return;
		}
		let currentEntry = selectedEntry;
		while (currentEntry = currentEntry.parent) {
			this.setNodeExpanded(currentEntry, true, animate);
		}
		this.minimallyScrollTo(selectedEntry.$element);
	}

	public setSelectedNodeExpanded(expanded: boolean) {
		let selectedEntry = this.getSelectedEntryWrapper();
		if (!selectedEntry || selectedEntry.isLeaf()) {
			return false;
		} else {
			let wasExpanded = selectedEntry.expanded;
			this.setNodeExpanded(selectedEntry, expanded, true);
			return !wasExpanded != !expanded;
		}
	}

	public updateChildren(parentNodeId: string | number, children: E[]) {
		const node = this.findEntryById(parentNodeId);
		if (node) {
			this.setChildren(node, children);
		} else {
			console.error("Could not set the children of unknown node with id " + parentNodeId);
		}
	};

	public updateNode(node: E, recursive: boolean = false) {
		const oldNode = this.findEntryById(this.config.idFunction(node));
		let shouldBeExpanded = (node as any)[this.config.expandedProperty];
		const expandedStateChanged = oldNode.expanded !== shouldBeExpanded;
		let parentNode = oldNode.parent;
		let newEntryWrapper: EntryWrapper<E>;
		if (recursive) {
			newEntryWrapper = this.createEntryWrapper(node, parentNode);
		} else {
			oldNode.entry = node;
			let $entry = parseHtml(this.config.entryRenderingFunction(node, oldNode.depth));
			$entry.classList.add("tr-tree-entry", "filterable-item");
			let $oldEntry = oldNode.$element.querySelector(":scope .tr-tree-entry");
			insertBefore($entry, $oldEntry);
			$oldEntry.remove();
			newEntryWrapper = oldNode;
		}
		if (parentNode) {
			parentNode.children[parentNode.children.indexOf(oldNode)] = newEntryWrapper;
		} else {
			this.entries[this.entries.indexOf(oldNode)] = newEntryWrapper;
		}
		if (newEntryWrapper != oldNode) {
			insertAfter(newEntryWrapper.render(), oldNode.$element);
			oldNode.$element.remove();
		}
		if (expandedStateChanged) {
			this.setNodeExpanded(newEntryWrapper, shouldBeExpanded as boolean, true)
		}
	};

	public removeNode(nodeId: string | number) {
		const childNode = this.findEntryById(nodeId);
		if (childNode) {
			const parentNode = childNode.parent;
			if (parentNode) {
				parentNode.children.splice(parentNode.children.indexOf(childNode), 1);
			} else {
				this.entries.splice(this.entries.indexOf(childNode), 1);
			}
			childNode.$element.remove();
		}
	};

	public addNode(parentNodeId: number | string, node: E) {
		const parentNode = this.findEntryById(parentNodeId);
		if (parentNode.children == null) {
			parentNode.children = [];
		}
		let newEntryWrapper = this.createEntryWrapper(node, parentNode);
		let $childrenWrapper = this.create$ChildrenWrapper(parentNode);
		if (parentNode.children.length === 0) {
			$childrenWrapper.innerHTML = ''; // remove the spinner!
		}
		parentNode.children.push(newEntryWrapper);
		$childrenWrapper.append(newEntryWrapper.render());
		parentNode.$element.classList.add('has-children');
	};

	public addOrUpdateNode(parentNodeId: number | string, node: E, recursiveUpdate = false) {
		let existingNode = this.findEntryById(this.config.idFunction(node));
		if (existingNode != null) {
			this.updateNode(node, recursiveUpdate);
		} else {
			this.addNode(parentNodeId, node);
		}
	}

	public destroy() {
		this.$componentWrapper.remove();
	};

	getMainDomElement(): HTMLElement {
		return this.$componentWrapper;
	}
}
