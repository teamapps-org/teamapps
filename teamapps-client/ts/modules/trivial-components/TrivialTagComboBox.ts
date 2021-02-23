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
	DEFAULT_TEMPLATES, defaultListQueryFunctionFactory, EditingMode, escapeSpecialRegexCharacter, HighlightDirection, QueryFunction, selectElementContents, TrivialComponent,
	wrapWithDefaultTagWrapper, keyCodes, RenderingFunction, DEFAULT_RENDERING_FUNCTIONS, generateUUID, defaultEntryMatchingFunctionFactory, defaultTreeQueryFunctionFactory, unProxyEntry
} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import {place} from "place-to";
import {createPopper, Instance as Popper} from '@popperjs/core';
import {TrivialTreeBox, TrivialTreeBoxConfig} from "./TrivialTreeBox";
import KeyDownEvent = JQuery.KeyDownEvent;
import MouseDownEvent = JQuery.MouseDownEvent;

export interface TrivialTagComboBoxConfig<E> extends TrivialTreeBoxConfig<E> {
    /**
     * Calculates the value to set on the original input.
     * 
     * @param entries the list of selected entries
     * @return the string to set as the value of the original input
     */
    inputValueFunction?: (entries: E[]) => string,

    /**
     * Rendering function used to display a _selected_ entry
     * (i.e. an entry inside the editor area of the component, not the dropdown).
     *
     * @param entry
     * @return HTML string
     * @default `wrapWithDefaultTagWrapper(entryRenderingFunction(entry))`
     */
    selectedEntryRenderingFunction?: RenderingFunction<E>,

    /**
     * Initially selected entries.
     *
     * @default `[]`
     */
    selectedEntries?: E[],

    /**
     * Performance setting. Defines the maximum number of entries until which text highlighting is performed.
     * Set to `0` to disable text highlighting.
     *
     * @default `100`
     */
    textHighlightingEntryLimit?: number,

    /**
     * Used to retrieve the entries ("suggestions") to be displayed in the dropdown box.
     *
     * @see QueryFunction
     * @default creates a client-side query function using the provided [[entries]]
     */
    queryFunction?: QueryFunction<E>,

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
     * Generates an autocompletion string for the current input of the user and currently highlighted entry in the dropdown.
     *
     * @param editorText the current text input from the user
     * @param entry the currently highlighted entry in the dropdown
     * @return The _full_ string (not only the completion part) to apply for auto-completion.
     * @default best effort implementation using entry properties
     */
    autoCompleteFunction?: (editorText: string, entry: E) => string,

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
     *  * allowing or disallowing free text entries (this was previously possible using the now removed `allowFreeText` option)
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
}

export class TrivialTagComboBox<E> implements TrivialComponent {

    private config: TrivialTagComboBoxConfig<E>;

    private $spinners = $();
    private $originalInput: JQuery;
    private $tagComboBox: JQuery;
    private $dropDown: JQuery;
    private popper: Popper;
    private $trigger: JQuery;
    private $editor: JQuery;
    private $dropDownTargetElement: JQuery;
    private $tagArea: JQuery;

    public readonly onSelectedEntryChanged = new TrivialEvent<E[]>(this);
    public readonly onFocus = new TrivialEvent<void>(this);
    public readonly onBlur = new TrivialEvent<void>(this);

    private treeBox: TrivialTreeBox<E>;
    private _isDropDownOpen = false;
    private entries: E[];
    private selectedEntries: E[] = [];
    private blurCausedByClickInsideComponent = false;
    private autoCompleteTimeoutId = -1;
    private doNoAutoCompleteBecauseBackspaceWasPressed = false;
    private listBoxDirty = true;
    private repositionDropDownScheduler: number = null;
    private editingMode: EditingMode;

    private usingDefaultQueryFunction: boolean;
    private currentPartialTag: E;

    constructor(originalInput: Element, options: TrivialTagComboBoxConfig<E>) {
	    let defaultIdFunction = (e:E) => {
		    if (e == null) {
			    return null;
		    } else if ((e as any)._isFreeTextEntry) {
			    return (e as any).displayValue;
		    } else {
			    return (e as any).id;
		    }
	    };
        this.config = $.extend({
            idFunction: defaultIdFunction,
            inputValueFunction: (entries:E[]) => entries.map(e => (e as any)._isFreeTextEntry ? (e as any).displayValue : defaultIdFunction(e)).join(','),
            selectedEntryRenderingFunction: (entry: E) => {
                return wrapWithDefaultTagWrapper(this.config.entryRenderingFunction(entry, 0));
            },
            spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
            textHighlightingEntryLimit: 100,
            entries: null,
            selectedEntries: [],
            maxSelectedEntries: null,
            queryFunction: null, // defined below...
            autoComplete: true,
            autoCompleteDelay: 0,
            autoCompleteFunction: (editorText: string, entry: E) => {
                if (editorText) {
                    for (let propertyName in entry) {
                        if (entry.hasOwnProperty(propertyName)) {
                            const propertyValue = entry[propertyName];
                            if (propertyValue && ("" + propertyValue).toLowerCase().indexOf(editorText.toLowerCase()) === 0) {
                                return "" + propertyValue;
                            }
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            },
            freeTextSeparators: [',', ';'], // TODO function here
            freeTextEntryFactory: (freeText: string) => {
                return {
                    displayValue: freeText,
	                id: generateUUID(),
                    _isFreeTextEntry: true
                };
            },
            tagCompleteDecider: (mergedEntry: E) => {
                return true;
            },
            entryMerger: (partialEntry: E, newEntry: E) => {
                return newEntry;
            },
            removePartialTagOnBlur: true,
            showTrigger: true,
            matchingOptions: {
                matchingMode: 'contains',
                ignoreCase: true,
                maxLevenshteinDistance: 2
            },
            editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
            showDropDownOnResultsOnly: false,
            selectionAcceptor: (e: E) => !(e as any)._isFreeTextEntry // do not allow free text entries by default
        }, options);

	    if (!this.config.queryFunction) {
		    this.config.queryFunction = defaultTreeQueryFunctionFactory(
			    this.config.entries || [],
			    defaultEntryMatchingFunctionFactory(["displayValue", "additionalInfo"], this.config.matchingOptions),
			    this.config.childrenProperty,
			    this.config.expandedProperty
		    );
		    this.usingDefaultQueryFunction = true;
	    }

        this.entries = this.config.entries;

        this.$originalInput = $(originalInput).addClass("tr-original-input");
        this.$tagComboBox = $(`<div class="tr-tagbox tr-input-wrapper">
            <div class="tr-tagbox-tagarea"></div>
            <div class="tr-trigger ${this.config.showTrigger ? '' : 'hidden'}"><span class="tr-trigger-icon"></span></div>
        </div>`)
            .insertAfter(this.$originalInput);
        this.$originalInput.prependTo(this.$tagComboBox);
        this.$tagArea = this.$tagComboBox.find('.tr-tagbox-tagarea');
        this.$trigger = this.$tagComboBox.find('.tr-trigger');
        this.$trigger.mousedown(() => {
            this.focus();
            if (this._isDropDownOpen) {
                this.closeDropDown();
            } else if (this.editingMode === "editable") {
                this.$editor.select();
                this.openDropDown();
                this.query();
            }
        });
        this.$dropDown = $('<div class="tr-dropdown"></div>')
            .scroll(() => {
                return false;
            });
        this.$dropDownTargetElement = $("body");
        this.setEditingMode(this.config.editingMode);
        this.$editor = $('<span contenteditable="true" class="tagbox-editor" autocomplete="off"></span>');

        this.$editor.appendTo(this.$tagArea).addClass("tr-tagbox-editor tr-editor")
            .focus(() => {
                if (this.blurCausedByClickInsideComponent) {
                    // do nothing!
                } else {
                    this.$originalInput.triggerHandler('focus');
                    this.onFocus.fire();
                    this.$tagComboBox.addClass('focus');
                }
                setTimeout(() => { // the editor needs to apply its new css sheets (:focus) before we scroll to it...
                    this.$editor[0].scrollIntoView({
                        behavior: "smooth",
                        block: "nearest",
                        inline: "nearest"
                    });
                });
            })
            .blur((e) => {
                if (this.blurCausedByClickInsideComponent) {
                    this.$editor.focus();
                } else {
                    this.$originalInput.triggerHandler('blur');
                    this.onBlur.fire();
                    this.$tagComboBox.removeClass('focus');
                    this.entries = null;
                    this.closeDropDown();
                    if (this.$editor.text().trim().length > 0) {
                        this.addSelectedEntry(this.config.freeTextEntryFactory(this.$editor.text()), true, e);
                    }
                    if (this.config.removePartialTagOnBlur && this.currentPartialTag != null) {
                        this.cancelPartialTag();
                    }
                    this.$editor.text("");
                }
            })
            .keydown((e: KeyDownEvent) => {
                if (keyCodes.isModifierKey(e)) {
                    return;
                } else if (e.which == keyCodes.tab || e.which == keyCodes.enter) {
                    const highlightedEntry = this.treeBox.getSelectedEntry();
                    if (this._isDropDownOpen && highlightedEntry != null) {
                        this.addSelectedEntry(highlightedEntry, true, e);
                        e.preventDefault(); // do not tab away from the tag box nor insert a newline character
                    } else if (this.$editor.text().trim().length > 0) {
                        this.addSelectedEntry(this.config.freeTextEntryFactory(this.$editor.text()), true, e);
                        e.preventDefault(); // do not tab away from the tag box nor insert a newline character
                    } else if (this.currentPartialTag) {
                        if (e.shiftKey) { // we do not want the editor to get the focus right back, so we need to position the $editor intelligently...
                            this.doIgnoringBlurEvents(() => this.$editor.insertAfter((this.currentPartialTag as any)._trEntryElement));
                        } else {
                            this.doIgnoringBlurEvents(() => this.$editor.insertBefore((this.currentPartialTag as any)._trEntryElement));
                        }
                        (this.currentPartialTag as any)._trEntryElement.remove();
                        this.currentPartialTag = null;
                    }

                    this.closeDropDown();
                    if (e.which == keyCodes.enter) {
                        e.preventDefault(); // under any circumstances, prevent the new line to be added to the editor!
                    }
                } else if (e.which == keyCodes.left_arrow || e.which == keyCodes.right_arrow) {
                    if (this._isDropDownOpen && this.treeBox.setSelectedNodeExpanded(e.which == keyCodes.right_arrow)) {
                        return false; // the currently highlighted node got effectively expanded/collapsed, so cancel any other effect of the key stroke!
                    } else if (e.which == keyCodes.left_arrow && this.$editor.text().length === 0 && window.getSelection().anchorOffset === 0) {
                        if (this.$editor.prev()) {
                            this.doIgnoringBlurEvents(() => this.$editor.insertBefore(this.$editor.prev()));
                            this.focus();
                        }
                    } else if (e.which == keyCodes.right_arrow && this.$editor.text().length === 0 && window.getSelection().anchorOffset === 0) {
                        if (this.$editor.next()) {
                            this.doIgnoringBlurEvents(() => this.$editor.insertAfter(this.$editor.next()));
                            this.focus();
                        }
                    }
                } else if (e.which == keyCodes.backspace || e.which == keyCodes.delete) {
                    if (this.$editor.text() == "") {
                        if (this.currentPartialTag != null) {
                            this.cancelPartialTag();
                            this.focus();
                        } else {
                            const tagToBeRemoved = this.selectedEntries[this.$editor.index() + (e.which == keyCodes.backspace ? -1 : 0)];
                            if (tagToBeRemoved) {
                                this.removeTag(tagToBeRemoved, true, e);
                                this.closeDropDown();
                            }
                        }
                    } else {
                        this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
	                    setTimeout(() => this.query(1)); // asynchronously to make sure the editor has been updated
                    }
                } else if (e.which == keyCodes.up_arrow || e.which == keyCodes.down_arrow) {
                    const direction = e.which == keyCodes.up_arrow ? -1 : 1;
                    if (!this._isDropDownOpen) {
	                    this.query(direction);
                        this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
                    } else {
                        this.treeBox.selectNextEntry(direction);
                        this.autoCompleteIfPossible(this.config.autoCompleteDelay);
                    }
                    return false; // some browsers move the caret to the beginning on up key
                } else if (e.which == keyCodes.escape) {
                    this.closeDropDown();
                    if (this.$editor.text().length > 0) {
                        this.$editor.text("");
                    } else if (this.currentPartialTag != null) {
                        this.cancelPartialTag();
                        this.focus();
                    }
                } else {
                    if (!this.config.showDropDownOnResultsOnly) {
                        this.openDropDown();
                    }
	                setTimeout(() => this.query(1)); // asynchronously to make sure the editor has been updated
                }
            })
            .keyup((e) => {
                function splitStringBySeparatorChars(s: string, separatorChars: string[]) {
                    return s.split(new RegExp("[" + escapeSpecialRegexCharacter(separatorChars.join()) + "]"));
                }

                if (this.$editor.find('*').length > 0) {
                    this.$editor.text(this.$editor.text()); // removes possible <div> or <br> or whatever the browser likes to put inside...
                }
                const editorValueBeforeCursor = this.getNonSelectedEditorValue();
                if (editorValueBeforeCursor.length > 0) {
                    const tagValuesEnteredByUser = splitStringBySeparatorChars(editorValueBeforeCursor, this.config.freeTextSeparators);

                    for (let i = 0; i < tagValuesEnteredByUser.length - 1; i++) {
                        const value = tagValuesEnteredByUser[i].trim();
                        if (value.length > 0) {
                            this.addSelectedEntry(this.config.freeTextEntryFactory(value), true, e);
                        }
                        this.$editor.text(tagValuesEnteredByUser[tagValuesEnteredByUser.length - 1]);
                        selectElementContents(this.$editor[0], this.$editor.text().length, this.$editor.text().length);
                        this.entries = null;
                        this.closeDropDown();
                    }
                }
            })
            .mousedown(() => {
	            if (this.editingMode === "editable") {
		            if (!this.config.showDropDownOnResultsOnly) {
			            this.openDropDown();
		            }
		            this.query();
	            }
            });


        if (this.$originalInput.attr("placeholder")) {
            this.$editor.attr("placeholder", this.$originalInput.attr("placeholder"));
        }
        if (this.$originalInput.attr("tabindex")) {
            this.$editor.attr("tabindex", this.$originalInput.attr("tabindex"));
        }
        if (this.$originalInput.attr("autofocus")) {
            this.$editor.focus();
        }

        this.$tagComboBox.add(this.$dropDown).mousedown(() => {
            if (this.$editor.is(":focus")) {
                this.blurCausedByClickInsideComponent = true;
            }
        }).mouseup(() => {
            if (this.blurCausedByClickInsideComponent) {
                this.focus();
                setTimeout(() => this.blurCausedByClickInsideComponent = false); // let the other handlers do their job before removing event blocker
            }
        }).mouseout(() => {
            if (this.blurCausedByClickInsideComponent) {
                this.focus();
                setTimeout(() => this.blurCausedByClickInsideComponent = false); // let the other handlers do their job before removing event blocker
            }
        });

        const configWithoutEntries = $.extend({}, this.config);
        configWithoutEntries.entries = []; // for init performance reasons, initialize the dropdown content lazily
        this.treeBox = new TrivialTreeBox<E>(configWithoutEntries);
        this.$dropDown.append(this.treeBox.getMainDomElement());
        this.treeBox.onSelectedEntryChanged.addListener((selectedEntry: E, eventSource?: any, originalEvent?: Event) => {
            if (selectedEntry) {
                this.addSelectedEntry(selectedEntry, true, originalEvent);
                this.treeBox.setSelectedEntryById(null);
                this.closeDropDown();
            }
        });

        this.$tagArea.mousedown((e) => {
            if (this.currentPartialTag == null) {
                let $nearestTag = this.findNearestTag(e);
                if ($nearestTag) {
                    const tagBoundingRect = $nearestTag[0].getBoundingClientRect();
                    const isRightSide = e.clientX > (tagBoundingRect.left + tagBoundingRect.right) / 2;
                    if (isRightSide) {
                        this.doIgnoringBlurEvents(() => this.$editor.insertAfter($nearestTag));
                    } else {
                        this.doIgnoringBlurEvents(() => this.$editor.insertBefore($nearestTag));
                    }
                }
            }
            this.$editor.focus();
        }).click((e) => {
	        if (this.editingMode === "editable") {
		        if (!this.config.showDropDownOnResultsOnly) {
			        this.openDropDown();
		        }
		        this.query();
	        }
        });

        this.setSelectedEntries(this.config.selectedEntries, true);

        // ===
        this.$tagComboBox.data("trivialTagComboBox", this);

        this.popper = createPopper(this.$tagComboBox[0], this.$dropDown[0], {
            placement: 'bottom',
            modifiers: [
                {
                    name: "flip",
                    options: {
                        fallbackPlacements: ['top']
                    }
                },
                {
                    name: "preventOverflow"
                },
                {
                    name: 'dropDownCornerSmoother',
                    enabled: true,
                    phase: 'write',
                    fn: ({state}) => {
                        this.$tagComboBox[0].classList.toggle("dropdown-flipped", state.placement === 'top');
                        this.$dropDown[0].classList.toggle("flipped", state.placement === 'top');
                    }
                }
            ]
        })
    }

    private cancelPartialTag() {
        this.doIgnoringBlurEvents(() => this.$editor.insertBefore((this.currentPartialTag as any)._trEntryElement));
        (this.currentPartialTag as any)._trEntryElement.remove();
        this.currentPartialTag = null;
    }

    private findNearestTag(mouseEvent: MouseDownEvent) {
        let $nearestTag: JQuery = null;
        let smallestDistanceX = 1000000;
        for (let i = 0; i < this.selectedEntries.length; i++) {
            const selectedEntry = this.selectedEntries[i];
            const $tag = (selectedEntry as any)._trEntryElement;
            const tagBoundingRect = $tag[0].getBoundingClientRect();
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

    private updateListBoxEntries() {
        this.blurCausedByClickInsideComponent = false; // we won't get any mouseout or mouseup events for entries if they get removed. so do this here proactively

        this.treeBox.updateEntries(this.entries);
        this.treeBox.highlightTextMatches(this.entries && this.entries.length <= this.config.textHighlightingEntryLimit ? this.getNonSelectedEditorValue() : null);

        this.listBoxDirty = false;
    }

    public updateEntries(newEntries: E[], highlightDirection?: HighlightDirection) {
	    newEntries = newEntries || [];
	    
        this.blurCausedByClickInsideComponent = false; // we won't get any mouseout or mouseup events for entries if they get removed. so do this here proactively

        this.entries = newEntries;

        this.hideSpinner();

        if (this._isDropDownOpen) {
            this.updateListBoxEntries();
        } else {
            this.listBoxDirty = true;
        }

        if (highlightDirection) {
            this.treeBox.selectNextEntry(highlightDirection);
        } else {
            this.treeBox.setSelectedEntryById(null);
        }

        this.autoCompleteIfPossible(this.config.autoCompleteDelay);

        if (this._isDropDownOpen) {
            this.openDropDown(); // only for repositioning!
        }
    }

    private removeTag(tagToBeRemoved: E, fireChangeEvent: boolean = false, originalEvent?: unknown) {
        const index = this.selectedEntries.indexOf(tagToBeRemoved);
        if (index > -1) {
            this.selectedEntries.splice(index, 1);
        }
        (tagToBeRemoved as any)._trEntryElement.remove();
        this.$originalInput.val(this.config.inputValueFunction(this.getSelectedEntries()));
        if (fireChangeEvent) {
            this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
        }
    }

    private async query(highlightDirection?: HighlightDirection) {
	    this.showSpinner();
        let newEntries = await this.config.queryFunction(this.getNonSelectedEditorValue());
        this.updateEntries(newEntries, highlightDirection);
        if (this.config.showDropDownOnResultsOnly && newEntries && newEntries.length > 0 && this.$editor.is(":focus")) {
            this.openDropDown();
        }
    }

	private showSpinner() {
		if (this.$spinners.length === 0) {
			const $spinner = $(this.config.spinnerTemplate).appendTo(this.$dropDown);
			this.$spinners = this.$spinners.add($spinner);
		}
		$(this.getDropDownComponent().getMainDomElement()).addClass('hidden');
	}

	private hideSpinner() {
		this.$spinners.remove();
		this.$spinners = $();
		$(this.getDropDownComponent().getMainDomElement()).removeClass('hidden');
	}

    private fireChangeEvents(entries: E[], originalEvent: unknown) {
        this.$originalInput.trigger("change");
        this.onSelectedEntryChanged.fire(unProxyEntry(entries), originalEvent);
    }

    private addSelectedEntry(entry: E, fireEvent = false, originalEvent?: unknown, forceAcceptance?: boolean) {
        if (entry == null) {
            return; // do nothing
        }
        if (!forceAcceptance && !this.config.selectionAcceptor(entry)) {
            return;
        }

        let wasPartial = !!this.currentPartialTag;
        const editorIndex = wasPartial ? (this.currentPartialTag as any)._trEntryElement.index() : this.$editor.index();
        if (wasPartial) {
            this.doIgnoringBlurEvents(() => this.$editor.appendTo(this.$tagArea)); // make sure the event handlers don't get detached when removing the partial tag
            (this.currentPartialTag as any)._trEntryElement.remove();
            entry = this.config.entryMerger(this.currentPartialTag, entry);
        }

        const tag = $.extend({}, entry);

        if (this.config.tagCompleteDecider(entry)) {
            this.selectedEntries.splice(editorIndex, 0, tag);
            this.$originalInput.val(this.config.inputValueFunction(this.getSelectedEntries()));
            this.currentPartialTag = null;
        } else {
            this.currentPartialTag = tag;
        }

        const $entry = $(this.config.selectedEntryRenderingFunction(tag));
        const $tagWrapper = $('<div class="tr-tagbox-tag"></div>')
            .append($entry);

        this.doIgnoringBlurEvents(() => this.insertAtIndex($tagWrapper, editorIndex));
        (tag as any)._trEntryElement = $tagWrapper;

        $entry.find('.tr-remove-button').click((e) => {
            this.removeTag(tag, true, e);
            return false;
        });

        if (this.config.tagCompleteDecider(entry)) {
            this.doIgnoringBlurEvents(() => this.insertAtIndex(this.$editor, editorIndex + 1));
        } else {
            this.doIgnoringBlurEvents(() => this.$editor.appendTo($entry.find('.tr-editor')));
        }

        this.$editor.text("");

        if (this.config.tagCompleteDecider(entry) && fireEvent) {
            this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
        }
    }

    private repositionDropDown() {
        this.popper.update();
	    this.$dropDown.width(this.$tagComboBox.width());
    }

    public openDropDown() {
        if (this.isDropDownNeeded()) {
            if (this.listBoxDirty) {
                this.updateListBoxEntries();
            }
            this.$tagComboBox.addClass("open");
            this.$dropDown.show();
            this.repositionDropDown();
            this._isDropDownOpen = true;
        }
        if (this.repositionDropDownScheduler == null) {
            this.repositionDropDownScheduler = window.setInterval(() => this.repositionDropDown(), 300); // make sure that under no circumstances the dropdown is mal-positioned
        }
    }

    public closeDropDown() {
        this.$tagComboBox.removeClass("open");
        this.$dropDown.hide();
        this._isDropDownOpen = false;
        if (this.repositionDropDownScheduler != null) {
            clearInterval(this.repositionDropDownScheduler);
            this.repositionDropDownScheduler = null;
        }
    }

    private getNonSelectedEditorValue() {
        const editorText = this.$editor.text().replace(String.fromCharCode(160), " ");
        const selection = window.getSelection();
        if (selection.anchorOffset != selection.focusOffset) {
            return editorText.substring(0, Math.min((window.getSelection() as any).baseOffset, window.getSelection().focusOffset));
        } else {
            return editorText;
        }
    }

    private autoCompleteIfPossible(delay: number) {
        if (this.config.autoComplete) {
            clearTimeout(this.autoCompleteTimeoutId);
            const highlightedEntry = this.treeBox.getSelectedEntry();
            if (highlightedEntry && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
                this.autoCompleteTimeoutId = window.setTimeout(() => {
                    const currentEditorValue = this.getNonSelectedEditorValue();
                    const autoCompleteString = this.config.autoCompleteFunction(currentEditorValue, highlightedEntry) || currentEditorValue;
                    this.$editor.text(currentEditorValue + autoCompleteString.replace(' ', String.fromCharCode(160)).substr(currentEditorValue.length)); // I have to replace whitespaces by 160 because text() trims whitespaces...
                    this.repositionDropDown(); // the auto-complete might cause a line-break, so the dropdown would cover the editor...
                    if (this.$editor.is(":focus")) {
                        selectElementContents(this.$editor[0], currentEditorValue.length, autoCompleteString.length);
                    }
                }, delay || 0);
            }
            this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        }
    }

    private isDropDownNeeded() {
        return this.editingMode == 'editable' && (this.config.entries && this.config.entries.length > 0 || !this.usingDefaultQueryFunction || this.config.showTrigger);
    }

    private insertAtIndex($element: JQuery, index: number) {
        const lastIndex = this.$tagArea.children().length;
        if (index < lastIndex) {
            this.$tagArea.children().eq(index).before($element);
        } else {
            this.$tagArea.append($element);
        }
    }

    private doIgnoringBlurEvents(f:Function) {
        let oldValueOfBlurCausedByClickInsideComponent = this.blurCausedByClickInsideComponent;
        this.blurCausedByClickInsideComponent = true;
        try {
            return f.call(this);
        } finally {
            this.blurCausedByClickInsideComponent = oldValueOfBlurCausedByClickInsideComponent;
        }
    }

    public setEditingMode(newEditingMode: EditingMode) {
        this.editingMode = newEditingMode;
        this.$tagComboBox.removeClass("editable readonly disabled").addClass(this.editingMode);
        if (this.isDropDownNeeded()) {
            this.$dropDown.appendTo(this.$dropDownTargetElement);
        }
    }

    public setSelectedEntries(entries: E[], forceAcceptance?: boolean) {
        this.selectedEntries
            .slice() // copy the array as it gets changed during the forEach loop
            .forEach((e) => this.removeTag(e, false));
        if (entries) {
            for (let i = 0; i < entries.length; i++) {
                this.addSelectedEntry(entries[i], false, null, forceAcceptance);
            }
        }
    }

    public getSelectedEntries(): E[] {
        const selectedEntriesToReturn: E[] = [];
        for (let i = 0; i < this.selectedEntries.length; i++) {
            const selectedEntryToReturn = $.extend({}, this.selectedEntries[i]);
            delete (selectedEntryToReturn as any)._trEntryElement;
            selectedEntriesToReturn.push(selectedEntryToReturn);
        }
        return selectedEntriesToReturn;
    };

	public getDropDownComponent(): TrivialComponent {
	    return this.treeBox;
    }

    public getCurrentPartialTag() {
        return this.currentPartialTag;
    }

    public focus() {
        this.$editor.focus();
	    selectElementContents(this.$editor[0], 0, this.$editor.text().length); // we need to do this, else the cursor does not appear in Chrome when navigating using left and right keys...
    };

    public getEditor(): Element {
        return this.$editor[0];
    }

	public setShowTrigger(showTrigger: boolean) {
		this.$trigger.toggleClass('hidden', !showTrigger);
	}

	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
        this.$originalInput.removeClass('tr-original-input').insertBefore(this.$tagComboBox);
        this.$tagComboBox.remove();
        this.$dropDown.remove();
    };

    getMainDomElement(): HTMLElement {
        return this.$tagComboBox[0];
    }

}
