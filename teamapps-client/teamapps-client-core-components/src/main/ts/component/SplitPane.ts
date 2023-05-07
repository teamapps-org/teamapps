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

import {AbstractComponent, bind, capitalizeFirstLetter, Component, parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {Emptyable, isEmptyable} from "../util/Emptyable";
import {
	DtoSplitDirection,
	DtoSplitPane,
	DtoSplitPane_SplitResizedEvent,
	DtoSplitPaneCommandHandler,
	DtoSplitPaneEventSource,
	DtoSplitSizePolicy
} from "../generated";
import {applyCss} from "../util/cssUtil";

export class SplitPane extends AbstractComponent<DtoSplitPane> implements Emptyable, DtoSplitPaneCommandHandler, DtoSplitPaneEventSource {
	public readonly onSplitResized: TeamAppsEvent<DtoSplitPane_SplitResizedEvent> = new TeamAppsEvent<DtoSplitPane_SplitResizedEvent>();

	private _firstChildComponent: Component;
	private _lastChildComponent: Component;
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
	public sizePolicy: DtoSplitSizePolicy;
	private firstChildMinSize: number;
	private lastChildMinSize: number;

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();

	constructor(config: DtoSplitPane) {
		super(config);
		this.referenceChildSize = config.referenceChildSize;
		this.sizePolicy = config.sizePolicy;
		const firstChildContainerId = config.id + '_firstChildContainer';
		const lastChildContainerId = config.id + '_lastChildContainer';
		this._$splitPane = parseHtml(`<div class="splitpane splitpane-${DtoSplitDirection[config.splitDirection].toLowerCase()} splitpane-${DtoSplitSizePolicy[this.sizePolicy].toLowerCase()}">
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

		this._sizeAttribute = config.splitDirection === DtoSplitDirection.HORIZONTAL ? 'height' : 'width';
		this._minSizeAttribute = "min" + capitalizeFirstLetter(this._sizeAttribute) as 'minWidth' | 'minHeight';
		this._maxSizeAttribute = "max" + capitalizeFirstLetter(this._sizeAttribute) as 'maxWidth' | 'maxHeight';
		this._offsetAttribute = config.splitDirection === DtoSplitDirection.HORIZONTAL ? 'top' : 'left';
		this._offsetSizeAttribute = config.splitDirection === DtoSplitDirection.HORIZONTAL ? 'offsetHeight' : 'offsetWidth';

		this.firstChildMinSize = config.firstChildMinSize;
		this.lastChildMinSize = config.lastChildMinSize;

		['mousedown', 'touchstart'].forEach((eventName) => this._$divider.addEventListener(eventName, (e: MouseEvent) => this.mousedownHandler(e)));

		this._updatePositions();
		this.setFirstChild(config.firstChild as Component);
		this.setLastChild(config.lastChild as Component);
		this._updateChildContainerClasses();

	}

	public get splitDirection(): DtoSplitDirection {
		return this.config.splitDirection
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
			if (this.sizePolicy === DtoSplitSizePolicy.RELATIVE) {
				referenceChildSize = this._$firstChildContainer[this._offsetSizeAttribute] / this._$splitPane[this._offsetSizeAttribute];
			} else if (this.sizePolicy === DtoSplitSizePolicy.FIRST_FIXED) {
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
			const diff = (this.config.splitDirection === DtoSplitDirection.HORIZONTAL) ? this.pageYof(event) - dragStartY : this.pageXof(event) - dragStartX;
			const newFirstChildSize = initialFirstContainerWidth + diff;

			if (this.sizePolicy === DtoSplitSizePolicy.RELATIVE) {
				this.referenceChildSize = newFirstChildSize / splitPaneSize;
				this._updatePositions();
			} else if (this.sizePolicy === DtoSplitSizePolicy.FIRST_FIXED) {
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

	public setFirstChild(firstChild: Component) {
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

	public setLastChild(lastChild: Component) {
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
		} else if (firstEmpty && this.config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.classList.add("empty-child");
			this._$lastChildContainerWrapper.classList.add("single-child");
			this._$lastChildContainerWrapper.classList.remove("empty-child");
		} else if (lastEmpty && this.config.fillIfSingleChild) {
			this._$firstChildContainerWrapper.classList.add("single-child");
			this._$firstChildContainerWrapper.classList.remove("empty-child");
			this._$lastChildContainerWrapper.classList.add("empty-child");
		} else {
			this._$firstChildContainerWrapper.classList.remove("single-child", "empty-child");
			this._$lastChildContainerWrapper.classList.remove("single-child", "empty-child");
		}
		this._$dividerWrapper.classList.toggle("hidden", firstEmpty || lastEmpty);
		this._$divider.classList.toggle("hidden", !this.config.resizable);
		this.onResize(); // yes!! Maybe this splitpane does not need it for itself, but we want the children to relayout if necessary!
	}

	private _updatePositions() {
		const referenceChildSize = this.referenceChildSize;

		if (this.sizePolicy === DtoSplitSizePolicy.RELATIVE) {
			applyCss(this._$firstChildContainerWrapper, {
				"flex-grow": "" + referenceChildSize,
				"flex-shrink": "" + referenceChildSize,
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			applyCss(this._$lastChildContainerWrapper, {
				"flex-grow": "" + (1 - referenceChildSize),
				"flex-shrink": "" + (1 - referenceChildSize),
				"flex-basis": "1px",
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === DtoSplitSizePolicy.FIRST_FIXED) {
			applyCss(this._$firstChildContainerWrapper, {
				"flex-grow": "0",
				"flex-shrink": "1",
				"flex-basis": referenceChildSize + 'px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			applyCss(this._$lastChildContainerWrapper, {
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		} else if (this.sizePolicy === DtoSplitSizePolicy.LAST_FIXED) {
			applyCss(this._$firstChildContainerWrapper, {
				"flex-grow": "1",
				"flex-shrink": "1",
				"flex-basis": '1px',
				[this._minSizeAttribute]: this.firstChildMinSize
			});
			applyCss(this._$lastChildContainerWrapper, {
				"flex-grow": "0",
				"flex-shrink": "1",
				"flex-basis": referenceChildSize + 'px',
				[this._minSizeAttribute]: this.lastChildMinSize
			});
		}
	}

	private isFirstEmpty() {
		return (!this._firstChildComponent || this.config.collapseEmptyChildren && isEmptyable(this._firstChildComponent) && this._firstChildComponent.empty);
	}

	private isLastEmtpy() {
		return (!this._lastChildComponent || this.config.collapseEmptyChildren && isEmptyable(this._lastChildComponent) && this._lastChildComponent.empty);
	}

	get firstChildComponent() {
		return this._firstChildComponent;
	}

	get lastChildComponent() {
		return this._lastChildComponent;
	}

	get id() {
		return this.config.id;
	}

	get empty() {
		return this.isFirstEmpty() && this.isLastEmtpy();
	}

	public setSize(referenceChildSize: number, sizePolicy: DtoSplitSizePolicy) {
		this.referenceChildSize = referenceChildSize;
		this.sizePolicy = sizePolicy;
		this._$divider.classList.add('splitpane-' + DtoSplitSizePolicy[this.sizePolicy].toLowerCase());
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


