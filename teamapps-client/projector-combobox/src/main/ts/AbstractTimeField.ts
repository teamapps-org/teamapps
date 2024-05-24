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
import {TimeSuggestionEngine} from "./TimeSuggestionEngine";
import {LocalDateTime} from "./LocalDateTime";
import {
	AbstractField, DtoDateTimeFormatDescriptor,
	DtoFieldEditingMode, SpecialKey,
	DtoTextInputHandlingField_SpecialKeyPressedEvent,
	DtoTextInputHandlingField_TextInputEvent
} from "teamapps-client-core-components";
import {DtoAbstractTimeField, DtoAbstractTimeFieldCommandHandler, DtoAbstractTimeFieldEventSource} from "./generated";
import {TeamAppsEvent} from "teamapps-client-core";
import {TrivialComboBox} from "./trivial-components/TrivialComboBox";
import {TreeBoxDropdown} from "./trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "./trivial-components/TrivialTreeBox";


export abstract class AbstractTimeField<C extends DtoAbstractTimeField, V> extends AbstractField<C, V> implements DtoAbstractTimeFieldEventSource, DtoAbstractTimeFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<DtoTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<DtoTextInputHandlingField_TextInputEvent>({throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent>({throttlingMode: "debounce", delay: 250});

	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected timeRenderer: (time: LocalDateTime) => string;

	protected initialize(config: DtoAbstractTimeField) {
		let timeSuggestionEngine = new TimeSuggestionEngine();
		this.timeRenderer = this.createTimeRenderer();

		this.trivialComboBox = new TrivialComboBox<LocalDateTime>({
			showTrigger: config.showDropDownButton,
			entryToEditorTextFunction: entry => this.localDateTimeToString(entry),
			editingMode: config.editingMode === DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			selectedEntryRenderingFunction: localDateTime => this.timeRenderer(localDateTime),
		}, new TreeBoxDropdown({
			queryFunction: (searchString: string) => timeSuggestionEngine.generateSuggestions(searchString),
			textHighlightingEntryLimit: -1, // no highlighting!
			preselectionMatcher: (query, entry) => this.localDateTimeToString(entry).toLowerCase().indexOf(query.toLowerCase()) >= 0
		}, new TrivialTreeBox<LocalDateTime>({
			entryRenderingFunction: localDateTime => this.timeRenderer(localDateTime),
		})));

		this.trivialComboBox.getEditor().addEventListener("input", e => this.onTextInput.fire({enteredString: (e.target as HTMLInputElement).value}));
		this.trivialComboBox.getMainDomElement().classList.add("DtoAbstractTimeField");
		this.trivialComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		this.trivialComboBox.getEditor().addEventListener("keydown", (e: KeyboardEvent) => {
			if (e.key === "Escape") {
				this.onSpecialKeyPressed.fire({
					key: SpecialKey.ESCAPE
				});
			} else if (e.key === "Enter") {
				this.onSpecialKeyPressed.fire({
					key: SpecialKey.ENTER
				});
			}
		});

		this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
	}

	protected abstract localDateTimeToString(entry: LocalDateTime): string;

	protected abstract createTimeRenderer(): (time: LocalDateTime) => string;

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	protected initFocusHandling() {
		this.trivialComboBox.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialComboBox.onBlur.addListener(() => this.onBlur.fire({}));
	}

	focus(): void {
		this.trivialComboBox.focus();
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		this.getMainInnerDomElement().classList.remove(...Object.values(AbstractField.editingModeCssClasses));
		this.getMainInnerDomElement().classList.add(AbstractField.editingModeCssClasses[editingMode]);
		if (editingMode === DtoFieldEditingMode.READONLY) {
			this.trivialComboBox.setEditingMode("readonly");
		} else if (editingMode === DtoFieldEditingMode.DISABLED) {
			this.trivialComboBox.setEditingMode("disabled");
		} else {
			this.trivialComboBox.setEditingMode("editable");
		}
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	getDefaultValue(): V {
		return null;
	}

	setLocaleAndTimeFormat(locale: string, timeFormat: DtoDateTimeFormatDescriptor): void {
		this.config.locale = locale;
		this.config.timeFormat = timeFormat;
		this.timeRenderer = this.createTimeRenderer();
		this.trivialComboBox.setValue(this.trivialComboBox.getValue());
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

}
