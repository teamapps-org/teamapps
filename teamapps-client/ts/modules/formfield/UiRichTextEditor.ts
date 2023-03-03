/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
	UiRichTextEditorCommandHandler,
	UiRichTextEditorConfig,
	UiRichTextEditorEventSource
} from "../../generated/UiRichTextEditorConfig";
import tinymce, {Editor} from 'tinymce';
import 'tinymce/themes/silver';
import 'tinymce/icons/default';
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";
import {DeferredExecutor} from "../util/DeferredExecutor";
import {generateUUID, keyCodes} from "../trivial-components/TrivialCore";
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
import {UiToolbarVisibilityMode} from "../../generated/UiToolbarVisibilityMode";
import {UiSpinner} from "../micro-components/UiSpinner";
import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {parseHtml, removeTags} from "../Common";
import {throttledMethod} from "../util/throttle";


export class UiRichTextEditor extends UiField<UiRichTextEditorConfig, string> implements UiRichTextEditorEventSource, UiRichTextEditorCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>();
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>({
		throttlingMode: "debounce",
		delay: 250
	});
	public readonly onImageUploadFailed: TeamAppsEvent<UiRichTextEditor_ImageUploadFailedEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadFailedEvent>();
	public readonly onImageUploadStarted: TeamAppsEvent<UiRichTextEditor_ImageUploadStartedEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadStartedEvent>();
	public readonly onImageUploadSuccessful: TeamAppsEvent<UiRichTextEditor_ImageUploadSuccessfulEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadSuccessfulEvent>();
	public readonly onImageUploadTooLarge: TeamAppsEvent<UiRichTextEditor_ImageUploadTooLargeEvent> = new TeamAppsEvent<UiRichTextEditor_ImageUploadTooLargeEvent>();

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
	private maxImageFileSizeInBytes: number;
	private uploadUrl: string;
	private runningImageUploadsCount: number = 0;
	private $spinnerWrapper: HTMLElement;
	private destroying: boolean;

	protected initialize(config: UiRichTextEditorConfig, context: TeamAppsUiContext) {
		this._config.toolbarVisibilityMode = config.toolbarVisibilityMode;
		this.setMaxImageFileSizeInBytes(config.maxImageFileSizeInBytes);
		this.setUploadUrl(config.uploadUrl);

		this.mceReadyExecutor = new DeferredExecutor();
		this.uuid = "c-" + generateUUID();
		this.$main = parseHtml(`<div class="UiRichTextEditor default-min-field-width teamapps-input-wrapper field-border field-border-glow" id="${this.uuid}">
			<div class="toolbar-container"></div>
			<div class="inline-editor field-background"></div>
			<input type="file" class="file-upload-button" tabindex="-1"></input>
			<div class="spinner-wrapper hidden"></div>
		</div>`);
		this.$toolbarContainer = this.$main.querySelector<HTMLElement>(':scope .toolbar-container');
		this.$spinnerWrapper = this.$main.querySelector<HTMLElement>(':scope .spinner-wrapper');
		this.$spinnerWrapper.appendChild(new UiSpinner().getMainDomElement());
		this.$fileField = this.$main.querySelector(':scope .file-upload-button');
		this.$fileField.addEventListener("change", (e) => {
			let files = (<HTMLInputElement>this.$fileField).files;
			for (let i = 0; i < files.length; i++) {
				let file = files.item(i);
				if (file.size > this.maxImageFileSizeInBytes) {
					this.onImageUploadTooLarge.fire({
						fileName: file.name,
						mimeType: file.type,
						sizeInBytes: file.size
					});
				} else {
					let fileReader = new FileReader();
					fileReader.onload = (e) => {
						let dataUrl = (e.target as any).result;
						this.editor.insertContent(`<img src="${dataUrl}" width="300">`);
						this.editor.uploadImages(() => undefined);
					};
					fileReader.readAsDataURL(files.item(0));
					this.$fileField.value = '';
				}
			}
		});
		this.initTinyMce();

		this.setMinHeight(config.minHeight);
		this.setMaxHeight(config.maxHeight);
	}

	@executeWhenFirstDisplayed()
	private initTinyMce() {
		const translationFileName = UiRichTextEditor.TRANSLATION_FILES[this._config.locale];
		tinymce.init({
			// theme: 'mobile',
			// skin: 'oxide',
			// icons: 'default',
			theme_url: '/runtime-resources/tinymce/themes/silver/index.js',
			skin_url: '/runtime-resources/tinymce/skins/ui/oxide',
			selector: `#${this.uuid} > .inline-editor`,
			readonly: !this.isEditable(),
			fixed_toolbar_container: `#${this.uuid} > .toolbar-container`,
			toolbar_persist: true,
			branding: false,
			menubar: false,
			inline: true,
			toolbar: `undo redo | styleselect | bold italic underline forecolor backcolor removeformat | ${this._config.imageUploadEnabled ? 'insertimagefromdisk |' : ' |'} alignleft aligncenter alignright alignjustify | blockquote bullist numlist outdent indent | table | overflowbutton`,
			plugins: `lists table link autolink contextmenu searchreplace textcolor ${this._config.imageUploadEnabled ? 'image imagetools' : ''}`,
			contextmenu: "openlink link unlink searchreplace",
			language_url: translationFileName != null ? "/runtime-resources/tinymce/langs/" + translationFileName : undefined,
			entity_encoding: "raw",
			formats: {
				note: {block: 'p', classes: 'note'}
			},
			style_formats: [
				// 	{
				// 	title: 'Frequently Used'
				// },
				{
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
					title: 'Paragraph',
					format: 'p'
				}, {
					title: 'Blockquote',
					format: 'blockquote'
				}, {
					title: 'Note',
					format: "note",
					remove: 'all'
				}, {
					title: 'Pre',
					format: 'pre',
				}, {
					title: 'Code',
					icon: 'sourcecode',
					format: 'code'
				},
				// 	{
				// 	title: 'All Styles'
				// },
				{
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
						block: 'p',
						wrapper: true,
						classes: ".alert",
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
						icon: 'strike-through',
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
						icon: 'sourcecode',
						format: 'code'
					}]
				}],
			init_instance_callback: editor => {
				if (this.destroying) { // already destroyed
					editor.destroy();
					return;
				}
				this.editor = editor;
				this.mceReadyExecutor.ready = true;
			},
			setup: (editor) => {
				if (this._config.imageUploadEnabled) {
					editor.ui.registry.addMenuItem('insertimagefromdisk', {
						icon: 'image',
						text: 'Insert image',
						onAction: () => this.$fileField.click()
					});
					editor.ui.registry.addButton('insertimagefromdisk', {
						icon: 'image',
						tooltip: 'Insert image',
						onAction: () => this.$fileField.click()
					});
				}
				editor.on('change undo redo keypress', (e) => {
					this.fireTextInputEvent();
				});
				editor.on('keydown', (e) => {
					if (e.keyCode === keyCodes.escape) {
						this.onSpecialKeyPressed.fire({
							key: UiSpecialKey.ESCAPE
						});
					} else if (e.keyCode === keyCodes.enter) {
						this.onSpecialKeyPressed.fire({
							key: UiSpecialKey.ENTER
						});
					}
				});
				editor.on('undo redo', (e) => {
					this.editor.uploadImages(() => undefined);
				});
			},
			images_upload_handler: (blobInfo: { base64: () => string, blob: () => Blob, blobUri: () => string, filename: () => string, id: () => string, name: () => string, uri: () => string }, success, failure) => {
				this.runningImageUploadsCount++;
				this.updateSpinnerVisibility();
				this.onImageUploadStarted.fire({
					fileName: blobInfo.filename(),
					mimeType: blobInfo.blob().type,
					sizeInBytes: blobInfo.blob().size,
					incompleteUploadsCount: this.runningImageUploadsCount
				});

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
							this.onImageUploadFailed.fire({
								name: blobInfo.filename(),
								mimeType: blobInfo.blob().type,
								sizeInBytes: blobInfo.blob().size,
								incompleteUploadsCount: this.runningImageUploadsCount
							});
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
							this.onImageUploadSuccessful.fire({
								fileUuid: fileUuid,
								name: blobInfo.filename(),
								mimeType: blobInfo.blob().type,
								sizeInBytes: blobInfo.blob().size,
								incompleteUploadsCount: this.runningImageUploadsCount
							});
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
		});

		this.getMainElement().addEventListener('focusin', (e) => {
			if (!this.getMainElement().contains(e.relatedTarget as Node)) { // make sure we REALLY lost the focus
				this._hasFocus = true;
				this.getMainElement().classList.add('focus');
				this.onFocusGained.fire(null);
				this.updateToolbarVisiblity();
			}
		});
		this.getMainElement().addEventListener('focusout', (e) => {
			// NOTE that it is important to do this on focusout instead of blur, since for some tinymce reason blur is called asynchronously only (setTimeout).
			// Being synchronous is important, in order to make sure that the changes are commited before processing a, say, button event that will save the form...
			if (!this.getMainElement().contains(e.relatedTarget as Node)) { // make sure we REALLY lost the focus
				this._hasFocus = false;
				this.getMainElement().classList.remove('focus');
				if (this.mayFireChangeEvents()) {
					this.commit();
				}
				this.onBlur.fire(null);
				this.updateToolbarVisiblity();
			}
		});
	}

	protected initFocusHandling() {
		// do nothing (see editor initialization)
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	@throttledMethod(5000)
	private fireTextInputEvent() {
		if (this.mayFireChangeEvents()) {
			this.onTextInput.fire({
				enteredString: this.getTransientValue()
			});
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

	protected displayCommittedValue(): void {
		const value = removeTags(this.getCommittedValue(), "style");
		this.mceReadyExecutor.invokeWhenReady(() => {
			this.editor.setContent(value);
			this.editor.undoManager.reset();
		});
	}

	focus(): void {
		this.editor && this.editor.focus(false);
	}

	destroy(): void {
		super.destroy();
		this.destroying = true;
		this.mceReadyExecutor.invokeOnceWhenReady(() => {
			this.editor.destroy();
		})
	}

	getTransientValue(): string {
		let content = this.editor != null ? this.editor.getContent() : this.getCommittedValue();
		return content.replace(/src="data:.*?"/g, "");
	}

	@executeWhenFirstDisplayed(true)
	onResize(): void {
		this.rerenderToolbar();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode: UiFieldEditingMode): void {
		this.mceReadyExecutor?.invokeOnceWhenReady(() => {
			// this MUST be done after initializing! Don't skip it when the editor is null,
			// since the editor might just be initializing and thereby setting the readonly value to an old value! (actually happened!)
			this.editor.setMode(this.isEditable() ? 'design' : 'readonly');
			this.updateToolbarVisiblity();
			this.rerenderToolbar();
		})
		let wasEditable = !(oldEditingMode === UiFieldEditingMode.DISABLED || oldEditingMode === UiFieldEditingMode.READONLY);
		UiField.defaultOnEditingModeChangedImpl(this, () => this.editor != null ? this.editor.getBody() : null);
	}

	private updateToolbarVisiblity() { // both visibility and content (+overflow) are updated on show()
		this.$toolbarContainer.classList.toggle("hidden", !this.toolbarShouldBeShown());
	}

	private rerenderToolbar() { // both visibility and content (+overflow) are updated on show()
		if (this.editor != null) {
			this.editor.ui.show(); // effectively rerenders the toolbar
		}
	}

	private toolbarShouldBeShown() {
		return this.isEditable() &&
			(this._config.toolbarVisibilityMode == UiToolbarVisibilityMode.VISIBLE || (this._config.toolbarVisibilityMode == UiToolbarVisibilityMode.VISIBLE_IF_FOCUSED && this.hasFocus()));
	}

	getDefaultValue(): string {
		return "";
	}

	setToolbarVisibilityMode(toolbarVisibilityMode: UiToolbarVisibilityMode): void {
		this._config.toolbarVisibilityMode = toolbarVisibilityMode;
		this.updateToolbarVisiblity();
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
