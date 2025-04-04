export function stopEventPropagations(element: HTMLElement, ...eventNames: string[]) {
    eventNames.forEach(name => element.addEventListener(name, (e) => e.stopPropagation()));
}