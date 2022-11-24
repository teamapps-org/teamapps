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
import {DeferredExecutor} from "teamapps-client-core";

export function executeWhenFirstDisplayed(onlyOnce?: boolean) {
	return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
		let oldMethod = descriptor.value;
		descriptor.value = function () {
			if (!this.displayedDeferredExecutor) {
				console.error("Could not find displayedDeferredExecutor!!");
			}
			if (onlyOnce) {
				this.displayedDeferredExecutor.invokeOnceWhenReady(oldMethod, this, arguments);
			} else {
				this.displayedDeferredExecutor.invokeWhenReady(oldMethod, this, arguments);
			}
		};
	};
}

export function executeDeferredExecutorReady(deferredExecutorGetter: () => DeferredExecutor, onlyOnce?: boolean) {
	return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
		let oldMethod = descriptor.value;
		descriptor.value = function () {
			let deferredExecutor = deferredExecutorGetter();
			if (onlyOnce) {
				this.deferredExecutor.invokeOnceWhenReady(oldMethod, this, arguments);
			} else {
				this.deferredExecutor.invokeWhenReady(oldMethod, this, arguments);
			}
		};
	};
}



