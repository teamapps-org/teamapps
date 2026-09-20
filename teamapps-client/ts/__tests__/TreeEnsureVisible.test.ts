/** @jest-environment jsdom */
import { UiTree } from "../modules/UiTree";
import { UiTreeRecordConfig } from "../generated/UiTreeRecordConfig";
jest.mock('resize-observer-polyfill', () => ({ __esModule: true, default: class ResizeObserverMock {
        observe() { }
        disconnect() { }
    } }));
(global as any).$ = require('jquery');
HTMLElement.prototype.scrollIntoView = jest.fn();
const node = (id: number, parentId?: number, expanded = false): UiTreeRecordConfig => ({ id, parentId, expanded, selectable: true, asString: `Node ${id}`, values: {} });
describe('explicit Tree visibility', () => {
    let tree: UiTree, expansions: any[], selections: any[];
    const selected = () => (tree as any).trivialTree.getSelectedEntry()?.id;
    const size = (height: number) => Object.defineProperty(tree.doGetMainElement(), 'clientHeight', { value: height, configurable: true });
    const scroll = () => HTMLElement.prototype.scrollIntoView as jest.Mock;
    beforeEach(() => {
        jest.useFakeTimers('modern');
        jest.setSystemTime(100000);
        tree = new UiTree({ initialData: [node(1), node(10), node(11, 10), node(12, 11)],
            selectedNodeId: 1, templates: {}, animate: false, showExpanders: true }, { templateRegistry: { createTemplateRenderers: () => ({}) } } as any);
        document.body.appendChild(tree.doGetMainElement());
        size(200);
        Object.defineProperty(tree.doGetMainElement(), 'clientWidth', { value: 300 });
        expansions = [];
        selections = [];
        tree.onNodeExpansionChanged.addListener(e => expansions.push(e));
        tree.onNodeSelected.addListener(e => selections.push(e));
        scroll().mockClear();
    });
    afterEach(() => { tree.destroy(); jest.runOnlyPendingTimers(); jest.useRealTimers(); document.body.innerHTML = ''; });
    test('opens ancestors root first and scrolls to a different node without selecting it', () => {
        tree.ensureVisible(12);
        expect(expansions).toEqual([{ nodeId: 10, expanded: true }, { nodeId: 11, expanded: true }]);
        expect(selected()).toBe(1);
        expect(selections).toEqual([]);
        expect(scroll().mock.instances.slice(-1)[0].closest('[data-id]').getAttribute('data-id')).toBe('12');
    });
    test('repeated requests do not duplicate expansion events', () => {
        tree.ensureVisible(12);
        tree.ensureVisible(12);
        expect(expansions).toHaveLength(2);
        expect(selections).toEqual([]);
    });
    test('selecting a hidden child and then revealing it preserves its visible selection', () => {
        tree.setSelectedNode(12);
        expect(expansions).toEqual([]);
        tree.ensureVisible(12);
        expect(selected()).toBe(12);
        expect(tree.doGetMainElement().querySelector('.tr-selected-entry')?.textContent).toBe('Node 12');
        expect(selections).toEqual([]);
    });
    test('waits for a pending replacement, then reveals the new target once', () => {
        tree.replaceData([node(1)]);
        tree.replaceData([node(1), node(20), node(21, 20)]);
        tree.ensureVisible(21);
        expect(scroll()).not.toHaveBeenCalled();
        jest.runAllTimers();
        expect(expansions).toEqual([{ nodeId: 20, expanded: true }]);
        expect(selected()).toBe(1);
    });
    test('hidden trees wait for their viewport; latest explicit request wins', () => {
        size(0);
        tree.ensureVisible(12);
        tree.ensureVisible(11);
        expect(scroll()).not.toHaveBeenCalled();
        size(200);
        tree.onResize();
        expect(expansions).toEqual([{ nodeId: 10, expanded: true }]);
        scroll().mockClear();
        tree.onResize();
        expect(scroll()).not.toHaveBeenCalled();
    });
    test('a user selection cancels a visibility request that is still waiting', () => {
        size(0);
        tree.ensureVisible(12);
        tree.doGetMainElement().querySelector('[data-id="10"] .tr-tree-entry')!
            .dispatchEvent(new MouseEvent('mousedown', { bubbles: true }));
        size(200);
        tree.onResize();
        expect(expansions).toEqual([]);
        expect(scroll()).not.toHaveBeenCalled();
        expect(selected()).toBe(10);
        expect(selections).toEqual([{ nodeId: 10 }]);
    });
    test('unknown targets are ignored and do not revive on unrelated later data', () => {
        tree.ensureVisible(999);
        tree.replaceData([node(999)]);
        jest.runAllTimers();
        expect(scroll()).not.toHaveBeenCalled();
        expect(expansions).toEqual([]);
    });
    test('a removed target cancels the hidden request', () => {
        size(0);
        tree.ensureVisible(12);
        tree.replaceData([node(1)]);
        jest.runAllTimers();
        size(200);
        tree.onResize();
        expect(scroll()).not.toHaveBeenCalled();
    });
    test('destroy cancels a pending replacement and visibility request', () => {
        tree.replaceData([node(1)]);
        tree.replaceData([node(20), node(21, 20)]);
        tree.ensureVisible(21);
        const update = jest.spyOn((tree as any).trivialTree, 'updateEntries');
        tree.destroy();
        jest.runAllTimers();
        tree.onResize();
        expect(update).not.toHaveBeenCalled();
        expect(expansions).toEqual([]);
        expect(scroll()).not.toHaveBeenCalled();
    });
});
