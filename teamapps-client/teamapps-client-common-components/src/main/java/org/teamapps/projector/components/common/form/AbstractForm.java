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
package org.teamapps.projector.components.common.form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.*;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.projector.components.common.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.validator.MultiFieldValidator;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyInjector;
import org.teamapps.projector.dataextraction.PropertyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractForm<RECORD> extends AbstractComponent implements Component {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<FieldChangeEventData> onFieldValueChanged = new ProjectorEvent<>();

	private final LogicalForm<RECORD> logicalForm = new LogicalForm<>();
	private final List<Component> children = new ArrayList<>();


	public void clearAllFields() {
		getFields().forEach(field -> field.setValue(null));
	}

	/**
	 * convenience method for field.setValue(...).
	 * Caution: this method is NOT typesafe! You can set any value type, but this would probably cause runtime errors.
	 */
	public void setFieldValue(String fieldName, Object value) {
		getFieldByPropertyName(fieldName).setValue(value);
	}

	/**
	 * convenience method for field.getValue()
	 */
	public Object getFieldValue(String fieldName) {
		return getFieldByPropertyName(fieldName).getValue();
	}

	protected void addField(String propertyName, AbstractField<?> field) {
		addComponent(field);
		logicalForm.addField(propertyName, field);
		field.onValueChanged.addListener(value -> {
			onFieldValueChanged.fire(new FieldChangeEventData(propertyName, field, value));
		});
	}

	protected void addComponent(Component component) {
		children.add(component);
		component.setParent(this);
		sendCommandIfRendered(() -> new DtoGridForm.AddOrReplaceFieldCommand(component.createDtoReference()));
	}

	public abstract List<FormLayoutPolicy> getLayoutPolicies();

	@Override
	public DtoComponent createDto() {
		List<DtoReference> uiFields = logicalForm.getFields().values().stream()
				.map(field -> field != null ? field.createDtoReference() : null)
				.collect(Collectors.toList());
		List<DtoFormLayoutPolicy> uiLayoutPolicies = getUiFormLayoutPolicies();
		DtoGridForm uiForm = new DtoGridForm(uiFields, uiLayoutPolicies);
		mapAbstractUiComponentProperties(uiForm);
		return uiForm;
	}

	private void validateLayoutPolicies(List<DtoFormLayoutPolicy> uiLayoutPolicies) {
		uiLayoutPolicies.stream()
				.flatMap(policy -> policy.getSections().stream())
				.forEach(section -> {
					int numberOfRows = section.getRows().size();
					section.getFieldPlacements().stream()
							.filter(fp -> fp.getRow() + fp.getRowSpan() > numberOfRows)
							.forEach(fp -> LOGGER.error("FieldPlacement to non-existing row! Number of rows: " + numberOfRows + "; row: " + fp.getRow() + "; rowSpan: " + fp.getRowSpan()));
					int numberOfColumns = section.getColumns().size();
					section.getFieldPlacements().stream()
							.filter(fp -> fp.getColumn() + fp.getColSpan() > numberOfColumns)
							.forEach(fp -> LOGGER.error("FieldPlacement to non-existing column! Number of columns: " + numberOfColumns + "; column: " + fp.getColumn() + "; colSpan: " + fp.getColSpan()));
				});
	}

	private List<DtoFormLayoutPolicy> getUiFormLayoutPolicies() {
		List<DtoFormLayoutPolicy> uiFormLayoutPolicies = getLayoutPolicies().stream()
				.map(layoutPolicy -> layoutPolicy != null ? layoutPolicy.createUiLayoutPolicy() : null)
				.collect(Collectors.toList());
		validateLayoutPolicies(uiFormLayoutPolicies);
		return uiFormLayoutPolicies;
	}

	protected void updateLayoutPolicies() {
		List<DtoFormLayoutPolicy> uiFormLayoutPolicies = getUiFormLayoutPolicies();
		sendCommandIfRendered(() -> new DtoGridForm.UpdateLayoutPoliciesCommand(uiFormLayoutPolicies));
	}

	public void applyRecordValuesToFields(RECORD record) {
		logicalForm.applyRecordValuesToFields(record);
	}

	public void applyFieldValuesToRecord(RECORD record) {
		logicalForm.applyFieldValuesToRecord(record);
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return logicalForm.getPropertyProvider();
	}

	public void setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		logicalForm.setPropertyProvider(propertyProvider);
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		logicalForm.setPropertyExtractor(propertyExtractor);
	}

	public PropertyInjector<RECORD> getPropertyInjector() {
		return logicalForm.getPropertyInjector();
	}

	public void setPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		logicalForm.setPropertyInjector(propertyInjector);
	}


	@Override
	public void handleUiEvent(DtoEventWrapper event) {
	}

	private List<String> getPropertyNames() {
		return new ArrayList<>(logicalForm.getFields().keySet());
	}

	public List<AbstractField<?>> getFields() {
		return new ArrayList<>(logicalForm.getFields().values());
	}


	public <V> AbstractField<V> getFieldByPropertyName(String propertyName) {
		return (AbstractField<V>) logicalForm.getFields().get(propertyName);
	}

	public List<Component> getAllChildren() {
		return children;
	}

	public void setSectionCollapsed(String sectionId, boolean collapsed) {
		sendCommandIfRendered(() -> new DtoGridForm.SetSectionCollapsedCommand(sectionId, collapsed));
	}

	public void addMultiFieldValidator(MultiFieldValidator multiFieldValidator) {
		this.logicalForm.addMultiFieldValidator(multiFieldValidator);
	}

	public FieldMessage.Severity validate() {
		return this.logicalForm.validate();
	}
}
