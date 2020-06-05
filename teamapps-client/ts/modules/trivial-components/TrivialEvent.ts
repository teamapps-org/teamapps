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
/*!
*
*  Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*
*/
export type TrivialEventListener<EO> = (eventObject?: EO, eventSource?: any, originalEvent?: Event) => void;

export class TrivialEvent<EO> {
    private listeners:TrivialEventListener<EO>[] = [];

    constructor(private eventSource: any) {
    }

    public addListener(fn:TrivialEventListener<EO>) {
        this.listeners.push(fn);
    };

    public removeListener(fn:TrivialEventListener<EO>) {
        const listenerIndex = this.listeners.indexOf(fn);
        if (listenerIndex != -1) {
            this.listeners.splice(listenerIndex, 1);
        }
    };

    public fire(eventObject?: EO, originalEvent?: any) {
        for (let i = 0; i < this.listeners.length; i++) {
            this.listeners[i].call(this.listeners[i], eventObject, this.eventSource, originalEvent);
        }
    };
}
