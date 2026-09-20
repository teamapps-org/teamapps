/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.tree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.teamapps.dto.UiCommand;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.dto.UiTree;
import org.teamapps.ux.component.template.BaseTemplateTreeNode;
import org.teamapps.ux.session.CurrentSessionContextTestUtil;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TreeTest {
    private SessionContext context;
    private Tree<BaseTemplateTreeNode<Void>> tree;
    private BaseTemplateTreeNode<Void> parent;
    private BaseTemplateTreeNode<Void> child;
    private UiTree ui;

    @Before
    public void setUp() {
        context = spy(UxTestUtil.createDummySessionContext());
        doNothing().when(context).queueCommand(any(UiCommand.class));
        CurrentSessionContextTestUtil.set(context);
        parent = new BaseTemplateTreeNode<>();
        parent.setCaption("Parent");
        child = new BaseTemplateTreeNode<>();
        child.setCaption("Child");
        child.setParent(parent);
        tree = new Tree<>(new SimpleTreeModel<>(List.of(parent, child)));
        ui = (UiTree) tree.createUiComponent();
    }

    @After
    public void tearDown() {
        CurrentSessionContextTestUtil.unset();
    }

    @Test
    public void expansionAndCollapseDoNotChangeSelectionOrFireSelectionEvents() {
        List<Object> selections = new ArrayList<>();
        List<TreeNodeExpansionEvent<BaseTemplateTreeNode<Void>>> expansions = new ArrayList<>();
        tree.onNodeSelected.addListener(node -> { selections.add(node); });
        tree.onNodeExpansionChanged.addListener(event -> { expansions.add(event); });
        tree.setSelectedNode(child);
        int parentId = ui.getInitialData().get(0).getId();
        tree.handleUiEvent(new UiTree.NodeExpansionChangedEvent(tree.getId(), parentId, true));
        tree.handleUiEvent(new UiTree.NodeExpansionChangedEvent(tree.getId(), parentId, false));
        assertThat(tree.getSelectedNode()).isSameAs(child);
        assertThat(selections).isEmpty();
        assertThat(expansions).hasSize(2);
        assertThat(expansions.get(0).getNode()).isSameAs(parent);
        assertThat(expansions.get(0).isExpanded()).isTrue();
        assertThat(expansions.get(1).isExpanded()).isFalse();
    }

    @Test
    public void userSelectionStillUpdatesSelectionBeforeNotifyingListeners() {
        tree.setSelectedNode(parent);
        List<Object> selections = new ArrayList<>();
        tree.onNodeSelected.addListener(node -> {
            assertThat(tree.getSelectedNode()).isSameAs(node);
            selections.add(node);
        });
        tree.handleUiEvent(new UiTree.NodeSelectedEvent(tree.getId(), ui.getInitialData().get(1).getId()));
        assertThat(tree.getSelectedNode()).isSameAs(child);
        assertThat(selections).containsExactly(child);
    }

    @Test
    public void expansionWithoutSelectionDoesNotInventOne() {
        tree.handleUiEvent(new UiTree.NodeExpansionChangedEvent(tree.getId(), ui.getInitialData().get(0).getId(), true));
        assertThat(tree.getSelectedNode()).isNull();
    }

    @Test
    public void ensureVisibleIsAnIndependentCommandAndDoesNotSelect() {
        tree.render();
        tree.setSelectedNode(child);
        clearInvocations(context);
        tree.ensureVisible(parent);
        ArgumentCaptor<UiCommand> commands = ArgumentCaptor.forClass(UiCommand.class);
        verify(context).queueCommand(commands.capture());
        assertThat(commands.getValue()).isInstanceOf(UiTree.EnsureVisibleCommand.class);
        assertThat(((UiTree.EnsureVisibleCommand) commands.getValue()).getRecordId()).isEqualTo(ui.getInitialData().get(0).getId());
        assertThat(tree.getSelectedNode()).isSameAs(child);
    }

    @Test
    public void latestPreRenderRequestRunsAfterCreationAndUnknownNodesAreIgnored() {
        tree.ensureVisible(parent);
        tree.ensureVisible(child);
        tree.render();
        ArgumentCaptor<UiCommand> commands = ArgumentCaptor.forClass(UiCommand.class);
        verify(context, times(2)).queueCommand(commands.capture());
        assertThat(commands.getAllValues().get(0).getClass().getSimpleName()).isEqualTo("CreateComponentCommand");
        assertThat(((UiTree.EnsureVisibleCommand) commands.getAllValues().get(1)).getRecordId()).isEqualTo(ui.getInitialData().get(1).getId());
        clearInvocations(context);
        tree.ensureVisible(new BaseTemplateTreeNode<>());
        verify(context, never()).queueCommand(any(UiCommand.class));
        assertThat(tree.getSelectedNode()).isNull();
    }
}
