

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.core.panel.Panel;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.IconComboBoxEntry;
import testapp.util.DemoComponentsGenerator;
import testapp.util.DemoDataGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static testapp.util.DemoComponentsGenerator.createDummyToolbar;

public class PanelTest extends AbstractComponentTest<Panel> {

	public PanelTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Icon", fieldGenerator.createComboBoxForIcon("icon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Title", fieldGenerator.createTextField("title", s -> s, c -> c != null ?
				c.toString() : null));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Maximizable", fieldGenerator.createCheckBox("maximizable"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Always show header field icons", fieldGenerator.createCheckBox("alwaysShowHeaderFieldIcons"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header component minimization policy", fieldGenerator.createComboBoxForEnum(
				"headerComponentMinimizationPolicy"));

		CheckBox toolButtonCheckBox = new CheckBox("Tool buttons");
		List<ToolButton> toolButtons = DemoDataGenerator.TOOL_ICONS.stream()
				.map(icon -> {
					ToolButton toolButton = new ToolButton(icon);
					toolButton.onClick.addListener(() -> printLineToConsole("EVENT (from ToolButton): clicked: " + icon.toString()));
					if (icon == MaterialIcon.HELP) {
						ItemView<BaseTemplateRecord, IconComboBoxEntry> itemView = DemoComponentsGenerator.createDummyItemView();
						itemView.onItemClicked.addListener(event -> printLineToConsole("EVENT (from ToolButton's ItemView): itemClicked: " + event.item().getCaption()));
						toolButton.setDropDownComponent(itemView);
					}
					return toolButton;
				})
				.limit(5)
				.collect(Collectors.toList());
		toolButtonCheckBox.onValueChanged.addListener(value -> {
			if (value) {
				getComponent().setToolButtons(toolButtons);
			} else {
				getComponent().setToolButtons(null);
			}
		});
		toolButtonCheckBox.setValue(getComponent().getToolButtons() != null && !getComponent().getToolButtons().isEmpty());
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Tool buttons", toolButtonCheckBox);

		CheckBox toolbarCheckBox = new CheckBox("Toolbar");
		toolbarCheckBox.onValueChanged.addListener(value -> {
			if (value) {
				getComponent().setToolbar(createDummyToolbar());
			} else {
				getComponent().setToolbar(null);
			}
		});
		toolbarCheckBox.setValue(getComponent().getToolbar() != null);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Toolbar", toolbarCheckBox);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Hide title bar", fieldGenerator.createCheckBox("hideTitleBar"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header background color", fieldGenerator.createColorPicker("headerBackgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header font color", fieldGenerator.createColorPicker("headerFontColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Body background color", fieldGenerator.createColorPicker("bodyBackgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Padding", fieldGenerator.createNumberField("padding", 0, 0, 200, false));

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Header fields").setCollapsed(true);

		ComboBox<BaseTemplateRecord> leftHeaderFieldComboBox = ComboBox.createForList(Arrays.asList(
				new BaseTemplateRecord(MaterialIcon.HELP, "TextField"),
				new BaseTemplateRecord(MaterialIcon.HELP, "ComboBox")
		));
		leftHeaderFieldComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		leftHeaderFieldComboBox.setClearButtonEnabled(true);
		leftHeaderFieldComboBox.onValueChanged.addListener(value -> {
			if (value == null) {
				getComponent().setLeftHeaderField(null);
			} else if (value.getCaption().equals("TextField")) {
				getComponent().setLeftHeaderField(new TextField());
			} else if (value.getCaption().equals("ComboBox")) {
				getComponent().setLeftHeaderField(createHeaderComboBox());
			}
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Left header field", leftHeaderFieldComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Left header field icon", fieldGenerator.createComboBoxForIcon("leftHeaderFieldIcon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Left header field min width", fieldGenerator.createNumberField("leftHeaderFieldMinWidth", 0, 0,
				1000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Left header field max width", fieldGenerator.createNumberField("leftHeaderFieldMaxWidth", 0, 0,
				1000, false));

		ComboBox<BaseTemplateRecord> rightHeaderFieldComboBox = ComboBox.createForList(Arrays.asList(
				new BaseTemplateRecord(MaterialIcon.HELP, "TextField"),
				new BaseTemplateRecord(MaterialIcon.HELP, "ComboBox")
		));
		rightHeaderFieldComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		rightHeaderFieldComboBox.setClearButtonEnabled(true);
		rightHeaderFieldComboBox.onValueChanged.addListener(value -> {
			if (value == null) {
				getComponent().setRightHeaderField(null);
			} else if (value.getCaption().equals("TextField")) {
				getComponent().setRightHeaderField(new TextField());
			} else if (value.getCaption().equals("ComboBox")) {
				getComponent().setRightHeaderField(createHeaderComboBox());
			}
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Right header field", rightHeaderFieldComboBox).field.getRowDefinition().setTopPadding(20);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Right header field icon", fieldGenerator.createComboBoxForIcon("rightHeaderFieldIcon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Right header field min width", fieldGenerator.createNumberField("rightHeaderFieldMinWidth", 0, 0
				, 1000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Right header field max width", fieldGenerator.createNumberField("rightHeaderFieldMaxWidth", 0, 0
				, 1000, false));

	}

	private ComboBox<IconComboBoxEntry> createHeaderComboBox() {
		ComboBox<IconComboBoxEntry> comboBox = ComboBox.createForList(ConfigurationFieldGenerator.FOOD_ICON_ENTRIES);
		comboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		return comboBox;
	}

	@Override
	public Panel createComponent() {
		Panel panel = new Panel();

		panel.setIcon(MaterialIcon.HELP);
		panel.setLeftHeaderFieldIcon(MaterialIcon.HELP);
		panel.setRightHeaderFieldIcon(MaterialIcon.HELP);
		panel.setTitle("Title");
		panel.setContent(new DummyComponent("Dummy content..."));

		return panel;
	}

	@Override
	public Component wrapComponent(Panel component) {
		Panel wrapper = new Panel();
		wrapper.setTitleBarHidden(true);
		wrapper.setBodyBackgroundColor(RgbaColor.MATERIAL_BLUE_GREY_200);
		wrapper.setPadding(10);
		wrapper.setContent(component);
		return wrapper;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Panel.html";
	}
}
