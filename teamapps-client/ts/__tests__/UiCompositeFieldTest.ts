/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import Column = Column;
describe('UiCompositeField.calculateColumnWidths()', function () {

    function column(values: any): Column {
        let defaults = {
            _type: null,
            width: 0,
            minWidth: 0,
            maxWidth: 0,
            index: 0,
            calculatedWidth: null,
            $col: null
        };
        return {...defaults, ...values};
    }

    it('calculates the column widths relative to the minWidth values if there is not enough available width for all minWidths', () => {
        let columns = [
            column({minWidth: 100, width: 100}),
            column({minWidth: 60, width: .5}),
            column({minWidth: 40, width: .1})
        ];

        console.log(columns[0]);
        UiCompositeField.calculateColumnWidths(columns, 100);

        expect(columns.map(c => c.calculatedWidth)).toEqual(['50%', '30%', '20%']);
    });

});
