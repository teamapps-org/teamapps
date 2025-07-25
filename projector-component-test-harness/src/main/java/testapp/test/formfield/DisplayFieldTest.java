

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.util.Util;

public class DisplayFieldTest extends AbstractFieldTest {

	private DisplayField field;

	private boolean showBorder;
	private boolean showHtml;

	public DisplayFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		CheckBox showBorderCheckBox = new CheckBox("Show border");
		showBorderCheckBox.onValueChanged.addListener(showBorder -> {
			this.showBorder = showBorder;
			printInvocationToConsole("setShowBorder", showBorder);
			field.setShowBorder(showBorder);
		});
		showBorderCheckBox.setValue(showBorder);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show border", showBorderCheckBox);

		CheckBox showHtmlCheckBox = new CheckBox("Show HTML");
		showHtmlCheckBox.onValueChanged.addListener(showHtml -> {
			this.showHtml = showHtml;
			printInvocationToConsole("setShowHtml", showHtml);
			field.setShowHtml(showHtml);
		});
		showHtmlCheckBox.setValue(showHtml);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show HTML", showHtmlCheckBox);
	}

	@Override
	protected AbstractField createField() {
		field = new DisplayField(this.showBorder, this.showHtml);
		field.setValue(Util.readResourceToString("org/teamapps/ux/testapp/demodata/richtexteditor-demo-text.html"));
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/DisplayField.html";
	}
}
