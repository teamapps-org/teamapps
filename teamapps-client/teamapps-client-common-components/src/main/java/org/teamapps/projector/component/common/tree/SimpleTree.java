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
package org.teamapps.projector.component.common.tree;

import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.projector.template.Template;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleTree<PAYLOAD> extends Tree<BaseTemplateTreeNode<PAYLOAD>> {

	private List<Template> templatesByDepth = Arrays.asList(BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES, BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES, BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);

	public SimpleTree() {
		this(Collections.emptyList());
	}

	public SimpleTree(List<BaseTemplateTreeNode<PAYLOAD>> records) {
		super(new SimpleTreeModel<>(records));
		this.setTemplateDecider(node -> templatesByDepth.get(Math.min(templatesByDepth.size() - 1, node.getDepth())));
	}

	public void addNode(BaseTemplateTreeNode<PAYLOAD> node) {
		getModel().addNode(node);
	}

	public void addNodes(List<BaseTemplateTreeNode<PAYLOAD>> nodes) {
		getModel().addNodes(nodes);
	}

	public void removeChildren(Collection<BaseTemplateTreeNode<PAYLOAD>> parents) {
		getModel().removeChildren(parents);
	}

	public void replaceChildren(Collection<BaseTemplateTreeNode<PAYLOAD>> parentsToEmpty, List<BaseTemplateTreeNode<PAYLOAD>> nodesToAdd) {
		getModel().replaceChildren(parentsToEmpty, nodesToAdd);
	}

	public void updateNode(BaseTemplateTreeNode<PAYLOAD> node) {
		getModel().updateNode(node);
	}

	public void setNodeExpanded(BaseTemplateTreeNode<PAYLOAD> node, boolean expanded) {
		node.setExpanded(expanded);
		updateNode(node);
	}

	public void relocateNode(BaseTemplateTreeNode<PAYLOAD> node) {
		getModel().relocateNode(node);
	}

	public void removeNode(BaseTemplateTreeNode<PAYLOAD> node) {
		getModel().removeNode(node);
	}

	public void removeAllNodes() {
		getModel().removeAllNodes();
	}

	public SimpleTreeModel<PAYLOAD> getModel() {
		return (SimpleTreeModel<PAYLOAD>) super.getModel();
	}

	public List<Template> getTemplatesByDepth() {
		return templatesByDepth;
	}

	public void setTemplatesByDepth(Template... templatesByDepth) {
		this.templatesByDepth = Arrays.asList(templatesByDepth);
	}

	@Override
	public void setEntryTemplate(Template entryTemplate) {
		setTemplatesByDepth(entryTemplate);
	}
}
