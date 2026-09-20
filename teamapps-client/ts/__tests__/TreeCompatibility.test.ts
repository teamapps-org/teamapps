/** @jest-environment jsdom */
import { UiTree } from "../modules/UiTree";
(global as any).$ = require('jquery');
HTMLElement.prototype.scrollIntoView = jest.fn();
const node = (id: number, parentId?: number, expanded = false) => ({ id, parentId, asString: `Node ${id}`, selectable: true, expanded, values: {} });
let tree: UiTree, selectedEvents: any[], expansionEvents: any[];
const internal = () => tree as any;
const selection = () => internal().trivialTree.getSelectedEntry()?.id;
const ids = () => internal().nodes.map(n => n.id);
function create(Type, data = [node(1), node(2)], extra = {}) {
    tree = new Type({ initialData: data, templates: {}, selectedNodeId: 1, animate: false, showExpanders: true, openOnSelection: false, ...extra }, { templateRegistry: { createTemplateRenderers: () => ({}) } } as any);
    document.body.appendChild(tree.doGetMainElement());
    Object.defineProperty(tree.doGetMainElement(), 'clientHeight', { value: 200, configurable: true });
    Object.defineProperty(tree.doGetMainElement(), 'clientWidth', { value: 300, configurable: true });
    selectedEvents = [];
    expansionEvents = [];
    tree.onNodeSelected.addListener(e => selectedEvents.push(e));
    tree.onNodeExpansionChanged.addListener(e => expansionEvents.push(e));
    (HTMLElement.prototype.scrollIntoView as jest.Mock).mockClear();
}
function click(id) { tree.doGetMainElement().querySelector(`[data-id="${id}"] > .tr-tree-entry-and-expander-wrapper .tr-tree-entry`).dispatchEvent(new MouseEvent('mousedown', { bubbles: true })); }
function pending(nodes) { tree.replaceData([node(1), node(2)]); tree.replaceData(nodes); }
describe('Tree command compatibility', () => {
    beforeEach(() => { jest.useFakeTimers('modern'); jest.setSystemTime(100000); });
    afterEach(() => { jest.runOnlyPendingTimers(); jest.useRealTimers(); document.body.innerHTML = ''; });
    test('server selection stays silent and does not scroll', () => {
        create(UiTree);
        tree.setSelectedNode(2);
        expect(selection()).toBe(2);
        expect(selectedEvents).toEqual([]);
        expect(expansionEvents).toEqual([]);
        expect((HTMLElement.prototype.scrollIntoView as jest.Mock).mock.calls.length > 0).toBe(false);
    });
    test('same existing selection does not scroll', () => { create(UiTree); tree.setSelectedNode(1); expect(selection()).toBe(1); expect(HTMLElement.prototype.scrollIntoView).not.toHaveBeenCalled(); });
    test('closed parent is not expanded by server selection', () => {
        create(UiTree, [node(1), node(5), node(6, 5)]);
        tree.setSelectedNode(6);
        expect(selection()).toBe(6);
        expect(selectedEvents).toEqual([]);
        expect(expansionEvents).toEqual([]);
    });
    test('old expander path already emits expansion without changing client selection', () => {
        create(UiTree, [node(1), node(5), node(6, 5)]);
        tree.doGetMainElement().querySelector('[data-id="5"] > .tr-tree-entry-and-expander-wrapper .tr-tree-expander').dispatchEvent(new MouseEvent('click', { bubbles: true }));
        expect(selection()).toBe(1);
        expect(selectedEvents).toEqual([]);
        expect(expansionEvents).toEqual([{ nodeId: 5, expanded: true }]);
    });
    test('ordinary user selection still emits exactly one event', () => { create(UiTree); click(2); expect(selection()).toBe(2); expect(selectedEvents).toEqual([{ nodeId: 2 }]); });
    test('throttled replacement applies the requested new id silently', () => {
        create(UiTree);
        pending([node(3)]);
        tree.setSelectedNode(3);
        jest.runAllTimers();
        expect(selection()).toBe(3);
        expect(selectedEvents).toEqual([]);
        expect(expansionEvents).toEqual([]);
    });
    test('latest server selection wins while replacement is pending', () => {
        create(UiTree);
        pending([node(3), node(4)]);
        tree.setSelectedNode(3);
        tree.setSelectedNode(4);
        jest.runAllTimers();
        expect(selection()).toBe(4);
        expect(selectedEvents).toEqual([]);
    });
    test.each([null, -1])('explicit clear %s cancels pending selection', clear => {
        create(UiTree);
        pending([node(3)]);
        tree.setSelectedNode(3);
        tree.setSelectedNode(clear);
        jest.runAllTimers();
        expect(selection()).toBeUndefined();
        expect(selectedEvents).toEqual([]);
    });
    test('later user click supersedes requested deferred selection', () => {
        create(UiTree);
        pending([node(2), node(3)]);
        tree.setSelectedNode(3);
        click(2);
        jest.runAllTimers();
        expect(selection()).toBe(2);
        expect(selectedEvents).toEqual([{ nodeId: 2 }]);
    });
    test('missing target with no pending data clears selection as before', () => {
        create(UiTree);
        tree.setSelectedNode(999);
        expect(selection()).toBe(undefined);
        tree.replaceData([node(999)]);
        jest.runAllTimers();
        expect(selection()).toBe(undefined);
    });
    test('unfulfilled request expires after pending data and never selects a later unrelated record', () => {
        create(UiTree);
        pending([node(3)]);
        tree.setSelectedNode(999);
        jest.runAllTimers();
        expect(selection()).toBeUndefined();
        tree.replaceData([node(999)]);
        jest.runAllTimers();
        expect(selection()).toBe(undefined);
    });
    test('coalesced snapshots resolve selection against the final dataset', () => {
        create(UiTree);
        pending([node(3)]);
        tree.setSelectedNode(3);
        tree.replaceData([node(3), node(4)]);
        jest.runAllTimers();
        expect(selection()).toBe(3);
    });
    test('a removed selected record is not replaced by a guessed equivalent record', () => {
        create(UiTree);
        tree.setSelectedNode(2);
        tree.replaceData([node(101), node(102)]);
        jest.runAllTimers();
        expect(selection()).toBeUndefined();
    });
    test('repeated replacements remain coalesced, including selection after each snapshot', () => {
        create(UiTree);
        const redraw = jest.spyOn(internal().trivialTree, 'updateEntries');
        for (let id = 10; id < 110; id++) {
            tree.replaceData([node(id)]);
            tree.setSelectedNode(id);
        }
        jest.runAllTimers();
        expect(redraw).toHaveBeenCalledTimes(2);
        expect(selection()).toBe(109);
    });
    test('showing hidden tree does not add scrolling without an explicit visibility request', () => {
        create(UiTree);
        Object.defineProperty(tree.doGetMainElement(), 'clientHeight', { value: 0, configurable: true });
        tree.setSelectedNode(2);
        Object.defineProperty(tree.doGetMainElement(), 'clientHeight', { value: 200, configurable: true });
        tree.onResize();
        expect((HTMLElement.prototype.scrollIntoView as jest.Mock).mock.calls.length > 0).toBe(false);
    });
    test('an intervening bulk removal is not overwritten by an older pending snapshot by applying the pending snapshot first', () => {
        create(UiTree);
        pending([node(3)]);
        tree.setSelectedNode(3);
        tree.bulkUpdate([3], []);
        jest.runAllTimers();
        expect(ids()).toEqual([]);
        expect(selection()).toBe(undefined);
    });
    test('ordering barrier can require one extra render when snapshots alternate with bulk changes', () => {
        create(UiTree);
        const redraw = jest.spyOn(internal().trivialTree, 'updateEntries');
        pending([node(3)]);
        tree.bulkUpdate([3], []);
        tree.replaceData([node(4)]);
        jest.runAllTimers();
        expect(ids()).toEqual([4]);
        expect(redraw).toHaveBeenCalledTimes(3);
    });
    test('normal synchronous bulk removal retains old behavior', () => { create(UiTree); tree.setSelectedNode(2); tree.bulkUpdate([2], []); expect(ids()).toEqual([1]); expect(selection()).toBeUndefined(); });
    test('repeated data-only refresh retains existing ids without selection event', () => { create(UiTree); tree.setSelectedNode(2); for (let i = 0; i < 5; i++)
        tree.replaceData([node(1), node(2)]); jest.runAllTimers(); expect(selection()).toBe(2); expect(selectedEvents).toEqual([]); });
});
