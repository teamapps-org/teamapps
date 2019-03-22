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
import {bind} from "./util/Bind";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {Emptyable, isEmptyable} from "./util/Emptyable";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {capitalizeFirstLetter} from "./Common";
import {UiSplitPane_SplitResizedEvent, UiSplitPaneCommandHandler, UiSplitPaneConfig, UiSplitPaneEventSource} from "../generated/UiSplitPaneConfig";
import {UiSplitSizePolicy} from "../generated/UiSplitSizePolicy";
import {UiSplitDirection} from "../generated/UiSplitDirection";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

export class UiSplitPane extends UiComponent<UiSplitPaneConfig> implements Emptyable, UiSplitPaneCommandHandler, UiSplitPaneEventSource {
	public readonly onSplitResized: TeamAppsEvent<UiSplitPane_SplitResizedEvent> = new TeamAppsEvent<UiSplitPane_SplitResizedEvent>(this);

	private _firstChildComponent: UiComponent<UiComponentConfig>;
	private _lastChildComponent: UiComponent<UiComponentConfig>;
	private _$splitPane: JQuery;
	private _$firstChildContainerWrapper: JQuery;
	private _$lastChildContainerWrapper: JQuery;
	private _$firstChildContainer: JQuery;
	private _$lastChildContainer: JQuery;
	private _$dividerWrapper: JQuery;
	private _$divider: JQuery;

	private _sizeAttribute: 'height' | 'width';
	private _minSizeAttribute: 'minWidth' | 'minHeight';
	private _maxSizeAttribute: 'maxWidth' | 'maxHeight';
	private _offsetAttribute: 'top' | 'left';
	private _offsetSizeAttribute: 'offsetHeight' | 'offsetWidth';

	public referenceChildSize: number;
	public sizePolicy: UiSplitSizePolicy;
	private firstChildMinSize: number;
	private lastChildMinSize: number;

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);

	constructor(config: UiSplitPaneConfig,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.referenceChildSize = config.referenceChildSize;
		this.sizePolicy = config.sizePolicy;
		const firstChildContainerId = config.id + '_firstChildContainer';
		const lastChildContainerId = config.id + '_lastChildContainer';
		this._$splitPane = $(`<div class="splitpane splitpane-${UiSplitDirection[config.splitDirection].toLowerCase()} splitpane-${UiSplitSizePolicy[this.sizePolicy].toLowerCase()}" data-teamapps-id="${config.id}">
	<div class="splitpane-component-wrapper">
		<div id="${firstChildContainerId}" class="splitpane-component"></div>
	</div>
	<div class="splitpane-divider-wrapper">
		<div class="splitpane-divider ${config.resizable ? "" : "hidden"}" id="divider"></div>
	</div>
	<div class="splitpane-component-wrapper">
		<div id="${lastChildContainerId}" class="splitpane-component"></div>
	</div>
</div>`);
		const $componentWrappers = this._$splitPane.find(".splitpane-component-wrapper");
		this._$firstChildContainerWrapper = $componentWrappers.first();
		this._$lastChildContainerWrapper = $componentWrappers.last();
		this._$firstChildContainer = this._$firstChildContainerWrapper.find(".splitpane-component");
		this._$lastChildContainer = this._$lastChildContainerWrapper.find(".splitpane-component");
		this._$dividerWrapper = this._$splitPane.find(".splitpane-divider-wrapper");
		this._$divider = this._$splitPane.find(".splitpane-divider");

		this._sizeAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'height' : 'width';
		this._minSizeAttribute = "min" + capitalizeFirstLetter(this._sizeAttribute) as 'minWidth' | 'minHeight';
		this._maxSizeAttribute = "max" + capitalizeFirstLetter(this._sizeAttribute) as 'maxWidth' | 'maxHeight';
		this._offsetAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'top' : 'left';
		this._offsetSizeAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'offsetHeight' : 'offsetWidth';

		this.firstChildMinSize = config.firstChildMinSize;
		this.lastChildMinSize = config.lastChildMinSize;

		this._$divider.bind('mousedown touchstart', this.mousedownHandler.bind(this));

		this._updatePositions();
		this.setFirstChild(config.firstChild);
		this.setLastChild(config.lastChild);
		this._updateChildContainerClasses();

	}

	public get splitDirection(): UiSplitDirection {
		return this._config.splitDirection
	}

	public getMainDomElement(): JQuery {
		return this._$splitPane;
	}

	protected onAttachedToDom() {
		if (this.firstChildComponent) this.firstChildComponent.attachedToDom = true;
		if (this.lastChildComponent) this.lastChildComponent.attachedToDom = true;
	}

	private mousedownHandler(event: MouseEvent) {
		//let blurredBackgroundImageContainers = $('.teamapps-blurredBackgroundImage').removeClass('teamapps-blurredBackgroundImage');
		event.preventDefault();
		const isTouchEvent = event.type.match(/^touch/),
			moveEvent = isTouchEvent ? 'touchmove' : 'mousemove',
			endEvent = isTouchEvent ? 'touchend' : 'mouseup';
		this._$divider.addClass('dragged');
		if (isTouchEvent) {
			this._$divider.addClass('touch');
		}
		$(document).on(moveEvent, this.createDragHandler(this.pageXof(event), this.pageYof(event)));
		$(document).on(endEvent, (event) => {
			$(document).unbind(moveEvent);
			$(document).unbind(endEvent);
			this._$divider.removeClass('dragged touch');

			let referenceChildSize;
			if (this.sizePolicy === UiSplitSizePolicy.RELATIVE) {
				referenceChildSize = this._$firstChildContainer.get(0)[this._offsetSizeAttribute] / this._$splitPane.get(0)[this._offsetSizeAttribute];
			} else if (this.sizePolicy === UiSplitSizePolicy.FIRST_FIXED) {
				referenceChildSize = this._$firstChildContainer.get(0)[this._offsetSizeAttribute];
			} else {
				referenceChildSize = this._$lastChildContainer.get(0)[this._offsetSizeAttribute];
			}
			this.onSplitResized.fire(EventFactory.createUiSplitPane_SplitResizedEvent(this._config.id, referenceChildSize));
			this.onResize();

			//blurredBackgroundImageContainers.addClass('teamapps-blurredBackgroundImage');
		});
	}

	private createDragHandler(dragStartX: number, dragStartY: number): JQuery.EventHandlerBase<MouseEvent, any> {
		const initialFirstContainerWidth = this._$firstChildContainer[0][this._offsetSizeAttribute];
		const splitPaneSize = this._$splitPane.get(0)[this._offsetSizeAttribute];
		return (event: MouseEvent) => {
			const diff = (this._config.splitDirection === UiSplitDirection.HORIZONTAL) ? this.pageYof(event) - dragStartY : this.pageXof(event) - dragStartX;
			const newFirstChildSize = initialFirstContainerWidth + diff;

			if (this.sizePolicy === UiSplitSizePolicy.RELATIVE) {
				this.referenceChildSize = newFirstChildSize / splitPaneSize;
				this._updatePositions();
			} else if (this.sizePolicy === UiSplitSizePolicy.FIRST_FIXED) {
				this.referenceChildSize = newFirstChildSize;
				this._updatePositions();
			} else {
				this.referenceChildSize = splitPaneSize - newFirstChildSize;
				this._updatePositions();
			}

		}
	}

	private pageXof(event: MouseEvent) {
		return event.pageX || (event as any).originalEvent.pageX || (event as any).touches[0].pageX;
	}

	private pageYof(event: MouseEvent) {
		return event.pageY || (event as any).originalEvent.pageY || (event as any).touches[0].pageY;
	}

	public setFirstChild(firstChild: UiComponent<UiComponentConfig>) {
		this._$firstChildContainer[0].innerHTML = '';

		this._firstChildComponent = firstChild;
		if (firstChild) {
			firstChild.getMainDomElement().appendTo(this._$firstChildContainer);
			firstChild.attachedToDom = this.attachedToDom;
			if (this._firstChildComponent && isEmptyable(this._firstChildComponent)) {
				this._firstChildComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged);
			}
		}
		this._updateChildContainerClasses();
		this._updatePositions();

		this.firstChildComponent && (this.firstChildComponent.attachedToDom = this.attachedToDom);
		this.lastChildComponent && this.lastChildComponent.reLayout();

		this.updateEmptyState();
	}

	public setLastChild(lastChild: UiComponent<UiComponentConfig>) {
		this._$lastChildContainer[0].innerHTML = '';

		this._lastChildComponent = lastChild;
		if (lastChild) {
			lastChild.getMainDomElement().appendTo(this._$lastChildContainer);
			lastChild.attachedToDom = this.attachedToDom;
			if (this._lastChildComponent && isEmptyable(this._lastChildComponent)) {
				this._lastChildComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged);
			}
		}
		this._updateChildContainerClasses();
		this._updatePositions();

		this.lastChildComponent && (this.lastChildComponent.attachedToDom = this.attachedToDom);
		this.lastChildComponent && this.lastChildComponent.reLayout();

		this.updateEmptyState();
	}

	private _updateChildContainerClasses() {
		const firstEmpty = this.isFirstEmpty();
		const lastEmpty = this.isLastEmtpy();

		if (firstEmpty && lastEmpty) {
			this._$firstChildContainerWrapper.addClass("empty-child").removeClass("single-child");
			this._$lastChildContainerWrapper.addClass("empty-child").removeClass("single-child");
		} else if (firstEmpty && this._config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.addClass("empty-child");
			this._$lastChildContainerWrapper.addClass("single-child").removeClass("empty-child");
		} else if (lastEmpty && this._config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.addClass("single-child").removeClass("empty-child");
			this._$lastChildContainerWrapper.addClass("empty-child");
		} else {
			this._$firstChildContainerWrapper.removeClass("single-child empty-child");
			this._$lastChildContainerWrapper.removeClass("single-child empty-child");
		}
		this._$dividerWrapper.toggleClass("hidden", firstEmpty || lastEmpty);
		this._$divider.toggleClass("hidden", !this._config.resizable);
		this.onResize(); // yes!! Maybe this splitpane does not need it for itself, but we want the children to relayout if necessary!
	}

	private _updatePositions() {
		const referenceChildSize = this.referenceChildSize;

		if (this.sizePolicy === UiSplitSizePolicy.RELATIVE) {
			this._$firstChildContainerWrapper.css({
				"flex-grow": "" + referenceChildSize,
				"flex-shrink": "" + referenceChildSize,
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			this._$lastChildContainerWrapper.css({
				"flex-grow": "" + (1 - referenceChildSize),
				"flex-shrink": "" + (1 - referenceChildSize),
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === UiSplitSizePolicy.FIRST_FIXED) {
			this._$firstChildContainerWrapper.css({
				"flex-grow": "0",
				"flex-shrink": "1",
				"flex-basis": referenceChildSize + 'px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			this._$lastChildContainerWrapper.css({
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === UiSplitSizePolicy.LAST_FIXED) {
			this._$firstChildContainerWrapper.css({
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			this._$lastChildContainerWrapper.css({
				"flex-grow": "0",
				"flex-shrink": "1",
				"flex-basis": referenceChildSize + 'px',
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		}
	}

	private isFirstEmpty() {
		return (!this._firstChildComponent || this._config.collapseEmptyChildren && isEmptyable(this._firstChildComponent) && this._firstChildComponent.empty);
	}

	private isLastEmtpy() {
		return (!this._lastChildComponent || this._config.collapseEmptyChildren && isEmptyable(this._lastChildComponent) && this._lastChildComponent.empty);
	}

	get firstChildComponent() {
		return this._firstChildComponent;
	}

	get lastChildComponent() {
		return this._lastChildComponent;
	}

	get id() {
		return this._config.id;
	}

	get empty() {
		return this.isFirstEmpty() && this.isLastEmtpy();
	}

	public onResize(): void {
		this._firstChildComponent && this._firstChildComponent.reLayout();
		this._lastChildComponent && this._lastChildComponent.reLayout();
	}

	public setSize(referenceChildSize: number, sizePolicy: UiSplitSizePolicy) {
		this.referenceChildSize = referenceChildSize;
		this.sizePolicy = sizePolicy;
		this._$divider.addClass('splitpane-' + UiSplitSizePolicy[this.sizePolicy].toLowerCase());
		this._updatePositions();
	}

	setFirstChildMinSize(firstChildMinSize: number): void {
		this.firstChildMinSize = firstChildMinSize;
		this._updatePositions();
	}

	setLastChildMinSize(lastChildMinSize: number): void {
		this.lastChildMinSize = lastChildMinSize;
		this._updatePositions();
	}

	@bind
	private onChildEmptyStateChanged() {
		this._updateChildContainerClasses();
		this.updateEmptyState();
	}

	private updateEmptyState() {
		this.onEmptyStateChanged.fireIfChanged(this.empty);
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiSplitPane", UiSplitPane);
