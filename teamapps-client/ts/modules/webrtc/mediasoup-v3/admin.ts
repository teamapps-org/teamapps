/*-
 * TeamApps
 * ---
 * Copyright (C) 2019 TeamApps.org
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
 */

const $ = require('jquery');

export class Admin {
  private createSessionCount: number = 0;
  private serverDomain: string;
  private serverPort: number;

  public constructor(serverDomain: string, serverPort: number) {
    this.serverDomain = serverDomain;
    this.serverPort = serverPort;
  }

  public async createSession(callback: (sessionID: number, streamUUID: string, authToken: string) => void) {
    this.createSessionCount++;
    let streamUUID: string = this.createUUID();
    let authToken: string = this.createUUID();
    let data = { streamUUID: streamUUID, authToken: authToken };
    $.ajax({
      crossDomain: true,
      type: "POST",
      url: `https://${this.serverDomain}:${this.serverPort}/add`,  
      data: data
    })
    .done(function() {
      callback(this.createSessionCount, streamUUID, authToken);
      console.log("REST call 'add' - completed - streamUUID: " + streamUUID + ", authToken: " + authToken);
    })
    .fail(function() {
      console.error("REST call 'add' - failed - streamUUID: " + streamUUID);
    });
  }

  public async deleteSession(streamUUID: string) {
    let data = { streamUUID: streamUUID };
    $.ajax({
      type: "POST",
      url: `https://${this.serverDomain}:${this.serverPort}/delete`,  
      data: data
    })
    .done(function() {
      console.log("REST call 'delete' - completed - streamUUID: " + streamUUID);
    })
    .fail(function() {
      console.error("REST call 'delete' - failed - streamUUID: " + streamUUID);
    });
  }

  private createUUID(): string { 
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}