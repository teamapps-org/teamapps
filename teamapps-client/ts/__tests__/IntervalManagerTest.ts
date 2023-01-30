/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import {Interval, IntervalManager} from "../modules/util/IntervalManager";

describe('IntervalManager', function () {

    it('is initially empty', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        expect(intervalManager.getCoveredIntervals().length).toBe(0);
    });

    it('adds the first interval as is', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        var interval: Interval = [100, 200];

        intervalManager.addInterval(interval);

        expect(intervalManager.getCoveredIntervals()).toEqual([interval]);
    });

    it('adds uncovered intervals as is', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        var interval1 :Interval = [100, 200];
        intervalManager.addInterval(interval1);

        var interval2 :Interval = [300, 400];
        intervalManager.addInterval(interval2);

        expect(intervalManager.getCoveredIntervals()).toEqual([interval1, interval2]);
    });

    it('merges adjacent intervals (exactly adjacent)', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval([100, 200]);

        var interval2 :Interval = [200, 400];
        intervalManager.addInterval(interval2);
        expect(intervalManager.getCoveredIntervals()).toEqual([[100, 400]]);

        var interval3 :Interval = [50, 100];
        intervalManager.addInterval(interval3);
        expect(intervalManager.getCoveredIntervals()).toEqual([[50, 400]]);
    });

    it('returns the missing intervals when adding a super interval', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval([100, 200]);
        intervalManager.addInterval([300, 400]);

        var newInterval :Interval = [50, 450];
        intervalManager.addInterval(newInterval);

        expect(intervalManager.getCoveredIntervals()).toEqual([[50, 450]]);
    });

    it('merges intervals if added interval overlaps them', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval([100, 200]);
        intervalManager.addInterval([300, 400]);

        var newInterval :Interval = [150, 350];
        intervalManager.addInterval(newInterval);

        expect(intervalManager.getCoveredIntervals().length).toBe(1);
        expect(intervalManager.getCoveredIntervals()).toEqual([[100, 400]]);
    });

    it('returns an empty array if the interval is already covered', function () {
        var intervalManager:IntervalManager = new IntervalManager();
        intervalManager.addInterval([100, 200]);
        intervalManager.addInterval([100, 200]);
        intervalManager.addInterval([101, 199]);

        expect(intervalManager.getCoveredIntervals().length).toBe(1);
        expect(intervalManager.getCoveredIntervals()).toEqual([[100, 200]]);
    });
});

