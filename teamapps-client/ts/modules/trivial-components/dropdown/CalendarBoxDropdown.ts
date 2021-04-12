import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {TrivialCalendarBox} from "../TrivialCalendarBox";
import {NavigationDirection} from "../TrivialCore";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {DropDownComponent, SelectionDirection} from "./DropDownComponent";

export class CalendarBoxDropdown implements DropDownComponent<LocalDateTime> {

	public readonly onValueChanged: TeamAppsEvent<{ value: LocalDateTime; finalSelection: boolean }> = new TeamAppsEvent(this);


	constructor(
		private calendarBox: TrivialCalendarBox,
		private queryFunction: (query: string) => Promise<LocalDateTime | null> | LocalDateTime | null
	) {
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

	async query(query: string, selectionDirection: SelectionDirection): Promise<boolean> {
		let suggestedDate = await this.queryFunction(query);
		if (suggestedDate != null) {
			this.calendarBox.setSelectedDate(suggestedDate);
			return true;
		} else {
			return false;
		}
	}

	destroy(): void {
		this.calendarBox.destroy();
	}

	getComponent(): TrivialCalendarBox {
		return this.calendarBox;
	}

}