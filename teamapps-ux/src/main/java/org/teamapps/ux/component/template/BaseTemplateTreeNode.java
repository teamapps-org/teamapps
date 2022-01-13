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
package org.teamapps.ux.component.template;

import org.teamapps.icons.Icon;
import org.teamapps.ux.component.node.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseTemplateTreeNode<PAYLOAD> extends BaseTemplateRecord<PAYLOAD> implements TreeNode {

	private BaseTemplateTreeNode<PAYLOAD> parent;
	private boolean expanded;
	private boolean lazyChildren;
	private boolean selectable = true;

	public BaseTemplateTreeNode() {
	}

	public BaseTemplateTreeNode(String caption) {
		super(caption);
	}

	public BaseTemplateTreeNode(Icon icon, String caption) {
		super(icon, caption);
	}

	public BaseTemplateTreeNode(Icon icon, String caption, String description) {
		super(icon, caption, description);
	}

	public BaseTemplateTreeNode(Icon icon, String caption, String description, String badge) {
		super(icon, caption, description, badge);
	}

	public BaseTemplateTreeNode(String image, String caption) {
		super(image, caption);
	}

	public BaseTemplateTreeNode(String image, String caption, String description) {
		super(image, caption, description);
	}

	public BaseTemplateTreeNode(String image, String caption, String description, String badge) {
		super(image, caption, description, badge);
	}

	public BaseTemplateTreeNode(Icon icon, String image, String caption, String description, String badge) {
		super(icon, image, caption, description, badge);
	}

	public BaseTemplateTreeNode(Icon icon, String image, String caption, String description, String badge, PAYLOAD payload) {
		super(icon, image, caption, description, badge, payload);
	}

	public BaseTemplateTreeNode<PAYLOAD> copy() {
		BaseTemplateTreeNode<PAYLOAD> copy = new BaseTemplateTreeNode<>(getIcon(), getImage(), getCaption(), getDescription(), getBadge(), getPayload());
		copy.setParent(parent);
		copy.setLazyChildren(lazyChildren);
		copy.setExpanded(expanded);
		copy.setSelectable(selectable);
		return copy;
	}

	public BaseTemplateTreeNode<PAYLOAD> setPayload(PAYLOAD payload) {
		super.setPayload(payload);
		return this;
	}

	@Override
	public BaseTemplateTreeNode<PAYLOAD> getParent() {
		return parent;
	}

	public BaseTemplateTreeNode<PAYLOAD> setParent(BaseTemplateTreeNode<PAYLOAD> parent) {
		this.parent = parent;
		return this;
	}

	@Override
	public boolean isLazyChildren() {
		return lazyChildren;
	}

	public void setLazyChildren(boolean lazyChildren) {
		this.lazyChildren = lazyChildren;
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public static <PAYLOAD> List<BaseTemplateTreeNode<PAYLOAD>> copyTree(List<BaseTemplateTreeNode<PAYLOAD>> tree) {
		Map<BaseTemplateTreeNode<PAYLOAD>, BaseTemplateTreeNode<PAYLOAD>> copysByOriginal = tree.stream()
				.collect(Collectors.toMap(node -> node, node -> node.copy()));
		copysByOriginal.values().forEach(node -> {
			node.setParent(node.getParent() != null ? copysByOriginal.get(node.getParent()) : null);
		});
		return new ArrayList<>(copysByOriginal.values());
	}
}
