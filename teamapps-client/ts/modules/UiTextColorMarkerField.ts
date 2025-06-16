import {UiEvent} from "../generated/UiEvent";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";
import {
	UiTextColorMarkerField_TextSelectedEvent,
	UiTextColorMarkerField_TransientChangeEvent,
	UiTextColorMarkerFieldCommandHandler,
	UiTextColorMarkerFieldConfig,
	UiTextColorMarkerFieldEventSource
} from "../generated/UiTextColorMarkerFieldConfig";
import {UiTextColorMarkerFieldMarkerConfig} from "../generated/UiTextColorMarkerFieldMarkerConfig";
import {UiTextColorMarkerFieldMarkerDefinitionConfig} from "../generated/UiTextColorMarkerFieldMarkerDefinitionConfig";
import {UiTextColorMarkerFieldValueConfig} from "../generated/UiTextColorMarkerFieldValueConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {deepEquals, parseHtml} from "./Common";
import {UiField} from "./formfield/UiField";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

export class UiTextColorMarkerField extends UiField<UiTextColorMarkerFieldConfig, UiTextColorMarkerFieldValueConfig> implements UiTextColorMarkerFieldCommandHandler, UiTextColorMarkerFieldEventSource {

	public readonly onTextSelected: TeamAppsEvent<UiTextColorMarkerField_TextSelectedEvent> = new TeamAppsEvent();
	public readonly onTransientChange: TeamAppsEvent<UiTextColorMarkerField_TransientChangeEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $toolbarWrapper: HTMLElement;
	private $editor: HTMLDivElement;

	private markerDefinitions: UiTextColorMarkerFieldMarkerDefinitionConfig[];
	private transientValue: UiTextColorMarkerFieldValueConfig;

	protected initialize(config: UiTextColorMarkerFieldConfig, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="UiTextColorMarkerField">
			<div class="toolbar-wrapper"></div>
			<div class="editor field-border" contenteditable="true"></div>
		</div>`);
		this.$toolbarWrapper = this.$main.querySelector(":scope > .toolbar-wrapper");
		this.$editor = this.$main.querySelector(":scope > .editor");
		//this.$editor.contentEditable = 'true';
		this.setupEventListeners();
		this.transientValue = this.getDefaultValue();
		this.setMarkerDefinitions(config.markerDefinitions, config.value);
	}

	public getMarkerDefinitionById(id: number): UiTextColorMarkerFieldMarkerDefinitionConfig | undefined {
		return this.markerDefinitions.find(definition => definition.id === id);
	}

	public getMarkers(): UiTextColorMarkerFieldMarkerConfig[] {
		return [...this.transientValue.markers];
	}

	public getMarkerById(id: number): UiTextColorMarkerFieldMarkerConfig | undefined {
		return this.transientValue.markers.find(marker => marker.id === id);
	}

	public getPlainText(): string {
		return this.transientValue.text;
	}

	public getTransientValue(): UiTextColorMarkerFieldValueConfig {
		this.commitTransientChanges();
		return {
			text: this.transientValue.text,
			markers: [...this.transientValue.markers]
		};
	}

	public setTransientValue(value?: UiTextColorMarkerFieldValueConfig): void {
		const newValue: UiTextColorMarkerFieldValueConfig = {
			text: value?.text || '',
			markers: value?.markers || []
		};

		this.updateTransientValue(newValue);
	}

	public setMarkerDefinitions(markerDefinitions: UiTextColorMarkerFieldMarkerDefinitionConfig[], newValue: UiTextColorMarkerFieldValueConfig): void {
		this.markerDefinitions = markerDefinitions;
		this.setTransientValue(newValue);
	}

	public setMarker(id: number, start: number, end: number): void {
		const definition = this.getMarkerDefinitionById(id);
		if (!definition) {
			throw new Error(`No marker definition found for id ${id}`);
		}

		// Check for overlapping markers
		if (this.transientValue.markers.some(existingMarker => {
			if (existingMarker.id === id) { return false; } // Skip the same marker
			const existingStart = existingMarker.start!;
			const existingEnd = existingMarker.end!;
			// Two markers overlap if one starts before the other ends and ends after the other starts
			// But we allow them to be nested
			const hasOverlap = () => start < existingEnd && end > existingStart;
			const isNested = () => (start <= existingStart && end >= existingEnd) ||
				(existingStart <= start && existingEnd >= end);
			return hasOverlap() && !isNested();
		})) {
			throw new Error('Invalid marker positions: marker overlaps with existing marker');
		}

		const marker: UiTextColorMarkerFieldMarkerConfig = { id, start, end };

		// Create a new value with the marker added
		const newMarkers = this.transientValue.markers.filter(m => m.id !== marker.id);
		newMarkers.push(marker);
		const newValue: UiTextColorMarkerFieldValueConfig = {
			text: this.transientValue.text,
			markers: newMarkers
		};

		this.updateTransientValue(newValue);
	}

	public removeMarker(id: number): void {
		const markerIndex = this.transientValue.markers.findIndex(m => m.id === id);
		if (markerIndex !== -1) {
			this.transientValue.markers.splice(markerIndex, 1);
			this.renderTransientValue();
		} else {
			this.logger.info(`Cannot remove marker "${id}" since it does not exist`);
		}
	}

	private commitTransientChanges(): void {
		const currentValue = this.getCurrentValue();
		if (this.hasTransientValueChanged(currentValue)) {
			this.transientValue = currentValue;
			this.triggerTransientChangeEvent(this.transientValue);
		}
	}

	private hasTransientValueChanged(newValue: UiTextColorMarkerFieldValueConfig): boolean {
		const hasTextChanged = () => newValue.text !== this.transientValue.text;
		const hasMarkersChanged = () => JSON.stringify(newValue.markers) !== JSON.stringify(this.transientValue.markers);
		return hasTextChanged() || hasMarkersChanged();
	}

	private triggerTransientChangeEvent(currentValue: UiTextColorMarkerFieldValueConfig) {
		this.onTransientChange?.fire({ value: { ...currentValue } });
	}

	private getCurrentValue(): UiTextColorMarkerFieldValueConfig {
		const text = this.getCurrentText()?.replace(/\u00A0/g, ' ');
		const markers = this.getCurrentMarkers();

		return { text, markers };
	}

	private getCurrentText(node: Node = this.$editor): string {
		if (!node.childNodes || node.childNodes.length === 0) {
			if (node.nodeName.toUpperCase() === 'BR') { return '\n'; }
			return node.textContent || '';
		}
		let text = '';
		for (let i = 0; i < node.childNodes.length; i++) {
			text += this.getCurrentText(node.childNodes.item(i));
		}
		return text;
	}

	private getCurrentMarkers(): UiTextColorMarkerFieldMarkerConfig[] {
		const markers: UiTextColorMarkerFieldMarkerConfig[] = [];

		// Find all marker spans and extract their data
		const markerSpans = this.$editor.querySelectorAll('span[data-marker-id]');
		markerSpans.forEach(span => {
			const markerId = span.getAttribute('data-marker-id');
			if (markerId) {
				const id = Number(markerId);
				const start = this.getNodePosition(span.firstChild || span);
				const end = start + this.getCurrentText(span).length;
				markers.push({ id, start, end });
			}
		});
		return this.sortMarkers(markers);
	}

	private updateTransientValue(value: UiTextColorMarkerFieldValueConfig): boolean {
		// Validate marker positions
		for (const marker of value.markers) {
			if (marker.start === undefined || marker.end === undefined || marker.start < 0 || marker.end > value.text.length || marker.start > marker.end) {
				throw new Error('Invalid marker positions');
			}
			if (marker.start === marker.end) {
				value.markers = value.markers.filter(m => m.id !== marker.id);
			}
		}

		// Sort markers
		value.markers = this.sortMarkers(value.markers);

		// Check if this would actually change anything
		if (!this.hasTransientValueChanged(value)) {
			return false;
		}

		// Update the transient value
		this.transientValue = value;
		this.renderTransientValue();
		return true;
	}

	private sortMarkers(markers: UiTextColorMarkerFieldMarkerConfig[]): UiTextColorMarkerFieldMarkerConfig[] {
		return [...markers].sort((a, b) => a.id - b.id);
	}

	// Main render method: escapes text, applies markers, and sets innerHTML
	private renderTransientValue(): void {
		const rawText = this.transientValue.text ?? '';
		this.$editor.innerHTML = this.renderWithMarkers(rawText, this.transientValue.markers);
		this.triggerTransientChangeEvent(this.transientValue);
	}

	// Injects marker spans at the correct offsets
	private renderWithMarkers(text: string, markers: UiTextColorMarkerFieldMarkerConfig[]): string {
		const operations: Array<{ type: 'open' | 'close', marker: UiTextColorMarkerFieldMarkerConfig }> = [];

		for (const marker of markers) {
			operations.push({ type: 'open', marker });
			operations.push({ type: 'close', marker });
		}

		operations.sort((a, b) => {
			const posA = a.type === 'open' ? a.marker.start! : a.marker.end!;
			const posB = b.type === 'open' ? b.marker.start! : b.marker.end!;
			if (posA !== posB) { return posA - posB; }
			if (a.type !== b.type) { return a.type === 'open' ? 1 : -1; }
			if (a.type === 'open') { return a.marker.end! > b.marker.end! ? -1 : 1; }
			return a.marker.end! > b.marker.end! ? 1 : -1; // else
		});

		const result: string[] = [];
		let currentPos = 0;

		for (const op of operations) {
			const pos = op.type === 'open' ? op.marker.start! : op.marker.end!;

			if (pos > currentPos) {
				result.push(this.escapeHtml(text.slice(currentPos, pos)));
			}

			if (op.type === 'open') {
				const def = this.getMarkerDefinitionById(op.marker.id);
				const style = [];
				if (def?.backgroundColor) {
					style.push(`--marker-bg-color:${def.backgroundColor}`);
				}
				if (def?.borderColor) {
					style.push(`--marker-border-color:${def.borderColor}`);
				}
				result.push(`<span data-marker-id="${op.marker.id}" class="marker" style="${style.join(';')}" title="${def?.hint}">`);
			} else {
				result.push('</span>');
			}

			currentPos = pos;
		}

		if (currentPos < text.length) {
			result.push(this.escapeHtml(text.slice(currentPos)));
		}

		return result.join('');
	}

	private escapeHtml(text: string): string {
		return text.replace(/[&<>]/g, c => {
			if (c === '<') { return '&lt;'; }
			if (c === '>') { return '&gt;'; }
			if (c === '&') { return '&amp;'; }
			return c;
		});
	}

	private setupEventListeners(): void {
		const handleSelection = () => {
			const selection = window.getSelection();
			if (selection && selection.rangeCount > 0) {
				const range = selection.getRangeAt(0);
				if (this.$editor.contains(range.commonAncestorContainer)) {
					const start = this.getNodePosition(range.startContainer) + range.startOffset;
					const end = this.getNodePosition(range.endContainer) + range.endOffset;

					if (start !== end) {
						this.onTextSelected.fire({ start, end });
					}
				}
			}
		};
		this.$editor.addEventListener('mouseup', handleSelection);
		this.$editor.addEventListener('keyup', handleSelection); // only needed for selection via keyboard

		this.$editor.addEventListener('blur', () => {
			this.commitTransientChanges();
		});
	}

	private getNodePosition(node: Node): number {
		let position = 0;
		const walker = document.createTreeWalker(
			this.$editor,
			NodeFilter.SHOW_TEXT | NodeFilter.SHOW_ELEMENT,
			null
		);

		let currentNode: Node | null = walker.firstChild();
		while (currentNode && currentNode !== node) { // stops looping at "node"
			if (currentNode.nodeType === Node.TEXT_NODE) {
				position += currentNode.textContent?.length || 0;
			} else if (currentNode.nodeType === Node.ELEMENT_NODE) {
				const element = currentNode as HTMLElement;
				if (element.tagName === 'BR') {
					position += 1; // Count newlines
				}
			}
			currentNode = walker.nextNode();
		}

		return position;
	}

	public getDefaultValue(): UiTextColorMarkerFieldValueConfig {
		return {
			text: '',
			markers: []
		};
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	public focus(): void {
		this.$editor.focus();
	}

	public isValidData(v: any): boolean {
		return true; /*TODO*/
	}

	protected displayCommittedValue(): void {
		this.setTransientValue(this.getCommittedValue());
		//this.$editor.innerText = this.getCommittedValue()?.text ?? ''; // TODO use setTransientValue() instead?!
	}

	public valuesChanged(v1: UiTextColorMarkerFieldValueConfig, v2: UiTextColorMarkerFieldValueConfig): boolean {
		return deepEquals(v1, v2);
	}

	// protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
	// 	UiField.defaultOnEditingModeChangedImpl(this, () => this.$editor /*TODO*/);
	// }
	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		switch (editingMode) {
			case UiFieldEditingMode.EDITABLE:
			case UiFieldEditingMode.EDITABLE_IF_FOCUSED:
				this.$editor.contentEditable = 'true';
				this.$editor.setAttribute('tabindex', '0');
				break;
			case UiFieldEditingMode.DISABLED:
				this.$editor.contentEditable = 'false';
				this.$editor.setAttribute('tabindex', '-1');
				break;
			case UiFieldEditingMode.READONLY:
				this.$editor.contentEditable = 'false';
				this.$editor.setAttribute('tabindex', '-1');
				break;
			default:
				this.logger.error("unknown editing mode! " + editingMode);
		}
	}

	public setToolbarEnabled(enabled: boolean) {
		this.$toolbarWrapper.innerText = "" + enabled;  // TODO
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTextColorMarkerField", UiTextColorMarkerField);
