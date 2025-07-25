

package testapp.test.formfield;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.Label;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.FieldMessage;
import org.teamapps.projector.component.field.FieldMessagePosition;
import org.teamapps.projector.component.field.FieldMessageVisibility;
import org.teamapps.projector.component.gridform.ResponsiveForm;
import org.teamapps.projector.component.gridform.ResponsiveFormConfigurationTemplate;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.gridform.ResponsiveFormSection;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.JustifyContent;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.teamapps.projector.component.field.FieldMessageSeverity.*;

public abstract class AbstractFieldTest<FIELD extends AbstractField<?>> extends AbstractComponentTest<FIELD> {

	private boolean showInfoMessage;
	private boolean showSuccessMessage;
	private boolean showWarningMessage;
	private boolean showErrorMessage;
	private FieldMessagePosition fieldMessagePosition = FieldMessagePosition.BELOW;
	private FieldMessageVisibility fieldMessageVisibility = FieldMessageVisibility.ALWAYS_VISIBLE;

	public AbstractFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<FIELD> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Editing mode", fieldGenerator.createComboBoxForEnum("editingMode"));

		ResponsiveFormSection validationSection = responsiveFormLayout.addSection(MaterialIcon.HELP, "Validation");
		validationSection.setGridGap(5);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Required", fieldGenerator.createCheckBox("required"));

		Button validateButton = Button.create("Validate");
		validateButton.onClick.addListener(() -> getComponent().validate());
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.CHECK, "Validate", validateButton);

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Custom Field Messages").setGridGap(5);

		CheckBox infoMessageCheckBox = new CheckBox();
		infoMessageCheckBox.onValueChanged.addListener(value -> {
			this.showInfoMessage = value;
			updateCustomFieldMessages();
		});
		infoMessageCheckBox.setValue(getComponent().getFieldMessages().stream().anyMatch(m -> m.getSeverity() == INFO));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Info message", infoMessageCheckBox);

		CheckBox successMessageCheckBox = new CheckBox();
		successMessageCheckBox.onValueChanged.addListener(value -> {
			this.showSuccessMessage = value;
			updateCustomFieldMessages();
		});
		successMessageCheckBox.setValue(getComponent().getFieldMessages().stream().anyMatch(m -> m.getSeverity() == SUCCESS));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Success message", successMessageCheckBox);

		CheckBox warningMessageCheckBox = new CheckBox();
		warningMessageCheckBox.onValueChanged.addListener(value -> {
			this.showWarningMessage = value;
			updateCustomFieldMessages();
		});
		warningMessageCheckBox.setValue(getComponent().getFieldMessages().stream().anyMatch(m -> m.getSeverity() == WARNING));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Warning message", warningMessageCheckBox);

		CheckBox errorMessageCheckBox = new CheckBox();
		errorMessageCheckBox.onValueChanged.addListener(value -> {
			this.showErrorMessage = value;
			updateCustomFieldMessages();
		});
		errorMessageCheckBox.setValue(getComponent().getFieldMessages().stream().anyMatch(m -> m.getSeverity() == ERROR));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Error message", errorMessageCheckBox);

		ComboBox<FieldMessagePosition> fieldMessagePositionComboBox = ComboBox.createForList(Arrays.asList(FieldMessagePosition.values()));
		fieldMessagePositionComboBox.onValueChanged.addListener(position -> {
			this.fieldMessagePosition = position;
			updateCustomFieldMessages();
		});
		fieldMessagePositionComboBox.setValue(this.fieldMessagePosition);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Messages position", fieldMessagePositionComboBox);

		ComboBox<FieldMessageVisibility> fieldMessageVisibilityModeComboBox = ComboBox.createForList(Arrays.asList(FieldMessageVisibility.values()));
		fieldMessageVisibilityModeComboBox.onValueChanged.addListener(visibilityMode -> {
			this.fieldMessageVisibility = visibilityMode;
			updateCustomFieldMessages();
		});
		fieldMessageVisibilityModeComboBox.setValue(this.fieldMessageVisibility);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Messages visibility mode", fieldMessageVisibilityModeComboBox);

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Util").setGridGap(5);
		Button button = Button.create(MaterialIcon.CLEAR, "Clear value");
		button.onClick.addListener(() -> getComponent().setValue(null));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Clear value", button);

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		this.addConfigurationFields(responsiveFormLayout);
	}

	private void updateCustomFieldMessages() {
		List<FieldMessage> fieldMessages = new ArrayList<FieldMessage>();
		if (this.showInfoMessage) {
			fieldMessages.add(new FieldMessage(this.fieldMessagePosition, this.fieldMessageVisibility, INFO, "This is an info message."));
		}
		if (this.showSuccessMessage) {
			fieldMessages.add(new FieldMessage(this.fieldMessagePosition, this.fieldMessageVisibility, SUCCESS, "This is a success message."));
		}
		if (this.showWarningMessage) {
			fieldMessages.add(new FieldMessage(this.fieldMessagePosition, this.fieldMessageVisibility, WARNING, "This is a warning message."));
		}
		if (this.showErrorMessage) {
			fieldMessages.add(new FieldMessage(this.fieldMessagePosition, this.fieldMessageVisibility, ERROR, "This is an error message."));
		}
		getTestContext().printInvocationToConsole("setFieldMessages", fieldMessages);
		getComponent().setCustomFieldMessages(fieldMessages);
	}

	@Override
	public final FIELD createComponent() {
		return createField();
	}

	@Override
	public Component wrapComponent(FIELD field) {
		ResponsiveForm<HashMap> form = new ResponsiveForm<>();
		form.setConfigurationTemplate(ResponsiveFormConfigurationTemplate.createDefaultTwoColumnTemplate(100, 0));
		ResponsiveFormLayout responsiveFormLayout = form.addResponsiveFormLayout(400);
		ResponsiveFormSection responsiveFormSection = responsiveFormLayout.addSection().setCollapsible(true).setHideWhenNoVisibleFields(true);

		ResponsiveFormLayout.LabelAndField labelAndField = responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Field", field);
		((Label) labelAndField.label.getField()).setTargetComponent(field);
		labelAndField.field // TODO encapsulation?
				.setHorizontalAlignment(JustifyContent.STRETCH)
				.setVerticalAlignment(AlignItems.STRETCH);
//				.getRowDefinition().setHeightPolicy(SizingPolicy.FILL_REMAINING); // TODO how to do it if I do NOT want to go by the field

		return form;
	}

	protected abstract FIELD createField();

	protected abstract void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout);

}
