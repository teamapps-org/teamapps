export function parseHtml<E extends HTMLElement>(htmlString: string): E {
	let tmpl = document.createElement('template');
	tmpl.innerHTML = htmlString;
	return tmpl.content.cloneNode(true).firstChild as E;
}