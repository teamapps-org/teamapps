declare module "levenshtein" {
	let Levenshtein: {
		new(s1: string, s2: string): { distance: number };
	};
	export = Levenshtein;
}
