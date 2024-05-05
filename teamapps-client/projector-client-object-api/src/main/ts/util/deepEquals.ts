export function deepEquals(x: any, y: any): boolean {
	if (x != null && y != null && typeof x === 'object' && typeof x === typeof y) {
		if (Array.isArray(x)) {
			return x.length === y.length && x.every((xi, i) => deepEquals(x[i], y[i]));
		} else {
			return Object.keys(x).length === Object.keys(y).length &&
				Object.keys(x).every(key => deepEquals(x[key], y[key]));
		}
	} else {
		return x === y
			|| ((x == null) && (y == null)); // make no difference between undefined and null!
	}
}