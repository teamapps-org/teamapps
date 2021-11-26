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
import "./less/teamapps.less";

import 'typeface-roboto';

import "moment"; // needs to be a global variable for fullcalendar

import "webui-popover";

export {DefaultTeamAppsUiContext} from "./ts/DefaultTeamAppsUiContext";
// export {TeamAppsConnectionImpl} from "./ts/modules/communication/TeamAppsConnectionImpl";
//
// export {UiPanel} from "./ts/modules/UiPanel";
// export {UiRootPanel} from "./ts/modules/UiRootPanel";
//
// export {UiField} from "./ts/modules/formfield/UiField";
//
// export {UiToolButton} from "./ts/modules/micro-components/UiToolButton";
//
// export {draggable} from "./ts/modules/util/draggable";

// export {typescriptDeclarationFixConstant as AbstractUiChartConfig} from "./../generated/AbstractUiChartConfig";

import * as log from "loglevel";
(window as any).log = log;
