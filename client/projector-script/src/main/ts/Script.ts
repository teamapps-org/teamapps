import {ClientObject, ServerObjectChannel} from "projector-client-object-api";
import {DtoScript, DtoScriptCommandHandler} from "./generated";

export class Script implements ClientObject, DtoScriptCommandHandler {
	private scriptElement: HTMLScriptElement;

	private modulePromise: Promise<any>;

	constructor(config: DtoScript, serverObjectChannel: ServerObjectChannel) {
		this.scriptElement = document.createElement('script');
		this.scriptElement.type = 'module';
		this.scriptElement.innerText = config.script;
		const someExistingScriptElement = document.getElementsByTagName('script')[0];
		someExistingScriptElement.parentNode.insertBefore(this.scriptElement, someExistingScriptElement);

		this.modulePromise = loadModuleFromString(config.script);
	}

	async callFunction(name: string, parameters: any[]) {
		((await this.modulePromise)[name] as Function).apply(null, parameters)
	}

	destroy(): void {
		// make typescript compiler happy
	}
}

async function loadModuleFromString(moduleCode: string) {
	const url = URL.createObjectURL(new Blob([moduleCode], {type: 'application/javascript'}));
	try {
		return await eval(`import("${url}")`); // needed so webpack does not try to be intelligent...
	} finally {
		URL.revokeObjectURL(url);
	}
}