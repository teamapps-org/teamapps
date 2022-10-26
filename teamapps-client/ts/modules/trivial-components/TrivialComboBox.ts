/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {
	EditingMode,
	keyCodes,
	objectEquals,
	RenderingFunction,
	setTimeoutOrDoImmediately,
	TrivialComponent,
	unProxyEntry
} from "./TrivialCore";
import {Instance as Popper} from '@popperjs/core';
import {getAutoCompleteOffValue, parseHtml} from "../Common";
import {DropDownComponent, SelectionDirection} from "./dropdown/DropDownComponent";
import {createComboBoxPopper} from "./ComboBoxPopper";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiToolButton} from "../micro-components/UiToolButton";

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
	 * Creates an entry (object) from a string entered by the user.
	 *
	 * @param freeText the text entered by the user
	 * @default `{ displayValue: freeText, _isFreeTextEntry: true }`
	 */
	textToEntryFunction?: (freeText: string) => E | any,

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
	 * When typing, preselect the first returned query result.
	 */
	preselectFirstQueryResult?: boolean,

	/**
	 * Text displayed when nothing has been selected/typed.
	 */
	placeholderText?: string,

	dropDownMinWidth?: number,
	dropDownMaxHeight?: number
}

export class TrivialComboBox<E> implements TrivialComponent {

	public readonly onSelectedEntryChanged = new TeamAppsEvent<E>();
	public readonly onFocus = new TeamAppsEvent<void>();
	public readonly onBlur = new TeamAppsEvent<void>();
	public readonly onBeforeQuery = new TeamAppsEvent<string>();
	public readonly onBeforeDropdownOpens = new TeamAppsEvent<string>();

	private config: TrivialComboBoxConfig<E>;

	private $comboBox: HTMLElement;
	private $dropDown: HTMLElement;
	private $editor: HTMLInputElement;
	private $selectedEntryWrapper: HTMLElement;
	private $trigger: HTMLElement;
	private $clearButton: HTMLElement;
	private $toolButtonsWrapper: HTMLElement;

	private popper: Popper;
	private dropDownComponent: DropDownComponent<E>;

	private selectedEntry: E = null;

	private blurCausedByClickInsideComponent = false;
	private focused: boolean;

	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;

	private editingMode: EditingMode;
	private dropDownOpen = false;
	private isEditorVisible = false;


	constructor(options: TrivialComboBoxConfig<E>, dropDownComponent?: DropDownComponent<E>) {
		this.config = {
			autoComplete: true,
			autoCompleteDelay: 0,
			textToEntryFunction: (freeText: string) => null,
			showClearButton: false,
			showTrigger: true,
			editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
			showDropDownOnResultsOnly: false,
			preselectFirstQueryResult: true,
			placeholderText: "",

			...options
		};

		this.$comboBox = parseHtml(`<div class="tr-combobox tr-input-wrapper editor-hidden">
			<div class="tr-combobox-main-area">
				<input type="text" class="tr-combobox-editor tr-editor" autocomplete="${getAutoCompleteOffValue()}"></input>
				<div class="tr-combobox-selected-entry-wrapper"></div>			
			</div>
			<div class="tr-tool-buttons">
			  <div class="tr-tool-button tr-remove-button ${this.config.showClearButton ? '' : 'hidden'}"></div>
			</div>
            <div class="tr-trigger ${this.config.showTrigger ? '' : 'hidden'}"><span class="tr-trigger-icon"></span></div>
        </div>`);
		this.$selectedEntryWrapper = this.$comboBox.querySelector(':scope .tr-combobox-selected-entry-wrapper');
		this.$toolButtonsWrapper = this.$comboBox.querySelector(':scope .tr-tool-buttons');
		this.$clearButton = this.$comboBox.querySelector(':scope .tr-remove-button');
		this.$clearButton.addEventListener("mousedown", (e) => {
			this.$editor.value = "";
			this.setValue(null, true, e);
		});
		this.$trigger = this.$comboBox.querySelector(':scope .tr-trigger');
		this.$dropDown = parseHtml('<div class="tr-dropdown"></div>');
		this.$dropDown.style.minWidth = this.config.dropDownMinWidth != null ? (this.config.dropDownMinWidth + "px") : null;
		this.$dropDown.style.maxHeight = this.config.dropDownMaxHeight != null ? (this.config.dropDownMaxHeight + "px") : null
		this.$dropDown.addEventListener("scroll", e => {
			e.stopPropagation();
			e.preventDefault();
		});
		this.setEditingMode(this.config.editingMode);
		this.$editor = this.$comboBox.querySelector(':scope .tr-editor');
		this.$editor.addEventListener("focus", () => {
			this.setFocused(true);
			if (!this.blurCausedByClickInsideComponent) {
				this.showEditor();
			}
		})
		this.$editor.addEventListener("blur", (e: FocusEvent) => {
			if (this.blurCausedByClickInsideComponent) {
				this.$editor.focus();
				// Don't just unset the blurCausedByClickInsideComponent here! See the comment with hashtag #regainFocus.
			} else {
				this.setFocused(false);
				if (this.isEditorVisible) {
					let freeTextEntry = this.getFreeTextEntry();
					if (freeTextEntry != null) {
						this.setValue(freeTextEntry, true, e);
					}
				}
				this.hideEditor();
				this.closeDropDown();
			}
		})
		this.$editor.addEventListener("keydown", (e: KeyboardEvent) => {
			if (keyCodes.isModifierKey(e)) {
				return;
			} else if (e.key === "Tab" || e.key === "Enter") {
				if (this.isEditorVisible) {
					e.key == "Enter" && e.preventDefault(); // do not submit form
					let highlightedEntry = this.dropDownComponent.getValue();
					if (this.dropDownOpen && highlightedEntry) {
						this.setValue(highlightedEntry, true, e);
					} else {
						let freeTextEntry = this.getFreeTextEntry();
						if (freeTextEntry != null) {
							this.setValue(freeTextEntry, true, e);
						}
					}
					this.closeDropDown();
					this.hideEditor();
				}
				return;
			} else if (e.key === "ArrowLeft" || e.key === "ArrowRight") {
				if (this.dropDownOpen && this.dropDownComponent.handleKeyboardInput(e)) {
					this.setAndSelectEditorValue(this.dropDownComponent.getValue());
					e.preventDefault();
					return;
				} else {
					this.showEditor();
					return; // let the user navigate freely left and right...
				}
			} else if (e.key === "Backspace" || e.key === "Delete") {
				this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
				setTimeout(() => this.query(this.getEditorValueLeftOfSelection(), 0)); // asynchronously to make sure the editor has been updated
			} else if (e.key === "ArrowUp" || e.key === "ArrowDown") {
				if (!this.isEditorVisible) {
					this.$editor.select();
					this.showEditor();
				}
				const direction = e.key === "ArrowUp" ? -1 : 1;
				if (!this.dropDownOpen) {
					this.query(this.getEditorValueLeftOfSelection(), direction);
					this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
				} else {
					if (this.dropDownComponent.handleKeyboardInput(e)) {
						this.setAndSelectEditorValue(this.dropDownComponent.getValue());
					}
				}
				e.preventDefault(); // some browsers move the caret to the beginning on up key
			} else if (e.key === "Escape") {
				if (!(!this.isEntrySelected() && this.$editor.value.length > 0 && this.dropDownOpen)) {
					this.hideEditor();
					this.$editor.value = "";
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
				setTimeout(() => this.query(this.getEditorValueLeftOfSelection(), this.config.preselectFirstQueryResult && this.$editor.value ? 1 : 0))
			}
		});

		[this.$comboBox, this.$dropDown].forEach(element => {
			element.addEventListener("mousedown", () => {
				this.blurCausedByClickInsideComponent = true;

				// #regainFocus
				// Why don't we just do a "setTimeout(() => this.blurCausedByClickInsideComponent = false);" and check for
				// the flag in the blur event handler instead of this overly complex handling of "mouseup" and "mousedown" events?
				// That's because in Firefox, doing $editor.focus() has no effect as long as the mouse is pressed. The
				// document.activeElement will be document.body until the mouse is released.
				// So when the user presses somewhere inside the tag combobox (except the $editor), the focus will be lost (blur event)
				// and re-focusing will have no effect. We HAVE TO wait for the mouseup or mouseout event in order to re-focus.
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

		this.setDropDownComponent(dropDownComponent);

		this.$editor.addEventListener("mousedown", () => {
			if (this.editingMode === "editable") {
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.query(this.getEditorValueLeftOfSelection());
			}
		});
		this.$trigger.addEventListener("mousedown", () => {
			if (this.dropDownOpen) {
				this.closeDropDown();
				this.showEditor();
			} else if (this.editingMode === "editable") {
				this.showEditor();
				this.$editor.select();
				this.dropDownComponent.setValue(this.getValue())
				this.query("", 0);
				this.openDropDown();
			}
		});
		this.$selectedEntryWrapper.addEventListener("click", () => {
			if (this.editingMode === "editable") {
				this.showEditor();
				this.$editor.select()
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.dropDownComponent.setValue(this.getValue())
				this.query("", 0);
			}
		});

		this.popper = createComboBoxPopper(this.$comboBox, this.$dropDown, () => this.closeDropDown());
	}

	private setFocused(focused: boolean) {
		if (focused != this.focused) {
			if (focused) {
				this.onFocus.fire();
			} else {
				this.onBlur.fire();
			}
			this.$comboBox.classList.toggle('focus', focused);
			this.focused = focused;
		}
	}

	private getFreeTextEntry() {
		let editorValue = this.getEditorValueLeftOfSelection();
		return editorValue ? this.config.textToEntryFunction(editorValue) : null;
	}

	private setAndSelectEditorValue(value: E) {
		if (value != null) {
			this.$editor.value = this.config.entryToEditorTextFunction(value);
			this.$editor.select();
		} else {
			this.$editor.value = "";
		}
	}

	private handleDropDownValueChange(eventData: { value: E; finalSelection: boolean }) {
		if (eventData.finalSelection) {
			this.setValue(eventData.value, true);
			this.closeDropDown();
			this.hideEditor();
		}
	}

	private async query(nonSelectedEditorValue: string, highlightDirection: SelectionDirection = 0) {
		this.onBeforeQuery.fire(nonSelectedEditorValue);
		let gotResultsForQuery = await this.dropDownComponent.handleQuery(nonSelectedEditorValue, highlightDirection, this.getValue());

		if (highlightDirection !== 0) {
			this.autoCompleteIfPossible(this.config.autoCompleteDelay);
		}

		if (this.config.showDropDownOnResultsOnly && gotResultsForQuery && document.activeElement == this.$editor) {
			this.openDropDown();
		}
	}

	private fireChangeEvents(entry: E, originalEvent?: unknown) {
		this.onSelectedEntryChanged.fire(unProxyEntry(entry));
	}

	public setValue(entry: E, fireEventIfChanged?: boolean, originalEvent?: unknown) {
		let changing = !objectEquals(entry, this.selectedEntry);
		this.selectedEntry = entry;
		this.$selectedEntryWrapper.innerHTML = '';
		let $selectedEntry = parseHtml(this.config.selectedEntryRenderingFunction(entry));
		if ($selectedEntry != null) {
			$selectedEntry.classList.add("tr-combobox-entry");
			this.$selectedEntryWrapper.append($selectedEntry);
		} else {
			this.$selectedEntryWrapper.append(parseHtml(`<div class="placeholder-text">${this.config.placeholderText ?? ""}</div>`))
		}
		if (entry != null) {
			this.$editor.value = this.config.entryToEditorTextFunction(entry)
		} else {
			this.$editor.value = '';
		}

		if (changing && fireEventIfChanged) {
			this.fireChangeEvents(entry, originalEvent);
		}
		this.$toolButtonsWrapper.classList.toggle("hidden", entry == null);
		if (this.isEditorVisible) {
			this.showEditor(); // reposition editor
		}
		if (this.dropDownOpen) {
			this.popper.update();
		}
	}

	private isEntrySelected() {
		return this.selectedEntry != null;
	}

	private showEditor() {
		this.$comboBox.classList.remove("editor-hidden");
		this.isEditorVisible = true;
	}

	private hideEditor() {
		this.$comboBox.classList.add("editor-hidden");
		this.isEditorVisible = false;
	}

	private parentElement: Element;

	public openDropDown() {
		if (this.isDropDownNeeded()) {
			if (this.getMainDomElement().parentElement !== this.parentElement) {
				this.popper.destroy();
				this.popper = createComboBoxPopper(this.$comboBox, this.$dropDown, () => this.closeDropDown());
				this.parentElement = this.getMainDomElement().parentElement;
			}
			if (!this.dropDownOpen) {
				this.onBeforeDropdownOpens.fire(this.getEditorValueLeftOfSelection());
				this.$comboBox.classList.add("open");
				this.popper.update();
				this.dropDownOpen = true;
			}
		}
	}

	public closeDropDown() {
		this.$comboBox.classList.remove("open");
		this.dropDownOpen = false;
	}

	private getEditorValueLeftOfSelection() {
		return this.$editor.value.substring(0, Math.min(this.$editor.selectionStart, this.$editor.selectionEnd));
	}

	private autoCompleteIfPossible(delay?: number) {
		if (this.config.autoComplete) {
			clearTimeout(this.autoCompleteTimeoutId);
			const dropDownValue = this.dropDownComponent.getValue();
			if (dropDownValue && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
				this.autoCompleteTimeoutId = setTimeoutOrDoImmediately(() => {
					const currentEditorValue = this.getEditorValueLeftOfSelection();
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

	public getValue(): E {
		return unProxyEntry(this.selectedEntry);
	}

	public getDropDownComponent(): DropDownComponent<E> {
		return this.dropDownComponent;
	}

	public setDropDownComponent(dropDownComponent: DropDownComponent<E>): void {
		if (this.dropDownComponent != null) {
			this.dropDownComponent.onValueChanged.removeListener(this.handleDropDownValueChange);
			this.$dropDown.innerHTML = '';
		}
		this.dropDownComponent = dropDownComponent;
		this.$dropDown.append(dropDownComponent.getMainDomElement());
		dropDownComponent.onValueChanged.addListener(this.handleDropDownValueChange.bind(this));
		dropDownComponent.setValue(this.getValue());
	}

	public focus() {
		this.showEditor();
		this.$editor.select();
	};

	public getEditor(): Element {
		return this.$editor;
	}

	public setShowClearButton(showClearButton: boolean) {
		this.config.showClearButton = showClearButton;
		this.$clearButton.classList.toggle('hidden', !this.config.showClearButton);
	}

	public setShowTrigger(showTrigger: boolean) {
		this.config.showTrigger = showTrigger;
		this.$trigger.classList.toggle('hidden', !showTrigger);
	}

	public setToolButtons(buttons: UiToolButton[]) {
		Array.from(this.$toolButtonsWrapper.children)
			.filter(e => e != this.$clearButton)
			.forEach(e => e.remove());
		buttons.forEach(b => this.$toolButtonsWrapper.insertBefore(b.getMainElement(), this.$clearButton));
	}

	public isDropDownOpen(): boolean {
		return this.dropDownOpen;
	}

	public destroy() {
		this.$comboBox.remove();
		this.$dropDown.remove();
	};

	getMainDomElement(): HTMLElement {
		return this.$comboBox;
	}

	setPlaceholderText(placeholderText: string) {
		this.config.placeholderText = placeholderText;
		let $placeholderText: HTMLElement = this.$selectedEntryWrapper.querySelector(":scope .placeholder-text");
		if ($placeholderText != null) {
			$placeholderText.innerText = placeholderText ?? "";
		}
	}
}
