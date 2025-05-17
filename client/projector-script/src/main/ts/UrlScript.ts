import {ClientObject, Invokable, ServerObjectChannel} from "projector-client-object-api";
import {DtoUrlScript, DtoUrlScriptCommandHandler} from "./generated";

export class UrlScript implements ClientObject, DtoUrlScriptCommandHandler, Invokable {
	private modulePromise: Promise<any>;

	constructor(config: DtoUrlScript, serverObjectChannel: ServerObjectChannel) {
		this.modulePromise = loadModuleFromUrl(config.url);
	}

	async invoke(name: string, parameters: any[]) {
		return ((await this.modulePromise)[name] as Function).apply(null, parameters);
	}

	destroy(): void {
		// make typescript compiler happy
	}
}

async function loadModuleFromUrl(url: string) {
	try {
		return await import(url);
	} finally {
		URL.revokeObjectURL(url);
	}
}