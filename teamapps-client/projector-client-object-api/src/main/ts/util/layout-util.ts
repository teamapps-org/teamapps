export function outerWidthIncludingMargins(el: HTMLElement) {
	let width = el.offsetWidth;
	const style = getComputedStyle(el);
	width += parseInt(style.marginLeft) + parseInt(style.marginRight);
	return width;
}

export function outerHeightIncludingMargins(el: HTMLElement) {
	let height = el.offsetHeight;
	const style = getComputedStyle(el);
	height += parseInt(style.marginTop) + parseInt(style.marginBottom);
	return height;
}