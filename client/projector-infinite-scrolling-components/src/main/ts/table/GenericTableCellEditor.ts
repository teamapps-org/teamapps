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
import {AbstractTableEditor} from "./AbstractTableEditor";
import {AbstractField, type DtoIdentifiableClientRecord} from "projector-client-object-api";


export class GenericTableCellEditor extends AbstractTableEditor {

	protected defaultValue: any;
	protected uiField: AbstractField;
	private destroyCallback: () => void;

	constructor(
		uiField: AbstractField,
		destroyCallback: () => void,
		args: Slick.Editors.EditorOptions<any> & { item: any }
	) {
		super(args);
		this.uiField = uiField;
		this.destroyCallback = destroyCallback;
		this.container.appendChild(uiField.getMainElement());
	}

	public destroy() {
		this.destroyCallback();
	};

	public focus() {
		this.uiField.focus();
	};

	public loadValue(item: DtoIdentifiableClientRecord) {
		this.defaultValue = item.values[this.column.field] || null;
		this.uiField.setCommittedValue(this.defaultValue);
		this.focus();
	};

	public serializeValue() {
		return this.uiField.getTransientValue();
	};

	public applyValue(item: DtoIdentifiableClientRecord, state: any /*TODO*/) {
		this.uiField.commit();
		item.values[this.column.field] = this.uiField.getCommittedValue();
	};

	public isValueChanged() {
		return this.uiField.valuesChanged(this.uiField.getTransientValue(), this.defaultValue);
	};

}
