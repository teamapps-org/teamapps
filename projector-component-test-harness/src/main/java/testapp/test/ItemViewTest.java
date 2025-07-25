

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.NumberFieldSliderMode;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.core.panel.Panel;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.itemview.*;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoDataGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemViewTest extends AbstractComponentTest<ItemView> {

	private final Template groupHeaderTemplate = BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE;
	private final int horizontalPadding = 10;
	private final int verticalPadding = 10;
	private final int groupSpacing = 10;
	private final ItemBackgroundMode itemBackgroundMode = ItemBackgroundMode.LIGHT;
	private boolean showSelection;
	private String filter;

	private ItemView<BaseTemplateRecord, BaseTemplateRecord> itemView;
	private final List<ItemGroup<BaseTemplateRecord, BaseTemplateRecord>> itemGroups = IntStream.range(0, 3)
			.mapToObj(i -> createItemGroup(i))
			.collect(Collectors.toList());

	public ItemViewTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Group header template", fieldGenerator.createComboBoxForList("groupHeaderTemplate", Arrays.asList(
				BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
				BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE,
				BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE,
				BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
				BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
				BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES
		)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Horizontal padding", fieldGenerator.createNumberField("horizontalPadding", 0, 0, 200, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Vertical padding", fieldGenerator.createNumberField("verticalPadding", 0, 0, 200, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Group spacing", fieldGenerator.createNumberField("groupSpacing", 0, 0, 200, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Filter", fieldGenerator.createTextField("filter"));
		ComboBox<ItemBackgroundMode> itemBackgroundModeComboBox = (ComboBox<ItemBackgroundMode>) fieldGenerator.createComboBoxForEnum("itemBackgroundMode");
		itemBackgroundModeComboBox.onValueChanged.addListener(mode -> ((Panel)getWrappedComponent()).setBodyBackgroundColor(mode == ItemBackgroundMode.DARK ? RgbaColor.MATERIAL_GREY_400 : RgbaColor.WHITE));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item background mode", itemBackgroundModeComboBox);

		for (int i = 0; i < itemGroups.size(); i++) {
			ItemGroup<BaseTemplateRecord, BaseTemplateRecord> itemGroup = itemGroups.get(i);
			String displayedGroupId = "" + (i + 1);
			responsiveFormLayout.addSection(MaterialIcon.HELP, "Group " + displayedGroupId + " settings")
					.setCollapsed(true);

			CheckBox headerVisibleCheckBox = new CheckBox("Header visible");
			headerVisibleCheckBox.onValueChanged.addListener(headerVisible -> {
				itemGroup.setHeaderVisible(headerVisible);
			});
			headerVisibleCheckBox.setValue(itemGroup.isHeaderVisible());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header visible", headerVisibleCheckBox);

			ComboBox<Template> itemTemplateComboBox = ComboBox.createForList(Arrays.asList(
					BaseTemplates.ITEM_VIEW_ITEM,
					BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE,
					BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE,
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
					BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
					BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES
			));
			itemTemplateComboBox.onValueChanged.addListener(itemTemplate -> {
				printInvocationToConsole("itemGroup.setItemTemplate", itemTemplate);
				itemGroup.setItemTemplate(itemTemplate);
			});
			itemTemplateComboBox.setValue(itemGroup.getItemTemplate());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item template", itemTemplateComboBox);

			ComboBox<FloatStyle> floatStyleComboBox = ComboBox.createForEnum(FloatStyle.class);
			floatStyleComboBox.onValueChanged.addListener(floatStyle -> {
				printInvocationToConsole("itemGroup.setFloatStyle", floatStyle);
				itemGroup.setFloatStyle(floatStyle);
			});
			floatStyleComboBox.setValue(itemGroup.getFloatStyle());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Float style", floatStyleComboBox);

			ComboBox<RowJustification> itemJustificationComboBox = ComboBox.createForEnum(RowJustification.class);
			itemJustificationComboBox.onValueChanged.addListener(itemJustification -> {
				printInvocationToConsole("itemGroup.setItemJustification", itemJustification);
				itemGroup.setItemJustification(itemJustification);
			});
			itemJustificationComboBox.setValue(itemGroup.getItemJustification());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item justification", itemJustificationComboBox);

			NumberField buttonWidthField = new NumberField(0)
					.setSliderMode(NumberFieldSliderMode.VISIBLE)
					.setMinValue(-1)
					.setMaxValue(500)
					.setPrecision(3);
			buttonWidthField.onValueChanged.addListener(buttonWidth -> {
				printInvocationToConsole("itemGroup.setButtonWidth", buttonWidth.floatValue());
				itemGroup.setButtonWidth(buttonWidth.floatValue());
			});
			buttonWidthField.setValue(itemGroup.getButtonWidth());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Button width", buttonWidthField);

			NumberField groupHorizontalPaddingField = new NumberField(0)
					.setSliderMode(NumberFieldSliderMode.VISIBLE)
					.setMinValue(0)
					.setMaxValue(200);
			groupHorizontalPaddingField.onValueChanged.addListener(horizontalPadding -> {
				printInvocationToConsole("itemGroup.setHorizontalPadding", horizontalPadding.intValue());
				itemGroup.setHorizontalPadding(horizontalPadding.intValue());
			});
			groupHorizontalPaddingField.setValue(itemGroup.getHorizontalPadding());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Horizontal padding", groupHorizontalPaddingField);

			NumberField groupVerticalPaddingField = new NumberField(0)
					.setSliderMode(NumberFieldSliderMode.VISIBLE)
					.setMinValue(0)
					.setMaxValue(200);
			groupVerticalPaddingField.onValueChanged.addListener(verticalPadding -> {
				printInvocationToConsole("itemGroup.setVerticalPadding", verticalPadding.intValue());
				itemGroup.setVerticalPadding(verticalPadding.intValue());
			});
			groupVerticalPaddingField.setValue(itemGroup.getVerticalPadding());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Vertical padding", groupVerticalPaddingField);

			NumberField horizontalItemMarginField = new NumberField(0)
					.setSliderMode(NumberFieldSliderMode.VISIBLE)
					.setMinValue(0)
					.setMaxValue(200);
			horizontalItemMarginField.onValueChanged.addListener(horizontalItemMargin -> {
				printInvocationToConsole("itemGroup.setHorizontalItemMargin", horizontalItemMargin.intValue());
				itemGroup.setHorizontalItemMargin(horizontalItemMargin.intValue());
			});
			horizontalItemMarginField.setValue(itemGroup.getHorizontalItemMargin());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Horizontal item margin", horizontalItemMarginField);

			NumberField verticalItemMarginField = new NumberField(0)
					.setSliderMode(NumberFieldSliderMode.VISIBLE)
					.setMinValue(0)
					.setMaxValue(200);
			verticalItemMarginField.onValueChanged.addListener(verticalItemMargin -> {
				printInvocationToConsole("itemGroup.setVerticalItemMargin", verticalItemMargin.intValue());
				itemGroup.setVerticalItemMargin(verticalItemMargin.intValue());
			});
			verticalItemMarginField.setValue(itemGroup.getVerticalItemMargin());
			responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Vertical item margin", verticalItemMarginField);
		}

		}

	@Override
	public Component wrapComponent(ItemView component) {
		Panel panel = new Panel(null, null);
		panel.setTitleBarHidden(true);
		panel.setContent(component);
		return panel;
	}

	@Override
	public ItemView createComponent() {
		itemView = new ItemView<BaseTemplateRecord, BaseTemplateRecord>(itemGroups);

		itemView.setGroupHeaderTemplate(groupHeaderTemplate);
		itemView.setHorizontalPadding(horizontalPadding);
		itemView.setVerticalPadding(verticalPadding);
		itemView.setGroupSpacing(groupSpacing);
		itemView.setItemBackgroundMode(itemBackgroundMode);
		itemView.setFilter(filter);

		return itemView;
	}

	private ItemGroup<BaseTemplateRecord, BaseTemplateRecord> createItemGroup(int groupNumber) {
		List<BaseTemplateRecord> items = IntStream.range(0, 23)
				.mapToObj(i -> new BaseTemplateRecord(DemoDataGenerator.randomIcon(), DemoDataGenerator.randomWords(2, true), DemoDataGenerator.randomWords(5, true), null))
				.collect(Collectors.toList());
		BaseTemplateRecord headerData = new BaseTemplateRecord(DemoDataGenerator.randomIcon(), "ItemGroup " + groupNumber, DemoDataGenerator.randomWords(3, true), null);
		return new ItemGroup<>(headerData, BaseTemplates.ITEM_VIEW_ITEM, items)
				.setButtonWidth(.25f);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/ItemView.html";
	}

}
