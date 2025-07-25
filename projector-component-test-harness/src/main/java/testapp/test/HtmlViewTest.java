package testapp.test;

import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.html.HtmlView;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.Util;

public class HtmlViewTest extends AbstractComponentTest<HtmlView> {

	public HtmlViewTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

	}

	@Override
	public HtmlView createComponent() {
		HtmlView htmlView = new HtmlView(Util.readResourceToString("/org/teamapps/ux/testapp/demodata/html-view-test.html"));
		htmlView.addComponent(".componentContainer", new DummyComponent());
		return htmlView;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/HtmlView.html";
	}

}
