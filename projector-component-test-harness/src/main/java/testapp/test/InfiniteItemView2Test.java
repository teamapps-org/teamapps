

package testapp.test;

import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.InfiniteItemView;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.ListInfiniteItemViewModel;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.VerticalElementAlignment;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.itemview.ItemGroup;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import org.teamapps.projector.template.mustache.MustacheTemplate;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoDataGenerator;
import testapp.util.Util;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InfiniteItemView2Test extends AbstractComponentTest<InfiniteItemView<BaseTemplateRecord<?>>> {

	private static final Template[] TEMPLATES = {
			BaseTemplates.ITEM_VIEW_ITEM,
			BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES,
			new MustacheTemplate(Util.readResourceToString("org/teamapps/ux/testapp/templates/conference-participant.html"))};

	private Template itemTemplate = BaseTemplates.ITEM_VIEW_ITEM;
	private ListInfiniteItemViewModel<BaseTemplateRecord<?>> model;

	public InfiniteItemView2Test(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		var fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		ComboBox<Template> itemTemplateComboBox = ComboBox.createForList(Arrays.asList(
				TEMPLATES
		));
		itemTemplateComboBox.onValueChanged.addListener(itemTemplate -> {
			this.itemTemplate = itemTemplate;
			printInvocationToConsole("setItemTemplate", itemTemplate);
			getComponent().setItemTemplate(itemTemplate);
		});
		itemTemplateComboBox.setValue(this.itemTemplate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item template", itemTemplateComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemWidth", fieldGenerator.createNumberField("itemWidth", 5, 0, 500, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemHeight", fieldGenerator.createNumberField("itemHeight", 0, 1, 500, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemContentHorizontalAlignment", fieldGenerator.createComboBoxForEnum("itemContentHorizontalAlignment"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemContentVerticalAlignment", fieldGenerator.createComboBoxForEnum("itemContentVerticalAlignment"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "itemPositionAnimationTime", fieldGenerator.createNumberField("itemPositionAnimationTime", 0, 0, 10_000, false));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove item", createRemoveItemButton(0));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove item", createRemoveItemButton(10));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove item", createRemoveItemButton(100));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "remove item", createRemoveItemButton(1000));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "add item", createAddItemButton(0));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "add item", createAddItemButton(10));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "add item", createAddItemButton(100));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "add item", createAddItemButton(1000));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "update item", createUpdateItemButton(0));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "update item", createUpdateItemButton(10));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "update item", createUpdateItemButton(100));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "update item", createUpdateItemButton(1000));
	}

	private Button createRemoveItemButton(int index) {
		Button button = Button.create("Remove item at position" + index);
		button.onClick.addListener(() -> {
			model.removeRecord(index);
		});
		return button;
	}

	private Button createAddItemButton(int index) {
		Button button = Button.create("Add item at position" + index);
		button.onClick.addListener(() -> {
			model.addRecord(index, new BaseTemplateRecord<>(MaterialIcon.HELP, "New Item!", "" + System.currentTimeMillis()));
		});
		return button;
	}

	private Button createUpdateItemButton(int index) {
		Button button = Button.create("Update item at position" + index);
		button.onClick.addListener(() -> {
			model.replaceRecord(index, new BaseTemplateRecord<>(MaterialIcon.HELP, "Updated item!", "" + System.currentTimeMillis()));
		});
		return button;
	}


	@Override
	public InfiniteItemView<BaseTemplateRecord<?>> createComponent() {
		InfiniteItemView<BaseTemplateRecord<?>> itemView = new InfiniteItemView<>(this.itemTemplate, 100, 100);
		model = new ListInfiniteItemViewModel<>(IntStream.range(0, 10)
				.mapToObj(i -> new BaseTemplateRecord<>(DemoDataGenerator.randomIcon(), "Item " + i, DemoDataGenerator.randomWords(1, true), null).setImage(DemoDataGenerator.randomUserImageUrl()))
				.collect(Collectors.toList()));
		itemView.setModel(model);

		ItemView<BaseTemplateRecord<Void>, BaseTemplateRecord<Runnable>> contextMenuItemView = new ItemView<>();
		ItemGroup<BaseTemplateRecord<Void>, BaseTemplateRecord<Runnable>> group = new ItemGroup<>(new BaseTemplateRecord<>(MaterialIcon.ATTACHMENT, "Some item group"), BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE);
		BaseTemplateRecord<Runnable> item = new BaseTemplateRecord<>(MaterialIcon.ALARM, "Do something", "width this entry...");
		contextMenuItemView.onItemClicked.addListener(event -> event.item().getPayload().run());
		group.addItem(item);
		itemView.setContextMenuProvider(record -> contextMenuItemView);

		itemView.setItemWidth(106);
		itemView.setItemHeight(148);
		itemView.setItemContentVerticalAlignment(VerticalElementAlignment.TOP);

		itemView.setItemTemplate(TEMPLATES[TEMPLATES.length - 1]);


		return itemView;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/InfiniteItemView2.html";
	}

}
