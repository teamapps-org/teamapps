/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {UiField} from "./UiField";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {
	UiRichTextEditor_ImageUploadFailedEvent,
	UiRichTextEditor_ImageUploadStartedEvent,
	UiRichTextEditor_ImageUploadSuccessfulEvent,
	UiRichTextEditor_ImageUploadTooLargeEvent,
	UiRichTextEditorConfig,
	UiRichTextEditorCommandHandler,
	UiRichTextEditorEventSource
} from "../../generated/UiRichTextEditorConfig";
import * as tinymce from 'tinymce';
import {Editor, Settings} from 'tinymce';
import 'tinymce/themes/modern/theme';
import {executeWhenAttached} from "../util/ExecuteWhenAttached";
import {DeferredExecutor} from "../util/DeferredExecutor";
import {generateUUID, keyCodes} from "trivial-components";
// Any plugins you want to use has to be imported
import 'tinymce/plugins/lists';
import 'tinymce/plugins/table';
import 'tinymce/plugins/image';
import 'tinymce/plugins/imagetools';
import 'tinymce/plugins/link';
import 'tinymce/plugins/autolink';
import 'tinymce/plugins/contextmenu';
import 'tinymce/plugins/searchreplace';
import 'tinymce/plugins/spellchecker';
import 'tinymce/plugins/textcolor';
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {debouncedMethod, DebounceMode} from "../util/debounce";
import {UiToolbarVisibilityMode} from "../../generated/UiToolbarVisibilityMode";
import {UiSpinner} from "../micro-components/UiSpinner";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {EventFactory} from "../../generated/EventFactory";
import {parseHtml, removeTags} from "../Common";


export class UiRichTextEditor extends UiField<UiRichTextEditorConfig, string> implements UiRichTextEditorEventSource, UiRichTextEditorCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 5000);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);
	public readonly onImageUploadFailed: TeamAppsEvent<UiRichTextEditor_ImageUploadFailedEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadFailedEvent>(this);
	public readonly onImageUploadStarted: TeamAppsEvent<UiRichTextEditor_ImageUploadStartedEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadStartedEvent>(this);
	public readonly onImageUploadSuccessful: TeamAppsEvent<UiRichTextEditor_ImageUploadSuccessfulEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadSuccessfulEvent>(this);
	public readonly onImageUploadTooLarge: TeamAppsEvent<UiRichTextEditor_ImageUploadTooLargeEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadTooLargeEvent>(this);

	private static readonly TRANSLATION_FILES: { [languageIso: string]: string } = {
		"af": "af_ZA.js",
		"ar": "ar.js",
		"be": "be.js",
		"bg": "bg_BG.js",
		"ca": "ca.js",
		"cs": "cs.js",
		"cy": "cy.js",
		"da": "da.js",
		"de": "de.js",
		"dv": "dv.js",
		"el": "el.js",
		"es": "es.js",
		"et": "et.js",
		"fr": "fr_FR.js",
		"ga": "ga.js",
		"gl": "gl.js",
		"he": "he_IL.js",
		"hr": "hr.js",
		"hu": "hu_HU.js",
		"it": "it.js",
		"ja": "ja.js",
		"ka": "ka_GE.js",
		"kab": "kab.js",
		"kk": "kk.js",
		"km": "km_KH.js",
		"ko": "ko_KR.js",
		"lv": "lv.js",
		"nb": "nb_NO.js",
		"nl": "nl.js",
		"pl": "pl.js",
		"pt": "pt_BR.js",
		"ro": "ro.js",
		"ru": "ru.js",
		"sk": "sk.js",
		"sl": "sl_SI.js",
		"sv": "sv_SE.js",
		"ta": "ta.js",
		"th": "th_TH.js",
		"tr": "tr.js",
		"uk": "uk.js",
		"uz": "uz.js",
		"vi": "vi_VN.js",
		"zh": "zh_CN.js"
	};


	private $main: HTMLElement;
	private $toolbarContainer: HTMLElement;
	private editor: Editor;
	private mceReadyExecutor: DeferredExecutor;
	private uuid: string;
	private imageUploadSuccessCallbacksByUuid: { [fileUuid: string]: (location: string) => void } = {};
	private $fileField: HTMLInputElement;
	private buttonGroupWidths: number[];
	private _hasFocus: boolean;
	private toolbarVisibilityMode: UiToolbarVisibilityMode;
	private maxImageFileSizeInBytes: number;
	private uploadUrl: string;
	private runningImageUploadsCount: number = 0;
	private $spinnerWrapper: HTMLElement;
	private destroying: boolean;
	private $readonlyView: HTMLElement;
	private initializationStarted: boolean;

	protected initialize(config: UiRichTextEditorConfig, context: TeamAppsUiContext) {
		this.toolbarVisibilityMode = config.toolbarVisibilityMode;
		this.setMaxImageFileSizeInBytes(config.maxImageFileSizeInBytes);
		this.setUploadUrl(config.uploadUrl);

		this.mceReadyExecutor = new DeferredExecutor();
		this.uuid = "c-" + generateUUID();
		this.$main = parseHtml(`<div class="UiRichTextEditor teamapps-input-wrapper field-border field-border-glow" id="${this.uuid}">
			<div class="toolbar-container"></div>
			<div class="inline-editor field-background"></div>
			<input type="file" class="file-upload-button"></input>
			<div class="spinner-wrapper hidden"></div>
			<div class="readonly-view"></div>
		</div>`);
		this.$toolbarContainer = this.$main.querySelector<HTMLElement>(':scope .toolbar-container');
		this.$spinnerWrapper = this.$main.querySelector<HTMLElement>(':scope .spinner-wrapper');
		this.$readonlyView = this.$main.querySelector<HTMLElement>(':scope .readonly-view');
		this.$spinnerWrapper.appendChild(new UiSpinner().getMainDomElement());
		this.$fileField = this.$main.querySelector(':scope .file-upload-button');
		this.$fileField.addEventListener("change", (e) => {
			let files = (<HTMLInputElement> this.$fileField).files;
			for (let i = 0; i < files.length; i++) {
				let file = files.item(i);
				if (file.size > this.maxImageFileSizeInBytes) {
					this.onImageUploadTooLarge.fire(EventFactory.createUiRichTextEditor_ImageUploadTooLargeEvent(this.getId(), file.name, file.type, file.size));
				} else {
					let fileReader = new FileReader();
					fileReader.onload = (e) => {
						let dataUrl = (e.target as any).result;
						this.editor.insertContent(`<img src="${dataUrl}" width="300">`);
						this.editor.uploadImages(() => undefined);
					};
					fileReader.readAsDataURL(files.item(0));
					this.$fileField.value ='';
				}
			}
		});
		this.initTinyMce();

		this.setMinHeight(config.minHeight);
		this.setMaxHeight(config.maxHeight);
	}

	@executeWhenAttached()
	private initTinyMce() {
		if (this.isEditable() && !this.initializationStarted) {
			this.initializationStarted = true;
			const translationFileName = UiRichTextEditor.TRANSLATION_FILES[this._context.config.isoLanguage];
			tinymce.init({
				theme_url: '/runtime-resources/tinymce/themes/modern/theme.js',
				skin_url: '/runtime-resources/tinymce/skins/lightgray',
				selector: `#${this.uuid} > .inline-editor`,
				fixed_toolbar_container: `#${this.uuid} > .toolbar-container`,
				branding: false,
				menubar: false,
				inline: true,
				toolbar: "undo redo | styleselect | bold italic underline forecolor backcolor removeformat | alignleft aligncenter alignright alignjustify | blockquote bullist numlist outdent indent | insertimage table | overflowbutton",
				plugins: 'lists table image imagetools link autolink contextmenu searchreplace textcolor',
				contextmenu: "openlink link unlink searchreplace",
				language_url: translationFileName != null ? "/runtime-resources/tinymce/langs/" + translationFileName : undefined,
				entity_encoding: "raw",
				custom_elements: 'alert',
				style_formats: [{
					title: 'Heading 1',
					format: 'h1'
				}, {
					title: 'Heading 2',
					format: 'h2'
				}, {
					title: 'Paragraph',
					format: 'p'
				}, {
					title: 'Blockquote',
					format: 'blockquote'
				}, {
					title: 'Note',
					block: 'alert',
					wrapper: 1,
					remove: 'all'
				}, {
					title: 'Pre',
					format: 'pre',
				}, {
					title: 'Code',
					icon: 'code',
					format: 'code'
				}, {
					title: '|'
				}, {
					title: 'Headings',
					items: [{
						title: 'Heading 1',
						format: 'h1'
					}, {
						title: 'Heading 2',
						format: 'h2'
					}, {
						title: 'Heading 3',
						format: 'h3'
					}, {
						title: 'Heading 4',
						format: 'h4'
					}, {
						title: 'Heading 5',
						format: 'h5'
					}, {
						title: 'Heading 6',
						format: 'h6'
					}]
				}, {
					title: 'Blocks',
					items: [{
						title: 'Paragraph',
						format: 'p'
					}, {
						title: 'Blockquote',
						format: 'blockquote'
					}, {
						title: 'Note',
						block: 'alert',
						wrapper: 1,
						remove: 'all'
					}, {
						title: 'Pre',
						format: 'pre'
					}]
				}, {
					title: 'Inline',
					items: [{
						title: 'Bold',
						icon: 'bold',
						format: 'bold'
					}, {
						title: 'Italic',
						icon: 'italic',
						format: 'italic'
					}, {
						title: 'Underline',
						icon: 'underline',
						format: 'underline'
					}, {
						title: 'Strikethrough',
						icon: 'strikethrough',
						format: 'strikethrough'
					}, {
						title: 'Superscript',
						icon: 'superscript',
						format: 'superscript'
					}, {
						title: 'Subscript',
						icon: 'subscript',
						format: 'subscript'
					}, {
						title: 'Code',
						icon: 'code',
						format: 'code'
					}]
				}],
				init_instance_callback: editor => {
					if (this.destroying) { // already destroyed
						editor.destroy();
						return;
					}
					this.editor = editor;

					editor.addMenuItem('bullist', {text: 'Bullet list', icon: 'bullist', cmd: 'InsertUnorderedList'});
					editor.addMenuItem('numlist', {text: 'Numbered list', icon: 'numlist', cmd: 'InsertOrderedList'});
					editor.addMenuItem('insertimage', {icon: 'image', text: 'Insert image', onclick: () => this.$fileField.click()});
					editor.addButton('overflowbutton', {
						type: 'MenuButton',
						image: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAABeUlEQVRYhe2TsWoUQRzGf//dWbYysZW7vIDBOq1NiK2+QcRU6RI4mNtbWLi7DRzoC0iSXkFfQZLq7NRHiBZ2Aatjb/ZvMx66mW1tMj9YmP3mm28+ZhiIRCKRyH1H/gzKsnzinHsN7KnqjYi8qev6IrRoNBo9MMacAS+89GG9XtvFYvEr5B+Pxy9V9UREdoBlmqan0+n0G0DiA4fOuWtgH9gSkV3g3Fp7GAo0xrwHjoFH/jvOsuxd3+bAuc/cAvadc1dFUQw2BYwxr4DtwPrTrlCW5S5w0NVV9Zm19nFAPwnkPlTVo00BYCfUXkSGXa1pmkHI6/135vyx92Yn/mfZk/m5KyRJ8gVoAt4G+BrQg9lt2y43BbIsuwCuO57bNE3vXEFd1z8B25EVsH7uH3zGbUe+yvP8Ev56BVVVmdVqdZgkyZ6qfheRt/P5/EeoPcBkMnnqnHvuN/k4m80+9XmLohio6pGIDNu2XeZ5fllV1brPH4lEIpHIf+U3FW15e2UaPR8AAAAASUVORK5CYII=',
						title: '...',
						menu: [
							(editor as any).menuItems['undo'],
							(editor as any).menuItems['redo'],
							(editor as any).menuItems['formats'],
							(editor as any).menuItems['bold'],
							(editor as any).menuItems['italic'],
							(editor as any).menuItems['removeformat'],
							(editor as any).menuItems['align'],
							(editor as any).menuItems['bullist'],
							(editor as any).menuItems['numlist'],
							(editor as any).menuItems['insertimage'],
							(editor as any).menuItems['inserttable'],
							(editor as any).menuItems['openlink'],
							(editor as any).menuItems['link'],
							(editor as any).menuItems['unlink'],
							(editor as any).menuItems['searchreplace']
						]
					});
					editor.addButton('insertimage', {icon: 'image', tooltip: 'Insert image', onclick: () => this.$fileField.click()});
					this.editor.fire("focus"); // triggers the initial rendering of the toolbar
					this.editor.fire("blur"); // make the editor NOT being rendered as focused
					this.updateToolbarOverflow(); // the toolbar got visible only now...

					this.mceReadyExecutor.ready = true;

					this.onResize();
				},
				setup: (editor) => {
					editor.on('change undo redo keypress', (e) => {
						this.fireTextInputEvent();
					});
					editor.on('keydown', (e) => {
						if (e.keyCode === keyCodes.escape) {
							this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
						} else if (e.keyCode === keyCodes.enter) {
							this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
						}
					});
					editor.on('undo redo', (e) => {
						this.editor.uploadImages(() => undefined);
					});
					editor.on('focus', (e) => {
						this._hasFocus = true;
						this.getMainDomElement().classList.add('focus');
						this.updateToolbarVisibility();
						this.onFocused.fire(null);
					});
					editor.on('blur', (e) => {
						this._hasFocus = false;
						this.getMainDomElement().classList.remove('focus');
						if (this.mayFireChangeEvents()) {
							this.commit();
						}
						this.updateToolbarVisibility();
						this.onBlurred.fire(null);
					});
				},
				images_upload_handler: (blobInfo: { base64: () => string, blob: () => Blob, blobUri: () => string, filename: () => string, id: () => string, name: () => string, uri: () => string }, success, failure) => {
					this.runningImageUploadsCount++;
					this.updateSpinnerVisibility();
					this.onImageUploadStarted.fire(EventFactory.createUiRichTextEditor_ImageUploadStartedEvent(this.getId(), blobInfo.filename(), blobInfo.blob().type, blobInfo.blob().size, this.runningImageUploadsCount));

					let upload = (isFirstUpload: boolean) => {
						let xhr: XMLHttpRequest;
						let formData: FormData;

						xhr = new XMLHttpRequest();
						xhr.withCredentials = false;
						xhr.open('POST', this.uploadUrl);

						const handleUploadFailure = () => {
							if (isFirstUpload) {
								setTimeout(() => upload(false), 5000);
							} else {
								this.runningImageUploadsCount--;
								this.updateSpinnerVisibility();
								this.onImageUploadFailed.fire(EventFactory.createUiRichTextEditor_ImageUploadFailedEvent(this.getId(), blobInfo.filename(), blobInfo.blob().type, blobInfo.blob().size, this.runningImageUploadsCount));
								this.editor.undoManager.transact(() => {
									this.$main.querySelector<HTMLElement>(`:scope [src='${blobInfo.blobUri()}']`).remove();
								});
								failure(null);
							}
						};

						xhr.onload = () => {
							if (xhr.status < 200 || xhr.status >= 300) {
								handleUploadFailure();
							} else {
								let fileUuid = JSON.parse(xhr.responseText)[0];
								this.imageUploadSuccessCallbacksByUuid[fileUuid] = success;
								this.runningImageUploadsCount--;
								this.updateSpinnerVisibility();
								this.onImageUploadSuccessful.fire(EventFactory.createUiRichTextEditor_ImageUploadSuccessfulEvent(this.getId(), fileUuid, blobInfo.filename(), blobInfo.blob().type, blobInfo.blob().size, this.runningImageUploadsCount));
							}
						};
						xhr.onerror = () => {
							handleUploadFailure();
						};

						formData = new FormData();
						formData.append('files', blobInfo.blob(), blobInfo.filename());

						xhr.send(formData);
					};
					upload(true);
				}
			} as Settings);
		}
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	private fireTextInputEvent() {
		if (this.mayFireChangeEvents()) {
			this.onTextInput.fire(EventFactory.createUiTextInputHandlingField_TextInputEvent(this.getId(), this.getTransientValue()));
		}
	}

	private updateSpinnerVisibility() {
		this.$spinnerWrapper.classList.toggle("hidden", this.runningImageUploadsCount === 0);
	}

	setUploadedImageUrl(fileUuid: string, url: string): void {
		let successCallback = this.imageUploadSuccessCallbacksByUuid[fileUuid];
		if (successCallback != null) {
			successCallback(url);
			this.fireTextInputEvent(); // the html has changed at this point with the replacement of the data URL with the server-side URL
		}
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	public getFocusableElement(): HTMLElement {
		return this.editor != null ? this.editor.getBody() : null;
	}

	protected displayCommittedValue(): void {
		const value = removeTags(this.getCommittedValue(), "style");
		if (this.isEditable()) {
			this.mceReadyExecutor.invokeWhenReady(() => {
				this.editor.setContent(value);
				this.editor.undoManager.clear();
			});
		} else {
			this.$readonlyView.innerHTML = value || '';
		}
	}

	focus(): void {
		this.editor && this.editor.focus(false);
	}

	doDestroy(): void {
		this.destroying = true;
		if (this.editor != null) {
            this.editor.destroy();
        }
	}

	getTransientValue(): string {
		let content = this.editor != null ? this.editor.getContent() : this.getCommittedValue();
		return content.replace(/src="data:.*?"/g, "");
	}

	@debouncedMethod(700, DebounceMode.BOTH)
	@executeWhenAttached(true)
	onResize(): void {
		this.updateToolbarOverflow();
	}

	private updateToolbarOverflow() {
		if (!this.buttonGroupWidths) {
			let buttonGroupWidths = Array.from(this.$toolbarContainer.querySelectorAll<HTMLElement>(":scope .mce-btn-group:not(:last-child)"))
				.map(e => e.offsetWidth);
			if (buttonGroupWidths.reduce((sum, groupWidth) => sum + groupWidth, 0) === 0) { // toolbar apparently not visible
				return;
			} else {
				this.buttonGroupWidths = buttonGroupWidths;
			}
		}

		let availableWidth = this.$toolbarContainer.offsetWidth - 50 /*overflowbutton*/;
		let $groupButtons = Array.from(this.$toolbarContainer.querySelectorAll<HTMLElement>(":scope .mce-btn-group:not(:last-child)"));
		let consumedWidth = 0;
		this.buttonGroupWidths.forEach((groupWidth, index) => {
			consumedWidth = consumedWidth + groupWidth;
			$groupButtons[index].classList.toggle('hidden', consumedWidth > availableWidth);
		});
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode: UiFieldEditingMode): void {
		let wasEditable = !(oldEditingMode === UiFieldEditingMode.DISABLED || oldEditingMode === UiFieldEditingMode.READONLY);
		if (this.isEditable()) {
			this.initTinyMce();
		} else if (wasEditable && !this.isEditable()) {
			this.$readonlyView.innerHTML = removeTags(this.getCommittedValue(), "style");
		}
		UiField.defaultOnEditingModeChangedImpl(this);
		this.$main.classList.toggle("editable-if-focused", editingMode === UiFieldEditingMode.EDITABLE_IF_FOCUSED);
		this.updateToolbarVisibility();
	}

	getDefaultValue(): string {
		return "";
	}

	setToolbarVisibilityMode(toolbarVisibilityMode: UiToolbarVisibilityMode): void {
		this.toolbarVisibilityMode = toolbarVisibilityMode;
		this.updateToolbarVisibility();
	}

	private updateToolbarVisibility() {
		const toolbarVisible =
			(this.isEditable() && (this.toolbarVisibilityMode === UiToolbarVisibilityMode.VISIBLE || (this.toolbarVisibilityMode === UiToolbarVisibilityMode.VISIBLE_IF_FOCUSED && this._hasFocus)));
		this.$toolbarContainer.classList.toggle('hidden', !toolbarVisible);
		this.updateToolbarOverflow();
	}

	hasFocus(): boolean {
		return this._hasFocus;
	}

	setMaxImageFileSizeInBytes(maxImageFileSizeInBytes: number): void {
		this.maxImageFileSizeInBytes = maxImageFileSizeInBytes;
	}

	setUploadUrl(uploadUrl: string): void {
		this.uploadUrl = uploadUrl;
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-UiRichTextEditor">${removeTags(value, "style")}</div>`;
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

	private mayFireChangeEvents() {
		return this.mceReadyExecutor.ready && !this.destroying;
	}

	public setMinHeight(minHeight: number) {
		this.$main.style.minHeight = minHeight ? minHeight + "px" : "";
	}

	public setMaxHeight(maxHeight: number) {
		this.$main.style.maxHeight = maxHeight ? maxHeight + "px" : "";
	}

	append(s: string, scrollToBottom: boolean): void {
		// this.mceReadyExecutor.invokeWhenReady(() => {
		// 	this.editor.appendChild(s);
		// 	this.editor.
		// });
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiRichTextEditor", UiRichTextEditor);
