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

import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiFieldGroup;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.flexcontainer.FlexSizeUnit;
import org.teamapps.ux.component.flexcontainer.FlexSizingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldGroup extends AbstractComponent {

	private final List<AbstractField> fields = new ArrayList<>();

	@Override
	public UiFieldGroup createUiClientObject() {
		UiFieldGroup uiFieldGroup = new UiFieldGroup();
		mapAbstractUiComponentProperties(uiFieldGroup);
		uiFieldGroup.setFields(createUiFieldReferences());
		return uiFieldGroup;
	}

	private List<UiClientObjectReference> createUiFieldReferences() {
		return fields.stream()
				.map(c -> c.createUiReference())
				.collect(Collectors.toList());
	}

	private void addField(AbstractField field, int index) {
		this.fields.remove(field);
		if (index > this.fields.size()) {
			index = this.fields.size();
		}
		this.fields.add(index, field);
		queueCommandIfRendered(() -> new UiFieldGroup.SetFieldsCommand(createUiFieldReferences()));
	}

	public void addField(AbstractField field, FlexSizingPolicy sizingPolicy) {
		field.setCssStyle("flex", sizingPolicy.toCssValue());
		addField(field, this.fields.size());
	}

	public void addFieldFillRemaining(AbstractField field) {
		addField(field, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1));
	}

	public void addFieldAutoSize(AbstractField field) {
		addField(field, new FlexSizingPolicy( 0, 0));
	}

	public void addField(AbstractField field, FlexSizingPolicy sizingPolicy, int index) {
		field.setCssStyle("flex", sizingPolicy.toCssValue());
		addField(field, index);
	}

	public void addFieldFillRemaining(AbstractField field, int index) {
		addField(field, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1), index);
	}

	public void addFieldAutoSize(AbstractField field, int index) {
		addField(field, new FlexSizingPolicy( 0, 0), index);
	}

	public void removeField(AbstractField field) {
		this.fields.remove(field);
		queueCommandIfRendered(() -> new UiFieldGroup.SetFieldsCommand(createUiFieldReferences()));
	}

}
