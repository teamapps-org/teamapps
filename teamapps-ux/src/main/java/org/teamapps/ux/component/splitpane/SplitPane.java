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
package org.teamapps.ux.component.splitpane;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.DtoSplitPane;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class SplitPane extends AbstractComponent {

	public ProjectorEvent<Double> onResized = createProjectorEventBoundToUiEvent(DtoSplitPane.SplitResizedEvent.TYPE_ID);

	private SplitDirection splitDirection;
	private SplitSizePolicy sizePolicy;
	private double referenceChildSize;
	private org.teamapps.ux.component.Component firstChild;
	private org.teamapps.ux.component.Component lastChild;
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
		uiSplitPane.setSplitDirection(splitDirection.toDto());
		uiSplitPane.setSizePolicy(sizePolicy.toDto());
		mapAbstractUiComponentProperties(uiSplitPane);
		uiSplitPane.setReferenceChildSize(referenceChildSize);
		if (firstChild != null) {
			uiSplitPane.setFirstChild(firstChild.createClientReference());
		}
		if (lastChild != null) {
			uiSplitPane.setLastChild(lastChild.createClientReference());
		}
		uiSplitPane.setFirstChildMinSize(firstChildMinSize);
		uiSplitPane.setLastChildMinSize(lastChildMinSize);
		uiSplitPane.setResizable(resizable);
		uiSplitPane.setChildCollapsingPolicy(childCollapsingPolicy.toDto());

		return uiSplitPane;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoSplitPane.SplitResizedEvent.TYPE_ID -> {
				var resizedEvent = event.as(DtoSplitPane.SplitResizedEventWrapper.class);
				this.referenceChildSize = resizedEvent.getReferenceChildSize();
				onResized.fire(resizedEvent.getReferenceChildSize());
			}
		}
	}

	public org.teamapps.ux.component.Component getFirstChild() {
		return firstChild;
	}

	public void setFirstChild(org.teamapps.ux.component.Component firstChild) {
		this.firstChild = firstChild;
		sendCommandIfRendered(() -> new DtoSplitPane.SetFirstChildCommand(firstChild != null ? firstChild.createClientReference() : null));
	}

	public org.teamapps.ux.component.Component getLastChild() {
		return lastChild;
	}

	public void setLastChild(org.teamapps.ux.component.Component lastChild) {
		this.lastChild = lastChild;
		sendCommandIfRendered(() -> new DtoSplitPane.SetLastChildCommand(lastChild != null ? lastChild.createClientReference() : null));
	}

	public SplitDirection getSplitDirection() {
		return splitDirection;
	}

	public void setSplitDirection(SplitDirection splitDirection) {
		this.splitDirection = splitDirection;
		this.sendCommandIfRendered(() -> new DtoSplitPane.SetSplitDirectionCommand(splitDirection.toDto()));
	}

	public SplitSizePolicy getSizePolicy() {
		return sizePolicy;
	}

	public void setSizePolicy(SplitSizePolicy sizePolicy) {
		this.sizePolicy = sizePolicy;
		sendCommandIfRendered(() -> new DtoSplitPane.SetSizePolicyCommand(sizePolicy.toDto()));
	}

	public double getReferenceChildSize() {
		return referenceChildSize;
	}

	public void setReferenceChildSize(float referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
		sendCommandIfRendered(() -> new DtoSplitPane.SetReferenceChildSizeCommand(referenceChildSize));
	}

	public int getFirstChildMinSize() {
		return firstChildMinSize;
	}

	public void setFirstChildMinSize(int firstChildMinSize) {
		this.firstChildMinSize = firstChildMinSize;
		this.sendCommandIfRendered(() -> new DtoSplitPane.SetFirstChildMinSizeCommand(firstChildMinSize));
	}

	public int getLastChildMinSize() {
		return lastChildMinSize;
	}

	public void setLastChildMinSize(int lastChildMinSize) {
		this.lastChildMinSize = lastChildMinSize;
		this.sendCommandIfRendered(() -> new DtoSplitPane.SetLastChildMinSizeCommand(lastChildMinSize));
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
		this.sendCommandIfRendered(() -> new DtoSplitPane.SetResizableCommand(resizable));
	}

	public ChildCollapsingPolicy getChildCollapsingPolicy() {
		return childCollapsingPolicy;
	}

	public void setChildCollapsingPolicy(ChildCollapsingPolicy childCollapsingPolicy) {
		this.childCollapsingPolicy = childCollapsingPolicy;
		this.sendCommandIfRendered(() -> new DtoSplitPane.SetChildCollapsingPolicyCommand(childCollapsingPolicy.toDto()));
	}
}
