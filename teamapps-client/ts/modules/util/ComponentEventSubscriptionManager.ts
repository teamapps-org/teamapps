/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

export const typescriptDeclarationFixConstant = 1;

import {UiEvent} from "../../generated/UiEvent";
import {EventSubscription, TeamAppsEventListener} from "./TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";

type EventDescriptor = { name: string, _type: string };

function capitalizeFirstLetter(s: string) {
	return s.charAt(0).toUpperCase() + s.slice(1);
}

export class ComponentEventSubscriptionManager {

	private readonly eventDescriptorsByComponentType: Map<string, EventDescriptor[]> = new Map();
	private readonly subscriptionsByComponent: Map<any, EventSubscription[]> = new Map();

	public registerComponentType(componentType: string, eventDescriptors: EventDescriptor[]) {
		this.eventDescriptorsByComponentType.set(componentType, eventDescriptors);
	}

	public registerComponentTypes(componentEventDescriptors: { componentType: string, eventDescriptors: EventDescriptor[] }[],
								  staticComponentEventDescriptors: { componentType: string, eventDescriptors: EventDescriptor[] }[],
								  staticEventListener: TeamAppsEventListener<UiEvent>) {
		componentEventDescriptors.forEach(r => this.registerComponentType(r.componentType, r.eventDescriptors));

		staticComponentEventDescriptors.forEach((value) => {
			let className = value.componentType as string;
			let componentClass = TeamAppsUiComponentRegistry.getComponentClassForName(className, false);
			this.registerListeners(componentClass, value.eventDescriptors, staticEventListener, {});
		})
	}

	public registerEventListener(component: any, type: string, eventListener: TeamAppsEventListener<UiEvent>, additionalEventProperties: object) {
		let eventNames = this.eventDescriptorsByComponentType.get(type);
		if (eventNames != null) {
			this.registerListeners(component, eventNames, eventListener, additionalEventProperties);
		}
	}

	private registerListeners(component: any, eventNames: EventDescriptor[], eventListener: (eventObject?: UiEvent) => void, additionalEventProperties: object) {
		let subscriptions = eventNames
			.map(eventDescriptor => {
				let listener = (eventObject: any) => eventListener({_type: eventDescriptor._type, ...eventObject, ...additionalEventProperties});
				return component["on" + capitalizeFirstLetter(eventDescriptor.name)]?.addListener(listener);
			});
		this.subscriptionsByComponent.set(component, subscriptions);
	}

	public unregisterEventListener(component: any) {
		let subscriptions = this.subscriptionsByComponent.get(component);
		if (subscriptions != null) {
			subscriptions.forEach(s => s.unsubscribe());
		}
	}
}
