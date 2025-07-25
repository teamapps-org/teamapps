

package testapp.test.formfield.datetime;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateTimeField;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

import java.time.LocalDateTime;
import java.util.Arrays;

public class LocalDateTimeFieldTest extends AbstractFieldTest<LocalDateTimeField> {

	public LocalDateTimeFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected LocalDateTimeField createField() {
		return new LocalDateTimeField();
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<LocalDateTimeField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "showDropDownButton", fieldGenerator.createCheckBox("showDropDownButton"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "favorPastDates", fieldGenerator.createCheckBox("favorPastDates"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "locale", fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "dateFormat", fieldGenerator.createComboBoxForList("dateFormat",
				DateTimeFieldTests.DATE_FORMAT_ENTRIES,
				instantDateTimeField -> new FormatEntry(instantDateTimeField.getDateFormat(), "Default (from SessionContext)"),
				(instantDateTimeField, formatEntry) -> instantDateTimeField.setDateFormat(formatEntry.getFormat())
		));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "timeFormat", fieldGenerator.createComboBoxForList("timeFormat",
				DateTimeFieldTests.TIME_FORMAT_ENTRIES,
				instantDateTimeField -> new FormatEntry(instantDateTimeField.getTimeFormat(), "Default (from SessionContext)"),
				(instantDateTimeField, formatEntry) -> instantDateTimeField.setTimeFormat(formatEntry.getFormat())
		));

		Button setValueToNowButton = Button.create(MaterialIcon.HELP, "setValue() to now (server time!)");
		setValueToNowButton.onClick.addListener(() -> {
			printInvocationToConsole("setValue", LocalDateTime.now());
			getComponent().setValue(LocalDateTime.now());
		});
		responsiveFormLayout.addLabelAndComponent(setValueToNowButton);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/LocalDateField.html";
	}

}
