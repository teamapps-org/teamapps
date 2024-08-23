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
package org.teamapps.projector.component.essential.splitpane;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.*;
import org.teamapps.projector.event.ProjectorEvent;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class SplitPane extends AbstractComponent implements DtoSplitPaneEventHandler {

	private final DtoSplitPaneClientObjectChannel clientObjectChannel = new DtoSplitPaneClientObjectChannel(getClientObjectChannel());

	public ProjectorEvent<Double> onResized = new ProjectorEvent<>(clientObjectChannel::toggleSplitResizedEvent);

	private SplitDirection splitDirection;
	private SplitSizePolicy sizePolicy;
	private double referenceChildSize;
	private Component firstChild;
	private Component lastChild;
	private int firstChildMinSize = 10;
	private int lastChildMinSize = 10;
	private boolean resizable = true;
	private ChildCollapsingPolicy childCollapsingPolicy = ChildCollapsingPolicy.IF_EMPTY;

	public static SplitPane createHorizontalSplitPane() {
		return new SplitPane(SplitDirection.HORIZONTAL);
	}

	public static SplitPane createVerticalSplitPane() {
		return new SplitPane(SplitDirection.VERTICAL);
	}

	public SplitPane(SplitDirection splitDirection) {
		this(splitDirection, SplitSizePolicy.RELATIVE, 0.5f);
	}

	public SplitPane(SplitDirection splitDirection, SplitSizePolicy sizePolicy, float referenceChildSize) {
		this.splitDirection = splitDirection;
		this.sizePolicy = sizePolicy;
		this.referenceChildSize = referenceChildSize;
	}

	@Override
	public DtoComponent createConfig() {
		DtoSplitPane uiSplitPane = new DtoSplitPane();
		uiSplitPane.setSplitDirection(splitDirection);
		uiSplitPane.setSizePolicy(sizePolicy);
		mapAbstractConfigProperties(uiSplitPane);
		uiSplitPane.setReferenceChildSize(referenceChildSize);
		if (firstChild != null) {
			uiSplitPane.setFirstChild(firstChild);
		}
		if (lastChild != null) {
			uiSplitPane.setLastChild(lastChild);
		}
		uiSplitPane.setFirstChildMinSize(firstChildMinSize);
		uiSplitPane.setLastChildMinSize(lastChildMinSize);
		uiSplitPane.setResizable(resizable);
		uiSplitPane.setChildCollapsingPolicy(childCollapsingPolicy);

		return uiSplitPane;
	}

	@Override
	public void handleSplitResized(double referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
		onResized.fire(referenceChildSize);
	}

	public Component getFirstChild() {
		return firstChild;
	}

	public void setFirstChild(Component firstChild) {
		this.firstChild = firstChild;
		clientObjectChannel.setFirstChild(firstChild);
	}

	public Component getLastChild() {
		return lastChild;
	}

	public void setLastChild(Component lastChild) {
		this.lastChild = lastChild;
		clientObjectChannel.setLastChild(lastChild);
	}

	public SplitDirection getSplitDirection() {
		return splitDirection;
	}

	public void setSplitDirection(SplitDirection splitDirection) {
		this.splitDirection = splitDirection;
		clientObjectChannel.setSplitDirection(splitDirection);
	}

	public SplitSizePolicy getSizePolicy() {
		return sizePolicy;
	}

	public void setSizePolicy(SplitSizePolicy sizePolicy) {
		this.sizePolicy = sizePolicy;
		clientObjectChannel.setSizePolicy(sizePolicy);
	}

	public double getReferenceChildSize() {
		return referenceChildSize;
	}

	public void setReferenceChildSize(float referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
		clientObjectChannel.setReferenceChildSize(referenceChildSize);
	}

	public int getFirstChildMinSize() {
		return firstChildMinSize;
	}

	public void setFirstChildMinSize(int firstChildMinSize) {
		this.firstChildMinSize = firstChildMinSize;
		clientObjectChannel.setFirstChildMinSize(firstChildMinSize);
	}

	public int getLastChildMinSize() {
		return lastChildMinSize;
	}

	public void setLastChildMinSize(int lastChildMinSize) {
		this.lastChildMinSize = lastChildMinSize;
		clientObjectChannel.setLastChildMinSize(lastChildMinSize);
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
		clientObjectChannel.setResizable(resizable);
	}

	public ChildCollapsingPolicy getChildCollapsingPolicy() {
		return childCollapsingPolicy;
	}

	public void setChildCollapsingPolicy(ChildCollapsingPolicy childCollapsingPolicy) {
		this.childCollapsingPolicy = childCollapsingPolicy;
		clientObjectChannel.setChildCollapsingPolicy(childCollapsingPolicy);
	}

}
