import {TrivialComponent} from "../TrivialCore";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";

export interface DropDownComponent<E> extends TrivialComponent {

	readonly onValueChanged: TeamAppsEvent<{ value: E, finalSelection: boolean }>;

	setValue(value: E): void;

	getValue(): E;

	/**
	 * @param event
	 * @return true if the event was processed by this component and should not lead to any other action
	 */
	handleKeyboardInput(event: KeyboardEvent): boolean;

	/**
	 * @param query
	 * @param selectionDirection
	 * @return true if it got results
	 */
	handleQuery(query: string, selectionDirection: SelectionDirection): Promise<boolean>;

	getComponent(): TrivialComponent;

}

export enum SelectionDirection {
	SELECT_FIRST = 1,
	NO_SELECTION = 0,
	SELECT_LAST = -1
}