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

import {UiPanelHeaderFieldConfig} from "./generated/UiPanelHeaderFieldConfig";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiToolButton} from "./micro-components/UiToolButton";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {UiPanel_WindowButtonClickedEvent, UiPanelCommandHandler, UiPanelConfig, UiPanelEventSource,} from "./generated/UiPanelConfig";
import {createUiToolButtonConfig} from "./generated/UiToolButtonConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {StaticIcons} from "./util/StaticIcons";
import {UiWindowButtonType} from "./generated/UiWindowButtonType";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {insertBefore, maximizeComponent, outerWidthIncludingMargins, parseHtml, prependChild} from "./Common";
import {UiComponent} from "./UiComponent";
import {UiPanelHeaderComponentMinimizationPolicy} from "./generated/UiPanelHeaderComponentMinimizationPolicy";

interface HeaderField {
	config: UiPanelHeaderFieldConfig;
	field: UiComponent;
	$wrapper: HTMLElement;
	$iconAndFieldWrapper: HTMLElement;
	$fieldWrapper: HTMLElement;
	$icon: HTMLElement;
	minimizedWidth?: number;
	minExpandedWidthWithIcon?: number;
	minExpandedWidth?: number;
}

export class UiPanel extends AbstractUiComponent<UiPanelConfig> implements UiPanelCommandHandler, UiPanelEventSource {

	public readonly onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent> = new TeamAppsEvent();

	private readonly defaultToolButtons = {
		[UiWindowButtonType.MINIMIZE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.MINIMIZE, "Minimize", {debuggingId: "window-button-minimize"}), this._context),
		[UiWindowButtonType.MAXIMIZE_RESTORE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.MAXIMIZE, "Maximize/Restore", {debuggingId: "window-button-maximize"}), this._context),
		[UiWindowButtonType.CLOSE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.CLOSE, "Close", {debuggingId: "window-button-close"}), this._context),
	};
	private readonly orderedDefaultToolButtonTypes = [
		UiWindowButtonType.MINIMIZE,
		UiWindowButtonType.MAXIMIZE_RESTORE,
		UiWindowButtonType.CLOSE
	];

	private $panel: HTMLElement;
	private $heading: HTMLElement;
	private $toolbarContainer: HTMLElement;
	private panelBody: HTMLElement;
	private $leftComponentWrapper: HTMLElement;
	private $headingSpacer: HTMLElement;
	private $rightComponentWrapper: HTMLElement;
	private $buttonContainer: HTMLElement;
	private $windowButtonContainer: HTMLElement;
	private $icon: HTMLElement;
	private $title: HTMLElement;

	private leftHeaderField: HeaderField;
	private rightHeaderField: HeaderField;
	private leftComponentFirstMinimized: boolean;
	private alwaysShowHeaderFieldIcons: boolean;
	private toolbar: UiToolbar;
	private icon: string;
	private title: string;

	private titleNaturalWidth: number;
	private toolButtons: UiToolButton[] = [];
	private windowButtons: UiWindowButtonType[];
	private restoreFunction: (animationCallback?: () => void) => void;

	constructor(config: UiPanelConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = parseHtml(`<div class="UiPanel panel teamapps-blurredBackgroundImage">
                <div class="panel-heading">
                    <div class="panel-icon"></div>
                    <div class="panel-title"></div>
                    <div class="panel-component-wrapper panel-left-component-wrapper"></div>
                    <div class="panel-heading-spacer"></div>
                    <div class="panel-component-wrapper panel-right-component-wrapper"></div>
                    <div class="panel-heading-buttons"></div>
                    <div class="panel-heading-window-buttons hidden"></div>
                </div>
                <div class="toolbar-container"></div>
                <div class="panel-body" style="padding: ${config.padding}px"></div>
            </div>`);

		this.$toolbarContainer = this.$panel.querySelector(':scope .toolbar-container');
		this.panelBody = this.$panel.querySelector(':scope .panel-body');
		this.$heading = this.$panel.querySelector(':scope >.panel-heading');
		this.$icon = this.$heading.querySelector(':scope >.panel-icon');
		this.$title = this.$heading.querySelector(':scope >.panel-title');
		this.$leftComponentWrapper = this.$heading.querySelector(':scope >.panel-left-component-wrapper');
		this.$headingSpacer = this.$heading.querySelector(':scope >.panel-heading-spacer');
		this.$rightComponentWrapper = this.$heading.querySelector(':scope >.panel-right-component-wrapper');
		this.$buttonContainer = this.$heading.querySelector(':scope >.panel-heading-buttons');
		this.$windowButtonContainer = this.$heading.querySelector(':scope >.panel-heading-window-buttons');

		this.alwaysShowHeaderFieldIcons = config.alwaysShowHeaderFieldIcons;
		this.setIcon(config.icon);
		this.setTitle(config.title);
		this.setLeftHeaderField(config.leftHeaderField);
		this.setRightHeaderField(config.rightHeaderField);
		this.setToolButtons(config.toolButtons as UiToolButton[]);

		if (config.hideTitleBar) {
			this.$heading.classList.add('hidden');
			this.$panel.classList.add("empty-heading");
		} else {
			this.$heading.classList.remove('hidden');
			this.$panel.classList.remove("empty-heading");
		}

		this.setToolbar(config.toolbar as UiToolbar);
		if (config.content) {
			this.setContent(config.content as UiComponent);
		}
		this.leftComponentFirstMinimized = this.config.headerComponentMinimizationPolicy == UiPanelHeaderComponentMinimizationPolicy.LEFT_COMPONENT_FIRST;

		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].onClicked.addListener(() => {
			if (this.restoreFunction == null) {
				this.maximize();
			} else {
				this.restore();
			}
		});
		this.orderedDefaultToolButtonTypes.forEach(windowButtonType => {
			this.defaultToolButtons[windowButtonType].onClicked.addListener(() => {
				this.onWindowButtonClicked.fire({
					windowButton: windowButtonType
				});
			});
		});
		this.setWindowButtons(config.windowButtons);
		this.setStretchContent(config.stretchContent);
	}

	public setMaximized(maximized: boolean) {
		if (maximized) {
			this.maximize();
		} else {
			this.restore();
		}
	}

	public maximize(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.RESTORE);
		this.restoreFunction = maximizeComponent(this);
	}

	public restore(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.MAXIMIZE);
		if (this.restoreFunction != null) {
			this.restoreFunction();
		}
		this.restoreFunction = null;
	}

	public setDraggable(draggable: boolean) {
		this.$heading.draggable = draggable;
	}

	public setToolButtons(toolButtons: UiToolButton[]) {
		this.toolButtons = [];
		this.$buttonContainer.innerHTML = '';
		toolButtons && toolButtons.forEach(toolButton => {
			this.$buttonContainer.appendChild(toolButton.getMainElement());
			this.toolButtons.push(toolButton);
		});
		this.relayoutHeader();
	}

	public setWindowButtons(buttonTypes: UiWindowButtonType[]): void {
		this.windowButtons = [];
		this.$windowButtonContainer.innerHTML = '';
		if (buttonTypes && buttonTypes.length > 0) {
			buttonTypes.forEach(toolButton => {
				this.addWindowButton(toolButton);
			});
		} else {
			this.windowButtons.slice().forEach(button => this.removeWindowButton(button));
		}
	}

	public addWindowButton(toolButtonType: UiWindowButtonType) {
		if (this.windowButtons.filter(tb => tb === toolButtonType).length > 0){
			this.removeWindowButton(toolButtonType);
		}
		this.$windowButtonContainer.classList.remove("hidden");
		this.windowButtons.push(toolButtonType);
		const button = this.defaultToolButtons[toolButtonType];
		if (this.$windowButtonContainer.children.length === 0) {
			prependChild(this.$windowButtonContainer, button.getMainElement());
		} else {
			let index = this.windowButtons
				.sort((a, b) =>this.orderedDefaultToolButtonTypes.indexOf(a) - this.orderedDefaultToolButtonTypes.indexOf(b))
				.indexOf(toolButtonType);
			if (index >= this.$windowButtonContainer.childNodes.length) {
				this.$windowButtonContainer.appendChild(button.getMainElement());
			} else {
				insertBefore(button.getMainElement(), this.$windowButtonContainer.children[index]);
			}
		}
		this.relayoutHeader();
	}

	public removeWindowButton(uiToolButton: UiWindowButtonType) {
		this.defaultToolButtons[uiToolButton].getMainElement().remove();
		this.windowButtons = this.windowButtons.filter(tb => tb !== uiToolButton);
		if (this.windowButtons.length === 0) {
			this.$windowButtonContainer.classList.add("hidden");
		}
	}

	public getWindowButton(buttonType: UiWindowButtonType) {
		return this.defaultToolButtons[buttonType];
	}

	public doGetMainElement(): HTMLElement {
		return this.$panel;
	}

	public setContent(content: UiComponent) {
		if (content?.getMainElement() !== this.panelBody.firstElementChild) {
			this.panelBody.innerHTML = '';
		}
		if (content != null) {
			this.panelBody.appendChild(content.getMainElement());
		}
	}

	@executeWhenFirstDisplayed(true)
	private calculateFieldWrapperSizes() {
		this.headerFields.forEach(headerField => {
			if (!headerField.minimizedWidth || !headerField.minExpandedWidth || !headerField.minExpandedWidthWithIcon) {
				headerField.$fieldWrapper.style.transition = "none";
				this.setMinimizedFields(headerField);
				this.$heading.classList.add("has-minimized-header-component");
				headerField.minimizedWidth = outerWidthIncludingMargins(headerField.$iconAndFieldWrapper);
				this.setMinimizedFields();
				headerField.minExpandedWidthWithIcon = outerWidthIncludingMargins(headerField.$iconAndFieldWrapper) - headerField.$fieldWrapper.offsetWidth + headerField.config.minWidth;
				this.$heading.classList.remove("has-minimized-header-component");
				headerField.minExpandedWidth = outerWidthIncludingMargins(headerField.$iconAndFieldWrapper) - headerField.$fieldWrapper.offsetWidth + headerField.config.minWidth;
				headerField.$fieldWrapper.style.transition = "";
			}
		});
	}

	private get headerFields() {
		return [this.leftHeaderField, this.rightHeaderField].filter(f => f != null);
	}

	private setMinimizedFields(...minimizedHeaderFields: HeaderField[]) {
		this.headerFields.forEach(headerField => {
			headerField.$wrapper.classList.toggle("minimized", minimizedHeaderFields.indexOf(headerField) != -1);
			headerField.$wrapper.classList.toggle("display-icon", minimizedHeaderFields.length > 0 || this.alwaysShowHeaderFieldIcons);
		});
	}

	public setLeftHeaderField(headerFieldConfig: UiPanelHeaderFieldConfig) {
		this.leftHeaderField = this.setHeaderField(headerFieldConfig, this.$leftComponentWrapper, true);
		this.calculateFieldWrapperSizes();
		this.relayoutHeader();
	}

	public setRightHeaderField(headerFieldConfig: UiPanelHeaderFieldConfig) {
		this.rightHeaderField = this.setHeaderField(headerFieldConfig, this.$rightComponentWrapper, false);
		this.calculateFieldWrapperSizes();
		this.relayoutHeader();
	}

	private setHeaderField(headerFieldConfig: UiPanelHeaderFieldConfig, $componentWrapper: HTMLElement, isLeft: boolean): HeaderField {
		if (isLeft && this.leftHeaderField) {
			this.leftHeaderField.$iconAndFieldWrapper.remove();
		} else if (!isLeft && this.rightHeaderField) {
			this.rightHeaderField.$iconAndFieldWrapper.remove();
		}

		$componentWrapper.innerHTML = '';
		$componentWrapper.classList.add('hidden');
		if (headerFieldConfig) {
			let $iconAndFieldWrapper = parseHtml(`<div class="icon-and-field-wrapper">
                    <div class="icon img img-16" style="background-image: ${headerFieldConfig.icon ? `url('${headerFieldConfig.icon}')` : 'none'}"></div>
                    <div class="field-wrapper"></div>
                </div>`);
			let $icon = $iconAndFieldWrapper.querySelector<HTMLElement>(':scope >.icon');
			$icon.addEventListener('click', () => {
				this.leftComponentFirstMinimized = !isLeft;
				this.relayoutHeader();
			});
			let $fieldWrapper = $iconAndFieldWrapper.querySelector<HTMLElement>(':scope >.field-wrapper');
			const field = (headerFieldConfig.field as UiComponent);
			$fieldWrapper.appendChild(field.getMainElement());
			field.onVisibilityChanged.addListener(visible => {
				this.relayoutHeader();
			});
			let headerField: HeaderField = {
				config: headerFieldConfig,
				field,
				$wrapper: $componentWrapper,
				$iconAndFieldWrapper,
				$icon: $icon,
				$fieldWrapper
			};
			$componentWrapper.appendChild($iconAndFieldWrapper);
			$componentWrapper.classList.remove('hidden');
			return headerField;
		} else {
			return null;
		}
	};

	public setIcon(icon: string) {
		this.icon = icon;
		if (icon) {
			this.$icon.innerHTML = '';
			this.$icon.appendChild(parseHtml(`<div class="img img-16" style="background-image: url('${icon}')"></div>`));
		}
		this.$icon.classList.toggle('hidden', icon == null);
		this.relayoutHeader();
	}

	public setTitle(title: string) {
		this.title = title;
		this.$title.textContent = title;
		this.recalculateTitleNaturalWidth();
		this.$title.classList.toggle('hidden', !title);
		this.relayoutHeader();
	}

	private recalculateTitleNaturalWidth() {
		if (!this.title) {
			this.titleNaturalWidth = 0;
		} else {
			this.$title.style.position = "absolute";
			this.$title.style.display = "inline-block";
			this.titleNaturalWidth = this.$title.offsetWidth;
			this.$title.style.position = null;
			this.$title.style.display = null;
		}
	};

	public setToolbar(toolbar: UiToolbar) {
		if (this.toolbar != null) {
			this.toolbar.getMainElement().remove();
		}
		this.toolbar = toolbar;
		if (toolbar) {
			this.$toolbarContainer.appendChild(this.toolbar.getMainElement());
			this.toolbar.onEmptyStateChanged.addListener(() => this.updateToolbarVisibility())
		}
		this.updateToolbarVisibility();
	}

	public setStretchContent(stretch: boolean): void {
		this.getMainElement().classList.toggle("stretch-content", stretch);
	}

	private updateToolbarVisibility() {
		this.$toolbarContainer.classList.toggle('hidden', this.toolbar == null || this.toolbar.empty);
	}

	onResize(): void {
		this.relayoutHeader();
	}

	@executeWhenFirstDisplayed(true)
	private relayoutHeader() {
		const computedHeadingStyle = getComputedStyle(this.$heading);
		let availableHeaderContentWidth = this.$heading.offsetWidth - parseInt(computedHeadingStyle.paddingLeft) - parseInt(computedHeadingStyle.paddingRight);
		if (this.title && this.titleNaturalWidth == 0) this.recalculateTitleNaturalWidth();
		if (this.headerFields.some(headerField => !headerField.minimizedWidth)) this.calculateFieldWrapperSizes();

		let titleWidth = Math.floor(this.title ? this.titleNaturalWidth : 0);
		let iconWidth = (this.icon ? this.$icon.offsetWidth + parseInt(getComputedStyle(this.$icon).marginRight) : 0);
		let minSpacerWidth = parseInt(getComputedStyle(this.$headingSpacer).flexBasis);
		let buttonContainerWidth = this.$buttonContainer.offsetWidth;
		let windowButtonContainerWidth = this.$windowButtonContainer.offsetWidth;

		let minAllExpandedWidth =
			// 1 // the width of the title may be no integer
			-1
			+ iconWidth
			+ titleWidth
			+ minSpacerWidth
			+ buttonContainerWidth
			+ windowButtonContainerWidth
			+ this.headerFields
				.map(headerField => this.alwaysShowHeaderFieldIcons ? headerField.minExpandedWidthWithIcon : headerField.minExpandedWidth)
				.reduce((totalWidth, fieldWidth) => (totalWidth + fieldWidth), 0);

		if (this.numberOfVisibleHeaderFields() == 2) {
			let firstFieldToGetMinified = this.leftComponentFirstMinimized ? this.leftHeaderField : this.rightHeaderField;
			let alwaysMaximizedField = this.leftComponentFirstMinimized ? this.rightHeaderField : this.leftHeaderField;
			let minFirstMinimizedWidth =
				1 // the width of the title may be no integer
				+ iconWidth
				+ titleWidth
				+ minSpacerWidth
				+ buttonContainerWidth
				+ windowButtonContainerWidth
				+ firstFieldToGetMinified.minimizedWidth
				+ alwaysMaximizedField.minExpandedWidthWithIcon;
			let minWidthNeededWithHiddenHeaderAndOneMinimizedField = minFirstMinimizedWidth - titleWidth;

			if (availableHeaderContentWidth >= minAllExpandedWidth) {
				this.$title.classList.remove("hidden");
				this.$title.style.width = null;
				this.$heading.classList.remove("has-minimized-header-component");
				let availableAdditionalSpace = availableHeaderContentWidth - minAllExpandedWidth;

				let minMaxFieldWidthDeltaSum = this.headerFields
					.map(headerField => {
						return headerField.config.maxWidth - headerField.config.minWidth;
					})
					.reduce((totalWidth, fieldWidth) => (totalWidth + fieldWidth), 0);

				this.headerFields.forEach(headerField => {
					this.setMinimizedFields();
					let newFieldWidth = Math.min(
						headerField.config.minWidth + availableAdditionalSpace * ((headerField.config.maxWidth - headerField.config.minWidth) / minMaxFieldWidthDeltaSum),
						headerField.config.maxWidth
					);
					headerField.$fieldWrapper.style.width = newFieldWidth + "px";
				});
			} else if (availableHeaderContentWidth >= minFirstMinimizedWidth) {
				this.$title.classList.remove("hidden");
				this.$title.style.width = null;
				this.$heading.classList.add("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				let availableAdditionalSpace = availableHeaderContentWidth - minFirstMinimizedWidth;
				let newMaximizedFieldWidth = Math.min(alwaysMaximizedField.config.minWidth + availableAdditionalSpace, alwaysMaximizedField.config.maxWidth);
				alwaysMaximizedField.$fieldWrapper.style.width = newMaximizedFieldWidth + "px";
			} else if (availableHeaderContentWidth >= minWidthNeededWithHiddenHeaderAndOneMinimizedField + 30 /* less does not make sense for title */) {
				this.$title.classList.remove("hidden");
				this.$heading.classList.add("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				alwaysMaximizedField.$fieldWrapper.style.width = alwaysMaximizedField.config.minWidth + "px";
				this.$title.style.width = (this.titleNaturalWidth - (minFirstMinimizedWidth - availableHeaderContentWidth)) + "px";
			} else {
				this.$title.classList.add("hidden");
				this.$heading.classList.add("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				const width = alwaysMaximizedField.config.minWidth + (availableHeaderContentWidth - minWidthNeededWithHiddenHeaderAndOneMinimizedField);
				alwaysMaximizedField.$fieldWrapper.style.width = width + "px";
			}
		} else if (this.numberOfVisibleHeaderFields() == 1) {
			this.$heading.classList.remove("has-minimized-header-component");
			let headerField = this.leftHeaderField || this.rightHeaderField;
			this.setMinimizedFields();

			if (availableHeaderContentWidth >= minAllExpandedWidth) {
				this.$title.classList.remove("hidden");
				this.$title.style.width = null;
				let availableAdditionalSpace = availableHeaderContentWidth - minAllExpandedWidth;
				this.headerFields.forEach(headerField => {
					let newFieldWidth = Math.min(
						headerField.config.minWidth + availableAdditionalSpace,
						headerField.config.maxWidth
					);
					headerField.$fieldWrapper.style.width = newFieldWidth + "px";
				});
			} else if (availableHeaderContentWidth >= minAllExpandedWidth - this.titleNaturalWidth + 30 /* less does not make sense for title */) {
				this.$title.classList.remove("hidden");
				headerField.$fieldWrapper.style.width = headerField.config.minWidth + "px";
				this.$title.style.width = (this.titleNaturalWidth - (minAllExpandedWidth - availableHeaderContentWidth)) + "px";
			} else {
				this.$title.classList.add("hidden");
				let widthLessThanNeeded = availableHeaderContentWidth - minAllExpandedWidth + this.titleNaturalWidth;
				headerField.$fieldWrapper.style.width = (headerField.config.minWidth + widthLessThanNeeded) + "px";
			}
		} else {
			this.$heading.classList.remove("has-minimized-header-component");
			this.$title.classList.remove("hidden");
			let availableAdditionalSpace = availableHeaderContentWidth - minAllExpandedWidth;
			this.$title.style.width = (this.titleNaturalWidth + availableAdditionalSpace) + "px";
		}
	};

	private numberOfVisibleHeaderFields() {
		return this.headerFields.filter(headerField => headerField.field.isVisible()).length;
	}

	public destroy(): void {
		super.destroy();
		this.$panel.remove(); // may be currently attached to document.body (maximized)
	}

	public static isDraggablePanelHeadingElement(target: HTMLElement): boolean {
		let heading = target.closest('.panel-heading');
		if (heading == null) {
			return false;
		}
		return target.classList.contains('panel-heading')
			|| target.classList.contains('panel-icon')
			|| target.classList.contains('panel-title')
			|| target.classList.contains('panel-heading-spacer');
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiPanel", UiPanel);
