

package testapp.test.formfield.datetime;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.datetime.InstantDateTimeField;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

public class InstantDateTimeFieldTest extends AbstractFieldTest<InstantDateTimeField> {

	public InstantDateTimeFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected InstantDateTimeField createField() {
		return new InstantDateTimeField();
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<InstantDateTimeField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

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
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "timeZoneId", fieldGenerator.createComboBoxForList("timeZoneId",
				Arrays.asList(ZoneId.of("UTC"), ZoneId.of("Europe/Berlin"), ZoneId.of("America/New_York"))));

		Button setValueToNowButton = Button.create(MaterialIcon.HELP, "setValue() to now (server time!)");
		setValueToNowButton.onClick.addListener(aBoolean -> {
			printInvocationToConsole("setValue", Instant.now());
			getComponent().setValue(Instant.now());
		});
		responsiveFormLayout.addLabelAndComponent(setValueToNowButton);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/LocalDateField.html";
	}

}
