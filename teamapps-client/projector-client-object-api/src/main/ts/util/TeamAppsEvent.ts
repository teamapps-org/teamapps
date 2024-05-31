/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {debounce, DebounceMode} from "./debounce";
import {throttle} from "./throttle";
import {deepEquals} from "./deepEquals";
import {isServerObjectChannel, ServerObjectChannel} from "../ClientObject";

export type TeamAppsEventListener<EO> = (eventObject?: EO) => void;

export interface EventSubscription {
	unsubscribe: () => void
}

type FireMethod<T> = (eventObject: T) => void;
type FireMethodDecorator<T> = (fire: FireMethod<T>) => FireMethod<T>;

/**
 * @param EO the event object type
 */
export class TeamAppsEvent<EO> {

	private listeners: TeamAppsEventListener<EO>[] = [];
	private previousEventObject: EO = undefined;

	public fire: (eventObject: EO) => void;

	/**
	 * @param fireMethodWrapper use for throttling and debouncing
	 */
	constructor(fireMethodWrapper?: FireMethodDecorator<EO>) {
		this.fire = this.#fire;

		if (fireMethodWrapper != null) {
			this.fire = fireMethodWrapper(this.fire);
		}
	}

	public static createThrottled<EO>(delayMillis: number) {
		return new TeamAppsEvent<EO>(fire => throttle(fire, delayMillis));
	}

	public static createDebounced<EO>(delayMillis: number, mode: DebounceMode) {
		return new TeamAppsEvent<EO>(fire => debounce(fire, delayMillis, mode));
	}

	public addListener(fn: TeamAppsEventListener<EO>, allowDuplicates = false): EventSubscription {
		if (!allowDuplicates) {
			this.listeners = this.listeners.filter(l => l !== fn);
		}
		this.listeners.push(fn);
		return {unsubscribe: () => this.removeListener(fn)};
	};

	public removeListener(fn: TeamAppsEventListener<EO>) {
		const listenerIndex = this.listeners.indexOf(fn);
		if (listenerIndex != -1) {
			this.listeners.splice(listenerIndex, 1);
		}
	};

	#fire(eventObject: EO) {
		for (let i = 0; i < this.listeners.length; i++) {
			this.listeners[i].call(null, eventObject);
		}
		this.previousEventObject = eventObject;
	};


	public fireIfChanged(eventObject: EO) {
		if (this.previousEventObject === undefined || !deepEquals(this.previousEventObject, eventObject)) {
			this.fire(eventObject);
			this.previousEventObject = eventObject;
		}
	}

	public resetChangeValue() {
		this.previousEventObject = undefined;
	}

	public getListeners() {
		return this.listeners.slice();
	}
}

export function isTeamAppsEvent(e: any): e is TeamAppsEvent<any> {
	return typeof e === "object"
		&& typeof (e["fire"]) === "function"
		&& typeof (e["addListener"]) === "function"
		&& typeof (e["removeListener"]) === "function";
}

