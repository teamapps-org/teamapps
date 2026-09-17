/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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

/** One optional owner per document; slots represent structural depth, never visited views. */
export interface MobileHistoryOwner {
	isActive(): boolean;
	getPosition(): number;
	getLength(): number;
	navigateTo(position: number): void;
}

const STATE_KEY = '__teamappsMobileNavigation';
interface HistorySlot { scope: string; position: number; extent: number; }

export class MobileNavigationHistory {
	private owner: MobileHistoryOwner;
	private scope: string;
	private position = 0;
	private extent = 0;
	private pendingPosition: number = null;
	private browserTraversal = false;
	private failed = false;

	constructor(private readonly history: History) {}

	public update(owner: MobileHistoryOwner): void {
		if (this.failed || !owner.isActive()) return;
		this.owner = owner;
		try {
			if (!this.scope) {
				const existing = this.readSlot(this.history.state);
				this.scope = existing ? existing.scope : `mobile-${Date.now()}-${Math.random()}`;
				this.position = existing ? existing.position : 0;
				this.extent = existing ? existing.extent : 0;
				if (!existing) this.writeSlot(false, 0);
			}
			this.synchronize();
		} catch (e) {
			// History can be unavailable in a sandboxed embedding. Toolbar/swipes still work.
			this.failed = true;
		}
	}

	public release(owner: MobileHistoryOwner): void {
		if (this.owner === owner) this.owner = null;
	}

	/** Returns true only for entries belonging to this optional navigation controller. */
	public handlePopState(state: any): boolean {
		const slot = this.readSlot(state);
		if (!this.scope || !slot || slot.scope !== this.scope || this.failed) {
			return false;
		}
		this.position = slot.position;
		this.extent = Math.max(this.extent, slot.extent);
		if (!this.owner || !this.owner.isActive()) {
			this.pendingPosition = null;
			this.browserTraversal = false;
			return false;
		}
		if (this.pendingPosition === slot.position) {
			this.pendingPosition = null;
		} else {
			this.pendingPosition = null;
			this.browserTraversal = true;
		}
		try {
			this.synchronize();
		} catch (e) {
			this.failed = true;
		}
		return true;
	}

	private synchronize(): void {
		if (!this.owner || !this.owner.isActive() || this.pendingPosition != null) return;
		const wanted = this.owner.getPosition();
		if (this.browserTraversal) {
			const target = Math.min(this.position, this.owner.getLength() - 1);
			if (wanted !== target) {
				this.owner.navigateTo(target);
				return; // Server acknowledgement will reconcile rapid repeated Back/Forward.
			}
			this.browserTraversal = false;
		}
		if (wanted === this.position) return;
		if (wanted <= this.extent) {
			this.pendingPosition = wanted;
			this.history.go(wanted - this.position);
		} else if (this.position < this.extent) {
			this.pendingPosition = this.extent;
			this.history.go(this.extent - this.position);
		} else {
			// Append only genuinely new structural depth, never re-arm after a browser Back.
			while (this.position < wanted) {
				this.writeSlot(true, this.position + 1);
			}
		}
	}

	private writeSlot(push: boolean, position: number): void {
		this.position = position;
		this.extent = Math.max(this.extent, position);
		const state = {...this.history.state, [STATE_KEY]: {scope: this.scope, position, extent: this.extent}};
		if (push) this.history.pushState(state, '');
		else this.history.replaceState(state, '');
	}

	private readSlot(state: any): HistorySlot {
		const slot = state && state[STATE_KEY];
		return slot && typeof slot.scope === 'string' && Number.isInteger(slot.position) && slot.position >= 0
			&& Number.isInteger(slot.extent) && slot.extent >= slot.position ? slot : null;
	}
}

export const mobileNavigationHistory = typeof window !== 'undefined' ? new MobileNavigationHistory(window.history) : null;
