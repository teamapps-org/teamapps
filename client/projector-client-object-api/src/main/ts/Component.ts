/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {ClientObject} from "./ClientObject";
import {DtoComponentCommandHandler} from "./generated";
import {ProjectorEvent} from "./util";


export interface Component extends ClientObject, DtoComponentCommandHandler {

	/**
	 * @return The main DOM element of this component.
	 */
	getMainElement(): HTMLElement;

	readonly onVisibilityChanged: ProjectorEvent<boolean>;

	isVisible(): boolean;
	
	setVisible(visible: boolean): any;
	setStyle(selector: string, styles: {[name: string]: string}): any;
	setClassNames(selector: string, classNames: {[name: string]: boolean}): any;
	setAttributes(selector: string, attributes: {[name: string]: string}): any;

}
