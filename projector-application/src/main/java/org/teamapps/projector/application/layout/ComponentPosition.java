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

import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutViewGroup;
import org.teamapps.ux.component.workspacelayout.definition.ViewGroupDefinition;

public class ComponentPosition {

	public static ComponentPosition asPanel(WorkSpaceLayoutViewGroup viewGroup) {
		return new ComponentPosition(viewGroup.getId());
	}

	public static ComponentPosition asPanel(ViewGroupDefinition viewGroup) {
		return new ComponentPosition(viewGroup.getId());
	}

	public static ComponentPosition asPanel(String layoutPosition) {
		return new ComponentPosition(layoutPosition);
	}

	public static ComponentPosition asPanel(String layoutPosition, float width, float height, int minWidth, int minHeight) {
		return new ComponentPosition(layoutPosition).setWidth(width).setHeight(height).setMinWidth(minWidth).setMinHeight(minHeight);
	}

	public static ComponentPosition asWindow(float width, float height) {
		return new ComponentPosition(width, height);
	}

	public static ComponentPosition asWindow(float width, float height, int minWidth, int minHeight) {
		return new ComponentPosition(width, height, minWidth, minHeight);
	}

	public static ComponentPosition asWindow(float width, float height, int minWidth, int minHeight, boolean modal) {
		return new ComponentPosition(width, height, minWidth, minHeight).setModalWindow(modal);
	}

	public static ComponentPosition asWindowMaxSize() {
		return new ComponentPosition(.9f, .9f);
	}

	public static ComponentPosition asWindowStandardSize() {
		return new ComponentPosition(.5f, .5f, 800, 600);
	}

	public static ComponentPosition asWindowSmallSize() {
		return new ComponentPosition(.35f, .35f);
	}

	public static ComponentPosition asWindowNarrowSize() {
		return new ComponentPosition(.9f, .5f);
	}

	public static ComponentPosition asWindowWideSize() {
		return new ComponentPosition(.9f, .9f);
	}

	private String layoutPosition;
	private boolean window;
	private boolean modalWindow;

	private float width;
	private float height;

	private int minWidth;
	private int minHeight;

	private String animation;

	public ComponentPosition(String layoutPosition) {
		this.layoutPosition = layoutPosition;
	}

	public ComponentPosition(String layoutPosition, float width, float height, int minWidth, int minHeight) {
		this.layoutPosition = layoutPosition;
		this.width = width;
		this.height = height;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public ComponentPosition(float width, float height) {
		this.window = true;
		this.width = width;
		this.height = height;
	}

	public ComponentPosition(float width, float height, int minWidth, int minHeight) {
		this.window = true;
		this.width = width;
		this.height = height;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public ComponentPosition setModalWindow(boolean modalWindow) {
		this.modalWindow = modalWindow;
		return this;
	}

	public ComponentPosition setAnimation(String animation) {
		this.animation = animation;
		return this;
	}

	public ComponentPosition setWidth(float width) {
		this.width = width;
		return this;
	}

	public ComponentPosition setHeight(float height) {
		this.height = height;
		return this;
	}

	public ComponentPosition setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public ComponentPosition setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public String getLayoutPosition() {
		return layoutPosition;
	}

	public boolean isWindow() {
		return window;
	}

	public boolean isModalWindow() {
		return modalWindow;
	}

	public String getAnimation() {
		return animation;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public int getMinHeight() {
		return minHeight;
	}
}
