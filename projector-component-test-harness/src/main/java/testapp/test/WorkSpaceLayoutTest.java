package testapp.test;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.workspacelayout.WorkSpaceLayout;
import org.teamapps.projector.component.workspacelayout.WorkSpaceLayoutView;
import org.teamapps.projector.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.IconComboBoxEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class WorkSpaceLayoutTest extends AbstractComponentTest<WorkSpaceLayout> {


	private final WorkSpaceLayout workSpaceLayout;
	private LayoutItemDefinition memorizedLayoutDefinition;

	public WorkSpaceLayoutTest(ComponentTestContext testContext, WorkSpaceLayout workSpaceLayout) {
		super(testContext);
		this.workSpaceLayout = workSpaceLayout;
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Actions");

		Button memorizeButton = Button.create(MaterialIcon.HELP, "Memorize Layout");
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Memorize Layout", memorizeButton);
		memorizeButton.onClick.addListener(() -> {
			memorizedLayoutDefinition = workSpaceLayout.extractLayoutDefinition();
		});

		Button restoreButton = Button.create(MaterialIcon.HELP, "Restore Layout");
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Restore Layout", restoreButton);
		restoreButton.onClick.addListener(() -> {
			workSpaceLayout.applyLayoutDefinition(memorizedLayoutDefinition);
		});

		// ======= view configs ============
		responsiveFormLayout.addSection(MaterialIcon.HELP, "View Configuration").setGridGap(5);
		ConfigurationFieldGenerator viewConfigFieldGenerator = new ConfigurationFieldGenerator(workSpaceLayout.getMainRootItem().getAllViews().get(0), getTestContext());

		ComboBox<WorkSpaceLayoutView> viewConfigurationComboBox = createViewComboBox();
		viewConfigurationComboBox.setValue(workSpaceLayout.getMainRootItem().getAllViews().get(0));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Select view", viewConfigurationComboBox);

		ComboBox<IconComboBoxEntry> iconComboBox = viewConfigFieldGenerator.createComboBoxForIcon("icon");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "icon", iconComboBox);
		TextField tabTitleTextField = viewConfigFieldGenerator.createTextField("tabTitle");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "tabTitle", tabTitleTextField);
		TextField panelTitleTextField = viewConfigFieldGenerator.createTextField("panelTitle");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "panelTitle", panelTitleTextField);
		CheckBox closeableCheckBox = viewConfigFieldGenerator.createCheckBox("closeable");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "closeable", closeableCheckBox);
		CheckBox visibleCheckBox = viewConfigFieldGenerator.createCheckBox("visible");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "visible", visibleCheckBox);

		viewConfigurationComboBox.onValueChanged.addListener(workSpaceLayoutView -> {
			viewConfigFieldGenerator.setComponent(workSpaceLayoutView);
			iconComboBox.setValue(new IconComboBoxEntry(workSpaceLayoutView.getIcon(), workSpaceLayoutView.getIcon().toString()));
			tabTitleTextField.setValue(workSpaceLayoutView.getTabTitle());
			panelTitleTextField.setValue(workSpaceLayoutView.getPanelTitle());
			closeableCheckBox.setValue(workSpaceLayoutView.isCloseable());
			visibleCheckBox.setValue(workSpaceLayoutView.isVisible());
		});

		// ========= view size ============
		responsiveFormLayout.addSection(MaterialIcon.HELP, "View Sizes").setGridGap(5);

		ComboBox<WorkSpaceLayoutView> viewSizeComboBox = createViewComboBox();
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Select view", viewSizeComboBox);
		NumberField sizeNumberField = new NumberField(0).setPrecision(2);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Size", sizeNumberField);
		ComboBox<String> widthHeightComboBox = ComboBox.createForList(Arrays.asList("width", "height"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Width/Height", widthHeightComboBox);
		Button setSizeButton = Button.create(MaterialIcon.HELP, "Set size");
		setSizeButton.onClick.addListener(aBoolean -> {
			WorkSpaceLayoutView view = viewSizeComboBox.getValue();
			boolean width = widthHeightComboBox.getValue().equals("width");
			float size = sizeNumberField.getValue().floatValue();
			if (width && size < 1) {
				view.setRelativeWidth(size);
			} else if (width && size >= 1) {
				view.setAbsoluteWidth((int) size);
			} else if (!width && size < 1) {
				view.setRelativeHeight(size);
			} else if (!width && size >= 1) {
				view.setAbsoluteHeight((int) size);
			}
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Set size", setSizeButton);

	}

	@Override
	protected WorkSpaceLayout createComponent() {
		return new WorkSpaceLayout(); // empty - this is not the one we are going to manipulate!
	}

	@Override
	protected Map<String, Component> getComponentsToMonitor() {
		return Collections.singletonMap("Component", workSpaceLayout);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}

	public void setMemorizedLayoutDefinition(LayoutItemDefinition memorizedLayoutDefinition) {
		this.memorizedLayoutDefinition = memorizedLayoutDefinition;
	}

	private ComboBox<WorkSpaceLayoutView> createViewComboBox() {
		ComboBox<WorkSpaceLayoutView> selectedTabComboBox = new ComboBox<>(query -> workSpaceLayout.getMainRootItem().getAllViews());
		selectedTabComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		selectedTabComboBox.setPropertyExtractor(new BeanPropertyExtractor<WorkSpaceLayoutView>()
				.addProperty("caption", view -> view.getTabTitle()));
		selectedTabComboBox.setRecordToStringFunction(view -> view.getTabTitle());
		return selectedTabComboBox;
	}
}
