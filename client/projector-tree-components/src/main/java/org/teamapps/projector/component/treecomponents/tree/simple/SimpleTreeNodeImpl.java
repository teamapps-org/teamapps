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
package org.teamapps.projector.component.treecomponents.tree.simple;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.component.treecomponents.tree.model.TreeNodeInfo;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleTreeNodeImpl<PAYLOAD> extends BaseTemplateRecord<PAYLOAD> implements SimpleTreeNode<SimpleTreeNodeImpl<PAYLOAD>> {

	private SimpleTreeNodeImpl<PAYLOAD> parent;
	private boolean expanded;
	private boolean lazyChildren;
	private boolean selectable = true;

	public SimpleTreeNodeImpl() {
	}

	public SimpleTreeNodeImpl(String caption) {
		super(caption);
	}

	public SimpleTreeNodeImpl(Icon icon, String caption) {
		super(icon, caption);
	}

	public SimpleTreeNodeImpl(Icon icon, String caption, String description) {
		super(icon, caption, description);
	}

	public SimpleTreeNodeImpl(Icon icon, String caption, String description, String badge) {
		super(icon, caption, description, badge);
	}

	public SimpleTreeNodeImpl(String image, String caption) {
		super(image, caption);
	}

	public SimpleTreeNodeImpl(String image, String caption, String description) {
		super(image, caption, description);
	}

	public SimpleTreeNodeImpl(String image, String caption, String description, String badge) {
		super(image, caption, description, badge);
	}

	public SimpleTreeNodeImpl(Icon icon, String image, String caption, String description, String badge) {
		super(icon, image, caption, description, badge);
	}

	public SimpleTreeNodeImpl(Icon icon, String image, String caption, String description, String badge, PAYLOAD payload) {
		super(icon, image, caption, description, badge, payload);
	}

	public SimpleTreeNodeImpl<PAYLOAD> copy() {
		SimpleTreeNodeImpl<PAYLOAD> copy = new SimpleTreeNodeImpl<>(getIcon(), getImage(), getCaption(), getDescription(), getBadge(), getPayload());
		copy.setParent(parent);
		copy.setLazyChildren(lazyChildren);
		copy.setExpanded(expanded);
		copy.setSelectable(selectable);
		return copy;
	}

	public SimpleTreeNodeImpl<PAYLOAD> setPayload(PAYLOAD payload) {
		super.setPayload(payload);
		return this;
	}

	@Override
	public SimpleTreeNodeImpl<PAYLOAD> getParent() {
		return parent;
	}

	public SimpleTreeNodeImpl<PAYLOAD> setParent(SimpleTreeNodeImpl<PAYLOAD> parent) {
		this.parent = parent;
		return this;
	}

	public TreeNodeInfo<SimpleTreeNodeImpl<PAYLOAD>> getTreeNodeInfo() {
		return new TreeNodeInfo<>(parent, expanded, selectable, lazyChildren);
	}

	public boolean isLazyChildren() {
		return lazyChildren;
	}

	public void setLazyChildren(boolean lazyChildren) {
		this.lazyChildren = lazyChildren;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public static <PAYLOAD> List<SimpleTreeNodeImpl<PAYLOAD>> copyTree(List<SimpleTreeNodeImpl<PAYLOAD>> tree) {
		Map<SimpleTreeNodeImpl<PAYLOAD>, SimpleTreeNodeImpl<PAYLOAD>> copysByOriginal = tree.stream()
				.collect(Collectors.toMap(node -> node, node -> node.copy()));
		copysByOriginal.values().forEach(node -> {
			node.setParent(node.getParent() != null ? copysByOriginal.get(node.getParent()) : null);
		});
		return new ArrayList<>(copysByOriginal.values());
	}
}
