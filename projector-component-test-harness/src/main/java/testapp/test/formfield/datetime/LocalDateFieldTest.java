

package testapp.test.formfield.datetime;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateField;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

import java.time.LocalDate;
import java.util.Arrays;

public class LocalDateFieldTest extends AbstractFieldTest<LocalDateField> {

	public LocalDateFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected LocalDateField createField() {
		return new LocalDateField();
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<LocalDateField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "showDropDownButton", fieldGenerator.createCheckBox("showDropDownButton"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "clearButtonEnabled", fieldGenerator.createCheckBox("clearButtonEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "favorPastDates", fieldGenerator.createCheckBox("favorPastDates"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "locale", fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "dateFormat", fieldGenerator.createComboBoxForList("dateFormat",
				DateTimeFieldTests.DATE_FORMAT_ENTRIES,
				instantDateTimeField -> new FormatEntry(instantDateTimeField.getDateFormat(), "Default (from SessionContext)"),
				(instantDateTimeField, formatEntry) -> instantDateTimeField.setDateFormat(formatEntry.getFormat())
		));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "defaultSuggestionDate", fieldGenerator.createLocalDateField("defaultSuggestionDate", true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "shuffledFormatSuggestionsEnabled", fieldGenerator.createCheckBox("shuffledFormatSuggestionsEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "dropDownMode", fieldGenerator.createComboBoxForEnum("dropDownMode"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "emptyText", fieldGenerator.createTextField("emptyText"));

		Button setValueToNowButton = Button.create(MaterialIcon.HELP, "setValue() to now (server time!)");
		setValueToNowButton.onClick.addListener(() -> {
			printInvocationToConsole("setValue", LocalDate.now());
			getComponent().setValue(LocalDate.now());
		});
		responsiveFormLayout.addLabelAndComponent(setValueToNowButton);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/LocalDateField.html";
	}


}
