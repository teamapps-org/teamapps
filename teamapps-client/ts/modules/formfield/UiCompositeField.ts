/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiField, ValueChangeEventData} from "./UiField";
import * as log from "loglevel";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiCompositeSubFieldConfig} from "../../generated/UiCompositeSubFieldConfig";
import {UiCompositeFieldConfig} from "../../generated/UiCompositeFieldConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiColumnDefinitionConfig} from "../../generated/UiColumnDefinitionConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TableDataProviderItem} from "../table/TableDataProvider";
import Logger = log.Logger;
import {closestAncestor, parseHtml} from "../Common";

export type SubField = {
	config: UiCompositeSubFieldConfig,
	field: UiField,
	$cell: HTMLElement,
	visible?: boolean
};

export /* for testing */ type ColumnWidthConstraints = {
	width: number;
	minWidth: number;
};

export class UiCompositeField extends UiField<UiCompositeFieldConfig, any> {

	private static logger: Logger = log.getLogger("UiCompositeField");

	public readonly onSubFieldValueChanged: TeamAppsEvent<ValueChangeEventData & { fieldName: string, originalEmitter: UiField }> =
		new TeamAppsEvent<ValueChangeEventData & { fieldName: string, originalEmitter: UiField }>(this);

	private subFields: SubField[];
	private $wrapper: HTMLElement;

	protected initialize(config: UiCompositeFieldConfig, context: TeamAppsUiContext) {
		this.logger.debug('initializing');
		this.subFields = [];
		let {$wrapper, subFieldSkeletons} = UiCompositeField.createDomStructure(config);
		subFieldSkeletons.forEach(subFieldSkeleton => {
			const field = subFieldSkeleton.config.field as UiField;
			subFieldSkeleton.$cell.appendChild(field.getMainElement());
			field.onValueChanged.addListener((eventObject: ValueChangeEventData ) => {
				this.onSubFieldValueChanged.fire({
					fieldName: "TODO!",
					originalEmitter: field,
					...eventObject
				});
			});
			this.subFields.push({
				...subFieldSkeleton,
				field
			});
		});
		UiCompositeField.validateNumberOfRowHeights(config);

		$wrapper.addEventListener("keydown", (e: KeyboardEvent) => {
			if (e.key == "Tab") {
				let nextFocusableField = this.getNextFocusableField(e.shiftKey ? -1 : 1);
				if (nextFocusableField != null) {
					nextFocusableField.focus();
					this.logger.trace("navigated to " + nextFocusableField.getMainInnerDomElement());
					return false;
				} else {
					this.logger.trace("not navigated");
				}
			}
		});

		this.$wrapper = $wrapper;
		this.onResize();
	}

	isValidData(v: any): boolean {
		return v == null || typeof v === "object";
	}

	private static validateNumberOfRowHeights(config: UiCompositeFieldConfig) {
		if (config.rowHeights.length < Math.max.apply(Math, config.subFields.map(f => f.row + f.rowSpan))) {
			UiCompositeField.logger.error(`The rowHeights configuration for this UiCompositeField (${config.id}) does not contain enough entries!`);
		}
	}

	private getNextFocusableField(navDirection: -1 | 1): UiField {
		let sortedFocusableSubFields = this.getSubFieldsSortedByTabOrder();
		let activeField = sortedFocusableSubFields.filter(f => f.field.hasFocus())[0];

		if (activeField == null) {
			return sortedFocusableSubFields[(sortedFocusableSubFields.length + navDirection) % sortedFocusableSubFields.length].field;
		}

		let currentIndex = sortedFocusableSubFields.indexOf(activeField);
		let newIndex = currentIndex + navDirection;
		if (newIndex >= 0 && newIndex < sortedFocusableSubFields.length) {
			this.logger.trace(`currentIndex: ${currentIndex}; current: ${this.subFieldToString(sortedFocusableSubFields[currentIndex])}; newIndex: ${newIndex}; new: ${this.subFieldToString(sortedFocusableSubFields[newIndex])}`);
			return sortedFocusableSubFields[newIndex].field;
		} else {
			this.logger.trace(`currentIndex: ${currentIndex}; current: ${this.subFieldToString(sortedFocusableSubFields[currentIndex])}; navDirection: ${navDirection}; --> jumping out of composite field`);
			return null;
		}
	}

	public commit(forceEvenIfNotChanged?: boolean): boolean {
		let focusedField = this.subFields.filter(f => f.field.hasFocus())[0];
		return !!focusedField && focusedField.field.commit();
	}

	private getSubFieldsSortedByTabOrder() {
		let allSubFields = this.getAllSubFields();
		let allFocusableSubFields = allSubFields
			.filter(f => f.field.getEditingMode() !== UiFieldEditingMode.READONLY
				&& f.field.getEditingMode() !== UiFieldEditingMode.DISABLED
				&& f.field.getFocusableElement() != null
				&& f.visible === true);
		return allFocusableSubFields
			.sort((sf1: SubField, sf2: SubField) => {
				this.logger.trace(`compare ${this.subFieldToString(sf1)} with ${this.subFieldToString(sf2)}`);
				if (sf1.config.tabIndex !== sf2.config.tabIndex) {
					return sf1.config.tabIndex - sf2.config.tabIndex
				}
				if (sf1.config.row !== sf2.config.row) {
					return sf1.config.row - sf2.config.row;
				}
				if (sf1.config.col !== sf2.config.col) {
					return sf1.config.col - sf2.config.col;
				}
				return 0;
			});
	}

	private subFieldToString(sf: SubField): string {
		return `[${sf.config.tabIndex}-${sf.config.row}-${sf.config.col}]`;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}

	public getFocusableElement(): HTMLElement {
		return this.$wrapper; // TODO!!
	}

	protected displayCommittedValue(): void {
		let committedValue: any = this.getCommittedValue();
		this.getAllSubFields().forEach(subField => {
			if (committedValue == null) {
				subField.field.setCommittedValue(null);
			} else {
				let fieldName = "TODO!"; //(subField.config.field as UiField).fieldName;
				subField.field.setCommittedValue(committedValue[fieldName]);
			}
		});
		UiCompositeField.updateDeclaredSubfieldVisibilities(this.subFields, committedValue);
		UiCompositeField.updateSubFieldVisibilities(this.subFields);
	}

	private static updateSubFieldVisibilities(subFields: SubField[]) {
		let visibleFieldsByCell: { [coordinates: string]: SubField } = {};
		subFields.filter(subField => subField.visible)
			.forEach(subField => {
				let coordinates = subField.config.row + ',' + subField.config.col;
				if (visibleFieldsByCell[coordinates] != null) {
					let fieldName = "TODO!"; // visibleFieldsByCell[coordinates].config.field.fieldName;
					let otherFieldName = "TODO!"; //subField.config.field.fieldName;
					UiCompositeField.logger.warn(`Two or more sub-fields are visible in the same cell in UiCompositeField! Showing the last one only. Conflicting fieldNames: ${fieldName}, ${otherFieldName}`);
				}
				visibleFieldsByCell[coordinates] = subField;
			});
		let visibleSubFields = Object.keys(visibleFieldsByCell).map(key => visibleFieldsByCell[key]);
		subFields.forEach(subField => subField.$cell.classList.toggle("hidden", visibleSubFields.indexOf(subField) === -1));
	}

	private static updateDeclaredSubfieldVisibilities(subFieldSkeletons: SubField[], value: any) {
		subFieldSkeletons.forEach(subFieldSkeleton => {
			if (subFieldSkeleton.config.visibilityPropertyName != null) {
				let visibilityValue = value[subFieldSkeleton.config.visibilityPropertyName];
				subFieldSkeleton.visible = !!visibilityValue;
			} else {
				subFieldSkeleton.visible = true;
			}
		});
	}

	public getTransientValue(): any {
		return this.getCommittedValue(); // we might want to overwrite the values contained in this composite field...
	}

	focus(): void {
		if (window.event instanceof MouseEvent) {
			let $cell = closestAncestor(window.event.target as HTMLElement, ".subfield-wrapper");
			if ($cell != null) {
				let subField = this.getSubFieldByFieldName($cell.getAttribute("data-fieldname"));
				if (subField) {
					subField.field.focus();
					return;
				}
			}
		}
		if (window.event instanceof KeyboardEvent && window.event.shiftKey) {
			let subFieldsByTabOrder = this.getSubFieldsSortedByTabOrder();
			subFieldsByTabOrder.length > 0 && subFieldsByTabOrder[subFieldsByTabOrder.length - 1].field.focus();
		} else {
			let subFieldsByTabOrder = this.getSubFieldsSortedByTabOrder();
			subFieldsByTabOrder.length > 0 && subFieldsByTabOrder[0].field.focus();
		}
	}

	private getSubFieldByFieldName(fieldName: string): SubField {
		console.error("TODO");
		return null;
		// return this.subFields.filter(sf => sf.config.field.fieldName === fieldName)[0];
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		// don't do anything! sub-fields have their own editing mode property!
	}

	private getAllUiFields(): UiField[] {
		return this.getAllSubFields().map(sf => sf.field);
	}

	private getAllSubFields(): SubField[] {
		return this.subFields;
	}

	public getReadOnlyHtml(value: any, availableWidth: number): string {
		UiCompositeField.validateNumberOfRowHeights(this._config);
		let {$wrapper, subFieldSkeletons} = UiCompositeField.createDomStructure(this._config);
		UiCompositeField.applyLayout(subFieldSkeletons, this._config.columnDefinitions, this._config.rowHeights, this._config.horizontalCellSpacing, this._config.verticalCellSpacing, availableWidth - 2 * this._config.padding);
		UiCompositeField.updateDeclaredSubfieldVisibilities(subFieldSkeletons, value);
		UiCompositeField.updateSubFieldVisibilities(subFieldSkeletons);

		subFieldSkeletons.forEach(subfield => {
			if (subfield.field.getReadOnlyHtml) {
				return (row: number, cell: number, value: any, columnDef: Slick.Column<TableDataProviderItem>, dataContext: TableDataProviderItem) => {
					return subfield.field.getReadOnlyHtml(dataContext.values[subfield.config.propertyName], columnDef.width);
				};
			}
		});
		$wrapper.classList.add("static-readonly-UiCompositeField");
		return $wrapper.outerHTML;
	}

	private static createDomStructure(config: UiCompositeFieldConfig) {
		const $wrapper = parseHtml(`<div class="UiCompositeField" style="padding: ${config.padding}px"><div class="padding-wrapper"></div></div>`);
		const $paddingWrapper = $wrapper.querySelector<HTMLElement>(":scope .padding-wrapper");
		$paddingWrapper.style.height = config.rowHeights.reduce((sum, height) => sum + height, 0) + "px";
		let subFieldSkeletons: SubField[] = [];
		config.subFields.forEach(subFieldConfig => {
			const uiField: UiField = subFieldConfig.field as UiField;
			let $cell = parseHtml(`<div class="subfield-wrapper" data-field-propertyname="${subFieldConfig.propertyName}" data-row="${subFieldConfig.row}" data-col="${subFieldConfig.col}" data-rowspan="${subFieldConfig.rowSpan}" data-colspan="${subFieldConfig.colSpan}"></div>`);
			$paddingWrapper.appendChild($cell);
			if (config.drawFieldBorders) {
				$cell.classList.add("bordered");
			}
			subFieldSkeletons.push({
				config: subFieldConfig,
				field: uiField,
				$cell: $cell
			});
		});
		return {$wrapper, subFieldSkeletons};
	}

	public onResize(): void {
		UiCompositeField.applyLayout(this.subFields, this._config.columnDefinitions, this._config.rowHeights, this._config.horizontalCellSpacing, this._config.verticalCellSpacing, this.getMainInnerDomElement().offsetWidth - this._config.padding);
	}

	private static applyLayout(subFieldSkeletons: SubField[], columnDefinitions: UiColumnDefinitionConfig[], rowHeights: number[], horizontalCellSpacing: number, verticalCellSpacing: number, availableWidth: number) {
		const colWidths = UiCompositeField.calculateColumnWidths(columnDefinitions, availableWidth);

		const rowTopEdges = [0];
		for (let i = 1; i < rowHeights.length; i++) {
			rowTopEdges[i] = rowTopEdges[i - 1] + rowHeights[i - 1];
		}
		const colLeftEdges = [0];
		for (let i = 1; i < colWidths.length; i++) {
			colLeftEdges[i] = colLeftEdges[i - 1] + colWidths[i - 1];
		}

		const cellTopEdges: number[] = [];
		const cellBottomEdges: number[] = [];
		for (let i = 0; i < rowHeights.length; i++) {
			cellTopEdges[i] = i == 0 ? rowTopEdges[i] : rowTopEdges[i] + verticalCellSpacing / 2;
			cellBottomEdges[i] = rowTopEdges[i] + rowHeights[i] - (i == rowHeights.length - 1 ? 0 : verticalCellSpacing / 2);
		}

		const cellLeftEdges: number[] = [];
		const cellRightEdges: number[] = [];
		for (let i = 0; i < colWidths.length; i++) {
			cellLeftEdges[i] = i == 0 ? colLeftEdges[i] : colLeftEdges[i] + horizontalCellSpacing / 2;
			cellRightEdges[i] = colLeftEdges[i] + colWidths[i] - (i == colWidths.length - 1 ? 0 : horizontalCellSpacing / 2);
		}
		UiCompositeField.logger.trace(`colWidths: ${colWidths} = ${colWidths.reduce((sum, w) => sum + w, 0)}; availableWidth: ${availableWidth}; colLeftEdges: ${colLeftEdges}; cellLeftEdges: ${cellLeftEdges}; cellRightEdges: ${cellRightEdges};`);
		UiCompositeField.logger.trace(`rowHeights: ${rowHeights}; sumOfRowHeights: ${rowHeights.reduce((sum, w) => sum + w, 0)}; rowTopEdges: ${rowTopEdges}; cellTopEdges: ${cellTopEdges}; cellBottomEdges: ${cellBottomEdges};`);

		subFieldSkeletons.forEach(subFieldSkeleton => {
			let endCol = subFieldSkeleton.config.col + subFieldSkeleton.config.colSpan - 1;
			let endRow = subFieldSkeleton.config.row + subFieldSkeleton.config.rowSpan - 1;
			Object.assign(subFieldSkeleton.$cell.style, {
				left: cellLeftEdges[subFieldSkeleton.config.col],
				top: cellTopEdges[subFieldSkeleton.config.row],
				width: cellRightEdges[endCol] - cellLeftEdges[subFieldSkeleton.config.col],
				height: cellBottomEdges[endRow] - cellTopEdges[subFieldSkeleton.config.row]
			});
		});
	}

	public static calculateColumnWidths(columnsWidthConstraints: ColumnWidthConstraints[], availableWidth: number): number[] {
		columnsWidthConstraints.forEach(c => c.minWidth = c.width >= 1 ? Math.max(c.minWidth, c.width) : c.minWidth); // normalize (set to effective minWidth)
		let minWidthsSum = columnsWidthConstraints.reduce((sum: number, column) => sum + column.minWidth, 0);
		let calculatedWidths: number[] = [];
		if (availableWidth < minWidthsSum) {
			// just distribute the available width by minWidths relatively
			columnsWidthConstraints.forEach((col, i) => calculatedWidths[i] = (availableWidth * col.minWidth / minWidthsSum));
		} else {
			let availableWidthMinusFixedWidths = availableWidth;

			// fixed widths
			columnsWidthConstraints.forEach((col, i) => {
				if (col.width >= 1) {
					calculatedWidths[i] = col.width;
					availableWidthMinusFixedWidths -= col.width;
				}
			});

			// relative widths
			let relativeWidthColumns = columnsWidthConstraints.filter(c => c.width < 1);
			let relativeWidthsSum = relativeWidthColumns.reduce((sum, col) => sum + col.width, 0);
			relativeWidthColumns.sort((c1, c2) => c1.minWidth / c1.width - c2.minWidth / c2.width);
			relativeWidthColumns.forEach((col) => {
				let columnIndex = columnsWidthConstraints.indexOf(col);
				if (availableWidthMinusFixedWidths <= col.minWidth * relativeWidthsSum / col.width) {
					calculatedWidths[columnIndex] = col.minWidth;
					availableWidthMinusFixedWidths -= col.minWidth; // !!!
					relativeWidthsSum -= col.width; // !!!
				} else {
					calculatedWidths[columnIndex] = availableWidthMinusFixedWidths * (col.width / relativeWidthsSum);
				}
			});
		}
		return calculatedWidths;
	}

	/*
	 Question: Can UiComposititeFields have something like a "commit"?
	 */

	public setFieldValue(fieldName: string, value: any) {
		this.logger.debug("setFieldValue: " + fieldName + " = " + JSON.stringify(value));
		let subField = this.getSubFieldByFieldName(fieldName);
		if (subField != null) {
			subField.field.setCommittedValue(value);
		}
		if (typeof value === "boolean") {
			let affectedSubFields = this.subFields
				.filter(subField => subField.config.visibilityPropertyName === fieldName);
			this.logger.debug("This value change affects the visibilities of the following fields: " + affectedSubFields.map(f => "TODO!" /*f.config.field.fieldName*/).join(', '));
			affectedSubFields
				.forEach(subField => subField.visible = value);
			UiCompositeField.updateSubFieldVisibilities(this.subFields);
		}
	}

	public focusField(fieldName: string) {
		let subField = this.getSubFieldByFieldName(fieldName);
		if (subField != null) {
			subField.field.focus();
		}
	}

	getSubFields(): SubField[] {
		return this.subFields;
	}

	getDefaultValue() {
		return {};
	}

	public valuesChanged(v1: any, v2: any): boolean {
		return false;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiCompositeField", UiCompositeField);
