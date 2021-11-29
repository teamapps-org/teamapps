/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFullLongMediumShortType} from "./UiFullLongMediumShortType";
import {UiDayPeriodDisplayStyle} from "./UiDayPeriodDisplayStyle";
import {UiHourCycleType} from "./UiHourCycleType";
import {UiLongShortNarrowType} from "./UiLongShortNarrowType";
import {UiNumericType} from "./UiNumericType";
import {UiNumericOrLongShortNarrowType} from "./UiNumericOrLongShortNarrowType";


export interface UiDateTimeFormatDescriptorConfig {
	_type?: string;
	dateStyle?: UiFullLongMediumShortType;
	timeStyle?: UiFullLongMediumShortType;
	fractionalSecondDigits?: number;
	dayPeriod?: UiDayPeriodDisplayStyle;
	hourCycle?: UiHourCycleType;
	weekday?: UiLongShortNarrowType;
	era?: UiLongShortNarrowType;
	year?: UiNumericType;
	month?: UiNumericOrLongShortNarrowType;
	day?: UiNumericType;
	hour?: UiNumericType;
	minute?: UiNumericType;
	second?: UiNumericType
}


