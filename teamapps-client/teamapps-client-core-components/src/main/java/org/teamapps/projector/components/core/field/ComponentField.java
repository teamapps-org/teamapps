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
package org.teamapps.projector.components.core.field;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.dto.*;
import org.teamapps.projector.field.AbstractField;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class ComponentField extends AbstractField<Void> implements DtoComponentFieldEventHandler {

    private final DtoComponentFieldClientObjectChannel clientObjectChannel = new DtoComponentFieldClientObjectChannel(getClientObjectChannel());

    private Component component;
    private int height; // 0 = auto-height
    private boolean bordered = true;

    public ComponentField(Component component) {
       this(component, 0);
    }

    public ComponentField(Component component, int height) {
        this.component = component;
        this.height = height;
    }

    @Override
    public DtoAbstractField createConfig() {
        DtoComponentField uiField = new DtoComponentField();
        mapAbstractFieldAttributesToUiField(uiField);
        uiField.setComponent(component);
        uiField.setHeight(height);
        uiField.setBordered(bordered);
        return uiField;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
		clientObjectChannel.setComponent(component);
	}

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
		clientObjectChannel.setHeight(height);
	}

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
		clientObjectChannel.setBordered(bordered);
	}

    @Override
    public Void doConvertClientValueToServerValue(JsonWrapper value) {
        return null;
    }

}
