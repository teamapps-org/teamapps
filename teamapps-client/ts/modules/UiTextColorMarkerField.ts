import {AbstractUiComponent} from "./AbstractUiComponent";
import {
	UiTextColorMarkerFieldCommandHandler,
	UiTextColorMarkerFieldConfig,
	UiTextColorMarkerFieldEventSource,
	UiTextColorMarkerField_TextSelectedEvent,
	UiTextColorMarkerField_TransientChangeEvent
} from "../generated/UiTextColorMarkerFieldConfig";
import {UiField} from "./formfield/UiField";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiTextColorMarkerFieldMarkerDefinitionConfig} from "../generated/UiTextColorMarkerFieldMarkerDefinitionConfig";
import {UiTextColorMarkerFieldValueConfig} from "../generated/UiTextColorMarkerFieldValueConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {deepEquals, parseHtml} from "./Common";


export class UiTextColorMarkerField extends UiField<UiTextColorMarkerFieldConfig, UiTextColorMarkerFieldValueConfig> implements UiTextColorMarkerFieldCommandHandler, UiTextColorMarkerFieldEventSource {

	public readonly onTextSelected: TeamAppsEvent<UiTextColorMarkerField_TextSelectedEvent> = new TeamAppsEvent();
	public readonly onTransientChange: TeamAppsEvent<UiTextColorMarkerField_TransientChangeEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $toolbarWrapper: HTMLElement;
	private $editor: HTMLElement;

	protected initialize(config: UiTextColorMarkerFieldConfig, context: TeamAppsUiContext): void {
		 this.$main = parseHtml(`<div class="UiTextColorMarkerField">
			<div class="toolbar-wrapper"></div>
			<div class="editor field-border" editable="editable">
			</div>
		</div>`);
		this.$toolbarWrapper =this.$main.querySelector(":scope > .toolbar-wrapper");
		 this.$editor = this.$main.querySelector(":scope > .editor");
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	focus(): void {
		this.$editor.focus();
	}

	isValidData(v: any): boolean {
		return true;
	}

	getTransientValue() {
	  return {
		  markers: [],
		  text: ""
	  } as UiTextColorMarkerFieldValueConfig;
	}

	protected displayCommittedValue(): void {
		this.$editor.innerText = this.getCommittedValue()?.text ?? '';
	}

	public valuesChanged(v1: UiTextColorMarkerFieldValueConfig, v2: UiTextColorMarkerFieldValueConfig): boolean {
		return deepEquals(v1, v2);
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this, () => null /*TODO*/);
	}




	setMarker(id: number, start: number, end: number) {
		throw new Error("Method not implemented.");
	}

	setMarkerDefinitions(markerDefinitions: UiTextColorMarkerFieldMarkerDefinitionConfig[], newValue: UiTextColorMarkerFieldValueConfig) {
		throw new Error("Method not implemented.");
	}

	setToolbarEnabled(enabled: boolean) {
		this.$toolbarWrapper.innerText = "" + enabled
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTextColorMarkerField", UiTextColorMarkerField);



// const textColorMarkerDefaultConfig = {
// 	colorSequence: ['purple', 'fuchsia', 'navy', 'blue', 'teal', 'aqua', 'green', 'lime', 'olive', 'yellow', 'maroon', 'red', 'orange'],
// 	mode: null,
// 	input: null, // { value: 'test any text', markers: [{start: 0, end: 4, value: 'test'}, ...] }
// 	output: {},
// };
//
// function initColorMarkerTextField(fieldId) {
// 	function findSelection() { // cross-browser fix
// 		let userSelection;
// 		if (window.getSelection) {
// 			userSelection = window.getSelection(); // Mozilla Selection object.
// 		} else if (document.selection) {
// 			userSelection = document.selection.createRange();
// 		} // gets Microsoft Text Range, should be second b/c Opera has poor support for it.
// 		if (userSelection.text) {
// 			return userSelection.text // for Microsoft Objects.
// 		} else {
// 			return userSelection // for Mozilla&Co Objects.
// 		}
// 	}
//
// 	function extractNodeText(node) {
// 		let text = '';
// 		if (!node.childNodes || node.childNodes.length === 0) {
// 			if (node.nodeName.toUpperCase() === 'BR') {
// 				text += '\n';
// 			} else if (node.textContent) {
// 				text += node.textContent.replaceAll('\n', ''); // for consistency: only <br> is new line
// 			}
// 		} else {
// 			for (let i = 0; i < node.childNodes.length; i++) {
// 				text += extractNodeText(node.childNodes.item(i));
// 			}
// 		}
// 		return text;
// 	}
//
// 	function extractConfig(root) {
// 		const config = {...JSON.parse(JSON.stringify(textColorMarkerDefaultConfig)), ...JSON.parse(root.getAttribute('data-config') || '{}')};
// 		if (root.getAttribute('data-input')) {
// 			config.input = JSON.parse(root.getAttribute('data-input'));
// 		}
// 		if (root.getAttribute('data-output')) {
// 			config.output = JSON.parse(root.getAttribute('data-output'));
// 		}
// 		if (!config.output) {
// 			config.output = {};
// 		}
// 		if (!config.output.markers) {
// 			config.output.markers = [];
// 		}
// 		if (root.innerHTML && !config.output.value) {
// 			config.output.value = extractNodeText(root); // output text value
// 		}
// 		if (!config.mode) {
// 			config.mode = config.input ? 'assigning' : 'creating';
// 		}
// 		return config;
// 	}
//
// 	const component = {
// 		root: null,
// 		config: null,
// 		pickColor: function (markerIdx) {
// 			if (typeof this.config.colorSequence === 'function') {
// 				return this.config.colorSequence(markerIdx);
// 			}
// 			return this.config.colorSequence[markerIdx] || 'gray';
// 		},
// 		pickColorLabel: function (markerIdx, marker) {
// 			return marker.value || (markerIdx + 1).toString()
// 		},
// 		hasMarker: function (markerIdx, data) {
// 			return data.markers[markerIdx]?.node !== undefined;
// 		},
// 		setMarker: function (selection, markerIdx, data, readonly = false) {
// 			if (this.hasMarker(markerIdx, data)) {
// 				this.unsetMarker(markerIdx, data);
// 			}
// 			const node = document.createElement('span');
// 			node.style.backgroundColor = this.pickColor(markerIdx);
// 			node.className = 'marker';
// 			node.setAttribute('data-index', markerIdx);
// 			const range = selection.getRangeAt(0);
// 			node.append(range.extractContents());
// 			selection.removeAllRanges();
// 			range.deleteContents(); // Remove selected text
// 			range.insertNode(node); // Insert colored span
// 			data.markers[markerIdx] = this.createMarkerData(node);
// 			if (this.config.input?.markers[markerIdx]?.button) {
// 				this.config.input.markers[markerIdx].button.style.backgroundColor = null;
// 			}
//
// 			if (!readonly) {
// 				node.onclick = function () {
// 					this.unsetMarker(markerIdx, data)
// 				}.bind(this);
// 			}
// 			this.exportOutput();
// 		},
// 		unsetMarker: function (markerIdx, data) {
// 			const node = data.markers[markerIdx]?.node;
// 			if (node?.parentNode) { // only needed if node still exists
// 				node.parentNode.replaceChild(document.createTextNode(node.textContent), node);
// 			}
// 			if (data.markers[markerIdx]) {
// 				data.markers[markerIdx] = null;
// 				if (this.config.input?.markers[markerIdx]?.button) {
// 					this.config.input.markers[markerIdx].button.style.backgroundColor = this.pickColor(markerIdx);
// 				}
// 				this.exportOutput();
// 			}
// 		},
// 		updateMarkers: function (node, data) {
// 			const elements = Array.from(node.getElementsByClassName('marker'));
// 			const existingIdx = elements.filter(el => el.hasAttribute('data-index')).map(el => el.getAttribute('data-index'));
// 			data.markers.forEach(function (marker, idx) {
// 				if (!marker) {
// 					return;
// 				}
// 				if (!existingIdx.includes(idx.toString())) {
// 					this.unsetMarker(idx, data);
// 					return;
// 				}
// 				data.markers[idx] = this.createMarkerData(marker.node);
// 			}.bind(this));
// 			this.exportOutput(this.config);
// 		},
// 		nextMarkerIndex: function (data) {
// 			let i = 0;
// 			while (data.markers[i]?.node) {
// 				i++;
// 			}
// 			return i;
// 		},
// 		createMarkerData: function (node) {
// 			const parent = node.parentNode;
// 			let offset = 0;
// 			for (let i = 0; i < parent.childNodes.length; i++) {
// 				const child = parent.childNodes.item(i);
// 				if (child === node) {
// 					break;
// 				}
// 				offset += extractNodeText(child).length;
// 			}
// 			return {
// 				start: offset,
// 				end: offset + node.textContent.length,
// 				value: node.textContent,
// 				node: node,
// 			};
// 		},
// 		seekNodeWithOffset: function (node, offset) {
// 			for (let i = 0; i < node.childNodes.length; i++) {
// 				const child = node.childNodes.item(i);
// 				offset -= child.textContent.length;
// 				if (offset <= 0) {
// 					return {node: child, offset: offset + child.textContent.length};
// 				}
// 				if (child.nodeName.toUpperCase() === 'BR') {
// 					offset--;
// 				}
// 			}
// 			return {node: node.lastChild, offset: offset};
// 		},
// 		createSelection: function (node, marker) {
// 			const range = document.createRange();
// 			const startPos = this.seekNodeWithOffset(node, marker.start);
// 			range.setStart(startPos.node, startPos.offset);
// 			let endPos = this.seekNodeWithOffset(node, marker.end);
// 			range.setEnd(endPos.node, endPos.offset);
// 			const selection = findSelection();
// 			selection.removeAllRanges();
// 			selection.addRange(range);
// 			return selection;
// 		},
// 		setTextWithMarkers: function (node, data, readonly = false) {
// 			node.innerHTML = data.value?.replaceAll(' ', '\u00A0')?.replaceAll('\n', '<br>') || '';
// 			if (data.markers) {
// 				for (let i = 0; i < data.markers.length; i++) {
// 					if (data.markers[i]) {
// 						this.setMarker(this.createSelection(node, data.markers[i]), i, data, readonly);
// 					}
// 				}
// 			}
// 		},
// 		applyColorOnSelection: function (markerIdx, data) {
// 			const selection = findSelection();
// 			if (selection.rangeCount > 0 && !selection.isCollapsed) {
// 				if (data.node.contains(selection.getRangeAt(0).startContainer)) { // ensure that selection is within component
// 					this.setMarker(selection, markerIdx, data);
// 				}
// 			} else {
// 				this.unsetMarker(markerIdx, data);
// 			}
// 		},
// 		setupTextTemplate: function () {
// 			this.config.input.node = document.createElement('div');
// 			this.config.input.node.className = 'textTemplate';
// 			this.root.appendChild(this.config.input.node);
// 			this.setTextWithMarkers(this.config.input.node, this.config.input, true);
// 		},
// 		setupTextEditor: function () {
// 			this.config.output.node = document.createElement('div');
// 			this.config.output.node.className = 'textInput';
// 			this.config.output.node.setAttribute('contenteditable', 'true');
// 			this.root.appendChild(this.config.output.node);
// 			this.setTextWithMarkers(this.config.output.node, this.config.output);
// 			this.config.output.node.addEventListener('keydown', function (e) { // cross-browser fix
// 				if (e.isComposing || e.keyCode === 13) { // = enter/return
// 					e.preventDefault(); // this is needed to fix cross-browser behavior of new line within contenteditable elements
// 					const selection = findSelection();
// 					const range = selection.getRangeAt(0);
//
// 					function keepWhitespace(rangeNode, rangeStartOffset, rangeEndOffset) {
// 						const range = document.createRange();
// 						range.setStart(rangeNode, rangeStartOffset);
// 						range.setEnd(rangeNode, rangeEndOffset);
// 						if (range.toString() === ' ') {
// 							range.deleteContents();
// 							range.insertNode(document.createTextNode('\u00A0'));
// 						}
// 					}
//
// 					keepWhitespace(range.endContainer, range.endOffset, Math.min(range.endOffset + 1, range.endContainer.length));
// 					range.deleteContents(); // remove selection if available
// 					range.insertNode(document.createElement('br'));
// 					keepWhitespace(range.startContainer, Math.max(0, range.startOffset - 1), range.startOffset);
// 					range.collapse(false); // cleanup selection
// 					selection.removeAllRanges();
// 					selection.addRange(range);
// 					this.updateMarkers(this.config.output.node, this.config.output);
// 					return false;
// 				}
// 			}.bind(this));
// 			this.config.output.node.addEventListener('input', function () { // on any input change
// 				this.updateMarkers(this.config.output.node, this.config.output);
// 			}.bind(this));
// 		},
// 		setupColorAssignmentButtons: function () {
// 			const colorButtons = document.createElement('div')
// 			colorButtons.className = 'colorButtons';
// 			for (let i = 0; i < this.config.input.markers.length; i++) {
// 				const marker = this.config.input.markers[i];
// 				marker.button = document.createElement('button');
// 				marker.button.textContent = this.pickColorLabel(i, marker);
// 				marker.button.style.backgroundColor = this.pickColor(i);
// 				marker.button.onclick = function () {
// 					this.applyColorOnSelection(i, this.config.output)
// 				}.bind(this);
// 				colorButtons.appendChild(marker.button);
// 			}
// 			this.root.appendChild(colorButtons);
// 		},
// 		setupColorCreationButton: function () {
// 			const colorButtons = document.createElement('div')
// 			colorButtons.className = 'colorButtons';
// 			const button = document.createElement('button');
// 			button.textContent = 'Mark selected Text';
// 			button.onclick = function () {
// 				this.applyColorOnSelection(this.nextMarkerIndex(this.config.output), this.config.output)
// 			}.bind(this);
// 			colorButtons.appendChild(button);
// 			this.root.appendChild(colorButtons);
// 		},
// 		cleanOutput: function (data) {
// 			if (typeof data !== 'object' || data === null) {
// 				return (typeof data === 'string') ? data.replaceAll('\u00A0', ' ') : data;
// 			}
// 			const result = Array.isArray(data) ? [] : {};
// 			Object.keys(data).forEach(function (key) {
// 				const value = data[key];
// 				if (value === null || value === undefined || key === 'node') {
// 					return;
// 				}
// 				result[Array.isArray(result) ? result.length : key] = this.cleanOutput(value);
// 			}.bind(this));
// 			return result;
// 		},
// 		exportOutput: function () {
// 			if (this.config.output.node) {
// 				this.config.output.value = extractNodeText(this.config.output.node);
// 				const newOutput = this.cleanOutput(this.config.output);
// 				const jsonOutput = JSON.stringify(newOutput);
// 				const prevOutput = this.root.getAttribute('data-output');
// 				this.root.setAttribute('data-output', jsonOutput);
// 				if (jsonOutput !== prevOutput) {
// 					this.root.dispatchEvent(new CustomEvent('change', {detail: newOutput}));
// 					//} else { console.log('unchanged', this.root.id, jsonOutput);
// 				}
// 			}
// 		},
// 		checkOutput: function () {
// 			const errors = [];
// 			if (this.config.input.value && !this.config.output.value) {
// 				errors.push('Missing Text');
// 			}
// 			for (const i in (this.config.input.markers || [])) {
// 				const marker = this.config.input.markers[i];
// 				if (marker && !this.config.output.markers[i]) {
// 					errors.push('Missing Marker: ' + marker.value);
// 				}
// 			}
// 			return errors;
// 		}
// 	};
//
// 	function initComponent(component) {
// 		component.root.innerHTML = ''; // clear out before rendering
// 		component.root.textColorMarker.getStatus = () => 0; // Fallback: always status OK
// 		component.root.textColorMarker.getErrors = () => []; // Fallback: no error messages
// 		switch (component.config.mode) {
// 			case 'creating':
// 				component.setupColorCreationButton();
// 				component.setupTextEditor();
// 				break;
// 			case 'editing':
// 				if (!component.config.input) {
// 					throw new Error('Missing input! The attribute "data-input" must be specified.');
// 				}
// 				component.config.output = component.cleanOutput(component.config.input);
// 				component.setupColorAssignmentButtons();
// 				component.setupTextEditor();
// 				break;
// 			case 'assigning':
// 				if (!component.config.input) {
// 					throw new Error('Missing input! The attribute "data-input" must be specified.');
// 				}
// 				component.setupTextTemplate();
// 				component.setupColorAssignmentButtons();
// 				component.setupTextEditor();
// 				component.root.textColorMarker.getStatus = function () {
// 					return this.checkOutput().length ? 1 : 0;
// 				}.bind(component);
// 				component.root.textColorMarker.getErrors = function () {
// 					return this.checkOutput();
// 				}.bind(component);
// 				break;
// 		}
// 		component.exportOutput();
// 	}
//
// 	component.root = document.getElementById(fieldId);
// 	component.config = extractConfig(component.root);
// 	component.root.textColorMarker = {};
// 	component.root.addEventListener('change', e => console.log('changed', e.target.id, JSON.stringify(e.detail)));
// 	initComponent(component);
//
// 	// listen for input changes:
// 	new MutationObserver(function (mutations) {
// 		mutations.forEach(function (mutation) {
// 			if (mutation.type === 'attributes' && mutation.attributeName === 'data-input'
// 				&& JSON.stringify(component.config.input) !== component.root.getAttribute('data-input')) {
// 				component.config.input = JSON.parse(component.root.getAttribute('data-input') || 'null');
// 				initComponent(component);
// 			}
// 		}.bind(this));
// 	}).observe(component.root, {attributes: true});
//
// 	return component.root.textColorMarker;
// }
//
// initColorMarkerTextField('color-marker')
// initColorMarkerTextField('color-marker1')
// document.getElementById('color-marker').addEventListener('change', function (e) {
// 	//document.getElementById('color-marker1').setAttribute('data-input', JSON.stringify(e.detail));
// });
// initColorMarkerTextField('color-marker2')
