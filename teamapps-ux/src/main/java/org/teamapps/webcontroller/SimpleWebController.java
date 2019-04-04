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

import org.teamapps.common.format.Color;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;

import java.util.function.Function;

public class SimpleWebController implements WebController {

    public final static String BACKGROUND_DEFAULT = "default";

    private final Function<SessionContext, Component> componentSupplier;
    private boolean showBackgroundImage;
    private Color defaultBackgroundColor;

    public static WebController createDefaultController(Function<SessionContext, Component> componentSupplier) {
        return createDefaultController(componentSupplier, Color.WHITE, false);
    }

    public static WebController createDefaultController(Function<SessionContext, Component> componentSupplier, Color defaultBackgroundColor) {
        return createDefaultController(componentSupplier, defaultBackgroundColor, false);
    }

    public static WebController createDefaultController(Function<SessionContext, Component> componentSupplier, Color defaultBackgroundColor, boolean showBackgroundImage) {
        SimpleWebController webController = new SimpleWebController(componentSupplier);
        webController.setDefaultBackgroundColor(defaultBackgroundColor);
        webController.setShowBackgroundImage(showBackgroundImage);
        return webController;
    }

    public SimpleWebController(Function<SessionContext, Component> componentSupplier) {
        this.componentSupplier = componentSupplier;
    }

    public void setShowBackgroundImage(boolean showBackgroundImage) {
        this.showBackgroundImage = showBackgroundImage;
    }

    public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    @Override
    public void onSessionStart(SessionContext context) {
        if (showBackgroundImage) {
            String defaultBackground = "/resources/backgrounds/default-bl.jpg";
            context.registerBackgroundImage(BACKGROUND_DEFAULT, defaultBackground, defaultBackground);
        }

        RootPanel rootPanel = new RootPanel();
        context.addRootComponent(null, rootPanel);
        rootPanel.setContent(componentSupplier.apply(context));
        if (defaultBackgroundColor != null) {
            context.setBackgroundColor(defaultBackgroundColor, 0);
        }
        if (showBackgroundImage) {
            context.setBackgroundImage(BACKGROUND_DEFAULT, 0);
        }
    }
}
