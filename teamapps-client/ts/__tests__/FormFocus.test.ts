/** @jest-environment jsdom */
import {preserveFormFocus} from "../modules/util/PreserveFormFocus";

describe("form layout changes while editing", () => {
    let container: HTMLDivElement, input: HTMLInputElement;
    beforeEach(() => {
        container = document.createElement("div"); input = document.createElement("input");
        container.appendChild(input); document.body.appendChild(container);
        // jsdom has no layout; represent a visible input.
        input.getClientRects = () => ({length: 1} as DOMRectList);
        input.value = "Kategorie";
        input.addEventListener("focus", () => input.select());
    });
    afterEach(() => document.body.innerHTML = "");
    test("keeps text and caret after appending a repeater row", () => {
        input.focus(); input.setSelectionRange(4, 4);
        const restore = preserveFormFocus(container);
        input.remove(); container.appendChild(input); container.appendChild(document.createElement("input"));
        restore();
        expect(document.activeElement).toBe(input); expect(input.value).toBe("Kategorie");
        expect([input.selectionStart, input.selectionEnd]).toEqual([4, 4]);
    });
    test("keeps a selected text range and direction", () => {
        input.focus(); input.setSelectionRange(1, 5, "backward");
        const restore = preserveFormFocus(container); input.remove(); container.appendChild(input); restore();
        expect([input.selectionStart, input.selectionEnd, input.selectionDirection]).toEqual([1, 5, "backward"]);
    });
    test("does not restore a deleted or hidden input", () => {
        input.focus(); const restore = preserveFormFocus(container); input.remove(); restore();
        expect(document.activeElement).not.toBe(input);
        container.appendChild(input); input.getClientRects = () => ({length: 0} as DOMRectList); restore();
        expect(document.activeElement).not.toBe(input);
    });
    test("does not steal focus from outside the form", () => {
        const external = document.createElement("input"); document.body.appendChild(external); external.focus();
        const restore = preserveFormFocus(container); container.appendChild(document.createElement("input")); restore();
        expect(document.activeElement).toBe(external);
    });
});
