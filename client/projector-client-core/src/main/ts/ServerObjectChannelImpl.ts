import {ServerObjectChannel} from "projector-client-object-api";

export interface ServerObjectChannelHandler {
	handleEvent(name: string, params: any[]): void,

	handleQuery(name: string, params: any[]): Promise<any>;
}

export class ServerObjectChannelImpl implements ServerObjectChannel {
	private activeEventNames: Set<string> = new Set<string>();
	private eventHandlers: { registrationId: string, eventName: string, handler: (any) => any }[];

	constructor(private delegate: ServerObjectChannelHandler) {
	}

	sendEvent(eventName: string, params: any[]) {
		this.eventHandlers
			.filter(eh => eh.eventName == eventName)
			.forEach(eh => eh.handler.call(null));

		if (this.activeEventNames.has(eventName)) {
			console.debug(`sendEvent(${eventName}) will be forwarded to server.`);
			this.delegate.handleEvent(eventName, params);
		} else {
			console.debug(`Event swallowed (not enabled): ${eventName}.`);
		}
	}

	sendQuery(name: string, params: any[]): Promise<any> {
		return this.delegate.handleQuery(name, params);
	}

	toggleEvent(name: string, enabled: boolean): void {
		console.debug(`Toggling event ${name}: ${enabled}`)
		if (enabled) {
			this.activeEventNames.add(name);
		} else {
			this.activeEventNames.delete(name);
		}
	}

	addEventHandler(eventName: string, registrationId: string, handler: (any) => any): void {
		console.debug(`Adding event handler for event ${eventName}. registrationId: ${registrationId}`)
		this.eventHandlers.push({registrationId, eventName, handler});
	}

	removeEventHandler(registrationId: string): void {
		console.debug(`Removing event handler with registrationId: ${registrationId}`)
		this.eventHandlers = this.eventHandlers.filter(eh => eh.registrationId != registrationId);
	}
}