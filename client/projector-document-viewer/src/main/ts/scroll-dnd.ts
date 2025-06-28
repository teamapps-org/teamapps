export function enableScrollViaDragAndDrop($scrollContainer: HTMLElement) {
	function mousedownHandler(startEvent: MouseEvent) {
		$scrollContainer.style.cursor = "move";
		startEvent.preventDefault();
		let initialScrollLeft = $scrollContainer.scrollLeft;
		let initialScrollTop = $scrollContainer.scrollTop;
		let dragHandler = (e: PointerEvent) => {
			let diffX = e.pageX - startEvent.pageX;
			let diffY = e.pageY - startEvent.pageY;
			$scrollContainer.scrollLeft = initialScrollLeft - diffX;
			$scrollContainer.scrollTop = initialScrollTop - diffY;
		};
		let dropHandler = () => {
			document.removeEventListener('pointermove', dragHandler);
			document.removeEventListener('pointerup', dropHandler);
			$scrollContainer.style.cursor = "";
		};
		document.addEventListener('pointermove', dragHandler);
		document.addEventListener('pointerup', dropHandler);
	}

	$scrollContainer.addEventListener("mousedown", (e) => mousedownHandler(e));
}