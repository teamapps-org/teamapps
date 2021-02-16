import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {TrivialCalendarBox} from "../TrivialCalendarBox";
import {NavigationDirection} from "../TrivialCore";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {DropDownComponent, SelectionDirection} from "./DropDownComponent";

export class CalendarBoxDropdown implements DropDownComponent<LocalDateTime> {

	public readonly onValueChanged: TeamAppsEvent<{ value: LocalDateTime; finalSelection: boolean }>;

	private calendarBox: TrivialCalendarBox;

	constructor(calendarBox: TrivialCalendarBox) {
		this.calendarBox = calendarBox;
		this.calendarBox.onChange.addListener(event => this.onValueChanged.fire({value: event.value, finalSelection: true}));
	}

	getMainDomElement(): HTMLElement {
		return this.calendarBox.getMainDomElement();
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

	handleQuery(query: string, selectionDirection: SelectionDirection): Promise<boolean> {
		return Promise.resolve(false);
	}

	setValue(value: LocalDateTime): void {
		this.calendarBox.setSelectedDate(value);
	}

	destroy(): void {
		this.calendarBox.destroy();
	}

}