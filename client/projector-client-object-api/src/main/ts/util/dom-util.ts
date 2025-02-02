export function elementIndex(node: Element) {
	let i = 0;
	while ((node = node.previousElementSibling) != null) {
		i++;
	}
	return i;
}

export function insertAtIndex($parent: Element, $child: Element, index: number) {
	let effectiveIndex = Math.min($parent.childElementCount, index);
	if (effectiveIndex === 0) {
		$parent.prepend($child);
	} else if (effectiveIndex === $parent.childElementCount) {
		$parent.insertAdjacentElement('beforeend', $child);
	} else {
		$parent.children[effectiveIndex].insertAdjacentElement('beforebegin', $child);
	}
}

export function prependChild(parent: Node, child: Node) {
	if (parent.childNodes.length > 0) {
		parent.insertBefore(child, parent.firstChild);
	} else {
		parent.appendChild(child);
	}
}

export function insertBefore(newNode: Node, referenceNode: Node) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode);
}

export function insertAfter(newNode: Node, referenceNode: Node) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling /* may be null ==> inserted at end!*/);
}

export function closestAncestor(el: HTMLElement, selector: string, includeSelf = false, $root: Element = document.body) {
	let currentNode: HTMLElement = (includeSelf ? el : el.parentNode) as HTMLElement;
	while (currentNode) {
		if (currentNode.matches(selector)) {
			return currentNode;
		}
		if (currentNode == $root) {
			break;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
}

export function closestAncestorMatching(el: Element, predicate: (ancestor: Element) => boolean, includeSelf = false) {
	let currentNode: Element = (includeSelf ? el : el.parentNode) as Element;
	while (currentNode) {
		if (predicate(currentNode)) {
			return currentNode;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
}

export function isDescendantOf(child: Element, potentialAncestor: Element, includeSelf = false) {
	let currentNode = includeSelf ? child : child.parentNode;
	while (currentNode) {
		if (currentNode == potentialAncestor) {
			return currentNode;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
}