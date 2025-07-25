

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.toolbar.ToolAccordion;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoComponentsGenerator;

public class ToolAccordionTest extends AbstractComponentTest<ToolAccordion> {

	public ToolAccordionTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "backgroundColor", fieldGenerator.createColorPicker("backgroundColor"));

		}

	@Override
	public ToolAccordion createComponent() {
		ToolAccordion toolAccordion = new ToolAccordion();
		ToolbarButtonGroup buttonGroup1 = new ToolbarButtonGroup();
		ConfigurationFieldGenerator.TOOL_ICON_ENTRIES.stream()
				.limit(10)
				.forEach(iconComboBoxEntry -> {
					ToolbarButton button = new ToolbarButton(iconComboBoxEntry);
					button.setDropDownComponent(DemoComponentsGenerator.createDummyItemView());
					// TODO discuss and add something like: button.setLazyDropDownView();
					buttonGroup1.addButton(button);
				});
		toolAccordion.addButtonGroup(buttonGroup1);
		ToolbarButtonGroup buttonGroup2 = new ToolbarButtonGroup();
		ConfigurationFieldGenerator.FOOD_ICON_ENTRIES.stream()
				.limit(10)
				.forEach(iconComboBoxEntry -> {
					ToolbarButton button = new ToolbarButton(iconComboBoxEntry);
					button.setBackgroundColor(RgbaColor.YELLOW);
					button.setHoverBackgroundColor(RgbaColor.RED);
					buttonGroup2.addButton(button);
				});
		toolAccordion.addButtonGroup(buttonGroup2);
		return toolAccordion;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/ToolAccordion.html";
	}
}
