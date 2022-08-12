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

import {bind} from "./util/Bind";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiComponentConfig} from "./generated/UiComponentConfig";
import {Emptyable, isEmptyable} from "./util/Emptyable";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {capitalizeFirstLetter, css, parseHtml} from "./Common";
import {UiSplitPane_SplitResizedEvent, UiSplitPaneCommandHandler, UiSplitPaneConfig, UiSplitPaneEventSource} from "./generated/UiSplitPaneConfig";
import {UiSplitSizePolicy} from "./generated/UiSplitSizePolicy";
import {UiSplitDirection} from "./generated/UiSplitDirection";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";

export class UiSplitPane extends AbstractUiComponent<UiSplitPaneConfig> implements Emptyable, UiSplitPaneCommandHandler, UiSplitPaneEventSource {
	public readonly onSplitResized: TeamAppsEvent<UiSplitPane_SplitResizedEvent> = new TeamAppsEvent<UiSplitPane_SplitResizedEvent>();

	private _firstChildComponent: UiComponent<UiComponentConfig>;
	private _lastChildComponent: UiComponent<UiComponentConfig>;
	private _$splitPane: HTMLElement;
	private _$firstChildContainerWrapper: HTMLElement;
	private _$lastChildContainerWrapper: HTMLElement;
	private _$firstChildContainer: HTMLElement;
	private _$lastChildContainer: HTMLElement;
	private _$dividerWrapper: HTMLElement;
	private _$divider: HTMLElement;

	private _sizeAttribute: 'height' | 'width';
	private _minSizeAttribute: 'minWidth' | 'minHeight';
	private _maxSizeAttribute: 'maxWidth' | 'maxHeight';
	private _offsetAttribute: 'top' | 'left';
	private _offsetSizeAttribute: 'offsetHeight' | 'offsetWidth';

	public referenceChildSize: number;
	public sizePolicy: UiSplitSizePolicy;
	private firstChildMinSize: number;
	private lastChildMinSize: number;

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();

	constructor(config: UiSplitPaneConfig,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.referenceChildSize = config.referenceChildSize;
		this.sizePolicy = config.sizePolicy;
		const firstChildContainerId = config.id + '_firstChildContainer';
		const lastChildContainerId = config.id + '_lastChildContainer';
		this._$splitPane = parseHtml(`<div class="splitpane splitpane-${UiSplitDirection[config.splitDirection].toLowerCase()} splitpane-${UiSplitSizePolicy[this.sizePolicy].toLowerCase()}">
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
		const $componentWrappers = this._$splitPane.querySelectorAll<HTMLElement>(":scope .splitpane-component-wrapper");
		this._$firstChildContainerWrapper = $componentWrappers.item(0);
		this._$lastChildContainerWrapper = $componentWrappers.item(1);
		this._$firstChildContainer = this._$firstChildContainerWrapper.querySelector<HTMLElement>(":scope .splitpane-component");
		this._$lastChildContainer = this._$lastChildContainerWrapper.querySelector<HTMLElement>(":scope .splitpane-component");
		this._$dividerWrapper = this._$splitPane.querySelector<HTMLElement>(":scope .splitpane-divider-wrapper");
		this._$divider = this._$splitPane.querySelector<HTMLElement>(":scope .splitpane-divider");

		this._sizeAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'height' : 'width';
		this._minSizeAttribute = "min" + capitalizeFirstLetter(this._sizeAttribute) as 'minWidth' | 'minHeight';
		this._maxSizeAttribute = "max" + capitalizeFirstLetter(this._sizeAttribute) as 'maxWidth' | 'maxHeight';
		this._offsetAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'top' : 'left';
		this._offsetSizeAttribute = config.splitDirection === UiSplitDirection.HORIZONTAL ? 'offsetHeight' : 'offsetWidth';

		this.firstChildMinSize = config.firstChildMinSize;
		this.lastChildMinSize = config.lastChildMinSize;

		['mousedown', 'touchstart'].forEach((eventName) => this._$divider.addEventListener(eventName, (e: MouseEvent) => this.mousedownHandler(e)));

		this._updatePositions();
		this.setFirstChild(config.firstChild as UiComponent);
		this.setLastChild(config.lastChild as UiComponent);
		this._updateChildContainerClasses();

	}

	public get splitDirection(): UiSplitDirection {
		return this._config.splitDirection
	}

	public doGetMainElement(): HTMLElement {
		return this._$splitPane;
	}

	private mousedownHandler(event: MouseEvent) {
		event.preventDefault();
		const isTouchEvent = event.type.match(/^touch/),
			moveEvent = isTouchEvent ? 'touchmove' : 'mousemove',
			endEvent = isTouchEvent ? 'touchend' : 'mouseup';
		this._$divider.classList.add('dragged');
		if (isTouchEvent) {
			this._$divider.classList.add('touch');
		}
		let dragHandler = this.createDragHandler(this.pageXof(event), this.pageYof(event));
		let dropHandler = (event: Event) => {
			document.removeEventListener(moveEvent, dragHandler);
			document.removeEventListener(endEvent, dropHandler);
			this._$divider.classList.remove('dragged', 'touch');

			let referenceChildSize;
			if (this.sizePolicy === UiSplitSizePolicy.RELATIVE) {
				referenceChildSize = this._$firstChildContainer[this._offsetSizeAttribute] / this._$splitPane[this._offsetSizeAttribute];
			} else if (this.sizePolicy === UiSplitSizePolicy.FIRST_FIXED) {
				referenceChildSize = this._$firstChildContainer[this._offsetSizeAttribute];
			} else {
				referenceChildSize = this._$lastChildContainer[this._offsetSizeAttribute];
			}
			this.onSplitResized.fire({
				referenceChildSize: referenceChildSize
			});
			this.onResize();

			//blurredBackgroundImageContainers.classList.add('teamapps-blurredBackgroundImage');
		};
		document.addEventListener(moveEvent, dragHandler);
		document.addEventListener(endEvent, dropHandler);
	}

	private createDragHandler(dragStartX: number, dragStartY: number): (e: MouseEvent) => void {
		const initialFirstContainerWidth = this._$firstChildContainer[this._offsetSizeAttribute];
		const splitPaneSize = this._$splitPane[this._offsetSizeAttribute];
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
		return event.pageX ?? (event as any).touches[0].pageX;
	}

	private pageYof(event: MouseEvent) {
		return event.pageY ?? (event as any).touches[0].pageY;
	}

	public setFirstChild(firstChild: UiComponent<UiComponentConfig>) {
		this._$firstChildContainer.innerHTML = '';

		this._firstChildComponent = firstChild;
		if (firstChild) {
			this._$firstChildContainer.appendChild(firstChild.getMainElement());
			if (this._firstChildComponent && isEmptyable(this._firstChildComponent)) {
				this._firstChildComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged);
			}
		}
		this._updateChildContainerClasses();
		this._updatePositions();

		this.updateEmptyState();
	}

	public setLastChild(lastChild: UiComponent<UiComponentConfig>) {
		this._$lastChildContainer.innerHTML = '';

		this._lastChildComponent = lastChild;
		if (lastChild) {
			this._$lastChildContainer.appendChild(lastChild.getMainElement());
			if (this._lastChildComponent && isEmptyable(this._lastChildComponent)) {
				this._lastChildComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged);
			}
		}
		this._updateChildContainerClasses();
		this._updatePositions();

		this.updateEmptyState();
	}

	private _updateChildContainerClasses() {
		const firstEmpty = this.isFirstEmpty();
		const lastEmpty = this.isLastEmtpy();

		if (firstEmpty && lastEmpty) {
			this._$firstChildContainerWrapper.classList.add("empty-child");
			this._$firstChildContainerWrapper.classList.remove("single-child");
			this._$lastChildContainerWrapper.classList.add("empty-child");
			this._$lastChildContainerWrapper.classList.remove("single-child");
		} else if (firstEmpty && this._config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.classList.add("empty-child");
			this._$lastChildContainerWrapper.classList.add("single-child");
			this._$lastChildContainerWrapper.classList.remove("empty-child");
		} else if (lastEmpty && this._config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.classList.add("single-child");
			this._$firstChildContainerWrapper.classList.remove("empty-child");
			this._$lastChildContainerWrapper.classList.add("empty-child");
		} else {
			this._$firstChildContainerWrapper.classList.remove("single-child", "empty-child");
			this._$lastChildContainerWrapper.classList.remove("single-child", "empty-child");
		}
		this._$dividerWrapper.classList.toggle("hidden", firstEmpty || lastEmpty);
		this._$divider.classList.toggle("hidden", !this._config.resizable);
		this.onResize(); // yes!! Maybe this splitpane does not need it for itself, but we want the children to relayout if necessary!
	}

	private _updatePositions() {
		const referenceChildSize = this.referenceChildSize;

		if (this.sizePolicy === UiSplitSizePolicy.RELATIVE) {
			css(this._$firstChildContainerWrapper, {
				"flex-grow": "" + referenceChildSize,
				"flex-shrink": "" + referenceChildSize,
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			css(this._$lastChildContainerWrapper, {
				"flex-grow": "" + (1 - referenceChildSize),
				"flex-shrink": "" + (1 - referenceChildSize),
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === UiSplitSizePolicy.FIRST_FIXED) {
			css(this._$firstChildContainerWrapper, {
				"flex-grow": "0",
				"flex-shrink": "1",
				"flex-basis": referenceChildSize + 'px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			css(this._$lastChildContainerWrapper, {
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === UiSplitSizePolicy.LAST_FIXED) {
			css(this._$firstChildContainerWrapper, {
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			css(this._$lastChildContainerWrapper, {
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

	public setSize(referenceChildSize: number, sizePolicy: UiSplitSizePolicy) {
		this.referenceChildSize = referenceChildSize;
		this.sizePolicy = sizePolicy;
		this._$divider.classList.add('splitpane-' + UiSplitSizePolicy[this.sizePolicy].toLowerCase());
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

}

TeamAppsUiComponentRegistry.registerComponentClass("UiSplitPane", UiSplitPane);
