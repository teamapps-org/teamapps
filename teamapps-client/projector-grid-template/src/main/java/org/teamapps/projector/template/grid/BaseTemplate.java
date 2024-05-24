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
package org.teamapps.projector.template.grid;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.format.*;

public class BaseTemplate {

	public static final GridTemplate TOOL_BUTTON = createToolButtonTemplate();

	public static final GridTemplate TOOLBAR_BUTTON = createToolbarButtonTemplate(40, 32, 1f, 0.7f);
	public static final GridTemplate TOOLBAR_BUTTON_SMALL = createToolbarButtonTemplate(30, 24, 0.9f, 0.7f);
	public static final GridTemplate TOOLBAR_BUTTON_TINY = createToolbarButtonTinyTemplate();

	public static final GridTemplate TOOLBAR_MENU_GROUP_HEADER = createToolbarMenuGroupHeader();
	public static final GridTemplate TOOLBAR_MENU_BIG_BUTTON = createToolbarMenuBigButtonTemplate();

	public static final GridTemplate ITEM_VIEW_ITEM = createItemViewTemplate();
	public static final GridTemplate APPLICATION_LISTING = createApplicationListingTemplate();

	public static final GridTemplate FILE_ITEM_FLOATING = createFloatingFileItemTemplate();
	public static final GridTemplate FILE_ITEM_LIST = createListFileItemTemplate();

	public static final GridTemplate LIST_ITEM_SMALL_ICON_SINGLE_LINE = createTreeSingleLineNodeTemplate(16, VerticalElementAlignment.CENTER, 24);
	public static final GridTemplate LIST_ITEM_MEDIUM_ICON_SINGLE_LINE = createTreeSingleLineNodeTemplate(24, VerticalElementAlignment.CENTER, 32);
	public static final GridTemplate LIST_ITEM_LARGE_ICON_SINGLE_LINE = createTreeSingleLineNodeTemplate(32, VerticalElementAlignment.CENTER, 44);

	public static final GridTemplate LIST_ITEM_EXTRA_VERY_LARGE_ICON_TWO_LINES = createListStyleIconTwoLinesBadgeTemplate(64, VerticalElementAlignment.CENTER, 100, 3);
	public static final GridTemplate LIST_ITEM_VERY_LARGE_ICON_TWO_LINES = createListStyleIconTwoLinesBadgeTemplate(48, VerticalElementAlignment.CENTER, 100, 3);
	public static final GridTemplate LIST_ITEM_LARGE_ICON_TWO_LINES = createListStyleIconTwoLinesBadgeTemplate(32, VerticalElementAlignment.CENTER, 60, 2);
	public static final GridTemplate LIST_ITEM_MEDIUM_ICON_TWO_LINES = createListStyleIconTwoLinesBadgeTemplate(24, VerticalElementAlignment.CENTER, 50, 1);

	public static final GridTemplate MENU_ITEM = createListStyleIconTwoLinesBadgeTemplate(32, VerticalElementAlignment.CENTER, 75, 10);

	public static final GridTemplate FORM_SECTION_HEADER = createFormSectionHeaderTemplate();

	public static final GridTemplate BUTTON = createFormButtonTemplate(16, 1);
	public static final GridTemplate BUTTON_LARGE = createFormButtonTemplate(24, 1.6f);
	public static final GridTemplate BUTTON_XLARGE = createFormButtonTemplate(32, 2.2f);

	public static final GridTemplate NOTIFICATION_ICON_CAPTION = createNotificationTemplateWithIconAndCaption();
	public static final GridTemplate NOTIFICATION_ICON_CAPTION_DESCRIPTION = createNotificationTemplateWithIconAndCaptionAndDescription();

	public static final GridTemplate NAVIGATION_BAR_ICON_ONLY = createNavigationBarIconOnlyTemplate();

	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_ICON = "icon";
	public static final String PROPERTY_IMAGE = "image";
	public static final String PROPERTY_CAPTION = "caption";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_BADGE = "badge";

	public static final String PROPERTY_CAPTION_ICON = "captionIcon";
	public static final String PROPERTY_RIGHT_ICON = "rightIcon";
	public static final String PROPERTY_SUB_TEXT1 = "setText1";
	public static final String PROPERTY_SUB_TEXT2 = "setText2";
	public static final String PROPERTY_SUB_ICON1 = "subIcon1";
	public static final String PROPERTY_SUB_ICON2 = "subIcon2";
	public static final String PROPERTY_ARIA_LABEL = "ariaLabel";
	public static final String PROPERTY_TITLE = "title";

	private static GridTemplate createToolbarButtonTemplate(int minWidth, int iconSize, float captionFontSize, float descriptionFontSize) {
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 1, 0)
						.setWrapLines(true)
						.setFontStyle(captionFontSize)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 2, 0)
						.setWrapLines(true)
						.setFontStyle(descriptionFontSize, Color.fromVariableName("ta-text-color-gray"))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.CENTER));
	}

	private static GridTemplate createToolbarButtonTinyTemplate() {
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setMargin(new Spacing(0, 2, 0, 0)))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(false)
						.setPadding(new Spacing(0, 2, 0, 0)));
	}

	private static GridTemplate createToolButtonTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.addColumn(SizingPolicy.AUTO)
				.addRow(SizingPolicy.AUTO)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 12))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 12, 12));
	}

	private static GridTemplate createToolbarMenuGroupHeader() {
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(false));
	}

	private static GridTemplate createToolbarMenuBigButtonTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(0).setMaxWidth(0)
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(true))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1).setWrapLines(true).setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray")));
	}

	public static GridTemplate createListStyleIconTwoLinesBadgeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, int spacing) {
		return createListStyleIconTwoLinesBadgeTemplate(iconSize, verticalIconAlignment, maxHeight, spacing, true);
	}

	public static GridTemplate createListStyleIconTwoLinesBadgeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, int spacing, boolean wrapLines) {
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
						.setShadow(BoxShadow.withSize(0.5f))
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

	public static GridTemplate createTemplate(int minWidth, int maxWidth, int minHeight, int maxHeight, Spacing padding, int gridGap, boolean horizontalCenterAlignment,
											  int mainIconSize, boolean mainIconAlignTop, Color mainImageBorderColor,
											  int captionIconSize, float captionSize, Color captionColor, boolean wrapCaption,
											  float badgeSize, Color badgeTextColor, Color badgeBackgroundColor, int rightIconSize,
											  int subIcon1Size, float subText1Size, Color subText1Color, int subIcon2Size, float subText2Size, Color subText2Color,
											  float descriptionTextSize, Color descriptionTextColor) {
		VerticalElementAlignment mainIconVerticalAlignment = mainIconAlignTop ? VerticalElementAlignment.TOP : VerticalElementAlignment.CENTER;

		HorizontalElementAlignment horizontalElementAlignment = horizontalCenterAlignment ? HorizontalElementAlignment.CENTER : HorizontalElementAlignment.LEFT;
		HorizontalElementAlignment horizontalElementAlignment2 = horizontalCenterAlignment ? HorizontalElementAlignment.CENTER : HorizontalElementAlignment.RIGHT;

		return new GridTemplate(minWidth, maxWidth, minHeight, maxHeight, padding, gridGap)
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addColumn(SizingPolicy.FRACTION)
				.addColumn(SizingPolicy.AUTO) // margin defined by badge, so no margin when no badge
				.addRow(SizeType.AUTO, 0, 0, 1, 0)
				.addRow(SizeType.AUTO, 0, 0, 1, 0)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)

				.addElement(new IconElement(PROPERTY_ICON, 0, 0, mainIconSize)
						.setRowSpan(3)
						.setVerticalAlignment(mainIconVerticalAlignment)
						.setMargin(new Spacing(0, 3, 0, 0)))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, mainIconSize, mainIconSize)
						.setRowSpan(3)
						.setBorder(new Border(new Line(mainImageBorderColor, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(BoxShadow.withSize(0.5f))
						.setVerticalAlignment(mainIconVerticalAlignment)
						.setMargin(new Spacing(0, 3, 0, 0)))

				.addElement(new FloatingElement(0, 1)
						.addElement(new IconElement(PROPERTY_CAPTION_ICON, captionIconSize)
								.setMargin(new Spacing(0, 2, 0, 0)))
						.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
								.setWrapLines(wrapCaption)
								.setFontStyle(captionSize, captionColor)
								.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
								.setHorizontalAlignment(horizontalElementAlignment))
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(horizontalElementAlignment)
				)
				.addElement(new FloatingElement(0, 2)
						.addElement(new BadgeElement(PROPERTY_BADGE)
								.setFontStyle(new FontStyle(badgeSize, badgeTextColor).setBackgroundColor(badgeBackgroundColor))
								.setWrapLines(true)
								.setHorizontalAlignment(horizontalElementAlignment2)
								.setMargin(new Spacing(0, 1, 0, 3))
						)
						.addElement(new IconElement(PROPERTY_RIGHT_ICON, rightIconSize)
								.setMargin(new Spacing(0, 1, 0, 3)))

						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(horizontalElementAlignment2)
				)

				.addElement(new FloatingElement(1, 1)
						.addElement(new IconElement(PROPERTY_SUB_ICON1, subIcon1Size)
								.setMargin(new Spacing(0, 2, 0, 0)))
						.addElement(new TextElement(PROPERTY_SUB_TEXT1)
								.setWrapLines(wrapCaption)
								.setFontStyle(subText1Size, subText1Color)
								.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
								.setHorizontalAlignment(horizontalElementAlignment))
						.addElement(new IconElement(PROPERTY_SUB_ICON2, subIcon2Size)
								.setMargin(new Spacing(0, 2, 0, 0)))
						.addElement(new TextElement(PROPERTY_SUB_TEXT2)
								.setWrapLines(wrapCaption)
								.setFontStyle(subText2Size, subText2Color)
								.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
								.setHorizontalAlignment(horizontalElementAlignment))

						.setVerticalAlignment(VerticalElementAlignment.BOTTOM)
						.setHorizontalAlignment(horizontalElementAlignment)
						.setColSpan(2)
				)

				.addElement(new TextElement(PROPERTY_DESCRIPTION, 2, 1)
						.setColSpan(2)
						.setWrapLines(true)
						.setFontStyle(descriptionTextSize, descriptionTextColor)
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(horizontalElementAlignment))
				;
	}

	public static GridTemplate createTreeSingleLineNodeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight) {
		return createTreeSingleLineNodeTemplate(iconSize, verticalIconAlignment, maxHeight, false);
	}

	public static GridTemplate createTreeSingleLineNodeTemplate(int iconSize, VerticalElementAlignment verticalIconAlignment, int maxHeight, boolean wrapLines) {
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
						.setShadow(BoxShadow.withSize(0.5f))
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

	private static GridTemplate createFormSectionHeaderTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(0).setMaxWidth(0)
				.setMinHeight(28).setMaxHeight(28)
				.setGridGap(4)
				.setPadding(new Spacing(0))
				.addColumn(SizeType.AUTO, 0, 0, 2, 2)
				.addColumn(SizeType.AUTO, 0, 0, 0, 2)
				.addRow(SizeType.AUTO, 0, 0, 1, 1)
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 24))
				.addElement(new ImageElement(PROPERTY_IMAGE, 0, 0, 24, 24)
						.setBorder(new Border(new Line(RgbaColor.GRAY, LineType.SOLID, 0.5f)).setBorderRadius(300))
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setFontStyle(1.15f)
						.setWrapLines(false));
	}

	public static GridTemplate createFormButtonTemplate(int iconSize, float relativeFontSize) {
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.TOP).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setFontStyle(relativeFontSize)
						.setWrapLines(false));
	}

	private static GridTemplate createNotificationTemplateWithIconAndCaption() {
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				)
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1)
						.setWrapLines(true)
				);
	}

	private static GridTemplate createNotificationTemplateWithIconAndCaptionAndDescription() {
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
						.setShadow(BoxShadow.withSize(1.5f))
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

	private static GridTemplate createItemViewTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(0).setMaxWidth(0)
				.setMinHeight(0).setMaxHeight(0)
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
						.setShadow(BoxShadow.withSize(1.5f))
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

	private static GridTemplate createFloatingFileItemTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(0).setMaxWidth(140)
				.setMinHeight(0).setMaxHeight(0)
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
						.setShadow(BoxShadow.withSize(1.5f))
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

	private static GridTemplate createListFileItemTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setMinWidth(0).setMaxWidth(0)
				.setMinHeight(0).setMaxHeight(0)
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
						.setShadow(BoxShadow.withSize(1.5f))
						.setVerticalAlignment(VerticalElementAlignment.CENTER)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
						.setRowSpan(2))
				.addElement(new TextElement(PROPERTY_CAPTION, 0, 1).setWrapLines(true)
						.setVerticalAlignment(VerticalElementAlignment.BOTTOM).setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(PROPERTY_DESCRIPTION, 1, 1).setWrapLines(true)
						.setFontStyle(0.8f, Color.fromVariableName("ta-text-color-gray")).setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT));
	}

	private static GridTemplate createApplicationListingTemplate() {
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
						.setShadow(BoxShadow.withSize(1.5f))
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

	private static GridTemplate createNavigationBarIconOnlyTemplate() {
		return new GridTemplate()
				.setAriaLabelProperty(PROPERTY_ARIA_LABEL)
				.setTitleProperty(PROPERTY_TITLE)
				.setPadding(new Spacing(2))
				.addColumn(new SizingPolicy(SizeType.FIXED, 24, 24))
				.addRow(new SizingPolicy(SizeType.FIXED, 24, 24))
				.addElement(new IconElement(PROPERTY_ICON, 0, 0, 24));
	}

}
