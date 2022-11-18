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