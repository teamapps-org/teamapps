document.addEventListener("fullscreenchange", fullScreenChangeHandler);
document.addEventListener("webkitfullscreenchange", fullScreenChangeHandler);
document.addEventListener("mozfullscreenchange", fullScreenChangeHandler);
document.addEventListener("MSFullscreenChange", fullScreenChangeHandler);

export function enterFullScreen(element: Element) {
	element.classList.add("fullscreen");
	if (element.requestFullscreen) {
		element.requestFullscreen();
	} else if ((element as any).msRequestFullscreen) {
		(element as any).msRequestFullscreen();
	} else if ((element as any).mozRequestFullScreen) {
		(element as any).mozRequestFullScreen();
	} else if ((element as any).webkitRequestFullscreen) {
		(element as any).webkitRequestFullscreen();
	}
}

function fullScreenChangeHandler(e: Event) {
	if (!getFullScreenElement()) {
		(e.target as Element).classList.remove("fullscreen");
	}
}

export function isFullScreen(): boolean {
	return !!getFullScreenElement();
}

function getFullScreenElement(): Element {
	return document.fullscreenElement ||
		(document as any).webkitFullscreenElement ||
		(document as any).mozFullScreenElement ||
		(document as any).msFullscreenElement;
}

export function exitFullScreen() {
	let fullScreenElement = getFullScreenElement();
	if (fullScreenElement) {
		if (document.exitFullscreen) {
			document.exitFullscreen();
		} else if ((document as any).webkitExitFullscreen) {
			(document as any).webkitExitFullscreen();
		} else if ((document as any).mozCancelFullScreen) {
			(document as any).mozCancelFullScreen();
		} else if ((document as any).msExitFullscreen) {
			(document as any).msExitFullscreen();
		}
	}
}