export function createUiLocation() {
	return {
		href: location.href,
		origin: location.origin,
		protocol: location.protocol,
		host: location.host,
		hostname: location.hostname,
		port: location.port && Number(location.port),
		pathname: location.pathname ?? '',
		search: location.search ?? '',
		hash: location.hash ?? ''
	};
}

export function getAllUrlParameters() {
	const query = location.search.substring(1);
	const result = {};
	query.split("&").forEach(function (part) {
		let [key, value] = part.split("=");
		if (value != null) {
			result[key] = decodeURIComponent(value);
		}
	});
	return result;
}