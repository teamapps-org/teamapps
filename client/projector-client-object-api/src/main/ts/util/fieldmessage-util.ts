import {FieldMessageSeverities, FieldMessageSeverity} from "../generated";

let severitiesSorted = [
	FieldMessageSeverities.INFO,
	FieldMessageSeverities.SUCCESS,
	FieldMessageSeverities.WARNING,
	FieldMessageSeverities.ERROR
];

export function compareSeverities(s1: FieldMessageSeverity, s2: FieldMessageSeverity) {
	if (s1 === s2) {
		return 0;
	} else if (s1 == null) {
		return -1;
	} else if (s2 == null) {
		return 1;
	} else {
		return severitiesSorted.indexOf(s1) - severitiesSorted.indexOf(s2);
	}
}

export function highestSeverity(severities: FieldMessageSeverity[]) {
	return severities.reduce((highestSeverity, sev) => compareSeverities(sev, highestSeverity) > 0 ? sev : highestSeverity, null as FieldMessageSeverity | null);
}