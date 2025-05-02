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
package org.teamapps.projector.component.gridform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.Field;
import org.teamapps.projector.component.field.FieldMessageSeverity;
import org.teamapps.projector.component.field.validator.MultiFieldValidator;
import org.teamapps.projector.component.gridform.layoutpolicy.FormLayoutPolicy;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyInjector;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGridForm<RECORD> extends AbstractComponent {

	private final DtoGridFormClientObjectChannel clientObjectChannel = new DtoGridFormClientObjectChannel(getClientObjectChannel());

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
		clientObjectChannel.addOrReplaceField(component);
	}

	public abstract List<FormLayoutPolicy> getLayoutPolicies();


	@Override
	public DtoComponentConfig createDto() {
		List<Field> uiFields = logicalForm.getFields().values().stream()
				.collect(Collectors.toList());
		List<DtoFormLayoutPolicy> uiLayoutPolicies = getUiFormLayoutPolicies();
		DtoGridForm uiForm = new DtoGridForm();
		uiForm.setFields(List.copyOf(uiFields));
		uiForm.setLayoutPolicies(uiLayoutPolicies);
		mapAbstractConfigProperties(uiForm);
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
				.map(layoutPolicy -> layoutPolicy != null ? layoutPolicy.createDtoLayoutPolicy() : null)
				.collect(Collectors.toList());
		validateLayoutPolicies(uiFormLayoutPolicies);
		return uiFormLayoutPolicies;
	}

	protected void updateLayoutPolicies() {
		clientObjectChannel.updateLayoutPolicies(getUiFormLayoutPolicies());
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
		clientObjectChannel.setSectionCollapsed(sectionId, collapsed);
	}

	public void addMultiFieldValidator(MultiFieldValidator multiFieldValidator) {
		this.logicalForm.addMultiFieldValidator(multiFieldValidator);
	}

	public FieldMessageSeverity validate() {
		return this.logicalForm.validate();
	}

	/**
	 * Checks whether any of the form fields have been mutated by the client.
	 *
	 * @return true if any of the form fields' values have been changed, false otherwise
	 */
	public boolean isChangedByClient() {
		return logicalForm.isChangedByClient();
	}
}
