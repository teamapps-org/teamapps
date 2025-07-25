

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.toolbar.Toolbar;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoDataGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class ToolbarTest extends AbstractComponentTest<Toolbar> {

	public ToolbarTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "backgroundColor", fieldGenerator.createColorPicker("backgroundColor"));

	}

	@Override
	public Toolbar createComponent() {
		Toolbar toolBar = new Toolbar();
		toolBar.setButtonTemplate(BaseTemplates.TOOLBAR_BUTTON);
		ToolbarButtonGroup buttonGroup1 = new ToolbarButtonGroup();
		List<ToolbarButton> buttons = DemoDataGenerator.TOOL_ICONS.stream()
				.limit(4)
				.map(icon -> {
					ToolbarButton button = new ToolbarButton(new BaseTemplateRecord(icon, "Toolbar Template", icon.toString()));
					// button.setTogglesFullScreenOnComponent(toolBar);
					DummyComponent dummyComponent = new DummyComponent();
					dummyComponent.onClick.addListener((eventData, disposable) -> button.closeDropDown());
					button.setDropDownComponent(dummyComponent);
					button.setDebuggingId("my-debugging-id");
					return button;
				})
				.collect(Collectors.toList());
		ToolbarButton customTemplateButton = new ToolbarButton(new BaseTemplateRecord(MaterialIcon.HELP, "Button Template", MaterialIcon.HELP.toString()));
		customTemplateButton.setTemplate(BaseTemplates.TOOLBAR_BUTTON_TINY);
		buttons.add(customTemplateButton);
		buttonGroup1.setButtons(buttons);
		toolBar.addButtonGroup(buttonGroup1);

		ToolbarButtonGroup buttonGroup2 = new ToolbarButtonGroup();
		buttonGroup2.setButtonTemplate(BaseTemplates.TOOLBAR_BUTTON_SMALL);
		DemoDataGenerator.FOOD_ICONS.stream()
				.limit(4)
				.forEach(icon -> {
					ToolbarButton button = new ToolbarButton(new BaseTemplateRecord(icon, "Group template", icon.toString()));
					button.setBackgroundColor(RgbaColor.YELLOW);
					button.setHoverBackgroundColor(RgbaColor.RED);
					buttonGroup2.addButton(button);
				});
		toolBar.addButtonGroup(buttonGroup2);

		ToolbarButtonGroup buttonGroup3 = new ToolbarButtonGroup();
		buttonGroup3.setButtonTemplate(BaseTemplates.TOOLBAR_BUTTON);
		DemoDataGenerator.FOOD_ICONS.stream()
				.limit(4)
				.forEach(icon -> {
					ToolbarButton button = new ToolbarButton(new BaseTemplateRecord(icon, "Group template", icon.toString()));
					button.setBackgroundColor(RgbaColor.LIGHT_BLUE);
					button.setHoverBackgroundColor(RgbaColor.RED);
					buttonGroup3.addButton(button);
				});
		buttonGroup3.setRightSide(true);
		toolBar.addButtonGroup(buttonGroup3);

		return toolBar;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Toolbar.html";
	}
}
