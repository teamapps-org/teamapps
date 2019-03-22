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
import * as $ from "jquery";
import {UiPanelHeaderFieldConfig} from "../generated/UiPanelHeaderFieldConfig";
import {UiField} from "./formfield/UiField";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiToolButton} from "./micro-components/UiToolButton";
import {UiComponent} from "./UiComponent";
import {UiDropDown} from "./micro-components/UiDropDown";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenAttached} from "./util/ExecuteWhenAttached";
import {UiPanel_HeaderComponentMinimizationPolicy, UiPanel_WindowButtonClickedEvent, UiPanelCommandHandler, UiPanelConfig, UiPanelEventSource,} from "../generated/UiPanelConfig";
import {createUiToolButtonConfig} from "../generated/UiToolButtonConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {StaticIcons} from "./util/StaticIcons";
import {UiWindowButtonType} from "../generated/UiWindowButtonType";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {EventFactory} from "../generated/EventFactory";
import {maximizeComponent} from "./Common";

interface HeaderField {
	config: UiPanelHeaderFieldConfig;
	field: UiField;
	$wrapper: JQuery;
	$iconAndFieldWrapper: JQuery;
	$fieldWrapper: JQuery;
	$icon: JQuery;
	minimizedWidth?: number;
	minExpandedWidthWithIcon?: number;
	minExpandedWidth?: number;
}

export class UiPanel extends UiComponent<UiPanelConfig> implements UiPanelCommandHandler, UiPanelEventSource {

	public readonly onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent> = new TeamAppsEvent(this);

	private readonly defaultToolButtons = {
		[UiWindowButtonType.MINIMIZE]: new UiToolButton(createUiToolButtonConfig("MINIMIZE", StaticIcons.MINIMIZE, "Minimize"), this._context),
		[UiWindowButtonType.MAXIMIZE_RESTORE]: new UiToolButton(createUiToolButtonConfig("MAXIMIZE_RESTORE", StaticIcons.MAXIMIZE, "Maximize/Restore"), this._context),
		[UiWindowButtonType.CLOSE]: new UiToolButton(createUiToolButtonConfig("CLOSE", StaticIcons.CLOSE, "Close"), this._context),
	};
	private readonly orderedDefaultToolButtonTypes = [
		UiWindowButtonType.MINIMIZE,
		UiWindowButtonType.MAXIMIZE_RESTORE,
		UiWindowButtonType.CLOSE
	];

	private $panel: JQuery;
	private $heading: JQuery;
	private $parentDomElement: JQuery; // When maximizing, this component is taken out of the DOM. When minimizing, it has to be reattached to this parent element.
	private $toolbarContainer: JQuery;
	private $bodyContainer: JQuery;
	private $leftComponentWrapper: JQuery;
	private $headingSpacer: JQuery;
	private $rightComponentWrapper: JQuery;
	private $buttonContainer: JQuery;
	private $windowButtonContainer: JQuery;
	private $icon: JQuery;
	private $title: JQuery;

	private contentComponent: UiComponent<UiComponentConfig>;
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
	private dropDown: UiDropDown;
	private restoreFunction: (animationCallback: () => void) => void;

	constructor(config: UiPanelConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = $(`<div id="${config.id}" class="UiPanel panel teamapps-blurredBackgroundImage">
                <div class="panel-heading">
                    <div class="panel-icon"/>
                    <div class="panel-title"/>
                    <div class="panel-component-wrapper panel-left-component-wrapper"/>
                    <div class="panel-heading-spacer"/>
                    <div class="panel-component-wrapper panel-right-component-wrapper"/>
                    <div class="panel-heading-buttons"/>
                    <div class="panel-heading-window-buttons hidden"/>
                </div>
                <div class="toolbar-container"></div>
                <div class="panel-body">
                  <div class="body-container scroll-container" style="padding: ${config.padding}px"></div>
                </div>
            </div>`);

		this.$toolbarContainer = this.$panel.find('.toolbar-container');
		this.$bodyContainer = this.$panel.find('.body-container');
		this.$heading = this.$panel.find('>.panel-heading');
		this.$icon = this.$heading.find('>.panel-icon');
		this.$title = this.$heading.find('>.panel-title');
		this.$leftComponentWrapper = this.$heading.find('>.panel-left-component-wrapper');
		this.$headingSpacer = this.$heading.find('>.panel-heading-spacer');
		this.$rightComponentWrapper = this.$heading.find('>.panel-right-component-wrapper');
		this.$buttonContainer = this.$heading.find('>.panel-heading-buttons');
		this.$windowButtonContainer = this.$heading.find('>.panel-heading-window-buttons');

		this.alwaysShowHeaderFieldIcons = config.alwaysShowHeaderFieldIcons;
		this.setIcon(config.icon);
		this.setTitle(config.title);
		this.setLeftHeaderField(config.leftHeaderField);
		this.setRightHeaderField(config.rightHeaderField);
		this.setToolButtons(config.toolButtons);

		if (config.hideTitleBar) {
			this.$heading.addClass('hidden');
			this.$panel.addClass("empty-heading");
		} else {
			this.$heading.removeClass('hidden');
			this.$panel.removeClass("empty-heading");
		}

		this.setToolbar(config.toolbar);
		if (config.content) {
			this.setContent(config.content);
		}
		this.leftComponentFirstMinimized = this._config.headerComponentMinimizationPolicy == UiPanel_HeaderComponentMinimizationPolicy.LEFT_COMPONENT_FIRST;

		this.dropDown = new UiDropDown();

		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].onClicked.addListener(() => {
			if (this.restoreFunction == null) {
				this.maximize();
			} else {
				this.restore();
			}
		});
		this.orderedDefaultToolButtonTypes.forEach(windowButtonType => {
			this.defaultToolButtons[windowButtonType].onClicked.addListener(() => {
				this.onWindowButtonClicked.fire(EventFactory.createUiPanel_WindowButtonClickedEvent(this.getId(), windowButtonType));
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
		this.restoreFunction = maximizeComponent(this, () => this.reLayout(true));
	}

	public restore(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.MAXIMIZE);
		if (this.restoreFunction != null) {
			this.restoreFunction(() => this.reLayout(true));
		}
		this.restoreFunction = null;
	}

	public setDraggable(draggable: boolean) {
		this.$heading.prop("draggable", draggable);
	}

	public setToolButtons(toolButtons: UiToolButton[]) {
		this.toolButtons = [];
		this.$buttonContainer[0].innerHTML = '';
		toolButtons && toolButtons.forEach(toolButton => {
			toolButton.getMainDomElement().appendTo(this.$buttonContainer);
			toolButton.attachedToDom = this.attachedToDom;
			this.toolButtons.push(toolButton);
		});
		this.relayoutHeader();
	}

	public setWindowButtons(buttonTypes: UiWindowButtonType[]): void {
		this.windowButtons = [];
		this.$windowButtonContainer[0].innerHTML = '';
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
		this.$windowButtonContainer.removeClass("hidden");
		this.windowButtons.push(toolButtonType);
		const button = this.defaultToolButtons[toolButtonType];
		if (this.$windowButtonContainer[0].children.length === 0) {
			button.getMainDomElement().prependTo(this.$windowButtonContainer);
		} else {
			let index = this.windowButtons
				.sort((a, b) =>this.orderedDefaultToolButtonTypes.indexOf(a) - this.orderedDefaultToolButtonTypes.indexOf(b))
				.indexOf(toolButtonType);
			if (index >= this.$windowButtonContainer[0].childNodes.length) {
				button.getMainDomElement().appendTo(this.$windowButtonContainer);
			} else {
				button.getMainDomElement().insertBefore(this.$windowButtonContainer[0].children[index]);
			}
		}
		this.relayoutHeader();
	}

	public removeWindowButton(uiToolButton: UiWindowButtonType) {
		this.defaultToolButtons[uiToolButton].getMainDomElement().detach();
		this.windowButtons = this.windowButtons.filter(tb => tb !== uiToolButton);
		if (this.windowButtons.length === 0) {
			this.$windowButtonContainer.addClass("hidden");
		}
	}

	public getWindowButton(buttonType: UiWindowButtonType) {
		return this.defaultToolButtons[buttonType];
	}

	public getMainDomElement(): JQuery {
		return this.$panel;
	}

	public setContent(content: UiComponent) {
		if (content == this.contentComponent) {
			return;
		}
		this.$bodyContainer[0].innerHTML = '';
		this.contentComponent = content;
		if (content != null) {
			this.contentComponent.getMainDomElement().appendTo(this.$bodyContainer);
			this.contentComponent.attachedToDom = this.attachedToDom;
		}
	}

	protected onAttachedToDom() {
		this.toolButtons.forEach(toolButton => toolButton.attachedToDom = true);
		if (this.toolbar) this.toolbar.attachedToDom = true;
		if (this.contentComponent) this.contentComponent.attachedToDom = true;
		this.reLayout();
	}

	@executeWhenAttached(true)
	private calculateFieldWrapperSizes() {
		this.headerFields.forEach(headerField => {
			if (!headerField.minimizedWidth || !headerField.minExpandedWidth || !headerField.minExpandedWidthWithIcon) {
				headerField.$fieldWrapper.css("transition", "none");
				this.setMinimizedFields(headerField);
				this.$heading.addClass("has-minimized-header-component");
				headerField.minimizedWidth = headerField.$iconAndFieldWrapper.outerWidth(true);
				this.setMinimizedFields();
				headerField.minExpandedWidthWithIcon = headerField.$iconAndFieldWrapper.outerWidth(true) - headerField.$fieldWrapper[0].offsetWidth + headerField.config.minWidth;
				this.$heading.removeClass("has-minimized-header-component");
				headerField.minExpandedWidth = headerField.$iconAndFieldWrapper.outerWidth(true) - headerField.$fieldWrapper[0].offsetWidth + headerField.config.minWidth;
				headerField.$fieldWrapper.css("transition", "");
			}
		});
	}

	private get headerFields() {
		return [this.leftHeaderField, this.rightHeaderField].filter(f => f != null);
	}

	private setMinimizedFields(...minimizedHeaderFields: HeaderField[]) {
		this.headerFields.forEach(headerField => {
			headerField.$wrapper.toggleClass("minimized", minimizedHeaderFields.indexOf(headerField) != -1);
			headerField.$wrapper.toggleClass("display-icon", minimizedHeaderFields.length > 0 || this.alwaysShowHeaderFieldIcons);
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

	private setHeaderField(headerFieldConfig: UiPanelHeaderFieldConfig, $componentWrapper: JQuery, isLeft: boolean): HeaderField {
		if (isLeft && this.leftHeaderField) {
			this.leftHeaderField.$iconAndFieldWrapper.detach();
		} else if (!isLeft && this.rightHeaderField) {
			this.rightHeaderField.$iconAndFieldWrapper.detach();
		}

		$componentWrapper[0].innerHTML = '';
		$componentWrapper.hide();
		if (headerFieldConfig) {
			let iconPath = this._context.getIconPath(headerFieldConfig.icon, 16);
			let $iconAndFieldWrapper = $(`<div class="icon-and-field-wrapper">
                    <div class="icon img img-16" style="background-image: ${iconPath ? 'url(' + iconPath + ')' : 'none'}"></div>
                    <div class="field-wrapper"></div>
                </div>`);
			let $icon = $iconAndFieldWrapper.find('>.icon');
			$icon.click(() => {
				this.leftComponentFirstMinimized = !isLeft;
				this.relayoutHeader();
			});
			let $fieldWrapper = $iconAndFieldWrapper.find('>.field-wrapper');
			const field = (headerFieldConfig.field as UiField);
			field.getMainDomElement().appendTo($fieldWrapper);
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
			$componentWrapper.append($iconAndFieldWrapper).show();
			return headerField;
		} else {
			return null;
		}
	};

	public setFieldValue(fieldName: string, value: any): void {
		const field = this.getHeaderFieldByName(fieldName);
		if (field) {
			field.field.setCommittedValue(value);
		}
	}

	private getHeaderFieldByName(fieldName: string): HeaderField {
		return this.headerFields
			.filter(field => field != null && field.config.field.fieldName === fieldName)[0];
	}

	public setIcon(icon: string) {
		this.icon = icon;
		if (icon) {
			this.$icon[0].innerHTML = '';
			this.$icon.append('<div class="img img-16" style="background-image: url(' + this._context.getIconPath(icon, 16) + ')"/>');
		}
		this.$icon.toggle(icon != null);
		this.relayoutHeader();
	}

	public setTitle(title: string) {
		this.title = title;
		this.$title.text(title);
		this.recalculateTitleNaturalWidth();
		this.$title.toggle(!!title);
		this.relayoutHeader();
	}

	private recalculateTitleNaturalWidth() {
		if (!this.title) {
			this.titleNaturalWidth = 0;
		} else {
			this.$title.css({
				position: "absolute",
				display: "inline-block"
			}); // TODO
			this.titleNaturalWidth = this.$title[0].offsetWidth;
			this.$title.css({
				position: "",
				display: ""
			});
		}
	};

	public setToolbar(toolbar: UiToolbar) {
		if (this.toolbar != null) {
			this.toolbar.getMainDomElement().detach();
		}
		this.toolbar = toolbar;
		if (toolbar) {
			this.toolbar.getMainDomElement().appendTo(this.$toolbarContainer);
			this.toolbar.onEmptyStateChanged.addListener(() => this.updateToolbarVisibility())
		}
		this.updateToolbarVisibility();
	}

	public setStretchContent(stretch: boolean): void {
		this.getMainDomElement().get(0).classList.toggle("stretch-content", stretch);
	}

	private updateToolbarVisibility() {
		this.$toolbarContainer.toggleClass('hidden', this.toolbar == null || this.toolbar.empty);
	}

	public focusField(fieldName: string) {
		this.getHeaderFieldByName(fieldName).field.focus();
	}

	onResize(): void {
		if (!this.attachedToDom || this.getMainDomElement()[0].offsetWidth <= 0) return;
		this.relayoutHeader();
		this.toolbar && this.toolbar.reLayout();
		this.contentComponent && this.contentComponent.reLayout();
		this.leftHeaderField && this.leftHeaderField.field.reLayout();
		this.rightHeaderField && this.rightHeaderField.field.reLayout();
	}

	@executeWhenAttached(true)
	private relayoutHeader() {
		let availableHeaderContentWidth = this.$heading[0].offsetWidth - parseInt(this.$heading.css("padding-left")) - parseInt(this.$heading.css("padding-right"));
		if (this.title && this.titleNaturalWidth == 0) this.recalculateTitleNaturalWidth();
		if (this.headerFields.some(headerField => !headerField.minimizedWidth)) this.calculateFieldWrapperSizes();

		let titleWidth = Math.floor(this.title ? this.titleNaturalWidth : 0);
		let iconWidth = (this.icon ? this.$icon[0].offsetWidth + parseInt(this.$icon.css("margin-right")) : 0);
		let minSpacerWidth = parseInt(this.$headingSpacer.css("flex-basis"));
		let buttonContainerWidth = this.$buttonContainer[0].offsetWidth;
		let windowButtonContainerWidth = this.$windowButtonContainer[0].offsetWidth;

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
				this.$title.removeClass("hidden").css("width", "");
				this.$heading.removeClass("has-minimized-header-component");
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
					headerField.$fieldWrapper.css({
						width: newFieldWidth,
					});
				});
			} else if (availableHeaderContentWidth >= minFirstMinimizedWidth) {
				this.$title.removeClass("hidden").css("width", "");
				this.$heading.addClass("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				let availableAdditionalSpace = availableHeaderContentWidth - minFirstMinimizedWidth;
				let newMaximizedFieldWidth = Math.min(alwaysMaximizedField.config.minWidth + availableAdditionalSpace, alwaysMaximizedField.config.maxWidth);
				alwaysMaximizedField.$fieldWrapper.css({
					width: newMaximizedFieldWidth,
				});
			} else if (availableHeaderContentWidth >= minWidthNeededWithHiddenHeaderAndOneMinimizedField + 30 /* less does not make sense for title */) {
				this.$title.removeClass("hidden");
				this.$heading.addClass("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				alwaysMaximizedField.$fieldWrapper.css({
					width: alwaysMaximizedField.config.minWidth,
				});
				this.$title.outerWidth(this.titleNaturalWidth - (minFirstMinimizedWidth - availableHeaderContentWidth));
			} else {
				this.$title.addClass("hidden");
				this.$heading.addClass("has-minimized-header-component");
				this.setMinimizedFields(firstFieldToGetMinified);
				const width = alwaysMaximizedField.config.minWidth + (availableHeaderContentWidth - minWidthNeededWithHiddenHeaderAndOneMinimizedField);
				console.log(alwaysMaximizedField.config.minWidth, (availableHeaderContentWidth - minWidthNeededWithHiddenHeaderAndOneMinimizedField), width);
				alwaysMaximizedField.$fieldWrapper.css({
					width: width
				});
			}
		} else if (this.numberOfVisibleHeaderFields() == 1) {
			this.$heading.removeClass("has-minimized-header-component");
			let headerField = this.leftHeaderField || this.rightHeaderField;
			this.setMinimizedFields();

			if (availableHeaderContentWidth >= minAllExpandedWidth) {
				this.$title.removeClass("hidden").css("width", "");
				let availableAdditionalSpace = availableHeaderContentWidth - minAllExpandedWidth;
				this.headerFields.forEach(headerField => {
					let newFieldWidth = Math.min(
						headerField.config.minWidth + availableAdditionalSpace,
						headerField.config.maxWidth
					);
					headerField.$fieldWrapper.css({
						width: newFieldWidth,
					});
				});
			} else if (availableHeaderContentWidth >= minAllExpandedWidth - this.titleNaturalWidth + 30 /* less does not make sense for title */) {
				this.$title.removeClass("hidden");
				headerField.$fieldWrapper.css({
					width: headerField.config.minWidth,
				});
				this.$title.outerWidth(this.titleNaturalWidth - (minAllExpandedWidth - availableHeaderContentWidth));
			} else {
				this.$title.addClass("hidden");
				let widthLessThanNeeded = availableHeaderContentWidth - minAllExpandedWidth + this.titleNaturalWidth;
				headerField.$fieldWrapper.css({
					width: headerField.config.minWidth + widthLessThanNeeded,
				});
			}
		} else {
			this.$heading.removeClass("has-minimized-header-component");
			this.$title.removeClass("hidden");
			let availableAdditionalSpace = availableHeaderContentWidth - minAllExpandedWidth;
			this.$title.outerWidth(this.titleNaturalWidth + availableAdditionalSpace);
		}
	};

	private numberOfVisibleHeaderFields() {
		return this.headerFields.filter(headerField => headerField.field.isVisible()).length;
	}

	public destroy(): void {
		this.$panel.detach(); // may be currently attached to document.body (maximized)
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
