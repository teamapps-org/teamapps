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
interface Invocation<T> {
	f: Function,
	theThis: any[]
	args: any[],
	resolve: (value: PromiseLike<T> | T) => void,
	reject: (reason: any) => void
}

export class DeferredExecutor {

	private _ready: boolean = false;
	private queue: Invocation<any>[] = [];

	public invokeWhenReady<T>(f: () => T, theThis?: any, args?: any[]): Promise<T> {
		if (this.ready) {
			return Promise.resolve(f.apply(theThis, args));
		} else {
			return new Promise<T>((resolve, reject) => {
				this.queue.push({f, theThis, args, resolve, reject});
			});
		}
	}

	public invokeOnceWhenReady<T>(f: () => T, theThis?: any, args?: any[]) {
		this.queue = this.queue.filter(invocation => f !== invocation.f);
		return this.invokeWhenReady(f, theThis, args);
	}

	get ready() {
		return this._ready;
	}

	set ready(ready) {
		this._ready = ready;
		if (ready) {
			for (var i = 0; i < this.queue.length; i++) {
				let invocation = this.queue[i];
				try {
					let resultOfInvocation = invocation.f.apply(invocation.theThis, invocation.args);
					invocation.resolve(resultOfInvocation);
				} catch (e) {
					invocation.reject(e);
				}
			}
			this.queue.length = 0;
		}
	}

}
