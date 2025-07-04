import {type ClientObject, type Invokable} from "projector-client-object-api";
import {type DtoStringScript, type DtoStringScriptCommandHandler} from "./generated";

export class StringScript implements ClientObject, DtoStringScriptCommandHandler, Invokable {
	private modulePromise: Promise<any>;

	constructor(config: DtoStringScript) {
		this.modulePromise = loadModuleFromString(config.script);
	}

	async invoke(name: string, parameters: any[]) {
		return ((await this.modulePromise)[name] as Function).apply(null, parameters);
	}

	destroy(): void {
		// make typescript compiler happy
	}
}

async function loadModuleFromString(moduleCode: string) {
	const url = URL.createObjectURL(new Blob([moduleCode], {type: 'application/javascript'}));
	try {
		return await import(url);
	} finally {
		URL.revokeObjectURL(url);
	}
}