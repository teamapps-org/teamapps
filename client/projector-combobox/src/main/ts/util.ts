export type NodeWithChildren<T> = T & { __children?: NodeWithChildren<T>[] };
export type TreeEntry = any & { __children?: TreeEntry[], _isFreeTextEntry?: boolean };

export function buildObjectTree<T extends object>(nodes: T[], idPropertyName: string, parentIdPropertyName: string): NodeWithChildren<T>[] {
	if (nodes == null) {
		return [];
	}
	nodes = nodes.map((node: T) => {
		return {...(node as object)};
	}) as T[];

	const rootNodes: TreeEntry[] = [];
	const nodesById: { [id: string]: TreeEntry } = {};
	for (let i = 0; i < nodes.length; i++) {
		const node = nodes[i];
		nodesById[(node as any)[idPropertyName]] = node;
	}
	// place children under parents
	for (let i = 0; i < nodes.length; i++) {
		const node = nodes[i];
		let parentId = (node as any)[parentIdPropertyName];
		if (parentId != null) {
			const parent = nodesById[parentId];
			if (parent != null) {
				if (!parent.__children) {
					parent.__children = [];
				}
				parent.__children.push(node);
			} else {
				rootNodes.push(node);
			}
		} else {
			rootNodes.push(node);
		}
	}
	return rootNodes;
}

export function buildTreeEntryHierarchy(entryList: any[], idPropertyName: string, parentIdPropertyName: string): TreeEntry[] {
	const rootEntries: TreeEntry[] = [];
	const entriesById: { [id: string]: TreeEntry } = {};
	if (entryList) {
		for (let i = 0; i < entryList.length; i++) {
			const entry = entryList[i];
			entriesById[idPropertyName ? entry[idPropertyName] : entry] = entry;
		}
	}
	// place children under parents
	for (let i = 0; i < entryList.length; i++) {
		const entry = entryList[i];
		let parentId = entry[parentIdPropertyName];
		if (parentId) {
			const parent = entriesById[parentId];
			if (parent != null) {
				if (!parent.__children) {
					parent.__children = [];
				}
				parent.__children.push(entry);
			} else {
				rootEntries.push(entry);
			}
		} else {
			rootEntries.push(entry);
		}
	}
	return rootEntries;
}


export function selectElementContents(domElement: Node, start?: number, end?: number) {
	if (domElement == null || !document.body.contains(domElement)) {
		return;
	}
	domElement = domElement.firstChild || domElement;
	const range = document.createRange();
	if (start == null || end == null) {
		range.selectNodeContents(domElement);
	} else {
		end = end || start;
		range.setStart(domElement, start);
		range.setEnd(domElement, end);
	}
	const sel = window.getSelection();
	try {
		sel.removeAllRanges();
	} catch (e) {
		// ignore (ie 11 problem, can be ignored even in ie 11)
	}
	sel.addRange(range);
}