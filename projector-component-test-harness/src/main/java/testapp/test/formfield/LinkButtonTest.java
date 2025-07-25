

package testapp.test.formfield;

import org.teamapps.projector.component.core.linkbutton.LinkButton;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class LinkButtonTest extends AbstractComponentTest<LinkButton> {

	public LinkButtonTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<LinkButton> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Text", fieldGenerator.createTextField("text"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "URL", fieldGenerator.createTextField("url"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Target", fieldGenerator.createComboBoxForEnum("target"));
	}

	@Override
	protected LinkButton createComponent() {
		return new LinkButton("This is a link button");
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/LinkButton.html";
	}

}
