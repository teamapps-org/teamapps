

package testapp.test.charting;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.chart.common.GraphNodeIcon;
import org.teamapps.projector.component.chart.common.GraphNodeImage;
import org.teamapps.projector.component.chart.tree.CornerShape;
import org.teamapps.projector.component.chart.tree.TreeGraph;
import org.teamapps.projector.component.chart.tree.TreeGraphNode;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TreeGraphTest extends AbstractComponentTest<TreeGraph<BaseTemplateRecord<Void>>> {

	private static final List<Color> COLORS = Arrays.asList(
			RgbaColor.MATERIAL_RED_400,
			RgbaColor.MATERIAL_GREEN_400,
			RgbaColor.MATERIAL_BLUE_400,
			RgbaColor.MATERIAL_YELLOW_400,
			RgbaColor.MATERIAL_GREY_400,
			RgbaColor.MATERIAL_ORANGE_400,
			RgbaColor.MATERIAL_CYAN_400,
			RgbaColor.MATERIAL_BROWN_400,
			RgbaColor.MATERIAL_PURPLE_400,
			RgbaColor.MATERIAL_AMBER_400
	);
	private List<TreeGraphNode<BaseTemplateRecord<Void>>> hierarchy;

	public TreeGraphTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator<TreeGraph> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.VIEW_QUILT, "Compact", fieldGenerator.createCheckBox("compact"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_LINE_SPACING, "Vertical Layer Gap", fieldGenerator.createNumberField("verticalLayerGap", 0, 0, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SPACE_BAR, "Horizontal Sibling Gap", fieldGenerator.createNumberField("horizontalSiblingGap", 0, 0, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.CODE, "Horizontal Non-Signling Gap", fieldGenerator.createNumberField("horizontalNonSignlingGap", 0, 0, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_INDENT_INCREASE, "Side List Indent", fieldGenerator.createNumberField("sideListIndent", 0, 0, 400, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_LINE_SPACING, "Side List Vertical Gap", fieldGenerator.createNumberField("sideListVerticalGap", 0, 0, 400, false));

		Button addNodeButton = Button.create("addNode()");
		addNodeButton.onClick.addListener(() -> {
			TreeGraphNode<BaseTemplateRecord<Void>> node = createGraphTreeNode(MaterialIcon.ADD, null, "Added node", "Under root...", 1000).setParent(hierarchy.get(0));
			hierarchy.add(node);
			getComponent().addNode(node);
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.ADD, "Add node", addNodeButton);

		Button removeNodeButton = Button.create("removeNode()");
		removeNodeButton.onClick.addListener(() -> {
			if (hierarchy.size() > 1) {
				TreeGraphNode<BaseTemplateRecord<Void>> node = hierarchy.get(hierarchy.size() - 1);
				getComponent().removeNode(node);
				hierarchy.remove(node);
			}
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.ADD, "Add node", removeNodeButton);
	}

	@Override
	public TreeGraph<BaseTemplateRecord<Void>> createComponent() {
		TreeGraph<BaseTemplateRecord<Void>> treeGraph = new TreeGraph<>();
		hierarchy = createHierarchy();
		treeGraph.setNodes(hierarchy);

		treeGraph.onNodeExpandedOrCollapsed.addListener(event -> {
			if (event.isLazyLoad()) {
				ArrayList<TreeGraphNode<BaseTemplateRecord<Void>>> lazyChildren = new ArrayList<>();
				for (int i = 0; i < 2; i++) {
					TreeGraphNode<BaseTemplateRecord<Void>> lazyChild = createGraphTreeNode(MaterialIcon.PLUS_ONE, null, event.getNode().getRecord().getCaption() + " " + i, "description...",
							ThreadLocalRandom.current().nextInt(50, 300));
					lazyChild.setParent(event.getNode());
					lazyChildren.add(lazyChild);
				}
				treeGraph.addNodes(lazyChildren);
			}
		});

		treeGraph.onParentExpandedOrCollapsed.addListener(event -> {
			if (event.isLazyLoad()) {
				ArrayList<TreeGraphNode<BaseTemplateRecord<Void>>> lazyChildren = new ArrayList<>();
				TreeGraphNode<BaseTemplateRecord<Void>> oldRoot = event.getNode();

				ArrayList<TreeGraphNode<BaseTemplateRecord<Void>>> newNodes = new ArrayList<>();

				TreeGraphNode<BaseTemplateRecord<Void>> lazyRoot = createGraphTreeNode(MaterialIcon.PLUS_ONE, null, "More root than" + oldRoot.getRecord().getCaption(), "description...",
						ThreadLocalRandom.current().nextInt(50, 300));
				lazyRoot.setExpanded(true);
				newNodes.add(lazyRoot);

				for (int i = 0; i < 3; i++) {
					TreeGraphNode<BaseTemplateRecord<Void>> child = createGraphTreeNode(MaterialIcon.CHILD_FRIENDLY, null, "New root child node " + i, "With some description",
							ThreadLocalRandom.current().nextInt(50, 300));
					child.setParent(lazyRoot);
					child.setSideListNodes(Arrays.asList(
							createGraphTreeNode(MaterialIcon.CHILD_FRIENDLY, null, "Some side list node " + 1, "With some description", ThreadLocalRandom.current().nextInt(50, 300)),
							createGraphTreeNode(null, "https://avatars0.githubusercontent.com/u/275410?s=64&v=4", "Some side list node " + 2, "With some description", ThreadLocalRandom.current().nextInt(50, 300))
					));
					child.setParentExpandable(true);

					newNodes.add(child);
				}
				treeGraph.addNodes(newNodes);

				oldRoot.setParent(lazyRoot);
				treeGraph.updateNode(oldRoot);
			}
		});

		return treeGraph;
	}

	private List<TreeGraphNode<BaseTemplateRecord<Void>>> createHierarchy() {
		ArrayList<TreeGraphNode<BaseTemplateRecord<Void>>> nodes = new ArrayList<>();
		TreeGraphNode<BaseTemplateRecord<Void>> root = createGraphTreeNode(null, "https://avatars0.githubusercontent.com/u/275410?s=64&v=4", "Root", "The root node", 50);
		root.setExpanded(true);
		root.setParentExpandable(true);
		nodes.add(root);
		for (int i = 0; i < 3; i++) {
			TreeGraphNode<BaseTemplateRecord<Void>> child = createGraphTreeNode(MaterialIcon.CHILD_FRIENDLY, null, "Some node " + i, "With some description", ThreadLocalRandom.current().nextInt(50,
					300));
			child.setParent(root);
			child.setSideListNodes(Arrays.asList(
					createGraphTreeNode(MaterialIcon.CHILD_FRIENDLY, null, "Some side list node " + 1, "With some description", ThreadLocalRandom.current().nextInt(50, 300)),
					createGraphTreeNode(null, "https://avatars0.githubusercontent.com/u/275410?s=64&v=4", "Some side list node " + 2, "With some description", ThreadLocalRandom.current().nextInt(50, 300))
			));
			child.setExpanded(true);
			child.setParentExpandable(true);
			nodes.add(child);
			for (int j = 0; j < 3; j++) {
				TreeGraphNode<BaseTemplateRecord<Void>> grandChild = createGraphTreeNode(MaterialIcon.CHAT_BUBBLE, null, "Some node " + i + " " + j, "With some description",
						ThreadLocalRandom.current().nextInt(50, 300));
				grandChild.setParent(child);
				grandChild.setExpanded(true);
				grandChild.setParentExpandable(true);
				nodes.add(grandChild);
				for (int k = 0; k < 3; k++) {
					TreeGraphNode<BaseTemplateRecord<Void>> grandGrandChild = createGraphTreeNode(MaterialIcon.CHAT_BUBBLE, null, "Some node " + i + " " + j + " " + k, "With some description",
							ThreadLocalRandom.current().nextInt(50, 300));
					grandGrandChild.setParent(grandChild);
					grandGrandChild.setHasLazyChildren(true);
					grandGrandChild.setExpanded(false);
					nodes.add(grandGrandChild);
					for (int l = 0; l < 3; l++) {
						TreeGraphNode<BaseTemplateRecord<Void>> grandGrandGrandChild = createGraphTreeNode(MaterialIcon.CHAT_BUBBLE, null, "Some node " + i + " " + j + " " + k + " " + l, "With some "
										+ "description",
								ThreadLocalRandom.current().nextInt(50, 300));
						grandGrandGrandChild.setParent(grandGrandChild);
						grandGrandGrandChild.setHasLazyChildren(true);
						grandGrandGrandChild.setExpanded(false);
						nodes.add(grandGrandGrandChild);
					}
				}
			}
		}
		return nodes;
	}

	private TreeGraphNode<BaseTemplateRecord<Void>> createGraphTreeNode(MaterialIcon icon, String imageUrl, String caption, String description, int height) {
		TreeGraphNode<BaseTemplateRecord<Void>> node = new TreeGraphNode<>();
		node.setWidth(200);
		node.setHeight(height);
		node.setBackgroundColor(RgbaColor.MATERIAL_BLUE_100);
		node.setBorderColor(RgbaColor.MATERIAL_BLUE_300);
		node.setBorderWidth(1);
		node.setBorderRadius(4);
		node.setIcon(icon != null ? new GraphNodeIcon(icon, 32) : null);
		if (imageUrl != null) {
			GraphNodeImage image = new GraphNodeImage(imageUrl, 32, 32);
			image.setCornerShape(CornerShape.CIRCLE);
			image.setShadow(true);
			image.setBorderColor(RgbaColor.WHITE);
			image.setBorderWidth(1);
			node.setImage(image);
		}
		node.setTemplate(BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
		node.setRecord(new BaseTemplateRecord<Void>((Icon) null, caption, description));
		node.setConnectorLineColor(RgbaColor.MATERIAL_RED_500);
		node.setConnectorLineWidth(1);
		node.setDashArray("4 1 2 1");
		node.setExpanded(false);
		return node;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}
}
