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
describe('UiCalendarFullCalendarEventSource', function () {

    var contextSpy:TeamAppsUiContext;

    beforeEach(function () {
        contextSpy = {
            createComponent: null,
            fireEvent: null,
            config: null,
            getIconPath: null
        };

        spyOn(contextSpy, 'fireEvent');
    });


    xit('fires teamapps event when called and has no data', function () {
        var eventSource = new UiCalendarFullCalendarEventSource(contextSpy, "myCalendarView");

        var start = moment("2015-01-01 00:00:00.000+00:00");
        var end = moment("2015-02-01 00:00:00.000+00:00");
        eventSource.events(start, end, 'UTC', null);

        expect(contextSpy.fireEvent.calls.count()).toBe(1);
        expect(contextSpy.fireEvent.calls.argsFor(0)[0].rangeStart).toBe(start.valueOf());
        expect(contextSpy.fireEvent.calls.argsFor(0)[0].rangeEnd).toBe(end.valueOf());
    });

    xit('requests only the data needed, when part of data is present', function () {
        var eventSource = new UiCalendarFullCalendarEventSource(contextSpy, "myCalendarView");
        eventSource.addEvents();

        var start = moment("2015-01-01 00:00:00.000+00:00");
        var end = moment("2015-02-01 00:00:00.000+00:00");
        eventSource.events(start, end, 'UTC', null);

        expect(contextSpy.fireEvent.calls.count()).toBe(1);
        expect(contextSpy.fireEvent.calls.argsFor(0)[0].rangeStart).toBe(start.valueOf());
        expect(contextSpy.fireEvent.calls.argsFor(0)[0].rangeEnd).toBe(end.valueOf());
    });


});

