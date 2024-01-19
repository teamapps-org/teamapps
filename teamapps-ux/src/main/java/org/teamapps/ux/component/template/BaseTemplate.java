/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.template;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.UiTemplate;
import org.teamapps.dto.UiTemplateReference;
import org.teamapps.ux.component.format.*;
import org.teamapps.ux.component.template.gridtemplate.*;

import java.util.List;

public enum BaseTemplate implements Template {

	TOOL_BUTTON(createToolButtonTemplate()),

	TOOLBAR_BUTTON(createToolbarButtonTemplate(40, 32, 1f, 0.7f)),
	TOOLBAR_BUTTON_SMALL(createToolbarButtonTemplate(30, 24, 0.9f, 0.7f)),
	TOOLBAR_BUTTON_TINY(createToolbarButtonTinyTemplate()),

	TOOLBAR_MENU_GROUP_HEADER(createToolbarMenuGroupHeader()),
	TOOLBAR_MENU_BIG_BUTTON(createToolbarMenuBigButtonTemplate()),

	ITEM_VIEW_ITEM(createItemViewTemplate()),
	APPLICATION_LISTING(createApplicationListingTemplate()),

	FILE_ITEM_FLOATING(createFloatingFileItemTemplate()),
	FILE_ITEM_LIST(createListFileItemTemplate()),

	LIST_ITEM_SMALL_ICON_SINGLE_LINE(createTreeSingleLineNodeTemplate(16, VerticalElementAlignment.CENTER, 24)),
	LIST_ITEM_MEDIUM_ICON_SINGLE_LINE(createTreeSingleLineNodeTemplate(24, VerticalElementAlignment.CENTER, 32)),
	LIST_ITEM_LARGE_ICON_SINGLE_LINE(createTreeSingleLineNodeTemplate(32, VerticalElementAlignment.CENTER, 44)),

	LIST_ITEM_EXTRA_VERY_LARGE_ICON_TWO_LINES(createListStyleIconTwoLinesBadgeTemplate(64, VerticalElementAlignment.CENTER, 100, 3)),
	LIST_ITEM_VERY_LARGE_ICON_TWO_LINES(createListStyleIconTwoLinesBadgeTemplate(48, VerticalElementAlignment.CENTER, 100, 3)),
	LIST_ITEM_LARGE_ICON_TWO_LINES(createListStyleIconTwoLinesBadgeTemplate(32, VerticalElementAlignment.CENTER, 60, 2)),
	LIST_ITEM_MEDIUM_ICON_TWO_LINES(createListStyleIconTwoLinesBadgeTemplate(24, VerticalElementAlignment.CENTER, 50, 1)),

	MENU_ITEM(createListStyleIconTwoLinesBadgeTemplate(32, VerticalElementAlignment.CENTER, 75, 10)),

	FORM_SECTION_HEADER(createFormSectionHeaderTemplate()),

	BUTTON(createFormButtonTemplate(16, 1)),
	BUTTON_LARGE(createFormButtonTemplate(24, 1.6f)),
	BUTTON_XLARGE(createFormButtonTemplate(32, 2.2f)),

	NOTIFICATION_ICON_CAPTION(createNotificationTemplateWithIconAndCaption()),
	NOTIFICATION_ICON_CAPTION_DESCRIPTION(createNotificationTemplateWithIconAndCaptionAndDescription()),

	NAVIGATION_BAR_ICON_ONLY(createNavigationBarIconOnlyTemplate());

	public static final String PROPERTY_ICON = "icon";
	public static final String PROPERTY_IMAGE = "image";
	public static final String PROPERTY_CAPTION = "caption";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_BADGE = "badge";
	public static final String PROPERTY_ARIA_LABEL = "ariaLabel";
	public static final String PROPERTY_TITLE = "title";

	private static Template createToolbarButtonTemplate(int minWidth, int iconSize, float captionFontSize, float descriptionFontSize) {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(minWidth)
				.setPadding(new Spacing(0))
				.addColumn(SizingPolicy.AUTO, 2, 2)
				.addRow(SizeType.FIXED, iconSize, iconSize, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, iconSize)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, iconSize, iconSize)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 1, 0)
						.setTextAlignment(TextAlignment.CENTER)
						.setWrapLines(true)
						.setFontStyle(captionFontSize)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 2, 0)
						.setTextAlignment(TextAlignment.CENTER)
						.setWrapLines(true)
						.setFontStyle(descriptionFontSize, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER));
	}

	private static Template createToolbarButtonTinyTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 0, 2, 2)
				.addColumn(SizingPolicy.FRACTION)
				.addRow(SizeType.AUTO, 0, 0, 2, 2)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 20).setMargin(new Spacing(0, 2, 0, 0)))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 20, 20)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setMargin(new Spacing(0, 2, 0, 0)))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setTextAlignment(TextAlignment.CENTER)
						.setWrapLines(false)
						.setPadding(new Spacing(0, 2, 0, 0)));
	}

	private static Template createToolButtonTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.addColumn(SizingPolicy.AUTO)
				.addRow(SizingPolicy.AUTO)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 12))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 12, 12));
	}

	private static Template createToolbarMenuGroupHeader() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinHeight(20).setMaxHeight(20)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 0, 2, 2)
				.addColumn(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 16))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 16, 16)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(false));
	}

	private static Template createToolbarMenuBigButtonTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinHeight(44).setMaxHeight(100)
				.setGridGap(0)
				.setPadding(new Spacing(4))
				.addColumn(SizeType.AUTO, 0, 0, 3, 3)
				.addColumn(SizeType.AUTO, 0, 0, 0, 3)
				.addRow(SizeType.AUTO, 0, 0, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 2, 2)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(true))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1).setWrapLines(true).setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray")));
	}

	public static Template createListStyleIconTwoLinesBadgeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, int spacing) {
		return createListStyleIconTwoLinesBadgeTemplate(iconSize, verticalIconAlignment, maxHeight, spacing, true);
	}

	public static Template createListStyleIconTwoLinesBadgeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, int spacing, boolean wrapLines) {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMaxHeight(maxHeight)
				.setPadding(new Spacing(spacing))
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addColumn(SizingPolicy.FRACTION)
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, iconSize)
						.setRowSpan(2)
						.setVerticalAlignment(verticalIconAlignment)
						.setMargin(new Spacing(0, 4, 0, 0)))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, iconSize, iconSize).setRowSpan(2)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(0.5f))
						.setVerticalAlignment(verticalIconAlignment)
						.setMargin(new Spacing(0, 4, 0, 0)))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(wrapLines)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1)
						.setColSpan(2)
						.setWrapLines(wrapLines)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new BadgeElement(PROPERTY_BADGE, 0, 2)
						.setFontStyle(new FontStyle().setFontColor(RgbaColor.WHITE).setBackgroundColor(RgbaColor.DEEP_SKY_BLUE))
						.setWrapLines(wrapLines)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.RIGHT)
						.setMargin(new Spacing(0, 0, 0, 3)));
	}

	public static Template createTreeSingleLineNodeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight) {
		return createTreeSingleLineNodeTemplate(iconSize, verticalIconAlignment, maxHeight, false);
	}

	public static Template createTreeSingleLineNodeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, boolean wrapLines) {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinHeight(16).setMaxHeight(maxHeight)
				.setPadding(new Spacing(2))
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addColumn(SizingPolicy.FRACTION)
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addRow(SizingPolicy.AUTO)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, iconSize)
						.setVerticalAlignment(verticalIconAlignment)
						.setMargin(new Spacing(0, 3, 0, 0)))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, iconSize, iconSize)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						//todo: shadow only if size of image large enough
						.setShadow(Shadow.withSize(0.5f))
						.setVerticalAlignment(verticalIconAlignment)
						.setMargin(new Spacing(0, 3, 0, 0)))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(wrapLines)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				)
				.addElement(new BadgeElement(PROPERTY_BADGE, 0, 2)
						.setFontStyle(new FontStyle().setFontColor(RgbaColor.WHITE).setBackgroundColor(RgbaColor.DEEP_SKY_BLUE))
						.setWrapLines(wrapLines)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.RIGHT)
						.setMargin(new Spacing(0, 0, 0, 3)));
	}

	private static Template createFormSectionHeaderTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinHeight(28).setMaxHeight(28)
				.setGridGap(4)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 0, 2, 2)
				.addColumn(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 24))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 24, 24)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setFontStyle(1.15f)
						.setWrapLines(false));
	}

	public static Template createFormButtonTemplate(int iconSize, float relativeFontSize) {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinHeight(20)
				.setGridGap(0)
				.setPadding(new Spacing(1, 5))
				.addColumn(SizeType.AUTO, 0, 0, 2, 4)
				.addColumn(SizingPolicy.AUTO, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 3, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, iconSize))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, iconSize, iconSize)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setFontStyle(relativeFontSize)
						.setWrapLines(false));
	}

	private static Template createNotificationTemplateWithIconAndCaption() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setGridGap(5)
				.setPadding(new Spacing(5))
				.addColumn(SizeType.AUTO, 0, 0, 0, 0)
				.addColumn(SizingPolicy.FRACTION)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				)
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				)
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(true)
				);
	}

	private static Template createNotificationTemplateWithIconAndCaptionAndDescription() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setGridGap(3)
				.setPadding(new Spacing(6))
				.addColumn(SizeType.AUTO, 0, 0, 0, 0)
				.addColumn(SizingPolicy.FRACTION)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32)
						.setRowSpan(2)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setMargin(new Spacing(0, 5, 0, 0))
				)
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setRowSpan(2)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setMargin(new Spacing(0, 5, 0, 0))
				)
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setFontStyle(new FontStyle().setBold(true))
						.setWrapLines(true)
				)
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1)
						.setWrapLines(true)
				);
	}

	private static Template createItemViewTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setGridGap(0)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 40, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addRow(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER)
						.setMargin(new Spacing(0, 0, 2, 0))
				)
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setMargin(new Spacing(0, 0, 2, 0))
				)
				.addElement(new TextElement(PROPERTY_CAPTION, 1, 0)
						.setTextAlignment(TextAlignment.CENTER)
						.setWrapLines(true)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 2, 0)
						.setTextAlignment(TextAlignment.CENTER)
						.setWrapLines(true)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER));
	}

	private static Template createFloatingFileItemTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMaxWidth(140)
				.setGridGap(0)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 40, 2, 2)
				.addRow(SizeType.AUTO, 32, 32, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 1, 0)
						.setWrapLines(true)
						.setTextAlignment(TextAlignment.CENTER)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 2, 0)
						.setWrapLines(true)
						.setTextAlignment(TextAlignment.CENTER)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER));
	}

	private static Template createListFileItemTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setGridGap(0)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.FIXED, 32, 0, 0, 0)
				.addColumn(SizeType.AUTO, 0, 0, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 0, 0)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 32)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setRowSpan(2))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 32, 32)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setRowSpan(2))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(true)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1).setWrapLines(true)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray")).setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT));
	}

	private static Template createApplicationListingTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setPadding(new Spacing(10))
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addColumn(SizingPolicy.FRACTION)
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addRow(SizeType.AUTO, 0, 0, 2, 2)
				.addRow(SizeType.AUTO, 0, 0, 2, 2)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 64)
						.setRowSpan(2)
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setMargin(new Spacing(0, 3, 0, 0)))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 64, 64).setRowSpan(2)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(Shadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setMargin(new Spacing(0, 3, 0, 0)))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(true)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1)
						.setColSpan(2)
						.setWrapLines(true)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new BadgeElement(PROPERTY_BADGE, 0, 2)
						.setFontStyle(new FontStyle().setFontColor(RgbaColor.WHITE).setBackgroundColor(RgbaColor.DEEP_SKY_BLUE))
						.setWrapLines(true)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(HorizontalElementAlignment.RIGHT)
						.setMargin(new Spacing(0, 0, 0, 3)));
	}

	private static Template createNavigationBarIconOnlyTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setPadding(new Spacing(2))
				.addColumn(new SizingPolicy(SizeType.FIXED, 24, 24))
				.addRow(new SizingPolicy(SizeType.FIXED, 24, 24))
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 24));
	}

	private final Template template;
	private final UiTemplateReference uiTemplateReference;

	BaseTemplate(Template template) {
		this.template = template;
		this.uiTemplateReference = new UiTemplateReference(name());
	}

	@Override
	public UiTemplate createUiTemplate() {
		return uiTemplateReference;
	}

	@Override
	public List<String> getPropertyNames() {
		return template.getPropertyNames();
	}

	public Template getTemplate() {
		return template;
	}

}
