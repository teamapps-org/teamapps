export function capitalizeFirstLetter(string: string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}

export function generateUUID(startingWithCharacter?: boolean) {
	return (startingWithCharacter ? 'u-' : '') + 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		const randomHex = Math.random() * 16 | 0;
		const v = c == 'x' ? randomHex : (randomHex & 0x3 | 0x8);
		return v.toString(16);
	});
}