

package testapp.test;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.div.Div;
import org.teamapps.projector.component.core.dummy.DummyComponent;
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

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static testapp.util.DemoComponentsGenerator.createDummyItemView;

public class ToolButtonTest extends AbstractComponentTest<ToolButton> {

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	private final DummyComponent dummyDropDownComponent;
	private final ItemView<BaseTemplateRecord, IconComboBoxEntry> itemViewDropDownComponent;

	public ToolButtonTest(ComponentTestContext testContext) {
		super(testContext);
		dummyDropDownComponent = new DummyComponent("...");
		dummyDropDownComponent.onClick.addListener(() -> getComponent().closeDropDown());
		itemViewDropDownComponent = createDummyItemView();
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "icon", fieldGenerator.createComboBoxForIcon("icon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "iconSize", fieldGenerator.createNumberField("iconSize", 0, 4, 128, true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "caption", fieldGenerator.createTextField("caption"));

		ComboBox<BaseTemplateRecord<Component>> dropDownComponentComboBox = ComboBox.createForList(Arrays.asList(
				new BaseTemplateRecord<>(MaterialIcon.HELP, "DummyComponent", dummyDropDownComponent),
				new BaseTemplateRecord<>(MaterialIcon.HELP, "ItemView", itemViewDropDownComponent)
		));
		dropDownComponentComboBox.setClearButtonEnabled(true);
		dropDownComponentComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		dropDownComponentComboBox.onValueChanged.addListener(record -> {
			getTestContext().printInvocationToConsole("setDropDownComponent", "...");
			getComponent().setDropDownComponent(record != null ? record.getPayload() : null);
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Drop-down component", dropDownComponentComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Open dropdown even if empty", fieldGenerator.createCheckBox("openDropDownIfNotSet"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Min dropdown width", fieldGenerator.createNumberField("minDropDownWidth", 0, 0, 2000, true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Min dropdown height", fieldGenerator.createNumberField("minDropDownHeight", 0, 0, 2000, true));
	}

	@Override
	protected ToolButton createComponent() {
		return new ToolButton(MaterialIcon.BACKUP);
	}

	@Override
	protected Component wrapComponent(ToolButton component) {
		return new Div(component);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/CheckBox.html";
	}

}
