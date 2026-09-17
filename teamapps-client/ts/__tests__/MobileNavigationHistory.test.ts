import {MobileHistoryOwner, MobileNavigationHistory} from '../modules/util/MobileNavigationHistory';

class FakeHistory {
	entries: any[] = [{applicationState: 'preserve'}];
	index = 0;
	moves: number[] = [];
	get state() { return this.entries[this.index]; }
	pushState(state: any) { this.entries.splice(++this.index, Infinity, state); }
	replaceState(state: any) { this.entries[this.index] = state; }
	go(delta: number) { this.moves.push(this.index + delta); }
	flush(controller: MobileNavigationHistory) {
		let count = 0;
		while (this.moves.length) {
			if (++count > 100) throw new Error('Navigation loop');
			this.index = this.moves.shift();
			expect(this.index).toBeGreaterThanOrEqual(0);
			expect(this.index).toBeLessThan(this.entries.length);
			controller.handlePopState(this.state);
		}
	}
	back(controller: MobileNavigationHistory, position: number) {
		this.index = position;
		return controller.handlePopState(this.state);
	}
}

function setup() {
	const history = new FakeHistory();
	const controller = new MobileNavigationHistory(history as unknown as History);
	let position = 3, length = 5, active = true;
	const requests: number[] = [];
	const owner: MobileHistoryOwner = {
		isActive: () => active, getPosition: () => position, getLength: () => length,
		navigateTo: p => requests.push(p)
	};
	const update = (p: number) => { position = p; controller.update(owner); history.flush(controller); };
	update(3);
	return {history, controller, owner, requests, update, setLength: (n: number) => length = n, hide: () => active = false};
}

test('30 master/detail switches reuse structural slots and preserve unrelated state', () => {
	const s = setup();
	for (let i = 0; i < 30; i++) { s.update(2); s.update(3); }
	expect(s.history.entries).toHaveLength(4);
	expect(s.history.state.applicationState).toBe('preserve');
	expect(s.requests).toEqual([]);
	s.history.back(s.controller, 2); s.update(2);
	s.history.back(s.controller, 1); s.update(1);
	s.history.back(s.controller, 0); s.update(0);
	expect(s.requests).toEqual([2, 1, 0]);
	expect(s.history.entries).toHaveLength(4);
});

test('browser Back does not append guard entries; browser Forward uses the current owner', () => {
	const s = setup();
	s.history.back(s.controller, 2); s.update(2);
	s.history.back(s.controller, 3); s.update(3);
	expect(s.requests).toEqual([2, 3]);
	expect(s.history.entries).toHaveLength(4);
});

test('rapid Back survives an intermediate server acknowledgement', () => {
	const s = setup();
	s.history.back(s.controller, 2);
	s.history.back(s.controller, 1);
	s.update(2); // first server response is already out of date
	expect(s.requests[s.requests.length - 1]).toBe(1);
	expect(s.history.index).toBe(1);
	s.update(1);
	expect(s.history.index).toBe(1);
});

test('a new owner reuses the same bounded slots', () => {
	const s = setup();
	const other = {...s.owner, getPosition: () => 1, navigateTo: jest.fn()};
	s.controller.update(other); s.history.flush(s.controller);
	s.history.back(s.controller, 0);
	expect(other.navigateTo).toHaveBeenCalledWith(0);
	expect(s.requests).toEqual([]);
	expect(s.history.entries).toHaveLength(4);
});

test('reconnection adopts existing slots without growing the history', () => {
	const s = setup();
	const replacement = new MobileNavigationHistory(s.history as unknown as History);
	replacement.update(s.owner); s.history.flush(replacement);
	expect(s.history.entries).toHaveLength(4);
});

test('shortened path clamps a stale browser Forward without adding entries', () => {
	const s = setup();
	s.update(1); s.setLength(2);
	s.history.back(s.controller, 3); s.update(1);
	expect(s.history.index).toBe(1);
	expect(s.history.entries).toHaveLength(4);
});

test('foreign state, inactive owners and released layouts keep legacy browser routing', () => {
	const s = setup();
	expect(s.controller.handlePopState({other: true})).toBe(false);
	s.hide(); expect(s.controller.handlePopState(s.history.state)).toBe(false);
	s.controller.release(s.owner); expect(s.controller.handlePopState(s.history.state)).toBe(false);
	expect(s.requests).toEqual([]);
});

test('normal navigation beyond existing depth appends only the missing slots', () => {
	const s = setup();
	s.update(1); s.update(4);
	expect(s.history.entries).toHaveLength(5);
	expect(s.history.index).toBe(4);
	expect(s.requests).toEqual([]);
});

test('detaching during an asynchronous history move does not lock the next owner', () => {
	const s = setup();
	const other = {...s.owner, getPosition: () => 1};
	s.controller.update(other);
	s.controller.release(other);
	s.history.flush(s.controller);
	s.controller.update(s.owner);
	s.history.flush(s.controller);
	expect(s.history.index).toBe(3);
	expect(s.history.entries).toHaveLength(4);
});
