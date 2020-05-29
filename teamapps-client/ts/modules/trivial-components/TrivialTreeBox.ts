/*!
Trivial Components (https://github.com/trivial-components/trivial-components)

Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import {DEFAULT_RENDERING_FUNCTIONS, DEFAULT_TEMPLATES, generateUUID, HighlightDirection, MatchingOptions, minimallyScrollTo, ResultCallback, TrivialComponent} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import {highlightMatches} from "./util/highlight";

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
	entryRenderingFunction?: (entry: E, depth: number) => string,

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
	lazyChildrenQueryFunction?: (entry: E, resultCallback: ResultCallback<E>) => void,

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
	expandOnSelection?: boolean,

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
	 * When highlighting/selecting nodes, they sometimes need to get revealed to the user by scrolling. This option specifies the corresponding scroll container.
	 */
	scrollContainer?: Element | JQuery<Element>,

	/**
	 * The indentation in pixels used.
	 *
	 * If null, use the default value given by the CSS rules.
	 * If number, indent each depth/level by the given number of pixels.
	 * If array, indent each depth by the corresponding value in the array.
	 * If function, indent each depth by the result of the given function (with depth starting at 0).
	 */
	indentation?: null | number | number[] | ((depth: number) => number)
}

class EntryWrapper<E> {
	public readonly entry: E;
	public $element: JQuery;
	public children: EntryWrapper<E>[];
	public readonly depth: number;
	private _id: string | number;

	constructor(entry: E, public readonly parent: EntryWrapper<E>, private config: TrivialTreeBoxConfig<E> & {
		elementFactory?: (entry: EntryWrapper<E>) => JQuery
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

	public lazyLoadChildren(resultCallback: ResultCallback<E>) {
		return this.config.lazyChildrenQueryFunction(this.entry, (entries: E[]) => {
			this.children = entries.map(child => new EntryWrapper(child, this, this.config));
			resultCallback(entries);
		});
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

	public get expanded() {
		return (this.entry as any)[this.config.expandedProperty] || false;
	}

	public set expanded(expanded: boolean) {
		(this.entry as any)[this.config.expandedProperty] = expanded;
		if (this.$element) {
			this.$element.toggleClass("expanded", expanded);
		}
	}
}

export class TrivialTreeBox<E> implements TrivialComponent {

	private config: TrivialTreeBoxConfig<E>;

	public readonly onSelectedEntryChanged = new TrivialEvent<E>(this);
	public readonly onNodeExpansionStateChanged = new TrivialEvent<E>(this);

	private $componentWrapper: JQuery;
	private $tree: JQuery;

	private entries: EntryWrapper<E>[];
	private selectedEntryId: string | number;
	private highlightedEntry: EntryWrapper<E>;

	constructor($container: JQuery | Element, options: TrivialTreeBoxConfig<E> = {}) {
		let defaultConfig: TrivialTreeBoxConfig<E> = {
			idFunction: (entry: E) => entry ? (entry as any).id : null,
			childrenProperty: "children",
			lazyChildrenFlag: "hasLazyChildren",
			lazyChildrenQueryFunction: function (node: E, resultCallback: Function) {
				resultCallback((node as any).children || []);
			},
			expandedProperty: 'expanded',
			entryRenderingFunction: function (entry: E, depth: number) {
				const defaultRenderers = [DEFAULT_RENDERING_FUNCTIONS.icon2Lines, DEFAULT_RENDERING_FUNCTIONS.iconSingleLine];
				const renderer = defaultRenderers[Math.min(depth, defaultRenderers.length - 1)];
				return renderer(entry);
			},
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
			expandOnSelection: false, // open expandable nodes when they are selected
			enforceSingleExpandedPath: false, // only one path is expanded at any time
			scrollContainer: $($container),
			indentation: null
		};
		this.config = $.extend(defaultConfig, options);

		this.$componentWrapper = $('<div class="tr-treebox"></div>').appendTo($container);
		this.$componentWrapper.toggleClass("hide-expanders", !this.config.showExpanders);

		this.updateEntries(this.config.entries || []);

		this.setSelectedEntryById(this.config.selectedEntryId);
	}

	private createEntryElement(entry: EntryWrapper<E>) {
		const $outerEntryWrapper = $(`<div class="tr-tree-entry-outer-wrapper ${(entry.isLeaf() ? '' : 'has-children')}" data-id="${entry.id}"></div>`);
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

	private create$ChildrenWrapper(entry: EntryWrapper<E>): JQuery {
		let $childrenWrapper = entry.$element.find('>.tr-tree-entry-children-wrapper');
		if ($childrenWrapper[0] == null) {
			$childrenWrapper = $('<div class="tr-tree-entry-children-wrapper"></div>')
				.appendTo(entry.$element);
			if (entry.children != null) {
				if (entry.expanded) {
					for (let i = 0; i < entry.children.length; i++) {
						this.createEntryElement(entry.children[i]).appendTo($childrenWrapper);
					}
				}
			} else {
				$childrenWrapper.hide().append(this.config.spinnerTemplate);
			}
		}
		return $childrenWrapper;
	}

	private setNodeExpanded(node: EntryWrapper<E>, expanded: boolean, animate: boolean) {
		let wasExpanded = node.expanded;

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
					this.setNodeExpanded(currentlyExpandedNode, false, true);
				}
			}
		}

		node.expanded = expanded;

		if (node.$element) {
			let nodeHasUnrenderedChildren = (node: EntryWrapper<E>) => {
				return node.children && node.children.some((child: EntryWrapper<E>) => {
					return !child.$element || !$.contains(document.documentElement, child.$element[0]);
				});
			};

			if (expanded && node.children == null) {
				node.lazyLoadChildren((children: E[]) => this.setChildren(node, children));
			} else if (expanded && nodeHasUnrenderedChildren(node)) {
				this.renderChildren(node);
			}
			if (expanded) {
				this.minimallyScrollTo(node.$element);
			}

			const childrenWrapper = node.$element.find("> .tr-tree-entry-children-wrapper");
			if (expanded) {
				if (animate && this.config.animationDuration > 0) {
					childrenWrapper.slideDown(this.config.animationDuration);
				} else {
					childrenWrapper.css("display", "block"); // show() does not do this if the node is not attached
				}
			} else {
				if (animate && this.config.animationDuration > 0) {
					childrenWrapper.slideUp(this.config.animationDuration);
				} else {
					childrenWrapper.hide();
				}
			}
		}

		if (!!wasExpanded != !!expanded) {
			this.onNodeExpansionStateChanged.fire(node.entry);
		}
	}

	private setChildren(parentEntryWrapper: EntryWrapper<E>, children: E[]) {
		parentEntryWrapper.children = children.map(child => this.createEntryWrapper(child, parentEntryWrapper));
		this.renderChildren(parentEntryWrapper);
	}

	private renderChildren(node: EntryWrapper<E>) {
		const $childrenWrapper = node.$element.find('> .tr-tree-entry-children-wrapper');
		$childrenWrapper[0].innerHTML = ''; // remove the spinner!
		if (node.children && node.children.length > 0) {
			node.children.forEach(child => {
				child.render().appendTo($childrenWrapper);
			});
		} else {
			node.$element.removeClass('has-children expanded');
		}
	}

	public updateEntries(newEntries: E[]) {
		newEntries = newEntries || [];
		this.highlightedEntry = null;
		this.entries = newEntries.map(e => this.createEntryWrapper(e, null));

		this.$tree && this.$tree.remove();
		this.$tree = $('<div class="tr-tree-entryTree"></div>');
		this.$tree.on("mousedown", ".tr-tree-entry-and-expander-wrapper", (e) => {
			let entryWrapper = ($(e.target).closest(".tr-tree-entry-and-expander-wrapper")[0] as any).trivialEntryWrapper as EntryWrapper<E>;
			this.setSelectedEntry(entryWrapper, null, true);
		}).on("mouseenter", ".tr-tree-entry-and-expander-wrapper", (e) => {
			let entryWrapper = ($(e.target).closest(".tr-tree-entry-and-expander-wrapper")[0] as any).trivialEntryWrapper as EntryWrapper<E>;
			this.setHighlightedEntry(entryWrapper);
		}).on("mouseleave", ".tr-tree-entry-and-expander-wrapper", (e) => {
			if (!$((e as any).toElement).is('.tr-tree-entry-outer-wrapper')) {
				this.setHighlightedEntry(null);
			}
		}).on("mousedown", ".tr-tree-expander", () => {
			return false; // prevent selection!
		}).on("click", ".tr-tree-expander", (e) => {
			let entryWrapper = ($(e.target).closest(".tr-tree-entry-and-expander-wrapper")[0] as any).trivialEntryWrapper  as EntryWrapper<E>;
			this.setNodeExpanded(entryWrapper, !entryWrapper.expanded, true);
		});

		if (this.entries.length > 0) {
			this.entries.forEach(entry => entry.render().appendTo(this.$tree))
		} else {
			this.$tree.append(this.config.noEntriesTemplate);
		}
		this.$tree.appendTo(this.$componentWrapper);

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

	private findEntries(filterFunction: ((node: EntryWrapper<E>) => boolean)) {
		let findEntriesInSubTree = (node: EntryWrapper<E>, listOfFoundEntries: EntryWrapper<E>[]) => {
			if (filterFunction.call(this, node)) {
				listOfFoundEntries.push(node);
			}
			if (node.children) {
				for (let i = 0; i < node.children.length; i++) {
					const child = node.children[i];
					findEntriesInSubTree(child, listOfFoundEntries);
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
		this.setHighlightedEntry(entry); // it makes no sense to select an entry and have another one still highlighted.
		if (fireEvents) {
			this.fireChangeEvents(entry, originalEvent);
		}
		if (entry && this.config.expandOnSelection) {
			this.setNodeExpanded(entry, true, true);
		}
	}

	public setSelectedEntryById(nodeId: number | string, fireEvents = false) {
		this.setSelectedEntry(this.findEntryById(nodeId), null, fireEvents);
	}

	private minimallyScrollTo($entryWrapper: JQuery) {
		minimallyScrollTo(this.config.scrollContainer, $entryWrapper);
	}

	private markSelectedEntry(entry: EntryWrapper<E>) {
		this.$tree.find(".tr-selected-entry").removeClass("tr-selected-entry");
		if (entry && entry.$element) {
			const $entryWrapper = entry.$element.find('>.tr-tree-entry-and-expander-wrapper');
			$entryWrapper.addClass("tr-selected-entry");
		}
	}

	private fireChangeEvents(entry: EntryWrapper<E>, originalEvent: unknown) {
		this.$componentWrapper.trigger("change");
		this.onSelectedEntryChanged.fire(entry.entry, originalEvent);
	}

	public selectNextEntry(direction: HighlightDirection, originalEvent?: unknown, fireEvents = false) {
		const nextVisibleEntry = this.getNextVisibleEntry(this.getSelectedEntryWrapper(), direction);
		if (nextVisibleEntry != null) {
			this.setSelectedEntry(nextVisibleEntry, originalEvent, fireEvents);
		}
	}

	private setHighlightedEntry(entry: EntryWrapper<E>) {
		if ((entry == null) !== (this.highlightedEntry == null) || (entry && entry.id) !== (this.highlightedEntry && this.highlightedEntry.id)) {
			this.highlightedEntry = entry != null ? this.findEntryById(entry.id) : null;
			this.$tree.find('.tr-highlighted-entry').removeClass('tr-highlighted-entry');
			if (this.highlightedEntry != null && this.highlightedEntry.$element) {
				const $entry = this.highlightedEntry.$element.find('>.tr-tree-entry-and-expander-wrapper');
				$entry.addClass('tr-highlighted-entry');
				this.minimallyScrollTo($entry);
			} else {
				const selectedEntry = this.getSelectedEntryWrapper();
				if (selectedEntry) {
					this.highlightedEntry = selectedEntry;
				}
			}
		}
	}

	public setHighlightedEntryById(entryId: number | string) {
		this.setHighlightedEntry(this.findEntryById(entryId))
	}

	private getNextVisibleEntry(currentEntry: EntryWrapper<E>, direction: HighlightDirection, onlyEntriesWithTextMatches: boolean = false) {
		let newSelectedElementIndex: number;
		const visibleEntriesAsList = this.findEntries((entry) => {
			if (!entry.$element) {
				return false;
			} else {
				if (onlyEntriesWithTextMatches) {
					return entry.$element.is(':visible') && entry.$element.has('>.tr-tree-entry-and-expander-wrapper .tr-highlighted-text').length > 0;
				} else {
					return entry.$element.is(':visible') || entry === currentEntry;
				}
			}
		});
		if (visibleEntriesAsList == null || visibleEntriesAsList.length == 0) {
			return null;
		} else if (currentEntry == null && direction > 0) {
			newSelectedElementIndex = -1 + direction;
		} else if (currentEntry == null && direction < 0) {
			newSelectedElementIndex = visibleEntriesAsList.length + direction;
		} else {
			const currentSelectedElementIndex = visibleEntriesAsList.indexOf(currentEntry);
			newSelectedElementIndex = (currentSelectedElementIndex + visibleEntriesAsList.length + direction) % visibleEntriesAsList.length;
		}
		return visibleEntriesAsList[newSelectedElementIndex];
	}

	public highlightTextMatches(searchString: string) {
		this.$tree.detach();
		for (let i = 0; i < this.entries.length; i++) {
			const entry = this.entries[i];
			const $entryElement = entry.$element.find('.tr-tree-entry');
			highlightMatches($entryElement, searchString, this.config.matchingOptions)
		}
		this.$tree.appendTo(this.$componentWrapper);
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

	public highlightNextEntry(direction: HighlightDirection) {
		const nextVisibleEntry = this.getNextVisibleEntry(this.highlightedEntry || this.getSelectedEntryWrapper(), direction);
		if (nextVisibleEntry != null) {
			this.setHighlightedEntry(nextVisibleEntry);
		}
	}

	public highlightNextMatchingEntry(direction: HighlightDirection) {
		const nextMatchingEntry = this.getNextVisibleEntry(this.highlightedEntry || this.getSelectedEntryWrapper(), direction, true);
		if (nextMatchingEntry != null) {
			this.setHighlightedEntry(nextMatchingEntry);
		}
	}

	public selectNextMatchingEntry(direction: HighlightDirection, fireEvents = false) {
		const nextMatchingEntry = this.getNextVisibleEntry(this.highlightedEntry, direction, true);
		if (nextMatchingEntry != null) {
			this.setSelectedEntry(nextMatchingEntry, null, fireEvents);
		}
	}

	public getHighlightedEntry() {
		return this.highlightedEntry && this.highlightedEntry.entry;
	}

	public setHighlightedNodeExpanded(expanded: boolean) {
		if (!this.highlightedEntry || this.highlightedEntry.isLeaf()) {
			return false;
		} else {
			let wasExpanded = this.highlightedEntry.expanded;
			this.setNodeExpanded(this.highlightedEntry, expanded, true);
			return !wasExpanded != !expanded;
		}
	}

	public updateChildren(parentNodeId: string | number, children: E[]) {
		const node = this.findEntryById(parentNodeId);
		if (node) {
			this.setChildren(node, children);
		} else {
			console.error("Could not set the children of unknown node with id " + parentNodeId);
		}
	};

	public updateNode(node: E) {
		const oldNode = this.findEntryById(this.config.idFunction(node));
		let parentNode = oldNode.parent;
		let newEntryWrapper = this.createEntryWrapper(node, parentNode);
		if (parentNode) {
			parentNode.children[parentNode.children.indexOf(oldNode)] = newEntryWrapper;
		} else {
			this.entries[this.entries.indexOf(oldNode)] = newEntryWrapper;
		}
		newEntryWrapper.render().insertAfter(oldNode.$element);
		oldNode.$element.remove();
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
			$childrenWrapper[0].innerHTML = ''; // remove the spinner!
		}
		parentNode.children.push(newEntryWrapper);
		newEntryWrapper.render().appendTo($childrenWrapper);
		parentNode.$element.addClass('has-children');
	};

	public addOrUpdateNode(parentNodeId: number | string, node: E) {
		let existingNode = this.findEntryById(this.config.idFunction(node));
		if (existingNode != null) {
			this.updateNode(node);
		} else {
			this.addNode(parentNodeId, node);
		}
	}

	public destroy() {
		this.$componentWrapper.remove();
	};

	getMainDomElement(): Element {
		return this.$componentWrapper[0];
	}
}
