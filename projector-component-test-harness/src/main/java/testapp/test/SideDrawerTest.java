package testapp.test;

import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.sidedrawer.DrawerPosition;
import org.teamapps.projector.component.sidedrawer.SideDrawer;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class SideDrawerTest extends AbstractComponentTest<DummyComponent> {

	private SideDrawer sideDrawer;

	public SideDrawerTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator<>(sideDrawer, this.getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Visible", fieldGenerator.createCheckBox("visible"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Position", fieldGenerator.createComboBoxForEnum("position"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Width", fieldGenerator.createNumberField("width", 0, -1, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Height", fieldGenerator.createNumberField("height", 0, -1, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Margin X", fieldGenerator.createNumberField("marginX", 0, 0, 50, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Margin Y", fieldGenerator.createNumberField("marginY", 0, 0, 50, false));
		CheckBox expandedCheckBox = fieldGenerator.createCheckBox("expanded");
		sideDrawer.onExpandedOrCollapsed.addListener(expandedCheckBox::setValue);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Expanded", expandedCheckBox);
		// responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", fieldGenerator.createColorPicker("backgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Expander handle color", fieldGenerator.createColorPicker("expanderHandleColor"));

		Button button = Button.create("setContent");
		button.onClick.addListener(aBoolean -> {
			sideDrawer.setContentComponent(new DummyComponent(System.currentTimeMillis() + ""));
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove", button);

		// Button button = Button.create("remove");
		// button.onValueChanged.addListener(aBoolean -> {
		// 	sideDrawer.unrender();
		// });
		// responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove", button);
	}                           

	@Override
	public DummyComponent createComponent() {
		DummyComponent dummyComponent = new DummyComponent();
		DummyComponent contentComponent = new DummyComponent();
		// Panel panel = new Panel(MaterialIcon.ARROW_BACK, "My floating component", dummyComponent);

		sideDrawer = new SideDrawer(contentComponent, dummyComponent);
		sideDrawer.setMarginX(10);
		sideDrawer.setMarginY(10);
		sideDrawer.setPosition(DrawerPosition.TOP_LEFT);
		sideDrawer.setWidth(300);
		sideDrawer.setHeight(300);
		sideDrawer.setCollapsible(true);

		sideDrawer.render();
		return dummyComponent;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/MapView.html";
	}

}
