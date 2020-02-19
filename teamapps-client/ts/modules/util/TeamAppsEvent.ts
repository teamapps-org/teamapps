/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

export type TeamAppsEventListener<EO> = (eventObject?: EO, emitter?: any) => void;

/**
 * @param EO the event object type
 */
export class TeamAppsEvent<EO> {

	private listeners: TeamAppsEventListener<EO>[] = [];
	private previousEventObject: EO = undefined;

	constructor(private eventSource: any, private debounceDelay = 0, private debounceMode = DebounceMode.BOTH) {
	}

	public addListener(fn: TeamAppsEventListener<EO>, allowDuplicates = false) {
		if (!allowDuplicates) {
			this.listeners = this.listeners.filter(l => l !== fn);
		}
		this.listeners.push(fn);
	};

	public removeListener(fn: TeamAppsEventListener<EO>) {
		const listenerIndex = this.listeners.indexOf(fn);
		if (listenerIndex != -1) {
			this.listeners.splice(listenerIndex, 1);
		}
	};

	private _fire(eventObject: EO) {
		for (let i = 0; i < this.listeners.length; i++) {
			this.listeners[i].call(null, eventObject, this.eventSource);
		}
		this.previousEventObject = eventObject;
	};

	public fire: (eventObject: EO) => void = debounce(this._fire.bind(this), this.debounceDelay, this.debounceMode);

	public fireIfChanged(eventObject: EO) {
		if (this.previousEventObject !== eventObject) {
			this.fire(eventObject);
			this.previousEventObject = eventObject;
		}
	};

	public getListeners() {
		return this.listeners.slice();
	}
}

