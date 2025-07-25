

package testapp.test.tree;

import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBox;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTree;
import org.teamapps.projector.component.treecomponents.tree.simple.SimpleTreeModel;
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
import java.util.List;

public class SimpleTreeTest extends AbstractComponentTest<SimpleTree<Void>> {

	private static final List<Template> TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE,
			BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES,
			BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES
	);

	private SimpleTree<Void> tree;

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

	public SimpleTreeTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	public SimpleTree<Void> createComponent() {
		tree = new SimpleTree<>();
		SimpleTreeModel<Void> model = createModel();
		tree.setModel(model);
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

		ConfigurationFieldGenerator<SimpleTree<Void>> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

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
		});
		modelLatencyField.setValue(modelLatency);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Model latency (ms)", modelLatencyField);

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Commands");

		Button selectNodeButton = Button.create(MaterialIcon.HELP, "Select first node");
		selectNodeButton.onClick.addListener(() -> this.tree.setSelectedNode(getComponent().getModel().getRecords().get(0)));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "selectNode()", selectNodeButton);

		Button addNodesButton = Button.create(MaterialIcon.HELP, "Add child nodes to first node");
		addNodesButton.onClick.addListener(() -> this.tree.addNodes(Arrays.asList(
				new SimpleTreeNodeImpl<Void>(MaterialIcon.HELP, "A new node").setParent(getComponent().getModel().getRecords().get(0)),
				new SimpleTreeNodeImpl<Void>(MaterialIcon.HELP, "Another new node").setParent(getComponent().getModel().getRecords().get(0))
		)));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "addNodes()", addNodesButton);

		Button removeChildrenButton = Button.create(MaterialIcon.HELP, "Remove all children from first node");
		removeChildrenButton.onClick.addListener(() -> this.tree.removeChildren(Collections.singletonList(getComponent().getModel().getRecords().get(0))));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "removeChildren()", removeChildrenButton);

		Button replaceChildrenButton = Button.create(MaterialIcon.HELP, "Replace all children of first node");
		replaceChildrenButton.onClick.addListener(() -> this.tree.replaceChildren(
				Collections.singletonList(getComponent().getModel().getRecords().get(0)),
				Arrays.asList(
						new SimpleTreeNodeImpl<Void>(MaterialIcon.HELP, "A replacement node").setParent(getComponent().getModel().getRecords().get(0)),
						new SimpleTreeNodeImpl<Void>(MaterialIcon.HELP, "Another replacement node").setParent(getComponent().getModel().getRecords().get(0)),
						new SimpleTreeNodeImpl<Void>(MaterialIcon.HELP, "Yet another replacement node").setParent(getComponent().getModel().getRecords().get(0))
				)
		));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "replaceChildren()", replaceChildrenButton);



//		Button updateNode = new Button("updateNode", MaterialIcon.HELP, "Update node (\"1\""), getSessionContext());
//		updateNode.onValueChanged.addListener(aBoolean -> {
//			SimpleTreeNodeImpl<Void> treeTestNode = this.model.getTreeData(null, null).get(0);
//			treeTestNode.setCaption("New caption!");
//			this.model.getOnNodeAddedOrUpdated().fire(treeTestNode);
//		});
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, new StaticCaption("Update node"), updateNode);

		}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Tree.html";
	}

	private SimpleTreeModel<Void> createModel() {
		SimpleTreeModel<Void> simpleTreeModel = new SimpleTreeModel<>() {
			@Override
			public List<SimpleTreeNodeImpl<Void>> getRecords() {
				sleep();
				return super.getRecords();
			}

			@Override
			public List<SimpleTreeNodeImpl<Void>> getChildRecords(SimpleTreeNodeImpl<Void> parentRecord) {
				sleep();
				return super.getChildRecords(parentRecord);
			}

			private void sleep() {
				try {
					Thread.sleep(SimpleTreeTest.this.modelLatency);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		for (int i = 0; i < 6; i++) {
			SimpleTreeNodeImpl<Void> rootRecord = new SimpleTreeNodeImpl<>(i % 2 == 0 ? MaterialIcon.HELP : MaterialIcon.HELP, "Root Node " + (i + 1));
			rootRecord.setExpanded(i % 3 == 0);
			if (i % 3 == 1) {
				rootRecord.setLazyChildren(true);
				rootRecord.setDescription("Loads its children lazily");
			} else {
				rootRecord.setDescription("Loads its children eagerly");
			}
			simpleTreeModel.addNode(rootRecord);
			if (i % 3 != 2) {
				for (int j = 0; j < 3; j++) {
					SimpleTreeNodeImpl<Void> level2Record = new SimpleTreeNodeImpl<>(j % 2 == 0 ? MaterialIcon.HELP : MaterialIcon.HELP, "Child Node " + (i + 1) + "-" + (j + 1));
					level2Record.setParent(rootRecord);
					if (j % 3 == 1) {
						level2Record.setLazyChildren(true);
						level2Record.setDescription("Loads its children lazily");
					} else {
						level2Record.setDescription("Loads its children eagerly");
					}
					simpleTreeModel.addNode(level2Record);
					if (j % 3 != 2) {
						for (int k = 0; k < 3; k++) {
							SimpleTreeNodeImpl<Void> level3Record = new SimpleTreeNodeImpl<>(MaterialIcon.HELP, "Grand-child Node " + (i + 1) + "-" + (j + 1) + "-" + (k + 1));
							level3Record.setParent(level2Record);
							simpleTreeModel.addNode(level3Record);
						}
					}
				}
			}
		}
		return simpleTreeModel;
	}
}
