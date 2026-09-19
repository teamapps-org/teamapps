/** @jest-environment jsdom */
import {UiTree} from "../modules/UiTree";
import {UiTreeRecordConfig} from "../generated/UiTreeRecordConfig";

(global as any).$ = require('jquery');
HTMLElement.prototype.scrollIntoView = jest.fn();

describe("tree selection across deferred data replacement", () => {
    let tree: UiTree;
    const node = (id: number, parentId?: number): UiTreeRecordConfig => ({id, parentId, asString: `Node ${id}`, selectable: true, expanded: true, values: {}});
    const selected = () => tree.doGetMainElement().querySelector('.tr-selected-entry')?.textContent;
    beforeEach(() => {
        jest.useFakeTimers('modern'); jest.setSystemTime(100000);
        tree = new UiTree({initialData: [node(1)], templates: {}, selectedNodeId: 1, animate: false,
            showExpanders: false, openOnSelection: true}, {templateRegistry: {createTemplateRenderers: () => ({})}} as any);
        document.body.appendChild(tree.doGetMainElement());
    });
    afterEach(() => { jest.runOnlyPendingTimers(); jest.useRealTimers(); document.body.innerHTML = ''; });
    test("applies the latest selection after a throttled replacement", () => {
        tree.replaceData([node(2)]);
        tree.replaceData([node(3), node(4)]);
        tree.setSelectedNode(3); tree.setSelectedNode(4);
        jest.runAllTimers();
        expect(selected()).toBe('Node 4');
    });
    test("does not resurrect a cleared selection", () => {
        tree.replaceData([node(2)]); tree.replaceData([node(3)]);
        tree.setSelectedNode(3); tree.setSelectedNode(null);
        jest.runAllTimers();
        expect(selected()).toBeUndefined();
    });
    test("nonselectable headings toggle by row click without expanders", () => {
        tree.replaceData([{...node(5), selectable: false}, node(6, 5)]); jest.runAllTimers();
        const heading = Array.from(tree.doGetMainElement().querySelectorAll('.tr-tree-entry')).find(e => e.textContent === 'Node 5') as HTMLElement;
        const events: boolean[] = []; tree.onNodeExpansionChanged.addListener(event => events.push(event.expanded));
        heading.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));
        heading.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));
        expect(events).toEqual([false, true]);
        expect(selected()).not.toBe('Node 5');
    });
});
