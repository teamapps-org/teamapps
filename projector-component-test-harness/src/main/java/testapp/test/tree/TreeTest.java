

package testapp.test.tree;

import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBox;
import org.teamapps.projector.component.treecomponents.tree.Tree;
import org.teamapps.projector.component.treecomponents.tree.model.TreeModelChangedEventData;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeNodeImpl;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.TemplateDecider;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TreeTest extends AbstractComponentTest<Tree<?>> {

	private static final List<Template> TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES
	);

	private Tree<SimpleTreeNodeImpl<Void>> tree;
	private TestTreeModel model;

	private List<Template> templatesByDepth = Arrays.asList(
			BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE
	);
	private final int indentation = 20;
	private boolean showExpanders = true;
	private boolean animate = true;
	private boolean openOnSelection;
	private boolean enforceSingleExpandedPath;
	private long modelLatency;

	public TreeTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	public Tree createComponent() {
		model = new TestTreeModel(getTestContext());
		tree = new Tree<>(model);
		tree.setSelectedNode(model.getRecords().get(2));
		tree.setTemplateDecider(createTemplateDecider());
		tree.setExpandersVisible(this.showExpanders);
		tree.setExpandAnimationEnabled(this.animate);
		tree.setToggleExpansionOnClick(this.openOnSelection);
		tree.setEnforceSingleExpandedPath(this.enforceSingleExpandedPath);
		tree.setIndentation(this.indentation);
		return tree;
	}

	private TemplateDecider<SimpleTreeNodeImpl<Void>> createTemplateDecider() {
		return node -> {
			if (templatesByDepth.isEmpty()) {
				return null;
			} else {
				return templatesByDepth.get(Math.min(templatesByDepth.size() - 1, node.getDepth()));
			}
		};
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator<Tree<?>> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		TagComboBox<Template> templatesTagComboBox = TagComboBox.createForList(TEMPLATE_COMBO_BOX_ENTRIES);
		templatesTagComboBox.onValueChanged.addListener(templateComboBoxEntries -> {
			this.templatesByDepth = templateComboBoxEntries;
			tree.setTemplateDecider(createTemplateDecider());
		});
		templatesTagComboBox.setValue(templatesByDepth);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Templates (by depth)", templatesTagComboBox);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Indentation", fieldGenerator.createNumberField("indentation", 0, 0, 200, false));

		CheckBox showExpandersCheckBox = new CheckBox("Show expanders");
		showExpandersCheckBox.onValueChanged.addListener(value -> {
			this.showExpanders = value;
			tree.setExpandersVisible(value);
		});
		showExpandersCheckBox.setValue(this.showExpanders);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show expanders", showExpandersCheckBox);

		CheckBox animationCheckBox = new CheckBox("Animate");
		animationCheckBox.onValueChanged.addListener(value -> {
			this.animate = value;
			tree.setExpandAnimationEnabled(value);
		});
		animationCheckBox.setValue(this.animate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Animate expand/collapse", animationCheckBox);

		CheckBox openOnSelectionCheckBox = new CheckBox("Open on selection");
		openOnSelectionCheckBox.onValueChanged.addListener(value -> {
			this.openOnSelection = value;
			tree.setToggleExpansionOnClick(value);
		});
		openOnSelectionCheckBox.setValue(this.openOnSelection);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Open on selection", openOnSelectionCheckBox);

		CheckBox enforceSingleExpandedPathCheckBox = new CheckBox("Enforce single expanded path");
		enforceSingleExpandedPathCheckBox.onValueChanged.addListener(value -> {
			this.enforceSingleExpandedPath = value;
			tree.setEnforceSingleExpandedPath(value);
		});
		enforceSingleExpandedPathCheckBox.setValue(this.enforceSingleExpandedPath);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Enforce single expanded path", enforceSingleExpandedPathCheckBox);

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

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Commands");

		Button selectNodeButton = Button.create(MaterialIcon.HELP, "Select first node");
		selectNodeButton.onClick.addListener(() -> this.tree.setSelectedNode(model.getRecords().get(0)));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "selectNode()", selectNodeButton);

		Button updateNode = Button.create(MaterialIcon.HELP, "Update first node");
		updateNode.onValueChanged.addListener(aBoolean -> {
			SimpleTreeNodeImpl<Void> node = this.model.getRecords().get(0);
			node.setCaption("New caption " + new Date().toString());
			this.model.onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), Collections.singletonList(node)));
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "updateNode()", updateNode);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Tree.html";
	}


}
