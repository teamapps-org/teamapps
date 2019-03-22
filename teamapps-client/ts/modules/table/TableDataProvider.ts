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
import {UiHierarchicalClientRecordConfig} from "../../generated/UiHierarchicalClientRecordConfig";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {getHighestSeverity} from "../micro-components/FieldMessagesPopper";
import {UiFieldMessageSeverity} from "../../generated/UiFieldMessageSeverity";
import {UiTableClientRecordConfig} from "../../generated/UiTableClientRecordConfig";

export interface TableDataProviderItem extends UiTableClientRecordConfig {
	children: TableDataProviderItem[];
	depth: number;
	parentId: number;
	expanded: boolean;
}

export class TableDataProvider {
	public onDataLoading = new Slick.Event();

	private static LOOKAHAED = 50;
	private timerId: number = null;

	private data: TableDataProviderItem[];
	private numberOfRootNodes: number;

	constructor(
		data: UiHierarchicalClientRecordConfig[],
		private dataRequestCallback: Function) {
		this.data = this.prepareData(data);
		this.numberOfRootNodes = this.data.filter(item => !item.parentId).length;
	}

	private prepareData(flatData: any[], parentId: number | string = 0, depth = 0): TableDataProviderItem[] {
		let itemsById: { [index: number]: any } = {};
		for (let i = 0; i < flatData.length; i++) {
			let item = flatData[i];
			itemsById[item.id] = item;
		}

		for (let i = 0; i < flatData.length; i++) {
			let item = flatData[i];
			if (item.parentId) {
				let parentItem = itemsById[item.parentId];
				if (parentItem) {
					parentItem.children || (parentItem.children = []);
					parentItem.children.push(item);
				}
			}
		}

		let resultData = flatData.filter(item => item.parentId == parentId || (!item.parentId && !parentId));

		function setDepth(node: TableDataProviderItem, depth: number) {
			node.depth = depth;
			if (node.children && node.children.length > 0) {
				node.children.forEach(child => setDepth(child, depth + 1));
			}
		}

		for (let i = 0; i < resultData.length; i++) {
			let rootNode = resultData[i];
			setDepth(rootNode, depth);
		}

		for (let i = resultData.length - 1; i >= 0; i--) { // iterate backwards since we are manipulating the array we are iterating on...
			const tableDataProviderItem = resultData[i];

			if (this.isRowExpanded(tableDataProviderItem) && tableDataProviderItem.children && tableDataProviderItem.children.length > 0) {
				let visibleDescendants = this.getListOfVisibleDescendants(resultData[i]);
				resultData.splice.apply(resultData, ([i + 1, 0] as any[]).concat(visibleDescendants));
			}
		}

		return resultData;
	}

	private isRowExpanded(tableDataProviderItem: any): boolean {
		return !!tableDataProviderItem.expanded;
	}

	public toggleRowExpanded(rowIndex: number) {
		let item = this.data[rowIndex];
		if (!item) {
			return;
		}
		if (!this.isRowExpanded(item)) {
			item.expanded = true;
			let visibleDescendants = this.getListOfVisibleDescendants(this.data[rowIndex]);
			this.data.splice.apply(this.data, ([rowIndex + 1, 0] as any).concat(visibleDescendants));
		} else {
			item.expanded = false;
			this.data.splice(rowIndex + 1, this.findEndOfChildrenIndex(rowIndex) - (rowIndex + 1));
		}
	}

	private getListOfVisibleDescendants(item: TableDataProviderItem): TableDataProviderItem[] {
		let visibleDescendants: TableDataProviderItem[] = [];

		let addNodesAndExpandedDescendants = (nodes: any[]) => {
			for (let child of nodes) {
				visibleDescendants.push(child);
				if (child.children && this.isRowExpanded(child)) {
					addNodesAndExpandedDescendants(child.children);
				}
			}
		};

		item.children && addNodesAndExpandedDescendants(item.children);
		return visibleDescendants;
	}

	/**
	 * @param parentItemRowIndex
	 * @returns {number} the index of the first non-child row following the node specified by parentItemRowIndex
	 */
	private findEndOfChildrenIndex(parentItemRowIndex: number) {
		let parentNode = this.data[parentItemRowIndex];
		if (!parentNode) { // seems not to be initialized yet, so no children
			return parentItemRowIndex + 1;
		}
		let parentIds = [parentNode.id];
		for (var followingRowIndex = parentItemRowIndex + 1; followingRowIndex < this.data.length; followingRowIndex++) {
			let row = this.data[followingRowIndex];
			if (!row) {
				break;
			}
			let rowParentId = row.parentId;
			if (rowParentId !== 0 && rowParentId === parentIds[parentIds.length - 1]) {
				// same level as before
			} else if (rowParentId === this.data[followingRowIndex - 1].id) {
				// nested row
				parentIds.push(rowParentId);
			} else {
				// non-sibling and non-nested => higher level.
				while (parentIds.length > 0 && parentIds[parentIds.length - 1] != rowParentId) {
					parentIds.pop();
				}
				if (parentIds.length === 0) {
					break;
				}
			}
		}
		return followingRowIndex;
	};

	/**
	 * This method is called by SlickGrid.
	 */
	public getLength(): number {
		return this.data.length;
	}

	public setTotalNumberOfRootNodes(totalNumberOfRootNodes: number) {
		let numberOfVisibleNonRootChildren = this.data.filter(node => node && node.depth > 0).length;
		this.numberOfRootNodes = totalNumberOfRootNodes;
		this.data.length = totalNumberOfRootNodes + numberOfVisibleNonRootChildren;
		// TODO #events Fire event because data.length has changed!
	}

	public getTotalNumberOfRootNodes() {
		return this.numberOfRootNodes;
	}

	/**
	 * This method is called by SlickGrid.
	 */
	public getItem(index: number): TableDataProviderItem {
		return this.data[index];  // yep, that's ok!
	}

	/**
	 * This method is called by SlickGrid.
	 */
	public getItemMetadata(index: number): Slick.RowMetadata<any> {
		if (!this.data[index]) {
			return null;
		} else {
			const record = this.data[index];
			return {
				cssClasses: record.bold ? "text-bold" : null
			};
		}
	}

	public clear(): void {
		this.data = [];
	}

	public updateRootNodeData(startRootNodeIndex: number, newData: any[]) {
		let rowIndex = 0;
		let rootNodeIndex = 0;
		while (rootNodeIndex < startRootNodeIndex) {
			rowIndex = this.findEndOfChildrenIndex(rowIndex);
			rootNodeIndex++;
		}

		let insertRowIndex = rowIndex;
		let numberOfRootNodesInNewData = newData.filter(item => !item._parentId).length;
		let newPreparedData = this.prepareData(newData);

		for (let i = 0; i < numberOfRootNodesInNewData; i++) {
			rowIndex = this.findEndOfChildrenIndex(rowIndex);
		}

		this.data.splice.apply(this.data, (<any[]>[insertRowIndex, rowIndex - insertRowIndex]).concat(newPreparedData));
	}

	setChildrenData(parentRecordId: number, tableData: any[]) {
		let parent = this.getNodeById(parentRecordId);

		if (parent) {
			let preparedData = this.prepareData(tableData, parentRecordId, parent.depth + 1).filter(item => item.parentId === parentRecordId); // throw everything away that is not below the parent node!
			let parentItemRowIndex = this.findVisibleRowIndexById(parentRecordId);
			if (parentItemRowIndex != null) {
				let endOfCurrentChildrenIndex = this.findEndOfChildrenIndex(parentItemRowIndex);
				this.data.splice.apply(this.data, (<any[]>[parentItemRowIndex + 1, endOfCurrentChildrenIndex - parentItemRowIndex - 1]).concat(preparedData));
			}
			parent.children = preparedData;
		}
	}

	public removeItem(recordId: number) {
		let rowIndex = this.findVisibleRowIndexById(recordId);
		this.removeItemInternal(recordId, rowIndex);
	}

	public removeItems(recordIds: number[]) {
		let rowIndexesByIds = this.findVisibleRowIndexesByIds(recordIds);
		Object.keys(rowIndexesByIds).forEach(id => {
			let idAsNumber = Number(id);
			this.removeItemInternal(idAsNumber, rowIndexesByIds[idAsNumber]);
		});
	}

	public deleteItems(recordIds: number[]) {
		let rowIndexesByIds = this.findVisibleRowIndexesByIds(recordIds);
		const rowIndexes: number[] = Object.values(rowIndexesByIds).sort((a, b) => b-a);
		rowIndexes.forEach(rowIndex => this.data.splice(rowIndex, 1));
		this.setTotalNumberOfRootNodes(this.data.length);
	}

	private removeItemInternal(recordId: number, rowIndex: number) {
		let node: TableDataProviderItem;
		if (rowIndex !== null) {
			node = this.data[rowIndex];
		} else { // the node is a hidden child or does not exist...
			node = this.getNodeById(recordId);
		}

		if (!node) {
			return;
		}

		if (rowIndex != null) {
			let deleteStart: number;
			if (node.parentId != null) { // delete the row itself only if it is not a root node!
				deleteStart = rowIndex;
			} else { // otherwise, only delete its children and set the row back to undefined
				deleteStart = rowIndex + 1;
				this.data[rowIndex] = undefined;
			}
			const deleteCount = this.findEndOfChildrenIndex(rowIndex) - (deleteStart);
			this.data.splice(deleteStart, deleteCount);
		}

		if (node.parentId) {
			let parentNode = this.getNodeById(node.parentId);
			parentNode.children = parentNode.children.filter(node => node.id != recordId);
		}
	}


	public ensureData(firstVisibleRowIndex: number, lastVisibleRowIndex: number) {
		if (firstVisibleRowIndex > lastVisibleRowIndex) {
			return;
		}

		let from = Math.max(firstVisibleRowIndex - TableDataProvider.LOOKAHAED, 0);
		let to = lastVisibleRowIndex + TableDataProvider.LOOKAHAED;
		if (this.data.length) {
			to = Math.min(to, this.data.length - 1);
		}

		while (this.data[from] !== undefined && from < to) {
			from++;
		}
		while (this.data[to] !== undefined && from < to) {
			to--;
		}

		if (firstVisibleRowIndex > to || lastVisibleRowIndex < from) { // not really necessary to load anything
			return;
		}

		if (from == to && this.data[to] !== undefined) {
			return;
		}

		if (this.timerId != null) {
			clearTimeout(this.timerId);
		}

		this.timerId = window.setTimeout(() => {
			for (let i = from; i <= to; i++) {
				this.data[i] = null; // null indicates a 'requested but not available yet'
			}

			let length = to - from + 1;
			this.dataRequestCallback(from, length);
		}, 100);
	}

	public findVisibleRowIndexById(id: number): number {
		if (id === null || id === undefined) {
			return null;
		}
		for (let i = 0; i < this.data.length; i++) {
			const entry = this.data[i];
			if (entry != null && entry.id === id) {
				return i;
			}
		}
		return null;
	}

	public findVisibleRowIndexesByIds(ids: number[]): {[id: number]: number} {
		const idsSet = ids.reduce((set, id) => {
			set[id] = true;
			return set;
		}, {} as { [id: string]: true });
		const rowIndexesById: {[id: number]: number} = {};
		for (let i = 0; i < this.data.length; i++) {
			const entry = this.data[i];
			if (entry != null && idsSet[entry.id]) {
				rowIndexesById[entry.id] = i;
			}
		}
		return rowIndexesById;
	}

	public getNodeById(id: number): TableDataProviderItem {
		let rowIndex = this.findVisibleRowIndexById(id);
		if (rowIndex != null) {
			return this.data[rowIndex];
		} else {
			return this.findNodes(item => item && item.id === id)[0];
		}
	}

	private findNodes(predicate: (item: TableDataProviderItem) => boolean): TableDataProviderItem[] {
		let rootNodes = this.data.filter(node => node && !node.parentId);

		let foundNodes: TableDataProviderItem[] = [];

		function find(node: TableDataProviderItem) {
			if (predicate.call(null, node)) {
				foundNodes.push(node);
			}
			if (node.children && node.children.length > 0) {
				node.children.forEach(child => find(child));
			}
		}

		for (let i = 0; i < rootNodes.length; i++) {
			let rootNode = rootNodes[i];
			find(rootNode);
		}

		return foundNodes;
	}

	public setCellMarked(recordId: number, propertyName: string, marked: boolean) {
		let node = this.getNodeById(recordId);
		if (node == null) {
			return;
		}
		if (node.markings == null) {
			node.markings = [];
		}
		node.markings = node.markings.filter(existingPropertyName => existingPropertyName !== propertyName);
		if (marked) {
			node.markings.push(propertyName);
		}
	}

	public clearAllFieldMarkings() {
		this.findNodes(() => true).forEach(node => node.markings = null);
	}

	public setCellMessages(recordId: number, fieldName: string, messages: UiFieldMessageConfig[]) {
		const item = this.getNodeById(recordId);
		if (item == null) {
			return;
		}
		if (item.messages == null) {
			item.messages = {};
		}
		item.messages[fieldName] = messages;
	}

	public clearAllCellMessages() {
		this.findNodes(() => true).forEach(node => node.messages = null);
	}

	insertRows(index: number, data: UiTableClientRecordConfig[]) {
		if (index < 0) {
			index = 0;
		} else if (index > this.data.length) {
			index = this.data.length;
		}
		this.data.splice(index, 0, ...this.prepareData(data, 0));
		this.numberOfRootNodes += data.length;
	}

	updateNode(record: UiTableClientRecordConfig) {
		const rowIndex = this.findVisibleRowIndexById(record.id);
		if (rowIndex != null) {
			this.data[rowIndex] = this.prepareData([record])[0];
		}
	}
}
