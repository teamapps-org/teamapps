/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
/*!
Trivial Components (https://github.com/trivial-components/trivial-components)

Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import {
	DEFAULT_TEMPLATES,
	EditingMode,
	generateUUID,
	keyCodes,
	objectEquals,
	RenderingFunction,
	setTimeoutOrDoImmediately,
	TrivialComponent,
	unProxyEntry
} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import {Instance as Popper} from '@popperjs/core';
import {parseHtml} from "../Common";
import {DropDownComponent, SelectionDirection} from "./dropdown/DropdownComponent";
import {createComboBoxPopper} from "./ComboBoxPopper";

export interface TrivialComboBoxConfig<E> {
	/**
	 * Rendering function used to display a _selected_ entry
	 * (i.e. an entry inside the editor area of the component, not the dropdown).
	 *
	 * @param entry
	 * @return HTML string
	 * @default `wrapWithDefaultTagWrapper(entryRenderingFunction(entry))`
	 */
	selectedEntryRenderingFunction: RenderingFunction<E>,

	/**
	 * Performance setting. Defines the maximum number of entries until which text highlighting is performed.
	 * Set to `0` to disable text highlighting.
	 *
	 * @default `100`
	 */
	textHighlightingEntryLimit?: number,

	/**
	 * Whether or not to provide auto-completion.
	 *
	 * @default `true`
	 */
	autoComplete?: boolean,

	/**
	 * The number of milliseconds to wait until auto-completion is performed.
	 *
	 * @default `0`
	 */
	autoCompleteDelay?: number,

	/**
	 * Used to set the editor's text when focusing the component.
	 * Additionally used to generate an autocompletion string for the current input of the user.
	 *
	 * @param entry the currently selected entry in the dropdown
	 */
	entryToEditorTextFunction: (entry: E) => string,

	/**
	 * Whether or not to allow free text to be entered by the user.
	 *
	 * @default `false`
	 */
	allowFreeText?: boolean,

	/**
	 * Creates an entry (object) from a string entered by the user.
	 *
	 * @param freeText the text entered by the user
	 * @default `{ displayValue: freeText, _isFreeTextEntry: true }`
	 */
	freeTextEntryFactory?: (freeText: string) => E | any,

	/**
	 * The clear button is a the small 'x' at the right of the entry display that can be clicked to clear the selection.
	 */
	showClearButton?: boolean,

	/**
	 * The trigger is the button on the right side of the component that can be clicket to open the dropdown.
	 *
	 * @default `true`
	 */
	showTrigger?: boolean,

	editingMode?: EditingMode,

	/**
	 * It `true`, opening the dropdown will be delayed until the result callback of the [[queryFunction]] is called.
	 *
	 * @default `false`
	 */
	showDropDownOnResultsOnly?: boolean,

	/**
	 * HTML string defining the spinner to be displayed while entries are being retrieved.
	 */
	spinnerTemplate?: string,

	/**
	 * When typing, preselect the first returned query result.
	 */
	preselectFirstQueryResult?: boolean,
}

export class TrivialComboBox<E> implements TrivialComponent {

	public readonly onSelectedEntryChanged = new TrivialEvent<E>(this);
	public readonly onFocus = new TrivialEvent<void>(this);
	public readonly onBlur = new TrivialEvent<void>(this);

	private config: TrivialComboBoxConfig<E>;

	private $comboBox: HTMLElement;
	private $dropDown: HTMLElement;
	private $editor: HTMLInputElement;
	private $selectedEntryWrapper: HTMLElement;
	private $trigger: HTMLElement;
	private $clearButton: HTMLElement;

	private popper: Popper;
	private dropDownComponent: DropDownComponent<E>;

	private selectedEntry: E = null;
	private lastCommittedValue: E = null;

	private blurCausedByClickInsideComponent = false;
	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;

	private editingMode: EditingMode;
	private _isDropDownOpen = false;
	private isEditorVisible = false;


	constructor(options: TrivialComboBoxConfig<E>, dropDownComponent?: DropDownComponent<E>) {
		this.config = {
			spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
			textHighlightingEntryLimit: 100,
			autoComplete: true,
			autoCompleteDelay: 0,
			allowFreeText: false,
			freeTextEntryFactory: (freeText: string) => {
				return {
					displayValue: freeText,
					id: generateUUID(),
					_isFreeTextEntry: true
				};
			},
			showClearButton: false,
			showTrigger: true,
			editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
			showDropDownOnResultsOnly: false,
			preselectFirstQueryResult: true,

			...options
		};
		this.dropDownComponent = dropDownComponent;

		this.$comboBox = parseHtml(`<div class="tr-combobox tr-input-wrapper editor-hidden">
            <div class="tr-combobox-selected-entry-wrapper"></div>
            <div class="tr-remove-button ${this.config.showClearButton ? '' : 'hidden'}"></div>
            <div class="tr-trigger ${this.config.showTrigger ? '' : 'hidden'}"><span class="tr-trigger-icon"></span></div>
        </div>`);
		this.$selectedEntryWrapper = this.$comboBox.querySelector(':scope .tr-combobox-selected-entry-wrapper');
		this.$clearButton = this.$comboBox.querySelector(':scope .tr-remove-button');
		this.$clearButton.addEventListener("mousedown", (e) => {
			this.$editor.value = "";
			this.setSelectedEntry(null, true, true, e);
		});
		this.$trigger = this.$comboBox.querySelector(':scope .tr-trigger');
		this.$trigger.addEventListener("mousedown", () => {
			if (this._isDropDownOpen) {
				this.showEditor();
				this.closeDropDown();
			} else if (this.editingMode === "editable") {
				this.showEditor();
				this.$editor.select();
				this.openDropDown();
				this.query();
			}
		});
		this.$dropDown = parseHtml('<div class="tr-dropdown"></div>');
		this.$dropDown.addEventListener("scroll", e => {
			e.stopPropagation();
			e.preventDefault();
		});
		this.setEditingMode(this.config.editingMode);
		this.$editor = parseHtml('<input type="text" autocomplete="off"></input>');
		this.$comboBox.prepend(this.$editor);
		this.$editor.classList.add("tr-combobox-editor", "tr-editor");
		this.$editor.addEventListener("focus", () => {
			this.onFocus.fire();
			this.$comboBox.classList.add('focus');
			if (!this.blurCausedByClickInsideComponent) {
				this.showEditor();
			}
		})
		this.$editor.addEventListener("blur", (e: FocusEvent) => {
			if (this.blurCausedByClickInsideComponent) {
				this.$editor.focus();
			} else {
				this.onBlur.fire();
				this.$comboBox.classList.remove('focus');
				if (this.editorContainsFreeText()) {
					if (!objectEquals(this.getSelectedEntry(), this.lastCommittedValue)) {
						this.setSelectedEntry(this.getSelectedEntry(), true, true, e);
					}
				} else {
					this.$editor.value = "";
					this.setSelectedEntry(this.lastCommittedValue, false, false, e);
				}
				this.hideEditor();
				this.closeDropDown();
			}
		})
		this.$editor.addEventListener("keydown", (e: KeyboardEvent) => {
			if (keyCodes.isModifierKey(e)) {
				return;
			} else if (e.which == keyCodes.tab || e.which == keyCodes.enter) {
				if (this.isEditorVisible || this.editorContainsFreeText()) {
					e.which == keyCodes.enter && e.preventDefault(); // do not submit form
					let highlightedEntry = this.dropDownComponent.getValue();
					if (this._isDropDownOpen && highlightedEntry) {
						this.setSelectedEntry(highlightedEntry, true, true, e);
					} else if (!this.$editor.value) {
						this.setSelectedEntry(null, true, true, e);
					} else if (this.config.allowFreeText) {
						this.setSelectedEntry(this.getSelectedEntry(), true, true, e);
					}
					this.closeDropDown();
					this.hideEditor();
				}
				return;
			} else if (e.which == keyCodes.left_arrow || e.which == keyCodes.right_arrow) {
				if (this._isDropDownOpen && this.dropDownComponent.handleKeyboardInput(e)) {
					e.preventDefault();
					return;
				} else {
					this.showEditor();
					return; // let the user navigate freely left and right...
				}
			} else if (e.which == keyCodes.backspace || e.which == keyCodes.delete) {
				this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
				setTimeout(() => this.query(0)); // asynchronously to make sure the editor has been updated
			} else if (e.which == keyCodes.up_arrow || e.which == keyCodes.down_arrow) {
				if (!this.isEditorVisible) {
					this.$editor.select();
					this.showEditor();
				}
				const direction = e.which == keyCodes.up_arrow ? -1 : 1;
				if (!this._isDropDownOpen) {
					this.query(direction);
					this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
				} else {
					if (this.dropDownComponent.handleKeyboardInput(e)) {
						if (this.dropDownComponent.getValue() != null) {
							this.$editor.value = this.config.entryToEditorTextFunction(this.dropDownComponent.getValue())
							this.$editor.select();
						} else {
							this.$editor.value = "";
						}
					}
				}
				e.preventDefault(); // some browsers move the caret to the beginning on up key
			} else if (e.which == keyCodes.escape) {
				if (!(this.editorContainsFreeText() && this._isDropDownOpen)) {
					this.hideEditor();
					this.$editor.value = "";
					this.setSelectedEntry(this.lastCommittedValue, false, false, e);
				}
				this.closeDropDown();
			} else {
				if (!this.isEditorVisible) {
					this.showEditor();
					this.$editor.select();
				}
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}

				// We need the new editor value (after the keydown event). Therefore setTimeout().
				setTimeout(() => this.query(this.config.preselectFirstQueryResult && this.$editor.value ? 1 : 0))
			}
		});
		this.$editor.addEventListener("mousedown", () => {
			if (this.editingMode === "editable") {
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.query();
			}
		});

		[this.$comboBox, this.$dropDown].forEach(element => {
			element.addEventListener("mousedown", () => {
				this.blurCausedByClickInsideComponent = true;
			}, true);
			element.addEventListener("mouseup", () => {
				if (this.blurCausedByClickInsideComponent) {
					this.$editor.focus();
					this.blurCausedByClickInsideComponent = false;
				}
			});
			element.addEventListener("mouseout", () => {
				if (this.blurCausedByClickInsideComponent) {
					this.$editor.focus();
					this.blurCausedByClickInsideComponent = false;
				}
			});
		});

		this.$dropDown.append(this.dropDownComponent.getMainDomElement());
		this.dropDownComponent.onValueChanged.addListener((eventData) => {
			if (eventData.finalSelection) {
				this.setSelectedEntry(eventData.value, true, !objectEquals(eventData.value, this.lastCommittedValue));
				this.closeDropDown();
				this.hideEditor();
			}
		});

		this.$selectedEntryWrapper.addEventListener("click", () => {
			if (this.editingMode === "editable") {
				this.showEditor();
				this.$editor.select()
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.query();
			}
		});

		this.popper = createComboBoxPopper(this.$comboBox, this.$dropDown, () => this.closeDropDown());
	}

	private async query(highlightDirection: SelectionDirection = 0) {
		let gotResultsForQuery = await this.dropDownComponent.handleQuery(this.getNonSelectedEditorValue(), highlightDirection);
		this.blurCausedByClickInsideComponent = false; // we won't get any mouseout or mouseup events for entries if they get removed. so do this here proactively

		this.autoCompleteIfPossible(this.config.autoCompleteDelay);

		if (this.config.showDropDownOnResultsOnly && gotResultsForQuery && document.activeElement == this.$editor) {
			this.openDropDown();
		}
	}

	private fireChangeEvents(entry: E, originalEvent?: unknown) {
		this.onSelectedEntryChanged.fire(unProxyEntry(entry), originalEvent);
	}

	public setSelectedEntry(entry: E, commit: boolean, fireEvent?: boolean, originalEvent?: unknown) {
		this.selectedEntry = entry;
		this.$selectedEntryWrapper.innerHTML = '';
		let $selectedEntry = parseHtml(this.config.selectedEntryRenderingFunction(entry));
		if ($selectedEntry != null) {
			$selectedEntry.classList.add("tr-combobox-entry");
			this.$selectedEntryWrapper.append($selectedEntry);
		}
		if (entry != null) {
			this.$editor.value = this.config.entryToEditorTextFunction(entry);
		}

		if (commit) {
			this.lastCommittedValue = entry;
			if (fireEvent) {
				this.fireChangeEvents(entry, originalEvent);
			}
		}
		if (this.$clearButton) {
			this.$clearButton.classList.toggle("hidden", entry == null);
		}
		if (this.isEditorVisible) {
			this.showEditor(); // reposition editor
		}
		if (this._isDropDownOpen) {
			this.repositionDropDown();
		}
	}

	private isEntrySelected() {
		return this.selectedEntry != null;
	}

	private editorContainsFreeText() {
		return this.config.allowFreeText && (this.$editor.value).length > 0 && !this.isEntrySelected();
	};

	private showEditor() {
		this.$comboBox.classList.remove("editor-hidden");
		this.isEditorVisible = true;
	}

	private hideEditor() {
		this.$comboBox.classList.add("editor-hidden");
		this.isEditorVisible = false;
	}

	private repositionDropDown() {
		this.popper.update();
	};

	private parentElement: Element;

	public openDropDown() {
		if (this.isDropDownNeeded()) {
			if (this.getMainDomElement().parentElement !== this.parentElement) {
				this.popper.destroy();
				this.popper = createComboBoxPopper(this.$comboBox, this.$dropDown, () => this.closeDropDown());
				this.parentElement = this.getMainDomElement().parentElement;
			}
			this.$comboBox.classList.add("open");
			this.repositionDropDown();
			this._isDropDownOpen = true;
		}
	}

	public closeDropDown() {
		this.$comboBox.classList.remove("open");
		this._isDropDownOpen = false;
	}

	private getNonSelectedEditorValue() {
		return this.$editor.value.substring(0, (this.$editor as any).selectionStart);
	}

	private autoCompleteIfPossible(delay?: number) {
		if (this.config.autoComplete) {
			clearTimeout(this.autoCompleteTimeoutId);
			const dropDownValue = this.dropDownComponent.getValue();
			console.log("dropdown value: ", dropDownValue);
			if (dropDownValue && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
				this.autoCompleteTimeoutId = setTimeoutOrDoImmediately(() => {
					const currentEditorValue = this.getNonSelectedEditorValue();
					const entryAsString = this.config.entryToEditorTextFunction(dropDownValue);
					if (entryAsString.toLowerCase().indexOf(("" + currentEditorValue).toLowerCase()) === 0) {
						this.$editor.value = currentEditorValue + entryAsString.substr(currentEditorValue.length);
						if (document.activeElement == this.$editor) {
							(this.$editor as any).setSelectionRange(currentEditorValue.length, entryAsString.length);
						}
					}
				}, delay);
			}
			this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
		}
	}

	private isDropDownNeeded() {
		return this.editingMode == 'editable';
	}

	public setEditingMode(newEditingMode: EditingMode) {
		this.editingMode = newEditingMode;
		this.$comboBox.classList.remove("editable", "readonly", "disabled");
		this.$comboBox.classList.add(this.editingMode);
		if (this.isDropDownNeeded()) {
			this.$comboBox.append(this.$dropDown);
		}
	}

	public getSelectedEntry(): E {
		if (this.selectedEntry == null && (!this.config.allowFreeText || !this.$editor.value)) {
			return null;
		} else if (this.selectedEntry == null && this.config.allowFreeText) {
			return this.config.freeTextEntryFactory(this.$editor.value);
		} else {
			return unProxyEntry(this.selectedEntry);
		}
	}

	public getDropDownComponent(): DropDownComponent<E> {
		return this.dropDownComponent;
	}

	public focus() {
		this.showEditor();
		this.$editor.select();
	};

	public getEditor(): Element {
		return this.$editor;
	}

	public getDropDown() {
		return this.$dropDown;
	};

	public setShowClearButton(showClearButton: boolean) {
		this.config.showClearButton = showClearButton;
		this.$clearButton.classList.toggle('hidden', !showClearButton);
	}

	public setShowTrigger(showTrigger: boolean) {
		this.config.showTrigger = showTrigger;
		this.$trigger.classList.toggle('hidden', !showTrigger);
	}


	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
		this.$comboBox.remove();
		this.$dropDown.remove();
	};

	getMainDomElement(): HTMLElement {
		return this.$comboBox;
	}
}
