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

export function executeWhenFirstDisplayed(onlyOnce?: boolean) {
	return function<This, Args extends any[], Return>(
		originalMethod: (this: This, ...args: Args) => Return,
		context: ClassMethodDecoratorContext<This, (this: This, ...args: Args) => Return>
	) {
		return function () {
			if (!this.displayedDeferredExecutor) {
				console.error("Could not find displayedDeferredExecutor!!");
			}
			if (onlyOnce) {
				this.displayedDeferredExecutor.invokeOnceWhenReady(originalMethod, this, arguments);
			} else {
				console.log("will invoke when ready: " + context.name.toString());
				this.displayedDeferredExecutor.invokeWhenReady(originalMethod, this, arguments);
			}
		};
	};
}

