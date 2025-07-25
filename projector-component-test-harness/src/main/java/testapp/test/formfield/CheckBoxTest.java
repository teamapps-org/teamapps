

package testapp.test.formfield;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.ColorPicker;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;

public class CheckBoxTest extends AbstractFieldTest {

	private CheckBox field;
	private boolean fieldValue = true;

	private String caption = "Some caption";
	private Color backgroundColor = new RgbaColor(255, 255, 255);
	private Color checkColor = new RgbaColor(70, 70, 70);
	private Color borderColor = new RgbaColor(204, 204, 204);

	public CheckBoxTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		TextField captionTextField = new TextField();
		captionTextField.onTextInput.addListener(caption -> {
			this.caption = caption;
			printInvocationToConsole("setCaption", caption);
			field.setCaption(caption);
		});
		captionTextField.setValue(caption);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Caption", captionTextField);

		ColorPicker backgroundColorPicker = new ColorPicker();
		backgroundColorPicker.onValueChanged.addListener(color -> {
			this.backgroundColor = color;
			printInvocationToConsole("setBackgroundColor", color);
			field.setBackgroundColor(color);
		});
		backgroundColorPicker.setValue(backgroundColor);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", backgroundColorPicker);

		ColorPicker checkColorPicker = new ColorPicker();
		checkColorPicker.onValueChanged.addListener(color -> {
			this.checkColor = color;
			printInvocationToConsole("setCheckColor", color);
			field.setCheckColor(color);
		});
		checkColorPicker.setValue(checkColor);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Check color", checkColorPicker);

		ColorPicker borderColorPicker = new ColorPicker();
		borderColorPicker.onValueChanged.addListener(color -> {
			this.borderColor = color;
			printInvocationToConsole("setBorderColor", color);
			field.setBorderColor(color);
		});
		borderColorPicker.setValue(borderColor);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Border color", borderColorPicker);
	}

	@Override
	protected AbstractField createField() {
		field = new CheckBox();
		field.setCaption(caption);
		field.setBackgroundColor(backgroundColor);
		field.setCheckColor(checkColor);
		field.setBorderColor(borderColor);
		field.setValue(fieldValue);
		field.onValueChanged.addListener(value -> {
			fieldValue = value;
		});
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/CheckBox.html";
	}
}
