import rgba from "color-rgba";

export function isVisibleColor(c: string) {
	return c != null && rgba(c)[3] > 0;
}