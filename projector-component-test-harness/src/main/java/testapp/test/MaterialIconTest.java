package testapp.test;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.InfiniteItemView;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.ListInfiniteItemViewModel;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.icon.material.MaterialIconStyle;
import org.teamapps.projector.icon.material.MaterialIconStyleType;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialIconTest extends AbstractComponentTest<InfiniteItemView<MaterialIcon>> {

	private static final List<MaterialIcon> MATERIAL_ICONS = Arrays.stream(MaterialIcon.class.getDeclaredFields())
			.filter(f -> Modifier.isStatic(f.getModifiers()))
			.filter(f -> f.getType() == MaterialIcon.class)
			.map(f -> {
				f.setAccessible(true);
				try {
					return (MaterialIcon) f.get(null);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());

	private Template itemTemplate = BaseTemplates.ITEM_VIEW_ITEM;

	private InfiniteItemView<MaterialIcon> component;

	public MaterialIconTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		ComboBox<MaterialIconStyleType> styleComboBox = ComboBox.createForEnum(MaterialIconStyleType.class);
		styleComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		styleComboBox.setClearButtonEnabled(true);
		styleComboBox.setPropertyExtractor((style, propertyName) -> {
			switch (propertyName) {
				case "icon":
					return MaterialIcon.COLORIZE;
				case "caption":
					return style.name();
			}
			return null;
		});
		styleComboBox.onValueChanged.addListener(style -> component.setModel(createModel(new MaterialIconStyle(style, Color.MATERIAL_BLUE_700.toHtmlColorString(), Color.MATERIAL_ORANGE_300.toHtmlColorString(), Color.MATERIAL_GREEN_500.toHtmlColorString()))));

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
			this.itemTemplate = itemTemplate;
			printInvocationToConsole("setItemTemplate", itemTemplate);
			this.component.setItemTemplate(itemTemplate);
		});
		itemTemplateComboBox.setValue(this.itemTemplate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item template", itemTemplateComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemWidth", fieldGenerator.createNumberField("itemWidth", 5, 0, 500, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemHeight", fieldGenerator.createNumberField("itemHeight", 0, 1, 500, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.COLORIZE, "Icon style", styleComboBox);
	}

	private ListInfiniteItemViewModel<MaterialIcon> createModel(MaterialIconStyle style) {
		ListInfiniteItemViewModel<MaterialIcon> items = new ListInfiniteItemViewModel<>();
		for (MaterialIcon icon : MATERIAL_ICONS) {
			items.addRecord(icon.withStyle(style));
		}
		return items;
	}

	@Override
	public InfiniteItemView createComponent() {
		component = new InfiniteItemView<>(this.itemTemplate, 100, 100);
		component.setItemPropertyExtractor((materialIcon, propertyName) -> {
			switch (propertyName) {
				case "icon":
					return materialIcon;
				case "caption":
					return materialIcon.getIconName().toUpperCase();
				default:
					return null;
			}
		});
		ListInfiniteItemViewModel<MaterialIcon> items = createModel(null);
		component.setModel(items);
		return component;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/InfiniteItemView.html";
	}

}
