export function parseHtml<E extends HTMLElement>(htmlString: string): E {
	htmlString = htmlString.trim();
	let tmpl = document.createElement('template');
	tmpl.innerHTML = htmlString;
	if (tmpl.content.childElementCount > 1) {
		throw `htmlString corresponds to multiple top level dom nodes! html: ${htmlString}`;
	}
	return tmpl.content.cloneNode(true).firstChild as E;
}

export function parseSvg<E extends Element>(htmlString: string): E {
	const svgPrefix = "<svg ";
	if (!htmlString.startsWith("<svg ")) {
		throw "svg string needs to start with '" + svgPrefix + "'";
	}
	let tagStartCount = (htmlString.match(/<\w+/g) || []).length;
	let tagEndCount = (htmlString.match(/<\//g) || []).length;
	if (tagStartCount !== tagEndCount) {
		throw "SVG strings need to have explicit closing tags! " + htmlString;
	}
	if (htmlString.indexOf("xmlns=\"") === -1) {
		// for browser compatibility reasons, make sure to add the SVG namespace!
		htmlString = htmlString.substring(0, svgPrefix.length) + 'xmlns="http://www.w3.org/2000/svg" ' + htmlString.substring(svgPrefix.length);
	}
	const node: E = new DOMParser().parseFromString(htmlString, 'image/svg+xml').getRootNode() as E;
	node.remove(); // detach from DOMParser <body>!
	return node;
}