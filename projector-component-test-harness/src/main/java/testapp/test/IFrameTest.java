

package testapp.test;

import org.teamapps.projector.component.core.iframe.IFrame;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class IFrameTest extends AbstractComponentTest<IFrame> {

	public IFrameTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator<IFrame> fieldGenerator = new ConfigurationFieldGenerator<>(this.getComponent(), this.getTestContext());
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "URL", fieldGenerator.createTextField("url"));
	}

	@Override
	public IFrame createComponent() {
		return new IFrame("https://en.wikipedia.org/wiki/HTML_element#Frames");
	}

}
