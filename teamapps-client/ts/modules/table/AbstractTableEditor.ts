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
export abstract class AbstractTableEditor implements Slick.Editors.Editor<any /*TODO*/> {

	protected column: Slick.Column<any>;
	protected container: HTMLElement;
	protected grid: Slick.Grid<any>;

	constructor(args: Slick.Editors.EditorOptions<any>) {
		this.column = args.column;
		this.container = args.container;
		this.grid = args.grid;
		this.init();
	}

	public init(): void {
		// actually not needed! (only implementing because the tsd says it...
	}

	// remove all data, events & dom elements created in the constructor
	abstract destroy(): void;

	// set the focus on the main input control (if any)
	abstract focus(): void;

	// return true if the value(s) being edited by the user has/have been changed
	abstract isValueChanged(): boolean;

	// return the value(s) being edited by the user in a serialized form
	// can be an arbitrary object
	// the only restriction is that it must be a simple object that can be passed around even
	// when the editor itself has been destroyed
	abstract serializeValue(): any /*TODO*/;

	// load the value(s) from the data item and update the UI
	// this method will be called immediately after the editor is initialized
	// it may also be called by the grid if if the row/cell being edited is updated via grid.updateRow/updateCell
	abstract loadValue(item: any /*TODO*/): void;

	// deserialize the value(s) saved to "state" and apply them to the data item
	// this method may get called after the editor itself has been destroyed
	// treat it as an equivalent of a Java/C# "static" method - no instance variables should be accessed
	abstract applyValue(item: any /*TODO*/, state: any /*TODO*/): void;

	// validate user input and return the result along with the validation message, if any
	// if the input is valid, return {valid:true,msg:null}
	public validate(): { valid: boolean, msg: string } {
		if ((this.column as any).validator) {
			const validationResults = (this.column as any).validator(this.serializeValue());
			if (!validationResults.valid) {
				return validationResults;
			}
		}
		return {
			valid: true,
			msg: null
		};
	};

	/*********** OPTIONAL METHODS***********/

	// if implemented, this will be called if the cell being edited is scrolled out of the view
	// implement this is your UI is not appended to the cell itself or if you open any secondary
	// selector controls (like a calendar for a datepicker input)
	public hide() {

	};

	// pretty much the opposite of hide
	public show() {

	};

	// if implemented, this will be called by the grid if any of the cell containers are scrolled
	// and the absolute position of the edited cell is changed
	// if your UI is constructed as a child of document BODY, implement this to update the
	// position of the elements as the position of the cell changes
	//
	// the cellBox: { top, left, bottom, right, width, height, visible }
	public position(cellBox: any /*TODO*/) {

	};
}

