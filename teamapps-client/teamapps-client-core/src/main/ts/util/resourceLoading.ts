export function loadJavaScript(url: string, id: string = null) {
	if (id != null && document.getElementById(id) != null) {
		return;
	}
	return new Promise<void>((resolve, reject) => {
		const scriptElement = document.createElement('script');
		scriptElement.src = url;
		scriptElement.addEventListener('load', ev => resolve(), false);
		scriptElement.addEventListener('error', ev => reject(ev.message), false);
		document.getElementsByTagName('head')[0].appendChild(scriptElement);
	});
}

export function loadCss(url: string, id: string = null) {
	if (id != null && document.getElementById(id) != null) {
		return;
	}
	return new Promise<void>((resolve, reject) => {
		const linkElement = document.createElement('link');
		linkElement.rel = 'stylesheet';
		linkElement.type = 'text/css';
		linkElement.href = url;
		linkElement.addEventListener('load', ev => resolve(), false);
		linkElement.addEventListener('error', ev => reject(ev.message), false);
		document.getElementsByTagName('head')[0].appendChild(linkElement);
	});
}