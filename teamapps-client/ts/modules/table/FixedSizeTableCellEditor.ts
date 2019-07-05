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
import {AbstractTableEditor} from "./AbstractTableEditor";
import {UiField} from "../formfield/UiField";
import {UiIdentifiableClientRecordConfig} from "../../generated/UiIdentifiableClientRecordConfig";
import {css} from "../Common";


export class FixedSizeTableCellEditor extends AbstractTableEditor {

	protected defaultValue: any;

	constructor(
		protected uiField: UiField,
		private destroyCallback: () => void,
		editorOptions: Slick.Editors.EditorOptions<any> & { item: any }
	) {
		super(editorOptions);

		// let availableSpaceRight = editorOptions.gridPosition.right - editorOptions.position.left;
		// let availableSpaceBottom = editorOptions.gridPosition.bottom - editorOptions.position.top;
		//
		// const preferredWidth = 300;
		// const preferredHeight = 300;
		//
		// let editorWidth = Math.max(editorOptions.position.width, Math.min(preferredWidth, editorOptions.gridPosition.width));
		// editorOptions.container.style.width = editorWidth + "px";
		//
		// let editorHeight = Math.max(editorOptions.position.height, Math.min(preferredHeight, editorOptions.gridPosition.height));
		// editorOptions.container.style.height = editorHeight + "px";
		//
		// editorOptions.container.style.left = Math.min(editorOptions.position.left - editorOptions.gridPosition.left, editorOptions.gridPosition.right - editorOptions.gridPosition.left - preferredWidth) + "px";
		// editorOptions.container.style.top = Math.min(0, availableSpaceBottom - preferredHeight) + "px";


		let availableSpaceRight = editorOptions.gridPosition.right - editorOptions.position.left;
		let availableSpaceBottom = editorOptions.gridPosition.bottom - editorOptions.position.top;

		const preferredWidth = 300;
		const preferredHeight = 200;

		let $uiField = uiField.getMainDomElement();
		css($uiField, {
			width: Math.max(editorOptions.position.width, Math.min(preferredWidth, editorOptions.gridPosition.width)),
			height: Math.max(editorOptions.position.height, Math.min(preferredHeight, editorOptions.gridPosition.height)),
			left:Math.min(-1, availableSpaceRight - preferredWidth) + "px",
			top:Math.min(-1, availableSpaceBottom - preferredHeight) + "px",
			minWidth: "unset",
			minHeight: "unset",
			maxWidth: "unset",
			maxHeight: "unset"
		});
		this.container.appendChild($uiField);
	}

	public destroy() {
		this.destroyCallback();
	};

	public focus() {
		this.uiField.focus();
	};

	public loadValue(item: UiIdentifiableClientRecordConfig) {
		this.defaultValue = item.values[this.column.field] || null;
		this.uiField.setCommittedValue(this.defaultValue);
		this.focus();
	};

	public serializeValue() {
		return this.uiField.getTransientValue();
	};

	public applyValue(item: UiIdentifiableClientRecordConfig, state: any /*TODO*/) {
		this.uiField.commit();
		item.values[this.column.field] = state;
	};

	public isValueChanged() {
		return this.uiField.valuesChanged(this.uiField.getTransientValue(), this.defaultValue);
	};

}
