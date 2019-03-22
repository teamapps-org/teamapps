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
package org.teamapps.ux.component.form;

import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyInjector;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.form.layoutpolicy.FormLayoutPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResponsiveForm<RECORD> extends AbstractForm<ResponsiveForm, RECORD> {

	private List<ResponsiveFormLayout> responsiveFormLayouts = new ArrayList<>();
	private boolean createSmallScreenLayoutIfMissing = true;
	private ResponsiveFormConfigurationTemplate configurationTemplate = new ResponsiveFormConfigurationTemplate();


	public ResponsiveForm() {
	}

	public ResponsiveForm(ResponsiveFormConfigurationTemplate configurationTemplate) {
		this.configurationTemplate = configurationTemplate;
	}

	public ResponsiveForm(int minLabelWidth, int fieldMinWidth, int fieldMaxWidth) {
		this.configurationTemplate = ResponsiveFormConfigurationTemplate.createDefaultTwoColumnTemplate(minLabelWidth, fieldMinWidth, fieldMaxWidth);
	}


	public ResponsiveFormLayout addResponsiveFormLayout(int minWidth) {
		return addResponsiveFormLayout(minWidth, null);
	}

	public ResponsiveFormLayout addResponsiveFormLayout(int minWidth, ResponsiveFormConfigurationTemplate configurationTemplate) {
		if (configurationTemplate == null) {
			configurationTemplate = this.configurationTemplate;
		}
		ResponsiveFormLayout responsiveFormLayout = new ResponsiveFormLayout(minWidth, this, configurationTemplate);
		responsiveFormLayouts.add(responsiveFormLayout);
		return responsiveFormLayout;
	}

	protected void addLayoutField(String propertyName, AbstractField field) {
		if (getFieldByPropertyName(propertyName) == null) {
			addField(propertyName, field);
		}
	}


	@Override
	public List<FormLayoutPolicy> getLayoutPolicies() {
		List<FormLayoutPolicy> policies = new ArrayList<>();
		responsiveFormLayouts.stream().forEach(responsiveFormLayout -> {
			policies.add(responsiveFormLayout.createFormLayoutPolicy());
		});
		ResponsiveFormLayout smallestLayout = responsiveFormLayouts.stream()
				.sorted(Comparator.comparingInt(ResponsiveFormLayout::getMinWidth))
				.findFirst()
				.orElseGet(() -> null);
		if (createSmallScreenLayoutIfMissing && smallestLayout != null && smallestLayout.getMinWidth() > 0) {
			policies.add(smallestLayout.createSmallScreenLayout());
		}
		return policies;
	}

	public boolean isCreateSmallScreenLayoutIfMissing() {
		return createSmallScreenLayoutIfMissing;
	}

	public void setCreateSmallScreenLayoutIfMissing(boolean createSmallScreenLayoutIfMissing) {
		this.createSmallScreenLayoutIfMissing = createSmallScreenLayoutIfMissing;
	}

	public ResponsiveFormConfigurationTemplate getConfigurationTemplate() {
		return configurationTemplate;
	}

	public void setConfigurationTemplate(ResponsiveFormConfigurationTemplate configurationTemplate) {
		this.configurationTemplate = configurationTemplate;
	}
}
