

package testapp.test.formfield;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.money.CurrencyField;
import org.teamapps.projector.component.treecomponents.money.value.CurrencyUnit;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.math.BigDecimal;
import java.util.Arrays;

public class CurrencyFieldTest extends AbstractFieldTest<CurrencyField> {

	private CurrencyField field;

	public CurrencyFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<CurrencyField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "locale", fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "currencies", fieldGenerator.createTagComboBoxForList("currencies", CurrencyUnit.getAllAvailableFromJdk()));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "fixedPrecision", fieldGenerator.createNumberField("fixedPrecision", 0, -1, 10, true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "alphabeticKeysQueryEnabled", fieldGenerator.createCheckBox("alphabeticKeysQueryEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "currencyBeforeAmount", fieldGenerator.createCheckBox("currencyBeforeAmount"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "currencySymbolsEnabled", fieldGenerator.createCheckBox("currencySymbolsEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "amount", fieldGenerator.createNumberField("amount",
				() -> getComponent().getAmount().orElse(null),
				number -> getComponent().setAmount(number != null ? new BigDecimal(number.toString()): null),
				4, -10_000_000_0000d, 10_000_000_0000d, true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "currency", fieldGenerator.createComboBoxForList("currency",
				CurrencyUnit.getAllAvailableFromJdk(),
				component -> component.getCurrency().orElse(null),
				CurrencyField::setCurrency,
				true));

		Button clearButton = Button.create("clear");
		clearButton.onClick.addListener((eventData, disposable) -> getComponent().setValue(null));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "clear", clearButton);
	}

	@Override
	protected CurrencyField createField() {
		return new CurrencyField();
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/CurrencyField.html";
	}

}
