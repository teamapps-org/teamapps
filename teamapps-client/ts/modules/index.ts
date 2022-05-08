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
import "@less/teamapps";

import 'typeface-roboto';

(window as any).jQuery = (window as any).$ = require("jquery"); // needs to be global for fullcalendar (at least)
require("jquery-ui/ui/version.js");
require("jquery-ui/ui/position.js");

(window as any).moment = require("moment"); // needs to be a global variable for fullcalendar

require("../lib/jquery.event.drag-2.3.0-fixed.js");
require("slickgrid/slick.core");
require("slickgrid/plugins/slick.checkboxselectcolumn");
require("slickgrid/plugins/slick.autotooltips");
require("slickgrid/plugins/slick.cellselectionmodel");
require("slickgrid/plugins/slick.rowselectionmodel");
require("slickgrid/controls/slick.columnpicker");
require("slickgrid/slick.formatters");
require("slickgrid/slick.editors");
require("slickgrid/slick.grid");

require("webui-popover");

require("slick-carousel");

export {DefaultTeamAppsUiContext} from "./DefaultTeamAppsUiContext";
export {UiWorkSpaceLayoutChildWindowTeamAppsUiContext} from "./workspace-layout/UiWorkSpaceLayoutChildWindowTeamAppsUiContext";
export {TeamAppsConnectionImpl} from "./communication/TeamAppsConnectionImpl";

export {UiApplicationLayout} from "./UiApplicationLayout";
export {UiCalendar} from "./UiCalendar";
export {UiDocumentViewer} from "./UiDocumentViewer";
export {UiDummyComponent} from "./UiDummyComponent";
export {UiElegantPanel} from "./UiElegantPanel";
export {UiStaticGridLayout} from "./UiStaticGridLayout";
export {UiResponsiveGridLayout} from "./UiResponsiveGridLayout";
export {UiGridForm} from "./UiGridForm";
export {UiImageCropper} from "./UiImageCropper";
export {UiImageDisplay} from "./UiImageDisplay";
export {UiInfiniteItemView} from "./UiInfiniteItemView";
export {UiInfiniteItemView2} from "./UiInfiniteItemView2";
export {UiLiveStreamComponent} from "./UiLiveStreamComponent";
export {UiMap} from "./UiMap";
export {UiMap2} from "./UiMap2";
export {UiMediaTrackGraph} from "./UiMediaTrackGraph";
export {UiMobileLayout} from "./UiMobileLayout";
export {UiNavigationBar} from "./UiNavigationBar";
export {UiNetworkGraph} from "./UiNetworkGraph";
export {UiPageView} from "./UiPageView";
export {UiPanel} from "./UiPanel";
export {UiRootPanel} from "./UiRootPanel";
export {UiSplitPane} from "./UiSplitPane";
export {UiItemView} from "./UiItemView";
export {UiTable} from "./table/UiTable";
export {UiTabPanel} from "./UiTabPanel";
export {UiTimeGraph} from "./charting/UiTimeGraph";
export {UiToolAccordion} from "./tool-container/tool-accordion/UiToolAccordion";
export {UiToolbar} from "./tool-container/toolbar/UiToolbar";
export {UiTree} from "./UiTree";
export {UiVerticalLayout} from "./UiVerticalLayout";
export {UiVideoPlayer} from "./UiVideoPlayer";
export {UiShakaPlayer} from "./UiShakaPlayer";
export {UiWebRtcPlayer} from "./UiWebRtcPlayer";
export {UiWindow} from "./UiWindow";
export {UiWorkSpaceLayout} from "./workspace-layout/UiWorkSpaceLayout";
export {UiIFrame} from "./UiIFrame";
export {UiFlexContainer} from "./UiFlexContainer";
export {UiChatDisplay} from "./UiChatDisplay";
export {UiChatInput} from "./UiChatInput";
export {UiAbsoluteLayout} from "./UiAbsoluteLayout";
export {UiFloatingComponent} from "./UiFloatingComponent";
export {UiPopup} from "./UiPopup";
export {UiProgressDisplay} from "./UiProgressDisplay";
export {UiDefaultMultiProgressDisplay} from "./UiDefaultMultiProgressDisplay";
export {UiNotification} from "./UiNotification";
export {UiNotificationBar} from "./UiNotificationBar";
export {UiQrCodeScanner} from "./UiQrCodeScanner";
export {UiMediaSoupV3WebRtcClient} from "./webrtc/mediasoup-v3/UiMediaSoupV3WebRtcClient";
export {UiAudioLevelIndicator} from "./UiAudioLevelIndicator";
export {UiHtmlView} from "./UiHtmlView";

export {UiGauge} from "./UiGauge";

export {UiButton} from "./formfield/UiButton";
export {UiCheckBox} from "./formfield/UiCheckBox";
export {UiComboBox} from "./formfield/UiComboBox";
export {UiComponentField} from "./formfield/UiComponentField";
export {UiCompositeField} from "./formfield/UiCompositeField";
export {UiCurrencyField} from "./formfield/UiCurrencyField";
export {UiLocalDateField} from "./formfield/datetime/UiLocalDateField";
export {UiInstantDateTimeField} from "./formfield/datetime/UiInstantDateTimeField";
export {UiLocalDateTimeField} from "./formfield/datetime/UiLocalDateTimeField";
export {UiLocalTimeField} from "./formfield/datetime/UiLocalTimeField";
export {UiDisplayField} from "./formfield/UiDisplayField";
export {UiField} from "./formfield/UiField";
export {UiNumberField} from "./formfield/UiNumberField";
export {UiImageField} from "./formfield/UiImageField";
export {UiLabel} from "./formfield/UiLabel";
export {UiFileField} from "./formfield/file/UiFileField";
export {UiSimpleFileField} from "./formfield/file/UiSimpleFileField";
export {UiPictureChooser} from "./formfield/file/UiPictureChooser";
export {UiMultiLineTextField} from "./formfield/UiMultiLineTextField";
export {UiPasswordField} from "./formfield/UiPasswordField";
export {UiTagComboBox} from "./formfield/UiTagComboBox";
export {UiTextField} from "./formfield/UiTextField";
export {UiRichTextEditor} from "./formfield/UiRichTextEditor";
export {UiColorPicker} from "./formfield/UiColorPicker";
export {UiFieldGroup} from "./formfield/UiFieldGroup";
export {UiTemplateField} from "./formfield/UiTemplateField";
export {UiLinkButton} from "./UiLinkButton";
export {UiDiv} from "./UiDiv";

export {UiToolButton} from "./micro-components/UiToolButton";

export {UiPieChart} from "./UiPieChart";
export {UiTreeGraph} from "./UiTreeGraph";

export {UiTestHarness} from "./UiTestHarness";
export {draggable} from "./util/draggable";

// export {typescriptDeclarationFixConstant as AbstractUiChartConfig} from "./../generated/AbstractUiChartConfig";

import * as log from "loglevel";
(window as any).log = log;

export {VideoTrackMixer} from "./webrtc/VideoTrackMixer"
