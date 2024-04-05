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

import org.teamapps.dto.DtoComponentField;
import org.teamapps.dto.DtoAbstractField;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class ComponentField extends AbstractField<Void> {

    private org.teamapps.ux.component.Component component;
    private int height; // 0 = auto-height
    private boolean bordered = true;

    public ComponentField(org.teamapps.ux.component.Component component) {
       this(component, 0);
    }

    public ComponentField(org.teamapps.ux.component.Component component, int height) {
        this.component = component;
        this.height = height;
    }

    @Override
    public DtoAbstractField createDto() {
        DtoComponentField uiField = new DtoComponentField();
        mapAbstractFieldAttributesToUiField(uiField);
        uiField.setComponent(ClientObject.createDtoReference(component));
        uiField.setHeight(height);
        uiField.setBordered(bordered);
        return uiField;
    }

    public org.teamapps.ux.component.Component getComponent() {
        return component;
    }

    public void setComponent(org.teamapps.ux.component.Component component) {
        this.component = component;
        sendCommandIfRendered(() -> new DtoComponentField.SetComponentCommand(ClientObject.createDtoReference(component)));
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        sendCommandIfRendered(() -> new DtoComponentField.SetHeightCommand(height));
    }

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
        sendCommandIfRendered(() -> new DtoComponentField.SetBorderedCommand(bordered));
    }

}
