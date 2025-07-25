package testapp.test.formfield;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.icon.Icon;
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
import java.util.concurrent.TimeUnit;

import static testapp.util.DemoComponentsGenerator.createDummyItemView;

public class ButtonTest extends AbstractComponentTest<Button> {

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	private Icon icon = MaterialIcon.HELP;
	private String caption = "Click me!";

	private final DummyComponent dummyDropDownComponent;
	private final ItemView<BaseTemplateRecord, IconComboBoxEntry> itemViewDropDownComponent;
	private ComboBox<BaseTemplateRecord<Component>> dropDownComponentComboBox;

	public ButtonTest(ComponentTestContext testContext) {
		super(testContext);
		dummyDropDownComponent = new DummyComponent("...");
		dummyDropDownComponent.onClick.addListener(() -> getComponent().closeDropDown());
		itemViewDropDownComponent = createDummyItemView();
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		ComboBox<IconComboBoxEntry> iconComboBox = ComboBox.createForList(ConfigurationFieldGenerator.FOOD_ICON_ENTRIES);
		iconComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		iconComboBox.onValueChanged.addListener(iconEntry -> {
			this.icon = iconEntry.getIcon();
			getTestContext().printInvocationToConsole("setTemplateRecord", "...");
			getComponent().setTemplateRecord(new BaseTemplateRecord<>(icon, caption));
		});
		iconComboBox.setValue(new IconComboBoxEntry(icon, "..."));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Color", iconComboBox);

		TextField captionField = new TextField();
		captionField.onValueChanged.addListener(value -> {
			this.caption = value;
			getTestContext().printInvocationToConsole("setTemplateRecord", "...");
			getComponent().setTemplateRecord(new BaseTemplateRecord<>(icon, caption));
		});
		captionField.setValue(caption);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Color", captionField);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Color", fieldGenerator.createColorPicker("color"));

		dropDownComponentComboBox = ComboBox.createForList(Arrays.asList(
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
	protected Button createComponent() {
		BaseTemplateRecord<Void> templateRecord = new BaseTemplateRecord<>(icon, caption, "Some description text");
		Button button = new Button(BaseTemplates.BUTTON, templateRecord);
		button.onDropDownOpened.addListener(dropdownComponent -> {
			if (button.getDropDownComponent() == null) {
				executorService.schedule(() -> {
					getSessionContext().runWithContext(() -> {
						BaseTemplateRecord<Component> record = dropDownComponentComboBox.getModel().getRecords("").get(1);
						dropDownComponentComboBox.setValue(record);
						button.setDropDownComponent(record.getPayload());
					});
				}, 1, TimeUnit.SECONDS);
			}
		});
		return button;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/CheckBox.html";
	}

}
