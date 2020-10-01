/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.webcontroller;

import org.teamapps.common.format.Color;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.icons.api.IconThemeImpl;
import org.teamapps.icons.provider.IconProvider;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SimpleWebController implements WebController {

    public final static String BACKGROUND_DEFAULT = "default";

    private final Function<SessionContext, Component> componentSupplier;
    private boolean showBackgroundImage;
    private Color defaultBackgroundColor;

    private final List<IconProvider> additionalIconProvider = new ArrayList<>();

    private IconProvider defaultIconProvider;
    private IconTheme defaultIconTheme;

    public static SimpleWebController createDefaultController(Function<SessionContext, Component> componentSupplier) {
        SimpleWebController webController = new SimpleWebController(componentSupplier);
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

    public void addAdditionalIconProvider(IconProvider iconProvider) {
        additionalIconProvider.add(iconProvider);
    }

    public void setDefaultIconProvider(IconProvider defaultIconProvider) {
        this.defaultIconProvider = defaultIconProvider;
    }

    public void setDefaultIconTheme(IconTheme defaultIconTheme) {
        this.defaultIconTheme = defaultIconTheme;
    }

    @Override
    public IconTheme getDefaultIconTheme(boolean isMobile) {
        if (defaultIconTheme != null) {
            return defaultIconTheme;
        } else if (defaultIconProvider != null) {
            return new IconThemeImpl(isMobile ? defaultIconProvider.getDefaultMobileStyle() : defaultIconProvider.getDefaultDesktopStyle()
                    , defaultIconProvider.getDefaultSubIconStyle());
        } else {
            return WebController.super.getDefaultIconTheme(isMobile);
        }
    }

    @Override
    public IconProvider getIconProvider() {
        if (defaultIconProvider != null) {
            return defaultIconProvider;
        } else {
            return WebController.super.getIconProvider();
        }
    }

    @Override
    public List<IconProvider> getAdditionalIconProvider() {
        return additionalIconProvider;
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
