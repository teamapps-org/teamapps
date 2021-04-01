import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {TrivialCalendarBox} from "../TrivialCalendarBox";
import {NavigationDirection} from "../TrivialCore";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {DropDownComponent, SelectionDirection} from "./DropDownComponent";

export class CalendarBoxDropdown implements DropDownComponent<LocalDateTime> {

	public readonly onValueChanged: TeamAppsEvent<{ value: LocalDateTime; finalSelection: boolean }> = new TeamAppsEvent(this);

	private calendarBox: TrivialCalendarBox;

	constructor(calendarBox: TrivialCalendarBox) {
		this.calendarBox = calendarBox;
		this.calendarBox.onChange.addListener(event => {
			this.onValueChanged.fire({value: event.value, finalSelection: event.timeUnitEdited == "day"});
		});
	}

	getMainDomElement(): HTMLElement {
		return this.calendarBox.getMainDomElement();
	}

	setValue(value: LocalDateTime): void {
		this.calendarBox.setSelectedDate(value ?? LocalDateTime.local());
	}

	getValue(): LocalDateTime {
		return this.calendarBox.getSelectedDate();
	}

	handleKeyboardInput(event: KeyboardEvent): boolean {
		if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].indexOf(event.code) !== -1) {
			this.calendarBox.navigateTimeUnit("day", event.code.substr(5).toLowerCase() as NavigationDirection);
			this.onValueChanged.fire({value: this.calendarBox.getSelectedDate(), finalSelection: false});
			return true;
		}
	}

	query(query: string, selectionDirection: SelectionDirection): Promise<boolean> {
		return Promise.resolve(false);
	}

	destroy(): void {
		this.calendarBox.destroy();
	}

	getComponent(): TrivialCalendarBox {
		return this.calendarBox;
	}

}