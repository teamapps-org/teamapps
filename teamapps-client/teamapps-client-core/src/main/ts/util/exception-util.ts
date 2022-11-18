
export function logException(e: any, additionalString?: string) {
	console.error(e, e.stack, additionalString);
}