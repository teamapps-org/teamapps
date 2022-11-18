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