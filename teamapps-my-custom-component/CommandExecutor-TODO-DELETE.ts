/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiClientObject} from "../UiClientObject";
import {UiClientObjectReference} from "./UiClientObjectConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "../TeamAppsUiContext";

export class CommandExecutor {
	private commandExecutorsByCommandName: {[commandName: string]: (targetObject: any, command: any) => void} = {
		'UiNetworkGraph.setZoomFactor': async (targetObject: any, command: any) => {
			return await targetObject.setZoomFactor(command.zoomFactor);
		},
		'UiNetworkGraph.setGravity': async (targetObject: any, command: any) => {
			return await targetObject.setGravity(command.gravity);
		},
		'UiNetworkGraph.setCharge': async (targetObject: any, command: any) => {
			return await targetObject.setCharge(command.charge, command.overrideNodeCharge);
		},
		'UiNetworkGraph.setDistance': async (targetObject: any, command: any) => {
			return await targetObject.setDistance(command.linkDistance, command.nodeDistance);
		},
		'UiNetworkGraph.zoomAllNodesIntoView': async (targetObject: any, command: any) => {
			return await targetObject.zoomAllNodesIntoView(command.animationDuration);
		},
		'UiNetworkGraph.addNodesAndLinks': async (targetObject: any, command: any) => {
			return await targetObject.addNodesAndLinks(command.nodes, command.links);
		},
		'UiNetworkGraph.removeNodesAndLinks': async (targetObject: any, command: any) => {
			return await targetObject.removeNodesAndLinks(command.nodeIds, command.linksBySourceNodeId);
		},
		'UiNotification.close': async (targetObject: any, command: any) => {
			return await targetObject.close();
		},
		'UiNotification.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiNotificationBar.addItem': async (targetObject: any, command: any) => {
			return await targetObject.addItem(command.item);
		},
		'UiNotificationBar.updateItem': async (targetObject: any, command: any) => {
			return await targetObject.updateItem(command.item);
		},
		'UiNotificationBar.removeItem': async (targetObject: any, command: any) => {
			return await targetObject.removeItem(command.id, command.exitAnimation);
		},
		'UiCalendar.setViewMode': async (targetObject: any, command: any) => {
			return await targetObject.setViewMode(command.viewMode);
		},
		'UiCalendar.setDisplayedDate': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayedDate(command.date);
		},
		'UiCalendar.addEvent': async (targetObject: any, command: any) => {
			return await targetObject.addEvent(command.theEvent);
		},
		'UiCalendar.removeEvent': async (targetObject: any, command: any) => {
			return await targetObject.removeEvent(command.eventId);
		},
		'UiCalendar.setCalendarData': async (targetObject: any, command: any) => {
			return await targetObject.setCalendarData(command.events);
		},
		'UiCalendar.clearCalendar': async (targetObject: any, command: any) => {
			return await targetObject.clearCalendar();
		},
		'UiCalendar.registerTemplate': async (targetObject: any, command: any) => {
			return await targetObject.registerTemplate(command.id, command.template);
		},
		'UiCalendar.setTimeZoneId': async (targetObject: any, command: any) => {
			return await targetObject.setTimeZoneId(command.timeZoneId);
		},
		'UiAudioLevelIndicator.setDeviceId': async (targetObject: any, command: any) => {
			return await targetObject.setDeviceId(command.deviceId);
		},
		'UiItemView.setFilter': async (targetObject: any, command: any) => {
			return await targetObject.setFilter(command.filter);
		},
		'UiItemView.addItemGroup': async (targetObject: any, command: any) => {
			return await targetObject.addItemGroup(command.itemGroup);
		},
		'UiItemView.refreshItemGroup': async (targetObject: any, command: any) => {
			return await targetObject.refreshItemGroup(command.itemGroup);
		},
		'UiItemView.removeItemGroup': async (targetObject: any, command: any) => {
			return await targetObject.removeItemGroup(command.groupId);
		},
		'UiItemView.addItem': async (targetObject: any, command: any) => {
			return await targetObject.addItem(command.groupId, command.item);
		},
		'UiItemView.removeItem': async (targetObject: any, command: any) => {
			return await targetObject.removeItem(command.groupId, command.itemId);
		},
		'UiAbsoluteLayout.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.components, command.animationDuration, command.easing);
		},
		'UiWorkSpaceLayout.setToolbar': async (targetObject: any, command: any) => {
			return await targetObject.setToolbar(command.toolbar);
		},
		'UiWorkSpaceLayout.addViewAsTab': async (targetObject: any, command: any) => {
			return await targetObject.addViewAsTab(command.newView, command.viewGroupId, command.select);
		},
		'UiWorkSpaceLayout.addViewAsNeighbourTab': async (targetObject: any, command: any) => {
			return await targetObject.addViewAsNeighbourTab(command.newView, command.existingViewName, command.select);
		},
		'UiWorkSpaceLayout.addViewRelativeToOtherView': async (targetObject: any, command: any) => {
			return await targetObject.addViewRelativeToOtherView(command.newView, command.existingViewName, command.relativePosition, command.sizePolicy, command.referenceChildSize);
		},
		'UiWorkSpaceLayout.addViewToTopLevel': async (targetObject: any, command: any) => {
			return await targetObject.addViewToTopLevel(command.newView, command.windowId, command.relativePosition, command.sizePolicy, command.referenceChildSize);
		},
		'UiWorkSpaceLayout.moveViewToNeighbourTab': async (targetObject: any, command: any) => {
			return await targetObject.moveViewToNeighbourTab(command.viewName, command.existingViewName, command.select);
		},
		'UiWorkSpaceLayout.moveViewRelativeToOtherView': async (targetObject: any, command: any) => {
			return await targetObject.moveViewRelativeToOtherView(command.viewName, command.existingViewName, command.relativePosition, command.sizePolicy, command.referenceChildSize);
		},
		'UiWorkSpaceLayout.moveViewToTopLevel': async (targetObject: any, command: any) => {
			return await targetObject.moveViewToTopLevel(command.viewName, command.windowId, command.relativePosition, command.sizePolicy, command.referenceChildSize);
		},
		'UiWorkSpaceLayout.redefineLayout': async (targetObject: any, command: any) => {
			return await targetObject.redefineLayout(command.layoutsByWindowId, command.addedViews);
		},
		'UiWorkSpaceLayout.removeView': async (targetObject: any, command: any) => {
			return await targetObject.removeView(command.viewName);
		},
		'UiWorkSpaceLayout.refreshViewAttributes': async (targetObject: any, command: any) => {
			return await targetObject.refreshViewAttributes(command.viewName, command.tabIcon, command.tabCaption, command.tabCloseable, command.visible);
		},
		'UiWorkSpaceLayout.refreshViewComponent': async (targetObject: any, command: any) => {
			return await targetObject.refreshViewComponent(command.viewName, command.component);
		},
		'UiWorkSpaceLayout.selectView': async (targetObject: any, command: any) => {
			return await targetObject.selectView(command.viewName);
		},
		'UiWorkSpaceLayout.setViewGroupPanelState': async (targetObject: any, command: any) => {
			return await targetObject.setViewGroupPanelState(command.viewGroupId, command.panelState);
		},
		'UiWorkSpaceLayout.setMultiProgressDisplay': async (targetObject: any, command: any) => {
			return await targetObject.setMultiProgressDisplay(command.multiProgressDisplay);
		},
		'UiContextMenu.hide': async (targetObject: any, command: any) => {
			return await targetObject.hide();
		},
		'UiMediaSoupV3WebRtcClient.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiMediaSoupV3WebRtcClient.setActive': async (targetObject: any, command: any) => {
			return await targetObject.setActive(command.active);
		},
		'UiMediaSoupV3WebRtcClient.setContextMenuContent': async (targetObject: any, command: any) => {
			return await targetObject.setContextMenuContent(command.requestId, command.component);
		},
		'UiMediaSoupV3WebRtcClient.closeContextMenu': async (targetObject: any, command: any) => {
			return await targetObject.closeContextMenu(command.requestId);
		},
		'UiPanel.setContent': async (targetObject: any, command: any) => {
			return await targetObject.setContent(command.content);
		},
		'UiPanel.setLeftHeaderField': async (targetObject: any, command: any) => {
			return await targetObject.setLeftHeaderField(command.field);
		},
		'UiPanel.setRightHeaderField': async (targetObject: any, command: any) => {
			return await targetObject.setRightHeaderField(command.field);
		},
		'UiPanel.setTitle': async (targetObject: any, command: any) => {
			return await targetObject.setTitle(command.title);
		},
		'UiPanel.setIcon': async (targetObject: any, command: any) => {
			return await targetObject.setIcon(command.icon);
		},
		'UiPanel.setToolbar': async (targetObject: any, command: any) => {
			return await targetObject.setToolbar(command.toolbar);
		},
		'UiPanel.setMaximized': async (targetObject: any, command: any) => {
			return await targetObject.setMaximized(command.maximized);
		},
		'UiPanel.setWindowButtons': async (targetObject: any, command: any) => {
			return await targetObject.setWindowButtons(command.windowButtons);
		},
		'UiPanel.setToolButtons': async (targetObject: any, command: any) => {
			return await targetObject.setToolButtons(command.toolButtons);
		},
		'UiPanel.setStretchContent': async (targetObject: any, command: any) => {
			return await targetObject.setStretchContent(command.stretch);
		},
		'UiWindow.show': async (targetObject: any, command: any) => {
			return await targetObject.show(command.animationDuration);
		},
		'UiWindow.close': async (targetObject: any, command: any) => {
			return await targetObject.close(command.animationDuration);
		},
		'UiWindow.setCloseable': async (targetObject: any, command: any) => {
			return await targetObject.setCloseable(command.closeable);
		},
		'UiWindow.setCloseOnEscape': async (targetObject: any, command: any) => {
			return await targetObject.setCloseOnEscape(command.closeOnEscape);
		},
		'UiWindow.setCloseOnClickOutside': async (targetObject: any, command: any) => {
			return await targetObject.setCloseOnClickOutside(command.closeOnClickOutside);
		},
		'UiWindow.setModal': async (targetObject: any, command: any) => {
			return await targetObject.setModal(command.modal);
		},
		'UiWindow.setModalBackgroundDimmingColor': async (targetObject: any, command: any) => {
			return await targetObject.setModalBackgroundDimmingColor(command.modalBackgroundDimmingColor);
		},
		'UiWindow.setSize': async (targetObject: any, command: any) => {
			return await targetObject.setSize(command.width, command.height);
		},
		'UiElegantPanel.setContent': async (targetObject: any, command: any) => {
			return await targetObject.setContent(command.content);
		},
		'UiTreeGraph.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiTreeGraph.setZoomFactor': async (targetObject: any, command: any) => {
			return await targetObject.setZoomFactor(command.zoomFactor);
		},
		'UiTreeGraph.setNodes': async (targetObject: any, command: any) => {
			return await targetObject.setNodes(command.nodes);
		},
		'UiTreeGraph.addNode': async (targetObject: any, command: any) => {
			return await targetObject.addNode(command.node);
		},
		'UiTreeGraph.removeNode': async (targetObject: any, command: any) => {
			return await targetObject.removeNode(command.nodeId);
		},
		'UiTreeGraph.setNodeExpanded': async (targetObject: any, command: any) => {
			return await targetObject.setNodeExpanded(command.nodeId, command.expanded);
		},
		'UiTreeGraph.updateNode': async (targetObject: any, command: any) => {
			return await targetObject.updateNode(command.node);
		},
		'UiTreeGraph.moveToRootNode': async (targetObject: any, command: any) => {
			return await targetObject.moveToRootNode();
		},
		'UiTreeGraph.moveToNode': async (targetObject: any, command: any) => {
			return await targetObject.moveToNode(command.nodeId);
		},
		'UiNavigationBar.setButtons': async (targetObject: any, command: any) => {
			return await targetObject.setButtons(command.buttons);
		},
		'UiNavigationBar.setButtonVisible': async (targetObject: any, command: any) => {
			return await targetObject.setButtonVisible(command.buttonId, command.visible);
		},
		'UiNavigationBar.setBackgroundColor': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundColor(command.backgroundColor);
		},
		'UiNavigationBar.setBorderColor': async (targetObject: any, command: any) => {
			return await targetObject.setBorderColor(command.borderColor);
		},
		'UiNavigationBar.addFanOutComponent': async (targetObject: any, command: any) => {
			return await targetObject.addFanOutComponent(command.fanOutComponent);
		},
		'UiNavigationBar.removeFanOutComponent': async (targetObject: any, command: any) => {
			return await targetObject.removeFanOutComponent(command.fanOutComponent);
		},
		'UiNavigationBar.showFanOutComponent': async (targetObject: any, command: any) => {
			return await targetObject.showFanOutComponent(command.fanOutComponent);
		},
		'UiNavigationBar.hideFanOutComponent': async (targetObject: any, command: any) => {
			return await targetObject.hideFanOutComponent();
		},
		'UiNavigationBar.setMultiProgressDisplay': async (targetObject: any, command: any) => {
			return await targetObject.setMultiProgressDisplay(command.multiProgressDisplay);
		},
		'UiMap2.registerTemplate': async (targetObject: any, command: any) => {
			return await targetObject.registerTemplate(command.id, command.template);
		},
		'UiMap2.addMarker': async (targetObject: any, command: any) => {
			return await targetObject.addMarker(command.marker);
		},
		'UiMap2.removeMarker': async (targetObject: any, command: any) => {
			return await targetObject.removeMarker(command.id);
		},
		'UiMap2.clearMarkers': async (targetObject: any, command: any) => {
			return await targetObject.clearMarkers();
		},
		'UiMap2.setMapMarkerCluster': async (targetObject: any, command: any) => {
			return await targetObject.setMapMarkerCluster(command.cluster);
		},
		'UiMap2.setHeatMap': async (targetObject: any, command: any) => {
			return await targetObject.setHeatMap(command.data);
		},
		'UiMap2.addShape': async (targetObject: any, command: any) => {
			return await targetObject.addShape(command.shapeId, command.shape);
		},
		'UiMap2.updateShape': async (targetObject: any, command: any) => {
			return await targetObject.updateShape(command.shapeId, command.shape);
		},
		'UiMap2.changeShape': async (targetObject: any, command: any) => {
			return await targetObject.changeShape(command.shapeId, command.change);
		},
		'UiMap2.removeShape': async (targetObject: any, command: any) => {
			return await targetObject.removeShape(command.shapeId);
		},
		'UiMap2.clearShapes': async (targetObject: any, command: any) => {
			return await targetObject.clearShapes();
		},
		'UiMap2.startDrawingShape': async (targetObject: any, command: any) => {
			return await targetObject.startDrawingShape(command.shapeType, command.shapeProperties);
		},
		'UiMap2.stopDrawingShape': async (targetObject: any, command: any) => {
			return await targetObject.stopDrawingShape();
		},
		'UiMap2.setZoomLevel': async (targetObject: any, command: any) => {
			return await targetObject.setZoomLevel(command.zoom);
		},
		'UiMap2.setLocation': async (targetObject: any, command: any) => {
			return await targetObject.setLocation(command.location, command.animationDurationMillis, command.targetZoomLevel);
		},
		'UiMap2.setStyleUrl': async (targetObject: any, command: any) => {
			return await targetObject.setStyleUrl(command.styleUrl);
		},
		'UiMap2.fitBounds': async (targetObject: any, command: any) => {
			return await targetObject.fitBounds(command.southWest, command.northEast);
		},
		'AbstractUiChart.setLegendStyle': async (targetObject: any, command: any) => {
			return await targetObject.setLegendStyle(command.legendStyle);
		},
		'UiPieChart.setDataPointWeighting': async (targetObject: any, command: any) => {
			return await targetObject.setDataPointWeighting(command.dataPointWeighting);
		},
		'UiPieChart.setRotation3D': async (targetObject: any, command: any) => {
			return await targetObject.setRotation3D(command.rotation3D);
		},
		'UiPieChart.setHeight3D': async (targetObject: any, command: any) => {
			return await targetObject.setHeight3D(command.height3D);
		},
		'UiPieChart.setRotationClockwise': async (targetObject: any, command: any) => {
			return await targetObject.setRotationClockwise(command.rotationClockwise);
		},
		'UiPieChart.setInnerRadiusProportion': async (targetObject: any, command: any) => {
			return await targetObject.setInnerRadiusProportion(command.innerRadiusProportion);
		},
		'UiPieChart.setDataPoints': async (targetObject: any, command: any) => {
			return await targetObject.setDataPoints(command.dataPoints, command.animationDuration);
		},
		'UiPageView.addBlock': async (targetObject: any, command: any) => {
			return await targetObject.addBlock(command.block, command.before, command.otherBlockId);
		},
		'UiPageView.removeBlock': async (targetObject: any, command: any) => {
			return await targetObject.removeBlock(command.blockId);
		},
		'UiDiv.setContent': async (targetObject: any, command: any) => {
			return await targetObject.setContent(command.content);
		},
		'UiDiv.setInnerHtml': async (targetObject: any, command: any) => {
			return await targetObject.setInnerHtml(command.innerHtml);
		},
		'UiHtmlView.addComponent': async (targetObject: any, command: any) => {
			return await targetObject.addComponent(command.containerElementSelector, command.component, command.clearContainer);
		},
		'UiHtmlView.removeComponent': async (targetObject: any, command: any) => {
			return await targetObject.removeComponent(command.component);
		},
		'UiHtmlView.setContentHtml': async (targetObject: any, command: any) => {
			return await targetObject.setContentHtml(command.containerElementSelector, command.html);
		},
		'UiLinkButton.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiMap.registerTemplate': async (targetObject: any, command: any) => {
			return await targetObject.registerTemplate(command.id, command.template);
		},
		'UiMap.addMarker': async (targetObject: any, command: any) => {
			return await targetObject.addMarker(command.marker);
		},
		'UiMap.removeMarker': async (targetObject: any, command: any) => {
			return await targetObject.removeMarker(command.id);
		},
		'UiMap.setMapMarkerCluster': async (targetObject: any, command: any) => {
			return await targetObject.setMapMarkerCluster(command.cluster);
		},
		'UiMap.addShape': async (targetObject: any, command: any) => {
			return await targetObject.addShape(command.shapeId, command.shape);
		},
		'UiMap.updateShape': async (targetObject: any, command: any) => {
			return await targetObject.updateShape(command.shapeId, command.shape);
		},
		'UiMap.removeShape': async (targetObject: any, command: any) => {
			return await targetObject.removeShape(command.shapeId);
		},
		'UiMap.clearShapes': async (targetObject: any, command: any) => {
			return await targetObject.clearShapes();
		},
		'UiMap.clearMarkers': async (targetObject: any, command: any) => {
			return await targetObject.clearMarkers();
		},
		'UiMap.clearMarkerCluster': async (targetObject: any, command: any) => {
			return await targetObject.clearMarkerCluster();
		},
		'UiMap.clearHeatMap': async (targetObject: any, command: any) => {
			return await targetObject.clearHeatMap();
		},
		'UiMap.startDrawingShape': async (targetObject: any, command: any) => {
			return await targetObject.startDrawingShape(command.shapeType, command.shapeProperties);
		},
		'UiMap.stopDrawingShape': async (targetObject: any, command: any) => {
			return await targetObject.stopDrawingShape();
		},
		'UiMap.setZoomLevel': async (targetObject: any, command: any) => {
			return await targetObject.setZoomLevel(command.zoom);
		},
		'UiMap.setLocation': async (targetObject: any, command: any) => {
			return await targetObject.setLocation(command.location);
		},
		'UiMap.setMapType': async (targetObject: any, command: any) => {
			return await targetObject.setMapType(command.mapType);
		},
		'UiMap.setHeatMap': async (targetObject: any, command: any) => {
			return await targetObject.setHeatMap(command.data);
		},
		'UiMap.fitBounds': async (targetObject: any, command: any) => {
			return await targetObject.fitBounds(command.southWest, command.northEast);
		},
		'UiIFrame.setUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUrl(command.url);
		},
		'UiMediaTrackGraph.setCursorPosition': async (targetObject: any, command: any) => {
			return await targetObject.setCursorPosition(command.time);
		},
		'UiVideoPlayer.setUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUrl(command.url);
		},
		'UiVideoPlayer.setPreloadMode': async (targetObject: any, command: any) => {
			return await targetObject.setPreloadMode(command.preloadMode);
		},
		'UiVideoPlayer.setAutoplay': async (targetObject: any, command: any) => {
			return await targetObject.setAutoplay(command.autoplay);
		},
		'UiVideoPlayer.play': async (targetObject: any, command: any) => {
			return await targetObject.play();
		},
		'UiVideoPlayer.pause': async (targetObject: any, command: any) => {
			return await targetObject.pause();
		},
		'UiVideoPlayer.jumpTo': async (targetObject: any, command: any) => {
			return await targetObject.jumpTo(command.timeInSeconds);
		},
		'UiFlexContainer.addComponent': async (targetObject: any, command: any) => {
			return await targetObject.addComponent(command.component);
		},
		'UiFlexContainer.removeComponent': async (targetObject: any, command: any) => {
			return await targetObject.removeComponent(command.component);
		},
		'UiShakaPlayer.setUrls': async (targetObject: any, command: any) => {
			return await targetObject.setUrls(command.hlsUrl, command.dashUrl);
		},
		'UiShakaPlayer.setTime': async (targetObject: any, command: any) => {
			return await targetObject.setTime(command.timeMillis);
		},
		'UiShakaPlayer.selectAudioLanguage': async (targetObject: any, command: any) => {
			return await targetObject.selectAudioLanguage(command.language, command.role);
		},
		'UiSplitPane.setFirstChild': async (targetObject: any, command: any) => {
			return await targetObject.setFirstChild(command.firstChild);
		},
		'UiSplitPane.setLastChild': async (targetObject: any, command: any) => {
			return await targetObject.setLastChild(command.lastChild);
		},
		'UiSplitPane.setSize': async (targetObject: any, command: any) => {
			return await targetObject.setSize(command.referenceChildSize, command.sizePolicy);
		},
		'UiSplitPane.setFirstChildMinSize': async (targetObject: any, command: any) => {
			return await targetObject.setFirstChildMinSize(command.firstChildMinSize);
		},
		'UiSplitPane.setLastChildMinSize': async (targetObject: any, command: any) => {
			return await targetObject.setLastChildMinSize(command.lastChildMinSize);
		},
		'UiWebRtcPublisher.publish': async (targetObject: any, command: any) => {
			return await targetObject.publish(command.settings);
		},
		'UiWebRtcPublisher.unPublish': async (targetObject: any, command: any) => {
			return await targetObject.unPublish();
		},
		'UiWebRtcPublisher.setMicrophoneMuted': async (targetObject: any, command: any) => {
			return await targetObject.setMicrophoneMuted(command.microphoneMuted);
		},
		'UiWebRtcPublisher.setBackgroundImageUrl': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundImageUrl(command.backgroundImageUrl);
		},
		'UiWebRtcPlayer.play': async (targetObject: any, command: any) => {
			return await targetObject.play(command.settings);
		},
		'UiWebRtcPlayer.stopPlaying': async (targetObject: any, command: any) => {
			return await targetObject.stopPlaying();
		},
		'UiWebRtcPlayer.setBackgroundImageUrl': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundImageUrl(command.backgroundImageUrl);
		},
		'UiProgressDisplay.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiMultiProgressDisplay.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiPopup.setBackgroundColor': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundColor(command.backgroundColor);
		},
		'UiPopup.setDimmingColor': async (targetObject: any, command: any) => {
			return await targetObject.setDimmingColor(command.backgroundColor);
		},
		'UiPopup.setPosition': async (targetObject: any, command: any) => {
			return await targetObject.setPosition(command.x, command.y);
		},
		'UiPopup.setDimensions': async (targetObject: any, command: any) => {
			return await targetObject.setDimensions(command.width, command.height);
		},
		'UiPopup.close': async (targetObject: any, command: any) => {
			return await targetObject.close();
		},
		'UiApplicationLayout.setToolbar': async (targetObject: any, command: any) => {
			return await targetObject.setToolbar(command.toolbar);
		},
		'UiApplicationLayout.setRootSplitPane': async (targetObject: any, command: any) => {
			return await targetObject.setRootSplitPane(command.splitPane);
		},
		'UiMobileLayout.setToolbar': async (targetObject: any, command: any) => {
			return await targetObject.setToolbar(command.toolbar);
		},
		'UiMobileLayout.setNavigationBar': async (targetObject: any, command: any) => {
			return await targetObject.setNavigationBar(command.navBar);
		},
		'UiMobileLayout.showView': async (targetObject: any, command: any) => {
			return await targetObject.showView(command.component, command.animation, command.animationDuration);
		},
		'UiAccordionLayout.addAccordionPanel': async (targetObject: any, command: any) => {
			return await targetObject.addAccordionPanel(command.panel, command.neighborPanelId, command.beforeNeighbor);
		},
		'UiAccordionLayout.addAccordionPanelContent': async (targetObject: any, command: any) => {
			return await targetObject.addAccordionPanelContent(command.panelId, command.content);
		},
		'UiAccordionLayout.removeAccordionPanel': async (targetObject: any, command: any) => {
			return await targetObject.removeAccordionPanel(command.panelId);
		},
		'UiAccordionLayout.removeAllPanels': async (targetObject: any, command: any) => {
			return await targetObject.removeAllPanels();
		},
		'UiAccordionLayout.selectPanel': async (targetObject: any, command: any) => {
			return await targetObject.selectPanel(command.panelId);
		},
		'UiAccordionLayout.setPanelOpen': async (targetObject: any, command: any) => {
			return await targetObject.setPanelOpen(command.panelId, command.open);
		},
		'UiRootPanel.setContent': async (targetObject: any, command: any) => {
			return await targetObject.setContent(command.content, command.transition, command.animationDuration);
		},
		'UiImageCropper.setImageUrl': async (targetObject: any, command: any) => {
			return await targetObject.setImageUrl(command.imageUrl);
		},
		'UiImageCropper.setSelectionMode': async (targetObject: any, command: any) => {
			return await targetObject.setSelectionMode(command.selectionMode);
		},
		'UiImageCropper.setAspectRatio': async (targetObject: any, command: any) => {
			return await targetObject.setAspectRatio(command.aspectRatio);
		},
		'UiImageCropper.setSelection': async (targetObject: any, command: any) => {
			return await targetObject.setSelection(command.selection);
		},
		'UiTimeGraph.setIntervalX': async (targetObject: any, command: any) => {
			return await targetObject.setIntervalX(command.intervalX);
		},
		'UiTimeGraph.setMaxPixelsBetweenDataPoints': async (targetObject: any, command: any) => {
			return await targetObject.setMaxPixelsBetweenDataPoints(command.maxPixelsBetweenDataPoints);
		},
		'UiTimeGraph.addData': async (targetObject: any, command: any) => {
			return await targetObject.addData(command.zoomLevel, command.data);
		},
		'UiTimeGraph.resetGraphData': async (targetObject: any, command: any) => {
			return await targetObject.resetGraphData(command.graphId);
		},
		'UiTimeGraph.resetAllData': async (targetObject: any, command: any) => {
			return await targetObject.resetAllData(command.intervalX, command.newZoomLevels);
		},
		'UiTimeGraph.setMouseScrollZoomPanMode': async (targetObject: any, command: any) => {
			return await targetObject.setMouseScrollZoomPanMode(command.mouseScrollZoomPanMode);
		},
		'UiTimeGraph.setSelectedInterval': async (targetObject: any, command: any) => {
			return await targetObject.setSelectedInterval(command.intervalX);
		},
		'UiTimeGraph.setGraphs': async (targetObject: any, command: any) => {
			return await targetObject.setGraphs(command.graphs);
		},
		'UiTimeGraph.addOrUpdateGraph': async (targetObject: any, command: any) => {
			return await targetObject.addOrUpdateGraph(command.graph);
		},
		'UiTimeGraph.zoomTo': async (targetObject: any, command: any) => {
			return await targetObject.zoomTo(command.intervalX);
		},
		'UiTable.clearTable': async (targetObject: any, command: any) => {
			return await targetObject.clearTable();
		},
		'UiTable.updateData': async (targetObject: any, command: any) => {
			return await targetObject.updateData(command.startIndex, command.recordIds, command.newRecords, command.totalNumberOfRecords);
		},
		'UiTable.setSorting': async (targetObject: any, command: any) => {
			return await targetObject.setSorting(command.sortField, command.sortDirection);
		},
		'UiTable.setCellValue': async (targetObject: any, command: any) => {
			return await targetObject.setCellValue(command.recordId, command.columnPropertyName, command.value);
		},
		'UiTable.markTableField': async (targetObject: any, command: any) => {
			return await targetObject.markTableField(command.recordId, command.columnPropertyName, command.mark);
		},
		'UiTable.clearAllFieldMarkings': async (targetObject: any, command: any) => {
			return await targetObject.clearAllFieldMarkings();
		},
		'UiTable.setRecordBold': async (targetObject: any, command: any) => {
			return await targetObject.setRecordBold(command.recordId, command.bold);
		},
		'UiTable.selectRecords': async (targetObject: any, command: any) => {
			return await targetObject.selectRecords(command.recordIds, command.scrollToFirstRecord);
		},
		'UiTable.selectRows': async (targetObject: any, command: any) => {
			return await targetObject.selectRows(command.rowIndexes, command.scrollToFirstRowIndex);
		},
		'UiTable.editCellIfAvailable': async (targetObject: any, command: any) => {
			return await targetObject.editCellIfAvailable(command.recordId, command.propertyName);
		},
		'UiTable.cancelEditingCell': async (targetObject: any, command: any) => {
			return await targetObject.cancelEditingCell(command.recordId, command.propertyName);
		},
		'UiTable.focusCell': async (targetObject: any, command: any) => {
			return await targetObject.focusCell(command.recordId, command.columnPropertyName);
		},
		'UiTable.setSingleCellMessages': async (targetObject: any, command: any) => {
			return await targetObject.setSingleCellMessages(command.recordId, command.columnPropertyName, command.messages);
		},
		'UiTable.clearAllCellMessages': async (targetObject: any, command: any) => {
			return await targetObject.clearAllCellMessages();
		},
		'UiTable.setColumnMessages': async (targetObject: any, command: any) => {
			return await targetObject.setColumnMessages(command.columnPropertyName, command.messages);
		},
		'UiTable.addColumns': async (targetObject: any, command: any) => {
			return await targetObject.addColumns(command.column, command.index);
		},
		'UiTable.removeColumns': async (targetObject: any, command: any) => {
			return await targetObject.removeColumns(command.columnName);
		},
		'UiTable.setColumnVisibility': async (targetObject: any, command: any) => {
			return await targetObject.setColumnVisibility(command.columnPropertyName, command.visible);
		},
		'UiTable.setContextMenuContent': async (targetObject: any, command: any) => {
			return await targetObject.setContextMenuContent(command.requestId, command.component);
		},
		'UiTable.closeContextMenu': async (targetObject: any, command: any) => {
			return await targetObject.closeContextMenu(command.requestId);
		},
		'UiGauge.setOptions': async (targetObject: any, command: any) => {
			return await targetObject.setOptions(command.options);
		},
		'UiGauge.setValue': async (targetObject: any, command: any) => {
			return await targetObject.setValue(command.value);
		},
		'UiQrCodeScanner.startScanning': async (targetObject: any, command: any) => {
			return await targetObject.startScanning(command.stopScanningAtFirstResult);
		},
		'UiQrCodeScanner.stopScanning': async (targetObject: any, command: any) => {
			return await targetObject.stopScanning();
		},
		'UiQrCodeScanner.switchCamera': async (targetObject: any, command: any) => {
			return await targetObject.switchCamera();
		},
		'UiDummyComponent.setText': async (targetObject: any, command: any) => {
			return await targetObject.setText(command.text);
		},
		'UiVerticalLayout.addComponent': async (targetObject: any, command: any) => {
			return await targetObject.addComponent(command.component);
		},
		'UiVerticalLayout.removeComponent': async (targetObject: any, command: any) => {
			return await targetObject.removeComponent(command.component);
		},
		'UiLiveStreamComponent.showWaitingVideos': async (targetObject: any, command: any) => {
			return await targetObject.showWaitingVideos(command.videoInfos, command.offsetSeconds, command.stopLiveStream);
		},
		'UiLiveStreamComponent.stopWaitingVideos': async (targetObject: any, command: any) => {
			return await targetObject.stopWaitingVideos();
		},
		'UiLiveStreamComponent.startHttpLiveStream': async (targetObject: any, command: any) => {
			return await targetObject.startHttpLiveStream(command.url);
		},
		'UiLiveStreamComponent.startLiveStreamComLiveStream': async (targetObject: any, command: any) => {
			return await targetObject.startLiveStreamComLiveStream(command.url);
		},
		'UiLiveStreamComponent.startYouTubeLiveStream': async (targetObject: any, command: any) => {
			return await targetObject.startYouTubeLiveStream(command.url);
		},
		'UiLiveStreamComponent.startCustomEmbeddedLiveStreamPlayer': async (targetObject: any, command: any) => {
			return await targetObject.startCustomEmbeddedLiveStreamPlayer(command.playerEmbedHtml, command.embedContainerId);
		},
		'UiLiveStreamComponent.stopLiveStream': async (targetObject: any, command: any) => {
			return await targetObject.stopLiveStream();
		},
		'UiLiveStreamComponent.displayImageOverlay': async (targetObject: any, command: any) => {
			return await targetObject.displayImageOverlay(command.imageUrl, command.displayMode, command.useVideoAreaAsFrame);
		},
		'UiLiveStreamComponent.removeImageOverlay': async (targetObject: any, command: any) => {
			return await targetObject.removeImageOverlay();
		},
		'UiLiveStreamComponent.displayInfoTextOverlay': async (targetObject: any, command: any) => {
			return await targetObject.displayInfoTextOverlay(command.text);
		},
		'UiLiveStreamComponent.removeInfoTextOverlay': async (targetObject: any, command: any) => {
			return await targetObject.removeInfoTextOverlay();
		},
		'UiLiveStreamComponent.setVolume': async (targetObject: any, command: any) => {
			return await targetObject.setVolume(command.volume);
		},
		'AbstractUiLiveStreamPlayer.play': async (targetObject: any, command: any) => {
			return await targetObject.play(command.url);
		},
		'AbstractUiLiveStreamPlayer.stop': async (targetObject: any, command: any) => {
			return await targetObject.stop();
		},
		'AbstractUiLiveStreamPlayer.setVolume': async (targetObject: any, command: any) => {
			return await targetObject.setVolume(command.volume);
		},
		'UiFieldGroup.setFields': async (targetObject: any, command: any) => {
			return await targetObject.setFields(command.fields);
		},
		'AbstractUiToolContainer.setButtonVisible': async (targetObject: any, command: any) => {
			return await targetObject.setButtonVisible(command.groupId, command.buttonId, command.visible);
		},
		'AbstractUiToolContainer.setButtonColors': async (targetObject: any, command: any) => {
			return await targetObject.setButtonColors(command.groupId, command.buttonId, command.backgroundColor, command.hoverBackgroundColor);
		},
		'AbstractUiToolContainer.setButtonGroupVisible': async (targetObject: any, command: any) => {
			return await targetObject.setButtonGroupVisible(command.groupId, command.visible);
		},
		'AbstractUiToolContainer.addButton': async (targetObject: any, command: any) => {
			return await targetObject.addButton(command.groupId, command.button, command.neighborButtonId, command.beforeNeighbor);
		},
		'AbstractUiToolContainer.removeButton': async (targetObject: any, command: any) => {
			return await targetObject.removeButton(command.groupId, command.buttonId);
		},
		'AbstractUiToolContainer.addButtonGroup': async (targetObject: any, command: any) => {
			return await targetObject.addButtonGroup(command.group, command.rightSide);
		},
		'AbstractUiToolContainer.removeButtonGroup': async (targetObject: any, command: any) => {
			return await targetObject.removeButtonGroup(command.groupId);
		},
		'AbstractUiToolContainer.setButtonHasDropDown': async (targetObject: any, command: any) => {
			return await targetObject.setButtonHasDropDown(command.groupId, command.buttonId, command.hasDropDown);
		},
		'AbstractUiToolContainer.setDropDownComponent': async (targetObject: any, command: any) => {
			return await targetObject.setDropDownComponent(command.groupId, command.buttonId, command.component);
		},
		'UiToolbar.setLogoImage': async (targetObject: any, command: any) => {
			return await targetObject.setLogoImage(command.logoImage);
		},
		'UiFloatingComponent.setContentComponent': async (targetObject: any, command: any) => {
			return await targetObject.setContentComponent(command.contentComponent);
		},
		'UiFloatingComponent.setExpanded': async (targetObject: any, command: any) => {
			return await targetObject.setExpanded(command.expanded);
		},
		'UiFloatingComponent.setPosition': async (targetObject: any, command: any) => {
			return await targetObject.setPosition(command.position);
		},
		'UiFloatingComponent.setDimensions': async (targetObject: any, command: any) => {
			return await targetObject.setDimensions(command.width, command.height);
		},
		'UiFloatingComponent.setMargins': async (targetObject: any, command: any) => {
			return await targetObject.setMargins(command.marginX, command.marginY);
		},
		'UiFloatingComponent.setBackgroundColor': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundColor(command.backgroundColor);
		},
		'UiFloatingComponent.setExpanderHandleColor': async (targetObject: any, command: any) => {
			return await targetObject.setExpanderHandleColor(command.expanderHandleColor);
		},
		'UiTree.replaceData': async (targetObject: any, command: any) => {
			return await targetObject.replaceData(command.nodes);
		},
		'UiTree.bulkUpdate': async (targetObject: any, command: any) => {
			return await targetObject.bulkUpdate(command.nodesToBeRemoved, command.nodesToBeAdded);
		},
		'UiTree.setSelectedNode': async (targetObject: any, command: any) => {
			return await targetObject.setSelectedNode(command.recordId);
		},
		'UiTree.registerTemplate': async (targetObject: any, command: any) => {
			return await targetObject.registerTemplate(command.id, command.template);
		},
		'UiImageDisplay.setCachedImages': async (targetObject: any, command: any) => {
			return await targetObject.setCachedImages(command.startIndex, command.cachedImages, command.totalNumberOfRecords);
		},
		'UiImageDisplay.setDisplayMode': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayMode(command.displayMode, command.zoomFactor);
		},
		'UiImageDisplay.setZoomFactor': async (targetObject: any, command: any) => {
			return await targetObject.setZoomFactor(command.zoomFactor);
		},
		'UiImageDisplay.showImage': async (targetObject: any, command: any) => {
			return await targetObject.showImage(command.id);
		},
		'UiTabPanel.setHideTabBarIfSingleTab': async (targetObject: any, command: any) => {
			return await targetObject.setHideTabBarIfSingleTab(command.hideTabBarIfSingleTab);
		},
		'UiTabPanel.setTabStyle': async (targetObject: any, command: any) => {
			return await targetObject.setTabStyle(command.tabStyle);
		},
		'UiTabPanel.setToolButtons': async (targetObject: any, command: any) => {
			return await targetObject.setToolButtons(command.toolButtons);
		},
		'UiTabPanel.setWindowButtons': async (targetObject: any, command: any) => {
			return await targetObject.setWindowButtons(command.windowButtons);
		},
		'UiTabPanel.selectTab': async (targetObject: any, command: any) => {
			return await targetObject.selectTab(command.tabId);
		},
		'UiTabPanel.addTab': async (targetObject: any, command: any) => {
			return await targetObject.addTab(command.tab, command.select);
		},
		'UiTabPanel.removeTab': async (targetObject: any, command: any) => {
			return await targetObject.removeTab(command.tabId);
		},
		'UiTabPanel.setTabToolbar': async (targetObject: any, command: any) => {
			return await targetObject.setTabToolbar(command.tabId, command.toolbar);
		},
		'UiTabPanel.setTabContent': async (targetObject: any, command: any) => {
			return await targetObject.setTabContent(command.tabId, command.component);
		},
		'UiTabPanel.setTabConfiguration': async (targetObject: any, command: any) => {
			return await targetObject.setTabConfiguration(command.tabId, command.icon, command.caption, command.closeable, command.visible, command.rightSide);
		},
		'UiStaticGridLayout.updateLayout': async (targetObject: any, command: any) => {
			return await targetObject.updateLayout(command.descriptor);
		},
		'UiResponsiveGridLayout.updateLayoutPolicies': async (targetObject: any, command: any) => {
			return await targetObject.updateLayoutPolicies(command.layoutPolicies);
		},
		'UiResponsiveGridLayout.setFillHeight': async (targetObject: any, command: any) => {
			return await targetObject.setFillHeight(command.fillHeight);
		},
		'UiInfiniteItemView.addData': async (targetObject: any, command: any) => {
			return await targetObject.addData(command.startIndex, command.data, command.totalNumberOfRecords, command.clear);
		},
		'UiInfiniteItemView.removeData': async (targetObject: any, command: any) => {
			return await targetObject.removeData(command.ids);
		},
		'UiInfiniteItemView.setItemTemplate': async (targetObject: any, command: any) => {
			return await targetObject.setItemTemplate(command.itemTemplate);
		},
		'UiInfiniteItemView.setItemWidth': async (targetObject: any, command: any) => {
			return await targetObject.setItemWidth(command.itemWidth);
		},
		'UiInfiniteItemView.setHorizontalItemMargin': async (targetObject: any, command: any) => {
			return await targetObject.setHorizontalItemMargin(command.horizontalItemMargin);
		},
		'UiInfiniteItemView.setItemJustification': async (targetObject: any, command: any) => {
			return await targetObject.setItemJustification(command.itemJustification);
		},
		'UiInfiniteItemView.setVerticalItemAlignment': async (targetObject: any, command: any) => {
			return await targetObject.setVerticalItemAlignment(command.verticalItemAlignment);
		},
		'UiInfiniteItemView.setContextMenuContent': async (targetObject: any, command: any) => {
			return await targetObject.setContextMenuContent(command.requestId, command.component);
		},
		'UiInfiniteItemView.closeContextMenu': async (targetObject: any, command: any) => {
			return await targetObject.closeContextMenu(command.requestId);
		},
		'UiInfiniteItemView2.setData': async (targetObject: any, command: any) => {
			return await targetObject.setData(command.startIndex, command.recordIds, command.newRecords, command.totalNumberOfRecords);
		},
		'UiInfiniteItemView2.setItemTemplate': async (targetObject: any, command: any) => {
			return await targetObject.setItemTemplate(command.itemTemplate);
		},
		'UiInfiniteItemView2.setItemWidth': async (targetObject: any, command: any) => {
			return await targetObject.setItemWidth(command.itemWidth);
		},
		'UiInfiniteItemView2.setItemHeight': async (targetObject: any, command: any) => {
			return await targetObject.setItemHeight(command.itemHeight);
		},
		'UiInfiniteItemView2.setHorizontalSpacing': async (targetObject: any, command: any) => {
			return await targetObject.setHorizontalSpacing(command.horizontalSpacing);
		},
		'UiInfiniteItemView2.setVerticalSpacing': async (targetObject: any, command: any) => {
			return await targetObject.setVerticalSpacing(command.verticalSpacing);
		},
		'UiInfiniteItemView2.setItemContentHorizontalAlignment': async (targetObject: any, command: any) => {
			return await targetObject.setItemContentHorizontalAlignment(command.itemContentHorizontalAlignment);
		},
		'UiInfiniteItemView2.setItemContentVerticalAlignment': async (targetObject: any, command: any) => {
			return await targetObject.setItemContentVerticalAlignment(command.itemContentVerticalAlignment);
		},
		'UiInfiniteItemView2.setRowHorizontalAlignment': async (targetObject: any, command: any) => {
			return await targetObject.setRowHorizontalAlignment(command.rowHorizontalAlignment);
		},
		'UiInfiniteItemView2.setItemPositionAnimationTime': async (targetObject: any, command: any) => {
			return await targetObject.setItemPositionAnimationTime(command.animationMillis);
		},
		'UiInfiniteItemView2.setContextMenuContent': async (targetObject: any, command: any) => {
			return await targetObject.setContextMenuContent(command.requestId, command.component);
		},
		'UiInfiniteItemView2.closeContextMenu': async (targetObject: any, command: any) => {
			return await targetObject.closeContextMenu(command.requestId);
		},
		'UiGridForm.updateLayoutPolicies': async (targetObject: any, command: any) => {
			return await targetObject.updateLayoutPolicies(command.layoutPolicies);
		},
		'UiGridForm.setSectionCollapsed': async (targetObject: any, command: any) => {
			return await targetObject.setSectionCollapsed(command.sectionId, command.collapsed);
		},
		'UiGridForm.addOrReplaceField': async (targetObject: any, command: any) => {
			return await targetObject.addOrReplaceField(command.field);
		},
		'UiDocumentViewer.setPageUrls': async (targetObject: any, command: any) => {
			return await targetObject.setPageUrls(command.pageUrls);
		},
		'UiDocumentViewer.setDisplayMode': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayMode(command.displayMode, command.zoomFactor);
		},
		'UiDocumentViewer.setZoomFactor': async (targetObject: any, command: any) => {
			return await targetObject.setZoomFactor(command.zoomFactor);
		},
		'UiDocumentViewer.setPageBorder': async (targetObject: any, command: any) => {
			return await targetObject.setPageBorder(command.pageBorder);
		},
		'UiDocumentViewer.setPageShadow': async (targetObject: any, command: any) => {
			return await targetObject.setPageShadow(command.pageShadow);
		},
		'UiDocumentViewer.setPaddding': async (targetObject: any, command: any) => {
			return await targetObject.setPaddding(command.padding);
		},
		'UiDocumentViewer.setPageSpacing': async (targetObject: any, command: any) => {
			return await targetObject.setPageSpacing(command.pageSpacing);
		},
		'UiToolButton.setIcon': async (targetObject: any, command: any) => {
			return await targetObject.setIcon(command.icon);
		},
		'UiToolButton.setPopoverText': async (targetObject: any, command: any) => {
			return await targetObject.setPopoverText(command.popoverText);
		},
		'UiToolButton.setGrayOutIfNotHovered': async (targetObject: any, command: any) => {
			return await targetObject.setGrayOutIfNotHovered(command.grayOutIfNotHovered);
		},
		'UiToolButton.setDropDownSize': async (targetObject: any, command: any) => {
			return await targetObject.setDropDownSize(command.minDropDownWidth, command.minDropDownHeight);
		},
		'UiToolButton.setOpenDropDownIfNotSet': async (targetObject: any, command: any) => {
			return await targetObject.setOpenDropDownIfNotSet(command.openDropDownIfNotSet);
		},
		'UiToolButton.setDropDownComponent': async (targetObject: any, command: any) => {
			return await targetObject.setDropDownComponent(command.dropDownComponent);
		},
		'UiComponent.setVisible': async (targetObject: any, command: any) => {
			return await targetObject.setVisible(command.visible);
		},
		'UiComponent.setStyle': async (targetObject: any, command: any) => {
			return await targetObject.setStyle(command.selector, command.styles);
		},
		'UiComponent.setClassNames': async (targetObject: any, command: any) => {
			return await targetObject.setClassNames(command.selector, command.classNames);
		},
		'UiComponent.setAttributes': async (targetObject: any, command: any) => {
			return await targetObject.setAttributes(command.selector, command.attributes);
		},
		'UiCurrencyField.setLocale': async (targetObject: any, command: any) => {
			return await targetObject.setLocale(command.locale);
		},
		'UiCurrencyField.setCurrencyUnits': async (targetObject: any, command: any) => {
			return await targetObject.setCurrencyUnits(command.currencyUnits);
		},
		'UiCurrencyField.setFixedPrecision': async (targetObject: any, command: any) => {
			return await targetObject.setFixedPrecision(command.fixedPrecision);
		},
		'UiCurrencyField.setShowCurrencyBeforeAmount': async (targetObject: any, command: any) => {
			return await targetObject.setShowCurrencyBeforeAmount(command.showCurrencyBeforeAmount);
		},
		'UiCurrencyField.setShowCurrencySymbol': async (targetObject: any, command: any) => {
			return await targetObject.setShowCurrencySymbol(command.showCurrencySymbol);
		},
		'UiRichTextEditor.setMinHeight': async (targetObject: any, command: any) => {
			return await targetObject.setMinHeight(command.minHeight);
		},
		'UiRichTextEditor.setMaxHeight': async (targetObject: any, command: any) => {
			return await targetObject.setMaxHeight(command.maxHeight);
		},
		'UiRichTextEditor.setUploadUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUploadUrl(command.uploadUrl);
		},
		'UiRichTextEditor.setMaxImageFileSizeInBytes': async (targetObject: any, command: any) => {
			return await targetObject.setMaxImageFileSizeInBytes(command.maxImageFileSizeInBytes);
		},
		'UiRichTextEditor.setUploadedImageUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUploadedImageUrl(command.fileUuid, command.url);
		},
		'UiRichTextEditor.setToolbarVisibilityMode': async (targetObject: any, command: any) => {
			return await targetObject.setToolbarVisibilityMode(command.toolbarVisibilityMode);
		},
		'UiTemplateField.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiPictureChooser.setBrowseButtonIcon': async (targetObject: any, command: any) => {
			return await targetObject.setBrowseButtonIcon(command.browseButtonIcon);
		},
		'UiPictureChooser.setUploadUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUploadUrl(command.uploadUrl);
		},
		'UiPictureChooser.setMaxFileSize': async (targetObject: any, command: any) => {
			return await targetObject.setMaxFileSize(command.maxFileSize);
		},
		'UiPictureChooser.setFileTooLargeMessage': async (targetObject: any, command: any) => {
			return await targetObject.setFileTooLargeMessage(command.fileTooLargeMessage);
		},
		'UiPictureChooser.setUploadErrorMessage': async (targetObject: any, command: any) => {
			return await targetObject.setUploadErrorMessage(command.uploadErrorMessage);
		},
		'UiPictureChooser.cancelUpload': async (targetObject: any, command: any) => {
			return await targetObject.cancelUpload();
		},
		'UiField.setEditingMode': async (targetObject: any, command: any) => {
			return await targetObject.setEditingMode(command.editingMode);
		},
		'UiField.setValue': async (targetObject: any, command: any) => {
			return await targetObject.setValue(command.value);
		},
		'UiField.focus': async (targetObject: any, command: any) => {
			return await targetObject.focus();
		},
		'UiField.setFieldMessages': async (targetObject: any, command: any) => {
			return await targetObject.setFieldMessages(command.fieldMessages);
		},
		'UiImageField.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiFileField.replaceFileItem': async (targetObject: any, command: any) => {
			return await targetObject.replaceFileItem(command.fileItemUuid, command.data);
		},
		'UiFileField.setItemTemplate': async (targetObject: any, command: any) => {
			return await targetObject.setItemTemplate(command.itemTemplate);
		},
		'UiFileField.setMaxBytesPerFile': async (targetObject: any, command: any) => {
			return await targetObject.setMaxBytesPerFile(command.maxBytesPerFile);
		},
		'UiFileField.setUploadUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUploadUrl(command.uploadUrl);
		},
		'UiFileField.setDisplayType': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayType(command.displayType);
		},
		'UiFileField.setMaxFiles': async (targetObject: any, command: any) => {
			return await targetObject.setMaxFiles(command.maxFiles);
		},
		'UiFileField.setUploadButtonTemplate': async (targetObject: any, command: any) => {
			return await targetObject.setUploadButtonTemplate(command.uploadButtonTemplate);
		},
		'UiFileField.setUploadButtonData': async (targetObject: any, command: any) => {
			return await targetObject.setUploadButtonData(command.uploadButtonData);
		},
		'UiFileField.setShowEntriesAsButtonsOnHover': async (targetObject: any, command: any) => {
			return await targetObject.setShowEntriesAsButtonsOnHover(command.showEntriesAsButtonsOnHover);
		},
		'UiFileField.cancelAllUploads': async (targetObject: any, command: any) => {
			return await targetObject.cancelAllUploads();
		},
		'UiFileField.cancelUpload': async (targetObject: any, command: any) => {
			return await targetObject.cancelUpload(command.fileItemUuid);
		},
		'UiSimpleFileField.addFileItem': async (targetObject: any, command: any) => {
			return await targetObject.addFileItem(command.item);
		},
		'UiSimpleFileField.updateFileItem': async (targetObject: any, command: any) => {
			return await targetObject.updateFileItem(command.item);
		},
		'UiSimpleFileField.removeFileItem': async (targetObject: any, command: any) => {
			return await targetObject.removeFileItem(command.itemUuid);
		},
		'UiSimpleFileField.setBrowseButtonIcon': async (targetObject: any, command: any) => {
			return await targetObject.setBrowseButtonIcon(command.browseButtonIcon);
		},
		'UiSimpleFileField.setBrowseButtonCaption': async (targetObject: any, command: any) => {
			return await targetObject.setBrowseButtonCaption(command.browseButtonCaption);
		},
		'UiSimpleFileField.setUploadUrl': async (targetObject: any, command: any) => {
			return await targetObject.setUploadUrl(command.uploadUrl);
		},
		'UiSimpleFileField.setMaxBytesPerFile': async (targetObject: any, command: any) => {
			return await targetObject.setMaxBytesPerFile(command.maxBytesPerFile);
		},
		'UiSimpleFileField.setFileTooLargeMessage': async (targetObject: any, command: any) => {
			return await targetObject.setFileTooLargeMessage(command.fileTooLargeMessage);
		},
		'UiSimpleFileField.setUploadErrorMessage': async (targetObject: any, command: any) => {
			return await targetObject.setUploadErrorMessage(command.uploadErrorMessage);
		},
		'UiSimpleFileField.setMaxFiles': async (targetObject: any, command: any) => {
			return await targetObject.setMaxFiles(command.maxFiles);
		},
		'UiSimpleFileField.setDisplayMode': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayMode(command.displayType);
		},
		'UiButton.setTemplate': async (targetObject: any, command: any) => {
			return await targetObject.setTemplate(command.template, command.templateRecord);
		},
		'UiButton.setTemplateRecord': async (targetObject: any, command: any) => {
			return await targetObject.setTemplateRecord(command.templateRecord);
		},
		'UiButton.setDropDownSize': async (targetObject: any, command: any) => {
			return await targetObject.setDropDownSize(command.minDropDownWidth, command.minDropDownHeight);
		},
		'UiButton.setOpenDropDownIfNotSet': async (targetObject: any, command: any) => {
			return await targetObject.setOpenDropDownIfNotSet(command.openDropDownIfNotSet);
		},
		'UiButton.setDropDownComponent': async (targetObject: any, command: any) => {
			return await targetObject.setDropDownComponent(command.dropDownComponent);
		},
		'UiButton.setOnClickJavaScript': async (targetObject: any, command: any) => {
			return await targetObject.setOnClickJavaScript(command.onClickJavaScript);
		},
		'UiLabel.setCaption': async (targetObject: any, command: any) => {
			return await targetObject.setCaption(command.caption);
		},
		'UiLabel.setIcon': async (targetObject: any, command: any) => {
			return await targetObject.setIcon(command.icon);
		},
		'UiLabel.setTargetComponent': async (targetObject: any, command: any) => {
			return await targetObject.setTargetComponent(command.targetField);
		},
		'UiCheckBox.setCaption': async (targetObject: any, command: any) => {
			return await targetObject.setCaption(command.caption);
		},
		'UiCheckBox.setBackgroundColor': async (targetObject: any, command: any) => {
			return await targetObject.setBackgroundColor(command.backgroundColor);
		},
		'UiCheckBox.setCheckColor': async (targetObject: any, command: any) => {
			return await targetObject.setCheckColor(command.checkColor);
		},
		'UiCheckBox.setBorderColor': async (targetObject: any, command: any) => {
			return await targetObject.setBorderColor(command.borderColor);
		},
		'UiSlider.setMin': async (targetObject: any, command: any) => {
			return await targetObject.setMin(command.min);
		},
		'UiSlider.setMax': async (targetObject: any, command: any) => {
			return await targetObject.setMax(command.max);
		},
		'UiSlider.setStep': async (targetObject: any, command: any) => {
			return await targetObject.setStep(command.step);
		},
		'UiSlider.setDisplayedDecimals': async (targetObject: any, command: any) => {
			return await targetObject.setDisplayedDecimals(command.displayedDecimals);
		},
		'UiSlider.setSelectionColor': async (targetObject: any, command: any) => {
			return await targetObject.setSelectionColor(command.selectionColor);
		},
		'UiSlider.setTooltipPrefix': async (targetObject: any, command: any) => {
			return await targetObject.setTooltipPrefix(command.tooltipPrefi);
		},
		'UiSlider.setTooltipPostfix': async (targetObject: any, command: any) => {
			return await targetObject.setTooltipPostfix(command.tooltipPostfi);
		},
		'UiSlider.setHumanReadableFileSize': async (targetObject: any, command: any) => {
			return await targetObject.setHumanReadableFileSize(command.humanReadableFileSize);
		},
		'UiTextField.setMaxCharacters': async (targetObject: any, command: any) => {
			return await targetObject.setMaxCharacters(command.maxCharacters);
		},
		'UiTextField.setShowClearButton': async (targetObject: any, command: any) => {
			return await targetObject.setShowClearButton(command.showClearButton);
		},
		'UiTextField.setPlaceholderText': async (targetObject: any, command: any) => {
			return await targetObject.setPlaceholderText(command.placeholderText);
		},
		'UiPasswordField.setSendValueAsMd5': async (targetObject: any, command: any) => {
			return await targetObject.setSendValueAsMd5(command.sendValueAsMd5);
		},
		'UiPasswordField.setSalt': async (targetObject: any, command: any) => {
			return await targetObject.setSalt(command.salt);
		},
		'UiMultiLineTextField.append': async (targetObject: any, command: any) => {
			return await targetObject.append(command.s, command.scrollToBottom);
		},
		'UiLocalDateField.update': async (targetObject: any, command: any) => {
			return await targetObject.update(command.config);
		},
		'UiNumberField.setMinValue': async (targetObject: any, command: any) => {
			return await targetObject.setMinValue(command.min);
		},
		'UiNumberField.setMaxValue': async (targetObject: any, command: any) => {
			return await targetObject.setMaxValue(command.max);
		},
		'UiNumberField.setSliderMode': async (targetObject: any, command: any) => {
			return await targetObject.setSliderMode(command.sliderMode);
		},
		'UiNumberField.setSliderStep': async (targetObject: any, command: any) => {
			return await targetObject.setSliderStep(command.step);
		},
		'UiNumberField.setCommitOnSliderChange': async (targetObject: any, command: any) => {
			return await targetObject.setCommitOnSliderChange(command.commitOnSliderChange);
		},
		'UiNumberField.setPrecision': async (targetObject: any, command: any) => {
			return await targetObject.setPrecision(command.displayPrecision);
		},
		'UiNumberField.setPlaceholderText': async (targetObject: any, command: any) => {
			return await targetObject.setPlaceholderText(command.placeholderText);
		},
		'UiNumberField.setShowClearButton': async (targetObject: any, command: any) => {
			return await targetObject.setShowClearButton(command.showClearButton);
		},
		'UiNumberField.setLocale': async (targetObject: any, command: any) => {
			return await targetObject.setLocale(command.locale);
		},
		'AbstractUiDateTimeField.setShowDropDownButton': async (targetObject: any, command: any) => {
			return await targetObject.setShowDropDownButton(command.showDropDownButton);
		},
		'AbstractUiDateTimeField.setFavorPastDates': async (targetObject: any, command: any) => {
			return await targetObject.setFavorPastDates(command.favorPastDates);
		},
		'AbstractUiDateTimeField.setLocaleAndFormats': async (targetObject: any, command: any) => {
			return await targetObject.setLocaleAndFormats(command.locale, command.dateFormat, command.timeFormat);
		},
		'UiInstantDateTimeField.setTimeZoneId': async (targetObject: any, command: any) => {
			return await targetObject.setTimeZoneId(command.timeZoneId);
		},
		'UiDisplayField.setShowBorder': async (targetObject: any, command: any) => {
			return await targetObject.setShowBorder(command.showBorder);
		},
		'UiDisplayField.setShowHtml': async (targetObject: any, command: any) => {
			return await targetObject.setShowHtml(command.showHtml);
		},
		'UiDisplayField.setRemoveStyleTags': async (targetObject: any, command: any) => {
			return await targetObject.setRemoveStyleTags(command.removeStyleTags);
		},
		'UiComboBox.registerTemplate': async (targetObject: any, command: any) => {
			return await targetObject.registerTemplate(command.id, command.template);
		},
		'UiComboBox.replaceFreeTextEntry': async (targetObject: any, command: any) => {
			return await targetObject.replaceFreeTextEntry(command.freeText, command.newEntry);
		},
		'AbstractUiTimeField.setShowDropDownButton': async (targetObject: any, command: any) => {
			return await targetObject.setShowDropDownButton(command.showDropDownButton);
		},
		'AbstractUiTimeField.setShowClearButton': async (targetObject: any, command: any) => {
			return await targetObject.setShowClearButton(command.showClearButton);
		},
		'AbstractUiTimeField.setLocaleAndTimeFormat': async (targetObject: any, command: any) => {
			return await targetObject.setLocaleAndTimeFormat(command.locale, command.timeFormat);
		},
		'UiComponentField.setComponent': async (targetObject: any, command: any) => {
			return await targetObject.setComponent(command.component);
		},
		'UiComponentField.setHeight': async (targetObject: any, command: any) => {
			return await targetObject.setHeight(command.height);
		},
		'UiComponentField.setBordered': async (targetObject: any, command: any) => {
			return await targetObject.setBordered(command.bordered);
		},
		'UiChatDisplay.addMessages': async (targetObject: any, command: any) => {
			return await targetObject.addMessages(command.messages);
		},
		'UiChatDisplay.updateMessage': async (targetObject: any, command: any) => {
			return await targetObject.updateMessage(command.message);
		},
		'UiChatDisplay.deleteMessage': async (targetObject: any, command: any) => {
			return await targetObject.deleteMessage(command.messageId);
		},
		'UiChatDisplay.clearMessages': async (targetObject: any, command: any) => {
			return await targetObject.clearMessages(command.messages);
		},
		'UiChatDisplay.closeContextMenu': async (targetObject: any, command: any) => {
			return await targetObject.closeContextMenu();
		},
		'UiChatInput.setAttachmentsEnabled': async (targetObject: any, command: any) => {
			return await targetObject.setAttachmentsEnabled(command.attachmentsEnabled);
		}
	};

	private staticCommandExecutorsByCommandName: {[commandName: string]: (command: any, context: TeamAppsUiContext) => void} = {
		'UiMediaSoupV3WebRtcClient.enumerateDevices': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiMediaSoupV3WebRtcClient") as any).enumerateDevices(context);
		},
		'UiShakaPlayer.setDistinctManifestAudioTracksFixEnabled': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiShakaPlayer") as any).setDistinctManifestAudioTracksFixEnabled(command.enabled, context);
		},
		'UiWebRtcPublisher.isChromeExtensionInstalled': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiWebRtcPublisher") as any).isChromeExtensionInstalled(context);
		},
		'UiWebRtcPlayer.getPlayableVideoCodecs': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiWebRtcPlayer") as any).getPlayableVideoCodecs(context);
		},
		'UiRootPanel.setGlobalKeyEventsEnabled': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setGlobalKeyEventsEnabled(command.unmodified, command.modifiedWithAltKey, command.modifiedWithCtrlKey, command.modifiedWithMetaKey, command.includeRepeats, command.keyDown, command.keyUp, context);
		},
		'UiRootPanel.createComponent': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).createComponent(command.component, context);
		},
		'UiRootPanel.destroyComponent': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).destroyComponent(command.id, context);
		},
		'UiRootPanel.refreshComponent': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).refreshComponent(command.component, context);
		},
		'UiRootPanel.setConfig': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setConfig(command.config, context);
		},
		'UiRootPanel.setThemeClassName': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setThemeClassName(command.theme, context);
		},
		'UiRootPanel.setSessionMessageWindows': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setSessionMessageWindows(command.expiredMessageWindow, command.errorMessageWindow, command.terminatedMessageWindow, context);
		},
		'UiRootPanel.setPageTitle': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setPageTitle(command.pageTitle, context);
		},
		'UiRootPanel.buildRootPanel': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).buildRootPanel(command.containerElementId, command.uiRootPanel, context);
		},
		'UiRootPanel.registerTemplate': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).registerTemplate(command.id, command.template, context);
		},
		'UiRootPanel.registerTemplates': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).registerTemplates(command.templates, context);
		},
		'UiRootPanel.addClientToken': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).addClientToken(command.token, context);
		},
		'UiRootPanel.removeClientToken': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).removeClientToken(command.token, context);
		},
		'UiRootPanel.clearClientTokens': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).clearClientTokens(context);
		},
		'UiRootPanel.downloadFile': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).downloadFile(command.fileUrl, command.downloadFileName, context);
		},
		'UiRootPanel.registerBackgroundImage': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).registerBackgroundImage(command.id, command.image, command.blurredImage, context);
		},
		'UiRootPanel.setBackgroundImage': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setBackgroundImage(command.id, command.animationDuration, context);
		},
		'UiRootPanel.setBackgroundColor': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setBackgroundColor(command.color, command.animationDuration, context);
		},
		'UiRootPanel.exitFullScreen': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).exitFullScreen(context);
		},
		'UiRootPanel.showNotification': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).showNotification(command.notification, command.position, command.entranceAnimation, command.exitAnimation, context);
		},
		'UiRootPanel.showDialogMessage': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).showDialogMessage(command.icon, command.title, command.message, command.options, context);
		},
		'UiRootPanel.showPopup': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).showPopup(command.popup, context);
		},
		'UiRootPanel.showPopupAtCurrentMousePosition': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).showPopupAtCurrentMousePosition(command.popup, context);
		},
		'UiRootPanel.requestWakeLock': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).requestWakeLock(command.uuid, context);
		},
		'UiRootPanel.releaseWakeLock': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).releaseWakeLock(command.uuid, context);
		},
		'UiRootPanel.goToUrl': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).goToUrl(command.url, command.blankPage, context);
		},
		'UiRootPanel.pushHistoryState': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).pushHistoryState(command.relativeUrl, context);
		},
		'UiRootPanel.navigateForward': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).navigateForward(command.steps, context);
		},
		'UiRootPanel.setFavicon': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setFavicon(command.url, context);
		},
		'UiRootPanel.setTitle': async (command: any, context: TeamAppsUiContext) => {
			return await (TeamAppsUiComponentRegistry.getComponentClassForName("UiRootPanel") as any).setTitle(command.title, context);
		}
	};

	public async executeCommand(targetObject: any, command: any) {
		return await this.commandExecutorsByCommandName[command._type](targetObject, command);
	}

	public async executeStaticCommand(command: any, context: TeamAppsUiContext) {
		return await this.staticCommandExecutorsByCommandName[command._type](command, context);
	}
}