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
	DEFAULT_TEMPLATES,
	type EditingMode,
	escapeSpecialRegexCharacter,
	generateUUID,
	isModifierKey,
	type RenderingFunction,
	setTimeoutOrDoImmediately,
	type TrivialComponent,
	unProxyEntry
} from "./TrivialCore";
import {type Instance as Popper} from '@popperjs/core';
import {type DropDownComponent, type SelectionDirection} from "./dropdown/DropDownComponent";
import {type Disposable, positionDropdownWithAutoUpdate} from "./ComboBoxPopper";
import {
	elementIndex,
	insertAfter,
	insertAtIndex,
	insertBefore,
	parseHtml,
	ProjectorEvent
} from "projector-client-object-api";
import {selectElementContents} from "../util";

export interface TrivialTagComboBoxConfig<E> {
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
	 * List of characters that, when entered by the user, trigger the creation of a tag/entry.
	 *
	 * @default `[",", ";"]`
	 */
	freeTextSeparators?: string[],

	/**
	 * Creates an entry (object) from a string entered by the user.
	 *
	 * @param freeText the text entered by the user
	 * @default `{ displayValue: freeText, _isFreeTextEntry: true }`
	 */
	freeTextEntryFactory?: (freeText: string) => E | any,

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
	 * Function deciding whether an entry entered by the user is complete or not (partial).
	 * A partial entry is an entry that needs more input from the user's side.
	 *
	 * @param entry
	 * @return `true` if the entry is considered complete, `false` if not
	 * @default always returns `true`
	 */
	tagCompleteDecider?: (entry: E) => boolean,

	/**
	 * Used to merge the current partial tag with the newly selected by the user.
	 * (composite tags feature)
	 *
	 * @param partialEntry the current partial entry
	 * @param newEntryPart the entry selected/entered by the user
	 * @return a new entry that will replace the current partial entry. This may in turn be a partial or complete entry.
	 * @default always returns the `newEntryPart`
	 */
	entryMerger?: (partialEntry: E, newEntryPart: E) => E,

	/**
	 * Whether or not a partial tag should be removed when the component
	 * looses the focus.
	 *
	 * @default `true`
	 */
	removePartialTagOnBlur?: boolean,

	/**
	 * Decides whether the user's input/selection is accepted or not.
	 * This can serve many purposes, including
	 *  * implementing duplicate prevention
	 *  * limiting the number of selectable tags
	 *  * allowing or disallowing free text entries (this was previously possible using the now removed `freeTextEnabled` option)
	 *  * allowing only free text entries of a certain form
	 *  * ...
	 *
	 * @param entry the entry to be accepted or not
	 * @return `true` if the entry is accepted, `false` if not
	 * @default accepting all non-free-text entries
	 */
	selectionAcceptor?: (entry: E) => boolean,

	/**
	 * HTML string defining the spinner to be displayed while entries are being retrieved.
	 */
	spinnerTemplate?: string

	/**
	 * This will cause tags to not directly be deleted when pressing the backspace or delete key, but first marked for deletion.
	 */
	twoStepDeletion?: boolean;

	/**
	 * When typing, preselect the first returned query result.
	 */
	preselectFirstQueryResult?: boolean,

	/**
	 * Text displayed when nothing has been selected/typed.
	 */
	placeholderText?: string,

	dropDownMinWidth?: number,
	dropDownMaxHeight?: number,

	/**
	 * The clear button is a the small 'x' at the right of the entry display that can be clicked to clear the selection.
	 */
	clearButtonEnabled?: boolean
}

export class TrivialTagComboBox<E> implements TrivialComponent {

	public readonly onValueChanged = new ProjectorEvent<E[]>();
	public readonly onFocus = new ProjectorEvent<void>();
	public readonly onBlur = new ProjectorEvent<void>();

	private config: TrivialTagComboBoxConfig<E>;

	private $tagComboBox: HTMLElement;
	private $dropDown: HTMLElement;
	private $placeholderText: HTMLElement;
	private $editor: HTMLInputElement;
	private $tagArea: HTMLElement;
	private $trigger: HTMLElement;
	private $clearButton: HTMLElement;

	private popper: Popper;
	private dropDownComponent: DropDownComponent<E>;

	private selectedEntries: Tag<E>[] = [];
	private tagToBeRemoved: Tag<E> | null;
	private currentPartialTag: Tag<E> | null;

	private blurCausedByClickInsideComponent = false;
	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;

	private editingMode: EditingMode;
	private _isDropDownOpen = false;
	private focused: boolean;


	constructor(options: TrivialTagComboBoxConfig<E>, dropDownComponent?: DropDownComponent<E>) {
		this.config = {
			spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
			autoComplete: true,
			autoCompleteDelay: 0,
			freeTextEntryFactory: (freeText: string) => {
				return {
					displayValue: freeText,
					id: generateUUID(),
					_isFreeTextEntry: true
				};
			},
			freeTextSeparators: [',', ';'], // TODO function here
			tagCompleteDecider: (mergedEntry: E) => {
				return true;
			},
			entryMerger: (partialEntry: E, newEntry: E) => {
				return newEntry;
			},
			removePartialTagOnBlur: true,
			selectionAcceptor: (e: E) => !(e as any)._isFreeTextEntry, // do not allow free text entries by default
			showTrigger: true,
			editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
			showDropDownOnResultsOnly: false,
			twoStepDeletion: false,
			placeholderText: "",

			...options
		};
		this.dropDownComponent = dropDownComponent;

		this.$tagComboBox = parseHtml(`<div class="tr-tagcombobox tr-input-wrapper">
			<div class="tr-tagcombobox-main-area">
				<div class="placeholder-text"></div>
				<div class="tr-tagcombobox-tagarea"></div>
			</div>
			 <div class="tr-remove-button ${this.config.clearButtonEnabled ? '' : 'hidden'}"></div>
            <div class="tr-trigger ${this.config.showTrigger ? '' : 'hidden'}"><span class="tr-trigger-icon"></span></div>
        </div>`);
		this.$tagArea = this.$tagComboBox.querySelector(':scope .tr-tagcombobox-tagarea');
		this.$placeholderText = this.$tagComboBox.querySelector(':scope .placeholder-text');
		this.setPlaceholderText(this.config.placeholderText);
		this.$clearButton = this.$tagComboBox.querySelector(':scope .tr-remove-button');
		this.$clearButton.addEventListener("mousedown", (e) => {
			this.$editor.value = "";
			this.setSelectedEntries([], true);
		});
		this.$trigger = this.$tagComboBox.querySelector(':scope .tr-trigger');
		this.$trigger.addEventListener("mousedown", () => {
			this.focus();
			if (this._isDropDownOpen) {
				this.closeDropDown();
			} else if (this.editingMode === "editable") {
				selectElementContents(this.$editor);
				this.openDropDown();
				this.query();
			}
		});
		this.$dropDown = parseHtml('<div class="tr-dropdown"></div>');
		this.$dropDown.style.minWidth = this.config.dropDownMinWidth != null ? (this.config.dropDownMinWidth + "px") : null;
		this.$dropDown.style.maxHeight = this.config.dropDownMaxHeight != null ? (this.config.dropDownMaxHeight + "px") : null
		this.$dropDown.addEventListener("scroll", e => {
			e.stopPropagation();
			e.preventDefault();
		});
		this.setEditingMode(this.config.editingMode);
		this.$editor = parseHtml(`<span contenteditable="true" class="tagbox-editor" autocomplete="no"></span>`);
		this.$tagArea.append(this.$editor);
		this.$editor.classList.add("tr-tagcombobox-editor", "tr-editor");
		this.$editor.addEventListener("focus", () => {
			this.setFocused(true);
			this.$tagComboBox.offsetHeight;
			this.$editor.scrollIntoView({
				behavior: "smooth",
				block: "nearest",
				inline: "nearest"
			});
		});
		this.$editor.addEventListener("blur", (e: FocusEvent) => {
			if (this.blurCausedByClickInsideComponent) {
				this.$editor.focus();
				// Don't just unset the blurCausedByClickInsideComponent here! See the comment with hashtag #regainFocus.
			} else {
				this.setFocused(false);
				this.setTagToBeRemoved(null);
				this.closeDropDown();
				if (this.$editor.textContent.trim().length > 0) {
					this.addTag(this.config.freeTextEntryFactory(this.$editor.textContent), true, e);
				}
				if (this.config.removePartialTagOnBlur && this.currentPartialTag != null) {
					this.cancelPartialTag();
				}
				this.$editor.textContent = "";
			}
		});
		this.$editor.addEventListener("keydown", (e: KeyboardEvent) => {
			if (isModifierKey(e)) {
				return;
			} else if (e.key == "Tab" || e.key == "Enter") {
				this.setTagToBeRemoved(null);
				const highlightedEntry = this.dropDownComponent.getValue();
				if (this._isDropDownOpen && highlightedEntry != null) {
					this.addTag(highlightedEntry, true, e);
					e.preventDefault(); // do not tab away from the tag box nor insert a newline character
				} else if (this.$editor.textContent.trim().length > 0) {
					this.addTag(this.config.freeTextEntryFactory(this.$editor.textContent), true, e);
					e.preventDefault(); // do not tab away from the tag box nor insert a newline character
				} else if (this.currentPartialTag) {
					if (e.shiftKey) { // we do not want the editor to get the focus right back, so we need to position the $editor intelligently...
						this.doPreservingFocus(() => insertAfter(this.$editor, this.currentPartialTag.$tagWrapper));
					} else {
						this.doPreservingFocus(() => insertBefore(this.$editor, this.currentPartialTag.$tagWrapper));
					}
					this.currentPartialTag.$tagWrapper.remove();
					this.currentPartialTag = null;
				}
				this.closeDropDown();
				if (e.key == "Enter") {
					e.preventDefault(); // under any circumstances, prevent the new line to be added to the editor!
				}
			} else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
				this.setTagToBeRemoved(null);
				if (this._isDropDownOpen && this.dropDownComponent.handleKeyboardInput(e)) {
					e.preventDefault();
					return;
				} else if (e.key == "ArrowLeft" && this.$editor.textContent.length === 0 && window.getSelection().anchorOffset === 0) {
					if (this.$editor.previousElementSibling != null) {
						this.doPreservingFocus(() => insertBefore(this.$editor, this.$editor.previousElementSibling));
						this.focus();
					}
				} else if (e.key == "ArrowRight" && this.$editor.textContent.length === 0 && window.getSelection().anchorOffset === 0) {
					if (this.$editor.nextElementSibling != null) {
						this.doPreservingFocus(() => insertAfter(this.$editor, this.$editor.nextElementSibling));
						this.focus();
					}
				}
			} else if (e.key == "Backspace" || e.key == "Delete") {
				if (this.$editor.textContent == "") {
					if (this.currentPartialTag != null) {
						this.cancelPartialTag();
						this.focus();
					} else {
						const tagToBeRemoved = this.selectedEntries[elementIndex(this.$editor) + (e.key == "Backspace" ? -1 : 0)];
						if (tagToBeRemoved != null) {
							if (!this.config.twoStepDeletion || this.tagToBeRemoved === tagToBeRemoved) {
								this.removeTag(tagToBeRemoved, true, e);
								this.closeDropDown();
								this.setTagToBeRemoved(null);
							} else {
								this.setTagToBeRemoved(tagToBeRemoved);
							}
						}
					}
				} else {
					this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
					setTimeout(() => this.query(0)); // asynchronously to make sure the editor has been updated
				}
			} else if (e.key == "ArrowUp" || e.key == "ArrowDown") {
				this.setTagToBeRemoved(null);
				const direction = e.key == "ArrowUp" ? -1 : 1;
				if (!this._isDropDownOpen) {
					this.query(direction);
					this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
				} else {
					if (this.dropDownComponent.handleKeyboardInput(e)) {
						if (this.dropDownComponent.getValue() != null) {
							this.$editor.textContent = this.config.entryToEditorTextFunction(this.dropDownComponent.getValue())
							selectElementContents(this.$editor);
						} else {
							this.$editor.textContent = "";
						}
					}
				}
				e.preventDefault(); // some browsers move the caret to the beginning on up key
			} else if (e.key == "Escape") {
				this.setTagToBeRemoved(null);
				if (this.$editor.textContent.length > 0) {
					this.$editor.textContent = "";
				} else if (this.currentPartialTag != null) {
					this.cancelPartialTag();
					this.focus();
				}
				this.closeDropDown();
			}
		});
		this.$editor.addEventListener("input", e => {
			this.setTagToBeRemoved(null);
			if (!this.config.showDropDownOnResultsOnly) {
				this.openDropDown();
			}

			function splitStringBySeparatorChars(s: string, separatorChars: string[]) {
				return s.split(new RegExp("[" + escapeSpecialRegexCharacter(separatorChars.join()) + "]"));
			}

			if (this.$editor.querySelector(':scope *') != null) {
				this.$editor.innerHTML = this.$editor.textContent; // removes possible <div> or <br> or whatever the browser likes to put inside...
			}
			const editorValueBeforeCursor = this.getNonSelectedEditorValue();
			if (editorValueBeforeCursor.length > 0) {
				const tagValuesEnteredByUser = splitStringBySeparatorChars(editorValueBeforeCursor, this.config.freeTextSeparators);

				for (let i = 0; i < tagValuesEnteredByUser.length - 1; i++) {
					const value = tagValuesEnteredByUser[i].trim();
					if (value.length > 0) {
						this.addTag(this.config.freeTextEntryFactory(value), true, e);
					}
					this.$editor.textContent = tagValuesEnteredByUser[tagValuesEnteredByUser.length - 1];
					selectElementContents(this.$editor, this.$editor.textContent.length, this.$editor.textContent.length);
					this.closeDropDown();
				}
			}

			this.query(this.config.preselectFirstQueryResult && this.$editor.textContent ? 1 : 0);
		})
		this.$editor.addEventListener("mousedown", () => {
			if (this.editingMode === "editable") {
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.query();
			}
		});

		[this.$tagComboBox, this.$dropDown].forEach(element => {
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

		this.$dropDown.append(this.dropDownComponent.getMainDomElement());
		this.dropDownComponent.onValueChanged.addListener((eventData) => {
			if (eventData.finalSelection) {
				this.setTagToBeRemoved(null);
				this.addTag(eventData.value, true);
				this.closeDropDown();
			}
		});

		this.$tagArea.addEventListener("mousedown", (e) => {
			if (this.currentPartialTag == null) {
				let $nearestTag = this.findNearestTag(e);
				if ($nearestTag) {
					const tagBoundingRect = $nearestTag.getBoundingClientRect();
					const isRightSide = e.clientX > (tagBoundingRect.left + tagBoundingRect.right) / 2;
					if (isRightSide) {
						this.doPreservingFocus(() => insertAfter(this.$editor, $nearestTag));
					} else {
						this.doPreservingFocus(() => insertBefore(this.$editor, $nearestTag));
					}
				}
			}
			this.$editor.focus();
		});

		this.$tagArea.addEventListener("click", (e) => {
			if (this.editingMode === "editable") {
				if (!this.config.showDropDownOnResultsOnly) {
					this.openDropDown();
				}
				this.query();
			}
		});

		this.$editor.addEventListener("keyup", () => this.updatePlaceholderTextVisibility());
	}

	private setFocused(focused: boolean) {
		if (focused != this.focused) {
			if (focused) {
				this.onFocus.fire();
			} else {
				this.onBlur.fire();
			}
			this.$tagComboBox.classList.toggle('focus', focused);
			this.focused = focused;
		}
	}

	private setTagToBeRemoved(tagToBeRemoved: Tag<E>) {
		if (this.tagToBeRemoved != null) {
			this.tagToBeRemoved.$tagWrapper.classList.remove("marked-for-removal");
		}
		this.tagToBeRemoved = tagToBeRemoved;
		if (tagToBeRemoved != null) {
			tagToBeRemoved.$tagWrapper.classList.add("marked-for-removal");
		}
	}

	private cancelPartialTag() {
		this.doPreservingFocus(() => insertBefore(this.$editor, this.currentPartialTag.$tagWrapper));
		this.currentPartialTag.$tagWrapper.remove();
		this.currentPartialTag = null;
	}

	private findNearestTag(mouseEvent: MouseEvent) {
		let $nearestTag: HTMLElement = null;
		let smallestDistanceX = 1000000;
		for (let i = 0; i < this.selectedEntries.length; i++) {
			const selectedEntry = this.selectedEntries[i];
			const $tag = selectedEntry.$tagWrapper;
			const tagBoundingRect = $tag.getBoundingClientRect();
			const sameRow = mouseEvent.clientY >= tagBoundingRect.top && mouseEvent.clientY < tagBoundingRect.bottom;
			const sameCol = mouseEvent.clientX >= tagBoundingRect.left && mouseEvent.clientX < tagBoundingRect.right;
			const distanceX = sameCol ? 0 : Math.min(Math.abs(mouseEvent.clientX - tagBoundingRect.left), Math.abs(mouseEvent.clientX - tagBoundingRect.right));
			if (sameRow && distanceX < smallestDistanceX) {
				$nearestTag = $tag;
				smallestDistanceX = distanceX;
				if (distanceX === 0) {
					break;
				}
			}
		}
		return $nearestTag;
	}

	private removeTag(tagToBeRemoved: Tag<E>, fireChangeEvent: boolean = false, originalEvent?: unknown) {
		const index = this.selectedEntries.indexOf(tagToBeRemoved);
		if (index > -1) {
			this.selectedEntries.splice(index, 1);
		}
		tagToBeRemoved.$tagWrapper.remove();
		if (fireChangeEvent) {
			this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
		}

		this.updatePlaceholderTextVisibility();
	}

	private async query(highlightDirection: SelectionDirection = 0) {
		let gotResultsForQuery = await this.dropDownComponent.handleQuery(this.getNonSelectedEditorValue(), highlightDirection, this.getSelectedEntries()[0] ?? null);
		this.blurCausedByClickInsideComponent = false; // we won't get any mouseout or mouseup events for entries if they get removed. so do this here proactively

		this.autoCompleteIfPossible(this.config.autoCompleteDelay);

		if (this.config.showDropDownOnResultsOnly && gotResultsForQuery && document.activeElement == this.$editor) {
			this.openDropDown();
		}
	}

	private fireChangeEvents(entries: E[], originalEvent: unknown) {
		this.onValueChanged.fire(entries.map(unProxyEntry));
	}

	private addTag(entry: E, fireEvent = false, originalEvent?: unknown, forceAcceptance?: boolean) {
		if (entry == null) {
			return; // do nothing
		}
		if (!forceAcceptance && !this.config.selectionAcceptor(entry)) {
			return;
		}

		let wasPartial = !!this.currentPartialTag;
		const editorIndex = wasPartial ? elementIndex(this.currentPartialTag.$tagWrapper) : elementIndex(this.$editor);
		if (wasPartial) {
			this.doPreservingFocus(() => this.$tagArea.append(this.$editor)); // make sure the event handlers don't get detached when removing the partial tag
			this.currentPartialTag.$tagWrapper.remove();
			entry = this.config.entryMerger(this.currentPartialTag.entry, entry);
		}

		const $entry = parseHtml(this.config.selectedEntryRenderingFunction(entry));
		const $tagWrapper = parseHtml('<div class="tr-tagcombobox-tag"></div>')
		$tagWrapper.append($entry);

		const tag = new Tag(entry, $tagWrapper, $entry);

		if (this.config.tagCompleteDecider(entry)) {
			this.selectedEntries.splice(editorIndex, 0, tag);
			this.currentPartialTag = null;
		} else {
			this.currentPartialTag = tag;
		}

		this.doPreservingFocus(() => insertAtIndex(this.$tagArea, $tagWrapper, editorIndex));

		let removeButton = $entry.querySelector('.tr-remove-button');
		if (removeButton != null) {
			removeButton.addEventListener("click", (e) => {
				this.removeTag(tag, true, e);
				return false;
			});
		}

		if (this.config.tagCompleteDecider(entry)) {
			this.doPreservingFocus(() => insertAtIndex(this.$tagArea, this.$editor, editorIndex + 1));
		} else {
			this.doPreservingFocus(() => $entry.querySelector('.tr-editor').append(this.$editor));
		}

		this.$editor.textContent = "";
		if (document.activeElement == this.$editor) {
			selectElementContents(this.$editor, 0, 0); // make sure Chrome displays a cursor after changing the content (bug...)
		}

		if (this.config.tagCompleteDecider(entry) && fireEvent) {
			this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
		}

		this.updatePlaceholderTextVisibility();
	}

	private repositionDropDown() {
		this.popper.update();
	}

	private parentElement: Element;
	private dropdownAutoUpdateDisposable: Disposable | null;

	public openDropDown() {
		if (this.isDropDownNeeded()) {
			if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
				this.dropdownAutoUpdateDisposable?.();
				this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = positionDropdownWithAutoUpdate(this.$tagComboBox, this.$dropDown, {
					referenceOutOfViewPortHandler: () => this.closeDropDown()
				});
				this.parentElement = this.getMainDomElement().parentElement;
			}
			this.$tagComboBox.classList.add("open");
			this.repositionDropDown();
			this._isDropDownOpen = true;
		}
	}

	public closeDropDown() {
		this.$tagComboBox.classList.remove("open");
		this._isDropDownOpen = false;
	}

	private getNonSelectedEditorValue() {
		const editorText = this.$editor.textContent.replace(String.fromCharCode(160), " ");
		const selection = window.getSelection();
		if (selection.anchorOffset != selection.focusOffset) {
			return editorText.substring(0, Math.min((window.getSelection() as any).baseOffset, window.getSelection().focusOffset));
		} else {
			return editorText;
		}
	}

	private autoCompleteIfPossible(delay?: number) {
		if (this.config.autoComplete) {
			clearTimeout(this.autoCompleteTimeoutId);
			const dropDownValue = this.dropDownComponent.getValue();
			if (dropDownValue && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
				this.autoCompleteTimeoutId = setTimeoutOrDoImmediately(() => {
					const currentEditorValue = this.getNonSelectedEditorValue();
					const entryAsString = this.config.entryToEditorTextFunction(dropDownValue);
					if (entryAsString.toLowerCase().indexOf(("" + currentEditorValue).toLowerCase()) === 0) {
						this.$editor.textContent = currentEditorValue + entryAsString.substr(currentEditorValue.length);
						if (document.activeElement == this.$editor) {
							selectElementContents(this.$editor, currentEditorValue.length, entryAsString.length);
						}
					}
					this.repositionDropDown(); // the auto-complete might cause a line-break, so the dropdown would cover the editor...
				}, delay || 0);
			}
			this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
		}
	}

	private isDropDownNeeded() {
		return this.editingMode == 'editable';
	}

	private doPreservingFocus(f: Function) {
		const hadFocus = document.activeElement == this.$editor;
		let oldValueOfBlurCausedByClickInsideComponent = this.blurCausedByClickInsideComponent;
		this.blurCausedByClickInsideComponent = true; // prevent triggering the onBlur event etc...
		try {
			return f.call(this);
		} finally {
			this.blurCausedByClickInsideComponent = oldValueOfBlurCausedByClickInsideComponent;
			const hasFocus = document.activeElement == this.$editor;
			if (hadFocus && !hasFocus) {
				this.$editor.focus();
			}
		}
	}

	public setEditingMode(newEditingMode: EditingMode) {
		this.editingMode = newEditingMode;
		this.$tagComboBox.classList.remove("editable", "readonly", "disabled");
		this.$tagComboBox.classList.add(this.editingMode);
		if (this.isDropDownNeeded()) {
			this.$tagComboBox.append(this.$dropDown);
		}
	}

	public setSelectedEntries(entries: E[], forceAcceptance?: boolean) {
		this.selectedEntries
			.slice() // copy the array as it gets changed during the forEach loop
			.forEach((e) => this.removeTag(e, false));
		if (entries) {
			for (let i = 0; i < entries.length; i++) {
				this.addTag(entries[i], false, null, forceAcceptance);
			}
		}
		if (this.$clearButton) {
			this.$clearButton.classList.toggle("hidden", !this.config.clearButtonEnabled || this.selectedEntries.length > 0);
		}
	}

	public getSelectedEntries(): E[] {
		return this.selectedEntries.map(tag => tag.entry);
	};

	public getDropDownComponent(): DropDownComponent<E> {
		return this.dropDownComponent;
	}

	public getCurrentPartialTag() {
		return this.currentPartialTag;
	}

	public focus() {
		this.$editor.focus();
		selectElementContents(this.$editor, 0, this.$editor.textContent.length); // we need to do this, else the cursor does not appear in Chrome when navigating using left and right keys...
	}

	public getEditor(): Element {
		return this.$editor;
	}

	public getDropDown() {
		return this.$dropDown;
	};

	public setClearButtonEnabled(clearButtonEnabled: boolean) {
		this.config.clearButtonEnabled = clearButtonEnabled;
		this.$clearButton.classList.toggle('hidden', !this.config.clearButtonEnabled || this.selectedEntries.length > 0);
	}

	public setShowTrigger(showTrigger: boolean) {
		this.config.showTrigger = showTrigger;
		this.$trigger.classList.toggle('hidden', !showTrigger);
	}

	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
		this.$tagComboBox.remove();
		this.$dropDown.remove();
	};

	getMainDomElement(): HTMLElement {
		return this.$tagComboBox;
	}

	private updatePlaceholderTextVisibility() {
		this.$placeholderText.classList.toggle("hidden", this.selectedEntries.length > 0 || !!this.$editor.textContent)
	}

	public setPlaceholderText(placeholderText: string) {
		this.$placeholderText.innerText = placeholderText ?? "";
	}

	public setShowDropDownOnResultsOnly(showDropDownAfterResultsArrive: boolean) {
		this.config.showDropDownOnResultsOnly = showDropDownAfterResultsArrive;
	}

	public setPreselectFirstQueryResult(preselectFirstQueryResult: boolean) {
		this.config.preselectFirstQueryResult = preselectFirstQueryResult;
	}

	public setAutoComplete(autoComplete: boolean) {
		this.config.autoComplete = autoComplete;
	}

	public setDropdownMinWidth(dropDownMinWidth: number | null) {
		this.$dropDown.style.minWidth = dropDownMinWidth != null ? (dropDownMinWidth + "px") : null;
	}

	public setDropdownMaxHeight(dropDownMaxHeight: number | null) {
		this.$dropDown.style.maxHeight = dropDownMaxHeight != null ? (dropDownMaxHeight + "px") : null
	}

	public setTwoStepDeletion(twoStepDeletionEnabled: boolean) {
		this.config.twoStepDeletion = twoStepDeletionEnabled;
	}
}

class Tag<E> {
	entry: E;
	$tagWrapper: HTMLElement;
	$entry: HTMLElement;

	constructor(entry: E, $tagWrapper: HTMLElement, $entry: HTMLElement) {
		this.entry = entry;
		this.$tagWrapper = $tagWrapper;
		this.$entry = $entry;
	}
}
