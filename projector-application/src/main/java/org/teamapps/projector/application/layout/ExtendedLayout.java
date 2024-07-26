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
package org.teamapps.projector.application.layout;

import org.teamapps.projector.component.essential.SplitDirection;
import org.teamapps.projector.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.projector.component.workspacelayout.definition.SplitPaneDefinition;
import org.teamapps.projector.component.workspacelayout.definition.SplitSize;
import org.teamapps.projector.component.workspacelayout.definition.ViewGroupDefinition;

public class ExtendedLayout {

	public static final String SUPER_TOP = "super-top";
	public static final String LEFT = "left";
	public static final String LEFT_BOTTOM = "left-bottom";
	public static final String INNER_LEFT = "inner-left";
	public static final String INNER_LEFT_BOTTOM = "inner-left-bottom";
	public static final String TOP = "top";
	public static final String CENTER = "center";
	public static final String CENTER_BOTTOM = "center-bottom";
	public static final String RIGHT = "right";
	public static final String RIGHT_BOTTOM = "right-bottom";
	public static final String OUTER_RIGHT = "outer-right";
	public static final String OUTER_RIGHT_BOTTOM = "outer-right-bottom";

	public final LayoutItemDefinition rootItem;
	public final ViewGroupDefinition superTopViewGroup;
	public final ViewGroupDefinition leftViewGroup;
	public final ViewGroupDefinition leftBottomViewGroup;
	public final ViewGroupDefinition innerLeftViewGroup;
	public final ViewGroupDefinition innerLeftBottomViewGroup;
	public final ViewGroupDefinition topViewGroup;
	public final ViewGroupDefinition centerViewGroup;
	public final ViewGroupDefinition centerBottomViewGroup;
	public final ViewGroupDefinition rightViewGroup;
	public final ViewGroupDefinition rightBottomViewGroup;
	public final ViewGroupDefinition outerRightViewGroup;
	public final ViewGroupDefinition outerRightBottomViewGroup;


	public static LayoutItemDefinition createLayout() {
		return new ExtendedLayout().getRootItem();
	}
	
	public static LayoutItemDefinition createLayout(SplitSize superTopHeight, SplitSize leftWidth, SplitSize leftHeight, SplitSize innerLeftWidth, SplitSize innerLeftHeight,
													SplitSize topHeight, SplitSize centerWidth, SplitSize centerHeight, SplitSize rightWidth, SplitSize rightHeight, SplitSize outerRightHeight) {
		return new ExtendedLayout(superTopHeight, leftWidth, leftHeight, innerLeftWidth, innerLeftHeight, topHeight, centerWidth, centerHeight, rightWidth, rightHeight, outerRightHeight).getRootItem();
	}

	public ExtendedLayout() {
		this(SplitSize.firstFixed(175), SplitSize.firstFixed(250), SplitSize.relative(.5f), SplitSize.firstFixed(250), SplitSize.relative(.5f), SplitSize.firstFixed(150),
				SplitSize.relative(.5f), SplitSize.relative(.5f), SplitSize.lastFixed(250), SplitSize.relative(.5f), SplitSize.relative(.5f));
	}

	public ExtendedLayout(SplitSize superTopHeight, SplitSize leftWidth, SplitSize leftHeight, SplitSize innerLeftWidth, SplitSize innerLeftHeight,
	                      SplitSize topHeight, SplitSize centerWidth, SplitSize centerHeight, SplitSize rightWidth, SplitSize rightHeight, SplitSize outerRightHeight) {
		superTopViewGroup = new ViewGroupDefinition(SUPER_TOP, true);
		leftViewGroup = new ViewGroupDefinition(LEFT, true);
		leftBottomViewGroup = new ViewGroupDefinition(LEFT_BOTTOM, true);
		innerLeftViewGroup = new ViewGroupDefinition(INNER_LEFT, true);
		innerLeftBottomViewGroup = new ViewGroupDefinition(INNER_LEFT_BOTTOM, true);
		topViewGroup = new ViewGroupDefinition(TOP, true);
		centerViewGroup = new ViewGroupDefinition(CENTER, true);
		centerBottomViewGroup = new ViewGroupDefinition(CENTER_BOTTOM, true);
		rightViewGroup = new ViewGroupDefinition(RIGHT, true);
		rightBottomViewGroup = new ViewGroupDefinition(RIGHT_BOTTOM, true);
		outerRightViewGroup = new ViewGroupDefinition(OUTER_RIGHT, true);
		outerRightBottomViewGroup = new ViewGroupDefinition(OUTER_RIGHT_BOTTOM, true);

		rootItem = new SplitPaneDefinition(
				"superTopSplit", SplitDirection.HORIZONTAL, superTopHeight,
				superTopViewGroup,
				new SplitPaneDefinition(
						"leftSplit", SplitDirection.VERTICAL, leftWidth,
						new SplitPaneDefinition(
								"leftInnerSplit", SplitDirection.HORIZONTAL, leftHeight,
								leftViewGroup,
								leftBottomViewGroup
						),
						new SplitPaneDefinition(
								"leftSplit2", SplitDirection.VERTICAL, innerLeftWidth,
								new SplitPaneDefinition(
										"leftInner2Split", SplitDirection.HORIZONTAL, innerLeftHeight,
										innerLeftViewGroup,
										innerLeftBottomViewGroup
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
														"rightSplit", SplitDirection.VERTICAL, rightWidth,
														new SplitPaneDefinition(
																"rightInnerSplit", SplitDirection.HORIZONTAL, rightHeight,
																rightViewGroup,
																rightBottomViewGroup
														),
														new SplitPaneDefinition(
																"rightInner2Split", SplitDirection.HORIZONTAL, outerRightHeight,
																outerRightViewGroup,
																outerRightBottomViewGroup
														)
												)
										)
								)
						)
				)
		);

	}

	public LayoutItemDefinition getRootItem() {
		return rootItem;
	}

	public ViewGroupDefinition getSuperTopViewGroup() {
		return superTopViewGroup;
	}

	public ViewGroupDefinition getLeftViewGroup() {
		return leftViewGroup;
	}

	public ViewGroupDefinition getLeftBottomViewGroup() {
		return leftBottomViewGroup;
	}

	public ViewGroupDefinition getInnerLeftViewGroup() {
		return innerLeftViewGroup;
	}

	public ViewGroupDefinition getInnerLeftBottomViewGroup() {
		return innerLeftBottomViewGroup;
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

	public ViewGroupDefinition getOuterRightViewGroup() {
		return outerRightViewGroup;
	}

	public ViewGroupDefinition getOuterRightBottomViewGroup() {
		return outerRightBottomViewGroup;
	}
}
