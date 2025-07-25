

package testapp.test.formfield.combobox;


import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBox;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBoxWrappingMode;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeNodeImpl;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.TemplateComboBoxEntry;
import testapp.test.formfield.AbstractFieldTest;

import java.util.Arrays;
import java.util.List;

public class TagComboBoxTest extends AbstractFieldTest<TagComboBox<?>> {

	private static final List<TemplateComboBoxEntry> TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			new TemplateComboBoxEntry("Small icon single line", BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Medium icon single line", BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Large icon single line", BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Very large icon two lines", BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES),
			new TemplateComboBoxEntry("Large icon two lines", BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES),
			new TemplateComboBoxEntry("Medium icon two lines", BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES)
	);

	private TagComboBox<SimpleTreeNodeImpl<Void>> field;
	private final TestComboBoxModel model;


	private TemplateComboBoxEntry defaultDisplayTemplateName = TEMPLATE_COMBO_BOX_ENTRIES.get(1);
	private TemplateComboBoxEntry defaultDropDownTemplateName = TEMPLATE_COMBO_BOX_ENTRIES.get(1);

	private boolean freeTextEnabled;
	private boolean showDropDownButton = true;
	private boolean showDropDownAfterResultsArrive;
	private boolean highlightFirstResultEntry = true;
	private boolean clearButtonEnabled = true;

	private boolean autoComplete = true;

	private boolean animate = true;
	private boolean showExpanders = true;

	private boolean showHighlighting = false;
	private final int textHighlightingEntryLimit = 20;
	private long modelLatency;

	private final int maxEntries = 10;
	private TagComboBoxWrappingMode wrappingMode = TagComboBoxWrappingMode.MULTI_LINE;


	public TagComboBoxTest(ComponentTestContext testContext) {
		super(testContext);
		model = new TestComboBoxModel(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<TagComboBox> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		ComboBox<TemplateComboBoxEntry> defaultDisplayTemplateNameComboBox = ComboBox.createForList(TEMPLATE_COMBO_BOX_ENTRIES);
		defaultDisplayTemplateNameComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		defaultDisplayTemplateNameComboBox.onValueChanged.addListener(defaultDisplayTemplateName -> {
			this.defaultDisplayTemplateName = defaultDisplayTemplateName;
			field.setSelectedEntryTemplate(defaultDisplayTemplateName.getTemplate());
		});
		defaultDisplayTemplateNameComboBox.setValue(defaultDisplayTemplateName);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Display template", defaultDisplayTemplateNameComboBox);

		ComboBox<TemplateComboBoxEntry> defaultDropDownTemplateNameComboBox = ComboBox.createForList(TEMPLATE_COMBO_BOX_ENTRIES);
		defaultDropDownTemplateNameComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		defaultDropDownTemplateNameComboBox.onValueChanged.addListener(defaultDropDownTemplateName -> {
			this.defaultDropDownTemplateName = defaultDropDownTemplateName;
			field.setDropDownTemplate(defaultDropDownTemplateName.getTemplate());
		});
		defaultDropDownTemplateNameComboBox.setValue(defaultDropDownTemplateName);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Dropdown template", defaultDropDownTemplateNameComboBox);

		CheckBox clearButtonEnabledCheckBox = new CheckBox("Show clear button");
		clearButtonEnabledCheckBox.onValueChanged.addListener(clearButtonEnabled -> {
			this.clearButtonEnabled = clearButtonEnabled;
			field.setClearButtonEnabled(clearButtonEnabled);
		});
		clearButtonEnabledCheckBox.setValue(clearButtonEnabled);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show clear button", clearButtonEnabledCheckBox);

		CheckBox freeTextEnabledCheckBox = new CheckBox("Allow any text");
		freeTextEnabledCheckBox.onValueChanged.addListener(freeTextEnabled -> {
			this.freeTextEnabled = freeTextEnabled;
			field.setFreeTextEnabled(freeTextEnabled);
		});
		freeTextEnabledCheckBox.setValue(freeTextEnabled);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Allow any text", freeTextEnabledCheckBox);

		CheckBox showDropDownButtonCheckBox = new CheckBox("Show dropdown button");
		showDropDownButtonCheckBox.onValueChanged.addListener(showDropDownButton -> {
			this.showDropDownButton = showDropDownButton;
			field.setDropDownButtonVisible(showDropDownButton);
		});
		showDropDownButtonCheckBox.setValue(showDropDownButton);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show dropdown button", showDropDownButtonCheckBox);

		CheckBox animateCheckBox = new CheckBox("Animate");
		animateCheckBox.onValueChanged.addListener(animate -> {
			this.animate = animate;
			field.setExpandAnimationEnabled(animate);
		});
		animateCheckBox.setValue(animate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Animate", animateCheckBox);

		CheckBox showExpandersCheckBox = new CheckBox("Show expanders");
		showExpandersCheckBox.onValueChanged.addListener(showExpanders -> {
			this.showExpanders = showExpanders;
			field.setExpandersVisible(showExpanders);
		});
		showExpandersCheckBox.setValue(showExpanders);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show expanders", showExpandersCheckBox);

		CheckBox showDropDownAfterResultsArriveCheckBox = new CheckBox("Show dropdown AFTER results arrive");
		showDropDownAfterResultsArriveCheckBox.onValueChanged.addListener(showDropDownAfterResultsArrive -> {
			this.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
			field.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
		});
		showDropDownAfterResultsArriveCheckBox.setValue(showDropDownAfterResultsArrive);
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Show dropdown AFTER results arrive", showDropDownAfterResultsArriveCheckBox);

		CheckBox highlightFirstResultEntryCheckBox = new CheckBox("Highlight first result entry");
		highlightFirstResultEntryCheckBox.onValueChanged.addListener(highlightFirstResultEntry -> {
			this.highlightFirstResultEntry = highlightFirstResultEntry;
			field.setTextHighlightingEnabled(highlightFirstResultEntry);
		});
		highlightFirstResultEntryCheckBox.setValue(highlightFirstResultEntry);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Highlight first result entry", highlightFirstResultEntryCheckBox);

		CheckBox autoCompleteCheckBox = new CheckBox("Auto-complete");
		autoCompleteCheckBox.onValueChanged.addListener(autoComplete -> {
			this.autoComplete = autoComplete;
			field.setAutoCompletionEnabled(autoComplete);
		});
		autoCompleteCheckBox.setValue(autoComplete);
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Auto-complete", autoCompleteCheckBox);

		CheckBox showHighlightingCheckBox = new CheckBox("Show highlighting");
		showHighlightingCheckBox.onValueChanged.addListener(showHighlighting -> {
			this.showHighlighting = showHighlighting;
			field.setTextHighlightingEnabled(showHighlighting);
		});
		showHighlightingCheckBox.setValue(showHighlighting);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show highlighting", showHighlightingCheckBox);

		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Highlighting entry limit", fieldGenerator.createNumberField("textHighlightingEntryLimit", 0, 0, 1000, false));

		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Max selected entries", fieldGenerator.createNumberField("maxEntries", 0, 0, 1000, false));

		ComboBox<TagComboBoxWrappingMode> wrappingModeComboBox = ComboBox.createForList(Arrays.asList(TagComboBoxWrappingMode.values()));
		wrappingModeComboBox.onValueChanged.addListener(textwrappingMode -> {
			this.wrappingMode = textwrappingMode;
			field.setWrappingMode(textwrappingMode);
		});
		wrappingModeComboBox.setValue(this.wrappingMode);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Wrapping mode", wrappingModeComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Distinct", fieldGenerator.createCheckBox("distinct"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "distinctModelResultFiltering", fieldGenerator.createCheckBox("distinctModelResultFiltering"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.DELETE_SWEEP, "Two-step deletion", fieldGenerator.createCheckBox("twoStepDeletion"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "emptyText", fieldGenerator.createTextField("emptyText"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_SIZE, "dropDownMaxHeight", fieldGenerator.createNumberField("dropDownMaxHeight", 0, 0, 1000, true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_SIZE, "dropDownMinWidth", fieldGenerator.createNumberField("dropDownMinWidth", 0, 0, 1000, true));

		NumberField modelLatencyField = new NumberField(0);
		modelLatencyField.setMaxValue(0);
		modelLatencyField.setMaxValue(10000);
		modelLatencyField.onValueChanged.addListener(modelLatency -> {
			this.modelLatency = modelLatency.intValue();
			printLineToConsole("Setting model latency to " + modelLatency + "ms");
			model.setLatency(modelLatency.longValue());
		});
		modelLatencyField.setValue(modelLatency);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Model latency (ms)", modelLatencyField);
	}

	@Override
	protected TagComboBox createField() {
		field = new TagComboBox<>(model);

		field.setSelectedEntryTemplate(defaultDisplayTemplateName.getTemplate());
		field.setDropDownTemplate(defaultDropDownTemplateName.getTemplate());
		field.setDropDownButtonVisible(showDropDownButton);
		field.setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive);
		field.setFirstEntryAutoHighlight(highlightFirstResultEntry);
		field.setAutoCompletionEnabled(autoComplete);
		field.setTextHighlightingEnabled(showHighlighting);
		field.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
		field.setFreeTextEnabled(freeTextEnabled);
		field.setClearButtonEnabled(clearButtonEnabled);
		field.setExpandAnimationEnabled(animate);
		field.setExpandersVisible(showExpanders);
		field.setMaxEntries(maxEntries);
		field.setWrappingMode(wrappingMode);
		field.setDistinct(true);
		field.setPlaceholderText("Please type...");
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/TagComboBox.html";
	}


}
