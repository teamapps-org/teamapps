import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {
	UiTextColorMarkerField_TextSelectedEvent,
	UiTextColorMarkerFieldCommandHandler,
	UiTextColorMarkerFieldConfig,
	UiTextColorMarkerFieldEventSource
} from "../../generated/UiTextColorMarkerFieldConfig";
import {UiTextColorMarkerFieldMarkerConfig} from "../../generated/UiTextColorMarkerFieldMarkerConfig";
import {UiTextColorMarkerFieldMarkerDefinitionConfig} from "../../generated/UiTextColorMarkerFieldMarkerDefinitionConfig";
import {UiTextColorMarkerFieldValueConfig} from "../../generated/UiTextColorMarkerFieldValueConfig";
import {escapeHtml, parseHtml} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiField} from "./UiField";

export class UiTextColorMarkerField extends UiField<UiTextColorMarkerFieldConfig, UiTextColorMarkerFieldValueConfig> implements UiTextColorMarkerFieldCommandHandler, UiTextColorMarkerFieldEventSource {

	public readonly onTextSelected: TeamAppsEvent<UiTextColorMarkerField_TextSelectedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $toolbarWrapper: HTMLElement;
	private $editor: HTMLDivElement;

	private markerDefinitions: UiTextColorMarkerFieldMarkerDefinitionConfig[];
	private toolbarEnabled: boolean;
	private currentSelection: UiTextColorMarkerField_TextSelectedEvent | null = null;

	protected initialize(config: UiTextColorMarkerFieldConfig, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="UiTextColorMarkerField default-min-field-width teamapps-input-wrapper field-border field-border-glow field-background">
			<div class="toolbar-wrapper hidden" tabindex="0"></div>
			<div class="editor" contenteditable="true" tabindex="0"></div>
		</div>`);
		this.$toolbarWrapper = this.$main.querySelector(":scope > .toolbar-wrapper");
		this.$editor = this.$main.querySelector(":scope > .editor");
		//this.$editor.contentEditable = 'true';
		this.toolbarEnabled = config.toolbarEnabled;
		this.setupEventListeners();
		this.setMarkerDefinitions(config.markerDefinitions, config.value);
	}

	private getMarkerDefinitionById(id: number): UiTextColorMarkerFieldMarkerDefinitionConfig | undefined {
		return this.markerDefinitions.find(definition => definition.id === id);
	}

	public setMarkerDefinitions(markerDefinitions: UiTextColorMarkerFieldMarkerDefinitionConfig[], newValue: UiTextColorMarkerFieldValueConfig): void {
		this.markerDefinitions = markerDefinitions;
		this.setValue(this.createFieldValue(
			newValue?.text,
			newValue?.markers
		));
	}

	public setMarker(markerDefinitionId: number, start: number, end: number, fireMarkerChangeEvent: boolean = false): void {
		const newMarkers = this.getCommittedValue().markers.filter(m => m.markerDefinitionId !== markerDefinitionId);
		newMarkers.push(this.createMarker(markerDefinitionId, start, end));

		this.commitAndSetMarkers(this.normalizeMarkers(newMarkers), fireMarkerChangeEvent);
	}

	public removeMarker(markerDefinitionId: number): void {
		const transientMarkers = this.getTransientMarkers();
		const newMarkers = transientMarkers.filter(m => m.markerDefinitionId !== markerDefinitionId);
		this.commitAndSetMarkers(newMarkers, true);
	}

	private commitAndSetMarkers(markers: UiTextColorMarkerFieldMarkerConfig[], fireMarkerChangeEvent: boolean): void {
		const value = this.createFieldValue(this.getTransientText(), markers);
		const committedValue = this.getCommittedValue();
		const changed = this.valuesChanged(value, committedValue);
		if (changed) {
			this.setCommittedValue(value);
			if (fireMarkerChangeEvent || this.textChanged(committedValue?.text, value?.text)) {
				this.fireValueChangedEvent();
			}
		}
	}

	private fireValueChangedEvent(): void {
		this.logger.trace("firing value changed event: " + JSON.stringify(this.getCommittedValue()));
		this.onValueChanged.fire({
			value: this.convertValueForSendingToServer(this.getCommittedValue())
		});
	}

	public valuesChanged(v1: UiTextColorMarkerFieldValueConfig, v2: UiTextColorMarkerFieldValueConfig): boolean {
		if (!v1 || !v2) {
			return v1 !== v2;
		}
		return this.textChanged(v1.text, v2.text) || this.markersChanged(v1.markers, v2.markers);
	}

	private textChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

	private markersChanged(v1: UiTextColorMarkerFieldMarkerConfig[], v2: UiTextColorMarkerFieldMarkerConfig[]): boolean {
		return JSON.stringify(v1) !== JSON.stringify(v2);
	}

	public getTransientValue(): UiTextColorMarkerFieldValueConfig {
		const text = this.getTransientText()?.replace(/\u00A0/g, ' ');
		const markers = this.getTransientMarkers();

		return this.createFieldValue(text, markers);
	}

	private getTransientText(node: Node = this.$editor): string {
		if (!node.childNodes || node.childNodes.length === 0) {
			if (node.nodeName.toUpperCase() === 'BR') { return '\n'; }
			return node.textContent || '';
		}
		let text = '';
		for (let i = 0; i < node.childNodes.length; i++) {
			text += this.getTransientText(node.childNodes.item(i));
		}
		return text;
	}

	private getTransientMarkers(): UiTextColorMarkerFieldMarkerConfig[] {
		const markers: UiTextColorMarkerFieldMarkerConfig[] = [];

		// Find all marker spans and extract their data
		const markerSpans = this.$editor.querySelectorAll('span[data-marker-id]');
		markerSpans.forEach(span => {
			const markerId = span.getAttribute('data-marker-id');
			if (markerId) {
				const markerDefinitionId = Number(markerId);
				const start = this.getNodePosition(span.firstChild || span);
				const end = start + this.getTransientText(span).length;
				markers.push(this.createMarker(markerDefinitionId, start, end));
			}
		});
		return this.normalizeMarkers(markers);
	}

	public setValue(value: UiTextColorMarkerFieldValueConfig): void {
		// Sort markers by their ID + remove empty markers (start == end)
		value.markers = this.normalizeMarkers(value.markers);

		// Check if this would actually change anything
		if (this.valuesChanged(this.getCommittedValue(), value)) {
			this.setCommittedValue(value);
		}
	}

	private normalizeMarkers(markers: UiTextColorMarkerFieldMarkerConfig[]): UiTextColorMarkerFieldMarkerConfig[] {
		return [...markers.filter(m => m.start !== m.end)]
			.sort((a, b) => a.markerDefinitionId - b.markerDefinitionId);
	}

	protected displayCommittedValue(): void {
		const value = this.getCommittedValue();
		this.$editor.innerHTML = this.renderWithMarkers(value.text ?? '', value.markers ?? []);
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
				const def = this.getMarkerDefinitionById(op.marker.markerDefinitionId);
				const style = [];
				if (def?.backgroundColor) {
					style.push(`--marker-bg-color:${this.escapeHtmlAttr(def.backgroundColor)}`);
					style.push(`--marker-text-color:${this.getContrastColor(def.backgroundColor)}`);
				}
				if (def?.borderColor) {
					style.push(`--marker-border-color:${this.escapeHtmlAttr(def.borderColor)}`);
				}
				result.push(`<span data-marker-id="${op.marker.markerDefinitionId}" class="marker" style="${style.join(';')}" title="${this.escapeHtmlAttr(def?.hint)}">`);
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
		return text?.replace(/[&<>]/g, c => {
			if (c === '<') { return '&lt;'; }
			if (c === '>') { return '&gt;'; }
			if (c === '&') { return '&amp;'; }
			return c;
		});
	}

	private escapeHtmlAttr(text: string): string {
		return escapeHtml(text?.replace(/"'/g, c => {
			if (c === '"') { return '&quot;'; }
			if (c === "'") { return '&#39;'; }
			return c;
		}));
	}

	private setupEventListeners(): void {
		const handleSelection = () => {
			let noSelection = true;
			const selection = window.getSelection();
			if (selection && selection.rangeCount > 0) {
				const range = selection.getRangeAt(0);
				if (this.$editor.contains(range.commonAncestorContainer)) {
					const start = this.getNodePosition(range.startContainer) + range.startOffset;
					const end = this.getNodePosition(range.endContainer) + range.endOffset;
					if (start < end) {
						noSelection = false;
						this.triggerSelection({ start, end });
					}
				}
			}
			if (noSelection) {
				this.triggerDeselection();
			}
		};
		this.$editor.addEventListener('mouseup', handleSelection);
		this.$editor.addEventListener('keyup', handleSelection); // only needed for selection via keyboard
		this.$editor.addEventListener('click', (e) => {
			const target = e.target as HTMLElement;
			if (target.classList.contains('marker') && this.isMarkerChangeAllowed()) {
				const markerId = parseInt(target.getAttribute('data-marker-id') || '0', 10);
				if (markerId) {
					this.removeMarker(markerId);
				}
			}
		});

		this.$editor.addEventListener('blur', e => {
			this.commit();
			// Only hide toolbar if the focus is not moving to the toolbar
			if (!this.$toolbarWrapper.contains(e.relatedTarget as Node)) {
				this.triggerDeselection();
			}
		});
	}

	public setToolbarEnabled(enabled: boolean): void {
		this.toolbarEnabled = enabled;
		if (!enabled) {
			this.hideToolbar();
		}
	}

	private triggerSelection(selection: UiTextColorMarkerField_TextSelectedEvent): void {
		this.currentSelection = selection;
		if (this.isMarkerChangeAllowed()) {
			if (this.toolbarEnabled) {
				this.showToolbar();
			}
			this.onTextSelected.fire(this.currentSelection);
		}
	}

	private triggerDeselection(forceCursorReset: boolean = false): void {
		if (this.currentSelection) {
			if (forceCursorReset) {
				window.getSelection()?.removeAllRanges(); //  preventing missplaced cursor after setting marker via toolbar for Safari
			}
			this.currentSelection = null;
			this.onTextSelected.fire({ start: 0, end: 0 }); // inform about selection removal
		}
		this.hideToolbar();
	}

	private showToolbar(): void {
		this.updateToolbarContent();
		this.$toolbarWrapper.classList.remove('hidden');
	}

	private hideToolbar(): void {
		this.$toolbarWrapper.classList.add('hidden');
	}

	private updateToolbarContent(): void {
		this.$toolbarWrapper.innerHTML = '';

		this.markerDefinitions.forEach(def => {
			const button = document.createElement('button');
			button.className = 'toolbar-button';
			button.innerHTML = this.escapeHtml(def.hint ?? '').replace(/\n/g, '<br>');

			// Style button with marker colors
			if (def.backgroundColor) {
				button.style.backgroundColor = def.backgroundColor;
				button.style.color = this.getContrastColor(def.backgroundColor);
			}
			if (def.borderColor) {
				button.style.borderColor = def.borderColor;
			}

			// Check if this marker is already applied
			if (this.getTransientMarkers().some(m => m.markerDefinitionId === def.id)) {
				button.classList.add('applied');
			}

			// Add click handler
			button.addEventListener('click', () => {
				if (this.currentSelection) {
					this.setMarker(def.id, this.currentSelection.start, this.currentSelection.end, true);
				}
				this.triggerDeselection(true);
			});

			this.$toolbarWrapper.appendChild(button);
		});
	}

	private getContrastColor(hexColor: string): string {
		// tslint:disable-next-line:one-variable-per-declaration
		let r: number, g: number, b: number;
		if (hexColor.startsWith('rgb(')) {
			const rgb = hexColor.replace('rgb(', '').replace(')', '').split(',');
			r = parseInt(rgb[0], 10);
			g = parseInt(rgb[1], 10);
			b = parseInt(rgb[2], 10);
		} else {
			const hex = hexColor.replace('#', '');
			r = parseInt(hex.substr(0, 2), 16);
			g = parseInt(hex.substr(2, 2), 16);
			b = parseInt(hex.substr(4, 2), 16);
		}
		const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;

		return luminance > 0.5 ? '#000000' : '#FFFFFF';
	}

	private getNodePosition(node: Node): number {
		let position = 0;
		const walker = document.createTreeWalker(
			this.$editor,
			// tslint:disable-next-line:no-bitwise
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
		return this.createFieldValue('', []);
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	public focus(): void {
		this.$editor.focus();
	}

	public isValidData(v: any): boolean {
		if (v == null || v.markers == null) {
			return true;
		}
		return !v.markers.some((m: any) => m === null
			|| !this.isMarkerValid(m, v));
	}

	private isMarkerValid(marker: UiTextColorMarkerFieldMarkerConfig, value: UiTextColorMarkerFieldValueConfig): boolean {
		if (!this.getMarkerDefinitionById(marker.markerDefinitionId)) {
			this.logger.warn("No definition found for this marker. Invalid marker: " + marker.markerDefinitionId);
			return false;
		}
		if (this.isMarkerOutOfRange(marker, value.text.length)) {
			this.logger.warn("Marker out of range. Invalid marker: " + JSON.stringify(marker));
			return false;
		}
		if (value.markers.some(otherMarker => this.isMarkerOverlappedButNotNested(otherMarker, marker))) {
			this.logger.warn("Marker overlaps with existing marker. Invalid marker: " + JSON.stringify(marker) + "\n Existing markers: " + JSON.stringify(value.markers));
			return false;
		}
		return true;
	}

	private isMarkerOutOfRange(marker: UiTextColorMarkerFieldMarkerConfig, textLength: number): boolean {
		return marker.start < 0
			|| marker.end > textLength
			|| marker.start > marker.end;
	}

	private isMarkerOverlappedButNotNested(otherMarker: UiTextColorMarkerFieldMarkerConfig, marker: UiTextColorMarkerFieldMarkerConfig): boolean {
		if (otherMarker.markerDefinitionId === marker.markerDefinitionId) { return false; } // Skip the same marker
		// Two markers overlap if one starts before the other ends and ends after the other starts
		// But we allow them to be nested
		const hasOverlap = () => marker.start < otherMarker.end && marker.end > otherMarker.start;
		const isNested = () => (marker.start <= otherMarker.start && marker.end >= otherMarker.end) ||
			(otherMarker.start <= marker.start && otherMarker.end >= marker.end);
		return hasOverlap() && !isNested();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainElement().classList.add(UiField.editingModeCssClasses[this.getEditingMode()]);
		this.getMainInnerDomElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainInnerDomElement().classList.add(UiField.editingModeCssClasses[this.getEditingMode()]);

		const toolbarTabIndex = this.isMarkerChangeAllowed() ? '0' : '-1';
		switch (editingMode) {
			case UiFieldEditingMode.EDITABLE:
			case UiFieldEditingMode.EDITABLE_IF_FOCUSED:
				this.$editor.contentEditable = 'true';
				this.$editor.setAttribute('tabindex', '0');
				this.$toolbarWrapper.setAttribute('tabindex', toolbarTabIndex);
				break;
			case UiFieldEditingMode.DISABLED:
				this.$editor.contentEditable = 'false';
				this.$editor.setAttribute('tabindex', '-1');
				this.$toolbarWrapper.setAttribute('tabindex', toolbarTabIndex);
				break;
			case UiFieldEditingMode.READONLY:
				this.$editor.contentEditable = 'false';
				this.$editor.setAttribute('tabindex', '-1');
				this.$toolbarWrapper.setAttribute('tabindex', toolbarTabIndex);
				break;
			default:
				this.logger.error("unknown editing mode! " + editingMode);
		}
	}

	private isMarkerChangeAllowed(): boolean {
		return ![UiFieldEditingMode.DISABLED].includes(this.getEditingMode());
	}

	private createFieldValue(text: string, markers: UiTextColorMarkerFieldMarkerConfig[]): UiTextColorMarkerFieldValueConfig {
		return {
			_type: "UiTextColorMarkerFieldValue",
			text: text ?? '',
			markers: [...(markers ?? [])]
		};
	}

	private createMarker(markerDefinitionId: number, start: number, end: number): UiTextColorMarkerFieldMarkerConfig {
		return {
			_type: "UiTextColorMarkerFieldMarker",
			markerDefinitionId,
			start,
			end
		};
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiTextColorMarkerField", UiTextColorMarkerField);
