

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.field.FieldMessage;
import org.teamapps.projector.component.field.FieldMessageSeverity;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Collections;

public class TextFieldTest extends AbstractFieldTest<TextField> {

	private TextField field;
	private String value = "Some text";

	private final int maxCharacters = 50;
	private boolean clearButtonEnabled;
	private String emptyText = "Please insert...";

	public TextFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<TextField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max characters", fieldGenerator.createNumberField("maxCharacters", 0, 0, 100, false));

		CheckBox clearButtonEnabledCheckBox = new CheckBox("Show clear button");
		clearButtonEnabledCheckBox.onValueChanged.addListener(clearButtonEnabled -> {
			this.clearButtonEnabled = clearButtonEnabled;
			printInvocationToConsole("setClearButtonEnabled", clearButtonEnabled);
			field.setClearButtonEnabled(clearButtonEnabled);
		});
		clearButtonEnabledCheckBox.setValue(clearButtonEnabled);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show clear button", clearButtonEnabledCheckBox);

		TextField emptyTextTextField = new TextField();
		emptyTextTextField.onValueChanged.addListener(emptyText -> {
			this.emptyText = emptyText;
			printInvocationToConsole("setPlaceholderText", emptyText);
			this.field.setPlaceholderText(emptyText);
		});
		emptyTextTextField.setValue(emptyText);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Emty text (placeholder)", emptyTextTextField);

		getComponent().addValidator(s -> {
			if (!s.contains("@")) {
				return Collections.singletonList(new FieldMessage(FieldMessageSeverity.ERROR, "Must contain an email address."));
			} else {
				return null;
			}
		});
	}

	@Override
	protected TextField createField() {
		field = new TextField();
		field.setValue(value);
		field.setMaxCharacters(maxCharacters);
		field.setClearButtonEnabled(clearButtonEnabled);
		field.setPlaceholderText(emptyText);
		field.onValueChanged.addListener(s -> {
			this.value = s;
		});
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/TextField.html";
	}
}
