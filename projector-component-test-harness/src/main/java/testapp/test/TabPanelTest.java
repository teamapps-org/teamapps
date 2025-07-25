

package testapp.test;

import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.core.tabpanel.Tab;
import org.teamapps.projector.component.core.tabpanel.TabPanel;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.IconComboBoxEntry;
import testapp.util.DemoComponentsGenerator;
import testapp.util.DemoDataGenerator;

import java.util.stream.Collectors;

public class TabPanelTest extends AbstractComponentTest<TabPanel> {

	private int tabTitleCounter;

	public TabPanelTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Hide tab bar if single tab", fieldGenerator.createCheckBox("hideTabBarIfSingleTab"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Tab style", fieldGenerator.createComboBoxForEnum("tabStyle"));

		ComboBox<Tab> selectedTabComboBox = createTabComboBox();
		selectedTabComboBox.onValueChanged.addListener(Tab::select);
		selectedTabComboBox.setValue(getComponent().getSelectedTab());
		getComponent().onTabSelected.addListener(tab -> selectedTabComboBox.setValue(tab));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selected tab", selectedTabComboBox);

		Button addTabButton = Button.create("addTab()");
		addTabButton.onClick.addListener(() -> getComponent().addTab(new Tab(DemoDataGenerator.randomFoodIcon(), "New tab " + ++tabTitleCounter,
				new DummyComponent("New tab content..."))));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add tab", addTabButton);
		Button removeTabButton = Button.create("removeTab()");
		removeTabButton.onClick.addListener(() -> {
			if (getComponent().getTabs().size() > 0) {
				getComponent().removeTab(getComponent().getTabs().get(getComponent().getTabs().size() - 1));
			}
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Remove tab", removeTabButton);

		Button addToolButtonButton = Button.create("addToolButton()");
		addToolButtonButton.onClick.addListener(() -> getComponent().addToolButton(createToolButton(DemoDataGenerator.randomToolIcon(), DemoDataGenerator.randomBoolean())));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add tool button", addToolButtonButton);
		Button removeToolButtonButton = Button.create("removeToolButton()");
		removeToolButtonButton.onClick.addListener(() -> {
			if (getComponent().getToolButtons().size() > 0) {
				getComponent().removeToolButton(getComponent().getToolButtons().get(getComponent().getToolButtons().size() - 1));
			}
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Remove tool button", removeToolButtonButton);

		// ======= tab config ============
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Tab Configuration").setGridGap(5);
		ConfigurationFieldGenerator tabConfigFieldGenerator = new ConfigurationFieldGenerator(getComponent().getTabs().get(0), getTestContext());

		ComboBox<Tab> tabConfigurationComboBox = createTabComboBox();
		tabConfigurationComboBox.setValue(getComponent().getTabs().get(0));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Tab", tabConfigurationComboBox);

		CheckBox visibleCheckBox = tabConfigFieldGenerator.createCheckBox("visible");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Visible", visibleCheckBox);
		ComboBox<IconComboBoxEntry> iconComboBox = tabConfigFieldGenerator.createComboBoxForIcon("icon");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Icon", iconComboBox);
		TextField titleComboBox = tabConfigFieldGenerator.createTextField("title");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Title", titleComboBox);
		CheckBox closeableCheckBox = tabConfigFieldGenerator.createCheckBox("closeable");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Closeable", closeableCheckBox);
		CheckBox rightSideCheckBox = tabConfigFieldGenerator.createCheckBox("rightSide");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Right side", rightSideCheckBox);
		// TODO toolbar

		tabConfigurationComboBox.onValueChanged.addListener(tab -> {
			tabConfigFieldGenerator.setComponent(tab);
			visibleCheckBox.setValue(tab.isVisible());
			iconComboBox.setValue(new IconComboBoxEntry(tab.getIcon(), tab.getIcon().toString()));
			titleComboBox.setValue(tab.getTitle());
			closeableCheckBox.setValue(tab.isCloseable());
			rightSideCheckBox.setValue(tab.isRightSide());
		});
	}

	@Override
	public TabPanel createComponent() {
		TabPanel tabPanel = new TabPanel();
		for (int i = 0; i < 3; i++) {
			tabPanel.addTab(new Tab(DemoDataGenerator.FOOD_ICONS.get(i), "Tab " + i, new DummyComponent("Content of tab " + i)));
		}
		for (int i = 0; i < 3; i++) {
			Icon icon = DemoDataGenerator.TOOL_ICONS.get(i);
			ToolButton toolButton = createToolButton(icon, i == 0);
			if (i % 3 == 2) {
				toolButton.setCaption("asdf");
			}
			tabPanel.addToolButton(toolButton);
		}

		return tabPanel;
	}

	private ToolButton createToolButton(Icon icon, boolean dropdown) {
		ToolButton toolButton = new ToolButton(icon);
		toolButton.onClick.addListener(() -> printLineToConsole("EVENT (from ToolButton): clicked: " + icon.toString()));
		if (dropdown) {
			ItemView<BaseTemplateRecord, IconComboBoxEntry> itemView = DemoComponentsGenerator.createDummyItemView();
			itemView.onItemClicked.addListener(event -> printLineToConsole("EVENT (from ToolButton's ItemView): itemClicked: " + event.item().getCaption()));
			toolButton.setDropDownComponent(itemView);
		}
		return toolButton;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/MapView.html";
	}

	private ComboBox<Tab> createTabComboBox() {
		ComboBox<Tab> selectedTabComboBox = new ComboBox<>(query -> getComponent().getTabs().stream()
				.filter(tab -> tab.getTitle().toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList()));
		selectedTabComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		selectedTabComboBox.setPropertyExtractor(new BeanPropertyExtractor<Tab>()
				.addProperty("caption", tab -> tab.getTitle()));
		selectedTabComboBox.setRecordToStringFunction(tab -> tab.getTitle());
		return selectedTabComboBox;
	}

}
