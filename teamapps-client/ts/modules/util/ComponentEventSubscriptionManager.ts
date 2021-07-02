/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiEvent} from "../../generated/UiEvent";
import {EventSubscription, TeamAppsEventListener} from "./TeamAppsEvent";

type EventDescriptor = { name: string, _type: string };

function capitalizeFirstLetter(s: string) {
	return s.charAt(0).toUpperCase() + s.slice(1);
}

export class ComponentEventSubscriptionManager {

	private readonly eventNamesByComponentType: Map<string, EventDescriptor[]> = new Map();
	private readonly subscriptionsByComponent: Map<any, EventSubscription[]> = new Map();

	public registerComponentType(componentType: string, eventDescriptors: EventDescriptor[]) {
		this.eventNamesByComponentType.set(componentType, eventDescriptors);
	}

	public registerComponentTypes(componentEventDescriptors: { componentType: string, eventDescriptors: EventDescriptor[] }[]) {
		componentEventDescriptors.forEach(r => this.registerComponentType(r.componentType, r.eventDescriptors));
	}

	public registerEventListener(component: any, type: string, eventListener: TeamAppsEventListener<UiEvent>, additionalEventProperties: object) {
		let eventNames = this.eventNamesByComponentType.get(type);
		if (eventNames != null) {
			let subscriptions = eventNames
				.map(eventDescriptor => {
					let listener = (eventObject: any) => eventListener({_type: eventDescriptor._type, ...eventObject, ...additionalEventProperties});
					return component["on" + capitalizeFirstLetter(eventDescriptor.name)]?.addListener(listener);
				});
			this.subscriptionsByComponent.set(component, subscriptions);
		}
	}

	public unregisterEventListener(component: any) {
		let subscriptions = this.subscriptionsByComponent.get(component);
		if (subscriptions != null) {
			subscriptions.forEach(s => s.unsubscribe());
		}
	}
}