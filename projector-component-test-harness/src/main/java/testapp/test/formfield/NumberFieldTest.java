

package testapp.test.formfield;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.component.core.NumberFieldSliderMode;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Arrays;

public class NumberFieldTest extends AbstractFieldTest<NumberField> {

	private NumberField field;
	private Number value = 123.45;

	private final int precision = 2;
	private String emptyText = "Please insert...";
	private boolean clearButtonEnabled = true;

	private double minValue = Long.MIN_VALUE;
	private double maxValue = Long.MAX_VALUE;
	private NumberFieldSliderMode sliderMode = NumberFieldSliderMode.DISABLED;
	private double sliderStep = 10;
	private NumberField maxValueNumberField;
	private NumberField minValueNumberField;

	public NumberFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<NumberField> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Locale", fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Precision", fieldGenerator.createNumberField("precision", 0, 0, 10, false));

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

		minValueNumberField = new NumberField(0);
		minValueNumberField.onValueChanged.addListener(minValue -> {
			this.minValue = minValue.doubleValue();
			printInvocationToConsole("setMinValue", this.minValue);
			this.field.setMinValue(this.minValue);
			if (this.minValue > this.maxValue) {
				this.maxValue = this.minValue + this.sliderStep;
				this.maxValueNumberField.setValue(maxValue);
				this.field.setMaxValue(this.maxValue);
			}
		});
		minValueNumberField.setValue(this.minValue);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Min value", minValueNumberField);

		maxValueNumberField = new NumberField(0);
		maxValueNumberField.onValueChanged.addListener(maxValue -> {
			this.maxValue = maxValue.doubleValue();
			printInvocationToConsole("setMaxValue", this.maxValue);
			this.field.setMaxValue(this.maxValue);
			if (this.minValue > this.maxValue) {
				this.minValue = this.maxValue - this.sliderStep;
				this.minValueNumberField.setValue(minValue);
				this.field.setMinValue(this.minValue);
			}
		});
		maxValueNumberField.setValue(this.maxValue);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max value", maxValueNumberField);

		ComboBox<NumberFieldSliderMode> sliderModeComboBox = ComboBox.createForList(Arrays.asList(NumberFieldSliderMode.values()));
		sliderModeComboBox.onValueChanged.addListener(sliderMode -> {
			this.sliderMode = sliderMode;
			printInvocationToConsole("setSliderMode", sliderMode);
			this.field.setSliderMode(sliderMode);
		});
		sliderModeComboBox.setValue(this.sliderMode);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Slider mode", sliderModeComboBox);

		NumberField sliderStepNumberField = new NumberField(0);
		sliderStepNumberField.onValueChanged.addListener(sliderStep -> {
			this.sliderStep = sliderStep.doubleValue();
			printInvocationToConsole("setSliderStep", this.sliderStep);
			this.field.setSliderStep(this.sliderStep);
		});
		sliderStepNumberField.setValue(this.sliderStep);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Slider step", sliderStepNumberField);
	}

	@Override
	protected NumberField createField() {
		field = new NumberField(this.precision);
		field.setClearButtonEnabled(clearButtonEnabled);
		field.setPlaceholderText(emptyText);
		field.setValue(value);
		field.setMinValue(minValue);
		field.setMaxValue(maxValue);
		field.setSliderMode(sliderMode);
		field.setSliderStep(sliderStep);
		field.onValueChanged.addListener(number -> {
			value = number;
		});
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/NumberField.html";
	}
}
