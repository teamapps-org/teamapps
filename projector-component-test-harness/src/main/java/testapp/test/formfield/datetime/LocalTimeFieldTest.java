

package testapp.test.formfield.datetime;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.datetime.LocalTimeField;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

import java.time.LocalTime;
import java.util.Arrays;

public class LocalTimeFieldTest extends AbstractFieldTest<LocalTimeField> {

	public LocalTimeFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected LocalTimeField createField() {
		return new LocalTimeField();
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<LocalTimeField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "showDropDownButton", fieldGenerator.createCheckBox("showDropDownButton"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "clearButtonEnabled", fieldGenerator.createCheckBox("clearButtonEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "locale", fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "timeFormat", fieldGenerator.createComboBoxForList("timeFormat",
				DateTimeFieldTests.TIME_FORMAT_ENTRIES,
				instantDateTimeField -> new FormatEntry(instantDateTimeField.getTimeFormat(), "Default (from SessionContext)"),
				(instantDateTimeField, formatEntry) -> instantDateTimeField.setTimeFormat(formatEntry.getFormat())
		));

		Button setValueToNowButton = Button.create(MaterialIcon.HELP, "setValue() to now (server time!)");
		setValueToNowButton.onClick.addListener(() -> {
			printInvocationToConsole("setValue", LocalTime.now());
			getComponent().setValue(LocalTime.now());
		});
		responsiveFormLayout.addLabelAndComponent(setValueToNowButton);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/LocalTimeField.html";
	}

}
