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
package org.teamapps.ux.application.layout;

import org.teamapps.ux.component.workspacelayout.SplitDirection;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.ux.component.workspacelayout.definition.SplitSize;
import org.teamapps.ux.component.workspacelayout.definition.SplitPaneDefinition;
import org.teamapps.ux.component.workspacelayout.definition.ViewGroupDefinition;

public class StandardLayout {

	public static final String LEFT = "left";
	public static final String LEFT_BOTTOM = "left-bottom";
	public static final String TOP = "top";
	public static final String CENTER = "center";
	public static final String CENTER_BOTTOM = "center-bottom";
	public static final String RIGHT = "right";
	public static final String RIGHT_BOTTOM = "right-bottom";

	public final LayoutItemDefinition rootItem;

	public final ViewGroupDefinition leftViewGroup;
	public final ViewGroupDefinition leftBottomViewGroup;
	public final ViewGroupDefinition topViewGroup;
	public final ViewGroupDefinition centerViewGroup;
	public final ViewGroupDefinition centerBottomViewGroup;
	public final ViewGroupDefinition rightViewGroup;
	public final ViewGroupDefinition rightBottomViewGroup;

	public static LayoutItemDefinition createLayout() {
		return new StandardLayout().getRootItem();
	}
	public static LayoutItemDefinition createLayout(SplitSize leftWidth, SplitSize leftHeight, SplitSize topHeight, SplitSize centerWidth, SplitSize centerHeight, SplitSize rightHeight) {
		return new StandardLayout(leftWidth, leftHeight, topHeight, centerWidth, centerHeight, rightHeight).getRootItem();
	}

	public StandardLayout() {
		this(SplitSize.firstFixed(250), SplitSize.relative(.5f), SplitSize.firstFixed(150), SplitSize.relative(.5f), SplitSize.relative(.5f), SplitSize.relative(.5f));
	}

	public StandardLayout(SplitSize leftWidth, SplitSize leftHeight, SplitSize topHeight, SplitSize centerWidth, SplitSize centerHeight, SplitSize rightHeight) {
		leftViewGroup = new ViewGroupDefinition(LEFT, true);
		leftBottomViewGroup = new ViewGroupDefinition(LEFT_BOTTOM, true);
		topViewGroup = new ViewGroupDefinition(TOP, true);
		centerViewGroup = new ViewGroupDefinition(CENTER, true);
		centerBottomViewGroup = new ViewGroupDefinition(CENTER_BOTTOM, true);
		rightViewGroup = new ViewGroupDefinition(RIGHT, true);
		rightBottomViewGroup = new ViewGroupDefinition(RIGHT_BOTTOM, true);

		rootItem = new SplitPaneDefinition(
				"leftSplit", SplitDirection.VERTICAL, leftWidth,
				new SplitPaneDefinition(
						"leftInner", SplitDirection.HORIZONTAL, leftHeight,
						leftViewGroup,
						leftBottomViewGroup
				),
				new SplitPaneDefinition(
						"topSplit", SplitDirection.HORIZONTAL, topHeight,
						topViewGroup,
						new SplitPaneDefinition(
								"centerRightSplit", SplitDirection.VERTICAL, centerWidth,
								new SplitPaneDefinition(
										"centerSplit", SplitDirection.HORIZONTAL, centerHeight,
										centerViewGroup,
										centerBottomViewGroup
								),
								new SplitPaneDefinition(
										"rightSplit", SplitDirection.HORIZONTAL, rightHeight,
										rightViewGroup,
										rightBottomViewGroup
								)
						)
				)
		);

	}

	public LayoutItemDefinition getRootItem() {
		return rootItem;
	}

	public ViewGroupDefinition getLeftViewGroup() {
		return leftViewGroup;
	}

	public ViewGroupDefinition getLeftBottomViewGroup() {
		return leftBottomViewGroup;
	}

	public ViewGroupDefinition getTopViewGroup() {
		return topViewGroup;
	}

	public ViewGroupDefinition getCenterViewGroup() {
		return centerViewGroup;
	}

	public ViewGroupDefinition getCenterBottomViewGroup() {
		return centerBottomViewGroup;
	}

	public ViewGroupDefinition getRightViewGroup() {
		return rightViewGroup;
	}

	public ViewGroupDefinition getRightBottomViewGroup() {
		return rightBottomViewGroup;
	}
}
