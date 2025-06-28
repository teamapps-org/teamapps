export const FileItemStates = Object.freeze({
	INITIATING: "initiating",
	TOO_LARGE: "too-large",
	UPLOADING: "uploading",
	FAILED: "failed",
	DONE: "done"
});
export type FileItemState = typeof FileItemStates[keyof typeof FileItemStates]