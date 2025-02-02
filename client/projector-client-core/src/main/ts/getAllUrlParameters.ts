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