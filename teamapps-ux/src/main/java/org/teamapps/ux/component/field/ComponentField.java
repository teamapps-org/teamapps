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
package org.teamapps.ux.component.field;

import org.teamapps.dto.UiComponentField;
import org.teamapps.dto.UiField;
import org.teamapps.ux.component.Component;

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
    public UiField createUiComponent() {
        UiComponentField uiField = new UiComponentField();
        mapAbstractFieldAttributesToUiField(uiField);
        uiField.setComponent(Component.createUiClientObjectReference(component));
        uiField.setHeight(height);
        uiField.setBordered(bordered);
        return uiField;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        queueCommandIfRendered(() -> new UiComponentField.SetComponentCommand(getId(), Component.createUiClientObjectReference(component)));
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        queueCommandIfRendered(() -> new UiComponentField.SetHeightCommand(getId(), height));
    }

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
        queueCommandIfRendered(() -> new UiComponentField.SetBorderedCommand(getId(), bordered));
    }

}
