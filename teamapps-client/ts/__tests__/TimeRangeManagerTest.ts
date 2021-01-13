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
describe('IntervalManager', function () {

    it('is initially empty', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        expect(intervalManager.intervals.length).toBe(0);
    });

    it('adds the first interval as is', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        var interval = {start: 100, end: 200};

        var result = intervalManager.addInterval(interval);

        expect(result).toEqual([interval]);
        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals[0]).toEqual(interval);
    });

    it('adds uncovered intervals as is', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        var interval1 = {start: 100, end: 200};
        intervalManager.addInterval(interval1);

        var interval2 = {start: 300, end: 400};
        var result = intervalManager.addInterval(interval2);

        expect(result).toEqual([interval2]);
        expect(intervalManager.intervals.length).toBe(2);
        expect(intervalManager.intervals).toEqual([interval1, interval2]);
    });

    it('merges adjacent intervals (exactly adjacent)', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval({start: 100, end: 200});

        var interval2 = {start: 200, end: 400};
        var result = intervalManager.addInterval(interval2);
        expect(result).toEqual([interval2]);
        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals).toEqual([{start: 100, end: 400}]);

        var interval3 = {start: 50, end: 100};
        result = intervalManager.addInterval(interval3);
        expect(result).toEqual([interval3]);
        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals).toEqual([{start: 50, end: 400}]);
    });

    it('returns the missing intervals when adding a super interval', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval({start: 100, end: 200});
        intervalManager.addInterval({start: 300, end: 400});

        var newInterval = {start: 50, end: 450};
        var result = intervalManager.addInterval(newInterval);
        expect(result).toEqual([{start: 50, end: 100}, {start: 200, end: 300}, {start: 400, end: 450}]);

        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals).toEqual([{start: 50, end: 450}]);
    });

    it('merges intervals if added interval overlaps them', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval({start: 100, end: 200});
        intervalManager.addInterval({start: 300, end: 400});

        var newInterval = {start: 150, end: 350};
        var result = intervalManager.addInterval(newInterval);
        expect(result).toEqual([{start: 200, end: 300}]);

        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals).toEqual([{start: 100, end: 400}]);
    });

    it('returns an empty array if the interval is already covered', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval({start: 100, end: 200});

        expect(intervalManager.addInterval({start: 100, end: 200})).toEqual([]);
        expect(intervalManager.addInterval({start: 101, end: 199})).toEqual([]);

        expect(intervalManager.intervals.length).toBe(1);
        expect(intervalManager.intervals).toEqual([{start: 100, end: 200}]);
    });
});

