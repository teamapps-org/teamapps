/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.webcontroller;

import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;

import java.util.function.Function;

public class SimpleWebController implements WebController {

    private final Function<SessionContext, Component> componentSupplier;

    public SimpleWebController(Function<SessionContext, Component> componentSupplier) {
        this.componentSupplier = componentSupplier;
    }

    @Override
    public void onSessionStart(SessionContext context) {
        String defaultBackground = "/resources/backgrounds/default-bl.jpg";
        context.registerBackgroundImage("default", defaultBackground, defaultBackground);

        RootPanel rootPanel = new RootPanel();
        context.addRootComponent(null, rootPanel);
        rootPanel.setContent(componentSupplier.apply(context));
        context.setBackgroundImage("default", 0);
    }
}
