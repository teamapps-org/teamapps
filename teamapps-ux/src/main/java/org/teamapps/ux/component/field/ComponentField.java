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
package org.teamapps.ux.component.field;

import org.teamapps.projector.dto.DtoComponentField;
import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class ComponentField extends AbstractField<Void> {

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
        uiField.setComponent(ClientObject.createClientReference(component));
        uiField.setHeight(height);
        uiField.setBordered(bordered);
        return uiField;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
		getClientObjectChannel().sendCommandIfRendered(new DtoComponentField.SetComponentCommand(ClientObject.createClientReference(component)), null);
	}

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
		getClientObjectChannel().sendCommandIfRendered(new DtoComponentField.SetHeightCommand(height), null);
	}

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
		getClientObjectChannel().sendCommandIfRendered(new DtoComponentField.SetBorderedCommand(bordered), null);
	}

}
