/** Restore the current editor after a form layout moves its existing DOM nodes. */
export function preserveFormFocus(container: HTMLElement): () => void {
    const active = document.activeElement as HTMLElement;
    if (!active || !container.contains(active)) return () => {};
    const text = active instanceof HTMLInputElement || active instanceof HTMLTextAreaElement ? active : null;
    const start = text && text.selectionStart, end = text && text.selectionEnd;
    const direction = text && text.selectionDirection;
    return () => {
        if (!container.contains(active) || !active.isConnected || active.getClientRects().length === 0) return;
        if (document.activeElement !== active) active.focus({preventScroll: true});
        // TextField selects all on focus. Restore the caret/selection captured before moving it.
        if (text && start != null && end != null) text.setSelectionRange(start, end, direction || undefined);
    };
}
