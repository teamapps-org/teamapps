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