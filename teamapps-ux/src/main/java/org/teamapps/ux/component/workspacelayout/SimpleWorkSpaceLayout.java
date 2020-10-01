/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
package org.teamapps.ux.component.workspacelayout;

import org.teamapps.ux.component.splitpane.SplitSizePolicy;

public class SimpleWorkSpaceLayout extends WorkSpaceLayout {

	private final WorkSpaceLayoutViewGroup leftViewGroup;
	private final WorkSpaceLayoutViewGroup leftBottomViewGroup;
	private final WorkSpaceLayoutViewGroup topViewGroup;
	private final WorkSpaceLayoutViewGroup centerViewGroup;
	private final WorkSpaceLayoutViewGroup centerBottomViewGroup;
	private final WorkSpaceLayoutViewGroup rightViewGroup;
	private final WorkSpaceLayoutViewGroup rightBottomViewGroup;

	public SimpleWorkSpaceLayout() {
		leftViewGroup = new WorkSpaceLayoutViewGroup("leftViewGroup", null, null, true, this);
		leftBottomViewGroup = new WorkSpaceLayoutViewGroup("leftBottomViewGroup", null, null, true, this);
		topViewGroup = new WorkSpaceLayoutViewGroup("topViewGroup", null, null, true, this);
		centerViewGroup = new WorkSpaceLayoutViewGroup("centerViewGroup", null, null, true, this);
		centerBottomViewGroup = new WorkSpaceLayoutViewGroup("centerBottomViewGroup", null, null, true, this);
		rightViewGroup = new WorkSpaceLayoutViewGroup("rightViewGroup", null, null, true, this);
		rightBottomViewGroup = new WorkSpaceLayoutViewGroup("rightBottomViewGroup", null, null, true, this);

		WorkSpaceLayoutItem rootItem = new WorkSpaceLayoutSplitPane(
				"root", // TODO remove
				SplitDirection.VERTICAL, SplitSizePolicy.FIRST_FIXED, 250,
				new WorkSpaceLayoutSplitPane(
						"left", // TODO remove
						SplitDirection.HORIZONTAL, SplitSizePolicy.RELATIVE, .5f,
						leftViewGroup,
						leftBottomViewGroup,
						this
				),
				new WorkSpaceLayoutSplitPane(
						"top-vs-main", // TODO remove
						SplitDirection.HORIZONTAL, SplitSizePolicy.FIRST_FIXED, 150,
						topViewGroup,
						new WorkSpaceLayoutSplitPane(
								"center-vs-right (main)", // TODO remove
								SplitDirection.VERTICAL, SplitSizePolicy.RELATIVE, .5f,
								new WorkSpaceLayoutSplitPane(
										"center",  // TODO remove
										SplitDirection.HORIZONTAL, SplitSizePolicy.RELATIVE, 0.5f,
										centerViewGroup,
										centerBottomViewGroup,
										this
								),
								new WorkSpaceLayoutSplitPane(
										"right", // TODO remove
										SplitDirection.HORIZONTAL, SplitSizePolicy.RELATIVE, 0.5f,
										rightViewGroup,
										rightBottomViewGroup,
										this
								),
								this
						),
						this
				),
				this
		);

		setMainRootItem(rootItem);
	}

	public WorkSpaceLayoutViewGroup getLeftViewGroup() {
		return leftViewGroup;
	}

	public WorkSpaceLayoutViewGroup getLeftBottomViewGroup() {
		return leftBottomViewGroup;
	}

	public WorkSpaceLayoutViewGroup getTopViewGroup() {
		return topViewGroup;
	}

	public WorkSpaceLayoutViewGroup getCenterViewGroup() {
		return centerViewGroup;
	}

	public WorkSpaceLayoutViewGroup getCenterBottomViewGroup() {
		return centerBottomViewGroup;
	}

	public WorkSpaceLayoutViewGroup getRightViewGroup() {
		return rightViewGroup;
	}

	public WorkSpaceLayoutViewGroup getRightBottomViewGroup() {
		return rightBottomViewGroup;
	}
}
