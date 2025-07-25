

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.MultiLineTextField;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class MultiLineTextFieldTest extends AbstractFieldTest<MultiLineTextField> {

	private MultiLineTextField field;
	private String value = "This is some text filling multiple lines in this MultiLineTextField. It is there for demonstration purposes.\nAnother line of text.";

	private final int minHeight = 100;
	private int maxHeight;
	private final int maxCharacters = 0;
	private boolean clearButtonEnabled;
	private String emptyText = "Please insert...";

	public MultiLineTextFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<MultiLineTextField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max characters", fieldGenerator.createNumberField("maxCharacters", 0, 0, 1000, false));

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
			field.setPlaceholderText(emptyText);
		});
		emptyTextTextField.setValue(emptyText);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Emty text (placeholder)", emptyTextTextField);

		Button appendButton = Button.create(MaterialIcon.HELP, "append(\"Appended text\\n\")");
		appendButton.onClick.addListener(() -> {
			String appendedText = "Appended text";
			printInvocationToConsole("append", appendedText + "\n", true);
			field.append(appendedText + "\n", true);
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "append()", appendButton);

		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "adjustHeightToContent", fieldGenerator.createCheckBox("adjustHeightToContent"));
	}

	@Override
	protected MultiLineTextField createField() {
		this.field = new MultiLineTextField();
		field.setMaxCharacters(maxCharacters);
		field.setClearButtonEnabled(clearButtonEnabled);
		field.setPlaceholderText(emptyText);
		field.setValue(value);
		field.onValueChanged.addListener(s -> {
			this.value = s;
		});
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/MultiLineTextField.html";
	}
}
