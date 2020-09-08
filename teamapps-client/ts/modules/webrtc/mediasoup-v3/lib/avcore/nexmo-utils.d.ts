/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
export declare class NexmoUtils {
    static pinCodeChoice(maxDigits: number, eventUrl: string, text?: string): ({
        action: string;
        text: string;
        bargeIn: boolean;
        maxDigits?: undefined;
        timeOut?: undefined;
        eventUrl?: undefined;
    } | {
        action: string;
        maxDigits: number;
        timeOut: number;
        eventUrl: string[];
        text?: undefined;
        bargeIn?: undefined;
    })[];
    static pinCodeChoiceRepeat(maxDigits: number, eventUrl: string, text?: string, pinCodeChoiceText?: string): ({
        action: string;
        text: string;
        bargeIn: boolean;
        maxDigits?: undefined;
        timeOut?: undefined;
        eventUrl?: undefined;
    } | {
        action: string;
        maxDigits: number;
        timeOut: number;
        eventUrl: string[];
        text?: undefined;
        bargeIn?: undefined;
    } | {
        action: string;
        text: string;
    })[];
    static mixerConnect(url: string, headers: {
        worker: number;
        mixerId: string;
        stream: string;
    }, text?: string): ({
        action: string;
        text: string;
        endpoint?: undefined;
    } | {
        action: string;
        endpoint: {
            type: string;
            uri: string;
            "content-type": string;
            headers: {
                worker: number;
                mixerId: string;
                stream: string;
            };
        }[];
        text?: undefined;
    })[];
}
