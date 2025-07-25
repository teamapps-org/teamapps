

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.PasswordField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class PasswordFieldTest extends AbstractFieldTest<PasswordField> {

	private PasswordField field;

	private final int maxCharacters = 50;
	private boolean clearButtonEnabled;
	private String emptyText = "Please insert...";
	private boolean sendValueAsMd5 = true;
	private String salt = "sd98df98df";

	public PasswordFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<PasswordField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max characters", fieldGenerator.createNumberField("maxCharacters", 0, 0, 100, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "clearButtonEnabled", fieldGenerator.createCheckBox("clearButtonEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "emptyText / placeholder text", fieldGenerator.createTextField("emptyText"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "passwordVisibilityToggleEnabled", fieldGenerator.createCheckBox("passwordVisibilityToggleEnabled"));
	}

	@Override
	protected PasswordField createField() {
		field = new PasswordField();
		field.setMaxCharacters(maxCharacters);
		field.setClearButtonEnabled(clearButtonEnabled);
		field.setPlaceholderText(emptyText);
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/PasswordField.html";
	}
}
