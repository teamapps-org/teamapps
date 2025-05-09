/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.Color;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.dummy.DummyComponent;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.component.tree.TreeNodeInfoImpl;
import org.teamapps.ux.model.AbstractTreeModel;
import org.teamapps.ux.model.ComboBoxModel;
import org.teamapps.webcontroller.WebController;

import java.util.List;

public class TeamAppsJettyEmbeddedServerTest {

    private static final User ALICE = new User(MaterialIcon.VERIFIED_USER, "Alice", Color.ALICE_BLUE);
    private static final User BOB = new User(MaterialIcon.VERIFIED_USER, "Bob", Color.ALICE_BLUE);
    private static final User CARL = new User(MaterialIcon.VERIFIED_USER, "Carl", Color.ALICE_BLUE);
    private static final User DAN = new User(MaterialIcon.VERIFIED_USER, "Dan", Color.ALICE_BLUE);
    private static final User EDUARD = new User(MaterialIcon.VERIFIED_USER, "Eduard", Color.ALICE_BLUE);

    public static void main(String[] args) throws Exception {
        WebController controller = sessionContext -> {
            RootPanel rootPanel = new RootPanel();
            sessionContext.addRootPanel(null, rootPanel);

            Tree<User> tree = new Tree<>(new AbstractTreeModel<User>() {
                @Override
                public List<User> getRecords() {
                    return List.of(
                            new User(MaterialIcon.VERIFIED_USER, "John", Color.RED),
                            new User(MaterialIcon.VERIFIED_USER, "Jack", Color.BLUE),
                            new User(MaterialIcon.VERIFIED_USER, "Jane", Color.GREEN)
                    );
                }
            });

            tree.setContextMenuProvider(user -> new DummyComponent(user.firstName()));

            rootPanel.setContent(tree);
        };

        TeamAppsJettyEmbeddedServer.builder(controller)
                .setPort(8082)
                .build()
                .start();
    }


    private static class UserAbstractTreeModel extends AbstractTreeModel<User> implements ComboBoxModel<User> {
        @Override
        public List<User> getRecords() {
            return List.of(ALICE, BOB, CARL, DAN, EDUARD);
        }

        @Override
        public TreeNodeInfo getTreeNodeInfo(User user) {
            if (user == ALICE || user == BOB) {
                return new TreeNodeInfoImpl<User>(null, false, false);
            } else if (user == CARL) {
                return new TreeNodeInfoImpl<>(ALICE, false);
            } else {
                return new TreeNodeInfoImpl<>(BOB, false);
            }
        }

        @Override
        public List<User> getRecords(String query) {
            return getRecords();
        }
    }
}
